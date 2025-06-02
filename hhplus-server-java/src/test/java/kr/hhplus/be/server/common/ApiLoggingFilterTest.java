package kr.hhplus.be.server.common;

import kr.hhplus.be.server.config.RedissonTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Import({RedissonTestConfig.class})
class ApiLoggingFilterTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("request,response가 로깅되는지 확인")
    void filterWorksTest() throws Exception {
        mockMvc.perform(get("/api/account/balance/history"))
                .andExpect(status().is5xxServerError());

    }

}