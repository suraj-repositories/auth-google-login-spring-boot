# auth-google-login-spring-boot
Login with google implementation using spring boot

under development...

## Steps - 

- Step 1 : First you need a simple login system - I suggest you to take reference from [Role based login](https://github.com/suraj-repositories/auth-spring-security-3)

- Step 2 : Add dependency for oauth2client and spring security on your project 

```xml
		 <dependency>
	      <groupId>org.springframework.boot</groupId>
	      <artifactId>spring-boot-starter-oauth2-client</artifactId>
	     </dependency>
	     
	     <dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
		
		<dependency>
			<groupId>org.thymeleaf.extras</groupId>
			<artifactId>thymeleaf-extras-springsecurity6</artifactId>
		</dependency>

```


- Step 3 : Now you need to create credentials on google cloud (steps may be changed in near feature)

    - on your browser search for 'google cloud' and visit to the website 
    - login yourself on the google cloud (don't worry this is free)
    - when you logged in the - on the top right corner you can see the 'console' Button - click on that button
    - on the top navbar click on "select a project" selector -> New Project -> fill the project name -> location may be empty -> click on create
    - once the project is created -> select the project from the selector 'select a project' (or it may be selected already)
    - On the sidebar go to API'S and services -> Credentials -> CONFIGURE CONCENT SCREEN -> select External radio btn -> click on create
    - In the 'Edit app registration' there are four section -
        - section 1 : Oauth consent screen
            - fill appname
            - fill user support email
            - fill developer conatact email
            - you can leave other fields empty if you want
            - click on save and continue
        - you can leave other sections empty
    - now go to the credentials on sidebar -> click on create credentials -> Oauth-client-id -> select the application type -> in our case the application type in web application
        - now it need some information
        - first is app name 
        - Authorized javascript origins : here you need to fill the url address or your website in my case it is http://localhost:8000
        - Authorized redirect URIs: here you need to fill the url where you want to send your user when login is successful in my case this is : http://localhost:8080/login/oauth2/code/google
        - click on create 
    - It will create client id and client secret for you -> You need to paste the client id and client-secret into your application.yml file (you need to create application.yml file as the same path on application.properties like : `/auth-spring-security-3/src/main/resources/application.yml`)
    
```bash
appUrl : http://localhost:8080/

spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: -----------------------------------YOUR_CLIENT_ID
            client-secret: --------------------------------YOUR_CLIENT_SECRET
            redirect-uri: "${appUrl}/login/oauth2/code/google"
            scope:
              - openid
              - profile
              - email
            client-name: Google
```

- Step 4 : you need to configure the oauth2client for google login : 

```java
package com.on14june.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
public class AuthConfig {
	
	@Autowired
	private UserDetailsService detailsService;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

		httpSecurity.csrf(Customizer.withDefaults())
					.authorizeHttpRequests(request -> request
								.requestMatchers("/admin/**")
								.hasRole("ADMIN")
								.requestMatchers("/user/**")
								.hasAnyRole("USER", "ADMIN")
								.requestMatchers("/**")
								.permitAll()
								.anyRequest()
								.authenticated())
					.formLogin(form -> form
								.loginPage("/login")
								.loginProcessingUrl("/login")
								.usernameParameter("email")
								.passwordParameter("password")
								.defaultSuccessUrl("/")
								.permitAll())
					.oauth2Login(form -> form
							.loginPage("/login")
							.defaultSuccessUrl("/login/google")        // we can create the custom controller for that URL
							.failureHandler(new SimpleUrlAuthenticationFailureHandler()))
					.logout(logout -> logout
							.logoutSuccessUrl("/login?logout")
							.permitAll()
							);

		return httpSecurity.build();

	}

    @Bean
    static PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(detailsService).passwordEncoder(passwordEncoder());
	}
	
}

```

- Step 5 : On your google login page you can use the given link to redirect to the google official login page

```html
	<a th:href="@{/oauth2/authorization/google}">Login with Google</a>
```


- Step 6 : the next step is to create controller methods to handle google login : 

```java
@GetMapping("/")
	public String home(Model model, Authentication authentication, HttpServletRequest request, HttpServletResponse response){
		if (authentication != null) {
			User user = service.getUserByEmail(authentication.getName());
			if (user == null) {
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				if (auth != null) {
					new SecurityContextLogoutHandler().logout(request, response, auth);
				}
			}
			model.addAttribute("user", user);
		}
		return "welcome";
	}
```

```java
	@GetMapping("/login/google")
	public String loginWithGoogle(OAuth2AuthenticationToken authentication) {
		try {
			Map<String, Object> attributes = authentication.getPrincipal().getAttributes();

			String email = (String) attributes.get("email");
			String name = (String) attributes.get("name");
			String picture = (String) attributes.get("picture");

			LOGGER.info("{} - {} - {}", email, name, picture);
			User user;

			user = service.getUserByEmail(email);

			if (user == null) {
				String unique = UUID.randomUUID().toString();

				LOGGER.info("{}", unique);
				User loginUser = User.builder().name(name).email(email).picture(picture).password(unique)
						.confirmPassword(unique).dob(null).role("USER").id(null).build();

				user = service.saveUser(loginUser);
			}

			Authentication auth = new UsernamePasswordAuthenticationToken(email, null,
					Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole())));

			SecurityContextHolder.getContext().setAuthentication(auth);

		} catch (Exception e) {
			LOGGER.error("Error while login with Google: " + e.getMessage());
			return "redirect:/error";
		}
		return "redirect:/";
	}


```
