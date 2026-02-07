# =========================================
# Deploy Phase 1.6: Catalog Enhancements
# =========================================

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "  Deploying Catalog Enhancements" -ForegroundColor Cyan
Write-Host "  (New fields: Concentracion, Presentacion)" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

# Navigate to project root
Set-Location "c:\APLICACIONES DISTRIBUIDAS\Proyecto 3er Parcial\Cadena_de_farmacias"

Write-Host "1. Rebuilding affected services..." -ForegroundColor Yellow
docker-compose build micro-catalogo frontend

Write-Host "2. Restarting services..." -ForegroundColor Yellow
docker-compose up -d micro-catalogo frontend

Write-Host "3. Waiting for services to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

Write-Host "4. Applying Database Migration..." -ForegroundColor Yellow
# Try to run migration automatically if container is healthy
$containerId = docker-compose ps -q mysql-catalogo
if ($containerId) {
    Write-Host "   Applying migration to mysql-catalogo..." -ForegroundColor Gray
    # Using 'abcd' as password from previous context
    Get-Content ".\micro_Catalogo\migration-v2-add-details.sql" | docker exec -i mysql-catalogo mysql -uAppRoot -pabcd catalogodb
    if ($?) {
        Write-Host "   ✅ Migration applied successfully!" -ForegroundColor Green
    }
    else {
        Write-Host "   ❌ Auto-migration failed (Check password or container status)." -ForegroundColor Red
        Write-Host "   Please run manually: docker exec -i mysql-catalogo mysql -uAppRoot -p catalogodb < migration-v2-add-details.sql" -ForegroundColor Red
    }
}
else {
    Write-Host "   ❌ Database container not found." -ForegroundColor Red
}

Write-Host "`n=========================================" -ForegroundColor Green
Write-Host "  Deployment Complete!" -ForegroundColor Green
Write-Host "=========================================" -ForegroundColor Green
