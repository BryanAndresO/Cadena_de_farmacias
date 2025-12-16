import React, { useEffect, useState } from 'react';
import { apiCatalogo } from '../services/api';

import MedicineForm from './MedicineForm';

const MedicineList = () => {
    const [medicines, setMedicines] = useState([]);
    const [showForm, setShowForm] = useState(false);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        fetchMedicines();
    }, []);

    const fetchMedicines = async () => {
        try {
            const response = await apiCatalogo.get('/medicamentos/');
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
                alert('Error al eliminar');
            }
        }
    };

    if (loading) return <div className="text-center p-4">Cargando...</div>;
    if (error) return <div className="text-center p-4 text-red-500">{error}</div>;

    return (
        <div className="container mx-auto p-4">
            <div className="flex justify-between items-center mb-4">
                <h2 className="text-2xl font-bold">Gestión de Medicamentos</h2>
                <button
                    onClick={() => setShowForm(!showForm)}
                    className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700"
                >
                    {showForm ? 'Cerrar Formulario' : 'Nuevo Medicamento'}
                </button>
            </div>

            {showForm && (
                <MedicineForm
                    onSave={() => {
                        setShowForm(false);
                        fetchMedicines();
                    }}
                    onCancel={() => setShowForm(false)}
                />
            )}

            <div className="bg-white shadow-md rounded-lg overflow-hidden">
                <table className="min-w-full leading-normal">
                    <thead>
                        <tr>
                            <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Nombre</th>
                            <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Laboratorio</th>
                            <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Precio</th>
                            <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Stock</th>
                            <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        {medicines.map((med) => (
                            <tr key={med.id}>
                                <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm">{med.nombre}</td>
                                <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm">{med.laboratorio}</td>
                                <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm">${med.precio}</td>
                                <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm">{med.stock}</td>
                                <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm">
                                    <button
                                        onClick={() => handleDelete(med.id)}
                                        className="text-red-600 hover:text-red-900 ml-2"
                                    >
                                        Eliminar
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
            {/* Formulario de creación pendiente de implementar */}
        </div>
    );
};

export default MedicineList;
