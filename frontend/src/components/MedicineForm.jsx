import React, { useState } from 'react';
import { apiCatalogo } from '../services/api';

const MedicineForm = ({ onSave, onCancel }) => {
    const [formData, setFormData] = useState({
        nombre: '',
        laboratorio: '',
        descripcion: '',
        precio: '',
        stock: ''
    });

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await apiCatalogo.post('/medicamentos', {
                ...formData,
                precio: parseFloat(formData.precio),
                stock: parseInt(formData.stock)
            });
            alert('Medicamento guardado');
            onSave();
        } catch (err) {
            alert('Error al guardar: ' + (err.response?.data?.message || err.message));
        }
    };

    return (
        <div className="bg-white p-6 rounded-lg shadow-md mb-6">
            <h3 className="text-xl font-bold mb-4">Nuevo Medicamento</h3>
            <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                    <label className="block text-gray-700">Nombre</label>
                    <input name="nombre" value={formData.nombre} onChange={handleChange} className="w-full border p-2 rounded" required />
                </div>
                <div>
                    <label className="block text-gray-700">Laboratorio</label>
                    <input name="laboratorio" value={formData.laboratorio} onChange={handleChange} className="w-full border p-2 rounded" required />
                </div>
                <div className="md:col-span-2">
                    <label className="block text-gray-700">Descripción</label>
                    <input name="descripcion" value={formData.descripcion} onChange={handleChange} className="w-full border p-2 rounded" />
                </div>
                <div>
                    <label className="block text-gray-700">Precio</label>
                    <input type="number" step="0.01" name="precio" value={formData.precio} onChange={handleChange} className="w-full border p-2 rounded" required />
                </div>
                <div>
                    <label className="block text-gray-700">Stock Inicial</label>
                    <input type="number" name="stock" value={formData.stock} onChange={handleChange} className="w-full border p-2 rounded" required />
                </div>
                <div className="md:col-span-2 flex justify-end space-x-2">
                    <button type="button" onClick={onCancel} className="bg-gray-300 text-gray-700 px-4 py-2 rounded">Cancelar</button>
                    <button type="submit" className="bg-blue-600 text-white px-4 py-2 rounded">Guardar</button>
                </div>
            </form>
        </div>
    );
};

export default MedicineForm;
