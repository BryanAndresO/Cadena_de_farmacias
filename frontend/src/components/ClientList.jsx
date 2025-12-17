import React, { useState, useEffect } from 'react';
import { apiCliente } from '../services/api';
import ClientForm from './ClientForm';

const ClientList = () => {
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
                alert('Error al eliminar cliente');
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
            alert('Error al guardar cliente');
            console.error(error);
        }
    };

    if (loading) return <div className="p-8 text-center">Cargando clientes...</div>;

    return (
        <div className="slide-up">
            <div className="flex justify-between items-center mb-8">
                <div>
                    <h2 className="text-3xl font-bold text-gray-800">Gestión de Clientes</h2>
                    <p className="text-gray-500">Administración de la base de datos de compradores</p>
                </div>
                <button
                    onClick={handleCreate}
                    className="btn-primary w-auto flex items-center gap-2"
                >
                    <span>+</span> Nuevo Cliente
                </button>
            </div>

            <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
                {clients.map(client => (
                    <div key={client.id} className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 hover:shadow-md transition-all hover:-translate-y-1">
                        <div className="flex justify-between items-start mb-4">
                            <div className="flex items-center gap-3">
                                <div className="w-10 h-10 rounded-full bg-gradient-to-br from-indigo-500 to-purple-600 flex items-center justify-center text-white font-bold text-lg">
                                    {client.nombre?.charAt(0) || '?'}{client.apellido?.charAt(0) || '?'}
                                </div>
                                <div>
                                    <h3 className="text-lg font-bold text-gray-800 leading-tight">{client.nombre || 'N/A'} {client.apellido || ''}</h3>
                                    <p className="text-xs text-indigo-600 font-medium">{client.email || 'N/A'}</p>
                                </div>
                            </div>
                            <span className="text-xs font-mono text-gray-400 bg-gray-100 px-2 py-1 rounded">#{client.id}</span>
                        </div>

                        <div className="space-y-2 text-sm text-gray-600 mb-6 bg-gray-50 p-3 rounded-lg">
                            <div className="flex items-center gap-2">
                                <span className="w-5 text-center">🆔</span>
                                <span className="font-medium text-gray-700">{client.cedula}</span>
                            </div>
                            <div className="flex items-center gap-2">
                                <span className="w-5 text-center">📞</span>
                                <span>{client.telefono}</span>
                            </div>
                            <div className="flex items-center gap-2">
                                <span className="w-5 text-center">📍</span>
                                <span className="truncate" title={client.direccion}>{client.direccion}</span>
                            </div>
                        </div>

                        <div className="flex gap-2 border-t border-gray-100 pt-4">
                            <button
                                onClick={() => handleEdit(client)}
                                className="flex-1 py-2 text-sm font-medium text-indigo-600 bg-indigo-50 hover:bg-indigo-100 rounded-lg transition-colors"
                            >
                                Editar
                            </button>
                            <button
                                onClick={() => handleDelete(client.id)}
                                className="flex-1 py-2 text-sm font-medium text-red-600 bg-red-50 hover:bg-red-100 rounded-lg transition-colors"
                            >
                                Eliminar
                            </button>
                        </div>
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
