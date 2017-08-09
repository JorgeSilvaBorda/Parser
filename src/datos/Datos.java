package datos;

import java.sql.ResultSet;
import java.sql.SQLException;
import logger.Logger;
import modelo.Conexion;
import modelo.servidor.ServicioTuxedo;
import modelo.servidor.ServidorTuxedo;
import modelo.servidor.dependencia.Dependencia;
import modelo.servidor.dependencia.Funcion;

/**
 * @author Jorge Silva Borda
 */
public class Datos {

    private static final Logger logger = new Logger("log.txt");

    public static int insertServidorTux(ServidorTuxedo servidor) {
	Conexion con = new Conexion("172.20.248.81", "ARQUITECTURA", "arquitectura", "arquitectura");
	String query = "SELECT COUNT(1) CUENTA FROM SERVIDORTUX WHERE NOMSERVIDORTUX = '" + servidor.getNombre() + "'";
	con.abrir();
	ResultSet rs = con.ejecutarQuery(query);
	int cont = 0;
	try {
	    while (rs.next()) {
		cont = rs.getInt("CUENTA");
	    }
	} catch (SQLException ex) {
	    logger.log("No se pudo obtener la existencia.");
	    logger.log(query);
	    con.cerrar();
	    return 0;
	}
	con.cerrar();
	if (cont > 0) {//ya existe
	    con.abrir();
	    query = "SELECT IDSERVIDORTUX FROM SERVIDORTUX WHERE NOMSERVIDORTUX = '" + servidor.getNombre() + "'";
	    rs = con.ejecutarQuery(query);
	    try {
		while (rs.next()) {
		    int id = rs.getInt("IDSERVIDORTUX");
		    con.cerrar();
		    return id;
		}
	    } catch (SQLException ex) {
		logger.log("No se pudo obtener la id del servidor.");
		logger.log(query);
		con.cerrar();
		return 0;
	    }
	    con.cerrar();
	} else {
	    con.abrir();
	    query = "INSERT INTO SERVIDORTUX(NOMSERVIDORTUX, ARCHIVOFUENTE) VALUES('" + servidor.getNombre() + "', '" + servidor.getArchivoFuente() + "')";
	    con.ejecutar(query);
	    con.cerrar();

	    query = "SELECT IDSERVIDORTUX FROM SERVIDORTUX WHERE NOMSERVIDORTUX = '" + servidor.getNombre() + "'";
	    con.abrir();
	    rs = con.ejecutarQuery(query);
	    try {
		while (rs.next()) {
		    int id = rs.getInt("IDSERVIDORTUX");
		    con.cerrar();
		    return id;
		}
	    } catch (SQLException ex) {
		logger.log("No se pudo obtener la id del servidor(2).");
		logger.log(query);
		con.cerrar();
		return 0;
	    }
	}
	return 0;
    }

    public static int insertarServicio(ServicioTuxedo servicio, int idServidor) {
	Conexion con = new Conexion("172.20.248.81", "ARQUITECTURA", "arquitectura", "arquitectura");
	String query = "SELECT COUNT(1) CUENTA FROM SERVICIOTUX WHERE NOMSERVICIOTUX = '" + servicio.getNombre() + "'";
	int cont = 0;
	con.abrir();
	try {
	    ResultSet rs = con.ejecutarQuery(query);
	    while (rs.next()) {
		cont = rs.getInt("CUENTA");
	    }
	    con.cerrar();
	} catch (SQLException ex) {
	    logger.log("No se pudo obtener la existencia.");
	    logger.log(query);
	    logger.log(ex);
	    con.cerrar();
	    return 0;
	}
	if (cont > 0) {
	    con = new Conexion("172.20.248.81", "ARQUITECTURA", "arquitectura", "arquitectura");
	    query = "SELECT IDSERVICIOTUX FROM SERVICIOTUX WHERE NOMSERVICIOTUX = '" + servicio.getNombre() + "'";
	    con.abrir();
	    try {
		ResultSet rs = con.ejecutarQuery(query);
		while (rs.next()) {
		    int salida = rs.getInt("IDSERVICIOTUX");
		    con.cerrar();
		    return salida;
		}
		con.cerrar();
	    } catch (SQLException ex) {
		logger.log("No se pudo obtener la id del servicio.");
		logger.log(query);
		logger.log(ex);
		con.cerrar();
		return 0;
	    }
	    con.cerrar();
	} else {
	    
	    query = "INSERT INTO SERVICIOTUX(NOMSERVICIOTUX) VALUES('" + servicio.getNombre() + "')";
	    con = new Conexion("172.20.248.81", "ARQUITECTURA", "arquitectura", "arquitectura");
	    con.abrir();
	    con.ejecutar(query);
	    con.cerrar();
	    con.abrir();
	    query = "SELECT IDSERVICIOTUX FROM SERVICIOTUX WHERE NOMSERVICIOTUX = '" + servicio.getNombre() + "'";
	    try {
		ResultSet rs = con.ejecutarQuery(query);
		while (rs.next()) {
		    int id = rs.getInt("IDSERVICIOTUX");
		    con.cerrar();
		    return id;
		}
	    } catch (SQLException ex) {
		logger.log("No se pudo obtener insertar el servicio.");
		logger.log(query);
		logger.log(ex);
		con.cerrar();
		return 0;
	    }
	}
	return 0;
    }

    public static int insertarServidorServicio(int idServidor, int idServicio) {
	Conexion con = new Conexion("172.20.248.81", "ARQUITECTURA", "arquitectura", "arquitectura");
	String query = "EXEC INS_SERVIDORSERVICIO " + idServidor + ", " + idServicio;
	con.abrir();
	ResultSet rs = con.ejecutarQuery(query);
	try {
	    while (rs.next()) {
		int salida = rs.getInt("SALIDA");
		con.cerrar();
		return salida;
	    }
	} catch (SQLException ex) {
	    logger.log("No se pudo ejecutar: " + query);
	    logger.log(ex);
	    con.cerrar();
	    return 0;
	}

	return 0;
    }

    public static void insertarServicioServicio(int idServicio, int idServicio2) {
	Conexion con = new Conexion("172.20.248.81", "ARQUITECTURA", "arquitectura", "arquitectura");
    }

    public static void insertarFuncion(Funcion funcion) {
	Conexion con = new Conexion("172.20.248.81", "ARQUITECTURA", "arquitectura", "arquitectura");
    }

    public static void insertarServicioFuncion(Funcion funcion) {
	Conexion con = new Conexion("172.20.248.81", "ARQUITECTURA", "arquitectura", "arquitectura");
    }

    public static void insertarDependencia(Dependencia dependencia) {
	Conexion con = new Conexion("172.20.248.81", "ARQUITECTURA", "arquitectura", "arquitectura");
    }
}
