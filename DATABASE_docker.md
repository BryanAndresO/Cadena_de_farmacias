# Database Setup - Microservicios

## Red Docker
```bash
docker network create red-microservicios
```

## Contenedores MySQL

### 1. Catálogo (Puerto 3309)
```bash
docker run -d -p 3309:3306 --name mysql-catalogo --network red-microservicios \
  -e MYSQL_DATABASE=catalogodb \
  -e MYSQL_ROOT_PASSWORD=abcd \
  -e MYSQL_USER=AppRoot \
  -e MYSQL_PASSWORD=abcd \
  -v mysql_catalogo_data:/var/lib/mysql \
  mysql:8.0
```

### 2. Sucursal (Puerto 3310)
```bash
docker run -d -p 3310:3306 --name mysql-sucursal --network red-microservicios \
  -e MYSQL_DATABASE=sucursalesdb \
  -e MYSQL_ROOT_PASSWORD=abcd \
  -e MYSQL_USER=AppRoot \
  -e MYSQL_PASSWORD=abcd \
  -v mysql_sucursal_data:/var/lib/mysql \
  mysql:8.0
```

### 3. Cliente (Puerto 3311)
```bash
docker run -d -p 3311:3306 --name mysql-cliente --network red-microservicios \
  -e MYSQL_DATABASE=clientesdb \
  -e MYSQL_ROOT_PASSWORD=abcd \
  -e MYSQL_USER=AppRoot \
  -e MYSQL_PASSWORD=abcd \
  -v mysql_cliente_data:/var/lib/mysql \
  mysql:8.0
```

### 4. Ventas (Puerto 3312)
```bash
docker run -d -p 3312:3306 --name mysql-ventas --network red-microservicios \
  -e MYSQL_DATABASE=ventasdb \
  -e MYSQL_ROOT_PASSWORD=abcd \
  -e MYSQL_USER=AppRoot \
  -e MYSQL_PASSWORD=abcd \
  -v mysql_ventas_data:/var/lib/mysql \
  mysql:8.0
```

### 5. Reporte (Puerto 3313)
```bash
docker run -d -p 3313:3306 --name mysql-reporte --network red-microservicios \
  -e MYSQL_DATABASE=reportesdb \
  -e MYSQL_ROOT_PASSWORD=abcd \
  -e MYSQL_USER=AppRoot \
  -e MYSQL_PASSWORD=abcd \
  -v mysql_reporte_data:/var/lib/mysql \
  mysql:8.0
```

### 6. Inventario (Puerto 3314)
```bash
docker run -d -p 3314:3306 --name mysql-inventario --network red-microservicios \
  -e MYSQL_DATABASE=inventariodb \
  -e MYSQL_ROOT_PASSWORD=abcd \
  -e MYSQL_USER=AppRoot \
  -e MYSQL_PASSWORD=abcd \
  -v mysql_inventario_data:/var/lib/mysql \
  mysql:8.0
```

## Comandos Útiles

### Ver contenedores en la red
```bash
docker ps --filter "network=red-microservicios"
```

### Detener todos los contenedores
```bash
docker stop mysql-catalogo mysql-sucursal mysql-cliente mysql-ventas mysql-reporte mysql-inventario
```

### Eliminar todos los contenedores
```bash
docker rm mysql-catalogo mysql-sucursal mysql-cliente mysql-ventas mysql-reporte mysql-inventario
```

### Eliminar volúmenes (CUIDADO: borra los datos)
```bash
docker volume rm mysql_catalogo_data mysql_sucursal_data mysql_cliente_data mysql_ventas_data mysql_reporte_data mysql_inventario_data
```

## Configuración de Microservicios

Cada microservicio debe configurar su `application.properties`:

```properties
# Inventario
spring.datasource.url=jdbc:mysql://localhost:3314/inventariodb
spring.datasource.username=AppRoot
spring.datasource.password=abcd
```

**Nota**: Cambiar el puerto según el microservicio (3309-3314).
