/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.numberaddition;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dr. Baladron
 */
class Header{
    
    public String table;
    public int regId;
    public int numhist;
    public String paciente;
    public Date fecha;
    public String procedencia;
    public String medico;
    public String motivo;
    public String episodio;

    public Header(String t, int r, int n, String p, Date f, String pr, String med, String mot, String ep) {
        table = t;
        regId = r;
        numhist = n;
        paciente = p;
        fecha = f;
        procedencia = pr;
        medico = med;
        motivo = mot;
        episodio = ep;
    }

    public Header(String t, int r) {
        table = t;
        regId = r;
        numhist = 0;
        paciente = "";
        fecha = new java.sql.Date(0000 - 00 - 00);
        procedencia = "";
        medico = "";
        motivo = "";
        episodio = "";
    }

    public void write(Connection conn) {
        try {

            // the mysql insert statement
            String query = " insert into " + table
                    + " values (?, ?, ?, ?, ?, ?, ?, ?)";
            System.out.println(query);
            // create the mysql insert preparedstatement
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1, regId);
            preparedStmt.setInt(2, numhist);
            preparedStmt.setString(3, paciente);
            preparedStmt.setDate(4, fecha);
            preparedStmt.setString(5, procedencia);
            preparedStmt.setString(6, medico);
            preparedStmt.setString(7, motivo);
            preparedStmt.setString(8, episodio);
            // execute the preparedstatement
            preparedStmt.execute();
        } catch (SQLException ex) {
            Logger.getLogger(NumberAdditionUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void update(Connection conn) {
        try {

            // the mysql insert statement
            String query = "UPDATE " + table + " SET "
                    //+ " regid = '" + Integer.toString(regId) + "',"
                    + " numhist = '" + Integer.toString(numhist) + "'," 
                    + " paciente = '" + paciente + "'," 
                    + " fecha = '" + NumberAdditionUI.sdf1.format(fecha) + "'," 
                    + " procedencia = '" + procedencia + "',"
                    + " medico = '" + medico + "'," 
                    + " motivo = '" + motivo + "'," 
                    + " episodio = '" + episodio + "'"
                    + " WHERE regid = " + Integer.toString(regId) +";";
            System.out.println(query);
            // create the mysql insert preparedstatement
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            //preparedStmt.setInt(1, regId);
            //preparedStmt.setInt(2, numhist);
            //preparedStmt.setString(3, paciente);
            //preparedStmt.setDate(4, fecha);
            //preparedStmt.setString(5, procedencia);
            //preparedStmt.setString(6, medico);
            //preparedStmt.setString(7, motivo);
            //preparedStmt.setString(8, episodio);
            // execute the preparedstatement
            preparedStmt.execute();
        } catch (SQLException ex) {
            Logger.getLogger(NumberAdditionUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
