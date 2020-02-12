package com.anuj.telegrambot.utils;

import com.anuj.telegrambot.contant.MySqlValues;
import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLConnectionUtils {



    public static Connection getConnection(){
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUser(MySqlValues.USERNAME);
        dataSource.setPassword(MySqlValues.PASSWORD);
        dataSource.setServerName(MySqlValues.SERVER_NAME);
        dataSource.setDatabaseName(MySqlValues.DB_NAME);
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;

    }

}
