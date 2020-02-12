package com.anuj.telegrambot.contant;

public class MySqlValues {

    public static final String USERNAME  = "root";

    public static final String PASSWORD= "root";
    
    public static final String SERVER_NAME = "localhost";
    
    public static final String DB_NAME = "scarcitydata";
    
    public static final String REPORT_INSERT_STATEMENT = "INSERT INTO report (telegram_user_id,product_name,product_scarcity,product_price,product_notes) VALUES (?,null,null,null,null)";


    public static final String GET_DATA_STATEMENT = "SELECT * FROM report where telegram_user_id=?";

    public static final String DELETE_DATA_STATEMENT = "DELETE FROM report where telegram_user_id=?";

    public static final String UPDATE_LOCATION_STATEMENT = "UPDATE report SET latitude=?, longitude=? where telegram_user_id=?";

    public static final String UPDATE_PRODUCT_NAME_STATEMENT = "UPDATE report SET product_name=? where telegram_user_id=?";

    public static final String UPDATE_SCARCITY_VALUE_STATEMENT = "UPDATE report SET product_scarcity=? where telegram_user_id=?";

    public static final String UPDATE_EXPENSIVE_VALUE_STATEMENT = "UPDATE report SET product_price=? where telegram_user_id=?";

    public static final String CREATE_TABLE_IF_NOT_EXIST = "create table if not exists scarcitydata.report ("+
            "id               INTEGER     AUTO_INCREMENT     not null," +
            "telegram_user_id INTEGER  UNIQUE        ," +
            "product_name     varchar(255) ," +
            "product_scarcity INTEGER       ," +
            "product_price    INTEGER      ," +
            "product_notes    varchar(255), " +
            "latitude         double        null," +
            "longitude        double        null," +

            "primary key (id))";

}
