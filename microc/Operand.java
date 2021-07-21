/* MicroJava Code Operands  (HM 06-12-28)
   Marco Cristo, 2014
   =======================
An Operand stores the attributes of a value during code generation.
*/

public class Operand {
    public static final int  // item kinds
    Desconhecido = 0,
    Const  = 1,
    Local  = 2,
    Static = 3,
    Stack  = 4,
    Elem   = 5,
    Func   = 6;

    public int    cat;	// Const, Local, Static, Stack, Elem, Func
    public Struct tipo;	// item tipo
    public Obj    obj;  // Func
    public int    val;  // Const: value
    public int    end;  // Local, Static, Func: address

    public Operand(Operand other) {
        this.cat = other.cat;
        this.tipo = other.tipo;
        this.obj = other.obj;
        this.val = other.val;
        this.end = other.end;
    }
    
    public Operand(Obj o) {
        tipo = o.tipo;
        val = o.val;
        end = o.end;
        cat = Stack; // default
        switch (o.cat) {
        case Obj.Const:
            cat = Const;
            break;
        case Obj.Var:
            if (o.nivel == 0) cat = Static;
            else cat = Local;
            break;
        case Obj.Func:
            cat = Func;
            obj = o;
            break;
        case Obj.Tipo:
            System.out.println("identificador de tipo nao permitido aqui");
	    System.exit(1);
            break;
        default:
            System.out.println("categoria errada de indentificador = " + o.cat);
	    System.exit(1);
            break;
        }
    }

    public Operand(int val) {
        cat = Const;
        this.val = val;
        tipo = Tab.tipoInt;
    }

    public Operand(int cat, int val, Struct tipo) {
        this.cat = cat;
        this.val = val;
        this.tipo = tipo;
    }

}
