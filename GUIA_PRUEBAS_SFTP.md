# Guia de pruebas SFTP

Este proyecto contiene dos aplicaciones:

- Emisor SFTP: genera el archivo `.txt`, lo guarda localmente y lo sube por SFTP.
- Servidor SFTP simulado: representa al servidor del cliente y recibe el archivo en una carpeta remota.

## 1. Requisitos

- Java 21
- IntelliJ IDEA
- Maven importado por IntelliJ

## 2. Abrir el proyecto en IntelliJ

1. Abre IntelliJ IDEA.
2. Selecciona `Open`.
3. Elige la carpeta [EnvioTXTArchivoSftp](/home/alberto/Documentos/EnvioTXTArchivo/EnvioTXTArchivoSftp).
4. Espera a que IntelliJ descargue dependencias desde el `pom.xml`.

## 3. Aplicaciones a ejecutar

### Servidor SFTP del cliente simulado

- Main class: `com.enviotxt.sftp.mockserver.SftpMockServerApplication`

Este servidor levanta SFTP en `localhost:2222`.

### Emisor SFTP

- Main class: `com.enviotxt.sftp.sender.SftpSenderApplication`

Este servidor HTTP levanta en `http://localhost:8081`.

## 4. Credenciales SFTP de prueba

- Host: `localhost`
- Puerto: `2222`
- Usuario: `cliente_sftp`
- Password: `cambio123`

Puedes cambiarlas en [application-sender-sftp.yml](/home/alberto/Documentos/EnvioTXTArchivo/EnvioTXTArchivoSftp/src/main/resources/application-sender-sftp.yml) y [application-mock-sftp.yml](/home/alberto/Documentos/EnvioTXTArchivo/EnvioTXTArchivoSftp/src/main/resources/application-mock-sftp.yml).

## 5. Endpoint para enviar el TXT

- `GET http://localhost:8081/api/sftp/invoices/sample`
- `POST http://localhost:8081/api/sftp/invoices/send`

Body JSON de ejemplo:

```json
{
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
}
```

Ejemplo con `curl`:

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

## 6. Resultado esperado

- El archivo se genera localmente en `storage/sender-outbox`.
- Luego se sube por SFTP al directorio remoto `/Datos de facturacion TXT`.
- En el servidor simulado, físicamente quedará dentro de `storage/mock-sftp-root/Datos de facturacion TXT`.

## 7. Formato del TXT

El archivo se genera en una sola linea, con los campos separados por `|`:

```txt
1|MXN|BPR110323JM5|GENERAL DE LEY PERSONAS MORALES|BUSINESS PREY|jonathan09m@hotmail.com|ALFONSO REYES|216|L003|06100|Hipodromo|||2026-03-25|1|1.00|Expedicion de Tarjeta de Identificacion Aeroportuaria ABA4C2D720|04|PUE|G03|20260325192912382800
```

## 8. Ajuste para ambiente real

Cuando tu cliente entregue sus datos reales, cambia:

- host
- puerto
- usuario
- password
- ruta remota

en [application-sender-sftp.yml](/home/alberto/Documentos/EnvioTXTArchivo/EnvioTXTArchivoSftp/src/main/resources/application-sender-sftp.yml).

## 9. Nota de seguridad

Para esta demo local el cliente SFTP acepta cualquier host key usando un verificador permisivo. Eso se hizo solo para facilitar pruebas locales. En produccion debes validar la host key real del servidor del cliente.
