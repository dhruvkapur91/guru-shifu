import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.IOException;

public class TryMethodNamePrinter {

    private static class MethodNamePrinter extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(MethodDeclaration aMethod, Void arg) {
            super.visit(aMethod, arg);
            System.out.println("Method name printed " + aMethod.getName());
        }
    }


    public static void main(String[] args) throws IOException {
        String path = "src/main/resources/ReverseStringTest.java";

        CompilationUnit compilationUnit = StaticJavaParser.parse(new File(path));
        MethodNamePrinter methodNamePrinter = new MethodNamePrinter();
        methodNamePrinter.visit(compilationUnit, null);
    }
}
