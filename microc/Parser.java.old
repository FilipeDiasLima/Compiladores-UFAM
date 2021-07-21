

import java.util.List;
import java.util.ArrayList;

class NDesig {
	String nome;
	boolean vec;
	public NDesig(String n, boolean v) {
		nome = n;
		vec = v;
	}
	public String getNome() {
		return nome;
	}
	public boolean vetor() {
		return vec;
	}
}



public class Parser {
	public static final int _EOF = 0;
	public static final int _id = 1;
	public static final int _strConst = 2;
	public static final int _num = 3;
	public static final int maxT = 34;

	static final boolean T = true;
	static final boolean x = false;
	static final int minErrDist = 2;

	public Token t;    // last recognized token
	public Token la;   // lookahead token
	int errDist = minErrDist;
	
	public Scanner scanner;
	public Errors errors;

	boolean temMain;
	private Tab ts;
	private Obj ofuncAtual;

	private Code objCode;

	private Struct getTipo(String tipo, boolean vec) { 
		Obj o = ts.buscar(tipo);
		Struct st = o.tipo;
		if (vec) st = new Struct(Struct.Vetor, o.tipo);
		return st;	
	}

	public void erro(String msg) {
		errors.SemErr(t.line, t.col, msg);
	}



	public Parser(Scanner scanner) {
		this.scanner = scanner;
		errors = new Errors();
	}

	void SynErr (int n) {
		if (errDist >= minErrDist) errors.SynErr(la.line, la.col, n);
		errDist = 0;
	}

	public void SemErr (String msg) {
		if (errDist >= minErrDist) errors.SemErr(t.line, t.col, msg);
		errDist = 0;
	}
	
	void Get () {
		for (;;) {
			t = la;
			la = scanner.Scan();
			if (la.kind <= maxT) {
				++errDist;
				break;
			}

			la = t;
		}
	}
	
	void Expect (int n) {
		if (la.kind==n) Get(); else { SynErr(n); }
	}
	
	boolean StartOf (int s) {
		return set[s][la.kind];
	}
	
	void ExpectWeak (int n, int follow) {
		if (la.kind == n) Get();
		else {
			SynErr(n);
			while (!StartOf(follow)) Get();
		}
	}
	
	boolean WeakSeparator (int n, int syFol, int repFol) {
		int kind = la.kind;
		if (kind == n) { Get(); return true; }
		else if (StartOf(repFol)) return false;
		else {
			SynErr(n);
			while (!(set[syFol][kind] || set[repFol][kind] || set[0][kind])) {
				Get();
				kind = la.kind;
			}
			return StartOf(syFol);
		}
	}
	
	void uBasic() {
		ofuncAtual = null;
		temMain=false;
		          	objCode = new Code();
		          	ts = new Tab(this);
		          	ts.abrirEscopo("Global");
		       
		while (la.kind == 4 || la.kind == 6 || la.kind == 10) {
			if (la.kind == 4) {
				Global();
			} else if (la.kind == 10) {
				Sub();
			} else {
				Funcao();
			}
		}
		if(ts.getNomeEscopo() == "Global" && temMain==false) {
		String nome = "main"; 
		ofuncAtual = ts.inserir(Obj.Func, nome, null);
		ts.abrirEscopo(nome); 
		objCode.setMainPC();
		ofuncAtual.end = objCode.getPC();
		ofuncAtual.nPars=0;
		objCode.put(objCode.enter);
		objCode.put(ofuncAtual.nPars);
		int pcvars = objCode.getPC();
		objCode.put(0);
		temMain=true;
		} 
		
		Instrucao();
		while (StartOf(1)) {
			Instrucao();
		}
		objCode.put(objCode.exit);
		objCode.put(objCode.return_);
		if(ts.getNomeEscopo() == "main") {
		ts.fecharEscopo();
		}
		objCode.setDataSize(ts.escopoAtual.nVars);
		             	ts.fecharEscopo(); 
	}

	void Global() {
		Expect(4);
		Expect(1);
		ts.inserir(Obj.Var, t.val, null); 
		while (la.kind == 5) {
			Get();
			Expect(1);
			ts.inserir(Obj.Var, t.val, null); 
		}
	}

	void Sub() {
		Expect(10);
		Expect(1);
		String nome = t.val; 
		ofuncAtual = ts.inserir(Obj.Func, t.val, null);
		ts.abrirEscopo("Func " + t.val); 
		Expect(7);
		if (nome.equals("main")) {
		objCode.setMainPC();
		temMain = true;
		} 
		if (la.kind == 1) {
			Get();
			ts.inserir(Obj.Var, t.val, null);
			ofuncAtual.nPars++; 
			while (la.kind == 5) {
				Get();
				Expect(1);
				ts.inserir(Obj.Var, t.val, null);
				ofuncAtual.nPars++; 
			}
		}
		Expect(8);
		ofuncAtual.end = objCode.getPC();
		objCode.put(objCode.enter);
		objCode.put(ofuncAtual.nPars);
		int pcvars = objCode.getPC();
		objCode.put(0); 
		Instrucoes();
		objCode.put(pcvars, ts.escopoAtual.nVars);
		objCode.put(objCode.exit);
		objCode.put(objCode.return_);
		ofuncAtual.locais = ts.escopoAtual.locais;
		ts.fecharEscopo(); 
		Expect(9);
		Expect(10);
	}

	void Funcao() {
		Expect(6);
		Expect(1);
		String nome = t.val; 
		ofuncAtual = ts.inserir(Obj.Func, t.val, null);
		ts.abrirEscopo("Func " + t.val); 
		Expect(7);
		if (nome.equals("main")) {
		objCode.setMainPC();
		temMain = true;
		}	
		if (la.kind == 1) {
			Get();
			ts.inserir(Obj.Var, t.val, null);
			ofuncAtual.nPars++; 
			while (la.kind == 5) {
				Get();
				Expect(1);
				ts.inserir(Obj.Var, t.val, null);
				ofuncAtual.nPars++; 
			}
		}
		Expect(8);
		ofuncAtual.end = objCode.getPC();
		objCode.put(objCode.enter);
		objCode.put(ofuncAtual.nPars);
		int pcvars = objCode.getPC();
		objCode.put(0);
		
		Instrucoes();
		objCode.put(pcvars, ts.escopoAtual.nVars);
		objCode.put(objCode.exit);
		objCode.put(objCode.return_);
		objCode.put(objCode.trap);
		       objCode.put(1);
		ofuncAtual.locais = ts.escopoAtual.locais;
		ts.fecharEscopo(); 
		Expect(9);
		Expect(6);
	}

	void Instrucao() {
		int opr; 
		switch (la.kind) {
		case 11: {
			Dim();
			break;
		}
		case 1: {
			AssignCall();
			break;
		}
		case 13: {
			If();
			break;
		}
		case 16: {
			While();
			break;
		}
		case 17: {
			For();
			break;
		}
		case 20: {
			Return();
			break;
		}
		case 21: {
			Print();
			break;
		}
		case 22: {
			Input();
			break;
		}
		default: SynErr(35); break;
		}
	}

	void Instrucoes() {
		while (StartOf(1)) {
			Instrucao();
		}
	}

	void Dim() {
		Expect(11);
		Expect(1);
		ts.inserir(Obj.Var, t.val, null); 
		while (la.kind == 5) {
			Get();
			Expect(1);
			ts.inserir(Obj.Var, t.val, null); 
		}
	}

	void AssignCall() {
		Operand o2 = null; 	Operand o1 = null; 
		Expect(1);
		String nome = t.val;
		o1 = new Operand(ts.buscar(nome)); 
		if (la.kind == 12) {
			Get();
			o2 = Exp();
			objCode.assign(o1, o2); 
		} else if (la.kind == 7) {
			ParametrosReais(o1);
			objCode.put(objCode.call);
			   	objCode.put2(o1.end); 
		} else SynErr(36);
	}

	void If() {
		int opr1, opr2, end; 
		Expect(13);
		opr1 = Condicao();
		end = objCode.putFalseJump(opr1, 0); 
		Instrucoes();
		while (la.kind == 14) {
			Get();
			int end1 = objCode.putJump(0);
			     objCode.fixup(end); 
			opr2 = Condicao();
			end1 = objCode.putFalseJump(opr2, 0); 
			Instrucoes();
			objCode.fixup(end1); 
		}
		if (la.kind == 15) {
			Get();
			int end2 = objCode.putJump(0);
			       objCode.fixup(end); 
			Instrucoes();
			objCode.fixup(end2); 
		}
		Expect(9);
		Expect(13);
		objCode.fixup(end); 
	}

	void While() {
		int opr, end; 
		Expect(16);
		int top = objCode.getPC(); 
		opr = Condicao();
		end = objCode.putFalseJump(opr, 0); 
		Instrucoes();
		objCode.putJump(top);
		objCode.fixup(end);
		         
		Expect(9);
		Expect(16);
	}

	void For() {
		int opr = -1; int end, end2, opr1; Operand op, op2, o2, opx, opy; opx = opy = op = op2 = null; 
		Expect(17);
		Expect(1);
		String nome = t.val;
		opx = new Operand(ts.buscar(nome));
		op = new Operand(ts.buscar(nome)); 
		Expect(12);
		o2 = Exp();
		objCode.assign(opx, o2); 
		Expect(18);
		opy = Exp();
		int antes_incremento = objCode.getPC();
		objCode.load(opx); 
		opr = objCode.le; 
		objCode.load(opy);
		end = objCode.putFalseJump(opr, 0);
		
		Instrucoes();
		opr1 = objCode.add;
		op2 = new Operand(Integer.parseInt("1"));
		objCode.load(op);
		objCode.load(op2);
		objCode.put(opr1);
		
		Expect(19);
		Expect(1);
		op = new Operand(ts.buscar(t.val));
		objCode.store(op);
		objCode.putJump(antes_incremento);
		 objCode.fixup(end); 
	}

	void Return() {
		Operand op = null; 
		Expect(20);
		op = Exp();
		objCode.load(op);
		objCode.put(Code.exit);
		objCode.put(Code.return_);  
	}

	void Print() {
		String s; Operand op; 
		Expect(21);
		if (la.kind == 2) {
			Get();
			s = t.val.substring(1, t.val.length() - 1);
			objCode.putPrintStrz(s); 
		} else if (StartOf(2)) {
			op = Exp();
			objCode.load(op);
			objCode.put(objCode.printi); 
		} else SynErr(37);
		while (la.kind == 5) {
			Get();
			if (la.kind == 2) {
				Get();
				s = t.val.substring(1, t.val.length() - 1);
				  objCode.putPrintStrz(s); 
			} else if (StartOf(2)) {
				op = Exp();
				objCode.load(op);
				 objCode.put(objCode.printi); 
			} else SynErr(38);
		}
	}

	void Input() {
		Operand op;  int i; String s; 
		Expect(22);
		if (la.kind == 2) {
			Get();
			s = t.val.substring(1, t.val.length() - 1);
			objCode.putPrintStrz(s); 
			Expect(5);
		}
		Expect(1);
		objCode.put(objCode.scani); 
		op = new Operand(ts.buscar(t.val));
		objCode.store(op);
		
	}

	Operand  Exp() {
		Operand  op;
		Operand op2; int opr; op = null; 
		if (la.kind == 1 || la.kind == 3 || la.kind == 7) {
			op = T();
		} else if (la.kind == 29) {
			Get();
			op = T();
			if (op.cat == Operand.Const)
			op.val = -op.val;
			                   	else {
			   objCode.load(op);
			objCode.put(objCode.neg);
			}
			                
		} else SynErr(39);
		while (la.kind == 29 || la.kind == 30) {
			if (la.kind == 30) {
				Get();
				opr = objCode.add; 
			} else {
				Get();
				opr = objCode.sub; 
			}
			objCode.load(op); 
			op2 = T();
			objCode.load(op2);
			objCode.put(opr); 
			
		}
		return op;
	}

	void ParametrosReais(Operand op) {
		Operand oppar; 
		Expect(7);
		if (op.cat != Operand.Func) {
		erro("nao Ã© uma funÃ§Ã£o");
		           	op.obj = ts.semObj;
		          	}
		          	int preais = 0;
		          	int pdecl = op.obj.nPars;
		          	Obj fp = op.obj.locais;
		       
		if (StartOf(2)) {
			oppar = Exp();
			objCode.load(oppar);
			preais ++; 
			while (la.kind == 5) {
				Get();
				oppar = Exp();
				objCode.load(oppar);
				preais ++; 
			}
		}
		if (preais > pdecl)
		erro("mais parametros passados que declarados");
		                	else if (preais < pdecl)
		erro("menos parametros passados que declarados"); 
		             
		Expect(8);
	}

	int  Condicao() {
		int  opr;
		Operand opx, opy; opx = opy = null; 
		opx = Exp();
		objCode.load(opx); 
		opr = OpRel();
		opy = Exp();
		objCode.load(opy); 
		return opr;
	}

	int  OpRel() {
		int  opr;
		opr = -1; 
		switch (la.kind) {
		case 23: {
			Get();
			opr = objCode.eq; 
			break;
		}
		case 24: {
			Get();
			opr = objCode.ne; 
			break;
		}
		case 25: {
			Get();
			opr = objCode.gt; 
			break;
		}
		case 26: {
			Get();
			opr = objCode.lt; 
			break;
		}
		case 27: {
			Get();
			opr = objCode.ge; 
			break;
		}
		case 28: {
			Get();
			opr = objCode.le; 
			break;
		}
		default: SynErr(40); break;
		}
		return opr;
	}

	Operand  T() {
		Operand  op;
		Operand op2; int opr; op = null; 
		op = P();
		while (la.kind == 31 || la.kind == 32 || la.kind == 33) {
			if (la.kind == 31) {
				Get();
				opr = objCode.mul; 
			} else if (la.kind == 32) {
				Get();
				opr = objCode.div; 
			} else {
				Get();
				opr = objCode.rem; 
			}
			objCode.load(op); 
			op2 = P();
			objCode.load(op2);
			     objCode.put(opr); 
		}
		return op;
	}

	Operand  P() {
		Operand  op;
		op = null; List<Integer> list = null; String tp; 
		if (la.kind == 1) {
			Get();
			String nome = t.val;   
			op = new Operand(ts.buscar(nome));
			
			if (la.kind == 7) {
				ParametrosReais(op);
				objCode.put(objCode.call);
				objCode.put2(op.end); 
				op.cat = Operand.Stack; 
			}
		} else if (la.kind == 3) {
			Get();
			op = new Operand(Integer.parseInt(t.val)); 
		} else if (la.kind == 7) {
			Get();
			op = Exp();
			Expect(8);
		} else SynErr(41);
		return op;
	}



	public void Parse() {
		la = new Token();
		la.val = "";		
		Get();
		uBasic();
		Expect(0);

	}

	private static final boolean[][] set = {
		{T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,T,x,x, x,x,x,x, x,x,x,T, x,T,x,x, T,T,x,x, T,T,T,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,T,x,T, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x}

	};
} // end Parser


class Errors {
	public int count = 0;                                    // number of errors detected
	public java.io.PrintStream errorStream = System.out;     // error messages go to this stream
	public String errMsgFormat = "-- line {0} col {1}: {2}"; // 0=line, 1=column, 2=text
	
	protected void printMsg(int line, int column, String msg) {
		StringBuffer b = new StringBuffer(errMsgFormat);
		int pos = b.indexOf("{0}");
		if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, line); }
		pos = b.indexOf("{1}");
		if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, column); }
		pos = b.indexOf("{2}");
		if (pos >= 0) b.replace(pos, pos+3, msg);
		errorStream.println(b.toString());
	}
	
	public void SynErr (int line, int col, int n) {
		String s;
		switch (n) {
			case 0: s = "EOF expected"; break;
			case 1: s = "id expected"; break;
			case 2: s = "strConst expected"; break;
			case 3: s = "num expected"; break;
			case 4: s = "\"Global\" expected"; break;
			case 5: s = "\",\" expected"; break;
			case 6: s = "\"Function\" expected"; break;
			case 7: s = "\"(\" expected"; break;
			case 8: s = "\")\" expected"; break;
			case 9: s = "\"End\" expected"; break;
			case 10: s = "\"Sub\" expected"; break;
			case 11: s = "\"Dim\" expected"; break;
			case 12: s = "\"=\" expected"; break;
			case 13: s = "\"If\" expected"; break;
			case 14: s = "\"ElseIf\" expected"; break;
			case 15: s = "\"Else\" expected"; break;
			case 16: s = "\"While\" expected"; break;
			case 17: s = "\"For\" expected"; break;
			case 18: s = "\"To\" expected"; break;
			case 19: s = "\"Next\" expected"; break;
			case 20: s = "\"Return\" expected"; break;
			case 21: s = "\"Print\" expected"; break;
			case 22: s = "\"Input\" expected"; break;
			case 23: s = "\"==\" expected"; break;
			case 24: s = "\"!=\" expected"; break;
			case 25: s = "\">\" expected"; break;
			case 26: s = "\"<\" expected"; break;
			case 27: s = "\">=\" expected"; break;
			case 28: s = "\"<=\" expected"; break;
			case 29: s = "\"-\" expected"; break;
			case 30: s = "\"+\" expected"; break;
			case 31: s = "\"*\" expected"; break;
			case 32: s = "\"/\" expected"; break;
			case 33: s = "\"%\" expected"; break;
			case 34: s = "??? expected"; break;
			case 35: s = "invalid Instrucao"; break;
			case 36: s = "invalid AssignCall"; break;
			case 37: s = "invalid Print"; break;
			case 38: s = "invalid Print"; break;
			case 39: s = "invalid Exp"; break;
			case 40: s = "invalid OpRel"; break;
			case 41: s = "invalid P"; break;
			default: s = "error " + n; break;
		}
		printMsg(line, col, s);
		count++;
	}

	public void SemErr (int line, int col, String s) {	
		printMsg(line, col, s);
		count++;
	}
	
	public void SemErr (String s) {
		errorStream.println(s);
		count++;
	}
	
	public void Warning (int line, int col, String s) {	
		printMsg(line, col, s);
	}
	
	public void Warning (String s) {
		errorStream.println(s);
	}
} // Errors


class FatalError extends RuntimeException {
	public static final long serialVersionUID = 1L;
	public FatalError(String s) { super(s); }
}
