#!/bin/bash
# Script para configurar automáticamente el archivo .env en Google Cloud VM
# Este script detecta la IP pública de la VM y genera la configuración

echo "Detectando IP publica de la VM..."

# Intentar obtener IP externa desde metadata service de GCP
EXTERNAL_IP=$(curl -s -H "Metadata-Flavor: Google" http://metadata.google.internal/computeMetadata/v1/instance/network-interfaces/0/access-configs/0/external-ip 2>/dev/null)

# Si falla (no está en GCP), intentar con servicio externo
if [ -z "$EXTERNAL_IP" ] || [ "$EXTERNAL_IP" == "" ]; then
    echo "ADVERTENCIA: No se detecto metadata de GCP, obteniendo IP externa..."
    EXTERNAL_IP=$(curl -s ifconfig.me)
fi

# Verificar que se obtuvo una IP válida
if [ -z "$EXTERNAL_IP" ] || [ "$EXTERNAL_IP" == "" ]; then
    echo "ERROR: No se pudo detectar la IP externa"
    echo "Por favor, configura manualmente el archivo .env"
    exit 1
fi

echo "OK - IP detectada: $EXTERNAL_IP"

# Generar archivo .env
cat > .env << EOF
# ==========================================
# CONFIGURACIÓN AUTOMÁTICA PARA VM
# ==========================================
# Generado automáticamente el: $(date)
# IP detectada: $EXTERNAL_IP

# IP pública o dominio del servidor
PUBLIC_HOST=$EXTERNAL_IP

# Puertos
GATEWAY_PORT=8080
OAUTH_PORT=9000

# URLs generadas automáticamente
GATEWAY_PUBLIC_URL=http://$EXTERNAL_IP:8080
OAUTH_PUBLIC_URL=http://$EXTERNAL_IP:9000
FRONTEND_PUBLIC_URL=http://$EXTERNAL_IP:8080
PUBLIC_BASE_URL=http://$EXTERNAL_IP:8080
OAUTH_ISSUER_URI=http://$EXTERNAL_IP:9000
OAUTH2_CLIENT_GATEWAY_BASE_URL=http://$EXTERNAL_IP:8080
OAUTH2_CLIENT_FRONTEND_BASE_URL=http://$EXTERNAL_IP:8080
OAUTH2_CLIENT_PUBLIC_BASE_URL=http://$EXTERNAL_IP:8080

# Credenciales de usuarios (CAMBIAR EN PRODUCCION REAL)
ADMIN_PASSWORD=admin123
USER_PASSWORD=user123

# SEC-002/003 FIX: OAuth2 Client Secret (CAMBIAR EN PRODUCCION REAL)
OAUTH_CLIENT_SECRET=my-secure-secret-key-2024
EOF

echo "Archivo .env generado exitosamente"
echo ""
echo "=== Configuracion aplicada ==="
echo "   IP Externa: $EXTERNAL_IP"
echo "   Gateway:    http://$EXTERNAL_IP:8080"
echo "   OAuth:      http://$EXTERNAL_IP:9000"
echo ""
echo "Puedes iniciar la aplicacion con: docker compose up -d"

