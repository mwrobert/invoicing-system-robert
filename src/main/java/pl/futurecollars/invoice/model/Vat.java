package pl.futurecollars.invoice.model;

import java.math.BigDecimal;

public enum Vat {

  Vat_23(23),
  Vat_8(8),
  Vat_7(7),
  Vat_5(5),
  Vat_0(0),
  Vat_ZW(0);

  private final BigDecimal vatRate;

  Vat(int vatRate) {
    this.vatRate = BigDecimal.valueOf(vatRate);
  }

  public BigDecimal getRate() {
    return this.vatRate;
  }

}
