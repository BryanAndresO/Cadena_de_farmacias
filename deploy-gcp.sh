#!/bin/bash
# ==============================================================================
# Script de Despliegue Automatizado para Cadena de Farmacias en GCP
# ==============================================================================

PROJECT_ID=$(gcloud config get-value project)
REGION="us-central1"
REPO_NAME="farmacia-repo"
IMAGE_PATH="${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO_NAME}"

echo "🚀 Iniciando despliegue para el Proyecto: $PROJECT_ID"

# 1. Habilitar APIs
echo "📦 Habilitando APIs de Google Cloud..."
gcloud services enable \
  run.googleapis.com \
  sqladmin.googleapis.com \
  artifactregistry.googleapis.com \
  cloudbuild.googleapis.com

# 2. Crear Repositorio en Artifact Registry
if ! gcloud artifacts repositories describe $REPO_NAME --location=$REGION &>/dev/null; then
    echo "🏗️ Creando repositorio Artifact Registry..."
    gcloud artifacts repositories create $REPO_NAME \
      --repository-format=docker \
      --location=$REGION
else
    echo "✅ El repositorio Artifact Registry ya existe."
fi

# 3. Lista de microservicios y sus carpetas
declare -A SERVICES
SERVICES=(
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

# 4. Construir y Subir Imágenes
echo "🛠️ Construyendo imágenes Docker en la nube..."
for SERVICE in "${!SERVICES[@]}"; do
    FOLDER=${SERVICES[$SERVICE]}
    echo "--- Construyendo $SERVICE desde $FOLDER ---"
    gcloud builds submit --tag "${IMAGE_PATH}/${SERVICE}" "$FOLDER"
done

echo "✅ Construcción completada. Proceda a crear las bases de datos en Cloud SQL y luego despliegue los servicios."
echo "💡 Recuerde usar el plan de implementación para configurar las variables de entorno de cada servicio."
