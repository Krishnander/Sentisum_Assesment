package com.sentisum.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenAPIConfig {

	@Value("${openapi.url}")
	private String swaggerUrl;

	@Bean
	OpenAPI myOpenAPI() {
		Server devServer = new Server();
		devServer.setUrl(swaggerUrl);
		devServer.setDescription("Swagger URL in Local environment");

		License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

		Info info = new Info().title("SentiSum Assessment").version("1.0")
				.description("This application gives solution to the coding exercise").license(mitLicense);

		return new OpenAPI().info(info).servers(List.of(devServer));
	}
}
