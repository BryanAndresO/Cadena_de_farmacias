import React, { useState, useEffect } from 'react';
import { apiCatalogo } from '../services/api';

const StockAssignmentForm = ({ branchId, onSave, onCancel, editingItem }) => {
    const [products, setProducts] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [isDropdownOpen, setIsDropdownOpen] = useState(false);
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
                stock: 0, // Reset to 0 for adding more stock
                stockMinimo: editingItem.stockMinimo || 5
            });
        }
    }, [editingItem]);

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleSearchChange = (e) => {
        setSearchTerm(e.target.value);
        setIsDropdownOpen(true);
        setForm({ ...form, productoId: '' }); // Clear selection if typing
    };

    const handleSelectProduct = (product) => {
        setForm({ ...form, productoId: product.id });
        setSearchTerm(product.nombre);
        setIsDropdownOpen(false);
    };

    const filteredProducts = products.filter(p =>
        p.nombre.toLowerCase().includes(searchTerm.toLowerCase())
    );

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
        // REMOVED: Stock validation against catalog is no longer valid as Catalog has no stock
        /*
        if (!editingItem) {
            const selectedProduct = products.find(p => p.id.toString() === form.productoId.toString());
            if (selectedProduct && selectedProduct.stock < form.stock) {
                setError(`Stock insuficiente en catálogo global. Disponible: ${selectedProduct.stock}`);
                return;
            }
        }
        */

        // Send numeric IDs as usually required
        onSave({
            sucursalID: branchId,
            productoID: form.productoId,
            stock: parseInt(form.stock),
            stockMinimo: parseInt(form.stockMinimo)
        });
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-40 flex justify-center items-center z-50">
            <div className="bg-white p-6 rounded shadow-lg w-96 slide-up border border-neutral-200">
                <h3 className="text-lg font-semibold mb-4 text-neutral-800">
                    {editingItem ? 'Editar Inventario' : 'Asignar Inventario a Sucursal'}
                </h3>

                {error && (
                    <div className="bg-neutral-100 text-neutral-700 p-3 rounded mb-4 text-sm border border-neutral-200">
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div className="relative">
                        <label className="block text-neutral-700 text-sm font-medium mb-2">Producto {editingItem && '(No editable)'}</label>
                        {!editingItem ? (
                            <>
                                <input
                                    type="text"
                                    value={searchTerm}
                                    onChange={handleSearchChange}
                                    onFocus={() => setIsDropdownOpen(true)}
                                    placeholder="Buscar producto..."
                                    className="input-field w-full"
                                />
                                {isDropdownOpen && filteredProducts.length > 0 && (
                                    <div className="absolute z-10 w-full bg-white border border-neutral-200 mt-1 max-h-48 overflow-y-auto shadow-lg rounded-md">
                                        {filteredProducts.map(p => (
                                            <div
                                                key={p.id}
                                                className="px-4 py-2 hover:bg-neutral-50 cursor-pointer text-sm"
                                                onClick={() => handleSelectProduct(p)}
                                            >
                                                <div className="font-medium">{p.nombre}</div>
                                                <div className="text-xs text-neutral-500">
                                                    {p.concentracion || ''} {p.presentacion ? `(${p.presentacion})` : ''}
                                                </div>
                                            </div>
                                        ))}
                                    </div>
                                )}
                                {form.productoId && (() => {
                                    const selected = products.find(p => p.id === form.productoId);
                                    if (!selected) return null;

                                    // Check if product has details (for backwards compatibility)
                                    const hasDetails = selected.concentracion || selected.presentacion;

                                    return (
                                        <div className="mt-2 p-3 bg-teal-50 border border-teal-200 rounded-md">
                                            <div className="text-xs font-semibold text-teal-800 mb-1">✓ Producto Seleccionado:</div>
                                            <div className="space-y-1">
                                                <div className="text-sm font-bold text-teal-900">{selected.nombre}</div>
                                                {selected.concentracion && (
                                                    <div className="text-xs text-teal-700"><span className="font-medium">Concentración:</span> {selected.concentracion}</div>
                                                )}
                                                {selected.presentacion && (
                                                    <div className="text-xs text-teal-700"><span className="font-medium">Presentación:</span> {selected.presentacion}</div>
                                                )}
                                                {!hasDetails && (
                                                    <div className="text-xs text-teal-600 italic">Producto sin detalles adicionales</div>
                                                )}
                                            </div>
                                        </div>
                                    );
                                })()}
                            </>
                        ) : (
                            <input
                                type="text"
                                value={products.find(p => p.id === form.productoId)?.nombre || 'Cargando...'}
                                disabled
                                className="input-field bg-neutral-100 text-neutral-500"
                            />
                        )}
                    </div>

                    <div>
                        <label className="block text-neutral-700 text-sm font-medium mb-2">
                            {editingItem ? 'Cantidad a Agregar al Stock Actual' : 'Cantidad a Asignar'}
                            {form.productoId && (() => {
                                const selected = products.find(p => p.id === form.productoId);
                                return selected?.presentacion ? (
                                    <span className="text-xs font-normal text-teal-600 ml-1">
                                        (en unidades de {selected.presentacion})
                                    </span>
                                ) : null;
                            })()}
                        </label>
                        <input
                            type="number"
                            name="stock"
                            value={form.stock}
                            onChange={handleChange}
                            className="input-field"
                            min="1"
                            required
                        />
                        {editingItem && (
                            <p className="text-xs text-teal-600 mt-1 font-medium">
                                Stock actual: {editingItem.cantidad} → Stock nuevo: {editingItem.cantidad + parseInt(form.stock || 0)}
                            </p>
                        )}
                    </div>

                    <div>
                        <label className="block text-neutral-700 text-sm font-medium mb-2">Alerta de Stock Mínimo</label>
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
