import { useState, useEffect } from 'react';

const LoginPage = ({ onLogin }) => {
  const [isLoading, setIsLoading] = useState(false);
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    // Trigger animations after mount
    setTimeout(() => setMounted(true), 100);
  }, []);

  const handleLogin = () => {
    setIsLoading(true);
    // Small delay for animation, then redirect to OAuth
    setTimeout(() => {
      window.location.href = '/oauth2/authorization/gateway-client';
    }, 800);
  };

  return (
    <div className="login-container">
      {/* Animated Background */}
      <div className="login-bg">
        <div className="login-bg-gradient"></div>
        <div className="login-bg-pattern"></div>
        
        {/* Floating Elements */}
        <div className="floating-elements">
          <div className="floating-pill floating-pill-1">💊</div>
          <div className="floating-pill floating-pill-2">🏥</div>
          <div className="floating-pill floating-pill-3">💉</div>
          <div className="floating-pill floating-pill-4">🩺</div>
          <div className="floating-pill floating-pill-5">❤️</div>
          <div className="floating-pill floating-pill-6">🧬</div>
        </div>
      </div>

      {/* Login Card */}
      <div className={`login-card ${mounted ? 'login-card-mounted' : ''}`}>
        {/* Logo Section */}
        <div className={`login-logo ${mounted ? 'login-logo-mounted' : ''}`}>
          <div className="login-logo-icon">
            <span className="login-logo-letter">F</span>
            <div className="login-logo-pulse"></div>
          </div>
        </div>

        {/* Title Section */}
        <div className={`login-title ${mounted ? 'login-title-mounted' : ''}`}>
          <h1>FarmaciaSys</h1>
          <p>Sistema de Gestión de Cadena de Farmacias</p>
        </div>

        {/* Features */}
        <div className={`login-features ${mounted ? 'login-features-mounted' : ''}`}>
          <div className="login-feature">
            <span className="login-feature-icon">📊</span>
            <span>Dashboard en tiempo real</span>
          </div>
          <div className="login-feature">
            <span className="login-feature-icon">🏪</span>
            <span>Gestión de sucursales</span>
          </div>
          <div className="login-feature">
            <span className="login-feature-icon">📦</span>
            <span>Control de inventario</span>
          </div>
        </div>

        {/* Login Button */}
        <div className={`login-button-container ${mounted ? 'login-button-mounted' : ''}`}>
          <button
            onClick={handleLogin}
            disabled={isLoading}
            className={`login-button ${isLoading ? 'login-button-loading' : ''}`}
          >
            {isLoading ? (
              <div className="login-button-spinner">
                <div className="spinner"></div>
                <span>Conectando...</span>
              </div>
            ) : (
              <>
                <svg className="login-button-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4" />
                  <polyline points="10 17 15 12 10 7" />
                  <line x1="15" y1="12" x2="3" y2="12" />
                </svg>
                <span>Iniciar Sesión</span>
                <div className="login-button-shine"></div>
              </>
            )}
          </button>
        </div>

        {/* Security Badge */}
        <div className={`login-security ${mounted ? 'login-security-mounted' : ''}`}>
          <svg className="login-security-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
            <path d="M9 12l2 2 4-4" />
          </svg>
          <span>Conexión segura con OAuth 2.0</span>
        </div>

        {/* Footer */}
        <div className={`login-footer ${mounted ? 'login-footer-mounted' : ''}`}>
          <p>© 2026 FarmaciaSys - Todos los derechos reservados</p>
        </div>
      </div>

      {/* Decorative Side Panel */}
      <div className={`login-side-panel ${mounted ? 'login-side-panel-mounted' : ''}`}>
        <div className="login-side-content">
          <h2>Bienvenido a tu sistema de gestión farmacéutica</h2>
          <div className="login-side-stats">
            <div className="login-stat">
              <div className="login-stat-number">100+</div>
              <div className="login-stat-label">Sucursales</div>
            </div>
            <div className="login-stat">
              <div className="login-stat-number">5000+</div>
              <div className="login-stat-label">Productos</div>
            </div>
            <div className="login-stat">
              <div className="login-stat-number">24/7</div>
              <div className="login-stat-label">Soporte</div>
            </div>
          </div>
          <div className="login-side-quote">
            <blockquote>
              "La salud de nuestros clientes es nuestra prioridad"
            </blockquote>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
