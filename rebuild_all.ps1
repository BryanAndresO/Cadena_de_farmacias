# Rebuild all microservices after CORS configuration

Write-Host "Rebuilding microservices..." -ForegroundColor Cyan

$services = @("micro_Catalogo", "micro_Sucursal", "micro_Cliente", "micro_Ventas", "micro_Reporte")

foreach ($service in $services) {
    Write-Host "Building $service..." -ForegroundColor Yellow
    Set-Location $service
    .\mvnw package -DskipTests
    Set-Location ..
}

Write-Host "All microservices rebuilt successfully!" -ForegroundColor Green
