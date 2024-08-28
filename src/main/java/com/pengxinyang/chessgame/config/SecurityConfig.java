//package com.pengxinyang.chessgame.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig { // 如果使用的是 Spring Security 6.x，则不需要继承 WebSecurityConfigurerAdapter
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.authorizeHttpRequests(authorizeRequests ->
//                    authorizeRequests
//                            .requestMatchers(
//                                    "/user/register",
//                                    "/user/login",
//                                    "/user/logout",
//                                    "/user/update/password",
//                                    "/user/get/information",
//                                    "/user/update/information",
//                                    "/user/update/level",
//                                    "/game/start_chess_game",
//                                    "/move/chess",
//                                    "/chess/position",
//                                    "/chess/XYposition"
//                            ).permitAll()  // 替换了 antMatchers
//                            .anyRequest().authenticated()
//            )
//            .formLogin(formLogin ->
//                    formLogin
//                            .loginPage("/login")
//                            .defaultSuccessUrl("/home", true)
//                            .permitAll()
//            )
//            .logout(LogoutConfigurer::permitAll);
//        return http.build();
//    }
//}