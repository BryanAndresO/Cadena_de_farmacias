# 🏥 Sistema de Gestión de Cadena de Farmacias

Bienvenido al repositorio del **Sistema de Gestión de Cadena de Farmacias**, una solución integral y distribuida diseñada para modernizar y optimizar las operaciones de redes farmacéuticas. Este proyecto implementa una arquitectura de **Microservicios** robusta y escalable, garantizando alta disponibilidad, seguridad y facilidad de mantenimiento.

## 🚀 Visión General

Este sistema permite la administración centralizada de inventarios, ventas, clientes y sucursales en tiempo real. Gracias a su diseño modular, cada componente funciona de manera autónoma pero integrada, permitiendo una gestión fluida desde la adquisición de productos hasta la venta final al cliente.

### ✨ Características Principales

*   **Arquitectura Desacoplada**: Basada en microservicios independientes para Catálogo, Inventario, Ventas, Clientes, Sucursales y Reportes.
*   **Seguridad Avanzada**: Implementación de **OAuth2** y **OpenID Connect** con un servidor de autorización dedicado para proteger todos los recursos.
*   **Gateway Centralizado**: Un API Gateway inteligente que actúa como único punto de entrada, gestionando el enrutamiento y la seguridad de borde.
*   **Frontend Moderno**: Interfaz de usuario reactiva y amigable construida con **React** y **TailwindCSS**, ofreciendo una experiencia de usuario premium.
*   **Despliegue Contenerizado**: Integración total con **Docker** y **Docker Compose** para un despliegue rápido y consistente en cualquier entorno.

## 🛠️ Stack Tecnológico

El proyecto utiliza las últimas tecnologías del estándar de la industria:

### Backend (Microservicios)
*   **Lenguaje**: Java 17
*   **Framework**: Spring Boot 3.2.0
*   **Seguridad**: Spring Security, OAuth2 Authorization Server, Resource Server
*   **Enrutamiento**: Spring Cloud Gateway
*   **Base de Datos**: MySQL 8.0 (Servicios de Negocio), PostgreSQL 15 (Servidor de Auth)
*   **Persistencia**: JPA / Hibernate

### Frontend
*   **Framework**: React 18
*   **Build Tool**: Vite
*   **Estilos**: TailwindCSS
*   **Cliente HTTP**: Axios

### Infraestructura
*   **Contenedores**: Docker
*   **Orquestación**: Docker Compose

## 🧩 Módulos del Sistema

| Microservicio | Descripción | Puerto (Interno) |
| :--- | :--- | :--- |
| **Auth Server** | Gestión de identidades, emisión de tokens JWT y seguridad. | 9000 |
| **API Gateway** | Enrutamiento de peticiones, balanceo de carga y retransmisión de tokens. | 8080 |
| **Micro-Catálogo** | Gestión maestra de productos farmacéuticos y categorías. | 8081 |
| **Micro-Sucursal** | Administración de las diferentes sedes de la farmacia. | 8082 |
| **Micro-Cliente** | Gestión de información y perfiles de clientes. | 8083 |
| **Micro-Ventas** | Procesamiento de transacciones y facturación. | 8084 |
| **Micro-Reporte** | Generación de análisis y estadísticas del negocio. | 8085 |
| **Micro-Inventario** | Control de stock en tiempo real por sucursal. | 8086 |

## 📦 Instalación y Despliegue

Sigue estos pasos para levantar el proyecto en tu entorno local:

### Prerrequisitos
*   Docker y Docker Compose instalados.
*   Java JDK 17 (Opcional, si deseas ejecutar sin Docker).
*   Node.js 18+ (Opcional, para desarrollo frontend).

### Pasos

1.  **Clonar el repositorio**:
    ```bash
    git clone https://github.com/tu-usuario/cadena-farmacias.git
    cd cadena-farmacias
    ```

2.  **Construir y Levantar los Servicios**:
    El proyecto incluye una configuración completa de Docker Compose. Ejecuta el siguiente comando para construir las imágenes e iniciar todos los contenedores:
    ```bash
    docker-compose up --build -d
    ```

3.  **Verificar el Despliegue**:
    Una vez que todos los contenedores estén activos (puedes verificar con `docker-compose ps`), accede a la aplicación:
    *   **Frontend Web**: [http://localhost:8080](http://localhost:8080)
    *   (El Gateway redirigirá automáticamente el tráfico al frontend y gestionará la autenticación).

## 📄 Licencia

Este proyecto es software propietario diseñado para fines académicos y demostrativos.
