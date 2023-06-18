package ru.raspgleb.movierental;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Database {
    private static final String user = "postgres";
    private static final String password = "1234567890";
    private static Connection connection;
    private static Statement statement;

    public static boolean connection(String ip,String port){
        try{
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://" + ip + ":" + port + "/postgres", user, password);
            statement = connection.createStatement();
            return true;
        }
        catch (Exception exception){
            System.out.println(exception);
            return false;
        }
    }


    public static void addUser(String surname, String name, String lastname, String email, String password) {
        try {
            statement.execute("INSERT INTO user_data (surname, name, lastname, email, password) VALUES " +
                    String.format("('%s', '%s', '%s', '%s', '%s')",surname , name, lastname, email, password));
        }catch (Exception e1){
            System.out.println(e1);
        }

    }

    public static String searchUser(String email){
        try{

            ResultSet rs = statement.executeQuery("SELECT password FROM user_data WHERE email=" +
                    String.format("'%s'",email));
            while(rs.next()){
                //System.out.println(rs.getString("password"));
                return rs.getString("password");
            }


        }catch (Exception e3){
            System.out.println(e3);
        }

        return "";
    }

    public static HashMap<String, String> readFilms(){
        HashMap<String,String> films_from_db = new HashMap<>();
        try{

            ResultSet rs = statement.executeQuery("SELECT name_film, genre_film FROM films_data");
            while(rs.next()){
                films_from_db.put(rs.getString("name_film"),rs.getString("genre_film"));
                //System.out.println(rs.getString("name_film") + " " + rs.getString("genre_film"));
            }
        }catch (Exception e3){
            System.out.println(e3);
        }
        return films_from_db;
    }

    public static HashMap<String, String> read_saveFilms(String email){
        HashMap<String,String> films_from_db = new HashMap<>();
        try{

            ResultSet rs = statement.executeQuery("SELECT name_film, genre_film, date_out FROM films_data WHERE date_out IS NOT NULL AND email=" + String.format("'%s'",email));
            while(rs.next()){
                films_from_db.put(rs.getString("name_film"),(rs.getString("genre_film"))+"!!!!"+((rs.getString("date_out")).split(" ")[0]));
                //System.out.println(rs.getString("name_film") + " " + rs.getString("genre_film"));
            }
        }catch (Exception e3){
            System.out.println(e3);
        }
        return films_from_db;
    }

    public static void takeFilm(String name_film, String genre_film, Timestamp date_take, Timestamp date_out, String email){
        try{
            statement.executeQuery("UPDATE films_data SET date_take=" + String.format("'%s'",date_take) + ", date_out=" + String.format("'%s'",date_out) + ", email=" + String.format("'%s'",email) +
                    "WHERE name_film=" + String.format("'%s'",name_film) + " AND genre_film=" + String.format("'%s'",genre_film) + "AND email IS NULL");

        }catch (Exception e3){
            System.out.println(e3);
        }
    }

    public static void returnFilm(String name_film, String genre_film, String email){
        try{
            statement.executeQuery("UPDATE films_data SET date_take = NULL, date_out = NULL, email= NULL WHERE email=" +
                    String.format("'%s'",email) + " AND name_film =" + String.format("'%s'",name_film) + " AND genre_film =" + String.format("'%s'",genre_film));

        }catch (Exception e23){
            System.out.println(e23);
        }
    }

    public static String checkFilm(String name_film, String genre_film){
        try{

            ResultSet rss = statement.executeQuery("SELECT email FROM films_data WHERE name_film="+ String.format("'%s'",name_film) + "AND genre_film=" + String.format("'%s'",genre_film));
            while(rss.next()){
                if(rss.getString("email")!=null)
                    return rss.getString("email");
            }
        }catch (Exception e3){
            System.out.println(e3);
        }
        return "not_found_email_in_database";
    }

    public static void addProfit(int money){
        java.util.Calendar calendar = java.util.Calendar.getInstance(java.util.TimeZone.getDefault(), java.util.Locale.getDefault());
        calendar.setTime(new java.util.Date());
        int lastEntryMoney = 0; // Прошлая запись о прибыли в том же месяце
        boolean checkCompleted = false; // Переменная сигнализирующая о том, что месяц и год не изменились: обновить строку, а не создать новую
        try {
            ResultSet checkLine = statement.executeQuery("SELECT month FROM bank_data WHERE year=" +
                    String.format("'%s'", calendar.get(java.util.Calendar.YEAR)));
            while(checkLine.next()){
                if(checkLine.getString("month").equals(String.valueOf(calendar.get(Calendar.MONTH)))){
                    ResultSet last_money = statement.executeQuery("SELECT money FROM bank_data WHERE month=" + String.format("'%s'",calendar.get(Calendar.MONTH)) +" AND year=" + String.format("'%s'",calendar.get(Calendar.YEAR)));
                    while(last_money.next()){
                        lastEntryMoney = Integer.parseInt(last_money.getString("money"));
                    }
                    statement.execute("UPDATE bank_data SET money =" + String.format("'%d'",lastEntryMoney + money) +
                            "WHERE month=" + String.format("'%s'",calendar.get(Calendar.MONTH)) + " AND year=" + String.format("'%s'",calendar.get(Calendar.YEAR)));
                    checkCompleted = true;
                }
            }
            if(!checkCompleted){
                statement.execute("INSERT INTO bank_data (month, year, money) VALUES " +
                        String.format("('%s', '%s', '%s')",calendar.get(Calendar.MONTH) , calendar.get(java.util.Calendar.YEAR), money));
            }

        }catch (Exception e){
            System.out.println(e);
        }
        checkCompleted = false;
    }

    public static void addFilm(String name_film, String genre_film, int price){
        try{
            statement.execute("INSERT INTO films_data (name_film, genre_film, price) VALUES " +
                    String.format("('%s', '%s', '%d')",name_film , genre_film, price));
        }catch (Exception e2){
            System.out.println(e2);
        }
    }

    public static void closeDB(){
        try{
            statement.close();
            connection.close();
        }
        catch (Exception exception){
            System.out.println(exception.getMessage());
        }
    }
}

