// MicroJava Virtual Machine
// -- veeeery small Java Integer Virtual Machine!
// version by Marco Cristo, 2014 
// -- this version includes scani opcode
// -------------------------
// Syntax: java MJ.Run fileName [-debug]
//=============================================================================

import java.io.*;
import java.util.Scanner;

public class VMRun {
    static byte code[];		// code array
    static int data[];		// global data
    static int heap[];		// dynamic heap
    static int stack[];		// expression stack
    static int local[];		// method stack
    static int dataSize;	// size of global data area
    static int startPC;		// address of main() method
    static int startStrz;	// address of list of null-terminated string constants
    static int pc;			// program counter
    static int fp, sp;		// frame pointer, stack pointer on method stack
    static int esp;			// expression stack pointer
    static int free;		// next free heap address
    static int codeSize;
    static boolean debug;	// debug output on or off
    
    static final int
    heapSize = 100000,		// size of the heap in words
    mStackSize = 400,		// size of the method stack in words
    eStackSize = 30;		// size of the expression stack in words
    
    static final int		// instruction codes
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
    trap		= 31;
    
    static final int  // compare operators
    eq = 0,
    ne = 1,
    lt = 2,
    le = 3,
    gt = 4,
    ge = 5;
    
    static String[] opcode = {
    "???     ", "load    ", "store   ", "getstati", "putstati",
    "const   ", "add     ", "sub     ", "mul     ", "div     ",
	"rem     ", "neg     ", "newarray", "aload   ", "astore  ",
	"arraylen", "pop     ", "jmp     ", "jeq     ", "jne     ",
	"jlt     ", "jle     ", "jgt     ", "jge     ", "call    ",
	"return  ", "enter   ", "exit    ", "printi  ", "scani   ",
	"prints  ", "trap    "
    };
    
    //----- expression stack
    
    static void push(int val) throws VMError {
        if (esp == eStackSize) throw new VMError("overflow na pilha de expressão");
        stack[esp++] = val;
    }
    
    static int pop() throws VMError {
        if (esp == 0) throw new VMError("POP em pilha de expressão vazia");
        return stack[--esp];
    }
    
    //----- method stack
    
    static void PUSH(int val) throws VMError {
        if (sp == mStackSize) throw new VMError("overflow na pilha de função");
        local[sp++] = val;
    }
    
    static int POP() throws VMError {
        if (sp == 0) throw new VMError("POP em pilha de função vazia");
        return local[--sp];
    }
    
    //----- instruction fetch
    
    static byte next() {
        return code[pc++];
    }
    
    static short next2() {
        return (short)(((next() << 8) + (next() & 0xff)) << 16 >> 16);
    }
    
    static int next4() {
        return (next2() << 16) + (next2() & 0xffff);
    }
    
    //----- VM internals
    
    static void load(String name) throws IOException, FormatException {
        byte sig[] = new byte[2];
        DataInputStream in = new DataInputStream(new FileInputStream(name));
        in.read(sig, 0, 2);
        if (sig[0] != 'U' || sig[1] != 'C') 
        	throw new FormatException("marcador errado");
        codeSize = in.readInt();
        if (codeSize <= 0) 
        	throw new FormatException("codeSize <= 0");
        dataSize = in.readInt();
        if (dataSize < 0) 
        	throw new FormatException("dataSize < 0");
        startPC = in.readInt();
        if (startPC < 0) 
        	throw new FormatException("startPC < 0");
        startStrz = in.readInt();
        if (startStrz < 0 || startStrz > codeSize) 
        	throw new FormatException("startStrz fora da área de código");
        code = new byte[codeSize];
        in.read(code, 0, codeSize);
    }
    
    static int alloc(int size) throws VMError { // allocate heap block of size bytes
        int adr = free;
        free += size;
        if (free > heapSize) throw new VMError("overflow no heap");
        return adr;
    }
    
    //----- debug output
    
    static void printNum(int val, int n) {
        String s = new Integer(val).toString();
        int len = s.length();
        while (len < n) {
            System.out.print(" ");
            len++;
        }
        System.out.print(s);
    }
    
    static void printInstr() {
        int op = code[pc - 1];
        String instr = op > 0 && op <= trap ? opcode[op] : "???     ";
        printNum(pc - 1, 4);
        System.out.print(": " + instr + "| ");
    }
    
    static void printStack() {
        for (int i = 0; i < esp; i++) System.out.print(stack[i] + " ");
        System.out.println();
    }
    
    //----- actual interpretation
    
    static void interpret() {
        int op, adr, off, idx, len, i;
		int val, val2;
		Scanner input = new Scanner(System.in);
        pc = startPC;
        try {
            for (;;) { // terminated by return instruction
                op = next();
                if (debug) printInstr();
                switch((int)op) {
                        
                    // load/store local variables
                    case load:
                        push(local[fp + next()]);
                        break;
                    case store:
                        local[fp + next()] = pop();
                        break;
                        
                    // load/store global variables
                    case getstatic:
                        push(data[next2()]);
                        break;
                    case putstatic:
                        data[next2()] = pop();
                        break;
                        
                    // load constants
                    case const_:
                        push(next4());
                        break;
                        
                    // arithmetic operations
                    case add:
                        push(pop() + pop());
                        break;
                    case sub:
                        push(-pop() + pop());
                        break;
                    case mul:
                        push(pop() * pop());
                        break;
                    case div:
                        val = pop();
                        if (val == 0) throw new VMError("divisão por zero");
                        push(pop() / val);
                        break;
                    case rem:
                        val = pop();
                        if (val == 0) throw new VMError("divisão por zero");
                        push(pop() % val);
                        break;
                    case neg:
                        push(-pop());
                        break;
                        
                    // array creation
                    case newarray:
                        len = (int)pop();
                        adr = alloc(1 + len);
                        heap[adr] = len;
                        push(adr);
                        break;
                        
                    // array access
                    case aload:
                        idx = (int)pop();
                        adr = (int)pop();
                        if (adr == 0) throw new VMError("uso de referência nula");
                        len = (int)heap[adr];
                        if (idx < 0 || idx >= len) throw new VMError("índice fora do limite");
                        push(heap[adr+1+idx]);
                        break;
                    case astore:
                        val = pop();
                        idx = (int)pop();
                        adr = (int)pop();
                        if (adr == 0) throw new VMError("uso de referência nula");
                        len = (int)heap[adr];
                        if (idx < 0 || idx >= len) throw new VMError("índice fora do limite");
                        heap[adr+1+idx] = val;
                        break;
                    case arraylength:
                        adr = (int)pop();
                        if (adr==0) throw new VMError("uso de referência nula");
                        push(heap[adr]);
                        break;
                        
                    // stack manipulation
                    case pop:
                        pop();
                        break;
                        
                    // jumps
                    case jmp:
                        adr = next2();
                        pc = adr;
                        break;
                    case jeq:
                    case jne:
                    case jlt:
                    case jle:
                    case jgt:
                    case jge:
                        adr = next2();
                        val2 = pop();
                        val = pop();
                        boolean cond = false;
                        switch(op) {
                            case jeq:
                                cond = val == val2;
                                break;
                            case jne:
                                cond = val != val2;
                                break;
                            case jlt:
                                cond = val < val2;
                                break;
                            case jle:
                                cond = val <= val2;
                                break;
                            case jgt:
                                cond = val > val2;
                                break;
                            case jge:
                                cond = val >= val2;
                                break;
                        }
                        if (cond) pc = adr;
                        break;
                        
                    // method calls
                    case call:
                        adr = next2();
                        PUSH(pc);
                        pc = adr;
                        break;
                    case return_:
                        if (sp == 0) return;
                        else pc = (int)POP();
                        break;
                    case enter:
                        int psize = next();
                        int lsize = next();
                        PUSH(fp);
                        fp = sp;
                        for (i = 0; i < lsize; i++) PUSH(0);
                        for (i = psize - 1; i >= 0; i--) local[fp + i] = pop();
                        break;
                    case exit:
                        sp = fp;
                        fp = (int)POP();
                        break;
                        
                    // I/O
                    case printi:
                        val = pop();
                        if (debug) System.out.print("\n OUT: [");
                        System.out.print(new Integer(val).toString());
                        if (debug) System.out.print("]");
                        break;
                    case prints:
                        adr = next2();
                        if (debug) System.out.print("\n OUT: [");
                        for (i = adr; code[i] != 0 && i < codeSize; i++) {
                            if (debug) {
                                char c = (char)code[i];
                                switch (c) {
                                    case '\n': System.out.print("\\n"); break;
                                    case '\r': System.out.print("\\r"); break;
                                    case '\t': System.out.print("\\t"); break;
                                    case '\0': System.out.print("\\0"); break;
                                    default: System.out.print(c);
                                }
                            } else
                                System.out.print((char)code[i]);
		    			}
               			if (debug) System.out.print("]");
                        break;
                    case scani:
						try {
    						val = Integer.parseInt(input.nextLine());
						} catch (NumberFormatException e) {
    						val = 0; // assumes 0 if user has not typed a number
						}
                        push(val);
                        break;
                        
                    // Other
                    case trap:
                        throw new VMError("trap(" + next() + ")");
                    default:
                        throw new VMError("opcode desconhecido " + op);
                }
                if (debug) printStack();
            }
        } catch (VMError e) {
            System.out.println("\n-- exceção no endereço " + (pc-1) + ": " + e.getMessage());;
        }
    }
    
    public static void main(String[] arg) {
        String fileName = null;
        debug = false;
        for (int i = 0; i < arg.length; i++) {
            if (arg[i].equals("-debug"))
                debug = true;
            else fileName = arg[i];
        }
        if (fileName == null) {
            System.out.println("Uso: java VMRun objeto [-debug]");
            return;
        }
        try {
            load(fileName);
            heap  = new int[heapSize];		// fixed sized heap
            data  = new int[dataSize];		// global data as specified in classfile
            stack = new int[eStackSize];		// expression stack
            local = new int[mStackSize];		// method stack
            fp = 0;
            sp = 0;
            esp = 0;
            free = 1;					// no block should start at address 0
            long startTime = System.currentTimeMillis();
            interpret();
            System.out.println("\nExecutado em " + (System.currentTimeMillis()-startTime) + " ms");
        } catch (FileNotFoundException e) {
            System.out.println("-- arquivo " + fileName + " não encontrado");
        } catch (IOException e) {
            System.out.println("-- erro ao ler arquivo " + fileName);
        } catch (FormatException e) {
            System.out.println("-- arquivo objeto corrompido " + fileName + ": " + e.getMessage());
        }
    }
}

class FormatException extends Exception {
    FormatException(String s) {
        super(s);
    }
}

class VMError extends Exception {
    VMError(String s) {
        super(s);
    }
}

















