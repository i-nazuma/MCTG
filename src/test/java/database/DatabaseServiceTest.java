package database;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DatabaseServiceTest {

    DatabaseService databaseService;

    @BeforeAll
    @DisplayName("Testing and connecting to Database for the following tests")
    public void setupConnection() {
        DatabaseService databaseService = new DatabaseService();

    }

    @Test
    public void checkDatabase() {

    }
}