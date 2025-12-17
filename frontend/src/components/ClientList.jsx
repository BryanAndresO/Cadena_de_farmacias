import React, { useState, useEffect } from 'react';
import { apiCliente } from '../services/api';

const ClientList = () => {
    const [clients, setClients] = useState([]);
    const [form, setForm] = useState({ nombre: '', apellido: '', email: '', telefono: '', direccion: '' });
    const [isEditing, setIsEditing] = useState(false);
    const [currentId, setCurrentId] = useState(null);
    const [message, setMessage] = useState('');

    useEffect(() => {
        fetchClients();
    }, []);

    const fetchClients = async () => {
        try {
            const response = await apiCliente.get('/');
            setClients(response.data);
        } catch (error) {
            console.error("Error fetching clients", error);
        }
    };

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (isEditing) {
                await apiCliente.put(`/${currentId}`, form);
                setMessage('Cliente actualizado correctamente');
            } else {
                await apiCliente.post('/', form);
                setMessage('Cliente creado correctamente');
            }
            setForm({ nombre: '', apellido: '', email: '', telefono: '', direccion: '' });
            setIsEditing(false);
            setCurrentId(null);
            fetchClients();
        } catch (error) {
            console.error("Error saving client", error);
            setMessage('Error al guardar cliente');
        }
    };

    const handleEdit = (client) => {
        setForm(client);
        setIsEditing(true);
        setCurrentId(client.id);
    };

    const handleDelete = async (id) => {
        if (window.confirm('¿Estás seguro de eliminar este cliente?')) {
            try {
                await apiCliente.delete(`/${id}`);
                fetchClients();
                setMessage('Cliente eliminado');
            } catch (error) {
                console.error("Error deleting client", error);
            }
        }
    };

    return (
        <div className="glass p-8 slide-up">
            <h2 className="text-3xl font-bold mb-6 text-gray-800">Gestión de Clientes</h2>

            {message && (
                <div className={`p-4 mb-4 rounded-lg text-white ${message.includes('Error') ? 'bg-red-500' : 'bg-green-500'}`}>
                    {message}
                </div>
            )}

            <div className="bg-white/50 backdrop-blur-sm p-6 rounded-xl shadow-sm mb-8">
                <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <input name="nombre" placeholder="Nombre" value={form.nombre} onChange={handleChange} className="input-field" required />
                    <input name="apellido" placeholder="Apellido" value={form.apellido} onChange={handleChange} className="input-field" required />
                    <input name="email" placeholder="Email" value={form.email} onChange={handleChange} className="input-field" required />
                    <input name="telefono" placeholder="Teléfono" value={form.telefono} onChange={handleChange} className="input-field" required />
                    <input name="direccion" placeholder="Dirección" value={form.direccion} onChange={handleChange} className="input-field md:col-span-2" required />

                    <button type="submit" className="btn-primary md:col-span-2">
                        {isEditing ? 'Actualizar Cliente' : 'Guardar Cliente'}
                    </button>
                    {isEditing && (
                        <button type="button" onClick={() => { setIsEditing(false); setForm({ nombre: '', apellido: '', email: '', telefono: '', direccion: '' }); }} className="btn-secondary md:col-span-2">
                            Cancelar Edición
                        </button>
                    )}
                </form>
            </div>

            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                {clients.map(client => (
                    <div key={client.id} className="bg-white p-6 rounded-xl shadow-md hover:shadow-lg transition-transform hover:-translate-y-1">
                        <div className="flex justify-between items-start mb-4">
                            <div>
                                <h3 className="text-xl font-semibold text-gray-800">{client.nombre} {client.apellido}</h3>
                                <p className="text-sm text-gray-500">{client.email}</p>
                            </div>
                            <div className="bg-blue-100 text-blue-800 text-xs px-2 py-1 rounded-full">ID: {client.id}</div>
                        </div>
                        <div className="space-y-2 text-gray-600 mb-4">
                            <p className="flex items-center gap-2"><span className="font-medium">Tel:</span> {client.telefono}</p>
                            <p className="flex items-center gap-2"><span className="font-medium">Dir:</span> {client.direccion}</p>
                        </div>
                        <div className="flex gap-2 mt-4 pt-4 border-t border-gray-100">
                            <button onClick={() => handleEdit(client)} className="flex-1 px-3 py-2 text-sm text-blue-600 hover:bg-blue-50 rounded-lg transition-colors">
                                Editar
                            </button>
                            <button onClick={() => handleDelete(client.id)} className="flex-1 px-3 py-2 text-sm text-red-600 hover:bg-red-50 rounded-lg transition-colors">
                                Eliminar
                            </button>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default ClientList;
