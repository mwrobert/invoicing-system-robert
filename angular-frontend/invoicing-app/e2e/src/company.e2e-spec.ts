import {browser, ExpectedConditions, logging} from 'protractor';
import {CompanyPage} from './company.po';
import {CompanyRow} from "./companyRow.po";

describe('Company page E2E test', () => {
  let page: CompanyPage;

  beforeEach(async () => {
    page = new CompanyPage();
    await page.navigateTo();

    await page.companyRows()
      .each(async (row) => {
        let companyRow = new CompanyRow(row);
        await companyRow.deleteBtn().click()
      })

    browser.wait(ExpectedConditions.not(ExpectedConditions.presenceOf(page.anyCompanyRow())));
    expect(await page.companyRows()).toEqual([])
  });

  it('should display correct values for table headers', async () => {
    expect(await page.getTaxIdHeaderValue()).toEqual('Tax identification number');
    expect(await page.getAddressHeaderValue()).toEqual('Address');
    expect(await page.getNameHeaderValue()).toEqual('Name');
    expect(await page.getPensionInsuranceHeaderValue()).toEqual('Pension insurance');
    expect(await page.getHealthInsuranceHeaderValue()).toEqual('Health insurance');
  });

  it('should be able to add company', async () => {
    await page.addNewCompany("111-222-333", "Ul. Sławkowska 123", "Optimus Bis", 1555, 450)

    await page.companyRows().then(async rows => {
      expect(rows.length).toEqual(1);
      await new CompanyRow(rows[0]).assertRowValues("111-222-333", "Ul. Sławkowska 123", "Optimus Bis", "1555", "450")
    })

  });

  it('should be able to delete company', async () => {
    await page.addNewCompany("111-222-333", "Ul. Sławkowska 123", "Optimus Bis", 1555, 450)
    await page.addNewCompany("333-222-111", "Ul. Wrocławska 56", "New Vision", 2111, 567)

    await page.companyRows().then(async rowsBeforeDelete => {
      expect(rowsBeforeDelete.length).toEqual(2);
      await new CompanyRow(rowsBeforeDelete[0]).deleteBtn().click()

      await page.companyRows().then(async rowsAfterDelete => {
        expect(rowsAfterDelete.length).toEqual(1);
        await new CompanyRow(rowsAfterDelete[0]).assertRowValues("333-222-111", "Ul. Wrocławska 56", "New Vision", "2111", "567")
      });
    })
  });

  it('should be able to update company', async () => {
    await page.addNewCompany("111-222-333", "Ul. Sławkowska 123", "Optimus Bis", 1555, 450)

    await page.companyRows().then(async rows => {
      const companyRow = new CompanyRow(rows[0]);
      await companyRow.updateCompany("333-222-111", "Ul. Wrocławska 56", "New Vision", 2111, 567)
      browser.wait(ExpectedConditions.not(ExpectedConditions.presenceOf(page.anyCompanyRow())));
      await companyRow.assertRowValues("333-222-111", "Ul. Wrocławska 56", "New Vision", "2111", "567")
    })
  });

  afterEach(async () => {
    const logs = await browser.manage().logs().get(logging.Type.BROWSER);
    expect(logs).not.toContain(jasmine.objectContaining({
      level: logging.Level.SEVERE,
    } as logging.Entry));
  });
});
