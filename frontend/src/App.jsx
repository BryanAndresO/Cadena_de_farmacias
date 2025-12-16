import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import Dashboard from './pages/Dashboard';
import MedicineList from './components/MedicineList';
import BranchList from './components/BranchList';
import SalesPOS from './components/SalesPOS';

function App() {
  return (
    <BrowserRouter>
      <div className="min-h-screen bg-gray-50 text-gray-900 font-sans">
        <nav>
          <div className="container">
            <Link to="/" className="logo">FarmaciaSys</Link>
            <div className="links">
              <Link to="/">Inicio</Link>
              <Link to="/medicines">Medicamentos</Link>
              <Link to="/branches">Sucursales</Link>
              <Link to="/sales">Ventas</Link>
            </div>
          </div>
        </nav>

        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/medicines" element={<MedicineList />} />
          <Route path="/branches" element={<BranchList />} />
          <Route path="/sales" element={<SalesPOS />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;
