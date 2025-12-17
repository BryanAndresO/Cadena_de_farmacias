import React, { useState, useEffect } from 'react';
import { apiCatalogo, apiCliente, apiSucursal, apiVentas } from '../services/api';

const SalesPOS = () => {
    const [step, setStep] = useState(1);
    const [clients, setClients] = useState([]);
    const [branches, setBranches] = useState([]);
    const [products, setProducts] = useState([]);

    const [selectedClient, setSelectedClient] = useState('');
    const [selectedBranch, setSelectedBranch] = useState('');
    const [cart, setCart] = useState([]);

    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {
        const [cRes, bRes, pRes] = await Promise.all([
            apiCliente.get('/clientes/'),
            apiSucursal.get('/sucursales/'),
            apiCatalogo.get('/medicamentos/')
        ]);
        setClients(cRes.data);
        setBranches(bRes.data);
        setProducts(pRes.data);
    };

    const addToCart = (product) => {
        const existing = cart.find(item => item.id === product.id);
        if (existing) {
            setCart(cart.map(item => item.id === product.id ? { ...item, quantity: item.quantity + 1 } : item));
        } else {
            setCart([...cart, { ...product, quantity: 1 }]);
        }
    };

    const removeFromCart = (id) => {
        setCart(cart.filter(item => item.id !== id));
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
            }

            alert(`Venta registrada con éxito! ID: ${saleId}`);
            setCart([]);
            setStep(1);
        } catch (err) {
            console.error(err);
            alert('Error al procesar la venta');
        }
    };

    return (
        <div className="slide-up ">
            <h2 className="text-3xl font-bold mb-2 text-gray-800">Punto de Venta</h2>
            <p className="text-gray-500 mb-8">Facturación y registro de ventas</p>

            <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
                {/* Left Column: Selection & Product Catalog (8 cols) */}
                <div className="lg:col-span-8 space-y-6">

                    {/* Header Controls */}
                    <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 flex flex-col md:flex-row gap-4">
                        <div className="flex-1">
                            <label className="block text-sm font-bold mb-2 text-gray-700">Sucursal</label>
                            <select
                                className="input-field"
                                value={selectedBranch}
                                onChange={(e) => setSelectedBranch(e.target.value)}
                            >
                                <option value="">-- Seleccione Sucursal --</option>
                                {branches.map(b => (
                                    <option key={b.id} value={b.id}>{b.nombre}</option>
                                ))}
                            </select>
                        </div>
                        <div className="flex-1">
                            <label className="block text-sm font-bold mb-2 text-gray-700">Cliente</label>
                            <select
                                className="input-field"
                                value={selectedClient}
                                onChange={(e) => setSelectedClient(e.target.value)}
                            >
                                <option value="">-- Seleccione Cliente --</option>
                                {clients.map(c => (
                                    <option key={c.id} value={c.id}>{c.nombre} - {c.cedula || 'N/A'}</option>
                                ))}
                            </select>
                        </div>
                    </div>

                    {/* Product Grid */}
                    <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
                        <h3 className="text-lg font-bold mb-4 text-gray-800">Catálogo de Productos</h3>
                        <div className="grid grid-cols-2 md:grid-cols-3 xl:grid-cols-4 gap-4 max-h-[500px] overflow-y-auto p-1">
                            {products.map(p => (
                                <div key={p.id} className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow cursor-pointer bg-gray-50 hover:bg-white flex flex-col justify-between" onClick={() => addToCart(p)}>
                                    <div>
                                        <div className="font-bold text-gray-800">{p.nombre}</div>
                                        <div className="text-xs text-gray-500 mb-2">{p.laboratorio}</div>
                                    </div>
                                    <div className="flex justify-between items-center mt-2">
                                        <span className="font-bold text-indigo-600">${p.precio}</span>
                                        <button className="bg-indigo-600 text-white w-8 h-8 rounded-full flex items-center justify-center hover:bg-indigo-700">+</button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
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
                                        <div className="text-xs text-gray-500">${item.precio} x {item.quantity}</div>
                                    </div>
                                    <div className="font-bold text-gray-800 mr-3">
                                        ${(item.precio * item.quantity).toFixed(2)}
                                    </div>
                                    <button
                                        onClick={() => removeFromCart(item.id)}
                                        className="text-red-400 hover:text-red-600 p-1"
                                    >
                                        ✕
                                    </button>
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
