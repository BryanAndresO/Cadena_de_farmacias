# Script to start all microservices and databases for the Pharmacy System

# 1. Network Setup
Write-Host "Checking Docker Network..." -ForegroundColor Cyan
if (-not (docker network ls --format '{{.Name}}' | Select-String "red-microservicios")) {
    docker network create red-microservicios
    Write-Host "Network 'red-microservicios' created." -ForegroundColor Green
}
else {
    Write-Host "Network 'red-microservicios' already exists." -ForegroundColor Yellow
}

# 2. Database Containers
$databases = @(
    @{Name = "mysql-catalogo"; Port = 3309; DB = "catalogodb"; Vol = "mysql_catalogo_data" },
    @{Name = "mysql-sucursal"; Port = 3310; DB = "sucursalesdb"; Vol = "mysql_sucursal_data" },
    @{Name = "mysql-cliente"; Port = 3311; DB = "clientesdb"; Vol = "mysql_cliente_data" },
    @{Name = "mysql-ventas"; Port = 3312; DB = "ventasdb"; Vol = "mysql_ventas_data" },
    @{Name = "mysql-reporte"; Port = 3313; DB = "reportesdb"; Vol = "mysql_reporte_data" },
    @{Name = "mysql-inventario"; Port = 3314; DB = "inventariodb"; Vol = "mysql_inventario_data" }
)

foreach ($db in $databases) {
    if (-not (docker ps -q -f name=$($db.Name))) {
        Write-Host "Starting $($db.Name)..." -ForegroundColor Cyan
        # Using standard password 'abcd' and user 'AppRoot' as per docs
        docker run -d -p "$($db.Port):3306" --name $db.Name --network red-microservicios `
            -e MYSQL_DATABASE=$db.DB `
            -e MYSQL_ROOT_PASSWORD=abcd `
            -e MYSQL_USER=AppRoot `
            -e MYSQL_PASSWORD=abcd `
            -v "$($db.Vol):/var/lib/mysql" `
            mysql:8.0
    }
    else {
        Write-Host "$($db.Name) is already running." -ForegroundColor Yellow
    }
}

# 3. Start Backend Microservices
$services = @(
    "micro_Catalogo",
    "micro_Sucursal",
    "micro_Cliente",
    "micro_Ventas",
    "micro_Reporte",
    "micro_inventario"
)

$rootPath = Get-Location

foreach ($service in $services) {
    $servicePath = Join-Path $rootPath $service
    if (Test-Path $servicePath) {
        Write-Host "Launching $service..." -ForegroundColor Cyan
        Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$servicePath'; .\mvnw spring-boot:run"
    }
    else {
        Write-Host "Error: Directory $service not found!" -ForegroundColor Red
    }
}

# 4. Start Frontend
$frontendPath = Join-Path $rootPath "frontend"
if (Test-Path $frontendPath) {
    Write-Host "Launching Frontend..." -ForegroundColor Cyan
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$frontendPath'; npm run dev"
}
else {
    Write-Host "Error: Frontend directory not found!" -ForegroundColor Red
}

Write-Host "All services launched in separate windows." -ForegroundColor Green
