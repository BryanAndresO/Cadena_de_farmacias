#!/bin/bash
# ==============================================================================
# Script de Despliegue en Google Cloud Run + Cloud SQL
# ==============================================================================
# Ejecutar en Google Cloud Shell después de subir el proyecto y crear Cloud SQL

set -e

# ============ CONFIGURACIÓN - EDITA ESTOS VALORES ============
MYSQL_INSTANCE="farmacia-mysql"
POSTGRES_INSTANCE="farmacia-postgres"
DB_PASSWORD="TU_PASSWORD_AQUI"           # Cambia esto
OAUTH_CLIENT_SECRET="mi-secreto-oauth"   # Cambia esto
ADMIN_PASSWORD="admin123"                # Cambia esto
USER_PASSWORD="user123"                  # Cambia esto
# =============================================================

PROJECT_ID=$(gcloud config get-value project)
REGION="us-central1"
REPO_NAME="farmacia-repo"
IMAGE_PATH="${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO_NAME}"

# Conexiones Cloud SQL
MYSQL_CONNECTION="${PROJECT_ID}:${REGION}:${MYSQL_INSTANCE}"
POSTGRES_CONNECTION="${PROJECT_ID}:${REGION}:${POSTGRES_INSTANCE}"

echo "=========================================="
echo "🚀 DESPLIEGUE EN CLOUD RUN"
echo "=========================================="
echo "Proyecto: $PROJECT_ID"
echo ""

# 1. Habilitar APIs
echo "📦 [1/4] Habilitando APIs..."
gcloud services enable \
  run.googleapis.com \
  sqladmin.googleapis.com \
  artifactregistry.googleapis.com \
  cloudbuild.googleapis.com

# 2. Crear repositorio
echo "🗄️  [2/4] Creando repositorio de imágenes..."
gcloud artifacts repositories create $REPO_NAME \
  --repository-format=docker \
  --location=$REGION 2>/dev/null || true

# 3. Construir imágenes
echo "🛠️  [3/4] Construyendo imágenes..."
declare -A SERVICES=(
  ["micro-catalogo"]="./micro_Catalogo"
  ["micro-sucursal"]="./micro_Sucursal"
  ["micro-cliente"]="./micro_Cliente"
  ["micro-ventas"]="./micro_Ventas"
  ["micro-reporte"]="./micro_Reporte"
  ["micro-inventario"]="./micro_inventario"
  ["oauth-server"]="./oauth.server"
  ["gateway"]="./gateway/gateway"
  ["frontend"]="./frontend"
)

for SERVICE in "${!SERVICES[@]}"; do
    echo "  → Construyendo $SERVICE..."
    gcloud builds submit --tag "${IMAGE_PATH}/${SERVICE}:latest" "${SERVICES[$SERVICE]}" --quiet
done

# 4. Desplegar servicios
echo "🚢 [4/4] Desplegando en Cloud Run..."

# OAuth Server (primero porque otros dependen de él)
gcloud run deploy oauth-server \
  --image "${IMAGE_PATH}/oauth-server:latest" \
  --region $REGION \
  --add-cloudsql-instances $POSTGRES_CONNECTION \
  --set-env-vars "SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/auth_db,SPRING_DATASOURCE_USERNAME=postgres,SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD},ADMIN_PASSWORD=${ADMIN_PASSWORD},USER_PASSWORD=${USER_PASSWORD},OAUTH2_CLIENT_SECRET=${OAUTH_CLIENT_SECRET}" \
  --allow-unauthenticated

OAUTH_URL=$(gcloud run services describe oauth-server --region $REGION --format='value(status.url)')
echo "✅ OAuth Server: $OAUTH_URL"

# Microservicios de negocio
for MS in micro-catalogo micro-sucursal micro-cliente micro-ventas micro-reporte; do
    DB_NAME="${MS#micro-}"  # Quita 'micro-' del nombre
    DB_NAME="${DB_NAME}db"  # Agrega 'db' al final (catalogodb, sucursaldb, etc.)
    
    gcloud run deploy $MS \
      --image "${IMAGE_PATH}/${MS}:latest" \
      --region $REGION \
      --add-cloudsql-instances $MYSQL_CONNECTION \
      --set-env-vars "SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/${DB_NAME}?useSSL=false,SPRING_DATASOURCE_USERNAME=root,SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD},SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI=${OAUTH_URL}/oauth2/jwks" \
      --no-allow-unauthenticated
done

# Inventario (necesita URL de catálogo)
CATALOGO_URL=$(gcloud run services describe micro-catalogo --region $REGION --format='value(status.url)')
gcloud run deploy micro-inventario \
  --image "${IMAGE_PATH}/micro-inventario:latest" \
  --region $REGION \
  --add-cloudsql-instances $MYSQL_CONNECTION \
  --set-env-vars "SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/inventariodb?useSSL=false,SPRING_DATASOURCE_USERNAME=root,SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD},SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI=${OAUTH_URL}/oauth2/jwks,CATALOGO_SERVICE_URL=${CATALOGO_URL}" \
  --no-allow-unauthenticated

# Frontend
gcloud run deploy frontend \
  --image "${IMAGE_PATH}/frontend:latest" \
  --region $REGION \
  --allow-unauthenticated

FRONTEND_URL=$(gcloud run services describe frontend --region $REGION --format='value(status.url)')

# Gateway (punto de entrada público)
gcloud run deploy gateway \
  --image "${IMAGE_PATH}/gateway:latest" \
  --region $REGION \
  --set-env-vars "OAUTH_PUBLIC_URL=${OAUTH_URL},OAUTH_CLIENT_SECRET=${OAUTH_CLIENT_SECRET}" \
  --allow-unauthenticated

GATEWAY_URL=$(gcloud run services describe gateway --region $REGION --format='value(status.url)')

echo ""
echo "=========================================="
echo "✅ DESPLIEGUE COMPLETADO"
echo "=========================================="
echo "🌐 URL de acceso: $GATEWAY_URL"
echo "🔐 OAuth Server: $OAUTH_URL"
echo ""
echo "⚠️  IMPORTANTE: Actualiza las URLs de redirect en OAuth Server"
echo "   - OAUTH2_CLIENT_GATEWAY_BASE_URL=$GATEWAY_URL"
echo "   - FRONTEND_PUBLIC_URL=$FRONTEND_URL"
