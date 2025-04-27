package kr.hhplus.be.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
public class TestcontainersConfiguration {

	public static final MySQLContainer<?> MYSQL_CONTAINER;

	static {
		MYSQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
				.withDatabaseName("hhplus")
				.withUsername("root")
				.withPassword("root");
		MYSQL_CONTAINER.start();

		System.setProperty("spring.datasource.url", MYSQL_CONTAINER.getJdbcUrl() + "?characterEncoding=UTF-8&serverTimezone=UTC");
		System.setProperty("spring.datasource.username", MYSQL_CONTAINER.getUsername());
		System.setProperty("spring.datasource.password", MYSQL_CONTAINER.getPassword());
	}

	@PostConstruct
	public void initializeTestData() {
		try (
				Connection conn = DriverManager.getConnection(
						MYSQL_CONTAINER.getJdbcUrl(), MYSQL_CONTAINER.getUsername(), MYSQL_CONTAINER.getPassword()
				);
		) {
			// schema.sql 실행 (옵션)
			runSqlFile(conn, "src/test/resources/schema.sql");

			// 프로시저 정의: 각 파일 통째로 실행
			runProcedureFile(conn, "src/test/resources/account/insert_account_procedures.sql");
			runProcedureFile(conn, "src/test/resources/product/insert_product_procedures.sql");
			runProcedureFile(conn, "src/test/resources/coupon/insert_coupon_procedures.sql");
			runProcedureFile(conn, "src/test/resources/order/insert_order_full_procedures.sql");


		} catch (Exception e) {
			throw new RuntimeException("Test DB 초기화 실패!", e);
		}
	}

	private void runSqlFile(Connection conn, String filePath) throws IOException, SQLException {
		String sql = Files.readString(Path.of(filePath));
		try (Statement stmt = conn.createStatement()) {
			for (String command : sql.split(";")) {
				String trimmed = command.trim();
				if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
					System.out.println("▶ Executing SQL: " + trimmed);
					stmt.execute(trimmed);
				}
			}
		}
		catch (SQLException e) {
			System.err.println("SQL Error: " + e.getMessage());
			throw e;
		}
	}

	private void runProcedureFile(Connection conn, String path) throws Exception {
		String sql = Files.readString(Path.of(path));

		// CREATE PROCEDURE 블록 단위로 분리해서 실행
		String[] procedures = sql.split("(?<=END;)");  // 'END;' 뒤에서 나누기

		try (Statement stmt = conn.createStatement()) {
			for (String proc : procedures) {
				String trimmed = proc.trim();
				if (!trimmed.isEmpty()) {
					stmt.execute(trimmed);  // 전체 CREATE PROCEDURE 한 블록씩 실행
				}
			}
		}
		catch (SQLException e) {
			System.err.println("SQL Error: " + e.getMessage());
			throw e;
		}
	}

	@PreDestroy
	public void stopContainer() {
		if (MYSQL_CONTAINER.isRunning()) {
			MYSQL_CONTAINER.stop();
		}
	}
}