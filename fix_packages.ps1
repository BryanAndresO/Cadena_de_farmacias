# Fix Package Structure
# Moves files from com.espe.test to com.espe.[service] and updates package/imports

$services = @(
    @{Dir = "micro_Cliente"; Pkg = "cliente" },
    @{Dir = "micro_Sucursal"; Pkg = "sucursal" },
    @{Dir = "micro_Ventas"; Pkg = "ventas" },
    @{Dir = "micro_Reporte"; Pkg = "reporte" },
    @{Dir = "micro_Inventario"; Pkg = "inventario" },
    @{Dir = "micro_Catalogo"; Pkg = "catalogo" }
)

$root = Get-Location

foreach ($svc in $services) {
    $servicePath = Join-Path $root $svc.Dir
    $basePkgPath = Join-Path $servicePath "src\main\java\com\espe"
    $oldPkgPath = Join-Path $basePkgPath "test"
    $newPkgPath = Join-Path $basePkgPath $svc.Pkg

    if ((Test-Path $oldPkgPath) -and (Test-Path $newPkgPath)) {
        Write-Host "Fixing $($svc.Dir)..." -ForegroundColor Cyan

        # 1. Move all items from old package to new package
        Get-ChildItem -Path $oldPkgPath | Move-Item -Destination $newPkgPath -Force

        # 2. Update content of all Java files in the new package
        $javaFiles = Get-ChildItem -Path $newPkgPath -Recurse -Filter "*.java"
        
        foreach ($file in $javaFiles) {
            $content = Get-Content $file.FullName -Raw
            
            # Replace package declaration
            # E.g., package com.espe.test.controllers; -> package com.espe.catalogo.controllers;
            $content = $content -replace "package com.espe.test", "package com.espe.$($svc.Pkg)"
            
            # Replace imports
            # E.g., import com.espe.test.models.X; -> import com.espe.catalogo.models.X;
            $content = $content -replace "import com.espe.test", "import com.espe.$($svc.Pkg)"
            
            Set-Content -Path $file.FullName -Value $content
        }

        # 3. Remove old empty directory
        if ((Get-ChildItem $oldPkgPath).Count -eq 0) {
            Remove-Item $oldPkgPath
        }
        
        Write-Host "Fixed $($svc.Dir)" -ForegroundColor Green
    }
    else {
        Write-Host "Skipping $($svc.Dir): Paths not found or already fixed." -ForegroundColor Yellow
    }
}
