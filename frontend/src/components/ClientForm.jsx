import React, { useState, useEffect } from 'react';

const ClientForm = ({ client, onSave, onCancel }) => {
    const [form, setForm] = useState({
        nombre: '',
        apellido: '',
        cedula: '',
        email: '',
        telefono: '',
        direccion: ''
    });

    useEffect(() => {
        if (client) {
            setForm(client);
        }
    }, [client]);

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        onSave(form);
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
            <div className="bg-white p-6 rounded-lg shadow-xl w-full max-w-md slide-up">
                <h3 className="text-xl font-bold mb-4 text-gray-800">
                    {client ? 'Editar Cliente' : 'Nuevo Cliente'}
                </h3>
                <form onSubmit={handleSubmit} className="space-y-4">
                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="block text-gray-700 text-sm font-bold mb-1">Nombre</label>
                            <input
                                name="nombre"
                                value={form.nombre}
                                onChange={handleChange}
                                className="input-field"
                                required
                            />
                        </div>
                        <div>
                            <label className="block text-gray-700 text-sm font-bold mb-1">Apellido</label>
                            <input
                                name="apellido"
                                value={form.apellido}
                                onChange={handleChange}
                                className="input-field"
                                required
                            />
                        </div>
                    </div>

                    <div>
                        <label className="block text-gray-700 text-sm font-bold mb-1">Cédula</label>
                        <input
                            name="cedula"
                            value={form.cedula}
                            onChange={handleChange}
                            className="input-field"
                            required
                        />
                    </div>

                    <div>
                        <label className="block text-gray-700 text-sm font-bold mb-1">Email</label>
                        <input
                            type="email"
                            name="email"
                            value={form.email}
                            onChange={handleChange}
                            className="input-field"
                            required
                        />
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="block text-gray-700 text-sm font-bold mb-1">Teléfono</label>
                            <input
                                name="telefono"
                                value={form.telefono}
                                onChange={handleChange}
                                className="input-field"
                                required
                            />
                        </div>
                        <div>
                            <label className="block text-gray-700 text-sm font-bold mb-1">Dirección</label>
                            <input
                                name="direccion"
                                value={form.direccion}
                                onChange={handleChange}
                                className="input-field"
                            />
                        </div>
                    </div>

                    <div className="flex gap-2 justify-end mt-6">
                        <button
                            type="button"
                            onClick={onCancel}
                            className="btn-secondary"
                        >
                            Cancelar
                        </button>
                        <button
                            type="submit"
                            className="btn-primary"
                        >
                            Guardar
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default ClientForm;
