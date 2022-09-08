package pl.futurecollars.invoice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Company {

  private String name;
  private String taxIdentificationNumber;
  private String address;

}
