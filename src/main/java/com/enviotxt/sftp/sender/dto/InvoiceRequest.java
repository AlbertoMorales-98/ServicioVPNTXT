package com.enviotxt.sftp.sender.dto;

import jakarta.validation.constraints.AssertTrue;

public record InvoiceRequest(
        String invoiceLine,
        String requestedFileName,
        String tipoRegistro,
        String moneda,
        String rfc,
        String regimenFiscal,
        String razonSocial,
        String email,
        String calle,
        String numeroExterior,
        String numeroInterior,
        String codigoPostal,
        String colonia,
        String municipio,
        String estado,
        String fechaFactura,
        Integer cantidad,
        Double importe,
        String concepto,
        String formaPago,
        String metodoPago,
        String usoCfdi,
        String folioOperacion
) {

    @AssertTrue(message = "Debes enviar invoiceLine o los campos estructurados de la factura.")
    public boolean isValidRequest() {
        if (hasText(invoiceLine)) {
            return true;
        }

        return hasText(tipoRegistro)
                && hasText(moneda)
                && hasText(rfc)
                && hasText(regimenFiscal)
                && hasText(razonSocial)
                && hasText(email)
                && hasText(calle)
                && hasText(numeroExterior)
                && hasText(numeroInterior)
                && hasText(codigoPostal)
                && colonia != null
                && municipio != null
                && estado != null
                && hasText(fechaFactura)
                && cantidad != null
                && importe != null
                && hasText(concepto)
                && hasText(formaPago)
                && hasText(metodoPago)
                && hasText(usoCfdi)
                && hasText(folioOperacion);
    }

    public String resolveTxtContent() {
        if (hasText(invoiceLine)) {
            return invoiceLine;
        }

        return String.join("|",
                safe(tipoRegistro),
                safe(moneda),
                safe(rfc),
                safe(regimenFiscal),
                safe(razonSocial),
                safe(email),
                safe(calle),
                safe(numeroInterior),
                safe(numeroExterior),
                safe(codigoPostal),
                safe(colonia),
                safe(municipio),
                safe(estado),
                safe(fechaFactura),
                cantidad == null ? "" : String.valueOf(cantidad),
                importe == null ? "" : "%.2f".formatted(importe),
                safe(concepto),
                safe(formaPago),
                safe(metodoPago),
                safe(usoCfdi),
                safe(folioOperacion)
        );
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
