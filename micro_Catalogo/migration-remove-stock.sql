-- =========================================
-- Migration: Remove Stock from Catalog
-- Database: catalogodb
-- Microservice: micro_Catalogo
-- =========================================

-- Purpose: Eliminate the 'stock' column from medicamentos table
-- Reason: Stock management belongs to Inventario microservice, not Catalog

USE catalogodb;

-- Step 1: Verify current schema
SELECT 
    COLUMN_NAME, 
    DATA_TYPE, 
    IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'catalogodb' 
  AND TABLE_NAME = 'medicamentos';

-- Step 2: (OPTIONAL) Create backup of stock data before dropping
-- Uncomment if you want to preserve historical data
/*
CREATE TABLE IF NOT EXISTS medicamentos_stock_backup (
    id BIGINT PRIMARY KEY,
    stock INT,
    backup_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO medicamentos_stock_backup (id, stock)
SELECT id, stock FROM medicamentos
WHERE stock IS NOT NULL;
*/

-- Step 3: Drop the stock column
ALTER TABLE medicamentos 
DROP COLUMN IF EXISTS stock;

-- Step 4: Verify the column was removed
SELECT 
    COLUMN_NAME
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'catalogodb' 
  AND TABLE_NAME = 'medicamentos';

-- Expected result: Column list WITHOUT 'stock'
-- Columns should be: id, nombre, descripcion, laboratorio, precio

SELECT 'Migration completed successfully' AS status;
