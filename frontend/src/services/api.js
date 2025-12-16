import axios from 'axios';

// Base URLs for each microservice
// Note: In a real production env, these might be behind a gateway.
// verified ports from docker-compose.yml:
// Catalogo: 8081
// Sucursal: 8082
// Cliente: 8083
// Ventas: 8084
// Reporte: 8085
// Inventario: 8086

const apiCatalogo = axios.create({ baseURL: 'http://localhost:8081' });
const apiSucursal = axios.create({ baseURL: 'http://localhost:8082' });
const apiCliente = axios.create({ baseURL: 'http://localhost:8083' });
const apiVentas = axios.create({ baseURL: 'http://localhost:8084' });
const apiReporte = axios.create({ baseURL: 'http://localhost:8085' });
const apiInventario = axios.create({ baseURL: 'http://localhost:8086/api/inventario' });

export {
    apiCatalogo,
    apiSucursal,
    apiCliente,
    apiVentas,
    apiReporte,
    apiInventario
};
