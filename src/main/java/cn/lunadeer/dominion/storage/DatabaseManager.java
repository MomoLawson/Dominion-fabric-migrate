package cn.lunadeer.dominion.storage;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.storage.mapper.GenericMapper;
import cn.lunadeer.dominion.storage.migration.V1__LegacySchema;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {

    public static DatabaseManager instance;

    private final HikariConfig config = new HikariConfig();
    private DatabaseType type;
    private HikariDataSource dataSource;
    private SqlSessionFactory sqlSessionFactory;

    /**
     * @param dataDir  the mod's config/data directory on disk
     * @param type     database type string (PGSQL, SQLITE, MYSQL, MARIADB)
     */
    public DatabaseManager(File dataDir, String type, String host, String port, String name, String user, String pass) {
        instance = this;
        set(dataDir, type, host, port, name, user, pass, 10);
    }

    public void set(File dataDir, String type, String host, String port, String name, String user, String pass, int poolSize) {
        try {
            this.type = DatabaseType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unsupported database type: " + type, e);
        }

        config.setPoolName("Dominion-" + this.type.name().toLowerCase());
        config.setUsername(user);
        config.setPassword(pass);
        config.setMaximumPoolSize(poolSize);
        config.setMinimumIdle(Math.min(2, poolSize));
        config.setIdleTimeout(60000);
        config.setConnectionTimeout(30000);
        config.setMaxLifetime(1800000);

        switch (this.type) {
            case PGSQL -> {
                config.setDriverClassName("org.postgresql.Driver");
                config.setJdbcUrl("jdbc:postgresql://" + host + ":" + port + "/" + name);
            }
            case SQLITE -> {
                config.setDriverClassName("org.sqlite.JDBC");
                config.setJdbcUrl("jdbc:sqlite:" + dataDir.getAbsolutePath() + "/" + name + ".db");
                config.setMaximumPoolSize(1);
                config.setMinimumIdle(1);
                config.addDataSourceProperty("foreign_keys", "true");
            }
            case MYSQL -> {
                config.setDriverClassName("com.mysql.cj.jdbc.Driver");
                config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + name + "?useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true");
            }
            case MARIADB -> {
                config.setDriverClassName("org.mariadb.jdbc.Driver");
                config.setJdbcUrl("jdbc:mariadb://" + host + ":" + port + "/" + name + "?useUnicode=true&characterEncoding=utf8&useSSL=false");
            }
        }
    }

    public void reconnect() {
        close();
        dataSource = new HikariDataSource(config);
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setEnvironment(new Environment("Dominion", new JdbcTransactionFactory(), dataSource));
        configuration.addMapper(GenericMapper.class);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    }

    public void migrate() {
        // First, create tables if they don't exist
        try (Connection conn = dataSource.getConnection(); java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS player_name (id INTEGER PRIMARY KEY AUTOINCREMENT, uuid VARCHAR(36) UNIQUE NOT NULL, last_known_name VARCHAR(255), last_join_at BIGINT, using_group_title_id INTEGER DEFAULT 0, skin_url TEXT, ui_preference VARCHAR(10) DEFAULT 'TUI')");
            stmt.execute("CREATE TABLE IF NOT EXISTS dominion (id INTEGER PRIMARY KEY AUTOINCREMENT, owner VARCHAR(36) NOT NULL, name VARCHAR(255) NOT NULL, world_uid VARCHAR(36) NOT NULL, x1 INTEGER, y1 INTEGER, z1 INTEGER, x2 INTEGER, y2 INTEGER, z2 INTEGER, parent_dom_id INTEGER DEFAULT -1, join_message TEXT, leave_message TEXT, tp_location TEXT, color VARCHAR(20), server_id INTEGER DEFAULT 0, owner_glow BOOLEAN DEFAULT FALSE, env_flags TEXT DEFAULT '{}', guest_flags TEXT DEFAULT '{}')");
            stmt.execute("CREATE TABLE IF NOT EXISTS dominion_member (id INTEGER PRIMARY KEY AUTOINCREMENT, player_uuid VARCHAR(36) NOT NULL, dom_id INTEGER NOT NULL, group_id INTEGER DEFAULT 0, flags TEXT DEFAULT '{}')");
            stmt.execute("CREATE TABLE IF NOT EXISTS dominion_group (id INTEGER PRIMARY KEY AUTOINCREMENT, dom_id INTEGER NOT NULL, name VARCHAR(255), name_colored VARCHAR(255), flags TEXT DEFAULT '{}')");
            stmt.execute("CREATE TABLE IF NOT EXISTS privilege_template (id INTEGER PRIMARY KEY AUTOINCREMENT, creator VARCHAR(36), name VARCHAR(255), flags TEXT DEFAULT '{}')");
            stmt.execute("CREATE TABLE IF NOT EXISTS server_info (id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(255))");
            stmt.execute("CREATE TABLE IF NOT EXISTS tp_cache (uuid VARCHAR(36) PRIMARY KEY, dom_id INTEGER)");
            XLogger.info("Database tables created/verified.");
        } catch (SQLException e) {
            XLogger.warn("Table creation note: {0}", e.getMessage());
        }

        // Run Flyway for schema versioning
        Flyway.configure(Dominion.class.getClassLoader())
                .dataSource(dataSource)
                .baselineOnMigrate(true)
                .baselineVersion("0")
                .load()
                .migrate();

        // Reconcile flags
        try {
            FlagReconciler.SyncResult result = new FlagReconciler(dataSource(), type).reconcile();
            if (result.changedEntries() > 0) {
                XLogger.info("Reconciled {0} flag columns/values.", result.changedEntries());
            }
        } catch (Exception e) {
            XLogger.warn("Flag reconciliation skipped: {0}", e.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            reconnect();
        }
        return dataSource.getConnection();
    }

    public DataSource dataSource() {
        if (dataSource == null) {
            reconnect();
        }
        return dataSource;
    }

    public SqlSessionFactory sqlSessionFactory() {
        if (sqlSessionFactory == null) {
            reconnect();
        }
        return sqlSessionFactory;
    }

    public SqlSession openSession() {
        return sqlSessionFactory().openSession(false);
    }

    public void close() {
        sqlSessionFactory = null;
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }

    public DatabaseType getType() {
        return type;
    }
}
