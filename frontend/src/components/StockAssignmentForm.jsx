import React, { useState, useEffect } from 'react';
import { apiCatalogo } from '../services/api';

const StockAssignmentForm = ({ branchId, onSave, onCancel, editingItem }) => {
    const [products, setProducts] = useState([]);
    const [form, setForm] = useState({
        productoId: '',
        stock: 0,
        stockMinimo: 5
    });
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchProducts = async () => {
            try {
                const res = await apiCatalogo.get('/medicamentos');
                setProducts(res.data);
            } catch (err) {
                console.error(err);
                setError("Error al cargar catálogo de productos");
            }
        };
        fetchProducts();
    }, []);

    // Pre-fill form when editing
    useEffect(() => {
        if (editingItem) {
            setForm({
                productoId: editingItem.productoId,
                stock: editingItem.cantidad,
                stockMinimo: editingItem.stockMinimo || 5
            });
        }
    }, [editingItem]);

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        setError(null);

        if (!editingItem && !form.productoId) {
            setError("Seleccione un producto");
            return;
        }
        if (form.stock <= 0) {
            setError("La cantidad debe ser mayor a 0");
            return;
        }

        // Only validate against catalog when creating new assignment
        if (!editingItem) {
            const selectedProduct = products.find(p => p.id.toString() === form.productoId.toString());
            if (selectedProduct && selectedProduct.stock < form.stock) {
                setError(`Stock insuficiente en catálogo global. Disponible: ${selectedProduct.stock}`);
                return;
            }
        }

        // Send numeric IDs as usually required
        onSave({
            sucursalID: branchId,
            productoID: form.productoId,
            stock: parseInt(form.stock),
            stockMinimo: parseInt(form.stockMinimo)
        });
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
            <div className="bg-white p-6 rounded-lg shadow-xl w-96 slide-up">
                <h3 className="text-xl font-bold mb-4 text-gray-800">
                    {editingItem ? 'Editar Inventario' : 'Asignar Inventario a Sucursal'}
                </h3>

                {error && (
                    <div className="bg-red-50 text-red-600 p-3 rounded mb-4 text-sm">
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-gray-700 text-sm font-bold mb-2">Producto</label>
                        <select
                            name="productoId"
                            value={form.productoId}
                            onChange={handleChange}
                            className="input-field"
                            required
                            disabled={!!editingItem}
                        >
                            <option value="">-- Seleccionar --</option>
                            {products.map(p => (
                                <option key={p.id} value={p.id}>
                                    {p.nombre} (Disponibles: {p.stock})
                                </option>
                            ))}
                        </select>
                        {editingItem && (
                            <p className="text-xs text-gray-500 mt-1">No se puede cambiar el producto al editar</p>
                        )}
                    </div>

                    <div>
                        <label className="block text-gray-700 text-sm font-bold mb-2">Cantidad a Asignar</label>
                        <input
                            type="number"
                            name="stock"
                            value={form.stock}
                            onChange={handleChange}
                            className="input-field"
                            min="1"
                            required
                        />
                    </div>

                    <div>
                        <label className="block text-gray-700 text-sm font-bold mb-2">Alerta de Stock Mínimo</label>
                        <input
                            type="number"
                            name="stockMinimo"
                            value={form.stockMinimo}
                            onChange={handleChange}
                            className="input-field"
                            min="1"
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
                            {editingItem ? 'Actualizar' : 'Asignar Stock'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default StockAssignmentForm;
