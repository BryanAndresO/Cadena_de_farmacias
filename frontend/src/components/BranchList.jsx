import React, { useEffect, useState } from 'react';
import { apiSucursal, apiInventario } from '../services/api';

const BranchList = () => {
    const [branches, setBranches] = useState([]);
    const [selectedBranch, setSelectedBranch] = useState(null);
    const [inventory, setInventory] = useState([]);
    const [loading, setLoading] = useState(true);

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
            setLoading(false);
        }
    };

    const handleSelectBranch = (branch) => {
        setSelectedBranch(branch);
        fetchInventory(branch.id);
    };

    if (loading && !selectedBranch) return <div className="p-4">Cargando sucursales...</div>;

    return (
        <div className="container mx-auto p-4 flex gap-6">
            <div className="w-1/3 bg-white shadow rounded-lg p-4">
                <h3 className="text-xl font-bold mb-4">Sucursales</h3>
                <ul>
                    {branches.map(b => (
                        <li
                            key={b.id}
                            onClick={() => handleSelectBranch(b)}
                            className={`p-3 border-b cursor-pointer hover:bg-gray-50 ${selectedBranch?.id === b.id ? 'bg-blue-50 border-l-4 border-blue-500' : ''}`}
                        >
                            <div className="font-semibold">{b.nombre}</div>
                            <div className="text-sm text-gray-600">{b.ciudad}</div>
                        </li>
                    ))}
                </ul>
            </div>

            <div className="w-2/3 bg-white shadow rounded-lg p-4">
                <h3 className="text-xl font-bold mb-4">
                    {selectedBranch ? `Inventario: ${selectedBranch.nombre}` : 'Seleccione una sucursal'}
                </h3>

                {selectedBranch && (
                    <table className="min-w-full">
                        <thead>
                            <tr>
                                <th className="text-left py-2">Producto ID</th>
                                <th className="text-left py-2">Stock</th>
                                <th className="text-left py-2">Estado</th>
                            </tr>
                        </thead>
                        <tbody>
                            {inventory.length > 0 ? inventory.map(item => (
                                <tr key={item.id} className="border-t">
                                    <td className="py-2">{item.productoId}</td>
                                    <td className="py-2">{item.cantidad}</td>
                                    <td className="py-2">
                                        <span className={`px-2 py-1 rounded text-xs ${item.cantidad < 10 ? 'bg-red-100 text-red-800' : 'bg-green-100 text-green-800'}`}>
                                            {item.cantidad < 10 ? 'Bajo Stock' : 'OK'}
                                        </span>
                                    </td>
                                </tr>
                            )) : (
                                <tr>
                                    <td colSpan="3" className="py-4 text-center text-gray-500">No hay inventario registrado.</td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    );
};

export default BranchList;
