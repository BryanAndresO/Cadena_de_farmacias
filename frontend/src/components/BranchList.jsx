import React, { useEffect, useState } from 'react';
import { apiSucursal, apiInventario, apiCatalogo } from '../services/api';
import { useAuth } from '../context/AuthContext';
import BranchForm from './BranchForm';
import StockAssignmentForm from './StockAssignmentForm';
import { extractApiMessage } from '../utils/error';

const BranchList = () => {
    const { isAdmin } = useAuth();
    const [branches, setBranches] = useState([]);
    const [selectedBranch, setSelectedBranch] = useState(null);
    const [inventory, setInventory] = useState([]);
    const [loading, setLoading] = useState(true);

    // CRUD State
    const [showForm, setShowForm] = useState(false);
    const [editingBranch, setEditingBranch] = useState(null);

    // Stock Assignment State
    const [showStockForm, setShowStockForm] = useState(false);

    // Product details cache (full objects, not just names)
    const [productDetails, setProductDetails] = useState({});
    const [editingInventory, setEditingInventory] = useState(null);

    useEffect(() => {
        fetchBranches();
        fetchProducts();
    }, []);

    const fetchProducts = async () => {
        try {
            const response = await apiCatalogo.get('/medicamentos');
            const details = {};
            response.data.forEach(p => { details[p.id] = p; });
            setProductDetails(details);
        } catch (err) {
            console.error('Error loading products:', err);
        }
    };

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
            console.error('Error fetching inventory:', err);
            setInventory([]);
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

    const handleAssignStock = async (stockData) => {
        try {
            // API: assign new stock to a branch inventory
            await apiInventario.post('/', stockData);
            fetchInventory(stockData.sucursalID);
            setShowStockForm(false);
            setEditingInventory(null);
            alert('Stock asignado correctamente');
        } catch (err) {
            console.error('Detalle técnico:', err);
            const raw = err.response?.data?.message || err.message;
            const msg = extractApiMessage({ response: { data: { message: raw } } });
            alert('No se pudo asignar el stock: ' + msg);
        }
    };

    const handleEditInventory = (item) => {
        setEditingInventory(item);
        setShowStockForm(true);
    };

    const handleDeleteInventory = async (id) => {
        if (window.confirm('¿Eliminar este producto del inventario de la sucursal?')) {
            try {
                await apiInventario.delete(`/${id}`);
                fetchInventory(selectedBranch.id);
                alert('Producto eliminado del inventario');
            } catch (err) {
                console.error(err);
                alert('Error al eliminar producto');
            }
        }
    };

    const handleUpdateInventory = async (stockData) => {
        try {
            // When editing, use PATCH to ADD stock, not replace it
            await apiInventario.patch(`/${editingInventory.id}/adjust`, {
                adjustment: parseInt(stockData.stock) // This will be ADDED to current stock
            });

            // Update stock minimum separately if changed
            if (stockData.stockMinimo !== editingInventory.stockMinimo) {
                await apiInventario.put(`/${editingInventory.id}`, {
                    stockMinimo: parseInt(stockData.stockMinimo)
                });
            }

            fetchInventory(selectedBranch.id);
            setShowStockForm(false);
            setEditingInventory(null);
            alert('Stock agregado correctamente. Se han sumado las unidades al inventario actual.');
        } catch (err) {
            console.error('Detalle técnico:', err);
            const raw = err.response?.data?.message || err.message;
            const msg = extractApiMessage({ response: { data: { message: raw } } });
            alert('No se pudo actualizar el inventario: ' + msg);
        }
    };

    if (loading && !branches.length) return <div className="p-8 text-center">Cargando sistema de sucursales...</div>;

    return (
        <div className="slide-up">
            {/* Header */}
            <div className="flex justify-between items-center mb-6">
                <div>
                    <h2 className="text-2xl font-semibold text-neutral-800">Gestión de Sucursales</h2>
                    <p className="text-neutral-500 text-sm">Administra puntos de venta e inventarios</p>
                </div>
                <button onClick={handleCreate} className="btn-primary w-auto flex items-center gap-2">
                    <span>+</span> Nueva Sucursal
                </button>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                {/* List View */}
                <div className="lg:col-span-1 bg-white rounded border border-neutral-200 overflow-hidden">
                    <div className="p-3 bg-neutral-50 border-b border-neutral-200 font-medium text-neutral-700 text-sm">
                        Listado de Sucursales
                    </div>
                    <div className="divide-y divide-neutral-100 max-h-[600px] overflow-y-auto">
                        {branches.map(b => (
                            <div
                                key={b.id}
                                onClick={() => handleSelectBranch(b)}
                                className={`p-3 cursor-pointer transition-colors hover:bg-neutral-50 group ${selectedBranch?.id === b.id ? 'bg-neutral-50 border-l-2 border-neutral-600' : 'border-l-2 border-transparent'}`}
                            >
                                <div className="flex justify-between items-start">
                                    <div>
                                        <h4 className="font-medium text-neutral-800 text-sm group-hover:text-neutral-900">{b.nombre}</h4>
                                        <p className="text-xs text-neutral-500 flex items-center gap-1">📍 {b.ciudad}</p>
                                    </div>
                                    {isAdmin() && (
                                        <div className="flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                                            <button onClick={(e) => handleEdit(e, b)} className="p-1 text-neutral-500 hover:bg-neutral-100 rounded text-sm">✏️</button>
                                            <button onClick={(e) => handleDelete(e, b.id)} className="p-1 text-neutral-500 hover:bg-neutral-100 rounded text-sm">🗑️</button>
                                        </div>
                                    )}
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                {/* Detail/Inventory View */}
                <div className="lg:col-span-2 glass p-5">
                    {selectedBranch ? (
                        <>
                            <div className="flex justify-between items-center mb-5">
                                <h3 className="text-base font-semibold text-neutral-800 flex items-center gap-2">
                                    📦 Inventario: <span className="text-neutral-600">{selectedBranch.nombre}</span>
                                </h3>
                                {isAdmin() && (
                                    <button
                                        onClick={() => setShowStockForm(true)}
                                        className="btn-secondary text-sm py-1"
                                    >
                                        + Asignar Producto
                                    </button>
                                )}
                            </div>

                            <div className="overflow-x-auto rounded border border-neutral-200">
                                <table className="w-full text-left bg-white">
                                    <thead className="bg-neutral-50">
                                        <tr>
                                            <th className="p-3 font-medium text-neutral-600 text-xs uppercase">Producto</th>
                                            <th className="p-3 font-medium text-neutral-600 text-xs uppercase">Stock Disponible</th>
                                            <th className="p-3 font-medium text-neutral-600 text-xs uppercase">Stock Mínimo</th>
                                            <th className="p-3 font-medium text-neutral-600 text-xs uppercase">Estado</th>
                                            {isAdmin() && <th className="p-3 font-medium text-neutral-600 text-xs uppercase">Acciones</th>}
                                        </tr>
                                    </thead>
                                    <tbody className="divide-y divide-neutral-100">
                                        {inventory.length > 0 ? inventory.map(item => (
                                            <tr key={item.id} className="hover:bg-neutral-50">
                                                <td className="p-3 text-neutral-700 text-sm">
                                                    {(() => {
                                                        const product = productDetails[item.productoId];
                                                        if (!product) return `Producto #${item.productoId}`;
                                                        return (
                                                            <div>
                                                                <div className="font-medium">{product.nombre}</div>
                                                                <div className="text-xs text-neutral-500">
                                                                    {product.concentracion || ''} {product.presentacion ? `(${product.presentacion})` : ''}
                                                                </div>
                                                            </div>
                                                        );
                                                    })()}
                                                </td>
                                                <td className="p-3 font-mono font-medium text-neutral-800 text-sm">{item.cantidad}</td>
                                                <td className="p-3 text-neutral-600 text-sm">{item.stockMinimo || 0}</td>
                                                <td className="p-3">
                                                    <span className={`px-2 py-1 rounded text-xs font-medium ${item.cantidad < (item.stockMinimo || 10) ? 'bg-neutral-200 text-neutral-700' : 'bg-neutral-100 text-neutral-600'}`}>
                                                        {item.cantidad < (item.stockMinimo || 10) ? 'Bajo Stock' : 'Normal'}
                                                    </span>
                                                </td>
                                                {isAdmin() && (
                                                    <td className="p-3">
                                                        <button onClick={() => handleEditInventory(item)} className="text-neutral-500 hover:text-neutral-700 mr-2 text-sm" title="Editar">✏️</button>
                                                        <button onClick={() => handleDeleteInventory(item.id)} className="text-neutral-500 hover:text-neutral-700 text-sm" title="Eliminar">🗑️</button>
                                                    </td>
                                                )}
                                            </tr>
                                        )) : (
                                            <tr>
                                                <td colSpan="6" className="p-6 text-center text-neutral-400 text-sm">
                                                    No hay items registrados en este inventario.
                                                </td>
                                            </tr>
                                        )}
                                    </tbody>
                                </table>
                            </div>
                        </>
                    ) : (
                        <div className="h-full flex flex-col items-center justify-center text-neutral-400 p-10 text-center border border-dashed border-neutral-200 rounded">
                            <span className="text-3xl mb-3">🏪</span>
                            <p className="text-sm">Seleccione una sucursal del listado para ver su inventario</p>
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

            {showStockForm && selectedBranch && (
                <StockAssignmentForm
                    branchId={selectedBranch.id}
                    editingItem={editingInventory}
                    onSave={editingInventory ? handleUpdateInventory : handleAssignStock}
                    onCancel={() => {
                        setShowStockForm(false);
                        setEditingInventory(null);
                    }}
                />
            )}
        </div>
    );
};

export default BranchList;
