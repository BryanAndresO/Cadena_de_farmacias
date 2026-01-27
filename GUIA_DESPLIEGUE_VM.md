# Guía de Despliegue en VM con Docker

## Requisitos Previos

- Una VM con Debian (o cualquier distribución Linux)
- Docker instalado
- Git instalado
- Puertos abiertos: 8080 (Gateway), 9000 (OAuth Server)

## Pasos para Desplegar

### 1. Clonar el Repositorio

```bash
git clone <URL_DE_TU_REPOSITORIO>
cd Cadena_de_farmacias
```

### 2. Configurar Variables de Entorno

Copia el archivo de plantilla y edítalo con la IP pública de tu VM:

```bash
cp .env.production .env
```

Edita el archivo `.env` y reemplaza `YOUR_VM_IP_OR_DOMAIN` con la IP pública o dominio de tu VM:

```bash
nano .env
```

Por ejemplo, si tu IP pública es `35.123.45.67`:

```env
PUBLIC_HOST=35.123.45.67
```

### 3. Iniciar los Servicios

Ejecuta Docker Compose para construir e iniciar todos los servicios:

```bash
docker compose up -d
```

Este comando:
- Descargará las imágenes necesarias
- Construirá las aplicaciones Java y el frontend
- Iniciará todos los servicios (bases de datos, microservicios, gateway, oauth server, frontend)

### 4. Verificar que los Servicios Están Ejecutándose

```bash
docker compose ps
```

Deberías ver todos los servicios en estado "Up" o "running".

### 5. Acceder a la Aplicación

Abre tu navegador y navega a:

```
http://TU_IP_PUBLICA:8080
```

Por ejemplo: `http://35.123.45.67:8080`

## Solución de Problemas

### Ver logs de un servicio específico

```bash
# Ver logs del Gateway
docker compose logs -f gateway

# Ver logs del servidor OAuth
docker compose logs -f oauth-server

# Ver logs de un microservicio
docker compose logs -f micro-catalogo
```

### Reiniciar un servicio específico

```bash
docker compose restart oauth-server
```

### Detener todos los servicios

```bash
docker compose down
```

### Detener y eliminar volúmenes (CUIDADO: elimina datos de BD)

```bash
docker compose down -v
```

## Configuración de Firewall

Asegúrate de que tu firewall de GCP permite tráfico entrante en los puertos:
- 8080/tcp (Gateway - Acceso principal)
- 9000/tcp (OAuth Server - Autenticación)

## Notas Adicionales

- La primera ejecución tomará más tiempo porque Docker necesita descargar imágenes y construir las aplicaciones
- Los datos de las bases de datos se persisten en volúmenes de Docker
- Para actualizar el código: haz `git pull`, luego `docker compose up -d --build`
