package com.etsy.pushbot.config;

import com.etsy.pushbot.config.Config;
import java.util.Map;
import java.util.HashMap;
import java.sql.*;

public class ConfigDao {

    private static ConfigDao instance = null;

    private Connection connection = null;
    private Map<String,Config> configCache = new HashMap<String,Config>();

    public static ConfigDao getInstance() throws SQLException {
        if(ConfigDao.instance == null) {
            ConfigDao.instance = new ConfigDao();
        }
        return ConfigDao.instance;
    }

    private ConfigDao() throws SQLException {
        this.connection = getConnection();
        PreparedStatement preparedStatement = 
            this.connection.prepareStatement("SELECT * FROM config;");
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            System.out.println(rs.getString("member") + "\t" + rs.getString("config"));
        }
        rs.close();
    }
   
    public Config getConfigForMember(String member) throws SQLException {

        Config cachedConfig = this.configCache.get(member);
        if(cachedConfig != null) {
            return cachedConfig;
        }

        PreparedStatement preparedStatement = 
            connection.prepareStatement("SELECT config FROM config WHERE member=?;");

        preparedStatement.setString(1, member);

        ResultSet rs = preparedStatement.executeQuery();

        if(!rs.next()) {
            this.configCache.put(member, new Config());
        }
        else {
            this.configCache.put(member,Config.fromString(rs.getString("config")));
        }
        rs.close();

        return this.configCache.get(member);
    }

    public void setConfigForMember(String member, Config config) throws SQLException {
        PreparedStatement preparedStatement =
            this.connection.prepareStatement("REPLACE INTO config VALUES (?, ?);");

        preparedStatement.setString(1, member);
        preparedStatement.setString(2, config.toString());
        preparedStatement.executeUpdate();

        this.configCache.put(member, config);

        System.out.println("added config for " + member + ": " + config.toString());
    }

    private Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        }
        catch(ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        Connection connection =
            DriverManager.getConnection("jdbc:sqlite:pushbot.db");

        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS config (member text, config text, PRIMARY KEY (member)); ");

        return connection;
    }
    
}
