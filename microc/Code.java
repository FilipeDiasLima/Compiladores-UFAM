/* MicroJava Code Generator  (HM 06-12-28) used as base
 *  then modified by Alexis Metaireau and Maxime Hardy (Feb.-Mar. 2011)
 *  then modified by Marco Cristo (2013, 2014) 
========================
This class holds the code buffer with its access primitives get* and put*.
It also holds methods to load operands and to generate complex instructions
such as assignments and jumps.
 */
import java.io.*;

public class Code {

    public static final byte // instruction codes
    	load        =  1,
    	store       =  2,
    	getstatic   =  3,
    	putstatic   =  4,
    	const_      =  5,
    	add         =  6,
    	sub         =  7,
    	mul         =  8,
    	div         =  9,
    	rem         = 10,
    	neg         = 11,
    	newarray    = 12,
    	aload       = 13,
    	astore      = 14,
    	arraylength = 15,
    	pop         = 16,
    	jmp         = 17,
    	jeq         = 18,
    	jne         = 19,
    	jlt         = 20,
    	jle         = 21,
    	jgt         = 22,
    	jge         = 23,
    	call        = 24,
    	return_     = 25,
    	enter       = 26,
    	exit        = 27,
    	printi      = 28,
    	scani       = 29,
    	prints      = 30,
    	trap	    = 31;
    public static final int // compare operators
            eq = 0,
            ne = 1,
            lt = 2,
            le = 3,
            gt = 4,
            ge = 5;
    private static int[] inverse = {ne, eq, ge, gt, le, lt};
    private static final int bufSize = 8192;
    public static byte[] buf;
    public static int pc;	        // next free byte in code buffer
    public static int mainPc;	    // pc of main function (set by parser)
    public static int dataSize;	    // length of static data in words (set by parser)
    public static int startStrz;	// address of list of null-terminated string constants
    public static String[] strzbuf; 
    public static int[] strzadr; 
    public static int strzCount;	// number of strz consts in program
    public static int cur;			// address of next byte to decode
    public static int adr;			// address of currently decoded instruction
    public static final Operand noOp = new Operand(Operand.Desconhecido, 0, Tab.semTipo);

    public Code() {
		init();
    }

    public int getPC() {
		return pc;
    }

    public void setMainPC() {
		mainPc = pc;
    }

    public void setDataSize(int n) {
		dataSize = n;
    }

    //--------------- code buffer access ----------------------
    public static void put(int x) {
        if (pc >= bufSize) {
            if (pc == bufSize) {
                System.out.println("programa com mais de " + bufSize + " bytes.");
				System.exit(1);
            }
            pc++;
        } else {
            buf[pc++] = (byte) x;
        }
    }

    public static void put(int pos, int x) {
        int oldpc = pc;
        pc = pos;
        put(x);
        pc = oldpc;
    }

    public static void put2(int x) {
        put(x >> 8);
        put(x);
    }

    public static void put2(int pos, int x) {
        int oldpc = pc;
        pc = pos;
        put2(x);
        pc = oldpc;
    }

    public static void put4(int x) {
        put2(x >> 16);
        put2(x);
    }

    public static int get(int pos) {
        return buf[pos];
    }

    //----------------- instruction generation --------------
    /**
     * Load an operand on the estack regardless its type.
     *
     * @param x The operand to load
     */
    public static void load(Operand x) {
        switch (x.cat) {
            case Operand.Const:
                put(const_);
                if (x.tipo == Tab.semTipo) 
                    x.val = 0;
                put4(x.val);
                break;
            case Operand.Elem:
                // for arrays, the address and index are already on the stack
                put(aload);
                break;
            case Operand.Local:
                put(load);
                put(x.end);
                break;
            case Operand.Static:
                put(getstatic);
                put2(x.end);
                break;
            case Operand.Stack:
                break;
            default:
                System.out.println("impossivel carregar este valor");
				System.exit(1);
        }
        x.cat = Operand.Stack; // change the type to the stack, to mark it as loaded.
    }

    // store stack value in operand regardless its type
    public static void store(Operand x) {
        switch (x.cat) {
            case Operand.Static:
                put(putstatic);
                put2(x.end);
                break;
            case Operand.Local:
                put(store);
                put(x.end);
                break;
            case Operand.Elem:
                // if it is an element, the address, index and value are on the stack
                put(astore);
				break;
            default:
                System.out.println("assinalamento nao eh suportado com este operando: " + x.cat);
				System.exit(1);
        }
    }

    // Generate an assignment x = y
    public static void assign(Operand x, Operand y) {
        load(y);
        store(x);
    }

    /**
     * Unconditional jump
     *
     * @param adr
     * @return the address to potentially fixup
     */
    public static int putJump(int adr) {
        put(jmp);
        int fixup = pc;
        put2(adr);
        
        return fixup;
    }

    /**
     * Conditional jump if op is false
     * 
     * @param op
     * @param adr
     * @return the address to potentially fixup
     */
    public static int putFalseJump(int op, int adr) {
        put((jeq + inverse[op]));
        int fixup = pc;
        put2(adr);
        
        return fixup;
    }

    // patch jump target at adr so that it jumps to the current pc
    public static void fixup(int adr) {
        // store the actual value of pc
        put2(adr, pc);
    }

    //------------------------------------
    // put code for print constant strings
    public static void putPrintStrz(String s) {
		strzbuf[strzCount] = s;
        put(prints);
		strzadr[strzCount] = pc;
        put2(strzCount);
		strzCount ++;
    }

    //------------------------------------
    // initialize code buffer
    public static void init() {
        buf = new byte[bufSize];
		strzbuf = new String[bufSize];
		strzadr = new int[bufSize];
        pc = 0;
        mainPc = -1;
        dataSize = 0;
		startStrz = 0;
		strzCount = 0;
    }

    // Write the code buffer to the output stream
    public static void write(OutputStream s) throws IOException {
        int codeSize, i, j;
		char c;
        try {
			startStrz = pc;
			for (i = 0; i < strzCount; i ++) {
				put2(strzadr[i], pc);
				for (j = 0; j < strzbuf[i].length(); j ++) {
					c = strzbuf[i].charAt(j);
					if (c == '\\') {
						c = strzbuf[i].charAt(++ j);
						switch (c) {
							case 'n': c = '\n'; break;
							case 'r': c = '\r'; break;
							case 't': c = '\t'; break;
						}
					}
					put(c);
				}
				put(0);
			}	
            codeSize = pc;
            put('U');
            put('C');
            put4(codeSize);
            put4(dataSize);
            put4(mainPc);
            put4(startStrz);
            s.write(buf, codeSize, pc - codeSize);	// header
            s.write(buf, 0, codeSize);			    // code
            s.close();
            decodeHeader(buf, codeSize);            // show header
            decode(buf, 0, startStrz, codeSize);    // show source code
        } catch (IOException e) {
            System.out.println("impossivel salvar arquivo de saida");
	    	System.exit(1);
        }
    }
/*
    public static String typeName(Operand op){
        if (op.type == null){
            return "Null";
        }
        return Struct.kindNames[op.type.kind];
    }

    public static String typeName(Obj o){
        return Struct.kindNames[o.kind];
    }
*/
    // Decode

    private static int get(byte[] code) {
        return ((int)code[cur++])<<24>>>24;
    }

    private static int get2(byte[] code) {
        return (get(code)*256 + get(code))<<16>>16;
    }

    private static int get4(byte[] code) {
        return (get2(code)<<16) + (get2(code)<<16>>>16);
    }

    private static void P(String s) {
        System.out.println(adr + ": " + s);
    }

    public static void decode(byte[] c, int off, int len, int cSize) {
    	cur = off;			// address of next byte to decode
    	adr = cur;			// address of currently decoded instruction
        int op;
        while (cur < len) {
            switch(get(c)) {
            case load:
                P("load "+get(c));
                break;
            case store:
                P("store "+get(c));
                break;
            case getstatic:
                P("getstatic "+get2(c));
                break;
            case putstatic:
                P("putstatic "+get2(c));
                break;
            case const_:
                P("const "+get4(c));
                break;
            case add:
                P("add");
                break;
            case sub:
                P("sub");
                break;
            case mul:
                P("mul");
                break;
            case div:
                P("div");
                break;
            case rem:
                P("rem");
                break;
            case neg:
                P("neg");
                break;
            case newarray:
                P("newarray ");
                break;
            case aload:
                P("aload");
                break;
            case astore:
                P("astore");
                break;
            case arraylength:
                P("arraylength");
                break;
            case pop:
                P("pop");
                break;
            case jmp:
                P("jmp "+get2(c));
                break;
            case jeq:
                P("jeq "+get2(c));
                break;
            case jne:
                P("jne "+get2(c));
                break;
            case jlt:
                P("jlt "+get2(c));
                break;
            case jle:
                P("jle "+get2(c));
                break;
            case jgt:
                P("jgt "+get2(c));
                break;
            case jge:
                P("jge "+get2(c));
                break;
            case call:
                P("call "+get2(c));
                break;
            case return_:
                P("return");
                break;
            case enter:
                P("enter "+get(c)+" "+get(c));
                break;
            case exit:
                P("exit");
                break;
            case printi:
                P("printi");
                break;
            case scani:
                P("scani");
                break;
            case prints:
                P("prints "+get2(c));
                break;
            case trap:
                P("trap "+get(c));
                break;
            default:
                P("-- error--");
                break;
            }
	    	adr = cur;
        }
		if (cur < cSize)
			System.out.print(cur + ": ");
			
		while (cur < cSize) {
			if (c[cur] == 0) {
				if (cur < cSize-1)
						System.out.print("\n" + (cur+1) + ": ");
				else
						System.out.print("\n");
			} else if (c[cur] == '\n') 	
				System.out.print("\\n");
			else if (c[cur] == '\r') 	
				System.out.print("\\r");
			else if (c[cur] == '\t') 	
				System.out.print("\\t");
			else if (c[cur] > 127) 	
				System.out.print(cur + "\\?");
			else	
				System.out.print((char)c[cur]);	
			cur ++;
		}
    }

    public static void decodeHeader(byte[] c, int cSize) {
    	cur = cSize;
        System.out.println("-----------------------");
        System.out.println(" marcador: " + (char)get(c) + (char)get(c));
        System.out.println(" codeSize: " + get4(c));
        System.out.println(" dataSize: " + get4(c));
        System.out.println("   mainPC: " + get4(c));
        System.out.println("startStrz: " + get4(c));
        System.out.println("-----------------------");
    }

}

