import axios from 'axios';

const API_BASE = window.location.hostname === 'localhost' ? 'http://localhost:8080' : '';

// PKCE helpers
function base64urlencode(str) {
  return btoa(String.fromCharCode.apply(null, new Uint8Array(str)))
    .replace(/\+/g, '-')
    .replace(/\//g, '_')
    .replace(/=+$/, '');
}

async function sha256(plain) {
  const encoder = new TextEncoder();
  const data = encoder.encode(plain);
  const hash = await window.crypto.subtle.digest('SHA-256', data);
  return new Uint8Array(hash);
}

async function generateCodeChallenge(verifier) {
  const hashed = await sha256(verifier);
  return base64urlencode(hashed);
}

function randomString(length = 43) {
  const array = new Uint8Array(length);
  window.crypto.getRandomValues(array);
  return Array.from(array).map(b => String.fromCharCode(b % 26 + 65)).join('');
}

export async function login() {
  const codeVerifier = randomString(64);
  const codeChallenge = await generateCodeChallenge(codeVerifier);
  sessionStorage.setItem('pkce_code_verifier', codeVerifier);

  const params = new URLSearchParams({
    response_type: 'code',
    client_id: 'spa-client',
    scope: 'openid profile read write',
    redirect_uri: window.location.origin + '/oauth2/callback',
    code_challenge: codeChallenge,
    code_challenge_method: 'S256'
  });

  // Redirect to authorization endpoint proxied via Gateway
  window.location.href = API_BASE + '/oauth2/authorize?' + params.toString();
}

export async function handleCallback() {
  const params = new URLSearchParams(window.location.search);
  const code = params.get('code');
  if (!code) throw new Error('No code in callback');

  const codeVerifier = sessionStorage.getItem('pkce_code_verifier');
  if (!codeVerifier) throw new Error('Missing PKCE code verifier');

  const tokenUrl = API_BASE + '/oauth2/token';

  const bodyParams = new URLSearchParams();
  bodyParams.append('grant_type', 'authorization_code');
  bodyParams.append('code', code);
  bodyParams.append('redirect_uri', window.location.origin + '/oauth2/callback');
  bodyParams.append('client_id', 'spa-client');
  bodyParams.append('code_verifier', codeVerifier);

  const resp = await axios.post(tokenUrl, bodyParams.toString(), {
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
  });

  const token = resp.data;
  // store access token for API calls (simple approach)
  localStorage.setItem('access_token', token.access_token);
  if (token.refresh_token) localStorage.setItem('refresh_token', token.refresh_token);
  return token;
}

export async function logout() {
  try {
    // Call the backend logout endpoint to get the OAuth2 provider logout URL
    // This will also invalidate the gateway session
    const resp = await axios.post(API_BASE + '/api/logout', {}, { withCredentials: true });

    // Clear all local storage
    localStorage.clear();
    sessionStorage.clear();

    // Get the logout URL from the response (includes id_token_hint for proper provider logout)
    const logoutUrl = resp?.data?.logoutUrl || '/oauth2/authorization/gateway-client';

    // Redirect to the OAuth2 provider logout endpoint
    window.location.href = logoutUrl;
  } catch (err) {
    console.error('Logout failed, redirecting to login', err);
    localStorage.clear();
    sessionStorage.clear();
    // Fallback: redirect to login
    window.location.href = '/oauth2/authorization/gateway-client';
  }
}
