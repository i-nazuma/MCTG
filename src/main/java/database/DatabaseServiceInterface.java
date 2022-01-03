package database;

import java.sql.Connection;

public interface DatabaseServiceInterface {
    Connection getConnection();
}
