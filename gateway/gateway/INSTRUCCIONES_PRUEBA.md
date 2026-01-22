# Guía de Pruebas del API Gateway

Esta guía explica cómo funciona el enrutamiento y cómo probar tus microservicios a través del Gateway.

## 1. Configuración del Enrutamiento

Hemos configurado el Gateway para usar **puertos estándar** y rutas amigables que ocultan la complejidad de los microservicios.

| Microservicio | URL Gateway (Pública) | URL Interna (Docker/Local) | Prefijo Eliminado (StripPrefix=2) |
| :--- | :--- | :--- | :--- |
| **Catálogo** | `http://localhost:8080/api/catalogo/medicamentos` | `http://localhost:8081/medicamentos` | `/api/catalogo` |
| **Sucursal** | `http://localhost:8080/api/sucursal/sucursales` | `http://localhost:8082/sucursales` | `/api/sucursal` |
| **Cliente** | `http://localhost:8080/api/cliente/clientes` | `http://localhost:8083/clientes` | `/api/cliente` |
| **Ventas** | `http://localhost:8080/api/ventas/ventas` | `http://localhost:8084/ventas` | `/api/ventas` |
| **Reporte** | `http://localhost:8080/api/reporte/reportes` | `http://localhost:8085/reportes` | `/api/reporte` |
| **Inventario** | `http://localhost:8080/api/inventario/inventarios` | `http://localhost:8086/inventarios` | `/api/inventario` |

> **Nota:** La parte `/api/{servicio}` es solo para organizar el Gateway. El microservicio final NO ve esa parte gracias a `StripPrefix=2`.

## 2. Cómo Ejecutar

### Paso 1: Iniciar Bases de Datos y Microservicios
(Si aún no lo has hecho)
```powershell
cd "c:\APLICACIONES DISTRIBUIDAS\Microservicios\microservi_bke"
docker-compose up -d
```
Verifica que todo esté corriendo con `docker ps`.

### Paso 2: Iniciar el Gateway
```powershell
cd "c:\APLICACIONES DISTRIBUIDAS\Microservicios\gateway\gateway"
mvn spring-boot:run
```

## 3. Pruebas con Postman o Navegador

Simplemente abre estas URLs en tu navegador para verificar que obtienes datos (JSON):

1.  **Catálogo:** [http://localhost:8080/api/catalogo/medicamentos](http://localhost:8080/api/catalogo/medicamentos)
2.  **Sucursales:** [http://localhost:8080/api/sucursal/sucursales](http://localhost:8080/api/sucursal/sucursales)

### ¿Por qué fallaba antes?
Antes tenías `StripPrefix=1`.
*   Petición: `/api/catalogo/medicamentos`
*   Gateway quitaba 1 prefijo (`/api`): Enviaba `/catalogo/medicamentos` al microservicio.
*   Microservicio: Solo conoce `/medicamentos`. Al recibir `/catalogo/...` devolvía **404 Not Found**.
*   Ahora con `StripPrefix=2`, quita `/api/catalogo` y envía `/medicamentos`, ¡correcto!
