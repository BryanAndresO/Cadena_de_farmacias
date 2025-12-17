import React from 'react';
import { Link } from 'react-router-dom';

const DashboardCard = ({ title, value, icon, color }) => (
    <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 hover:shadow-md transition-shadow">
        <div className="flex items-center justify-between mb-4">
            <h3 className="text-gray-500 text-sm font-medium">{title}</h3>
            <span className={`p-2 rounded-lg ${color} bg-opacity-10 text-xl`}>{icon}</span>
        </div>
        <p className="text-3xl font-bold text-gray-800">{value}</p>
    </div>
);

const QuickAction = ({ to, title, description, icon, gradient }) => (
    <Link to={to} className={`block p-6 rounded-xl text-white shadow-lg transform transition-all hover:scale-[1.02] hover:shadow-xl ${gradient}`}>
        <div className="text-3xl mb-4">{icon}</div>
        <h3 className="text-xl font-bold mb-2">{title}</h3>
        <p className="text-white/80 text-sm">{description}</p>
    </Link>
);

const Dashboard = () => {
    return (
        <div className="slide-up">
            <header className="mb-8">
                <h1 className="text-4xl font-bold text-gray-800 mb-2">Bienvenido a FarmaciaSys</h1>
                <p className="text-gray-500">Panel de control general del sistema</p>
            </header>

            {/* Stats Grid - Mock Data for Visuals */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-10">
                <DashboardCard title="Ventas del Día" value="$1,240" icon="💰" color="bg-green-500 text-green-600" />
                <DashboardCard title="Productos" value="452" icon="💊" color="bg-blue-500 text-blue-600" />
                <DashboardCard title="Sucursales" value="3" icon="🏥" color="bg-purple-500 text-purple-600" />
                <DashboardCard title="Clientes" value="128" icon="👥" color="bg-orange-500 text-orange-600" />
            </div>

            <h2 className="text-2xl font-bold text-gray-800 mb-6">Accesos Rápidos</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                <QuickAction
                    to="/medicines"
                    title="Medicamentos"
                    description="Gestionar catálogo y stock"
                    icon="🏷️"
                    gradient="bg-gradient-to-br from-blue-500 to-blue-700"
                />
                <QuickAction
                    to="/sales"
                    title="Nueva Venta"
                    description="Registrar facturación POS"
                    icon="🛒"
                    gradient="bg-gradient-to-br from-green-500 to-emerald-700"
                />
                <QuickAction
                    to="/clients"
                    title="Clientes"
                    description="Base de datos de compradores"
                    icon="👤"
                    gradient="bg-gradient-to-br from-orange-400 to-orange-600"
                />
                <QuickAction
                    to="/branches"
                    title="Sucursales"
                    description="Administrar puntos de venta"
                    icon="🏢"
                    gradient="bg-gradient-to-br from-purple-500 to-purple-700"
                />
                <QuickAction
                    to="/reports"
                    title="Reportes"
                    description="Análisis de ventas e inventario"
                    icon="📊"
                    gradient="bg-gradient-to-br from-indigo-500 to-indigo-700"
                />
            </div>
        </div>
    );
};

export default Dashboard;
