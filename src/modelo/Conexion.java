package modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Jorge Silva Borda
 */
public final class Conexion {

    private final String servidor;
    private final String baseDato;
    private final String usuario;
    private final String password;
    private Connection conn;

    public Conexion(String servidor, String baseDato, String usuario, String password) {
	this.servidor = servidor;
	this.baseDato = baseDato;
	this.usuario = usuario;
	this.password = password;
	abrir();
    }
    
    public boolean probarConexion(){
	return null != this.conn;
    }

    public boolean abrir() {
	try {
	    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	    this.conn = DriverManager.getConnection("jdbc:sqlserver://" + servidor + ";databaseName=" + baseDato, usuario, password);
	    return true;
	} catch (ClassNotFoundException | SQLException ex) {
	    System.out.println("No se puede conectar.");
	    System.out.println(ex);
	    return false;
	}
    }

    public void cerrar() {
	try {
	    if (!this.conn.isClosed()) {
		this.conn.close();
	    }
	} catch (SQLException ex) {
	    System.out.println("No se ha podido cerrar la conexión");
	    System.out.println(ex);
	}
    }
    
    public ResultSet ejecutarQuery(String query){
	try{
	    return (conn.createStatement().executeQuery(query));
	}catch (SQLException ex) {
	    System.out.println("No se pudo ejecutar la query: ");
	    System.out.println(query);
	    System.out.println(ex);
	    return null;
	}
    }
    
    public void ejecutar(String query){
	try{
	    conn.createStatement().execute(query);
	}catch (SQLException ex) {
	    System.out.println("No se pudo ejecutar la query: ");
	    System.out.println(query);
	    System.out.println(ex);
	}
    }
}
