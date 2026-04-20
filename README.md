# Envio TXT por SFTP

Proyecto Spring Boot con dos aplicaciones:

- `sender`: expone una API HTTP para generar un archivo `.txt` y subirlo por SFTP.
- `mock-sftp`: levanta un servidor SFTP embebido para pruebas locales.

La forma recomendada de probarlo en este repo es con Docker Compose.

## Requisitos

- Docker
- Docker Compose

## Servicios

### `sender`

- Puerto local: `8081`
- Endpoint base: `http://localhost:8081`

Endpoints:

- `GET /api/sftp/invoices/sample`
- `POST /api/sftp/invoices/send`

### `mock-sftp`

- Host local: `localhost`
- Puerto local: `2222`
- Usuario: `cliente_sftp`
- Password: `cambio123`

## Levantar el proyecto

Desde la raíz del repo:

```bash
docker compose up --build
```

Si quieres dejarlo en segundo plano:

```bash
docker compose up --build -d
```

## Detenerlo

```bash
docker compose down
```

## Ver logs

Todos los servicios:

```bash
docker compose logs -f
```

Solo el emisor:

```bash
docker compose logs -f sender
```

Solo el servidor SFTP:

```bash
docker compose logs -f mock-sftp
```

## Probar la API

### 1. Obtener payload de ejemplo

```bash
curl http://localhost:8081/api/sftp/invoices/sample
```

### 2. Enviar una factura estructurada

```bash
curl -X POST http://localhost:8081/api/sftp/invoices/send \
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

### 3. Enviar una línea TXT ya construida

```bash
curl -X POST http://localhost:8081/api/sftp/invoices/send \
  -H "Content-Type: application/json" \
  -d '{
    "requestedFileName": "factura-directa.txt",
    "invoiceLine": "1|MXN|BPR110323JM5|GENERAL DE LEY PERSONAS MORALES|BUSINESS PREY|jonathan09m@hotmail.com|ALFONSO REYES|216|L003|06100|Hipodromo|||2026-03-25|1|1.00|Expedicion de Tarjeta de Identificacion Aeroportuaria ABA4C2D720|04|PUE|G03|20260325192912382800"
  }'
```

## Resultado esperado

Cuando el envío es exitoso:

- el archivo local queda en `storage/sender-outbox`
- el archivo remoto queda en `storage/mock-sftp-root/Datos de facturacion TXT`

## Configuración Docker actual

En `docker-compose.yml`:

- `sender` se conecta a `mock-sftp:2222`
- el directorio remoto configurado es `/Datos de facturacion TXT`
- las carpetas `storage/sender-outbox`, `storage/mock-sftp-root` y `storage/keys` se montan como volúmenes locales

## Archivos Docker

- `Dockerfile.sender`
- `Dockerfile.mock-sftp`
- `docker-compose.yml`

## Notas

- El cliente SFTP actual acepta cualquier host key. Eso facilita pruebas locales, pero no es adecuado para producción.
- Las credenciales del ejemplo están pensadas para entorno local de prueba.
- Si cambias puertos, usuarios o rutas, actualízalos también en `docker-compose.yml`.
