# =========================================
# Docker Restart Script
# Purpose: Rebuild and restart services after stock removal
# =========================================

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "  Docker Rebuild & Restart Script" -ForegroundColor Cyan
Write-Host "  Refactor: Stock Removed from Catalog" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

# Navigate to project root
Set-Location "c:\APLICACIONES DISTRIBUIDAS\Proyecto 3er Parcial\Cadena_de_farmacias"

# Option 1: Keep existing data, run SQL migration manually
Write-Host "[OPTION 1] Keep existing data + Manual SQL migration" -ForegroundColor Yellow
Write-Host "  1. Connect to MySQL container:" -ForegroundColor Gray
Write-Host "     docker exec -it mysql-catalogo mysql -uAppRoot -pabcd" -ForegroundColor Cyan
Write-Host ""
Write-Host "  2. Inside MySQL console, run:" -ForegroundColor Gray
Write-Host "     USE catalogodb;" -ForegroundColor Cyan
Write-Host "     ALTER TABLE medicamentos DROP COLUMN IF EXISTS stock;" -ForegroundColor Cyan
Write-Host "     EXIT;" -ForegroundColor Cyan
Write-Host ""

# Option 2: Fresh start (deletes all data)
Write-Host "[OPTION 2] Fresh start (DELETES ALL DATA)" -ForegroundColor Red
Write-Host "  Warning: This will delete ALL catalog data!" -ForegroundColor Red
Write-Host ""

$choice = Read-Host "Choose option (1=Keep data, 2=Fresh start, 3=Skip DB) [1]"
if ([string]::IsNullOrWhiteSpace($choice)) { $choice = "1" }

switch ($choice) {
    "1" {
        Write-Host "`n✓ Selected: Keep data + Manual migration" -ForegroundColor Green
        Write-Host "Please run the SQL commands above manually.`n" -ForegroundColor Yellow
        Read-Host "Press Enter when migration is complete"
    }
    "2" {
        Write-Host "`n⚠️  Deleting catalog database volume..." -ForegroundColor Red
        docker-compose down
        docker volume rm cadena_de_farmacias_mysql_catalogo_data
        Write-Host "✓ Volume deleted" -ForegroundColor Green
    }
    "3" {
        Write-Host "`n⚠️  Skipping database migration (not recommended)" -ForegroundColor Yellow
    }
}

# Rebuild affected services
Write-Host "`nRebuilding affected Docker services..." -ForegroundColor Cyan
docker-compose build micro-catalogo frontend

# Restart all services
Write-Host "`nRestarting all services..." -ForegroundColor Cyan
docker-compose up -d

# Wait for healthchecks
Write-Host "`nWaiting for services to be healthy..." -ForegroundColor Cyan
Start-Sleep -Seconds 10

# Show service status
Write-Host "`nService Status:" -ForegroundColor Cyan
docker-compose ps

# Show logs for catalog service
Write-Host "`nMicro-Catalogo Logs (last 20 lines):" -ForegroundColor Cyan
docker-compose logs --tail=20 micro-catalogo

Write-Host "`n=========================================" -ForegroundColor Green
Write-Host "  Migration Complete!" -ForegroundColor Green
Write-Host "=========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "  1. Open http://localhost:5173" -ForegroundColor Gray
Write-Host "  2. Go to Medicines section" -ForegroundColor Gray
Write-Host "  3. Try creating a new medicine (no stock field should appear)" -ForegroundColor Gray
Write-Host ""
