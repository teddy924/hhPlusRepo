package kr.hhplus.be.server.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import kr.hhplus.be.server.common.ResponseApi;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.stream.Collectors;

import static kr.hhplus.be.server.config.swagger.ErrorCode.CUSTOM_INTERNAL_SERVER_ERROR;
import static kr.hhplus.be.server.config.swagger.ErrorCode.CUSTOM_METHOD_NOT_ALLOWED;

@Configuration
@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(
                title = "HHPlus E-Commerce Service API",
                version = "1.0.0"
        ),
        servers = @io.swagger.v3.oas.annotations.servers.Server(
                url = "http://localhost:8080"
        )
)
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HHPlus E-Commerce API")
                        .version("1.0.0")
                        .description("Camping 프로젝트의 Swagger 명세서입니다."));
    }

    private static final List<ErrorCode> DEFAULT_ERROR_CODES = List.of(
            CUSTOM_INTERNAL_SERVER_ERROR,
            CUSTOM_METHOD_NOT_ALLOWED
    );

    @Bean
    public OperationCustomizer customizer() {
        return (operation, handlerMethod) -> {
            Optional.ofNullable(handlerMethod.getMethodAnnotation(SwaggerError.class))
                    .ifPresent(
                            swaggerErrorExample -> settingExamples(operation, swaggerErrorExample.value()));
            Optional.ofNullable(handlerMethod.getMethodAnnotation(SwaggerSuccess.class))
                    .ifPresent(swaggerSuccessExample -> settingSuccessExample(operation,
                            swaggerSuccessExample.responseType()));
            return operation;
        };
    }

    private void settingExamples(Operation operation, ErrorCode[] swaggerErrorCodes) {
        List<ErrorCode> errorCodes = new ArrayList<>(DEFAULT_ERROR_CODES);
        errorCodes.addAll(Arrays.asList(swaggerErrorCodes));

        Map<Integer, List<ExampleHolder>> groupedExamples = errorCodes.stream()
                .map(this::createExampleHolder)
                .collect(Collectors.groupingBy(ExampleHolder::getCode));

        addExamplesToResponses(operation.getResponses(), groupedExamples);
    }

    private ExampleHolder createExampleHolder(ErrorCode code) {
        return ExampleHolder.builder()
                .holder(createExample(code))
                .code(code.getStatusCode())
                .name(code.name())
                .build();
    }


    private void settingSuccessExample(Operation operation, Class<?> responseType) {
        ApiResponses responses = operation.getResponses();
        MediaType mediaType = new MediaType();

        Example successExample = new Example().value(
                new ResponseApi<>(true, "요청이 성공했습니다.", "1", createMockResponse(responseType)));
        mediaType.addExamples("SuccessResponse", successExample);

        responses.addApiResponse("200", new ApiResponse().description("성공적인 응답")
                .content(new Content().addMediaType("application/json", mediaType)));
    }

    private Object createMockResponse(Class<?> responseType) {
        try {
            return responseType.getDeclaredConstructor().newInstance(); // 기본 생성자로 Mock 객체 생성
        } catch (Exception e) {
            return new Object(); // 실패 시 빈 객체 반환
        }
    }

    private Example createExample(ErrorCode type) {
        return new Example().value(
                new ResponseApi<>(false, type.getMessage(), type.getProcessCode(), new Object()));
    }

    private void addExamplesToResponses(ApiResponses responses,
                                        Map<Integer, List<ExampleHolder>> exampleHolders) {
        exampleHolders.forEach((status, holders) -> {
            MediaType mediaType = new MediaType();
            holders.forEach(holder -> mediaType.addExamples(holder.getName(), holder.getHolder()));

            responses.addApiResponse(status.toString(), new ApiResponse().content(
                    new Content().addMediaType("application/json", mediaType)));
        });
    }
}