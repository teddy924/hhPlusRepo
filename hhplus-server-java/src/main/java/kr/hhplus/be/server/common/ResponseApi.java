package kr.hhplus.be.server.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseApi<T> {

    @Schema(description = "API 요청 성공 또는 실패 여부")
    private boolean success;
    @Schema(description = "성공 또는 실패 사유")
    private String message;
    @Schema(description = "성공 또는 실패에 대한 상세 코드, 성공: 1, 실패 '-' 를 포함한 숫자")
    private String processCode;
    @Schema(description = "응답 데이터")
    private T data;

    public ResponseApi(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public ResponseApi(String message, T data) {
        this.success = true;
        this.message = message;
        this.processCode = "1";
        this.data = data;
    }

    public ResponseApi(String message) {
        this.success = true;
        this.message = message;
        this.processCode = "1";
        this.data = null;
    }

    public ResponseApi(T data) {
        this.success = true;
        this.message = "요청이 성공했습니다.";
        this.processCode = "1";
        this.data = data;
    }
}
