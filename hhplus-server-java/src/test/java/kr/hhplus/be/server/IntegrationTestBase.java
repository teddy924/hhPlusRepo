package kr.hhplus.be.server;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class IntegrationTestBase {

    @Autowired
    protected DataSource dataSource;

    private static final List<String> TABLES_TO_TRUNCATE = List.of(
            "payment",
            "order_coupon",
            "order_history",
            "order_address",
            "order_item",
            "orders",
            "coupon_issue",
            "coupon",
            "product",
            "account_history",
            "account",
            "users"
    );

    @BeforeAll
    void truncateTables() throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");

            for (String table : TABLES_TO_TRUNCATE) {
                stmt.execute("TRUNCATE TABLE " + table);
            }

            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");

            // 프로시저 호출
            runSqlFile(conn, "src/test/resources/full-init.sql");
            runSqlFile(conn, "src/test/resources/init_test_data.sql");
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
}
