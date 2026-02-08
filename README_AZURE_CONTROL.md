# Control de Servicios Azure - Cadena de Farmacias

## Resumen

Guía para detener e iniciar el sistema desplegado en Azure para optimizar costos cuando no esté en uso.

**Resource Group**: `rg-cadena-farmacias-prod`  
**Región**: `mexicocentral` / `southcentralus`  
**Total Container Apps**: 9  
**Total Bases de Datos**: 2 (PostgreSQL + MySQL)

---

## 1. Verificar Estado Actual

```bash
# Ver todos los Container Apps y su estado
az containerapp list --resource-group rg-cadena-farmacias-prod -o table

# Ver bases de datos
az postgres flexible-server list --resource-group rg-cadena-farmacias-prod -o table
az mysql flexible-server list --resource-group rg-cadena-farmacias-prod -o table
az containerapp list --resource-group rg-cadena-farmacias-prod --query "[].{name:name, state:properties.runningStatus}" -o table

# Ver costos estimados (requiere Cost Management)
az costmanagement query --resource-group rg-cadena-farmacias-prod --type "Usage" --dataset "ActualCost" --timeframe "MonthToDate"
```

---

## 2. Detener Sistema Completo

### 2.1 Detener Container Apps (9 servicios)

```bash
# Detener todos los Container Apps
az containerapp stop --name oauth-server --resource-group rg-cadena-farmacias-prod
az containerapp stop --name gateway --resource-group rg-cadena-farmacias-prod
az containerapp stop --name micro-catalogo --resource-group rg-cadena-farmacias-prod
az containerapp stop --name micro-cliente --resource-group rg-cadena-farmacias-prod
az containerapp stop --name micro-sucursal --resource-group rg-cadena-farmacias-prod
az containerapp stop --name micro-ventas --resource-group rg-cadena-farmacias-prod
az containerapp stop --name micro-reporte --resource-group rg-cadena-farmacias-prod
az containerapp stop --name micro-inventario --resource-group rg-cadena-farmacias-prod
az containerapp stop --name frontend --resource-group rg-cadena-farmacias-prod

echo "✅ Todos los Container Apps detenidos"
```

### 2.2 Detener Bases de Datos

```bash
# Detener PostgreSQL
az postgres flexible-server stop \
  --name psql-auth-farmacia2026 \
  --resource-group rg-cadena-farmacias-prod

# Detener MySQL
az mysql flexible-server stop \
  --name mysql-farmacia2026 \
  --resource-group rg-cadena-farmacias-prod

echo "✅ Bases de datos detenidas"
```

---

## 3. Iniciar Sistema Completo

### 3.1 Iniciar Bases de Datos (primero)

```bash
# Iniciar PostgreSQL
az postgres flexible-server start \
  --name psql-auth-farmacia2026 \
  --resource-group rg-cadena-farmacias-prod

# Iniciar MySQL
az mysql flexible-server start \
  --name mysql-farmacia2026 \
  --resource-group rg-cadena-farmacias-prod

echo "⏳ Esperando 2 minutos que las bases de datos inicien..."
sleep 120
```

### 3.2 Iniciar Container Apps

```bash
# Iniciar OAuth Server (primero)
az containerapp start --name oauth-server --resource-group rg-cadena-farmacias-prod

echo "⏳ Esperando 30 segundos que OAuth Server inicie..."
sleep 30

# Iniciar Gateway
az containerapp start --name gateway --resource-group rg-cadena-farmacias-prod

echo "⏳ Esperando 30 segundos que Gateway inicie..."
sleep 30

# Iniciar microservicios
az containerapp start --name micro-catalogo --resource-group rg-cadena-farmacias-prod
az containerapp start --name micro-cliente --resource-group rg-cadena-farmacias-prod
az containerapp start --name micro-sucursal --resource-group rg-cadena-farmacias-prod
az containerapp start --name micro-ventas --resource-group rg-cadena-farmacias-prod
az containerapp start --name micro-reporte --resource-group rg-cadena-farmacias-prod
az containerapp start --name micro-inventario --resource-group rg-cadena-farmacias-prod

echo "⏳ Esperando 60 segundos que los microservicios inicien..."
sleep 60

# Iniciar Frontend (último)
az containerapp start --name frontend --resource-group rg-cadena-farmacias-prod

echo "✅ Todos los servicios iniciados"
```

---

## 4. Scripts Automatizados

### 4.1 Script para Detener Todo

**Archivo**: `stop-azure-services.sh`

```bash
#!/bin/bash

echo "🛑 Deteniendo todos los servicios Azure..."

# Lista de Container Apps
APPS=("oauth-server" "gateway" "micro-catalogo" "micro-cliente" "micro-sucursal" "micro-ventas" "micro-reporte" "micro-inventario" "frontend")
RESOURCE_GROUP="rg-cadena-farmacias-prod"

# Detener Container Apps
for app in "${APPS[@]}"; do
    echo "Deteniendo: $app"
    az containerapp stop --name $app --resource-group $RESOURCE_GROUP
done

# Detener bases de datos
echo "Deteniendo PostgreSQL..."
az postgres flexible-server stop --name psql-auth-farmacia2026 --resource-group $RESOURCE_GROUP

echo "Deteniendo MySQL..."
az mysql flexible-server stop --name mysql-farmacia2026 --resource-group $RESOURCE_GROUP

echo "✅ Sistema completamente detenido"
echo "💰 Ahorrando costos de computo y bases de datos"
```

### 4.2 Script para Iniciar Todo

**Archivo**: `start-azure-services.sh`

```bash
#!/bin/bash

echo "🚀 Iniciando todos los servicios Azure..."

RESOURCE_GROUP="rg-cadena-farmacias-prod"

# Iniciar bases de datos primero
echo "Iniciando PostgreSQL..."
az postgres flexible-server start --name psql-auth-farmacia2026 --resource-group $RESOURCE_GROUP

echo "Iniciando MySQL..."
az mysql flexible-server start --name mysql-farmacia2026 --resource-group $RESOURCE_GROUP

echo "⏳ Esperando 2 minutos que las bases de datos inicien..."
sleep 120

# Iniciar Container Apps en orden
echo "Iniciando OAuth Server..."
az containerapp start --name oauth-server --resource-group $RESOURCE_GROUP
sleep 30

echo "Iniciando Gateway..."
az containerapp start --name gateway --resource-group $RESOURCE_GROUP
sleep 30

# Microservicios
echo "Iniciando microservicios..."
az containerapp start --name micro-catalogo --resource-group $RESOURCE_GROUP
az containerapp start --name micro-cliente --resource-group $RESOURCE_GROUP
az containerapp start --name micro-sucursal --resource-group $RESOURCE_GROUP
az containerapp start --name micro-ventas --resource-group $RESOURCE_GROUP
az containerapp start --name micro-reporte --resource-group $RESOURCE_GROUP
az containerapp start --name micro-inventario --resource-group $RESOURCE_GROUP
sleep 60

# Frontend último
echo "Iniciando Frontend..."
az containerapp start --name frontend --resource-group $RESOURCE_GROUP

echo "✅ Sistema completamente iniciado"
echo "🌐 Frontend disponible en: https://frontend.livelymeadow-f161e41f.southcentralus.azurecontainerapps.io/"
echo "🌐 Gateway disponible en: https://gateway.livelymeadow-f161e41f.southcentralus.azurecontainerapps.io/"
```

### 4.3 Ejecutar Scripts

```bash
# Dar permisos de ejecución
chmod +x stop-azure-services.sh
chmod +x start-azure-services.sh

# Detener todo
./stop-azure-services.sh

# Iniciar todo
./start-azure-services.sh
```

---

## 5. Opciones Alternativas

### 5.1 Escalar a 0 Réplicas (Auto-stop)

```bash
# Configurar min-replicas = 0 para auto-detener sin tráfico
az containerapp update \
  --name gateway \
  --resource-group rg-cadena-farmacias-prod \
  --min-replicas 0 \
  --max-replicas 1

# Ventaja: Se inicia automáticamente con tráfico (cold start ~30s)
# Desventaja: Primer acceso más lento
```

### 5.2 Programar Detenciones (Azure Automation)

```bash
# Crear Automation Account (si no existe)
az automation account create \
  --resource-group rg-cadena-farmacias-prod \
  --name aa-farmacia-scheduler \
  --location mexicocentral

# Crear runbook para detener servicios
az automation runbook create \
  --resource-group rg-cadena-farmacias-prod \
  --automation-account-name aa-farmacia-scheduler \
  --name StopServices \
  --type PowerShell

# Crear schedule (ej: detener todos los días a 10 PM)
az automation schedule create \
  --resource-group rg-cadena-farmacias-prod \
  --automation-account-name aa-farmacia-scheduler \
  --name DailyStopSchedule \
  --frequency "Day" \
  --interval 1 \
  --start-time "22:00:00"
```

---

## 6. Verificación Post-Operación

### 6.1 Verificar que todo está detenido

```bash
# Verificar Container Apps
az containerapp list --resource-group rg-cadena-farmacias-prod --query "[].{name:name, state:properties.runningStatus}" -o table

# Verificar bases de datos
az postgres flexible-server show --name psql-auth-farmacia2026 --resource-group rg-cadena-farmacias-prod --query "state" -o tsv
az mysql flexible-server show --name mysql-farmacia2026 --resource-group rg-cadena-farmacias-prod --query "state" -o tsv
```

### 6.2 Verificar que todo está iniciado

```bash
# Verificar Container Apps
az containerapp list --resource-group rg-cadena-farmacias-prod --query "[].{name:name, state:properties.runningStatus}" -o table

# Verificar URLs accesibles
curl -s https://gateway.livelymeadow-f161e41f.southcentralus.azurecontainerapps.io/actuator/health
curl -s https://frontend.livelymeadow-f161e41f.southcentralus.azurecontainerapps.io/
```

---

## 7. Estimación de Costos

| Estado | Container Apps (9) | PostgreSQL | MySQL | Total Mensual |
|--------|-------------------|------------|-------|---------------|
| **Todo iniciado** | $25-35 | $15-20 | $15-20 | $55-75 |
| **Todo detenido** | $0 | $5-8 | $5-8 | $10-16 |
| **Ahorro** | **100%** | **~70%** | **~60%** | **~80%** |

**Nota**: Los costos de Storage de las bases de datos persisten incluso deteniendo los servidores.

---

## 8. Recomendaciones

### 8.1 Cuándo detener
- **Fines de semana** si no se usa el sistema
- **Vacaciones** o períodos largos sin uso
- **Horario nocturno** (ej: 10 PM a 8 AM)

### 8.2 Cuándo mantener iniciado
- **Horas laborales** con uso activo
- **Demostraciones** o presentaciones importantes
- **Pruebas** que requieran disponibilidad inmediata

### 8.3 Mejores prácticas
1. **Siempre iniciar bases de datos primero**
2. **Esperar 2 minutos entre iniciar BD y contenedores**
3. **Verificar estado con los comandos de sección 6**
4. **Documentar cuándo se detiene/inicia para auditoría**

---

## 9. Troubleshooting

### Problema: Container App no inicia
```bash
# Ver logs
az containerapp logs show --name oauth-server --resource-group rg-cadena-farmacias-prod

# Verificar imagen en ACR
az acr repository show --name acrcadenafarmacia2026 --repository oauth-server
```

### Problema: Base de datos tarda mucho en iniciar
```bash
# Verificar estado de la BD
az postgres flexible-server show --name psql-auth-farmacia2026 --resource-group rg-cadena-farmacias-prod

# Esperar hasta que state = "Ready"
```

### Problema: URLs no accesibles
```bash
# Verificar estado del Gateway
az containerapp show --name gateway --resource-group rg-cadena-farmacias-prod --query "properties.configuration.ingress.fqdn"

# Verificar que esté en estado "Running"
az containerapp show --name gateway --resource-group rg-cadena-farmacias-prod --query "properties.runningStatus"
```

---

## 10. Comandos de Emergencia

### 10.1 Forzar detención completa
```bash
# Detener todo sin esperar
az containerapp list --resource-group rg-cadena-farmacias-prod -o tsv --query "[].name" | xargs -I {} az containerapp stop --name {} --resource-group rg-cadena-farmacias-prod
az postgres flexible-server stop --name psql-auth-farmacia2026 --resource-group rg-cadena-farmacias-prod --yes
az mysql flexible-server stop --name mysql-farmacia2026 --resource-group rg-cadena-farmacias-prod --yes
```

### 10.2 Verificar costos en tiempo real
```bash
# Ver consumo del mes actual
az costmanagement query \
  --resource-group rg-cadena-farmacias-prod \
  --type "Usage" \
  --dataset "ActualCost" \
  --timeframe "MonthToDate" \
  --output table
```

---

**Documentación creada**: Febrero 2025  
**Versión**: 1.0  
**Propósito**: Control de costos y gestión del ciclo de vida del sistema en Azure
