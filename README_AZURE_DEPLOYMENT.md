# Despliegue en Azure - Sistema de Cadena de Farmacias

## Resumen Ejecutivo

Este documento describe el proceso técnico completo del despliegue de un sistema distribuido basado en microservicios en Microsoft Azure, garantizando operatividad, accesibilidad externa y comunicación interna entre componentes.

**Proyecto**: Sistema de Gestión para Cadena de Farmacias  
**Arquitectura**: Microservicios (Java Spring Boot + React)  
**Proveedor Cloud**: Microsoft Azure  
**Fecha de Despliegue**: Febrero 2025  
**Estado**: ✅ Producción Activa

---

## 1. Justificación Técnica de Azure

### 1.1 Criterios de Selección

| Criterio | Azure | AWS | GCP |
|----------|-------|-----|-----|
| Integración Java/Spring Boot | ⭐⭐⭐ Excelente | ⭐⭐ Buena | ⭐⭐ Buena |
| Servicios de Contenedores | ⭐⭐⭐ AKS, ACA | ⭐⭐ EKS, ECS | ⭐⭐ GKE, Cloud Run |
| Bases de Datos Administradas | ⭐⭐⭐ Flexible Server | ⭐⭐ RDS | ⭐⭐ Cloud SQL |
| Precio Estudiante/Educativo | ⭐⭐⭐ $100/año | ⭐⭐ $100/año | ⭐⭐ $100/año |
| Documentación en Español | ⭐⭐⭐ Completa | ⭐⭐ Parcial | ⭐⭐ Parcial |
| Presencia en Latinoamérica | ⭐⭐⭐ México, Chile | ⭐⭐ Brasil | ⭐⭐ México |

### 1.2 Justificación Técnica

**Azure fue seleccionado por:**

1. **Azure Container Apps**: Solución serverless para microservicios sin gestionar Kubernetes completo
2. **Azure Container Registry (ACR)**: Registro privado integrado con autenticación Azure AD
3. **Azure Database for PostgreSQL/MySQL Flexible Server**: Bases administradas con alta disponibilidad
4. **Log Analytics Workspace**: Monitoreo centralizado de todos los componentes
5. **Regiones disponibles**: `mexicocentral` y `southcentralus` cumplen con políticas de suscripción

---

## 2. Arquitectura de Despliegue

### 2.1 Diagrama de Arquitectura

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           INTERNET (HTTPS)                                  │
└─────────────────────────────────────────────────────────────────────────────┘
                                     │
                                     ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                        AZURE CONTAINER APPS                                  │
│                    ┌─────────────────────────┐                               │
│                    │   cae-cadena-farmacias  │                               │
│                    │    (South Central US)     │                               │
│                    └─────────────────────────┘                               │
│                                     │                                       │
│    ┌────────────────────────────────┼────────────────────────────────┐     │
│    │           INTERNAL              │           EXTERNAL            │     │
│    │                                 │                                 │     │
│    │  ┌──────────────┐              │    ┌──────────────┐            │     │
│    │  │ oauth-server │              │    │    gateway   │◄───────────┼─────┼──► Usuarios
│    │  │   (9000)     │              │    │   (8080)     │            │     │
│    │  └──────────────┘              │    └──────────────┘            │     │
│    │                                 │                                 │     │
│    │  ┌──────────────┐              │    ┌──────────────┐            │     │
│    │  │micro-catalogo│              │    │   frontend   │◄───────────┼─────┼──► Usuarios
│    │  │   (8082)     │              │    │    (80)      │            │     │
│    │  └──────────────┘              │    └──────────────┘            │     │
│    │                                 │                                 │     │
│    │  ┌──────────────┐              │                                   │     │
│    │  │micro-cliente │              │                                   │     │
│    │  │   (8083)     │              │                                   │     │
│    │  └──────────────┘              │                                   │     │
│    │                                 │                                   │     │
│    │  ┌──────────────┐              │                                   │     │
│    │  │micro-sucursal│              │                                   │     │
│    │  │   (8084)     │              │                                   │     │
│    │  └──────────────┘              │                                   │     │
│    │                                 │                                   │     │
│    │  ┌──────────────┐              │                                   │     │
│    │  │ micro-ventas │              │                                   │     │
│    │  │   (8085)     │              │                                   │     │
│    │  └──────────────┘              │                                   │     │
│    │                                 │                                   │     │
│    │  ┌──────────────┐              │                                   │     │
│    │  │ micro-reporte│              │                                   │     │
│    │  │   (8086)     │              │                                   │     │
│    │  └──────────────┘              │                                   │     │
│    │                                 │                                   │     │
│    │  ┌──────────────┐              │                                   │     │
│    │  │micro-inventar│              │                                   │     │
│    │  │   (8087)     │              │                                   │     │
│    │  └──────────────┘              │                                   │     │
│    └────────────────────────────────┴────────────────────────────────┘     │
│                                     │                                       │
└─────────────────────────────────────┼───────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                     AZURE DATABASE SERVICES                                  │
│                                                                              │
│  ┌─────────────────────────────┐    ┌─────────────────────────────┐         │
│  │   PostgreSQL Flexible Server │    │    MySQL Flexible Server    │         │
│  │   psql-auth-farmacia2026     │    │    mysql-farmacia2026      │         │
│  │      (Mexico Central)        │    │      (Mexico Central)      │         │
│  │                              │    │                             │         │
│  │  ┌────────────────────────┐  │    │  ┌────────────────────────┐  │         │
│  │  │       auth_db          │  │    │  │      catalogodb        │  │         │
│  │  │    (Autenticación)     │  │    │  ├────────────────────────┤  │         │
│  │  └────────────────────────┘  │    │  │      clientesdb        │  │         │
│  │                              │    │  ├────────────────────────┤  │         │
│  │  Usuario: postgresadmin      │    │  │     sucursalesdb       │  │         │
│  │  Contraseña: [encriptada]    │    │  ├────────────────────────┤  │         │
│  │                              │    │  │       ventasdb         │  │         │
│  └─────────────────────────────┘    │  ├────────────────────────┤  │         │
│                                      │  │      reportesdb        │  │         │
│                                      │  ├────────────────────────┤  │         │
│                                      │  │     inventariodb       │  │         │
│                                      │  └────────────────────────┘  │         │
│                                      │                              │         │
│                                      │  Usuario: mysqladmin         │         │
│                                      │  Contraseña: [encriptada]    │         │
│                                      └─────────────────────────────┘         │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                    AZURE CONTAINER REGISTRY (ACR)                            │
│                                                                              │
│  ┌─────────────────────────────────────────────────────────────────────────┐ │
│  │              acrcadenafarmacia2026.azurecr.io                          │ │
│  │                        (Mexico Central)                                │ │
│  │                                                                         │ │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐        │ │
│  │  │oauth-server │ │   gateway   │ │micro-catalog│ │micro-cliente│        │ │
│  │  │   :latest   │ │   :latest   │ │  o:latest   │ │  :latest    │        │ │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘        │ │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐        │ │
│  │  │micro-sucursa│ │ micro-ventas│ │ micro-report│ │micro-inventa│        │ │
│  │  │   l:latest   │ │   :latest   │ │  e:latest   │ │ rio:latest  │        │ │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘        │ │
│  │  ┌─────────────┐                                                        │ │
│  │  │  frontend   │                                                        │ │
│  │  │   :latest   │                                                        │ │
│  │  └─────────────┘                                                        │ │
│  └─────────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 Flujo de Comunicación

```
Usuario ──► Frontend (HTTPS) ──► Gateway (HTTPS) ──► Microservicios (HTTP interno)
                                           │
                                           ▼
                                    OAuth Server (HTTP interno)
                                           │
                                           ▼
                                    PostgreSQL (SSL)
                                           │
                                           ▼
                                    MySQL (SSL)
```

---

## 3. Recursos Azure Desplegados

### 3.1 Infraestructura Principal

| Recurso | Nombre | Región | Propósito |
|---------|--------|--------|-----------|
| Resource Group | `rg-cadena-farmacias-prod` | `mexicocentral` | Agrupación lógica de recursos |
| Container Registry | `acrcadenafarmacia2026` | `mexicocentral` | Almacenamiento de imágenes Docker |
| Log Analytics Workspace | `law-cadena-farmacias-scus` | `southcentralus` | Monitoreo y logs |
| Container Apps Environment | `cae-cadena-farmacias` | `southcentralus` | Entorno de ejecución serverless |
| PostgreSQL Flexible Server | `psql-auth-farmacia2026` | `mexicocentral` | Base de datos de autenticación |
| MySQL Flexible Server | `mysql-farmacia2026` | `mexicocentral` | Bases de datos de negocio |

### 3.2 Container Apps (9 total)

| Nombre | Tipo Ingress | Puerto | Imagen | Estado |
|--------|--------------|--------|--------|--------|
| `oauth-server` | 🔒 Internal | 9000 | `acrcadenafarmacia2026.azurecr.io/oauth-server:latest` | ✅ Running |
| `gateway` | 🌐 External | 8080 | `acrcadenafarmacia2026.azurecr.io/gateway:latest` | ✅ Running |
| `micro-catalogo` | 🔒 Internal | 8082 | `acrcadenafarmacia2026.azurecr.io/micro-catalogo:latest` | ✅ Running |
| `micro-cliente` | 🔒 Internal | 8083 | `acrcadenafarmacia2026.azurecr.io/micro-cliente:latest` | ✅ Running |
| `micro-sucursal` | 🔒 Internal | 8084 | `acrcadenafarmacia2026.azurecr.io/micro-sucursal:latest` | ✅ Running |
| `micro-ventas` | 🔒 Internal | 8085 | `acrcadenafarmacia2026.azurecr.io/micro-ventas:latest` | ✅ Running |
| `micro-reporte` | 🔒 Internal | 8086 | `acrcadenafarmacia2026.azurecr.io/micro-reporte:latest` | ✅ Running |
| `micro-inventario` | 🔒 Internal | 8087 | `acrcadenafarmacia2026.azurecr.io/micro-inventario:latest` | ✅ Running |
| `frontend` | 🌐 External | 80 | `acrcadenafarmacia2026.azurecr.io/frontend:latest` | ✅ Running |

### 3.3 URLs de Acceso

| Servicio | URL Pública |
|----------|-------------|
| **Frontend** | `https://frontend.livelymeadow-f161e41f.southcentralus.azurecontainerapps.io/` |
| **Gateway API** | `https://gateway.livelymeadow-f161e41f.southcentralus.azurecontainerapps.io/` |

---

## 4. Preparación del Entorno Productivo

### 4.1 Configuración de Variables de Entorno

**OAuth Server**:
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://psql-auth-farmacia2026.postgres.database.azure.com:5432/auth_db?sslmode=require
SPRING_DATASOURCE_USERNAME=postgresadmin
SPRING_DATASOURCE_PASSWORD=Farmacia2024Secure!
ADMIN_USERNAME=admin
ADMIN_PASSWORD=admin123
USER_USERNAME=user
USER_PASSWORD=user123
OAUTH_CLIENT_SECRET=azure-secure-secret-key-2024-cadena-farmacia
JWKS_URI=http://oauth-server:9000/oauth2/jwks
```

**Gateway**:
```bash
OAUTH_SERVER_URL=http://oauth-server:9000
OAUTH_CLIENT_ID=gateway-client
OAUTH_CLIENT_SECRET=azure-secure-secret-key-2024-cadena-farmacia
JWT_ISSUER_URI=http://oauth-server:9000
JWT_JWK_SET_URI=http://oauth-server:9000/oauth2/jwks
```

**Microservicios** (ejemplo micro-catalogo):
```bash
SPRING_DATASOURCE_URL=jdbc:mysql://mysql-farmacia2026.mysql.database.azure.com:3306/catalogodb?useSSL=true&serverTimezone=America/Mexico_City
SPRING_DATASOURCE_USERNAME=mysqladmin
SPRING_DATASOURCE_PASSWORD=Farmacia2024Secure!
OAUTH_SERVER_URL=http://oauth-server:9000
JWT_ISSUER_URI=http://oauth-server:9000
JWT_JWK_SET_URI=http://oauth-server:9000/oauth2/jwks
```

### 4.2 Imágenes Docker Construidas

Todas las imágenes usan **multi-stage build** para optimización:

```dockerfile
# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 5. Proceso de Despliegue

### 5.1 Fase 1: Infraestructura Base

```bash
# 1. Login en Azure
az login

# 2. Crear Resource Group
az group create \
  --name rg-cadena-farmacias-prod \
  --location mexicocentral \
  --tags environment=production project=cadena-farmacias

# 3. Registrar providers necesarios
az provider register --namespace Microsoft.ContainerRegistry
az provider register --namespace Microsoft.App
az provider register --namespace Microsoft.OperationalInsights
az provider register --namespace Microsoft.DBforPostgreSQL
az provider register --namespace Microsoft.DBforMySQL
```

### 5.2 Fase 2: Container Registry

```bash
# Crear Azure Container Registry
az acr create \
  --resource-group rg-cadena-farmacias-prod \
  --name acrcadenafarmacia2026 \
  --sku Standard \
  --location mexicocentral \
  --admin-enabled true

# Login en ACR
az acr login --name acrcadenafarmacia2026
```

### 5.3 Fase 3: Monitoreo

```bash
# Crear Log Analytics Workspace
az monitor log-analytics workspace create \
  --resource-group rg-cadena-farmacias-prod \
  --workspace-name law-cadena-farmacias-scus \
  --location southcentralus

# Obtener workspace ID y key
WORKSPACE_ID=$(az monitor log-analytics workspace show \
  --resource-group rg-cadena-farmacias-prod \
  --workspace-name law-cadena-farmacias-scus \
  --query customerId -o tsv)

WORKSPACE_KEY=$(az monitor log-analytics workspace get-shared-keys \
  --resource-group rg-cadena-farmacias-prod \
  --workspace-name law-cadena-farmacias-scus \
  --query primarySharedKey -o tsv)
```

### 5.4 Fase 4: Container Apps Environment

```bash
# Crear Container Apps Environment
az containerapp env create \
  --name cae-cadena-farmacias \
  --resource-group rg-cadena-farmacias-prod \
  --location southcentralus \
  --logs-workspace-id "$WORKSPACE_ID" \
  --logs-workspace-key "$WORKSPACE_KEY"
```

### 5.5 Fase 5: Bases de Datos

```bash
# PostgreSQL Flexible Server
az postgres flexible-server create \
  --resource-group rg-cadena-farmacias-prod \
  --name psql-auth-farmacia2026 \
  --location mexicocentral \
  --admin-user postgresadmin \
  --admin-password "Farmacia2024Secure!" \
  --sku-name Standard_B1ms \
  --tier Burstable \
  --storage-size 32 \
  --version 15 \
  --public-access all

# Crear base de datos auth_db
az postgres flexible-server db create \
  --resource-group rg-cadena-farmacias-prod \
  --server-name psql-auth-farmacia2026 \
  --database-name auth_db

# MySQL Flexible Server
az mysql flexible-server create \
  --resource-group rg-cadena-farmacias-prod \
  --name mysql-farmacia2026 \
  --location mexicocentral \
  --admin-user mysqladmin \
  --admin-password "Farmacia2024Secure!" \
  --sku-name Standard_B1ms \
  --tier Burstable \
  --storage-size 32 \
  --version 8.0.21 \
  --public-access all

# Crear bases de datos MySQL
for DB in catalogodb clientesdb sucursalesdb ventasdb reportesdb inventariodb; do
  az mysql flexible-server db create \
    --resource-group rg-cadena-farmacias-prod \
    --server-name mysql-farmacia2026 \
    --database-name $DB
done
```

### 5.6 Fase 6: Build y Push de Imágenes

```bash
# Construir todas las imágenes
SERVICES="oauth-server gateway micro-catalogo micro-cliente micro-sucursal micro-ventas micro-reporte micro-inventario frontend"

for SERVICE in $SERVICES; do
  docker build -t acrcadenafarmacia2026.azurecr.io/$SERVICE:latest ./$SERVICE
  docker push acrcadenafarmacia2026.azurecr.io/$SERVICE:latest
done
```

### 5.7 Fase 7: Despliegue de Container Apps

**OAuth Server (Internal)**:
```bash
az containerapp create \
  --name oauth-server \
  --resource-group rg-cadena-farmacias-prod \
  --environment cae-cadena-farmacias \
  --image acrcadenafarmacia2026.azurecr.io/oauth-server:latest \
  --target-port 9000 \
  --ingress internal \
  --registry-server acrcadenafarmacia2026.azurecr.io \
  --env-vars "SPRING_DATASOURCE_URL=jdbc:postgresql://psql-auth-farmacia2026.postgres.database.azure.com:5432/auth_db?sslmode=require" "SPRING_DATASOURCE_USERNAME=postgresadmin" "SPRING_DATASOURCE_PASSWORD=Farmacia2024Secure!" "ADMIN_USERNAME=admin" "ADMIN_PASSWORD=admin123" "USER_USERNAME=user" "USER_PASSWORD=user123" "OAUTH_CLIENT_SECRET=azure-secure-secret-key-2024-cadena-farmacia" "JWKS_URI=http://oauth-server:9000/oauth2/jwks"
```

**Gateway (External)**:
```bash
az containerapp create \
  --name gateway \
  --resource-group rg-cadena-farmacias-prod \
  --environment cae-cadena-farmacias \
  --image acrcadenafarmacia2026.azurecr.io/gateway:latest \
  --target-port 8080 \
  --ingress external \
  --allow-insecure true \
  --registry-server acrcadenafarmacia2026.azurecr.io \
  --env-vars "OAUTH_SERVER_URL=http://oauth-server:9000" "OAUTH_CLIENT_ID=gateway-client" "OAUTH_CLIENT_SECRET=azure-secure-secret-key-2024-cadena-farmacia" "JWT_ISSUER_URI=http://oauth-server:9000" "JWT_JWK_SET_URI=http://oauth-server:9000/oauth2/jwks"
```

**Microservicios (Internal)** - Ejemplo micro-catalogo:
```bash
az containerapp create \
  --name micro-catalogo \
  --resource-group rg-cadena-farmacias-prod \
  --environment cae-cadena-farmacias \
  --image acrcadenafarmacia2026.azurecr.io/micro-catalogo:latest \
  --target-port 8082 \
  --ingress internal \
  --registry-server acrcadenafarmacia2026.azurecr.io \
  --env-vars "SPRING_DATASOURCE_URL=jdbc:mysql://mysql-farmacia2026.mysql.database.azure.com:3306/catalogodb?useSSL=true&serverTimezone=America/Mexico_City" "SPRING_DATASOURCE_USERNAME=mysqladmin" "SPRING_DATASOURCE_PASSWORD=Farmacia2024Secure!" "OAUTH_SERVER_URL=http://oauth-server:9000" "JWT_ISSUER_URI=http://oauth-server:9000" "JWT_JWK_SET_URI=http://oauth-server:9000/oauth2/jwks"
```

**Frontend (External)**:
```bash
az containerapp create \
  --name frontend \
  --resource-group rg-cadena-farmacias-prod \
  --environment cae-cadena-farmacias \
  --image acrcadenafarmacia2026.azurecr.io/frontend:latest \
  --target-port 80 \
  --ingress external \
  --allow-insecure true \
  --registry-server acrcadenafarmacia2026.azurecr.io \
  --env-vars "REACT_APP_API_URL=https://gateway.livelymeadow-f161e41f.southcentralus.azurecontainerapps.io"
```

---

## 6. Configuración de Red y Seguridad

### 6.1 Segregación de Red

| Tipo | Componentes | Acceso |
|------|-------------|--------|
| **External** | Gateway, Frontend | Público (Internet) |
| **Internal** | OAuth Server, Microservicios | Privado (solo dentro del env) |
| **Database** | PostgreSQL, MySQL | SSL + Credenciales |

### 6.2 Comunicación entre Servicios

Los microservicios se comunican mediante **service discovery** interno de Azure Container Apps:

```
http://<nombre-servicio>:<puerto>

Ejemplos:
- http://oauth-server:9000
- http://gateway:8080
- http://micro-catalogo:8082
```

### 6.3 Autenticación y Autorización

**OAuth2/OIDC Flow**:
1. Usuario solicita token al Gateway
2. Gateway valida credenciales contra OAuth Server
3. OAuth Server verifica en PostgreSQL
4. Se emite JWT firmado
5. Gateway valida JWT en cada request a microservicios
6. Microservicios confían en el JWT (usando JWKS del OAuth Server)

### 6.4 Credenciales de Sistema

| Sistema | Usuario | Rol |
|---------|---------|-----|
| Sistema | admin | ROLE_ADMIN |
| Sistema | user | ROLE_USER |

---

## 7. Validación del Sistema

### 7.1 Comandos de Verificación

```bash
# Listar todos los Container Apps
az containerapp list \
  --resource-group rg-cadena-farmacias-prod \
  -o table

# Ver logs de un servicio específico
az containerapp logs show \
  --name oauth-server \
  --resource-group rg-cadena-farmacias-prod \
  --follow

# Verificar imágenes en ACR
az acr repository list \
  --name acrcadenafarmacia2026 \
  -o table
```

### 7.2 Endpoints de Prueba

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `https://gateway.livelymeadow-f161e41f.southcentralus.azurecontainerapps.io/actuator/health` | GET | Health check Gateway |
| `https://frontend.livelymeadow-f161e41f.southcentralus.azurecontainerapps.io/` | GET | Interfaz de usuario |

### 7.3 Flujo de Prueba End-to-End

1. Abrir **Frontend**: `https://frontend.livelymeadow-f161e41f.southcentralus.azurecontainerapps.io/`
2. Login con credenciales `admin` / `admin123` (o `user` / `user123`)
3. Navegar por módulos: Catálogo, Clientes, Sucursales, Ventas, Reportes, Inventario
4. Verificar que los datos persisten en MySQL
5. Verificar que la autenticación funciona correctamente

---

## 8. Monitoreo y Logs

### 8.1 Azure Portal

Acceder a: `portal.azure.com` → Resource Groups → `rg-cadena-farmacias-prod`

### 8.2 Log Analytics Queries

```kusto
// Logs de todos los Container Apps
ContainerAppConsoleLogs_CL
| project TimeGenerated, ContainerAppName_s, Log_s
| order by TimeGenerated desc

// Logs de errores
ContainerAppConsoleLogs_CL
| where Log_s contains "ERROR" or Log_s contains "Exception"
| project TimeGenerated, ContainerAppName_s, Log_s
| order by TimeGenerated desc
```

### 8.3 Métricas Disponibles

- CPU Usage
- Memory Usage
- Network In/Out
- Request Count
- Response Time
- Restart Count

---

## 9. Mantenimiento y Operaciones

### 9.1 Actualización de Imágenes

```bash
# Reconstruir imagen
docker build -t acrcadenafarmacia2026.azurecr.io/micro-catalogo:latest ./micro_Catalogo
docker push acrcadenafarmacia2026.azurecr.io/micro-catalogo:latest

# Reiniciar Container App para usar nueva imagen
az containerapp update \
  --name micro-catalogo \
  --resource-group rg-cadena-farmacias-prod \
  --image acrcadenafarmacia2026.azurecr.io/micro-catalogo:latest
```

### 9.2 Escalado

```bash
# Escalar a múltiples réplicas
az containerapp update \
  --name gateway \
  --resource-group rg-cadena-farmacias-prod \
  --min-replicas 2 \
  --max-replicas 5
```

### 9.3 Backup de Bases de Datos

Azure Flexible Server realiza backups automáticos diarios con retención de 7-35 días.

---

## 10. Troubleshooting

### 10.1 Problemas Comunes

| Síntoma | Causa Probable | Solución |
|---------|---------------|----------|
| Container no inicia | Error en variables de entorno | Verificar `az containerapp show` |
| Error 502 en Gateway | Microservicio no responde | Revisar logs del microservicio |
| Timeout en BD | Firewall/SSL incorrecto | Verificar `sslmode=require` y credenciales |
| Imagen no encontrada | No push a ACR | Ejecutar `docker push` correctamente |

### 10.2 Comandos de Diagnóstico

```bash
# Ver estado detallado
az containerapp show \
  --name oauth-server \
  --resource-group rg-cadena-farmacias-prod

# Ver revisiones
az containerapp revision list \
  --name oauth-server \
  --resource-group rg-cadena-farmacias-prod

# Conectar a PostgreSQL para verificar
psql "host=psql-auth-farmacia2026.postgres.database.azure.com port=5432 dbname=auth_db user=postgresadmin sslmode=require"
```

---

## 11. Conclusiones

### 11.1 Logros del Despliegue

✅ **Microservicios containerizados** en Azure Container Apps  
✅ **Bases de datos administradas** con Azure Flexible Servers  
✅ **Registry privado** con Azure Container Registry  
✅ **Monitoreo centralizado** con Log Analytics  
✅ **Seguridad** mediante OAuth2/OIDC con JWT  
✅ **Accesibilidad externa** via HTTPS para Frontend y Gateway  
✅ **Comunicación interna** segura entre componentes  

### 11.2 Métricas del Sistema

| Métrica | Valor |
|---------|-------|
| Total Container Apps | 9 |
| Total Imágenes Docker | 9 |
| Bases de Datos PostgreSQL | 1 |
| Bases de Datos MySQL | 6 |
| Tiempo de despliegue | ~30 minutos |
| Región primaria | `mexicocentral` / `southcentralus` |

### 11.3 URLs de Producción

- **Frontend**: https://frontend.livelymeadow-f161e41f.southcentralus.azurecontainerapps.io/
- **API Gateway**: https://gateway.livelymeadow-f161e41f.southcentralus.azurecontainerapps.io/

---

## 12. Referencias

- [Azure Container Apps Documentation](https://docs.microsoft.com/azure/container-apps/)
- [Azure Container Registry](https://docs.microsoft.com/azure/container-registry/)
- [Azure Database for PostgreSQL](https://docs.microsoft.com/azure/postgresql/)
- [Azure Database for MySQL](https://docs.microsoft.com/azure/mysql/)
- [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)
- [OAuth 2.0 Authorization Server](https://docs.spring.io/spring-authorization-server/)

---

**Documento generado**: Febrero 2025  
**Versión**: 1.0  
**Autor**: Desarrollador del Sistema de Cadena de Farmacias
