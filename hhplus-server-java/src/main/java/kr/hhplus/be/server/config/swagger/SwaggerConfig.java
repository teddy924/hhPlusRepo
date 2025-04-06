package kr.hhplus.be.server.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Camping E-Commerce API")
                        .description("캠핑 이커머스 프로젝트의 Swagger 문서입니다.")
                        .version("v1.0.0"));
    }
}
