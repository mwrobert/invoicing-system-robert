package pl.futurecollars.invoice.configuration.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import pl.futurecollars.invoice.configuration.security.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired
  private CorsFilter corsFilter;

  @Value("${csrf.disable}")
  private boolean disableCsrf;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
        .antMatchers("/v2/api-docs", "/swagger-resources", "/swagger-ui.html", "/swagger-ui/**", "/swagger*/**")
        .permitAll()
        .anyRequest()
        .authenticated().and()
        .httpBasic()
        .and()
        .addFilterBefore(corsFilter, ChannelProcessingFilter.class);

    if (disableCsrf) {
      http.csrf().disable();
    } else {
      http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
    }
  }
}
