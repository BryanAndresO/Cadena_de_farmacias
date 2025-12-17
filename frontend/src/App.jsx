import { BrowserRouter, Routes, Route, Link, useLocation } from 'react-router-dom';
import Dashboard from './pages/Dashboard';
import MedicineList from './components/MedicineList';
import BranchList from './components/BranchList';
import SalesPOS from './components/SalesPOS';
import ClientList from './components/ClientList';
import ReportsDashboard from './components/ReportsDashboard';

// Sidebar Link Component for active state styling
const SidebarLink = ({ to, icon, label }) => {
  const location = useLocation();
  const isActive = location.pathname === to;
  return (
    <Link to={to} className={`sidebar-link ${isActive ? 'active' : ''}`}>
      <span className="text-xl">{icon}</span>
      <span>{label}</span>
    </Link>
  );
};

const Layout = ({ children }) => {
  return (
    <div className="min-h-screen flex bg-gray-50">
      {/* Sidebar */}
      <aside className="w-64 bg-white border-r border-gray-200 fixed h-full z-[9999] shadow-lg md:block">
        <div className="p-6 border-b border-gray-100">
          <h1 className="text-2xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-indigo-600 to-purple-600">
            FarmaciaSys
          </h1>
        </div>
        <nav className="p-4 space-y-2">
          <SidebarLink to="/" icon="📊" label="Dashboard" />
          <SidebarLink to="/medicines" icon="💊" label="Medicamentos" />
          <SidebarLink to="/branches" icon="🏥" label="Sucursales" />
          <SidebarLink to="/clients" icon="👥" label="Clientes" />
          <SidebarLink to="/sales" icon="🛒" label="Punto de Venta" />
          <SidebarLink to="/reports" icon="📈" label="Reportes" />
        </nav>
      </aside>

      {/* Main Content */}
      <main className="flex-1 md:ml-64 p-8">
        <div className="max-w-7xl mx-auto">
          {children}
        </div>
      </main>
    </div>
  );
};

function App() {
  return (
    <BrowserRouter>
      <Layout>
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/medicines" element={<MedicineList />} />
          <Route path="/branches" element={<BranchList />} />
          <Route path="/sales" element={<SalesPOS />} />
          <Route path="/clients" element={<ClientList />} />
          <Route path="/reports" element={<ReportsDashboard />} />
        </Routes>
      </Layout>
    </BrowserRouter>
  );
}

export default App;
