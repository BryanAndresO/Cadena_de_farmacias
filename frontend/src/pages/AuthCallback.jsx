import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { handleCallback } from '../services/auth';

export default function AuthCallback() {
  const navigate = useNavigate();

  useEffect(() => {
    (async () => {
      try {
        await handleCallback();
        // token stored; redirect to home
        navigate('/');
      } catch (err) {
        console.error('Auth callback error', err);
        navigate('/');
      }
    })();
  }, [navigate]);

  return <div>Autenticando...</div>;
}
