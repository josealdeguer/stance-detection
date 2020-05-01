package josealdeguer.tfg.violenciadegenero;

import java.sql.*;

public class Connect {
    static Connection connection = null;
//    static PreparedStatement preparedSt = null;

    protected Connection getConnection() {
        String url = "jdbc:mysql://localhost:3306/tweets_vg";
        String user="root";
        String password="holamundo123";

//        Connection conexion = null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
//            conexion = DriverManager.getConnection(url,user,password);
            connection = DriverManager.getConnection(url,user,password);
        }catch(SQLException e){
            System.out.println("Error sql: "+e.getMessage());
            e.printStackTrace();
        }catch(Exception e){
            System.out.println("Error: "+e.getMessage());
            e.printStackTrace();
        }

//        return conexion;
        return connection;
    }

    protected void closeConnection() {
        try {
            connection.close();
        } catch(SQLException e){
            System.out.println("Error sql: "+e.getMessage());
            e.printStackTrace();
        }
    }

    protected PreparedStatement getPreparedStatement(String sql) throws SQLException {
        Connection conexion = getConnection();
        PreparedStatement pst = conexion.prepareStatement(sql);
        return pst;
    }

    protected int addToDB(PreparedStatement pst) {
        Integer resultado = -1;

        try {
            resultado = pst.executeUpdate();
        } catch(SQLException e) {
            System.out.println("Error: "+e.getMessage());
            e.printStackTrace();
        }

        return resultado;
    }

    protected ResultSet getFromDB(PreparedStatement pst) {
        ResultSet resultado = null;

        try {
            resultado = pst.executeQuery();
        } catch(SQLException e) {
            System.out.println("Error: "+e.getMessage());
            e.printStackTrace();
        }

        return resultado;
    }



//    private static void addDataToDB(String companyName, String address, int totalEmployee, String webSite) {
//
//        try {
//            String insertQueryStatement = "INSERT  INTO  Employee  VALUES  (?,?,?,?)";
//
//            preparedSt = connection.prepareStatement(insertQueryStatement);
//            preparedSt.setString(1, companyName);
//            preparedSt.setString(2, address);
//            preparedSt.setInt(3, totalEmployee);
//            preparedSt.setString(4, webSite);
//
//            // execute insert SQL statement
//             preparedSt.executeUpdate();
//            log(companyName + " added successfully");
//        } catch (
//
//                SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void getDataFromDB() {
//
//        try {
//            // MySQL Select Query Tutorial
//            String getQueryStatement = "SELECT * FROM employee";
//
//            preparedSt = connection.prepareStatement(getQueryStatement);
//
//            // Execute the Query, and get a java ResultSet
//            ResultSet rs = preparedSt.executeQuery();
//
//            // Let's iterate through the java ResultSet
//            while (rs.next()) {
//                String name = rs.getString("Name");
//                String address = rs.getString("Address");
//                int employeeCount = rs.getInt("EmployeeCount");
//                String website = rs.getString("Website");
//
//                // Simply Print the results
//                System.out.format("%s, %s, %s, %s\n", name, address, employeeCount, website);
//            }
//
//        } catch (
//
//                SQLException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    // Simple log utility
//    private static void log(String string) {
//        System.out.println(string);
//
//    }

}