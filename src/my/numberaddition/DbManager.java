/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.numberaddition;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import my.numberaddition.Header;

/**
 *
 * @author Dr. Baladron
 */
public class DbManager {
    public Connection conn;
    private Boolean connStatus = false;
    private NumberAdditionUI ui;
    
    public final String TABLE_HEADER = "header";
    public final String ID_NAME = "regid";
    
    public DbManager(){
        conn=null;
        connStatus=false;
    }
    
    public Boolean isConnected(){
        if (connStatus) return(true);
        else return(false);  
    }
    
    public Boolean connect(String host, String user, String pass){
        Boolean result = true;
        try {
            // The newInstance() call is a work around for some
            // broken Java implementations

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            System.out.println("DRIVER OK");
        } catch (Exception ex) {
            System.out.println("DRIVER ERROR");
            result = false;
        }
        
        try {
            conn
                    = DriverManager.getConnection("jdbc:mysql://" + host + "?"
                            + "user=" + user + "&password=" + pass);
            System.out.println("Conection OK");
            connStatus=true;
            // Do something with the Connection
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            result = false;
            connStatus=false;
        }
        return(result);
    }
    
    public Boolean insertHeader(Header h) {
        try {
            h.write(conn);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public Boolean updateHeader(Header h) {
        try {
            h.update(conn);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public int getNextId() {
        int resultado = 0;
        try {
            Statement s = conn.createStatement(); 
            ResultSet rs = s.executeQuery("SELECT MAX(" + ID_NAME + ") FROM " + TABLE_HEADER + ";");
            if (rs.next()){
                resultado=rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return(resultado);
    }
    
    public DataPackage createVoidReg(int id){
        DataPackage d = new DataPackage();
        d.header=new Header(TABLE_HEADER,id);
        d.header.write(conn);
        return(d);
    }
    
    public DataPackage retrieveReg(int id){
       DataPackage data = new DataPackage();
       System.out.println("SELECT * FROM " + TABLE_HEADER + 
                    " WHERE "+ ID_NAME +"='"+ Integer.toString(id)+"';");
            try {
                // Preparamos la consulta
                Statement s = conn.createStatement(); 
                ResultSet rs = s.executeQuery("SELECT * FROM " + TABLE_HEADER + 
                    " WHERE "+ ID_NAME +"='"+ Integer.toString(id)+"';");
                if (rs.next()){
                    data.header=new Header(TABLE_HEADER,rs.getInt(1),rs.getInt(2),rs.getString(3),
                    rs.getDate(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8));
                }
            } catch (SQLException ex) {
                Logger.getLogger(NumberAdditionUI.class.getName()).log(Level.SEVERE, null, ex);
            }
       return(data);
    }
}
