import {browser, by, element, ElementArrayFinder, ElementFinder} from 'protractor';

export class CompanyPage {

  async navigateTo(): Promise<unknown> {
    return browser.get(browser.baseUrl);
  }

  async getTaxIdHeaderValue(): Promise<string> {
    return element(by.id('taxIdHeader')).getText();
  }

  async getAddressHeaderValue(): Promise<string> {
    return element(by.id('addressHeader')).getText();
  }

  async getNameHeaderValue(): Promise<string> {
    return element(by.id('nameHeader')).getText();
  }

  async getPensionInsuranceHeaderValue(): Promise<string> {
    return element(by.id('pensionInsuranceHeader')).getText();
  }

  async getHealthInsuranceHeaderValue(): Promise<string> {
    return element(by.id('healthInsuranceHeader')).getText();
  }

  companyRows(): ElementArrayFinder {
    return element.all(by.css('.companyRow'))
  }

  anyCompanyRow(): ElementFinder {
    return element(by.css('.companyRow'))
  }

  async addNewCompany(taxId: string, address: string, name: string, pensionInsurance: number, healthInsurance: number) {
    await this.taxIdInput().sendKeys(taxId)
    await this.addressInput().sendKeys(address)
    await this.nameInput().sendKeys(name)

    await this.pensionInsuranceInput().clear()
    await this.pensionInsuranceInput().sendKeys(pensionInsurance)

    await this.healthInsuranceInput().clear()
    await this.healthInsuranceInput().sendKeys(healthInsurance)

    await element(by.id("addCompanyBtn")).click()
  }

  private taxIdInput() {
    return element(by.css('input[name=taxIdentificationNumber]'));
  }

  private addressInput() {
    return element(by.css('input[name=address]'));
  }

  private nameInput() {
    return element(by.css('input[name=name]'));
  }

  private healthInsuranceInput() {
    return element(by.css('input[name=healthInsurance]'));
  }

  private pensionInsuranceInput() {
    return element(by.css('input[name=pensionInsurance]'));
  }
}
