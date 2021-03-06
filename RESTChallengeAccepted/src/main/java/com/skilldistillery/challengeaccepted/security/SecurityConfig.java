package com.skilldistillery.challengeaccepted.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

// this you get for free when you configure the db connection in application.properties file
@Autowired
private DataSource dataSource;

// this bean is created in the application starter class if you're looking for it
@Autowired
private PasswordEncoder encoder;

@Override
protected void configure(HttpSecurity http) throws Exception {
        http
        .csrf().disable()
        .authorizeRequests()
        .antMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()  // For CORS, the preflight request will hit the OPTIONS on the route
        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        .antMatchers(HttpMethod.GET, "/").permitAll()
        .antMatchers(HttpMethod.GET, "/index.html").permitAll()
        .antMatchers(HttpMethod.GET, "/**").permitAll()
//        .antMatchers(HttpMethod.DELETE, "/api/users").permitAll()
        .antMatchers(HttpMethod.GET, "/api/challenges").permitAll()
//        challenges/status/{sid}
        .antMatchers(HttpMethod.GET, "/api/challenges/status/**").permitAll()
        .antMatchers(HttpMethod.GET, "/api/challenges/**").permitAll()
        .antMatchers(HttpMethod.GET, "/api/userskills/indexByPoints").permitAll()
        .antMatchers(HttpMethod.GET, "/api/skills").permitAll()
        .antMatchers(HttpMethod.GET, "/assets/**").permitAll()
        .antMatchers(HttpMethod.GET, "/api/tags").permitAll()
        .antMatchers("/login").permitAll()
        .antMatchers("/register").permitAll()
        .anyRequest().authenticated()
        .and()
        .httpBasic();

        http
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  }

@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    String userQuery = "SELECT username, password, enabled FROM User WHERE username=?";
    String authQuery = "SELECT username, role FROM User WHERE username=?";
    auth
      .jdbcAuthentication()
      .dataSource(dataSource)
      .usersByUsernameQuery(userQuery)
      .authoritiesByUsernameQuery(authQuery)
      .passwordEncoder(encoder);
  }
}