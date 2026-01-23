import { BrowserRouter, Routes, Route, Link, useLocation } from 'react-router-dom';
import { useState, useEffect } from 'react';
import Dashboard from './pages/Dashboard';
import AuthCallback from './pages/AuthCallback';
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
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Fetch user info from the gateway
    fetch('/api/userinfo', { credentials: 'include' })
      .then(res => res.json())
      .then(data => {
        if (data.authenticated) {
          setUser(data);
        }
        setLoading(false);
      })
      .catch(err => {
        console.error('Error fetching user info:', err);
        setLoading(false);
      });
  }, []);

  const handleLogout = () => {
    // Redirect to Spring Security logout endpoint
    window.location.href = '/logout';
  };

  return (
    <div className="min-h-screen flex bg-gray-50">
      {/* Sidebar */}
      <aside className="w-64 fixed h-full z-40 shadow-xl transition-transform duration-300">
        <div className="p-6 flex items-center gap-2">
          {/* Simple Icon Logo */}
          <div className="w-8 h-8 bg-indigo-500 rounded-lg flex items-center justify-center">
            <span className="text-white font-bold text-lg">F</span>
          </div>
          <h1 className="text-xl font-bold tracking-tight text-white m-0">
            FarmaciaSys
          </h1>
        </div>
        <nav className="p-4 space-y-1">
          {/* User info and login/logout */}
          {loading ? (
            <div className="px-4 py-2 text-slate-400 text-sm">Cargando...</div>
          ) : user ? (
            <div className="mb-4 pb-4 border-b border-slate-600">
              <div className="px-4 py-2 text-white">
                <div className="text-sm font-medium">👤 {user.name || user.username}</div>
                {user.roles && user.roles.length > 0 && (
                  <div className="text-xs text-slate-400 mt-1">
                    {user.roles.map(role => role.replace('ROLE_', '')).join(', ')}
                  </div>
                )}
              </div>
              <button
                onClick={handleLogout}
                className="block w-full px-4 py-2 mt-2 rounded bg-red-600 hover:bg-red-700 text-white text-center transition-colors"
              >
                Cerrar sesión
              </button>
            </div>
          ) : (
            <a
              href="/oauth2/authorization/gateway-client"
              className="block w-full px-4 py-2 rounded bg-indigo-600 hover:bg-indigo-700 text-white text-center text-decoration-none transition-colors mb-4"
            >
              Iniciar sesión
            </a>
          )}
          <SidebarLink to="/" icon="📊" label="Dashboard" />
          <SidebarLink to="/medicines" icon="💊" label="Medicamentos" />
          <SidebarLink to="/branches" icon="🏥" label="Sucursales" />
          <SidebarLink to="/clients" icon="👥" label="Clientes" />
          <SidebarLink to="/sales" icon="🛒" label="Terminal POS" />
          <SidebarLink to="/reports" icon="📈" label="Reportes" />
        </nav>
      </aside>

      {/* Main Content */}
      <main className="flex-1 md:ml-64 p-8 bg-slate-50 min-h-screen">
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
          <Route path="/oauth2/callback" element={<AuthCallback />} />
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
