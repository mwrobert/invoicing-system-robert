package pl.futurecollars.invoice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration

public class SwaggerConfiguration {

  @Bean
  public Docket docket() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.basePackage("pl.futurecollars"))
        .paths(PathSelectors.any())
        .build()
        .tags(
            new Tag("invoice-controller", "Controller used to list / add / update / delete invoices.")
        )
        .apiInfo(
            new ApiInfoBuilder()
                .description("Application to manage set of invoices")
                .license("No license available - private!")
                .title("Private Invoicing")
                .contact(
                    new Contact(
                        "Szkolenie",
                        "https://github.com/JoannaBiegala/invoicing-system-joanna-biegala",
                        "hello@futurecollars.com"
                    )
                )
                .build()
        );
  }

}
