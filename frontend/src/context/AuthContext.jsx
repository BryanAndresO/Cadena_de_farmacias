import { createContext, useContext, useState, useEffect } from 'react';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
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

  const logout = async () => {
    // Clear user state immediately
    setUser(null);
    
    // Use frontend auth service to call backend logout
    try {
      const { logout: authLogout } = await import('../services/auth');
      await authLogout();
    } catch (err) {
      console.error('Error during logout', err);
      window.location.href = '/logout';
    }
  };

  // Check if user has a specific role
  const hasRole = (role) => {
    if (!user || !user.roles) return false;
    // Support both "ROLE_ADMIN" and "ADMIN" formats
    const normalizedRole = role.startsWith('ROLE_') ? role : `ROLE_${role}`;
    return user.roles.includes(normalizedRole);
  };

  // Check if user is admin
  const isAdmin = () => hasRole('ADMIN');

  // Check if user can perform an action based on the permission matrix
  const canPerform = (action) => {
    if (isAdmin()) return true;
    
    // ROLE_USER permissions
    switch (action) {
      case 'read':
      case 'create':
        return true;
      case 'update':
      case 'delete':
      case 'reports':
      case 'administration':
        return false;
      default:
        return false;
    }
  };

  return (
    <AuthContext.Provider value={{ 
      user, 
      loading, 
      logout, 
      hasRole, 
      isAdmin, 
      canPerform 
    }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
