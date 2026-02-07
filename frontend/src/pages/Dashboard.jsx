import React from 'react';
import { Link } from 'react-router-dom';
import { apiCatalogo, apiVentas, apiCliente } from '../services/api';

// Updated Components for Dashboard
const DashboardCard = ({ title, value, icon, color, accent }) => (
    <div className="bg-white p-6 rounded-xl border border-slate-200 hover:border-slate-300 hover:shadow-md transition-all duration-200 group">
        <div className="flex items-center justify-between mb-4">
            <h3 className="text-slate-500 text-xs font-semibold uppercase tracking-wider">{title}</h3>
            <div className={`p-2.5 rounded-lg ${color} text-lg shadow-sm group-hover:scale-110 transition-transform`}>{icon}</div>
        </div>
        <p className="text-3xl font-bold text-slate-800">{value}</p>
        <div className="mt-3 text-xs font-medium flex items-center gap-1">
            <span className={`${accent} flex items-center gap-0.5`}>
                <svg className="w-3 h-3" fill="currentColor" viewBox="0 0 20 20"><path fillRule="evenodd" d="M5.293 9.707a1 1 0 010-1.414l4-4a1 1 0 011.414 0l4 4a1 1 0 01-1.414 1.414L11 7.414V15a1 1 0 11-2 0V7.414L6.707 9.707a1 1 0 01-1.414 0z" clipRule="evenodd"></path></svg>
                12%
            </span>
            <span className="text-slate-400 font-normal">vs mes anterior</span>
        </div>
    </div>
);

const QuickAction = ({ to, title, description, icon }) => (
    <Link to={to} className="group bg-white p-5 rounded-xl border border-slate-200 hover:border-teal-300 hover:shadow-lg hover:shadow-teal-500/5 transition-all duration-200 flex items-start gap-4 text-decoration-none">
        <div className="bg-gradient-to-br from-slate-100 to-slate-50 p-3.5 rounded-xl text-2xl group-hover:from-teal-50 group-hover:to-emerald-50 group-hover:scale-110 transition-all duration-200 shadow-sm">
            {icon}
        </div>
        <div>
            <h3 className="text-base font-semibold text-slate-800 group-hover:text-teal-700 transition-colors mb-0.5">{title}</h3>
            <p className="text-slate-500 text-sm leading-relaxed">{description}</p>
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
                // TODO: Migrate to Inventario API for low stock alerts
                // const lowStockAlerts = productsRes.data.filter(p => p.stock < 10).length;
                const lowStockAlerts = 0; // Temporarily disabled until Inventario API integration

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
                    <h1 className="text-2xl font-bold text-slate-800 tracking-tight">Dashboard General</h1>
                    <p className="text-slate-500 mt-1 text-sm">Visión general del rendimiento de FarmaciaSys</p>
                </div>
                <div className="text-xs text-slate-400 bg-slate-100 px-3 py-1.5 rounded-full">🕐 {new Date().toLocaleTimeString()}</div>
            </header>

            {/* Stats Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-5 mb-10">
                <DashboardCard
                    title="Ventas Totales"
                    value={`$${stats.sales.toFixed(2)}`}
                    icon="💵"
                    color="text-emerald-600 bg-gradient-to-br from-emerald-50 to-teal-50"
                    accent="text-emerald-500"
                />
                <DashboardCard
                    title="Productos Activos"
                    value={stats.products}
                    icon="📦"
                    color="text-blue-600 bg-gradient-to-br from-blue-50 to-sky-50"
                    accent="text-blue-500"
                />
                <DashboardCard
                    title="Clientes Nuevos"
                    value={stats.clients}
                    icon="👥"
                    color="text-violet-600 bg-gradient-to-br from-violet-50 to-purple-50"
                    accent="text-violet-500"
                />
                <DashboardCard
                    title="Alertas Stock"
                    value={stats.alerts}
                    icon="⚠️"
                    color="text-amber-600 bg-gradient-to-br from-amber-50 to-orange-50"
                    accent="text-amber-500"
                />
            </div>

            <h2 className="text-lg font-bold text-slate-800 mb-5 flex items-center gap-3">
                <span className="w-1 h-6 bg-gradient-to-b from-teal-400 to-emerald-500 rounded-full"></span>
                Accesos Directos
                <div className="h-px bg-slate-200 flex-1 ml-2"></div>
            </h2>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
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
