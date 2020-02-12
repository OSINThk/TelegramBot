package com.anuj.telegrambot.utils;

import com.anuj.telegrambot.contant.MySqlValues;
import com.anuj.telegrambot.contant.Urls;
import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.*;

public class MySQLConnectionUtils {



    public void readDatabase() throws SQLException {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUser(MySqlValues.USERNAME);
        dataSource.setPassword(MySqlValues.PASSWORD);
        dataSource.setServerName(MySqlValues.SERVER_NAME);
        dataSource.setDatabaseName(MySqlValues.DB_NAME);

        Connection connection = dataSource.getConnection();
        System.out.println(connection);

    }

}
