import React, { useState } from 'react';
import { apiCatalogo } from '../services/api';
import { extractApiMessage } from '../utils/error';

const MedicineForm = ({ medicine, onSave, onCancel }) => {
    const [formData, setFormData] = useState({
        nombre: '',
        laboratorio: '',
        descripcion: '',
        precio: '',
        concentracion: '',
        presentacion: ''
    });

    // Populate form if editing
    React.useEffect(() => {
        if (medicine) {
            setFormData({
                nombre: medicine.nombre,
                laboratorio: medicine.laboratorio,
                descripcion: medicine.descripcion || '',
                precio: medicine.precio,
                concentracion: medicine.concentracion || '',
                presentacion: medicine.presentacion || ''
            });
        }
    }, [medicine]);

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const payload = {
                ...formData,
                precio: parseFloat(formData.precio)
            };

            if (medicine) {
                await apiCatalogo.put(`/medicamentos/${medicine.id}`, payload);
                alert('Medicamento actualizado');
            } else {
                await apiCatalogo.post('/medicamentos', payload);
                alert('Medicamento guardado');
            }
            onSave();
        } catch (err) {
            console.error('Detalle técnico:', err);
            const msg = extractApiMessage(err);
            alert('No fue posible guardar el medicamento: ' + msg);
        }
    };

    return (
        <div className="bg-white rounded">
            <h3 className="text-lg font-semibold mb-4 text-neutral-800">{medicine ? 'Editar Medicamento' : 'Nuevo Medicamento'}</h3>
            <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                    <label className="block text-neutral-600 text-sm mb-1">Nombre</label>
                    <input name="nombre" value={formData.nombre} onChange={handleChange} className="w-full border border-neutral-200 p-2 rounded text-sm focus:border-neutral-400 focus:outline-none" required />
                </div>
                <div>
                    <label className="block text-neutral-600 text-sm mb-1">Laboratorio</label>
                    <input name="laboratorio" value={formData.laboratorio} onChange={handleChange} className="w-full border border-neutral-200 p-2 rounded text-sm focus:border-neutral-400 focus:outline-none" required />
                </div>
                <div className="md:col-span-2">
                    <label className="block text-neutral-600 text-sm mb-1">Descripción</label>
                    <input name="descripcion" value={formData.descripcion} onChange={handleChange} className="w-full border border-neutral-200 p-2 rounded text-sm focus:border-neutral-400 focus:outline-none" />
                </div>
                <div>
                    <label className="block text-neutral-600 text-sm mb-1">Precio</label>
                    <input type="number" step="0.01" name="precio" value={formData.precio} onChange={handleChange} className="w-full border border-neutral-200 p-2 rounded text-sm focus:border-neutral-400 focus:outline-none" required />
                </div>
                <div>
                    <label className="block text-neutral-600 text-sm mb-1">Concentración</label>
                    <input name="concentracion" value={formData.concentracion} onChange={handleChange} className="w-full border border-neutral-200 p-2 rounded text-sm focus:border-neutral-400 focus:outline-none" placeholder="Ej. 500mg" />
                    <p className="text-xs text-neutral-400 mt-1">Ej. 500mg, 10ml, 5%</p>
                </div>
                <div>
                    <label className="block text-neutral-600 text-sm mb-1">Presentación</label>
                    <input name="presentacion" value={formData.presentacion} onChange={handleChange} className="w-full border border-neutral-200 p-2 rounded text-sm focus:border-neutral-400 focus:outline-none" placeholder="Ej. Caja x 10" />
                    <p className="text-xs text-neutral-400 mt-1">Ej. Caja, Jarabe, Blister</p>
                </div>
                <div className="md:col-span-2 flex justify-end space-x-2">
                    <button type="button" onClick={onCancel} className="bg-neutral-100 text-neutral-700 px-4 py-2 rounded text-sm hover:bg-neutral-200 border border-neutral-200">Cancelar</button>
                    <button type="submit" className="bg-zinc-800 text-white px-4 py-2 rounded text-sm hover:bg-zinc-700">Guardar</button>
                </div>
            </form>
        </div>
    );
};

export default MedicineForm;
