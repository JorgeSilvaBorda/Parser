package modelo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import modelo.servidor.ServidorTux;

public class Escritor {

    public static String makeServidor = "interfaces\\make-servidor.csv";
    public static String servidorDependencias = "interfaces\\servidor-dependencias.csv";
    public static String servidorServicios = "interfaces\\servidor-servicios.csv";
    public static String servicioFuncion = "interfaces\\servicio-funcion.csv";
    public static String funcionFuncion = "interfaces\\funcion-funcion.csv";
    public static String servicioAlias = "interfaces\\servicio-alias.csv";
    public static String dependenciaFuncionConsumo = "interfaces\\dependencia-funcion-consumo.csv";
    public static String dependenciaFuncionFuncion = "interfaces\\dependencia-funcion-funcion.csv";
    public static String dependenciaFuncion = "interfaces\\dependencia-funcion.csv";
    public static String errores = "interfaces\\errores.csv";
    public static String funcionServicioCore = "interfaces\\funcion-core.csv";

    public static void iniciarInterfaces() {
	try {
	    BufferedWriter writer = new BufferedWriter(new FileWriter(makeServidor));
	    //writer.append("sep=,").append(System.getProperty("line.separator"));
	    writer.append("ARCHIVO_MAKE,NOMBRE_SERVIDOR").append(System.getProperty("line.separator"));
	    writer.close();

	    writer = new BufferedWriter(new FileWriter(servidorDependencias));
	    //writer.append("sep=,").append(System.getProperty("line.separator"));
	    writer.append("NOMBRE_SERVIDOR,DEPENDENCIA").append(System.getProperty("line.separator"));
	    writer.close();

	    writer = new BufferedWriter(new FileWriter(servidorServicios));
	    //writer.append("sep=,").append(System.getProperty("line.separator"));
	    writer.append("ARCHIVO_FUENTE,NOMBRE_SERVIDOR,NOMBRE_SERVICIO").append(System.getProperty("line.separator"));
	    writer.close();

	    writer = new BufferedWriter(new FileWriter(servicioFuncion));
	    //writer.append("sep=,").append(System.getProperty("line.separator"));
	    writer.append("ARCHIVO_FUENTE,SERVICIO,FUNCION").append(System.getProperty("line.separator"));
	    writer.close();

	    writer = new BufferedWriter(new FileWriter(funcionFuncion));
	    //writer.append("sep=,").append(System.getProperty("line.separator"));
	    writer.append("FUNCION,LLAMA_FUNCION").append(System.getProperty("line.separator"));
	    writer.close();

	    writer = new BufferedWriter(new FileWriter(servicioAlias));
	    //writer.append("sep=,").append(System.getProperty("line.separator"));
	    writer.append("ARCHIVO_MAKE,SERVICIO,ALIAS").append(System.getProperty("line.separator"));
	    writer.close();

	    writer = new BufferedWriter(new FileWriter(dependenciaFuncionConsumo));
	    //writer.append("sep=,").append(System.getProperty("line.separator"));
	    writer.append("DEPENDENCIA,FUNCION,SERVICIO_CONSUMIDO").append(System.getProperty("line.separator"));
	    writer.close();

	    writer = new BufferedWriter(new FileWriter(dependenciaFuncionFuncion));
	    //writer.append("sep=,").append(System.getProperty("line.separator"));
	    writer.append("DEPENDENCIA,FUNCION,FUNCION_EJECUTADA").append(System.getProperty("line.separator"));
	    writer.close();

	    writer = new BufferedWriter(new FileWriter(errores));
	    //writer.append("sep=,").append(System.getProperty("line.separator"));
	    writer.append("MAKE_ERROR").append(System.getProperty("line.separator"));
	    writer.close();
	    
	    writer = new BufferedWriter(new FileWriter(dependenciaFuncion));
	    //writer.append("sep=,").append(System.getProperty("line.separator"));
	    writer.append("DEPENDENCIA,FUNCION").append(System.getProperty("line.separator"));
	    writer.close();
	    
	    writer = new BufferedWriter(new FileWriter(funcionServicioCore));
	    //writer.append("sep=,").append(System.getProperty("line.separator"));
	    writer.append("DEPENDENCIA,FUNCION,LLAMADO_CORE").append(System.getProperty("line.separator"));
	    writer.close();

	} catch (Exception ex) {
	    System.out.println("No se puede iniciar las interfaces.");
	    System.out.println(ex);
	}

    }

    public static void escribirServidor(ServidorTux servidor) {
	try {
	    BufferedWriter writer;
	    
	    //Make con su servidor
	    System.out.println("Almacenando Make de servidor");
	    writer = new BufferedWriter(new FileWriter(makeServidor, true));
	    writer.append(servidor.archivoMake.getAbsolutePath()).append(",")
		    .append(servidor.nombre).append(System.getProperty("line.separator"));
	    writer.close();

	    //Servidor con sus dependencias
	    System.out.println("Almacenando dependencias");
	    writer = new BufferedWriter(new FileWriter(servidorDependencias, true));
	    for (File dependencia : servidor.dependencias) {
		writer.append(servidor.nombre).append(",")
			.append(dependencia.getAbsolutePath()).append(System.getProperty("line.separator"));
	    }
	    writer.close();

	    //Servidor con sus servicios
	    System.out.println("Almacenando servicios");
	    writer = new BufferedWriter(new FileWriter(servidorServicios, true));
	    for (String[] servicio : servidor.servicios) {

		writer.append(servidor.archivoFuente.getAbsolutePath()).append(",")
			.append(servidor.nombre).append(",")
			.append(servicio[0]).append(System.getProperty("line.separator"));
	    }
	    writer.close();
	    
	    //Servicios y sus llamados a funciones
	    System.out.println("Almacenando servicios y llamados a funciones");
	    writer = new BufferedWriter(new FileWriter(servicioFuncion, true));
	    for (String[] servicioFunc : servidor.servicioFuncion) {
		writer.append(servidor.archivoFuente.getAbsolutePath()).append(",")
			.append(servicioFunc[0]).append(",")
			.append(servicioFunc[1]).append(System.getProperty("line.separator"));
	    }
	    writer.close();
	    
	    //Servicios con sus aliases
	    System.out.println("Almacenando servicios con alias");
	    writer = new BufferedWriter(new FileWriter(servicioAlias, true));
	    for (String[] servAlias : servidor.servicioAlias) {
		writer.append(servAlias[0]).append(",") //Make
			.append(servAlias[1]).append(",")//Servicio
			.append(servAlias[2]).append(System.getProperty("line.separator")); //Alias
	    }
	    writer.close();

	    //Dependencias con sus funciones y sus llamados a servicios
	    System.out.println("Almacenando dependencias con funciones y sus llamados a servicios");
	    writer = new BufferedWriter(new FileWriter(dependenciaFuncionConsumo, true));
	    for (String[] depeFuncServicio : servidor.funcionServicio) {
		writer.append(depeFuncServicio[0]).append(",")
			.append(depeFuncServicio[1]).append(",")
			.append(depeFuncServicio[2]).append(System.getProperty("line.separator"));
	    }
	    writer.close();

	    //Dependencias con sus funciones declaradas (todas)
	    System.out.println("Almacenando dependencias con funciones declaradas");
	    writer = new BufferedWriter(new FileWriter(dependenciaFuncion, true));
	    for (String[] depeFunc : servidor.dependenciaFuncion) {
		writer.append(depeFunc[0]).append(",")
			.append(depeFunc[1]).append(System.getProperty("line.separator"));
	    }
	    writer.close();

	    //Dependencias con sus funciones declaradas y los llamados desde ellas a otras funciones
	    System.out.println("Almacenando dependencias con funciones declaradas y sus llamados");
	    writer = new BufferedWriter(new FileWriter(dependenciaFuncionFuncion, true));
	    for (String[] depeFuncFunc : servidor.dependenciaFuncionFuncion) {
		writer.append(depeFuncFunc[0]).append(",")
			.append(depeFuncFunc[1]).append(",")
			.append(depeFuncFunc[2]).append(System.getProperty("line.separator"));
	    }
	    writer.close();
	    
	    //Dependencias con sus funciones declaradas y los llamados a servicios CORE
	    System.out.println("Almacenando dependencias con funciones declaradas y llamados a servicios CORE");
	    writer = new BufferedWriter(new FileWriter(funcionServicioCore, true));
	    for (String[] depeFuncCore : servidor.funcionCore) {
		writer.append(depeFuncCore[0]).append(",") //Dependencia
			.append(depeFuncCore[1]).append(",") //Nombre funcion
			.append(depeFuncCore[2]).append(System.getProperty("line.separator")); //Nombre servicio core llamado
	    }
	    writer.close();
	    
	} catch (IOException ex) {
	    System.out.println("No se pueden almacenar los datos.");
	    System.out.println(ex);
	}
    }

    public static void escribirError(modelo.MakeFile make) {
	try {
	    BufferedWriter writer = new BufferedWriter(new FileWriter(errores, true));
	    writer.append(make.getRuta()).append(System.getProperty("line.separator"));
	    writer.close();
	} catch (IOException ex) {
	    System.out.println("No se puede escribir el error.");
	    System.out.println(ex);
	}
    }
}
