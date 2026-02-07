-- =========================================
-- Migration: Add Product Details to Catalog
-- Database: catalogodb
-- Microservice: micro_Catalogo
-- =========================================

-- Purpose: Add 'concentracion' and 'presentacion' columns
-- Reason: Improve product description and categorization in frontend

USE catalogodb;

-- Step 1: Add columns if they don't exist
-- Note: MySQL 8.0 supports IF NOT EXISTS in ALTER TABLE for columns, 
-- but to be safe and compatible with older versions or specific modes,
-- we'll just run the add command assuming it's a fresh migration.
-- If re-running, check manually first.

ALTER TABLE medicamentos
ADD COLUMN concentracion VARCHAR(100) NULL,
ADD COLUMN presentacion VARCHAR(100) NULL;

-- Step 2: Verify columns
SELECT COLUMN_NAME 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'medicamentos' 
  AND COLUMN_NAME IN ('concentracion', 'presentacion');

-- Expected result: both columns should appear.
