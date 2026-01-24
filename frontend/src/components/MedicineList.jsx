import React, { useEffect, useState } from 'react';
import { apiCatalogo } from '../services/api';
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
                if (err.response?.status === 403) {
                    alert('No tienes permisos para eliminar medicamentos');
                } else {
                    alert('Error al eliminar');
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
            <div className="flex justify-between items-center mb-8">
                <div>
                    <h2 className="text-3xl font-bold text-gray-800">Catálogo de Medicamentos</h2>
                    <p className="text-gray-500">Gestión de productos farmacéuticos</p>
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

            <div className="bg-white shadow-sm rounded-xl overflow-hidden border border-gray-100">
                <table className="min-w-full leading-normal">
                    <thead>
                        <tr>
                            <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-50 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Nombre</th>
                            <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-50 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Laboratorio</th>
                            <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-50 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Precio</th>
                            <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-50 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Stock Base</th>
                            <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-50 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        {medicines.map((med) => (
                            <tr key={med.id} className="hover:bg-gray-50 transition-colors">
                                <td className="px-5 py-5 border-b border-gray-100 bg-white text-sm font-medium text-gray-900">{med.nombre}</td>
                                <td className="px-5 py-5 border-b border-gray-100 bg-white text-sm text-gray-600">{med.laboratorio}</td>
                                <td className="px-5 py-5 border-b border-gray-100 bg-white text-sm font-bold text-green-600">${med.precio}</td>
                                <td className="px-5 py-5 border-b border-gray-100 bg-white text-sm text-gray-600">{med.stock}</td>
                                <td className="px-5 py-5 border-b border-gray-100 bg-white text-sm">
                                    <div className="flex gap-2">
                                        {isAdmin() && (
                                            <>
                                                <button
                                                    onClick={() => handleEdit(med)}
                                                    className="text-blue-600 hover:bg-blue-50 px-3 py-1 rounded transition-colors"
                                                >
                                                    Editar
                                                </button>
                                                <button
                                                    onClick={() => handleDelete(med.id)}
                                                    className="text-red-600 hover:bg-red-50 px-3 py-1 rounded transition-colors"
                                                >
                                                    Eliminar
                                                </button>
                                            </>
                                        )}
                                        {!isAdmin() && (
                                            <span className="text-gray-400 text-xs">Solo lectura</span>
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
