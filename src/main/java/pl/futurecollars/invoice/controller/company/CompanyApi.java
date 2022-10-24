package pl.futurecollars.invoice.controller.company;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.futurecollars.invoice.model.Company;

@RequestMapping(value = "/companies", produces = {"application/json;charset=UTF-8"})
@Api(tags = {"company-controller"})
public interface CompanyApi {

  @ApiOperation(value = "Get company with given id")
  @GetMapping(value = "/{id}")
  ResponseEntity<Company> getCompany(@PathVariable long id);

  @ApiOperation(value = "Get list of all companies")
  @GetMapping
  List<Company> getAll();

  @ApiOperation(value = "Add new company to system")
  @PostMapping
  long saveCompany(@RequestBody Company company);

  @ApiOperation(value = "Delete company with given id")
  @DeleteMapping(value = "/{id}")
  ResponseEntity<?> deleteCompany(@PathVariable long id);

  @ApiOperation(value = "Update company with given id")
  @PutMapping(value = "/{id}")
  ResponseEntity<?> updateCompany(@RequestBody Company company, @PathVariable long id);
}
