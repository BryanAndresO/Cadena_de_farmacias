import axios from 'axios';

// La URL base se obtiene de la configuración o usa el gateway por defecto
const API_BASE = '';

/**
 * Servicio para gestión de usuarios
 * Solo disponible para administradores
 */

/**
 * Obtener todos los usuarios
 */
export async function getUsers() {
    const response = await axios.get(`${API_BASE}/api/users`, {
        withCredentials: true
    });
    return response.data;
}

/**
 * Obtener un usuario por ID
 */
export async function getUserById(id) {
    const response = await axios.get(`${API_BASE}/api/users/${id}`, {
        withCredentials: true
    });
    return response.data;
}

/**
 * Crear un nuevo usuario
 */
export async function createUser(userData) {
    const response = await axios.post(`${API_BASE}/api/users`, userData, {
        withCredentials: true
    });
    return response.data;
}

/**
 * Actualizar un usuario existente
 */
export async function updateUser(id, userData) {
    const response = await axios.put(`${API_BASE}/api/users/${id}`, userData, {
        withCredentials: true
    });
    return response.data;
}

/**
 * Eliminar un usuario
 */
export async function deleteUser(id) {
    const response = await axios.delete(`${API_BASE}/api/users/${id}`, {
        withCredentials: true
    });
    return response.data;
}

/**
 * Cambiar estado activo/inactivo de un usuario
 */
export async function toggleUserStatus(id) {
    const response = await axios.patch(`${API_BASE}/api/users/${id}/toggle-status`, {}, {
        withCredentials: true
    });
    return response.data;
}
