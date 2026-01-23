import axios from 'axios';

// Base URL for the API Gateway
// In production (Docker), the frontend is served through the Gateway
// In development, we access the Gateway directly
const API_BASE_URL = window.location.hostname === 'localhost' 
    ? 'http://localhost:8080' 
    : '';  // Empty for relative paths when served through Gateway

// Create axios instance with base URL
const api = axios.create({ 
    baseURL: API_BASE_URL,
    withCredentials: true  // Important for OAuth2 cookies/sessions
});

// Attach bearer token from localStorage if present
api.interceptors.request.use((config) => {
    const token = localStorage.getItem('access_token');
    if (token) {
        config.headers = config.headers || {};
        config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
});

// Individual API endpoints through Gateway routes
const apiCatalogo = {
    get: (path) => api.get(`/api/catalogo${path}`),
    post: (path, data) => api.post(`/api/catalogo${path}`, data),
    put: (path, data) => api.put(`/api/catalogo${path}`, data),
    delete: (path) => api.delete(`/api/catalogo${path}`)
};

const apiSucursal = {
    get: (path) => api.get(`/api/sucursal${path}`),
    post: (path, data) => api.post(`/api/sucursal${path}`, data),
    put: (path, data) => api.put(`/api/sucursal${path}`, data),
    delete: (path) => api.delete(`/api/sucursal${path}`)
};

const apiCliente = {
    get: (path) => api.get(`/api/cliente${path}`),
    post: (path, data) => api.post(`/api/cliente${path}`, data),
    put: (path, data) => api.put(`/api/cliente${path}`, data),
    delete: (path) => api.delete(`/api/cliente${path}`)
};

const apiVentas = {
    get: (path) => api.get(`/api/ventas${path}`),
    post: (path, data) => api.post(`/api/ventas${path}`, data),
    put: (path, data) => api.put(`/api/ventas${path}`, data),
    delete: (path) => api.delete(`/api/ventas${path}`)
};

const apiReporte = {
    get: (path) => api.get(`/api/reporte${path}`),
    post: (path, data) => api.post(`/api/reporte${path}`, data),
    put: (path, data) => api.put(`/api/reporte${path}`, data),
    delete: (path) => api.delete(`/api/reporte${path}`)
};

const apiInventario = {
    get: (path) => api.get(`/api/inventario${path}`),
    post: (path, data) => api.post(`/api/inventario${path}`, data),
    put: (path, data) => api.put(`/api/inventario${path}`, data),
    patch: (path, data) => api.patch(`/api/inventario${path}`, data),
    delete: (path) => api.delete(`/api/inventario${path}`)
};

export {
    apiCatalogo,
    apiSucursal,
    apiCliente,
    apiVentas,
    apiReporte,
    apiInventario
};

