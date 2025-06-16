package grupo08.proyecto_efsrtv.config;

import grupo08.proyecto_efsrtv.service.impl.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    @Bean
//    public BCryptPasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
    //MOvida a clase passwordencoderconfig p

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/manage/login").permitAll()
                        .requestMatchers("/manage/start").hasAnyRole("ADMIN", "OPERATOR")
                        .requestMatchers("/manage/crear").hasAnyRole("ADMIN")
                        .requestMatchers("/manage/{id}").hasAnyRole("ADMIN", "OPERATOR")
                        .requestMatchers("/manage/update/{id}").hasAnyRole("ADMIN", "OPERATOR")
                        .requestMatchers("/manage/delete/{id}").hasAnyRole("ADMIN", "OPERATOR")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/manage/login")
                        .defaultSuccessUrl("/manage/start", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/manage/login?logout")
                        .permitAll()
                )
                .exceptionHandling(ex -> ex.accessDeniedPage("/manage/restricted"))
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

}
