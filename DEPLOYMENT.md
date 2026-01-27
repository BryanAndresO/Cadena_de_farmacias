# Configuración Dinámica para Despliegue

Este proyecto ahora soporta configuración dinámica usando variables de entorno, lo que permite desplegarlo en cualquier entorno (local, VM, cloud) sin modificar código.

## Variables de Entorno

### Para Desarrollo Local (valores por defecto)
```bash
OAUTH_PUBLIC_URL=http://localhost:9000
GATEWAY_PUBLIC_URL=http://localhost:8080
FRONTEND_PUBLIC_URL=http://localhost:5173
PUBLIC_BASE_URL=
```

### Para Despliegue en VM/Cloud
```bash
OAUTH_PUBLIC_URL=http://YOUR_PUBLIC_IP:9000
GATEWAY_PUBLIC_URL=http://YOUR_PUBLIC_IP:8080
FRONTEND_PUBLIC_URL=http://YOUR_PUBLIC_IP:8080
PUBLIC_BASE_URL=http://YOUR_PUBLIC_IP:8080
```

### Para Producción con Dominio
```bash
OAUTH_PUBLIC_URL=https://auth.yourdomain.com
GATEWAY_PUBLIC_URL=https://api.yourdomain.com
FRONTEND_PUBLIC_URL=https://app.yourdomain.com
PUBLIC_BASE_URL=https://app.yourdomain.com
```

## Cómo Usar

1. **Copia el archivo de ejemplo:**
   ```bash
   cp .env.example .env
   ```

2. **Edita `.env` con tus URLs específicas:**
   ```bash
   nano .env
   ```

3. **Ejecuta docker-compose (lee automáticamente `.env`):**
   ```bash
   docker-compose up --build -d
   ```

## Ventajas de la Configuración Dinámica

✅ **Portabilidad**: Funciona en local, VM, cloud sin cambios de código
✅ **Seguridad**: Redirect URIs correctos para cada entorno
✅ **Flexibilidad**: Soporte para HTTP en desarrollo, HTTPS en producción
✅ **Mantenibilidad**: Una sola configuración para múltiples entornos

## URLs de Prueba

### Desarrollo Local
- Frontend: http://localhost:8080
- OAuth Server: http://localhost:9000
- API Gateway: http://localhost:8080/api/

### VM/Cloud (ejemplo actual)
- Frontend: http://104.155.170.220:8080
- OAuth Server: http://104.155.170.220:9000
- API Gateway: http://104.155.170.220:8080/api/

## Firewall/Red

Para despliegue en cloud, asegúrate de que los puertos estén abiertos:
- Puerto 8080 (Gateway/Frontend)
- Puerto 9000 (OAuth Server) - solo si necesitas acceso directo

```bash
# Google Cloud ejemplo
gcloud compute firewall-rules create allow-app-ports \
  --allow=tcp:8080,tcp:9000 \
  --target-tags=app-server \
  --source-ranges=YOUR_IP/32
```