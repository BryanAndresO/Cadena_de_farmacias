export function sanitizeErrorMessage(raw) {
  if (!raw) return 'Ocurrió un error. Intente nuevamente más tarde.';
  let m = String(raw);
  // Replace internal microservice names like micro-catalogo
  m = m.replace(/micro-[\w-]+/gi, 'servicio externo');
  // Remove URLs
  m = m.replace(/https?:\/\/[^\s]+/gi, '');
  // Remove internal hostnames like localhost:1234
  m = m.replace(/\blocalhost(:\d+)?\b/gi, '');
  // Remove potential stack traces or JSON braces
  m = m.replace(/[{}\[\]]/g, '');
  // Trim and limit length
  m = m.trim();
  if (m.length === 0) return 'Ocurrió un error. Intente nuevamente más tarde.';
  if (m.length > 250) m = m.substring(0, 250) + '...';
  return m;
}

export function extractApiMessage(err) {
  // If backend returns standard ApiError, prefer its message
  try {
    const data = err?.response?.data;
    if (data && data.message) return sanitizeErrorMessage(data.message);
  } catch (e) {
    // ignore
  }
  return sanitizeErrorMessage(err?.response?.data?.message || err?.message || err);
}
