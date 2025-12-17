import React, { useState, useEffect } from 'react';

const BranchForm = ({ branch, onSave, onCancel }) => {
    const [form, setForm] = useState({
        nombre: '',
        ciudad: ''
    });

    useEffect(() => {
        if (branch) {
            setForm(branch);
        }
    }, [branch]);

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        onSave(form);
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
            <div className="bg-white p-6 rounded-lg shadow-xl w-96 slide-up">
                <h3 className="text-xl font-bold mb-4">
                    {branch ? 'Editar Sucursal' : 'Nueva Sucursal'}
                </h3>
                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-gray-700 text-sm font-bold mb-2">Nombre</label>
                        <input
                            name="nombre"
                            value={form.nombre}
                            onChange={handleChange}
                            className="input-field"
                            required
                        />
                    </div>
                    <div>
                        <label className="block text-gray-700 text-sm font-bold mb-2">Ciudad</label>
                        <input
                            name="ciudad"
                            value={form.ciudad}
                            onChange={handleChange}
                            className="input-field"
                            required
                        />
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

export default BranchForm;
