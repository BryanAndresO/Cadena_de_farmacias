import React, { useState, useEffect } from 'react';
import { apiCliente } from '../services/api';
import { extractApiMessage } from '../utils/error';
import { useAuth } from '../context/AuthContext';
import ClientForm from './ClientForm';

const ClientList = () => {
    const { isAdmin } = useAuth();
    const [clients, setClients] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // CRUD State
    const [showForm, setShowForm] = useState(false);
    const [editingClient, setEditingClient] = useState(null);

    useEffect(() => {
        fetchClients();
    }, []);

    const fetchClients = async () => {
        try {
            const response = await apiCliente.get('/clientes/');
            setClients(response.data);
            setLoading(false);
        } catch (error) {
            console.error("Error fetching clients", error);
            setError('Error al cargar clientes');
            setLoading(false);
        }
    };

    const handleCreate = () => {
        setEditingClient(null);
        setShowForm(true);
    };

    const handleEdit = (client) => {
        setEditingClient(client);
        setShowForm(true);
    };

    const handleDelete = async (id) => {
        if (window.confirm('¿Estás seguro de eliminar este cliente?')) {
            try {
                await apiCliente.delete(`/clientes/${id}`);
                fetchClients();
            } catch (error) {
                console.error('Detalle técnico:', error);
                if (error.response?.status === 403) {
                    alert('No tienes permisos para eliminar clientes');
                } else {
                    alert('No fue posible eliminar el cliente: ' + extractApiMessage(error));
                }
            }
        }
    };

    const handleSave = async (clientData) => {
        try {
            if (editingClient) {
                await apiCliente.put(`/clientes/${editingClient.id}`, clientData);
            } else {
                await apiCliente.post('/clientes/', clientData);
            }
            setShowForm(false);
            fetchClients();
        } catch (error) {
            console.error('Detalle técnico:', error);
            if (error.response?.status === 403) {
                alert('No tienes permisos para esta operación');
            } else {
                alert('No fue posible guardar el cliente: ' + extractApiMessage(error));
            }
        }
    };

    if (loading) return <div className="p-8 text-center">Cargando clientes...</div>;

    return (
        <div className="slide-up">
            <div className="flex justify-between items-center mb-6">
                <div>
                    <h2 className="text-2xl font-semibold text-neutral-800">Gestión de Clientes</h2>
                    <p className="text-neutral-500 text-sm">Administración de la base de datos de compradores</p>
                </div>
                <button
                    onClick={handleCreate}
                    className="btn-primary w-auto flex items-center gap-2"
                >
                    <span>+</span> Nuevo Cliente
                </button>
            </div>

            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                {clients.map(client => (
                    <div key={client.id} className="bg-white p-5 rounded border border-neutral-200 hover:border-neutral-300 transition-colors">
                        <div className="flex justify-between items-start mb-4">
                            <div className="flex items-center gap-3">
                                <div className="w-9 h-9 rounded bg-neutral-200 flex items-center justify-center text-neutral-600 font-medium text-sm">
                                    {client.nombre?.charAt(0) || '?'}{client.apellido?.charAt(0) || '?'}
                                </div>
                                <div>
                                    <h3 className="text-base font-semibold text-neutral-800 leading-tight">{client.nombre || 'N/A'} {client.apellido || ''}</h3>
                                    <p className="text-xs text-neutral-500">{client.email || 'N/A'}</p>
                                </div>
                            </div>
                            <span className="text-xs font-mono text-neutral-400 bg-neutral-100 px-2 py-1 rounded">#{client.id}</span>
                        </div>

                        <div className="space-y-2 text-sm text-neutral-600 mb-5 bg-neutral-50 p-3 rounded">
                            <div className="flex items-center gap-2">
                                <span className="w-5 text-center text-xs">🆔</span>
                                <span className="font-medium text-neutral-700">{client.cedula}</span>
                            </div>
                            <div className="flex items-center gap-2">
                                <span className="w-5 text-center text-xs">📞</span>
                                <span>{client.telefono}</span>
                            </div>
                            <div className="flex items-center gap-2">
                                <span className="w-5 text-center text-xs">📍</span>
                                <span className="truncate" title={client.direccion}>{client.direccion}</span>
                            </div>
                        </div>

                        {isAdmin() && (
                            <div className="flex gap-2 border-t border-neutral-100 pt-3">
                                <button
                                    onClick={() => handleEdit(client)}
                                    className="flex-1 py-2 text-sm font-medium text-neutral-600 bg-neutral-100 hover:bg-neutral-200 rounded transition-colors"
                                >
                                    Editar
                                </button>
                                <button
                                    onClick={() => handleDelete(client.id)}
                                    className="flex-1 py-2 text-sm font-medium text-neutral-500 bg-neutral-100 hover:bg-neutral-200 rounded transition-colors"
                                >
                                    Eliminar
                                </button>
                            </div>
                        )}
                        {!isAdmin() && (
                            <div className="border-t border-neutral-100 pt-3 text-center">
                                <span className="text-xs text-neutral-400">Solo lectura</span>
                            </div>
                        )}
                    </div>
                ))}
            </div>

            {showForm && (
                <ClientForm
                    client={editingClient}
                    onSave={handleSave}
                    onCancel={() => setShowForm(false)}
                />
            )}
        </div>
    );
};

export default ClientList;
