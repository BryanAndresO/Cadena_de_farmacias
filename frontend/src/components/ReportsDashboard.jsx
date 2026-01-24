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
        <div className="slide-up">
            <div className="flex items-center gap-3 mb-6">
                <span className="w-1 h-8 bg-gradient-to-b from-teal-400 to-emerald-500 rounded-full"></span>
                <h2 className="text-2xl font-bold text-slate-800">Reportes de Negocio</h2>
            </div>

            <div className="flex gap-2 mb-6">
                <button
                    onClick={() => setActiveTab('sales')}
                    className={`px-5 py-2.5 rounded-lg text-sm font-medium transition-all ${activeTab === 'sales' 
                        ? 'bg-gradient-to-r from-slate-800 to-slate-900 text-white shadow-md' 
                        : 'bg-white text-slate-600 hover:bg-slate-50 border border-slate-200'}`}
                >
                    📊 Reportes de Ventas
                </button>
                <button
                    onClick={() => setActiveTab('inventory')}
                    className={`px-5 py-2.5 rounded-lg text-sm font-medium transition-all ${activeTab === 'inventory' 
                        ? 'bg-gradient-to-r from-slate-800 to-slate-900 text-white shadow-md' 
                        : 'bg-white text-slate-600 hover:bg-slate-50 border border-slate-200'}`}
                >
                    📦 Reportes de Inventario
                </button>
            </div>

            <div className="bg-white rounded-xl border border-slate-200 overflow-hidden shadow-sm">
                <div className="overflow-x-auto">
                    <table className="w-full text-left border-collapse">
                        <thead>
                            <tr className="bg-neutral-50 border-b border-neutral-100">
                                {activeTab === 'sales' ? (
                                    <>
                                        <th className="p-3 font-medium text-neutral-600 text-sm">ID</th>
                                        <th className="p-3 font-medium text-neutral-600 text-sm">Fecha</th>
                                        <th className="p-3 font-medium text-neutral-600 text-sm">Total Ventas</th>
                                        <th className="p-3 font-medium text-neutral-600 text-sm">Sucursal ID</th>
                                    </>
                                ) : (
                                    <>
                                        <th className="p-3 font-medium text-neutral-600 text-sm">ID</th>
                                        <th className="p-3 font-medium text-neutral-600 text-sm">Fecha</th>
                                        <th className="p-3 font-medium text-neutral-600 text-sm">Medicamento ID</th>
                                        <th className="p-3 font-medium text-neutral-600 text-sm">Stock</th>
                                        <th className="p-3 font-medium text-neutral-600 text-sm">Sucursal ID</th>
                                    </>
                                )}
                            </tr>
                        </thead>
                        <tbody>
                            {(activeTab === 'sales' ? salesReports : inventoryReports).map((report) => (
                                <tr key={report.id} className="border-b border-neutral-100 hover:bg-neutral-50 transition-colors">
                                    <td className="p-3 text-neutral-800 text-sm">#{report.id}</td>
                                    <td className="p-3 text-neutral-600 text-sm">{report.fecha}</td>
                                    {activeTab === 'sales' ? (
                                        <>
                                            <td className="p-3 text-neutral-800 font-medium text-sm">${report.totalVentas?.toFixed(2)}</td>
                                            <td className="p-3 text-neutral-600 text-sm">{report.sucursalId}</td>
                                        </>
                                    ) : (
                                        <>
                                            <td className="p-3 text-neutral-600 text-sm">{report.medicamentoId}</td>
                                            <td className="p-3 text-neutral-800 font-medium text-sm">{report.stock}</td>
                                            <td className="p-3 text-neutral-600 text-sm">{report.sucursalId}</td>
                                        </>
                                    )}
                                </tr>
                            ))}
                            {(activeTab === 'sales' ? salesReports : inventoryReports).length === 0 && (
                                <tr>
                                    <td colSpan="5" className="p-6 text-center text-neutral-400 text-sm">
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
