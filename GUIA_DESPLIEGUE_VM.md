# Guía de Despliegue Automático en VM con Docker

## Requisitos Previos

- Una VM con Debian/Ubuntu (Google Cloud, AWS, Azure, etc.)
- Docker instalado
- Git instalado
- Puertos abiertos: 8080 (Gateway), 9000 (OAuth Server)

## 🚀 Despliegue Rápido (Recomendado)

### 1. Clonar el Repositorio

```bash
git clone <URL_DE_TU_REPOSITORIO>
cd Cadena_de_farmacias
```

### 2. Ejecutar Script de Configuración Automática

El script `setup-vm.sh` detectará automáticamente la IP pública de tu VM:

```bash
chmod +x setup-vm.sh
./setup-vm.sh
```

Este script:
- ✅ Detecta la IP pública automáticamente (usando metadata de GCP o servicio externo)
- ✅ Genera el archivo `.env` con la configuración correcta
- ✅ No requiere edición manual

### 3. Iniciar los Servicios

```bash
docker compose up -d
```

### 4. Verificar Estado

```bash
docker compose ps
```

### 5. Acceder a la Aplicación

```
http://TU_IP_PUBLICA:8080
```

---

## 🔧 Configuración Manual (Alternativa)

Si prefieres configurar manualmente o el script automático no funciona:

### 1. Obtener tu IP Pública

```bash
curl ifconfig.me
```

### 2. Copiar y Editar .env

```bash
cp .env.production .env
nano .env
```

Reemplaza `YOUR_VM_IP_OR_DOMAIN` con tu IP real.

### 3. Iniciar Servicios

```bash
docker compose up -d
```

---

## 📋 Comandos Útiles

### Ver logs de todos los servicios

```bash
docker compose logs -f
```

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

### Reiniciar todos los servicios

```bash
docker compose restart
```

### Detener todos los servicios

```bash
docker compose down
```

### Detener y eliminar volúmenes (⚠️ CUIDADO: elimina datos de BD)

```bash
docker compose down -v
```

### Actualizar código y reconstruir

```bash
git pull
docker compose up -d --build
```

---

## 🔥 Configuración de Firewall en Google Cloud

### Opción 1: Desde la Consola Web

1. Ve a **VPC Network** → **Firewall**
2. Click en **"CREATE FIREWALL RULE"**
3. Configuración:
   - **Name**: `allow-pharmacy-app`
   - **Direction**: Ingress
   - **Action**: Allow
   - **Targets**: All instances in the network
   - **Source IPv4 ranges**: `0.0.0.0/0`
   - **Protocols and ports**: tcp:`8080,9000`
4. Click **CREATE**

### Opción 2: Desde gcloud CLI

```bash
gcloud compute firewall-rules create allow-pharmacy-app \
    --allow tcp:8080,tcp:9000 \
    --source-ranges 0.0.0.0/0 \
    --description "Allow access to Pharmacy Chain application" \
    --direction INGRESS
```

---

## 🐛 Solución de Problemas

### Verificar si los puertos están escuchando

```bash
sudo netstat -tlnp | grep -E ':(8080|9000)'
```

Deberías ver `docker-proxy` en ambos puertos.

### Probar conectividad desde otra máquina

```bash
# Desde tu computadora local
telnet TU_IP_PUBLICA 8080
```

O desde PowerShell (Windows):
```powershell
Test-NetConnection -ComputerName TU_IP_PUBLICA -Port 8080
```

### Si los contenedores no inician

```bash
# Ver por qué falló un contenedor
docker compose logs nombre-del-contenedor

# Reconstruir desde cero
docker compose down -v
docker compose up -d --build
```

### Si cambias la IP de la VM

Simplemente vuelve a ejecutar el script de configuración:
```bash
./setup-vm.sh
docker compose down
docker compose up -d
```

---

## 📝 Notas Adicionales

- **Primera ejecución**: Tomará más tiempo (descargar imágenes y construir)
- **Persistencia**: Los datos de las bases de datos se guardan en volúmenes de Docker
- **Puertos requeridos**: Asegúrate de que tu firewall/security group permita tráfico en 8080 y 9000
- **Actualizar el código**: `git pull && docker compose up -d --build`

---

## 🌐 Acceso desde Dominio (Opcional)

Si tienes un dominio (ej: `miapp.com`), puedes usarlo en lugar de la IP:

1. Apunta el registro A de tu dominio a la IP de la VM
2. Edita `.env` y cambia `PUBLIC_HOST` por tu dominio
3. Reinicia: `docker compose down && docker compose up -d`
