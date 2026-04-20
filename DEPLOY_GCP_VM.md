# Despliegue en Google Cloud VM

Esta VM ya tiene una IP pública y, por la configuración que compartiste, tiene abierto el puerto `80`, `443` y `22`.

Eso significa que la forma más simple de publicar este servicio es:

- dejar la aplicación Spring dentro del contenedor en `8081`
- mapear el puerto `80` de la VM al `8081` del contenedor
- mantener `mock-sftp` solo para uso interno en la red de Docker

Con eso, la API quedará accesible en:

```text
http://34.51.32.104/api/sftp/invoices/sample
http://34.51.32.104/api/sftp/invoices/send
```

## 1. Conectarte por SSH

Desde tu equipo:

```bash
ssh leonardo_esteban@34.51.32.104
```

Si usas otro usuario en la VM, reemplázalo por el correcto.

## 2. Instalar Docker en la VM

Dentro de la VM:

```bash
sudo apt update
sudo apt install -y ca-certificates curl gnupg
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/debian/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/debian \
  $(. /etc/os-release && echo $VERSION_CODENAME) stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
sudo systemctl enable docker
sudo systemctl start docker
sudo usermod -aG docker $USER
```

Luego cierra la sesión y vuelve a entrar por SSH para que aplique el grupo `docker`.

## 3. Subir el proyecto a la VM

Opción A, con `git`:

```bash
git clone <URL_DE_TU_REPO>
cd ServicioVPNTXT
```

Opción B, copiando el proyecto desde tu máquina:

```bash
scp -r /ruta/local/ServicioVPNTXT leonardo_esteban@34.51.32.104:~
```

Y luego en la VM:

```bash
cd ~/ServicioVPNTXT
```

## 4. Levantar los contenedores

Usa el compose base más el override para VM:

```bash
docker compose -f docker-compose.yml -f docker-compose.vm.yml up --build -d
```

## 5. Verificar que quedó arriba

```bash
docker compose -f docker-compose.yml -f docker-compose.vm.yml ps
docker compose -f docker-compose.yml -f docker-compose.vm.yml logs -f
```

## 6. Probar desde navegador o Postman

### Endpoint de ejemplo

```bash
curl http://34.51.32.104/api/sftp/invoices/sample
```

### Envío de factura

```bash
curl -X POST http://34.51.32.104/api/sftp/invoices/send \
  -H "Content-Type: application/json" \
  -d '{
    "requestedFileName": "factura-sftp-demo.txt",
    "tipoRegistro": "1",
    "moneda": "MXN",
    "rfc": "BPR110323JM5",
    "regimenFiscal": "GENERAL DE LEY PERSONAS MORALES",
    "razonSocial": "BUSINESS PREY",
    "email": "jonathan09m@hotmail.com",
    "calle": "ALFONSO REYES",
    "numeroExterior": "L003",
    "numeroInterior": "216",
    "codigoPostal": "06100",
    "colonia": "Hipodromo",
    "municipio": "",
    "estado": "",
    "fechaFactura": "2026-03-25",
    "cantidad": 1,
    "importe": 1.00,
    "concepto": "Expedicion de Tarjeta de Identificacion Aeroportuaria ABA4C2D720",
    "formaPago": "04",
    "metodoPago": "PUE",
    "usoCfdi": "G03",
    "folioOperacion": "20260325192912382800"
  }'
```

## 7. Dónde quedan los archivos

En la VM, dentro del proyecto:

- `storage/sender-outbox`
- `storage/mock-sftp-root`
- `storage/keys`

## 8. Reiniciar o bajar el servicio

Reiniciar:

```bash
docker compose -f docker-compose.yml -f docker-compose.vm.yml restart
```

Bajarlo:

```bash
docker compose -f docker-compose.yml -f docker-compose.vm.yml down
```

## 9. Observaciones importantes

- Tu firewall actual no muestra apertura para `8081`, así que mapear el servicio a `80` en la VM es la mejor opción.
- El `mock-sftp` no se expone públicamente en `docker-compose.vm.yml`; solo lo usa `sender` dentro de Docker.
- Si después quieres usar un servidor SFTP real, puedes cambiar las variables del servicio `sender` en `docker-compose.yml` y prescindir del `mock-sftp`.
