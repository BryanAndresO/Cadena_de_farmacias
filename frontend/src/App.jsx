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
          setLoading(false);
        } else {
          // Not authenticated - redirect to login
          window.location.href = '/oauth2/authorization/gateway-client';
        }
      })
      .catch(err => {
        console.error('Error fetching user info:', err);
        // On error, redirect to login
        window.location.href = '/oauth2/authorization/gateway-client';
      });
  }, []);

  const handleLogout = async () => {
    // Clear user state immediately
    setUser(null);
    
    // Use frontend auth service to call backend logout, clear storage and
    // redirect to provider logout when available.
    try {
      const { logout } = await import('./services/auth');
      await logout();
    } catch (err) {
      console.error('Error during logout', err);
      // Force a full page reload to clear all state
      window.location.href = '/logout';
    }
  };

  // Show loading screen while checking authentication
  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-slate-800">
        <div className="text-center">
          <div className="w-16 h-16 bg-indigo-500 rounded-lg flex items-center justify-center mx-auto mb-4">
            <span className="text-white font-bold text-3xl">F</span>
          </div>
          <h1 className="text-2xl font-bold text-white mb-2">FarmaciaSys</h1>
          <p className="text-slate-400">Verificando autenticación...</p>
          <div className="mt-4">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-500 mx-auto"></div>
          </div>
        </div>
      </div>
    );
  }

  // If not authenticated, the useEffect will redirect - show nothing
  if (!user) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-slate-800">
        <div className="text-center">
          <p className="text-slate-400">Redirigiendo al login...</p>
        </div>
      </div>
    );
  }

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
          {/* User info */}
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
