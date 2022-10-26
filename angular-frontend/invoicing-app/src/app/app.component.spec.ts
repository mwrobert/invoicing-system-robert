import { TestBed, ComponentFixture } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { FormsModule } from '@angular/forms';
import { Company } from './company';
import { CompanyService } from './company.service';
import { Observable, of } from 'rxjs';
import { OnInit } from '@angular/core';

describe('AppComponent', () => {
  let fixture: ComponentFixture<AppComponent>;
  let component: AppComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        { provide: CompanyService, useClass: MockCompanyService }
      ],
      declarations: [
        AppComponent
      ],
      imports: [
        FormsModule
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;

    component.ngOnInit()
    fixture.detectChanges();
  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  it(`should have as title 'invoicing-app'`, () => {
    expect(component.title).toEqual('InvoicingApp');
  });

  it('should display list of companies', () => {
    expect(fixture.nativeElement.innerText).toContain("111-111-111	First Street 1	First Company	1000.11	100.11");
    expect(fixture.nativeElement.innerText).toContain("222-222-222	Second Street 2	Second Company	2000.22	200.22");
    expect(component.companies).toEqual(MockCompanyService.companies)
  });

  it('newly added company is added to the list', () => {
    const taxIdInput: HTMLInputElement = fixture.nativeElement.querySelector("input[name=taxIdentificationNumber]")
    taxIdInput.value = "333-333-333"
    taxIdInput.dispatchEvent(new Event('input'));

    const addressInput: HTMLInputElement = fixture.nativeElement.querySelector("input[name=address]")
    addressInput.value = "Third Street 3"
    addressInput.dispatchEvent(new Event('input'));

    const nameInput: HTMLInputElement = fixture.nativeElement.querySelector("input[name=name]")
    nameInput.value = "Third Company"
    nameInput.dispatchEvent(new Event('input'));

    const pensionInsuranceInput: HTMLInputElement = fixture.nativeElement.querySelector("input[name=pensionInsurance]")
    pensionInsuranceInput.value = "3000.33"
    pensionInsuranceInput.dispatchEvent(new Event('input'));

    const healthInsuranceInput: HTMLInputElement = fixture.nativeElement.querySelector("input[name=healthInsurance]")
    healthInsuranceInput.value = "300.33"
    healthInsuranceInput.dispatchEvent(new Event('input'));

    const addCompanyBtn: HTMLElement = fixture.nativeElement.querySelector("#addCompanyBtn")
    addCompanyBtn.click()

    fixture.detectChanges();
    expect(component.companies.length).toEqual(3);
    expect(fixture.nativeElement.innerText).toContain("333-333-333	Third Street 3	Third Company	3000.33	300.33")
  });



  class MockCompanyService {

    static companies: Company[] = [
      new Company(
        1,
        "111-111-111",
        "First Street 1",
        "First Company",
        1000.11,
        100.11
      ),
      new Company(
        2,
        "222-222-222",
        "Second Street 2",
        "Second Company",
        2000.22,
        200.22
      ),
    ];

    getCompanies(): Observable<Company[]> {
      return of(MockCompanyService.companies)
    }

    addCompany(company: Company) {
      MockCompanyService.companies.push(company)
      return of()
    }
  }

});
