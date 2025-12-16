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
        <div className="container mx-auto p-4">
            <h2 className="text-2xl font-bold mb-6">Punto de Venta</h2>

            <div className="grid grid-cols-3 gap-6">
                {/* Left Column: Selection */}
                <div className="bg-white p-4 shadow rounded col-span-1">
                    <div className="mb-4">
                        <label className="block text-sm font-bold mb-2">Sucursal</label>
                        <select
                            className="w-full border p-2 rounded"
                            value={selectedBranch}
                            onChange={(e) => setSelectedBranch(e.target.value)}
                        >
                            <option value="">Seleccione Sucursal</option>
                            {branches.map(b => (
                                <option key={b.id} value={b.id}>{b.nombre}</option>
                            ))}
                        </select>
                    </div>

                    <div className="mb-4">
                        <label className="block text-sm font-bold mb-2">Cliente</label>
                        <select
                            className="w-full border p-2 rounded"
                            value={selectedClient}
                            onChange={(e) => setSelectedClient(e.target.value)}
                        >
                            <option value="">Seleccione Cliente</option>
                            {clients.map(c => (
                                <option key={c.id} value={c.id}>{c.nombre} - {c.cedula}</option>
                            ))}
                        </select>
                    </div>

                    <div className="border-t pt-4">
                        <h3 className="font-bold mb-2">Productos</h3>
                        <div className="h-64 overflow-y-auto">
                            {products.map(p => (
                                <div key={p.id} className="flex justify-between items-center p-2 border-b hover:bg-gray-50">
                                    <div>
                                        <div className="font-medium">{p.nombre}</div>
                                        <div className="text-xs text-gray-500">${p.precio}</div>
                                    </div>
                                    <button
                                        onClick={() => addToCart(p)}
                                        className="bg-blue-500 text-white px-2 py-1 rounded text-xs"
                                    >
                                        +
                                    </button>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>

                {/* Right Column: Cart */}
                <div className="bg-white p-4 shadow rounded col-span-2">
                    <h3 className="text-xl font-bold mb-4">Carrito de Compras</h3>
                    <table className="w-full mb-4">
                        <thead>
                            <tr className="border-b">
                                <th className="text-left pb-2">Producto</th>
                                <th className="text-center pb-2">Cant</th>
                                <th className="text-right pb-2">Precio</th>
                                <th className="text-right pb-2">Subtotal</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            {cart.map(item => (
                                <tr key={item.id} className="border-b">
                                    <td className="py-2">{item.nombre}</td>
                                    <td className="text-center">{item.quantity}</td>
                                    <td className="text-right">${item.precio}</td>
                                    <td className="text-right">${(item.precio * item.quantity).toFixed(2)}</td>
                                    <td className="text-right">
                                        <button
                                            onClick={() => removeFromCart(item.id)}
                                            className="text-red-500 hover:text-red-700"
                                        >
                                            x
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>

                    <div className="flex justify-end items-center text-2xl font-bold mb-6">
                        Total: ${calculateTotal().toFixed(2)}
                    </div>

                    <button
                        onClick={handleCheckout}
                        disabled={cart.length === 0}
                        className="w-full bg-green-600 text-white py-3 rounded text-lg font-bold hover:bg-green-700 disabled:bg-gray-400"
                    >
                        Confirmar Venta
                    </button>
                </div>
            </div>
        </div>
    );
};

export default SalesPOS;
