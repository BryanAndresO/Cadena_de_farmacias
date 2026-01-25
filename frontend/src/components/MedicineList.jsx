import React, { useEffect, useState } from 'react';
import { apiCatalogo } from '../services/api';
import { extractApiMessage } from '../utils/error';
import { useAuth } from '../context/AuthContext';

import MedicineForm from './MedicineForm';

const MedicineList = () => {
    const { isAdmin } = useAuth();
    const [medicines, setMedicines] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showForm, setShowForm] = useState(false);

    // CRUD State
    const [editingMedicine, setEditingMedicine] = useState(null);

    useEffect(() => {
        fetchMedicines();
    }, []);

    const fetchMedicines = async () => {
        try {
            const response = await apiCatalogo.get('/medicamentos');
            setMedicines(response.data);
            setLoading(false);
        } catch (err) {
            setError('Error al cargar medicamentos');
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('¿Estás seguro de eliminar este medicamento?')) {
            try {
                await apiCatalogo.delete(`/medicamentos/${id}`);
                setMedicines(medicines.filter(m => m.id !== id));
                } catch (err) {
                console.error('Detalle técnico:', err);
                const msg = extractApiMessage(err);
                if (err.response?.status === 403) {
                    alert('No tienes permisos para eliminar medicamentos');
                } else {
                    alert('No fue posible eliminar el medicamento: ' + msg);
                }
            }
        }
    };

    const handleCreate = () => {
        setEditingMedicine(null);
        setShowForm(true);
    };

    const handleEdit = (med) => {
        setEditingMedicine(med);
        setShowForm(true);
    };

    const handleSave = () => {
        setShowForm(false);
        fetchMedicines();
    };

    if (loading) return <div className="p-8 text-center">Cargando catálogo...</div>;
    if (error) return <div className="p-8 text-center text-red-500">{error}</div>;

    return (
        <div className="slide-up">
            <div className="flex justify-between items-center mb-6">
                <div>
                    <h2 className="text-2xl font-semibold text-neutral-800">Catálogo de Medicamentos</h2>
                    <p className="text-neutral-500 text-sm">Gestión de productos farmacéuticos</p>
                </div>
                <button
                    onClick={handleCreate}
                    className="btn-primary w-auto flex items-center gap-2"
                >
                    <span>+</span> Nuevo Medicamento
                </button>
            </div>

            {showForm && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
                    <div className="bg-white p-6 rounded-lg shadow-xl w-full max-w-2xl max-h-[90vh] overflow-y-auto slide-up">
                        <MedicineForm
                            medicine={editingMedicine}
                            onSave={handleSave}
                            onCancel={() => setShowForm(false)}
                        />
                    </div>
                </div>
            )}

            <div className="bg-white rounded border border-neutral-200 overflow-hidden">
                <table className="min-w-full">
                    <thead>
                        <tr>
                            <th className="px-4 py-3 border-b border-neutral-200 bg-neutral-50 text-left text-xs font-medium text-neutral-600 uppercase tracking-wider">Nombre</th>
                            <th className="px-4 py-3 border-b border-neutral-200 bg-neutral-50 text-left text-xs font-medium text-neutral-600 uppercase tracking-wider">Laboratorio</th>
                            <th className="px-4 py-3 border-b border-neutral-200 bg-neutral-50 text-left text-xs font-medium text-neutral-600 uppercase tracking-wider">Precio</th>
                            <th className="px-4 py-3 border-b border-neutral-200 bg-neutral-50 text-left text-xs font-medium text-neutral-600 uppercase tracking-wider">Stock Base</th>
                            <th className="px-4 py-3 border-b border-neutral-200 bg-neutral-50 text-left text-xs font-medium text-neutral-600 uppercase tracking-wider">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        {medicines.map((med) => (
                            <tr key={med.id} className="hover:bg-neutral-50 transition-colors">
                                <td className="px-4 py-3 border-b border-neutral-100 text-sm font-medium text-neutral-800">{med.nombre}</td>
                                <td className="px-4 py-3 border-b border-neutral-100 text-sm text-neutral-600">{med.laboratorio}</td>
                                <td className="px-4 py-3 border-b border-neutral-100 text-sm font-medium text-neutral-800">${med.precio}</td>
                                <td className="px-4 py-3 border-b border-neutral-100 text-sm text-neutral-600">{med.stock}</td>
                                <td className="px-4 py-3 border-b border-neutral-100 text-sm">
                                    <div className="flex gap-2">
                                        {isAdmin() && (
                                            <>
                                                <button
                                                    onClick={() => handleEdit(med)}
                                                    className="text-neutral-600 hover:bg-neutral-100 px-3 py-1 rounded text-sm transition-colors"
                                                >
                                                    Editar
                                                </button>
                                                <button
                                                    onClick={() => handleDelete(med.id)}
                                                    className="text-neutral-500 hover:text-neutral-700 hover:bg-neutral-100 px-3 py-1 rounded text-sm transition-colors"
                                                >
                                                    Eliminar
                                                </button>
                                            </>
                                        )}
                                        {!isAdmin() && (
                                            <span className="text-neutral-400 text-xs">Solo lectura</span>
                                        )}
                                    </div>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default MedicineList;
