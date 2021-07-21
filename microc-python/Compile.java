import java.io.*;

public class Compile
{
    public static void main(String argv[])
    {
        if (argv.length > 0) {
            String source = argv[0];
            Scanner s = new Scanner(source);
            Parser p = new Parser(s);
            p.Parse();
            System.out.println("-- errors: " + p.errors.count);
        } else 
            System.out.println("-- uso: java Compiler <arquivo-fonte>");
    }
}

