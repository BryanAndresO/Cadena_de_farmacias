import React, { useState, useEffect } from 'react';
import { apiReporte } from '../services/api';
import { useAuth } from '../context/AuthContext';

const ReportsDashboard = () => {
    const { isAdmin } = useAuth();
    const [salesReports, setSalesReports] = useState([]);
    const [inventoryReports, setInventoryReports] = useState([]);
    const [activeTab, setActiveTab] = useState('sales');
    const [error, setError] = useState(null);

    useEffect(() => {
        if (isAdmin()) {
            fetchSalesReports();
            fetchInventoryReports();
        }
    }, []);

    const fetchSalesReports = async () => {
        try {
            const response = await apiReporte.get('/reporte-ventas/');
            setSalesReports(response.data);
        } catch (error) {
            console.error("Error fetching sales reports", error);
            if (error.response?.status === 403) {
                setError('No tienes permisos para ver los reportes');
            }
        }
    };

    const fetchInventoryReports = async () => {
        try {
            const response = await apiReporte.get('/reporte-inventarios/');
            setInventoryReports(response.data);
        } catch (error) {
            console.error("Error fetching inventory reports", error);
            if (error.response?.status === 403) {
                setError('No tienes permisos para ver los reportes');
            }
        }
    };

    // Access denied for non-admin users
    if (!isAdmin()) {
        return (
            <div className="glass p-8 slide-up">
                <div className="text-center py-16">
                    <span className="text-6xl mb-4 block">🔒</span>
                    <h2 className="text-2xl font-bold text-gray-800 mb-2">Acceso Denegado</h2>
                    <p className="text-gray-500">Solo los administradores pueden acceder a los reportes.</p>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="glass p-8 slide-up">
                <div className="text-center py-16 text-red-500">
                    <span className="text-6xl mb-4 block">⚠️</span>
                    <h2 className="text-2xl font-bold mb-2">Error</h2>
                    <p>{error}</p>
                </div>
            </div>
        );
    }

    return (
        <div className="glass p-8 slide-up">
            <h2 className="text-3xl font-bold mb-8 text-gray-800">Reportes de Negocio</h2>

            <div className="flex gap-4 mb-8">
                <button
                    onClick={() => setActiveTab('sales')}
                    className={`px-6 py-2 rounded-lg font-medium transition-all ${activeTab === 'sales' ? 'bg-indigo-600 text-white shadow-md' : 'bg-white text-gray-600 hover:bg-gray-50'}`}
                >
                    Reportes de Ventas
                </button>
                <button
                    onClick={() => setActiveTab('inventory')}
                    className={`px-6 py-2 rounded-lg font-medium transition-all ${activeTab === 'inventory' ? 'bg-indigo-600 text-white shadow-md' : 'bg-white text-gray-600 hover:bg-gray-50'}`}
                >
                    Reportes de Inventario
                </button>
            </div>

            <div className="bg-white rounded-xl shadow-lg overflow-hidden">
                <div className="overflow-x-auto">
                    <table className="w-full text-left border-collapse">
                        <thead>
                            <tr className="bg-gray-50 border-b border-gray-100">
                                {activeTab === 'sales' ? (
                                    <>
                                        <th className="p-4 font-semibold text-gray-600">ID</th>
                                        <th className="p-4 font-semibold text-gray-600">Fecha</th>
                                        <th className="p-4 font-semibold text-gray-600">Total Ventas</th>
                                        <th className="p-4 font-semibold text-gray-600">Sucursal ID</th>
                                    </>
                                ) : (
                                    <>
                                        <th className="p-4 font-semibold text-gray-600">ID</th>
                                        <th className="p-4 font-semibold text-gray-600">Fecha</th>
                                        <th className="p-4 font-semibold text-gray-600">Medicamento ID</th>
                                        <th className="p-4 font-semibold text-gray-600">Stock</th>
                                        <th className="p-4 font-semibold text-gray-600">Sucursal ID</th>
                                    </>
                                )}
                            </tr>
                        </thead>
                        <tbody>
                            {(activeTab === 'sales' ? salesReports : inventoryReports).map((report) => (
                                <tr key={report.id} className="border-b border-gray-50 hover:bg-gray-50/50 transition-colors">
                                    <td className="p-4 text-gray-800 font-medium">#{report.id}</td>
                                    <td className="p-4 text-gray-600">{report.fecha}</td>
                                    {activeTab === 'sales' ? (
                                        <>
                                            <td className="p-4 text-green-600 font-bold">${report.totalVentas?.toFixed(2)}</td>
                                            <td className="p-4 text-gray-600">{report.sucursalId}</td>
                                        </>
                                    ) : (
                                        <>
                                            <td className="p-4 text-gray-600">{report.medicamentoId}</td>
                                            <td className="p-4 text-indigo-600 font-bold">{report.stock}</td>
                                            <td className="p-4 text-gray-600">{report.sucursalId}</td>
                                        </>
                                    )}
                                </tr>
                            ))}
                            {(activeTab === 'sales' ? salesReports : inventoryReports).length === 0 && (
                                <tr>
                                    <td colSpan="5" className="p-8 text-center text-gray-400">
                                        No hay reportes disponibles.
                                    </td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
};

export default ReportsDashboard;
