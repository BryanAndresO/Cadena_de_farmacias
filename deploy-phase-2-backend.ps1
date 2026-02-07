$ErrorActionPreference = "Stop"

Write-Host "Starting Phase 2 Backend Deployment..."

# 1. Build micro_Catalogo
Write-Host "Building micro_Catalogo..."
cd "c:\APLICACIONES DISTRIBUIDAS\Proyecto 3er Parcial\Cadena_de_farmacias\micro_Catalogo"
./mvnw clean package -DskipTests
if ($LASTEXITCODE -ne 0) { Write-Error "Build failed for micro_Catalogo"; exit 1 }

# 2. Build micro_inventario
Write-Host "Building micro_inventario..."
cd "c:\APLICACIONES DISTRIBUIDAS\Proyecto 3er Parcial\Cadena_de_farmacias\micro_inventario"
./mvnw clean package -DskipTests
if ($LASTEXITCODE -ne 0) { Write-Error "Build failed for micro_inventario"; exit 1 }

# 3. Restart Docker containers
Write-Host "Restarting Docker containers..."
cd "c:\APLICACIONES DISTRIBUIDAS\Proyecto 3er Parcial\Cadena_de_farmacias"
docker-compose down micro-catalogo micro-inventario
docker-compose up -d --build micro-catalogo micro-inventario

Write-Host "Deployment Phase 2 Backend Complete!"
