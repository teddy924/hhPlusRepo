package kr.hhplus.be.server;

import kr.hhplus.be.server.config.RedissonTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import({RedissonTestConfig.class})
class ServerApplicationTests {

	@Test
	void contextLoads() {
	}

}
