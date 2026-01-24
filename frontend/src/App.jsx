import { BrowserRouter, Routes, Route, Link, useLocation } from 'react-router-dom';
import Dashboard from './pages/Dashboard';
import AuthCallback from './pages/AuthCallback';
import MedicineList from './components/MedicineList';
import BranchList from './components/BranchList';
import SalesPOS from './components/SalesPOS';
import ClientList from './components/ClientList';
import ReportsDashboard from './components/ReportsDashboard';
import { AuthProvider, useAuth } from './context/AuthContext';

// Sidebar Link Component for active state styling
const SidebarLink = ({ to, icon, label, requireAdmin = false }) => {
  const location = useLocation();
  const { isAdmin } = useAuth();
  const isActive = location.pathname === to;
  
  // Hide link if it requires admin and user is not admin
  if (requireAdmin && !isAdmin()) {
    return null;
  }
  
  return (
    <Link
      to={to}
      className={`flex items-center gap-3 px-3 py-2.5 rounded-lg transition-colors duration-200 group w-full text-left
        ${isActive
          ? 'bg-slate-800 text-slate-100 border-l-4 border-teal-500 pl-2'
          : 'text-slate-300 hover:text-white hover:bg-slate-800/40'
        }`}>
      <span className={`w-6 h-6 flex items-center justify-center ${isActive ? 'text-teal-400' : 'text-slate-400 group-hover:text-slate-200'}`}>{icon}</span>
      <span className="font-medium text-sm">{label}</span>
      {isActive && <span className="ml-auto w-2 h-2 bg-teal-400 rounded-full shadow-sm"></span>}
    </Link>
  );
};

const Layout = ({ children }) => {
  const { user, loading, logout, isAdmin } = useAuth();

  // Show loading screen while checking authentication
  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-zinc-900">
        <div className="text-center">
          <div className="w-16 h-16 bg-zinc-700 rounded-lg flex items-center justify-center mx-auto mb-4">
            <span className="text-white font-bold text-3xl">F</span>
          </div>
          <h1 className="text-2xl font-bold text-white mb-2">FarmaciaSys</h1>
          <p className="text-zinc-400">Verificando autenticación...</p>
          <div className="mt-4">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-zinc-500 mx-auto"></div>
          </div>
        </div>
      </div>
    );
  }

  // If not authenticated, the useEffect will redirect - show nothing
  if (!user) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-zinc-900">
        <div className="text-center">
          <p className="text-zinc-400">Redirigiendo al login...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex bg-slate-50">
      {/* Sidebar */}
      <aside className="w-64 fixed h-full z-40 transition-transform duration-200 sidebar">
        {/* Logo Section (uniform color) */}
        <div className="p-5 flex items-center gap-3 border-b border-slate-800">
          <div className="w-10 h-10 bg-slate-800 rounded-lg flex items-center justify-center ring-1 ring-slate-700">
            <div className="w-7 h-7 bg-teal-500 rounded-md flex items-center justify-center text-white font-bold text-sm">F</div>
          </div>
          <div>
            <h1 className="text-lg font-semibold tracking-tight text-slate-100 m-0">
              FarmaciaSys
            </h1>
            <p className="text-xs text-slate-400 -mt-0.5">Gestión Farmacéutica</p>
          </div>
        </div>
        
        <nav className="p-4 space-y-3">
          {/* User info */}
          <div className="mb-2 pb-2 border-b border-slate-800">
            <div className="px-2 py-3 flex items-center gap-3">
              <div className="w-10 h-10 bg-slate-700 rounded-full flex items-center justify-center text-white font-semibold text-sm">
                {(user.name || user.username || 'U').charAt(0).toUpperCase()}
              </div>
              <div className="flex-1">
                <div className="text-sm font-semibold text-slate-100">{user.name || user.username}</div>
                {user.roles && user.roles.length > 0 && (
                  <div className="text-xs text-slate-400">
                    {user.roles.map(role => role.replace('ROLE_', '')).join(', ')}
                  </div>
                )}
              </div>
            </div>
            <button
              onClick={logout}
              className="btn btn-secondary w-full px-3 py-2 mt-3 text-sm"
            >
              Cerrar sesión
            </button>
          </div>
          <SidebarLink to="/" icon={<svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" viewBox="0 0 20 20" fill="currentColor"><path d="M3 3h4v4H3V3zM3 9h4v8H3V9zm6-6h8v6H9V3zm0 8h8v6H9v-6z"/></svg>} label="Dashboard" />
          <SidebarLink to="/medicines" icon={<svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" viewBox="0 0 24 24" fill="currentColor"><path d="M21.707 11.293l-9-9a2 2 0 00-2.828 0L6 5.172 18.828 18l3.879-3.879a2 2 0 000-2.828zM3 17.25A2.25 2.25 0 015.25 15h1.5A2.25 2.25 0 011 17.25 2.25 2.25 0 013 17.25z"/></svg>} label="Medicamentos" />
          <SidebarLink to="/branches" icon={<svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" viewBox="0 0 24 24" fill="currentColor"><path d="M3 13h2v-2H3v2zm0 4h2v-2H3v2zM7 7h2V5H7v2zm0 4h2V9H7v2zm0 4h2v-2H7v2zM11 3h8v2h-8V3zm0 4h8v2h-8V7zm0 4h8v6h-8v-6z"/></svg>} label="Sucursales" requireAdmin={true} />
          <SidebarLink to="/clients" icon={<svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" viewBox="0 0 24 24" fill="currentColor"><path d="M16 11c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zM6 11c1.657 0 3-1.343 3-3S7.657 5 6 5 3 6.343 3 8s1.343 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V20h14v-3.5C13 14.17 8.33 13 6 13zm10 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.99 1.97 3.45V20h6v-3.5C23 14.17 18.33 13 16 13z"/></svg>} label="Clientes" />
          <SidebarLink to="/sales" icon={<svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" viewBox="0 0 24 24" fill="currentColor"><path d="M7 18c-1.1 0-1.99.9-1.99 2S5.9 22 7 22s2-.9 2-2-.9-2-2-2zM1 2v2h2l3.6 7.59-1.35 2.45A1.99 1.99 0 006 17h12v-2H7.42c-.14 0-.25-.11-.25-.25l.03-.12L8.1 13h7.45c.75 0 1.41-.41 1.75-1.03l3.58-6.49A1 1 0 0020.9 3H5.21L4.27 1H1z"/></svg>} label="Terminal POS" />
          <SidebarLink to="/reports" icon={<svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" viewBox="0 0 24 24" fill="currentColor"><path d="M3 13h2v6H3v-6zm4-4h2v10H7V9zm4-6h2v16h-2V3zm4 8h2v8h-2v-8z"/></svg>} label="Reportes" requireAdmin={true} />
        </nav>
      </aside>

      {/* Main Content */}
      <main className="flex-1 md:ml-64 p-8 bg-gradient-to-br from-slate-50 to-slate-100 min-h-screen">
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
      <AuthProvider>
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
    </AuthProvider>
  </BrowserRouter>
  );
}

export default App;
