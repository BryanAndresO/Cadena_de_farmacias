import React, { useState, useEffect } from 'react';
import { apiCatalogo, apiCliente, apiSucursal, apiVentas, apiInventario, apiReporte } from '../services/api';

const SalesPOS = () => {
    const [step, setStep] = useState(1);
    const [clients, setClients] = useState([]);
    const [branches, setBranches] = useState([]);
    const [products, setProducts] = useState([]);
    const [branchInventory, setBranchInventory] = useState([]);

    const [selectedClient, setSelectedClient] = useState('');
    const [selectedBranch, setSelectedBranch] = useState('');
    const [cart, setCart] = useState([]);

    useEffect(() => {
        loadInitialData();
    }, []);

    const loadInitialData = async () => {
        try {
            const [cRes, bRes] = await Promise.all([
                apiCliente.get('/clientes/'),
                apiSucursal.get('/sucursales/')
            ]);
            setClients(cRes.data);
            setBranches(bRes.data);
        } catch (err) {
            console.error('Error loading initial data:', err);
        }
    };

    const handleBranchSelect = async (branchId) => {
        setSelectedBranch(branchId);
        setCart([]);

        if (!branchId) {
            setProducts([]);
            setBranchInventory([]);
            return;
        }

        try {
            // Load inventory for selected branch
            const invRes = await apiInventario.get(`/sucursal/${branchId}`);
            setBranchInventory(invRes.data);

            // Load full product details from catalog
            const catalogRes = await apiCatalogo.get('/medicamentos');
            const allProducts = catalogRes.data;

            // Filter products to only show those in branch inventory
            const availableProducts = invRes.data
                .map(invItem => {
                    const product = allProducts.find(p => p.id.toString() === invItem.productoId.toString());
                    if (product) {
                        return {
                            ...product,
                            branchStock: invItem.cantidad,
                            inventoryId: invItem.id
                        };
                    }
                    return null;
                })
                .filter(p => p !== null && p.branchStock > 0);

            setProducts(availableProducts);
        } catch (err) {
            console.error('Error loading branch inventory:', err);
            alert('Error al cargar inventario de la sucursal');
        }
    };

    const addToCart = (product) => {
        const existing = cart.find(item => item.id === product.id);
        const currentQty = existing ? existing.quantity : 0;

        if (currentQty + 1 > product.branchStock) {
            alert(`Stock insuficiente. Disponible en sucursal: ${product.branchStock}`);
            return;
        }

        if (existing) {
            setCart(cart.map(item =>
                item.id === product.id
                    ? { ...item, quantity: item.quantity + 1 }
                    : item
            ));
        } else {
            setCart([...cart, { ...product, quantity: 1 }]);
        }
    };

    const removeFromCart = (id) => {
        setCart(cart.filter(item => item.id !== id));
    };

    const updateQuantity = (id, newQty) => {
        const product = products.find(p => p.id === id);
        if (newQty > product.branchStock) {
            alert(`Stock insuficiente. Disponible: ${product.branchStock}`);
            return;
        }
        if (newQty <= 0) {
            removeFromCart(id);
            return;
        }
        setCart(cart.map(item =>
            item.id === id ? { ...item, quantity: newQty } : item
        ));
    };

    const calculateTotal = () => {
        return cart.reduce((sum, item) => sum + (item.precio * item.quantity), 0);
    };

    const handleCheckout = async () => {
        if (!selectedClient || !selectedBranch || cart.length === 0) {
            alert('Complete todos los campos');
            return;
        }

        const saleData = {
            fecha: new Date().toISOString().split('T')[0],
            total: calculateTotal(),
            clienteId: parseInt(selectedClient),
            sucursalId: parseInt(selectedBranch)
        };

        try {
            // 1. Create Sale
            const saleRes = await apiVentas.post('/ventas/', saleData);
            const saleId = saleRes.data.id;

            // 2. Create Details
            for (const item of cart) {
                await apiVentas.post('/detalle-ventas/', {
                    ventaId: saleId,
                    medicamentoId: item.id,
                    cantidad: item.quantity,
                    precioUnitario: item.precio
                });

                // 3. Deduct from branch inventory
                await apiInventario.patch(`/${item.inventoryId}/adjust`, {
                    adjustment: -item.quantity
                });
            }

            // 4. Create Report Entry
            try {
                await apiReporte.post('/reporte-ventas/', {
                    fecha: saleData.fecha,
                    totalVentas: saleData.total,
                    sucursalId: saleData.sucursalId
                });
            } catch (reportErr) {
                console.error('Error creating report:', reportErr);
                // Don't fail the sale if report creation fails
            }

            alert(`¡Venta registrada con éxito! ID: ${saleId}`);
            setCart([]);
            setSelectedClient('');
            setSelectedBranch('');
            setProducts([]);
            setStep(1);
        } catch (err) {
            console.error(err);
            alert('Error al procesar la venta: ' + (err.response?.data?.message || err.message));
        }
    };

    return (
        <div className="slide-up">
            <h2 className="text-3xl font-bold mb-2 text-gray-800">Punto de Venta</h2>
            <p className="text-gray-500 mb-8">Facturación y registro de ventas</p>

            <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
                {/* Left Column: Selection & Product Catalog (8 cols) */}
                <div className="lg:col-span-8 space-y-6">

                    {/* Step Indicator */}
                    <div className="bg-white p-4 rounded-xl shadow-sm border border-gray-100">
                        <div className="flex items-center justify-between">
                            <div className={`flex items-center ${selectedBranch ? 'text-green-600' : 'text-indigo-600'}`}>
                                <div className={`w-8 h-8 rounded-full flex items-center justify-center font-bold ${selectedBranch ? 'bg-green-100' : 'bg-indigo-100'}`}>
                                    {selectedBranch ? '✓' : '1'}
                                </div>
                                <span className="ml-2 font-semibold">Seleccionar Sucursal</span>
                            </div>
                            <div className="h-1 flex-1 mx-4 bg-gray-200 rounded"></div>
                            <div className={`flex items-center ${selectedClient ? 'text-green-600' : selectedBranch ? 'text-indigo-600' : 'text-gray-400'}`}>
                                <div className={`w-8 h-8 rounded-full flex items-center justify-center font-bold ${selectedClient ? 'bg-green-100' : selectedBranch ? 'bg-indigo-100' : 'bg-gray-100'}`}>
                                    {selectedClient ? '✓' : '2'}
                                </div>
                                <span className="ml-2 font-semibold">Seleccionar Cliente</span>
                            </div>
                            <div className="h-1 flex-1 mx-4 bg-gray-200 rounded"></div>
                            <div className={`flex items-center ${cart.length > 0 ? 'text-green-600' : selectedClient ? 'text-indigo-600' : 'text-gray-400'}`}>
                                <div className={`w-8 h-8 rounded-full flex items-center justify-center font-bold ${cart.length > 0 ? 'bg-green-100' : selectedClient ? 'bg-indigo-100' : 'bg-gray-100'}`}>
                                    {cart.length > 0 ? '✓' : '3'}
                                </div>
                                <span className="ml-2 font-semibold">Agregar Productos</span>
                            </div>
                        </div>
                    </div>

                    {/* Header Controls */}
                    <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 flex flex-col md:flex-row gap-4">
                        <div className="flex-1">
                            <label className="block text-sm font-bold mb-2 text-gray-700">1. Sucursal *</label>
                            <select
                                className="input-field"
                                value={selectedBranch}
                                onChange={(e) => handleBranchSelect(e.target.value)}
                            >
                                <option value="">-- Seleccione Sucursal --</option>
                                {branches.map(b => (
                                    <option key={b.id} value={b.id}>{b.nombre} - {b.ciudad}</option>
                                ))}
                            </select>
                        </div>
                        <div className="flex-1">
                            <label className="block text-sm font-bold mb-2 text-gray-700">2. Cliente *</label>
                            <select
                                className="input-field"
                                value={selectedClient}
                                onChange={(e) => setSelectedClient(e.target.value)}
                                disabled={!selectedBranch}
                            >
                                <option value="">-- Seleccione Cliente --</option>
                                {clients.map(c => (
                                    <option key={c.id} value={c.id}>{c.nombre} - {c.cedula || 'N/A'}</option>
                                ))}
                            </select>
                        </div>
                    </div>

                    {/* Product Grid */}
                    {selectedBranch && selectedClient ? (
                        <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
                            <h3 className="text-lg font-bold mb-4 text-gray-800">
                                3. Productos Disponibles en {branches.find(b => b.id.toString() === selectedBranch)?.nombre}
                            </h3>
                            {products.length > 0 ? (
                                <div className="grid grid-cols-2 md:grid-cols-3 xl:grid-cols-4 gap-4 max-h-[500px] overflow-y-auto p-1">
                                    {products.map(p => (
                                        <div
                                            key={p.id}
                                            className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow cursor-pointer bg-gray-50 hover:bg-white flex flex-col justify-between"
                                            onClick={() => addToCart(p)}
                                        >
                                            <div>
                                                <div className="font-bold text-gray-800">{p.nombre}</div>
                                                <div className="text-xs text-gray-500 mb-2">{p.laboratorio}</div>
                                                <div className="text-xs text-indigo-600 font-semibold">Stock: {p.branchStock}</div>
                                            </div>
                                            <div className="flex justify-between items-center mt-2">
                                                <span className="font-bold text-indigo-600">${p.precio}</span>
                                                <button className="bg-indigo-600 text-white w-8 h-8 rounded-full flex items-center justify-center hover:bg-indigo-700">+</button>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            ) : (
                                <div className="text-center py-12 text-gray-400 border-2 border-dashed border-gray-200 rounded-lg">
                                    <p className="text-lg">No hay productos disponibles en esta sucursal</p>
                                    <p className="text-sm mt-2">Asigne productos al inventario de la sucursal primero</p>
                                </div>
                            )}
                        </div>
                    ) : (
                        <div className="bg-white p-12 rounded-xl shadow-sm border-2 border-dashed border-gray-200 text-center">
                            <div className="text-6xl mb-4">🏪</div>
                            <p className="text-gray-500 text-lg">
                                {!selectedBranch ? 'Seleccione una sucursal para comenzar' : 'Seleccione un cliente para continuar'}
                            </p>
                        </div>
                    )}
                </div>

                {/* Right Column: Cart (4 cols) */}
                <div className="lg:col-span-4">
                    <div className="bg-white p-6 rounded-xl shadow-lg border border-gray-100 sticky top-4">
                        <h3 className="text-xl font-bold mb-6 flex items-center gap-2">
                            🛒 Carrito de Compras
                        </h3>

                        <div className="space-y-4 mb-6 max-h-[400px] overflow-y-auto">
                            {cart.length === 0 ? (
                                <div className="text-center py-8 text-gray-400 border-2 border-dashed border-gray-200 rounded-lg">
                                    El carrito está vacío
                                </div>
                            ) : cart.map(item => (
                                <div key={item.id} className="flex justify-between items-center p-3 bg-gray-50 rounded-lg">
                                    <div className="flex-1">
                                        <div className="font-medium text-sm text-gray-800">{item.nombre}</div>
                                        <div className="text-xs text-gray-500">${item.precio} c/u</div>
                                        <div className="flex items-center gap-2 mt-1">
                                            <button
                                                onClick={() => updateQuantity(item.id, item.quantity - 1)}
                                                className="w-6 h-6 bg-gray-200 hover:bg-gray-300 rounded text-sm"
                                            >-</button>
                                            <span className="text-sm font-bold">{item.quantity}</span>
                                            <button
                                                onClick={() => updateQuantity(item.id, item.quantity + 1)}
                                                className="w-6 h-6 bg-gray-200 hover:bg-gray-300 rounded text-sm"
                                            >+</button>
                                        </div>
                                    </div>
                                    <div className="text-right">
                                        <div className="font-bold text-gray-800">
                                            ${(item.precio * item.quantity).toFixed(2)}
                                        </div>
                                        <button
                                            onClick={() => removeFromCart(item.id)}
                                            className="text-red-400 hover:text-red-600 text-xs mt-1"
                                        >
                                            Eliminar
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>

                        {/* Summary */}
                        <div className="border-t border-gray-200 pt-4 space-y-2 mb-6">
                            <div className="flex justify-between text-gray-600">
                                <span>Subtotal</span>
                                <span>${calculateTotal().toFixed(2)}</span>
                            </div>
                            <div className="flex justify-between text-gray-600">
                                <span>Impuestos (0%)</span>
                                <span>$0.00</span>
                            </div>
                            <div className="flex justify-between text-2xl font-bold text-indigo-700 pt-2">
                                <span>Total</span>
                                <span>${calculateTotal().toFixed(2)}</span>
                            </div>
                        </div>

                        <button
                            onClick={handleCheckout}
                            disabled={cart.length === 0 || !selectedClient || !selectedBranch}
                            className="w-full bg-gradient-to-r from-green-500 to-emerald-600 text-white py-4 rounded-xl text-lg font-bold hover:shadow-lg hover:opacity-95 disabled:opacity-50 disabled:cursor-not-allowed transition-all"
                        >
                            Confirmar Venta
                        </button>

                        {(!selectedClient || !selectedBranch) && (
                            <p className="text-xs text-center text-red-500 mt-2">
                                * Debe seleccionar Sucursal y Cliente
                            </p>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default SalesPOS;
