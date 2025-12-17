import React from 'react';
import { Link } from 'react-router-dom';
import { apiCatalogo, apiVentas, apiCliente } from '../services/api';

// Updated Components for Dashboard
const DashboardCard = ({ title, value, icon, color }) => (
    <div className="bg-white p-6 rounded-lg border border-slate-200 shadow-sm hover:shadow-md transition-all">
        <div className="flex items-center justify-between mb-4">
            <h3 className="text-slate-500 text-sm font-medium uppercase tracking-wider">{title}</h3>
            <div className={`p-2 rounded-md ${color} text-lg`}>{icon}</div>
        </div>
        <p className="text-3xl font-bold text-slate-800">{value}</p>
        <div className="mt-2 text-xs text-emerald-600 font-medium flex items-center gap-1">
            <span>↑ 12%</span> <span className="text-slate-400 font-normal">vs mes anterior</span>
        </div>
    </div>
);

const QuickAction = ({ to, title, description, icon }) => (
    <Link to={to} className="group bg-white p-6 rounded-lg border border-slate-200 shadow-sm hover:border-indigo-500 hover:shadow-md transition-all flex items-start gap-4 text-decoration-none">
        <div className="bg-slate-50 p-3 rounded-lg text-2xl group-hover:bg-indigo-50 group-hover:text-indigo-600 transition-colors">
            {icon}
        </div>
        <div>
            <h3 className="text-lg font-semibold text-slate-800 group-hover:text-indigo-600 transition-colors mb-0">{title}</h3>
            <p className="text-slate-500 text-sm mt-1 leading-relaxed">{description}</p>
        </div>
    </Link>
);

const Dashboard = () => {
    const [stats, setStats] = React.useState({
        sales: 0,
        products: 0,
        clients: 0,
        alerts: 0
    });

    React.useEffect(() => {
        const fetchData = async () => {
            try {
                // Fetch basic lists to calculate stats
                const [salesRes, productsRes, clientsRes] = await Promise.all([
                    apiVentas.get('/ventas/'),
                    apiCatalogo.get('/medicamentos'),
                    apiCliente.get('/clientes/')
                ]);

                // Calculate totals
                const totalSales = salesRes.data.reduce((acc, curr) => acc + curr.total, 0);
                const totalProducts = productsRes.data.length;
                const totalClients = clientsRes.data.length;
                const lowStockAlerts = productsRes.data.filter(p => p.stock < 10).length;

                setStats({
                    sales: totalSales,
                    products: totalProducts,
                    clients: totalClients,
                    alerts: lowStockAlerts
                });

            } catch (error) {
                console.error("Error fetching dashboard data:", error);
            }
        };

        fetchData();
    }, []);

    return (
        <div className="slide-up">
            <header className="mb-8 flex justify-between items-end border-b border-slate-200 pb-6">
                <div>
                    <h1 className="text-3xl font-bold text-slate-800 tracking-tight">Dashboard General</h1>
                    <p className="text-slate-500 mt-1">Visión general del rendimiento de FarmaciaSys</p>
                </div>
                <div className="text-sm text-slate-400">Ultima actualización: {new Date().toLocaleTimeString()}</div>
            </header>

            {/* Stats Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-10">
                <DashboardCard
                    title="Ventas Totales"
                    value={`$${stats.sales.toFixed(2)}`}
                    icon="💵"
                    color="text-emerald-600 bg-emerald-50"
                />
                <DashboardCard
                    title="Productos Activos"
                    value={stats.products}
                    icon="📦"
                    color="text-blue-600 bg-blue-50"
                />
                <DashboardCard
                    title="Clientes Nuevos"
                    value={stats.clients}
                    icon="👥"
                    color="text-indigo-600 bg-indigo-50"
                />
                <DashboardCard
                    title="Alertas Stock"
                    value={stats.alerts}
                    icon="⚠️"
                    color="text-amber-600 bg-amber-50"
                />
            </div>

            <h2 className="text-xl font-semibold text-slate-800 mb-6 flex items-center gap-2">
                Accesos Directos
                <div className="h-px bg-slate-200 flex-1 ml-4"></div>
            </h2>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                <QuickAction
                    to="/medicines"
                    title="Inventario"
                    description="Gestionar catálogo, precios y stock"
                    icon="💊"
                />
                <QuickAction
                    to="/sales"
                    title="Terminal POS"
                    description="Nueva venta y facturación"
                    icon="📠"
                />
                <QuickAction
                    to="/clients"
                    title="Base de Clientes"
                    description="Administrar información de compradores"
                    icon="👥"
                />
                <QuickAction
                    to="/branches"
                    title="Sucursales"
                    description="Gestión de locales y puntos de venta"
                    icon="🏢"
                />
                <QuickAction
                    to="/reports"
                    title="Reportes"
                    description="Analíticas de ventas y rendimiento"
                    icon="📈"
                />
            </div>
        </div>
    );
};

export default Dashboard;
