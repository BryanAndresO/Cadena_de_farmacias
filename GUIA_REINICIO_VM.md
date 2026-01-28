# 🔄 Guía: Después de Apagar/Prender tu VM

**⚠️ PROBLEMA**: Google Cloud asigna una **IP nueva** cada vez que **APAGAS y PRENDES** tu VM (Stop/Start desde la consola).

---

## 📋 Pasos Cuando Prendes tu VM (3 Opciones)

### ✅ Opción A: Manual Rápido (5 minutos)

```bash
# 1. Conectarte a tu VM por SSH desde Google Cloud Console

# 2. Ver tu nueva IP
curl ifconfig.me
# Ejemplo de salida: 34.123.45.67

# 3. Ir al proyecto
cd Cadena_de_farmacias

# 4. Editar el .env
nano .env

# 5. Cambiar TODAS las IPs por la nueva:
#    - PUBLIC_HOST=34.123.45.67
#    - GATEWAY_PUBLIC_URL=http://34.123.45.67:8080
#    - OAUTH_PUBLIC_URL=http://34.123.45.67:9000
#    - FRONTEND_PUBLIC_URL=http://34.123.45.67:8080
#    - PUBLIC_BASE_URL=http://34.123.45.67:8080
#    - OAUTH_ISSUER_URI=http://34.123.45.67:9000
#    - OAUTH2_CLIENT_GATEWAY_BASE_URL=http://34.123.45.67:8080
#    - OAUTH2_CLIENT_FRONTEND_BASE_URL=http://34.123.45.67:8080
#    - OAUTH2_CLIENT_PUBLIC_BASE_URL=http://34.123.45.67:8080

# 6. Guardar y salir
# Presiona: Ctrl+O → Enter → Ctrl+X

# 7. Reiniciar contenedores
docker compose down
docker compose up -d

# 8. Acceder con la nueva IP
# http://34.123.45.67:8080
```

---

### ⚡ Opción B: Script Automático (2 minutos)

```bash
# 1. Conectarte a tu VM
cd Cadena_de_farmacias

# 2. Ejecutar el script (detecta la IP automáticamente)
chmod +x setup-vm.sh
./setup-vm.sh

# 3. Reiniciar contenedores
docker compose down
docker compose up -d

# 4. La nueva IP se muestra al final, accede con ella
```

---

### 💎 Opción C: IP Estática (Solución Permanente)

**Costo**: ~$3 USD/mes  
**Ventaja**: Tu IP **NUNCA cambia**, no necesitas reconfigurar nada.

#### Pasos:

1. Abre **Google Cloud Console**: https://console.cloud.google.com
2. Ve a: **☰ → VPC Network → External IP addresses**
3. Busca la fila de tu VM `vm-linuxa`
4. En la columna **"Type"**, haz click en **"Ephemeral"**
5. Selecciona **"Static"** en el menú
6. Dale un nombre: `pharmacy-app-static-ip`
7. Click **RESERVE**

✅ **Listo**, tu IP es ahora **permanente**.

---

## 🚨 ¿Qué pasa si solo REINICIAS la VM?

**Reiniciar** (desde el sistema operativo con `sudo reboot`) ≠ **Apagar/Prender**

- **`sudo reboot`**: La IP se **mantiene** ✅
- **Stop/Start desde Google Console**: La IP **cambia** ❌

Si solo reinicias, Docker arranca automáticamente y todo funciona sin cambios.

---

## 📝 Resumen

| Acción | ¿Cambia IP? | ¿Qué hacer? |
|--------|-------------|-------------|
| `sudo reboot` | ❌ No | Nada, espera a que arranque |
| Stop/Start (Console) | ✅ Sí | Opción A, B o C |
| IP Estática reservada | ❌ Nunca | Nada, funciona siempre |

---

## 💡 Recomendación

**Para pruebas/desarrollo**: Usa Opción B (script automático)  
**Para producción**: Usa Opción C (IP estática)
