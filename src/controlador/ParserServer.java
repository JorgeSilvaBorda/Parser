package controlador;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
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
 * Clase para parsear los servidores Tuxedo y ubicar todas las funciones
 * internas.
 *
 * @author Jorge Silva Borda
 */
public final class ParserServer {

    private String nomServer;
    private LinkedList<Funcion> funciones = new LinkedList();
    private MakeFile make;
    private String texto;
    private File archivoFuente = null;
    private LinkedList<File> dependencias;
    private String rutaBase;

    ASTVisitor visitor = new ASTVisitor() {//Objeto que recorre
	@Override
	public int visit(IASTName name) {
	    if ((name.getParent() instanceof CPPASTFunctionDeclarator || name.getParent() instanceof CPPASTFunctionCallExpression)) {
		System.out.println(name.getClass().getSimpleName() + " " + name.getRawSignature() + " " + name.getParent().getClass().getSimpleName());
	    }
	    return 3;
	}

	@Override
	public int visit(IASTDeclaration declaration) {
	    System.out.println(declaration + " " + declaration.getRawSignature());

	    if ((declaration instanceof IASTSimpleDeclaration)) {
		IASTSimpleDeclaration ast = (IASTSimpleDeclaration) declaration;
		//try {
		//System.out.println(ast.getSyntax().toString() + " " + ast.getChildren().length);
		IASTNode typedef = ast.getChildren().length == 1 ? ast.getChildren()[0] : ast.getChildren()[1];
		//System.out.println(typedef);
		IASTNode[] children = typedef.getChildren();
		if ((children != null) && (children.length > 0)) {
		    //System.out.println(children[0].getRawSignature());
		}
		//} catch (ExpansionOverlapsBoundaryException e) {
		//System.out.println("Error: " + e);
		//}

		IASTDeclarator[] declarators = ast.getDeclarators();
		for (IASTDeclarator iastDeclarator : declarators) {
		    //System.out.println(iastDeclarator.getName());
		}

		IASTAttribute[] attributes = ast.getAttributes();
		for (IASTAttribute iastAttribute : attributes) {
		    //System.out.println(iastAttribute);
		}
	    }

	    if ((declaration instanceof IASTFunctionDefinition)) {
		IASTFunctionDefinition ast = (IASTFunctionDefinition) declaration;
		IScope scope = ast.getScope();
		//try {
		//System.out.println(scope.getParent().getScopeName());
		//System.out.println(ast.getSyntax());
		//} catch (DOMException | ExpansionOverlapsBoundaryException e) {
		//System.out.println("Error: " + e);
		//}
		ICPPASTFunctionDeclarator typedef = (ICPPASTFunctionDeclarator) ast.getDeclarator();
		//System.out.println(typedef.getName());
	    }
	    return 3;
	}

	public int visit(IASTFunctionCallExpression call) {
	    //System.out.println(call + " " + call.getRawSignature());
	    return 3;
	}

	@Override
	public int visit(IASTTypeId typeId) {
	    //System.out.println(typeId.getRawSignature());
	    return 3;
	}

	@Override
	public int visit(IASTStatement statement) {
	    //System.out.println(statement.getRawSignature());
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

	//Obtener las funciones del código.
	//System.out.println("Archivo servidor: " + this.archivoFuente.getAbsolutePath());
	//Para el proceso de los archivos fuentes.
	for (File dependencia : dependencias) {
	    if (new modelo.Utilidades().getExtension(dependencia) != null) {
		System.out.println("Archivo examinado: " + dependencia.getAbsolutePath());
		//FileContent fileContent = FileContent.createForExternalFileLocation(this.archivoFuente.getAbsolutePath());
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
		    //System.out.println("include - " + include.getName());
		}
		//recorrerArbol(translationUnit, 1);
		visitor.shouldVisitNames = true;
		visitor.shouldVisitDeclarations = false;

		visitor.shouldVisitDeclarators = true;
		visitor.shouldVisitAttributes = true;
		visitor.shouldVisitStatements = false; //Muestra el código de las declaraciones.
		visitor.shouldVisitTypeIds = false;

		//translationUnit.accept(visitor);
		recorrerArbol(translationUnit, 1); //--------------Mostrar contenido
		funciones = armarFunciones2(objetos);
	    }

	}
	return construirServidor();
    }

    LinkedList<Object[]> objetos = new LinkedList();

    private void recorrerArbol(IASTNode node, int index) {
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
		System.out.println("Error: " + e);
	    } catch (UnsupportedOperationException e) {
		offset = "UnsupportedOperationException";
	    }

	    if (!node.getClass().getSimpleName().equals("CPPASTTranslationUnit")) {
		//if (node.getClass().getSimpleName().equals("CPPASTFunctionDeclarator")
		//|| node.getClass().getSimpleName().equals("CPPASTFunctionCallExpression")
		/*|| node.getClass().getSimpleName().equals("CPPASTName")*///) {
		//System.out.print(index + ":");

		Object[] objeto = new Object[2];
		switch (node.getClass().getSimpleName()) {
		    case "CPPASTFunctionDeclarator":
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
			nombre = node.getRawSignature().subSequence(0, node.getFileLocation().getNodeLength()).toString().split("\\(")[0];
			f = new Funcion(nombre, "Llama");
			if (f.getNombre().equals("tpcall")) {
			    //System.out.println(node.getRawSignature().subSequence(0, node.getFileLocation().getNodeLength()).toString());
			    //System.out.println(node.getRawSignature().subSequence(0, node.getFileLocation().getNodeLength()).toString().split("\"")[1]);
			    System.out.println("Llamada a servicio: " + node.getRawSignature().subSequence(0, node.getFileLocation().getNodeLength()).toString().split("\"")[1]);
			    f = new Funcion(node.getRawSignature().subSequence(0, node.getFileLocation().getNodeLength()).toString().split("\"")[1], "LlamaServicio");
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
		    //System.out.println(String.format(new StringBuilder("%1$").append(index * 2).append("s").toString(), new Object[]{"-"}) + node.getClass().getSimpleName() + offset + " -> " + (printContents ? node.getRawSignature().replaceAll("\n", " \\ ") : node.getRawSignature().subSequence(0, node.getFileLocation().getNodeLength())));
		    //Para los sql.c
		    //CPPASTSimpleDeclaration contiene la declaración de una variable con query.
		    //Luego viene CPPASTDeclarator con el nombre de la variable (Puede contener asterisco delante de ella) [* nomVar =]
		    //Luego CPPASTDeclarator Contiene el String de la query
		    //Expresion para extraer el texto: (\")(select|insert|update|delete)(.*?)(;;*)
		    //CPPASTFunctionDefinition Indica que viene la declaración de una función con query. 
		    //CPPASTExpressionStatement Puede tratarse de una variable que contenga query
		    //CPPASTLiteralExpression contiene la cadena con query en caso de ser
		    ParserSQL parserSql = new ParserSQL();
		    if (node.getClass().getSimpleName().equals("CPPASTSimpleDeclaration") && nodo.contains(" char ")) {
			//Es variable
			parserSql.parsearVariable(nodo);
		    } else if (node.getClass().getSimpleName().equals("CPPASTFunctionDefinition")) {
			//Es funcion
			parserSql.parsearFuncion(nodo);
		    }
		    //Para los sql.pc
		    //CPPASTFunctionDefinition 
		    //CPPASTCompoundStatement contendría la query
		    //Las querys terminan con ;
		    //Expresion para extraer texto de las querys: (EXEC SQL)(\s?)(SELECT|INSERT|UPDATE|DELETE)(.*?);;*
		}
	    }
	    for (IASTNode iastNode : children) {
		recorrerArbol(iastNode, index + 1);
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

	return servidor;
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
