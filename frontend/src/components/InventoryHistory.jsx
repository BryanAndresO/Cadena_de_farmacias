import { useState, useEffect } from 'react';
import api from '../services/api';

function InventoryHistory() {
  const [movements, setMovements] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filterType, setFilterType] = useState('all'); // all, sucursal, producto
  const [filterId, setFilterId] = useState('');

  useEffect(() => {
    loadMovements();
  }, [filterType, filterId]);

  const loadMovements = async () => {
    try {
      setLoading(true);
      setError(null);
      
      let response;
      if (filterType === 'all') {
        response = await api.get('/api/inventario/movimientos');
      } else if (filterType === 'sucursal' && filterId) {
        response = await api.get(`/api/inventario/movimientos/sucursal/${filterId}`);
      } else if (filterType === 'producto' && filterId) {
        response = await api.get(`/api/inventario/movimientos/producto/${filterId}`);
      } else {
        response = await api.get('/api/inventario/movimientos');
      }
      
      setMovements(response.data);
    } catch (err) {
      console.error('Error loading movements:', err);
      setError('Error al cargar el historial de movimientos');
    } finally {
      setLoading(false);
    }
  };

  const getTipoMovimientoLabel = (tipo) => {
    const labels = {
      ASIGNACION_INICIAL: 'Asignación Inicial',
      ASIGNACION_ADICIONAL: 'Asignación Adicional',
      DEVOLUCION: 'Devolución',
      AJUSTE_INVENTARIO: 'Ajuste de Inventario',
      VENTA: 'Venta',
      ENTRADA_COMPRA: 'Entrada por Compra'
    };
    return labels[tipo] || tipo;
  };

  const getTipoMovimientoColor = (tipo) => {
    const colors = {
      ASIGNACION_INICIAL: 'bg-blue-100 text-blue-800',
      ASIGNACION_ADICIONAL: 'bg-green-100 text-green-800',
      DEVOLUCION: 'bg-yellow-100 text-yellow-800',
      AJUSTE_INVENTARIO: 'bg-purple-100 text-purple-800',
      VENTA: 'bg-red-100 text-red-800',
      ENTRADA_COMPRA: 'bg-teal-100 text-teal-800'
    };
    return colors[tipo] || 'bg-gray-100 text-gray-800';
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleString('es-ES', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  if (loading) {
    return (
      <div className="card">
        <div className="flex items-center justify-center p-8">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-slate-700"></div>
          <span className="ml-3 text-slate-600">Cargando historial...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Historial de Movimientos de Inventario</h1>
          <p className="text-sm text-slate-600 mt-1">
            Registro completo e inmutable de todas las transacciones
          </p>
        </div>
      </div>

      {/* Filtros */}
      <div className="card">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">Tipo de Filtro</label>
            <select
              value={filterType}
              onChange={(e) => {
                setFilterType(e.target.value);
                setFilterId('');
              }}
              className="w-full px-3 py-2 border border-slate-300 rounded-md focus:outline-none focus:ring-2 focus:ring-teal-500"
            >
              <option value="all">Todos</option>
              <option value="sucursal">Por Sucursal</option>
              <option value="producto">Por Producto</option>
            </select>
          </div>
          {(filterType === 'sucursal' || filterType === 'producto') && (
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">
                ID {filterType === 'sucursal' ? 'de Sucursal' : 'de Producto'}
              </label>
              <input
                type="text"
                value={filterId}
                onChange={(e) => setFilterId(e.target.value)}
                placeholder={`Ingrese ID ${filterType === 'sucursal' ? 'de sucursal' : 'de producto'}`}
                className="w-full px-3 py-2 border border-slate-300 rounded-md focus:outline-none focus:ring-2 focus:ring-teal-500"
              />
            </div>
          )}
          <div className="flex items-end">
            <button
              onClick={loadMovements}
              className="btn btn-primary w-full"
            >
              Aplicar Filtro
            </button>
          </div>
        </div>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
          {error}
        </div>
      )}

      {/* Tabla de movimientos */}
      <div className="card overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="bg-slate-50">
                <th className="px-4 py-3 text-left text-xs font-semibold text-slate-600 uppercase">Fecha</th>
                <th className="px-4 py-3 text-left text-xs font-semibold text-slate-600 uppercase">Tipo</th>
                <th className="px-4 py-3 text-left text-xs font-semibold text-slate-600 uppercase">Sucursal</th>
                <th className="px-4 py-3 text-left text-xs font-semibold text-slate-600 uppercase">Producto</th>
                <th className="px-4 py-3 text-right text-xs font-semibold text-slate-600 uppercase">Cantidad</th>
                <th className="px-4 py-3 text-right text-xs font-semibold text-slate-600 uppercase">Stock Anterior</th>
                <th className="px-4 py-3 text-right text-xs font-semibold text-slate-600 uppercase">Stock Nuevo</th>
                <th className="px-4 py-3 text-right text-xs font-semibold text-slate-600 uppercase">Stock Global</th>
                <th className="px-4 py-3 text-left text-xs font-semibold text-slate-600 uppercase">Observaciones</th>
              </tr>
            </thead>
            <tbody>
              {movements.length === 0 ? (
                <tr>
                  <td colSpan="9" className="px-4 py-8 text-center text-slate-500">
                    No hay movimientos registrados
                  </td>
                </tr>
              ) : (
                movements.map((mov) => (
                  <tr key={mov.id} className="border-t border-slate-200 hover:bg-slate-50">
                    <td className="px-4 py-3 text-sm text-slate-600">
                      {formatDate(mov.fechaMovimiento)}
                    </td>
                    <td className="px-4 py-3">
                      <span className={`inline-flex px-2 py-1 text-xs font-medium rounded ${getTipoMovimientoColor(mov.tipoMovimiento)}`}>
                        {getTipoMovimientoLabel(mov.tipoMovimiento)}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-sm text-slate-900">{mov.sucursalId}</td>
                    <td className="px-4 py-3 text-sm text-slate-900">{mov.productoId}</td>
                    <td className={`px-4 py-3 text-sm text-right font-semibold ${mov.cantidad > 0 ? 'text-green-600' : 'text-red-600'}`}>
                      {mov.cantidad > 0 ? '+' : ''}{mov.cantidad}
                    </td>
                    <td className="px-4 py-3 text-sm text-right text-slate-600">
                      {mov.stockAnterior || 0}
                    </td>
                    <td className="px-4 py-3 text-sm text-right text-slate-900 font-semibold">
                      {mov.stockNuevo || 0}
                    </td>
                    <td className="px-4 py-3 text-sm text-right text-slate-600">
                      {mov.stockGlobalAnterior !== null && mov.stockGlobalNuevo !== null
                        ? `${mov.stockGlobalAnterior} → ${mov.stockGlobalNuevo}`
                        : 'N/A'}
                    </td>
                    <td className="px-4 py-3 text-sm text-slate-600">
                      {mov.observaciones || '-'}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Resumen */}
      {movements.length > 0 && (
        <div className="card bg-slate-50">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-slate-600">Total de movimientos</p>
              <p className="text-2xl font-bold text-slate-900">{movements.length}</p>
            </div>
            <div className="text-right">
              <p className="text-xs text-slate-600">
                Los movimientos son <span className="font-semibold text-teal-600">inmutables</span>
              </p>
              <p className="text-xs text-slate-500 mt-1">
                No se pueden eliminar ni modificar registros pasados
              </p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default InventoryHistory;
