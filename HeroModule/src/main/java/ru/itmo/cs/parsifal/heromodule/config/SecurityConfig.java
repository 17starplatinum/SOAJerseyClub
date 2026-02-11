package ru.itmo.cs.parsifal.heromodule.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.header.StrictTransportSecurityServerHttpHeadersWriter;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${server.http.port:8080}")
    private int httpPort;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll()
                )

                .headers(headers -> headers
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .preload(true)
                                .maxAgeInSeconds(31536000)
                        )
                )

                .addFilterBefore(httpsRedirectFilter(), BasicAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public OncePerRequestFilter httpsRedirectFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain)
                    throws ServletException, IOException {

                String forwardedProto = request.getHeader("X-Forwarded-Proto");
                boolean isForwardedHttps = "https".equals(forwardedProto);
                boolean isHttps = request.isSecure() || isForwardedHttps;

                if (!isHttps) {
                    StringBuffer url = request.getRequestURL();
                    String queryString = request.getQueryString();
                    String httpsUrl = url.toString()
                            .replace("http://", "https://")
                            .replace(":" + httpPort, ":" + request.getServerPort());

                    if (queryString != null) {
                        httpsUrl += "?" + queryString;
                    }

                    response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
                    response.setHeader("Location", httpsUrl);
                    return;
                }

                filterChain.doFilter(request, response);
            }
        };
    }
}