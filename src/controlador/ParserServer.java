package controlador;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import modelo.MakeFile;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.ExpansionOverlapsBoundaryException;
import org.eclipse.cdt.core.dom.ast.IASTAttribute;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
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
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTTranslationUnit;
import org.eclipse.core.runtime.CoreException;

/**
 * Clase para parsear los servidores Tuxedo y ubicar todas las funciones
 * internas.
 * @author Jorge Silva Borda
 */
public class ParserServer {

    private String nomServer;
    private LinkedList<String> funciones = new LinkedList();
    private MakeFile make;
    private String texto;
    private File archivoFuente = null;
    private LinkedList<File> dependencias;
    private String rutaBase;

    ASTVisitor visitor = new ASTVisitor() {//Objeto que recorre
        @Override
        public int visit(IASTName name) {
            if ((name.getParent() instanceof CPPASTFunctionDeclarator)) {
                System.out.println(name.getClass().getSimpleName() + " " + name.getRawSignature() + " " + name.getParent().getClass().getSimpleName());
            }
            return 3;
        }

        @Override
        public int visit(IASTDeclaration declaration) {
            System.out.println(declaration + " " + declaration.getRawSignature());

            if ((declaration instanceof IASTSimpleDeclaration)) {
                IASTSimpleDeclaration ast = (IASTSimpleDeclaration) declaration;
                try {
                    System.out.println(ast.getSyntax().toString() + " " + ast.getChildren().length);
                    IASTNode typedef = ast.getChildren().length == 1 ? ast.getChildren()[0] : ast.getChildren()[1];
                    System.out.println(typedef);
                    IASTNode[] children = typedef.getChildren();
                    if ((children != null) && (children.length > 0)) {
                        System.out.println(children[0].getRawSignature());
                    }
                } catch (ExpansionOverlapsBoundaryException e) {
                    System.out.println("Error: " + e);
                }

                IASTDeclarator[] declarators = ast.getDeclarators();
                for (IASTDeclarator iastDeclarator : declarators) {
                    System.out.println(iastDeclarator.getName());
                }

                IASTAttribute[] attributes = ast.getAttributes();
                for (IASTAttribute iastAttribute : attributes) {
                    System.out.println(iastAttribute);
                }
            }

            if ((declaration instanceof IASTFunctionDefinition)) {
                IASTFunctionDefinition ast = (IASTFunctionDefinition) declaration;
                IScope scope = ast.getScope();
                try {
                    System.out.println(scope.getParent().getScopeName());
                    System.out.println(ast.getSyntax());
                } catch (DOMException | ExpansionOverlapsBoundaryException e) {
                    System.out.println("Error: " + e);
                }
                ICPPASTFunctionDeclarator typedef = (ICPPASTFunctionDeclarator) ast.getDeclarator();
                System.out.println(typedef.getName());
            }
            return 3;
        }

        @Override
        public int visit(IASTTypeId typeId) {
            System.out.println(typeId.getRawSignature());
            return 3;
        }

        @Override
        public int visit(IASTStatement statement) {
            System.out.println(statement.getRawSignature());
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

    public void parse() throws CoreException {
        //Preparar código sin comentarios.
        texto = new modelo.Utilidades().quitarComentariosJavaC(texto);

        //Obtener las funciones del código.
        System.out.println("Archivo servidor: " + this.archivoFuente.getAbsolutePath());

        //Para el proceso de los archivos fuentes.
        for (File dependencia : dependencias) {
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
                System.out.println("include - " + include.getName());
            }
            //recorrerArbol(translationUnit, 1);
            visitor.shouldVisitNames = true;
            visitor.shouldVisitDeclarations = false;
            visitor.shouldVisitDeclarators = true;
            visitor.shouldVisitAttributes = true;
            visitor.shouldVisitStatements = false; //Muestra el código de las declaraciones.
            visitor.shouldVisitTypeIds = false;

            translationUnit.accept(visitor);
        }
    }

    private void recorrerArbol(IASTNode node, int index) {
        IASTNode[] children = node.getChildren();

        boolean printContents = true;

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

        System.out.println(String.format(new StringBuilder("%1$").append(index * 2).append("s").toString(), new Object[]{"-"}) + node.getClass().getSimpleName() + offset + " -> " + (printContents ? node.getRawSignature().replaceAll("\n", " \\ ") : node.getRawSignature().subSequence(0, 5)));

        for (IASTNode iastNode : children) {
            recorrerArbol(iastNode, index + 1);
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

    public LinkedList<String> getFunciones() {
        return funciones;
    }

    public void setFunciones(LinkedList<String> funciones) {
        this.funciones = funciones;
    }

    public MakeFile getMake() {
        return make;
    }

    public void setMake(MakeFile make) {
        this.make = make;
    }

}
