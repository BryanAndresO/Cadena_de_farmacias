import React, { useEffect, useState } from 'react';
import { apiSucursal, apiInventario } from '../services/api';
import BranchForm from './BranchForm';

const BranchList = () => {
    const [branches, setBranches] = useState([]);
    const [selectedBranch, setSelectedBranch] = useState(null);
    const [inventory, setInventory] = useState([]);
    const [loading, setLoading] = useState(true);

    // CRUD State
    const [showForm, setShowForm] = useState(false);
    const [editingBranch, setEditingBranch] = useState(null);

    useEffect(() => {
        fetchBranches();
    }, []);

    const fetchBranches = async () => {
        try {
            const response = await apiSucursal.get('/sucursales/');
            setBranches(response.data);
            setLoading(false);
        } catch (err) {
            console.error(err);
            setLoading(false);
        }
    };

    const fetchInventory = async (branchId) => {
        setLoading(true);
        try {
            const response = await apiInventario.get(`/sucursal/${branchId}`);
            setInventory(response.data);
            setLoading(false);
        } catch (err) {
            console.error(err);
            setInventory([]); // Clear on error
            setLoading(false);
        }
    };

    const handleSelectBranch = (branch) => {
        setSelectedBranch(branch);
        fetchInventory(branch.id);
    };

    const handleCreate = () => {
        setEditingBranch(null);
        setShowForm(true);
    };

    const handleEdit = (e, branch) => {
        e.stopPropagation(); // Prevent row selection
        setEditingBranch(branch);
        setShowForm(true);
    };

    const handleDelete = async (e, id) => {
        e.stopPropagation();
        if (window.confirm('¿Eliminar esta sucursal? Esta acción puede afectar el inventario.')) {
            try {
                await apiSucursal.delete(`/sucursales/${id}`);
                fetchBranches();
                if (selectedBranch?.id === id) setSelectedBranch(null);
            } catch (err) {
                alert('Error al eliminar sucursal');
            }
        }
    };

    const handleSave = async (branchData) => {
        try {
            if (editingBranch) {
                await apiSucursal.put(`/sucursales/${editingBranch.id}`, branchData);
            } else {
                await apiSucursal.post('/sucursales/', branchData);
            }
            setShowForm(false);
            fetchBranches();
        } catch (err) {
            alert('Error al guardar sucursal');
        }
    };

    if (loading && !branches.length) return <div className="p-8 text-center">Cargando sistema de sucursales...</div>;

    return (
        <div className="slide-up">
            {/* Header */}
            <div className="flex justify-between items-center mb-8">
                <div>
                    <h2 className="text-3xl font-bold text-gray-800">Gestión de Sucursales</h2>
                    <p className="text-gray-500">Administra puntos de venta e inventarios</p>
                </div>
                <button onClick={handleCreate} className="btn-primary w-auto flex items-center gap-2">
                    <span>+</span> Nueva Sucursal
                </button>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                {/* List View */}
                <div className="lg:col-span-1 bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
                    <div className="p-4 bg-gray-50 border-b border-gray-100 font-semibold text-gray-700">
                        Listado de Sucursales
                    </div>
                    <div className="divide-y divide-gray-100 max-h-[600px] overflow-y-auto">
                        {branches.map(b => (
                            <div
                                key={b.id}
                                onClick={() => handleSelectBranch(b)}
                                className={`p-4 cursor-pointer transition-all hover:bg-indigo-50 group ${selectedBranch?.id === b.id ? 'bg-indigo-50 border-l-4 border-indigo-600' : 'border-l-4 border-transparent'}`}
                            >
                                <div className="flex justify-between items-start">
                                    <div>
                                        <h4 className="font-bold text-gray-800 group-hover:text-indigo-700">{b.nombre}</h4>
                                        <p className="text-sm text-gray-500 flex items-center gap-1">📍 {b.ciudad}</p>
                                    </div>
                                    <div className="flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                                        <button onClick={(e) => handleEdit(e, b)} className="p-1 text-blue-600 hover:bg-blue-100 rounded">✏️</button>
                                        <button onClick={(e) => handleDelete(e, b.id)} className="p-1 text-red-600 hover:bg-red-100 rounded">🗑️</button>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                {/* Detail/Inventory View */}
                <div className="lg:col-span-2 glass p-6">
                    {selectedBranch ? (
                        <>
                            <div className="flex justify-between items-center mb-6">
                                <h3 className="text-xl font-bold text-gray-800 flex items-center gap-2">
                                    📦 Inventario: <span className="text-indigo-600">{selectedBranch.nombre}</span>
                                </h3>
                                {/* Future: Add 'Adjust Stock' button here */}
                            </div>

                            <div className="overflow-x-auto rounded-lg border border-gray-200">
                                <table className="w-full text-left bg-white">
                                    <thead className="bg-gray-50">
                                        <tr>
                                            <th className="p-3 font-semibold text-gray-600 text-sm">Producto ID</th>
                                            <th className="p-3 font-semibold text-gray-600 text-sm">Stock Disponible</th>
                                            <th className="p-3 font-semibold text-gray-600 text-sm">Estado</th>
                                        </tr>
                                    </thead>
                                    <tbody className="divide-y divide-gray-100">
                                        {inventory.length > 0 ? inventory.map(item => (
                                            <tr key={item.id} className="hover:bg-gray-50">
                                                <td className="p-3 text-gray-700">#{item.productoId}</td>
                                                <td className="p-3 font-mono font-bold text-indigo-600">{item.cantidad}</td>
                                                <td className="p-3">
                                                    <span className={`px-2 py-1 rounded-full text-xs font-medium ${item.cantidad < 10 ? 'bg-red-100 text-red-700' : 'bg-green-100 text-green-700'}`}>
                                                        {item.cantidad < 10 ? '🔴 Bajo Stock' : '🟢 Normal'}
                                                    </span>
                                                </td>
                                            </tr>
                                        )) : (
                                            <tr>
                                                <td colSpan="3" className="p-8 text-center text-gray-400 italic">
                                                    No hay items registrados en este inventario.
                                                </td>
                                            </tr>
                                        )}
                                    </tbody>
                                </table>
                            </div>
                        </>
                    ) : (
                        <div className="h-full flex flex-col items-center justify-center text-gray-400 p-12 text-center border-2 border-dashed border-gray-200 rounded-xl">
                            <span className="text-4xl mb-4">🏪</span>
                            <p className="text-lg">Seleccione una sucursal del listado para ver su inventario</p>
                        </div>
                    )}
                </div>
            </div>

            {showForm && (
                <BranchForm
                    branch={editingBranch}
                    onSave={handleSave}
                    onCancel={() => setShowForm(false)}
                />
            )}
        </div>
    );
};

export default BranchList;
