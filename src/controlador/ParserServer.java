package controlador;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import logger.Logger;
import modelo.MakeFile;
import modelo.servidor.ServicioTuxedo;
import modelo.servidor.ServidorTuxedo;
import modelo.servidor.dependencia.Funcion;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.ExpansionOverlapsBoundaryException;
import org.eclipse.cdt.core.dom.ast.IASTAttribute;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.parser.DefaultLogService;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.core.parser.ScannerInfo;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionCallExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTTranslationUnit;
import org.eclipse.core.runtime.CoreException;

/**
 * Clase para parsear los servidores Tuxedo y ubicar todas las funciones internas.
 *
 * @author Jorge Silva Borda
 * @deprecated Se reemplaza con {@link controlador.ParseServer2}
 */
public final class ParserServer {

    private String nomServer;
    private LinkedList<Funcion> funciones = new LinkedList();
    private MakeFile make;
    private String texto;
    private File archivoFuente = null;
    private LinkedList<File> dependencias;
    private String rutaBase;
    private LinkedList<String[]> aliases = new LinkedList();
    public boolean conError = false;

    ASTVisitor visitor = new ASTVisitor() {//Objeto que recorre
	@Override
	public int visit(IASTName name) {
	    if ((name.getParent() instanceof CPPASTFunctionDeclarator || name.getParent() instanceof CPPASTFunctionCallExpression)) {
		//System.out.println(name.getClass().getSimpleName() + " " + name.getRawSignature() + " " + name.getParent().getClass().getSimpleName());
	    }

	    return 3;
	}

	@Override
	public int visit(IASTDeclaration declaration) {
	    //System.out.println(declaration + " " + declaration.getRawSignature());

	    if ((declaration instanceof IASTSimpleDeclaration)) {
		IASTSimpleDeclaration ast = (IASTSimpleDeclaration) declaration;

		IASTNode typedef = ast.getChildren().length == 1 ? ast.getChildren()[0] : ast.getChildren()[1];
		IASTNode[] children = typedef.getChildren();
		if ((children != null) && (children.length > 0)) {
		}

		IASTDeclarator[] declarators = ast.getDeclarators();
		for (IASTDeclarator iastDeclarator : declarators) {
		}

		IASTAttribute[] attributes = ast.getAttributes();
		for (IASTAttribute iastAttribute : attributes) {
		}
	    }

	    if ((declaration instanceof IASTFunctionDefinition)) {
		IASTFunctionDefinition ast = (IASTFunctionDefinition) declaration;
		IScope scope = ast.getScope();
		ICPPASTFunctionDeclarator typedef = (ICPPASTFunctionDeclarator) ast.getDeclarator();
	    }
	    return 3;
	}

	public int visit(IASTFunctionCallExpression call) {
	    return 3;
	}

	@Override
	public int visit(IASTTypeId typeId) {
	    return 3;
	}

	@Override
	public int visit(IASTStatement statement) {
	    return 3;
	}

	@Override
	public int visit(IASTAttribute attribute) {
	    return 3;
	}
    };

    public ParserServer(MakeFile make) throws IOException {
	this.make = make;
	this.nomServer = make.getNombreServer();
	this.rutaBase = make.getRutaBase();
	dependencias = cargarDependencias();
	this.ajustarFuenteServidor();
	this.archivoFuente = make.getArchivoFuenteServidor();
    }

    private void ajustarFuenteServidor() throws IOException {
	for (File dep : make.getDependencias()) {
	    if (dep.getName().split("\\.")[0] != null) {
		if (dep.getName().split("\\.")[0].equals(this.nomServer)) {
		    this.archivoFuente = dep;
		    this.texto = new modelo.Lector(archivoFuente.getAbsolutePath()).getTexto();
		}
	    }
	}
    }

    public ServidorTuxedo parse() throws CoreException {
	//Preparar código sin comentarios.
	texto = new modelo.Utilidades().quitarComentariosJavaC(texto);
	for (File dependencia : dependencias) {
	    if (new modelo.Utilidades().getExtension(dependencia) != null) {
		FileContent fileContent = FileContent.createForExternalFileLocation(dependencia.getAbsolutePath());
		Map definedSymbols = new HashMap();
		String[] includePaths = new String[0];
		IScannerInfo info = new ScannerInfo(definedSymbols, includePaths);
		IParserLogService log = new DefaultLogService();
		IncludeFileContentProvider emptyIncludes = IncludeFileContentProvider.getEmptyFilesProvider();
		int opts = 8;

		IASTTranslationUnit translationUnit = GPPLanguage.getDefault().getASTTranslationUnit(fileContent, info, emptyIncludes, null, opts, log);
		IASTPreprocessorIncludeStatement[] includes = translationUnit.getIncludeDirectives();
		for (IASTPreprocessorIncludeStatement include : includes) {
		}
		//recorrerArbol(translationUnit, 1);
		visitor.shouldVisitNames = true;
		visitor.shouldVisitDeclarations = false;

		visitor.shouldVisitDeclarators = true;
		visitor.shouldVisitAttributes = true;
		visitor.shouldVisitStatements = false; //Muestra el código de las declaraciones.
		visitor.shouldVisitTypeIds = false;

		//translationUnit.accept(visitor);
		//System.out.println("Dependencia de servidor: " + dependencia.getAbsolutePath());
		recorrerArbol(translationUnit, 1, dependencia); //--------------Mostrar contenido
		funciones = armarFunciones2(objetos);
	    }

	}
	return construirServidor();
    }

    LinkedList<Object[]> objetos = new LinkedList();

    private void recorrerArbol(IASTNode node, int index, File dependencia) {
	IASTNode[] children = node.getChildren();
	Funcion f;
	String nombre;
	boolean printContents = true;
	try {
	    if ((node instanceof CPPASTTranslationUnit)) {
		printContents = false;
	    }

	    String offset = "";
	    try {
		offset = node.getSyntax() != null ? " (offset: " + node.getFileLocation().getNodeOffset() + "," + node.getFileLocation().getNodeLength() + ")" : "";
		printContents = node.getFileLocation().getNodeLength() < 30;
	    } catch (ExpansionOverlapsBoundaryException e) {
		//System.out.println("Error: " + e);
		//logear error e indicar cuál es el archivo para post proceso manual
		conError = true;
	    } catch (UnsupportedOperationException e) {
		offset = "UnsupportedOperationException";
		conError = true;
	    }

	    if (!node.getClass().getSimpleName().equals("CPPASTTranslationUnit")) {

		Object[] objeto = new Object[2];
		switch (node.getClass().getSimpleName()) {
		    case "CPPASTFunctionDeclarator":
			//System.out.println(index + ": Declara: " + node.getRawSignature().subSequence(0, node.getFileLocation().getNodeLength()).toString());
			nombre = node.getRawSignature().subSequence(0, node.getFileLocation().getNodeLength()).toString().split("\\(")[0];
			f = new Funcion(nombre, "Declara");
			if (node.getRawSignature().subSequence(0, node.getFileLocation().getNodeLength()).toString().split("\\(")[1].contains("TPSVCINFO")) {
			    f.setEsServicio(true);
			} else {
			    f.setEsServicio(false);
			}
			objeto[0] = f;
			objeto[1] = index;
			objetos.add(objeto);
			break;
		    case "CPPASTFunctionCallExpression":
			//System.out.println(index + ": Llama:" + node.getRawSignature().subSequence(0, node.getFileLocation().getNodeLength()).toString());
			nombre = node.getRawSignature().subSequence(0, node.getFileLocation().getNodeLength()).toString().split("\\(")[0];
			f = new Funcion(nombre, "Llama");
			if (f.getNombre().equals("tpcall")) {
			    Funcion llam = new Funcion(node.getRawSignature().subSequence(0, node.getFileLocation().getNodeLength()).toString().split("\"")[1], "LlamaServicio");
			    //System.out.println("Se guarda llamada a servicio.");
			    ((Funcion) objetos.get(objetos.size() - 1)[0]).addLlamado(llam);
			    objeto[0] = f;
			    objeto[1] = index;
			    objetos.add(objeto);
			    break;
			} else {
			    objeto[0] = f;
			    objeto[1] = index;
			    objetos.add(objeto);
			    break;
			}

		}
		String nodo = String.format(new StringBuilder("%1$").append(index * 2).append("s").toString(), new Object[]{"-"}) + node.getClass().getSimpleName() + offset + " -> " + (printContents ? node.getRawSignature().replaceAll("\n", " \\ ") : node.getRawSignature().subSequence(0, node.getFileLocation().getNodeLength()));
		//System.out.println(String.format(new StringBuilder("%1$").append(index * 2).append("s").toString(), new Object[]{"-"}) + node.getClass().getSimpleName() + offset + " -> " + (printContents ? node.getRawSignature().replaceAll("\n", " \\ ") : node.getRawSignature().subSequence(0, node.getFileLocation().getNodeLength())));
		if (nodo.toUpperCase().contains("SELECT")
			|| nodo.toUpperCase().contains("UPDATE")
			|| nodo.toUpperCase().contains("INSERT")
			|| nodo.toUpperCase().contains("DELETE")) {
		    ParserSQL parserSql = new ParserSQL();
		    if (node.getClass().getSimpleName().equals("CPPASTSimpleDeclaration") && nodo.contains(" char ")) {
			//Es variable
			parserSql.parsearVariable(nodo);
		    } else if (node.getClass().getSimpleName().equals("CPPASTFunctionDefinition")) {
			//Es funcion
			parserSql.parsearFuncion(nodo);
		    }
		}
	    }
	    for (IASTNode iastNode : children) {
		recorrerArbol(iastNode, index + 1, dependencia);
	    }
	} catch (Exception ex) {
	    Logger log = new Logger("log.txt");
	    log.log("Error al procesar: " + this.make.getRuta());
	    log.log(ex);
	}
    }

    public LinkedList<Funcion> armarFunciones(LinkedList<Object[]> objetos) {
	LinkedList<Funcion> funcs = new LinkedList();
	if (objetos.size() > 0) {
	    Funcion funcion;
	    int actual = 0;
	    int indice = (int) objetos.get(0)[1];
	    funcs.add((Funcion) objetos.get(0)[0]);

	    for (int i = 1; i < objetos.size(); i++) {
		if ((int) objetos.get(i)[1] > indice) {
		    if (!funcs.contains((Funcion) objetos.get(i)[0])) {
			funcs.get(actual).addLlamado((Funcion) objetos.get(i)[0]);
		    }
		} else {
		    if (!funcs.contains((Funcion) objetos.get(i)[0])) {
			funcs.add((Funcion) objetos.get(i)[0]);
			actual++;
		    }
		}
		indice = (int) objetos.get(i)[1];
	    }
	}
	return funcs;
    }

    public LinkedList<Funcion> armarFunciones2(LinkedList<Object[]> objetos) {
	if (objetos.size() > 0) {
	    int funActual = -1;
	    int index = (int) objetos.get(0)[1];
	    LinkedList<Funcion> funcs = new LinkedList();
	    for (Object[] obj : objetos) {
		if (((Funcion) obj[0]).getTipo().equals("Declara")) {
		    funcs.add((Funcion) obj[0]);
		    funActual++;
		} else {
		    if (!new modelo.Utilidades().existeEnLista((Funcion) obj[0], funcs.get(funActual).getLlamados())) {
			funcs.get(funActual).addLlamado((Funcion) obj[0]);
		    }
		}
	    }
	    return funcs;
	} else {
	    return new LinkedList<Funcion>();
	}

    }

    public ServidorTuxedo construirServidor() {
	ServidorTuxedo servidor = new ServidorTuxedo();
	servidor.setNombre(nomServer);
	for (File dependencia : dependencias) {
	    servidor.addDependencia(dependencia);
	}
	for (Funcion f : funciones) {
	    if (f.getEsServicio()) {
		ServicioTuxedo servicio = new ServicioTuxedo();
		servicio.setNombre(f.getNombre());
		if (f.getLlamados().size() > 0) {
		    for (Funcion llam : f.getLlamados()) {
			if (!new modelo.Utilidades().existeEnLista(llam, servicio.getFunciones())) {
			    servicio.addLlamado(llam);
			}
		    }
		}
		if (!servidor.getServicios().contains(servicio)) {
		    servidor.addServicio(servicio);
		}
	    } else {
		if (!new modelo.Utilidades().existeEnLista(f, servidor.getFunciones())) {
		    servidor.addFuncion(f);
		}
	    }
	}
	//Acá meter el parseo del texto del MakeFile para buscar los aliases.
	buscarAliases(this.make.getTexto());
	servidor.setAliases(this.aliases);
	servidor.llamadosDependencias();
	servidor.conError = this.conError;
	return servidor;
    }

    private void buscarAliases(String textoMake) {
	
	String regex = "(-s)(\\s+)(\\w+)((,)(\\w+))*(:(\\w+))?";
	Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	Matcher m = p.matcher(textoMake);
	while (m.find()) {
	    this.procesarAliases(m.group());
	}
    }
    
    public void procesarAliases(String linea){
	if(linea.contains(":")){
	    String servicio = linea.split("\\:")[linea.split("\\:").length - 1];
	    linea = linea.replaceAll("-s", "");
	    linea = linea.replaceAll(" ", "");
	    linea = linea.replaceAll(":" + servicio, "");
	    String[] aliases = linea.split(",");
	    for(String alias : aliases){
		if(!servicio.equals(alias)){
		    //System.out.println(servicio + ":" + alias);
		    this.aliases.add(new String[]{servicio, alias});
		}
		
	    }
	}
    }

    public LinkedList<File> cargarDependencias() {
	return make.getDependencias();
    }

    public String getNomServer() {
	return nomServer;
    }

    public void setNomServer(String nomServer) {
	this.nomServer = nomServer;
    }

    public LinkedList<Funcion> getFunciones() {
	return funciones;
    }

    public void setFunciones(LinkedList<Funcion> funciones) {
	this.funciones = funciones;
    }

    public MakeFile getMake() {
	return make;
    }

    public void setMake(MakeFile make) {
	this.make = make;
    }

    //Método de pruebas:
    public void mostrarFunciones(LinkedList<Funcion> funcs) {
	for (Funcion f : funcs) {
	    System.out.println("Funcion: " + f.getNombre() + " - Llamados: " + f.getLlamados().size() + " Es servicio?: " + f.getEsServicio());
	    if (f.getLlamados().size() >= 1) {
		for (Funcion llam : f.getLlamados()) {
		    System.out.println("    -Llamado: " + llam.getNombre());
		}
	    }
	}
    }

}
