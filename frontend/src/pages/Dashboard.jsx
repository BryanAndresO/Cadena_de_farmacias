import React from 'react';
import { Link } from 'react-router-dom';

const Dashboard = () => {
    return (
        <div className="container mx-auto p-4">
            <h1 className="text-3xl font-bold mb-6">Sistema de Gestión de Farmacias</h1>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                <div className="card">
                    <h2 className="text-xl font-semibold mb-2">Medicamentos</h2>
                    <p className="text-gray-600 mb-4">Gestión del catálogo de productos y precios.</p>
                    <Link to="/medicines" className="text-blue-500 font-medium">Ir a Medicamentos &rarr;</Link>
                </div>

                <div className="card">
                    <h2 className="text-xl font-semibold mb-2">Sucursales e Inventario</h2>
                    <p className="text-gray-600 mb-4">Administrar puntos de venta y stock disponible.</p>
                    <Link to="/branches" className="text-blue-500 font-medium">Ir a Sucursales &rarr;</Link>
                </div>

                <div className="card">
                    <h2 className="text-xl font-semibold mb-2">Punto de Venta</h2>
                    <p className="text-gray-600 mb-4">Registrar ventas y facturación.</p>
                    <Link to="/sales" className="text-blue-500 font-medium">Ir a Ventas &rarr;</Link>
                </div>
            </div>
        </div>
    );
};

export default Dashboard;
