

import java.io.*;
import java.util.*;

class Global {
  public ArrayList<Funcao> funcoes;
  public ArrayList<String> globalVars;

  public Global() {
    funcoes = new ArrayList<Funcao>();
    globalVars = new ArrayList<String>();
  }

  public void addGlobalVar(String globalVar) {
    globalVars.add(globalVar);
  }

  public void addFunction(Funcao funcao) {
    funcoes.add(funcao);
  }

  public String getGlobalVars() {
    String vars="";
    for(int i=0;i<globalVars.size();i++) {
      vars += "  " + globalVars.get(i) + " = 0" + "\n";
    }
    return vars;
  }

  public String getGlobalVar(String globalVar) {
    for(int i=0;i<globalVars.size();i++) {
      if(globalVars.get(i).equals(globalVar)) {
        return "_global_." + globalVars.get(i);
      }
    }
    return "";
  }

  public void getFunctions() {
    for(int i=0;i<funcoes.size();i++) {
      System.out.println(funcoes.get(i).retornaFuncao() + "\n");
    }
    if(globalVars.size()>0) {
      System.out.println("class _global_:");
      System.out.println(getGlobalVars()); 
    }
    System.out.println("main()");
  }
}

class Funcao {
  String nome;
  ArrayList<String> parametros = new ArrayList<String>();
  Bloco bloco;

  public Funcao() {
    this.nome = nome;
  }

  public void setName(String nome) {
    this.nome = nome;
  }

  public String getName() {
    return this.nome;
  }

  public void addParam(String param) {
    parametros.add(param);
  }

  public String getParams() {
    String params = "";
    for(int i=0;i<parametros.size();i++) {
      if(i+1 == parametros.size()) {
        params += parametros.get(i);  
      } else {
        params += parametros.get(i) + ", ";
      }
    }
    return params;
  }

  public void setBloco(Bloco bloco) {
    this.bloco = bloco;
  }

  public String retornaFuncao() {
    return "def " + nome + "(" + getParams() + "):" + "\n" + bloco.getBloco();
  }
}

class Bloco {
  public ArrayList<Instruction> instrucoes;

  public Bloco() {
    instrucoes = new ArrayList<Instruction>();
  }

  public void removeInstrucao(Instruction instr) {
    this.instrucoes.remove(instr);
  }

  public void addInstrucao(Instruction instr) {
    instrucoes.add(instr);
  }

  public String getBloco() {
    String bloco="";
    for(int i=0;i<instrucoes.size();i++) {
      if(i+1==instrucoes.size()) {
        bloco += instrucoes.get(i).getInstrucao();
      } else {
        bloco += instrucoes.get(i).getInstrucao() + "\n";
      }
    }
    return bloco;
  }
}

class Instruction {
  String instr;
  NDesig nd;

  public void setInstrucao(String instr, Integer tabs) {
    String tab="";
    for(int i=0;i<tabs;i++) {
      tab+="  ";
    }
    this.instr = tab + instr;
  }

  public String getInstrucao() {
    return this.instr;
  }
}

class NDesig {
	String nome;
	boolean vec;
	public NDesig(String n, boolean v) {
		nome = n;
		vec = v;
	}
	public String getName() {
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

	Global global;
  Funcao func;
  Bloco bloco;
  Instruction instr;
  Integer cont=0;

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
	
	void Python() {
		global = new Global();
		func = new Funcao();
		bloco = new Bloco();
		instr = new Instruction();
		
		while (la.kind == 4) {
			DeclConst();
		}
		while (la.kind == 7 || la.kind == 11) {
			Definicao();
		}
		global.getFunctions(); 
	}

	void DeclConst() {
		Expect(4);
		Expect(1);
		Expect(5);
		Expect(3);
		Expect(6);
	}

	void Definicao() {
		NDesig nd; 
		if (la.kind == 11) {
			Tipo();
		} else if (la.kind == 7) {
			Get();
		} else SynErr(35);
		nd = DesigI();
		if (la.kind == 6 || la.kind == 8) {
			DeclVar(nd);
			if(cont == 0) {
			 global.addGlobalVar(nd.getName());
			} 
		} else if (la.kind == 9) {
			DeclFuncao(nd);
			func = new Funcao();
			bloco = new Bloco();  
		} else SynErr(36);
	}

	void Tipo() {
		Expect(11);
	}

	NDesig  DesigI() {
		NDesig  nd;
		nd = null; String nome=""; boolean vet = false; 
		Expect(1);
		nome = t.val; 
		if (la.kind == 12) {
			Get();
			vet = true; 
			Expect(13);
		}
		nd = new NDesig(nome, vet); 
		return nd;
	}

	void DeclVar(NDesig nd) {
		while (la.kind == 8) {
			Get();
			nd = DesigI();
		}
		Expect(6);
	}

	void DeclFuncao(NDesig nd) {
		NDesig ndi; 
		Expect(9);
		if (la.kind == 11) {
			Tipo();
			ndi = DesigI();
			func.addParam(ndi.getName()); 
			while (la.kind == 8) {
				Get();
				Tipo();
				ndi = DesigI();
				func.addParam(ndi.getName()); 
			}
		}
		Expect(10);
		CBlock();
		func.setBloco(bloco);
		func.setName(nd.getName());
		global.addFunction(func); 
	}

	void CBlock() {
		Expect(14);
		cont+=1; 
		while (StartOf(1)) {
			if (la.kind == 7 || la.kind == 11) {
				Definicao();
			} else {
				Instrucao();
			}
		}
		Expect(15);
		cont-=1; 
	}

	void Instrucao() {
		String nome=""; String param; String assina; 
		switch (la.kind) {
		case 1: {
			nome = Designador();
			if (la.kind == 5) {
				assina = Assinalamento(nome);
			} else if (la.kind == 9) {
				param = Parametros();
				instr = new Instruction();
				String p;
				if(nome.contains("scanf")) {
				param = param.replace("(","");
				param = param.replace(")","");
				p = param + " = int(input())"; 
				} else {
				p = nome + param;
				}
				instr.setInstrucao(p, cont);
				bloco.addInstrucao(instr); 
			} else SynErr(37);
			Expect(6);
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
		case 18: {
			IfElse();
			break;
		}
		case 20: {
			Return();
			break;
		}
		case 21: {
			Printf();
			break;
		}
		case 14: {
			CBlock();
			break;
		}
		case 6: {
			Get();
			break;
		}
		default: SynErr(38); break;
		}
	}

	String  Designador() {
		String  nome;
		String exp=""; 
		Expect(1);
		nome = t.val;
		if(global.getGlobalVar(nome) != "") {
		  nome = global.getGlobalVar(nome);
		} 
		if (la.kind == 12) {
			Get();
			nome += "["; 
			exp = Expr();
			nome += exp; 
			Expect(13);
			nome += "]"; 
		}
		return nome;
	}

	String  Assinalamento(String nome) {
		String  assina;
		String exp=""; instr = new Instruction(); assina=""; 
		Expect(5);
		exp = Expr();
		if(global.getGlobalVar(nome) != "") {
		 nome = global.getGlobalVar(nome);
		}
		assina = nome + " = " + exp;
		instr.setInstrucao(assina, cont);
		bloco.addInstrucao(instr); 
		return assina;
	}

	String  Parametros() {
		String  param;
		String exp; param=""; 
		Expect(9);
		param="("; 
		if (StartOf(2)) {
			exp = Expr();
			param+=exp; 
			while (la.kind == 8) {
				Get();
				exp = Expr();
				param+=", " + exp; 
			}
		}
		Expect(10);
		param+=")"; 
		return param;
	}

	void While() {
		String condicao; String whileStr=""; instr = new Instruction(); 
		Expect(16);
		Expect(9);
		whileStr = "while "; 
		condicao = Condicao();
		whileStr += condicao; 
		Expect(10);
		whileStr += ":";
		instr.setInstrucao(whileStr, cont);
		bloco.addInstrucao(instr); 
		Instrucao();
	}

	void For() {
		String nome; String condicao; String assina; String forStr=""; 
		Expect(17);
		Expect(9);
		nome = Designador();
		assina = Assinalamento(nome);
		Expect(6);
		instr = new Instruction(); 
		condicao = Condicao();
		forStr = "while " + condicao + ":";
		instr.setInstrucao(forStr, cont);
		bloco.addInstrucao(instr); 
		Expect(6);
		String assinaAux = assina; 
		nome = Designador();
		assina = Assinalamento(nome);
		bloco.removeInstrucao(instr); 
		Expect(10);
		Instruction copia = new Instruction();
		cont+=1; 
		Instrucao();
		copia.setInstrucao(assina, cont);
		bloco.addInstrucao(copia);
		cont-=1; 
	}

	void IfElse() {
		String condicao; String ifCond=""; instr = new Instruction(); 
		Expect(18);
		Expect(9);
		ifCond="if "; 
		condicao = Condicao();
		ifCond+=condicao+":"; 
		Expect(10);
		instr.setInstrucao(ifCond, cont);
		bloco.addInstrucao(instr);
		cont+=1; 
		Instrucao();
		cont-=1; 
		if (la.kind == 19) {
			Get();
			instr = new Instruction(); 
			String elseCond="else: ";
			instr.setInstrucao(elseCond, cont);
			bloco.addInstrucao(instr);
			cont+=1; 
			Instrucao();
			cont-=1; 
		} else if (StartOf(3)) {
		} else SynErr(39);
	}

	void Return() {
		String exp; String retorno=""; instr = new Instruction(); 
		Expect(20);
		retorno="return"; 
		if (StartOf(2)) {
			exp = Expr();
			retorno+=" " + exp; 
		} else if (la.kind == 6) {
		} else SynErr(40);
		Expect(6);
		instr.setInstrucao(retorno, cont); 
		bloco.addInstrucao(instr); 
	}

	void Printf() {
		String exp; String print=""; instr = new Instruction(); 
		Expect(21);
		Expect(9);
		print="print("; 
		if (la.kind == 2) {
			Get();
			print+=t.val; 
		} else if (StartOf(2)) {
			exp = Expr();
			print+=exp; 
		} else SynErr(41);
		while (la.kind == 8) {
			Get();
			print+=", "; 
			if (la.kind == 2) {
				Get();
				print+=t.val; 
			} else if (StartOf(2)) {
				exp = Expr();
				print+=exp; 
			} else SynErr(42);
		}
		Expect(10);
		print+=", end = '')"; 
		Expect(6);
		instr.setInstrucao(print, cont); 
		bloco.addInstrucao(instr); 
	}

	String  Expr() {
		String  exp;
		String term=""; exp=""; 
		if (StartOf(4)) {
			term = Termo();
			exp = term; 
		} else if (la.kind == 28) {
			Get();
			term = Termo();
			exp = " - " + term; 
		} else SynErr(43);
		while (la.kind == 28 || la.kind == 29) {
			if (la.kind == 29) {
				Get();
				exp += " + "; 
			} else {
				Get();
				exp += " - "; 
			}
			term = Termo();
			exp += term; 
		}
		return exp;
	}

	String  Condicao() {
		String  condicao;
		String exp; condicao=""; String opRel; 
		exp = Expr();
		condicao+=exp; 
		opRel = OpRel();
		condicao+=" " + opRel + " "; 
		exp = Expr();
		condicao+=exp; 
		return condicao;
	}

	String  OpRel() {
		String  opRel;
		opRel=""; 
		switch (la.kind) {
		case 22: {
			Get();
			opRel="=="; 
			break;
		}
		case 23: {
			Get();
			opRel="!="; 
			break;
		}
		case 24: {
			Get();
			opRel=">"; 
			break;
		}
		case 25: {
			Get();
			opRel=">="; 
			break;
		}
		case 26: {
			Get();
			opRel="<"; 
			break;
		}
		case 27: {
			Get();
			opRel="<="; 
			break;
		}
		default: SynErr(44); break;
		}
		return opRel;
	}

	String  Termo() {
		String  term;
		String factor=""; term=""; 
		factor = Fator();
		term = factor; 
		while (la.kind == 30 || la.kind == 31 || la.kind == 32) {
			if (la.kind == 30) {
				Get();
				term += " * "; 
			} else if (la.kind == 31) {
				Get();
				term += " / "; 
			} else {
				Get();
				term += " % "; 
			}
			factor = Fator();
			term += factor; 
		}
		return term;
	}

	String  Fator() {
		String  factor;
		String exp=""; String nome=""; factor=""; String param=""; 
		if (la.kind == 1) {
			nome = Designador();
			factor = nome; 
			if (la.kind == 9) {
				param = Parametros();
			}
			factor += param; 
		} else if (la.kind == 3) {
			Get();
			factor = t.val; 
		} else if (la.kind == 9) {
			Get();
			exp = Expr();
			factor = "(" + exp; 
			Expect(10);
			factor += ")"; 
		} else if (la.kind == 33) {
			Get();
			Tipo();
			if (la.kind == 12) {
				Get();
				exp = Expr();
				factor = "[0] * (" + exp + ")"; 
				Expect(13);
			} else if (la.kind == 14) {
				Get();
				factor = "["; 
				Expect(3);
				factor += t.val; 
				while (la.kind == 8) {
					Get();
					Expect(3);
					factor += ", " + t.val; 
				}
				Expect(15);
				factor += "]"; 
			} else SynErr(45);
		} else SynErr(46);
		return factor;
	}



	public void Parse() {
		la = new Token();
		la.val = "";		
		Get();
		Python();
		Expect(0);

	}

	private static final boolean[][] set = {
		{T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,T,x,x, x,x,T,T, x,x,x,T, x,x,T,x, T,T,T,x, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,T,x,T, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,T,x,x},
		{x,T,x,x, x,x,T,T, x,x,x,T, x,x,T,T, T,T,T,T, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,T,x,T, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x}

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
			case 4: s = "\"const\" expected"; break;
			case 5: s = "\"=\" expected"; break;
			case 6: s = "\";\" expected"; break;
			case 7: s = "\"void\" expected"; break;
			case 8: s = "\",\" expected"; break;
			case 9: s = "\"(\" expected"; break;
			case 10: s = "\")\" expected"; break;
			case 11: s = "\"int\" expected"; break;
			case 12: s = "\"[\" expected"; break;
			case 13: s = "\"]\" expected"; break;
			case 14: s = "\"{\" expected"; break;
			case 15: s = "\"}\" expected"; break;
			case 16: s = "\"while\" expected"; break;
			case 17: s = "\"for\" expected"; break;
			case 18: s = "\"if\" expected"; break;
			case 19: s = "\"else\" expected"; break;
			case 20: s = "\"return\" expected"; break;
			case 21: s = "\"printf\" expected"; break;
			case 22: s = "\"==\" expected"; break;
			case 23: s = "\"!=\" expected"; break;
			case 24: s = "\">\" expected"; break;
			case 25: s = "\">=\" expected"; break;
			case 26: s = "\"<\" expected"; break;
			case 27: s = "\"<=\" expected"; break;
			case 28: s = "\"-\" expected"; break;
			case 29: s = "\"+\" expected"; break;
			case 30: s = "\"*\" expected"; break;
			case 31: s = "\"/\" expected"; break;
			case 32: s = "\"%\" expected"; break;
			case 33: s = "\"new\" expected"; break;
			case 34: s = "??? expected"; break;
			case 35: s = "invalid Definicao"; break;
			case 36: s = "invalid Definicao"; break;
			case 37: s = "invalid Instrucao"; break;
			case 38: s = "invalid Instrucao"; break;
			case 39: s = "invalid IfElse"; break;
			case 40: s = "invalid Return"; break;
			case 41: s = "invalid Printf"; break;
			case 42: s = "invalid Printf"; break;
			case 43: s = "invalid Expr"; break;
			case 44: s = "invalid OpRel"; break;
			case 45: s = "invalid Fator"; break;
			case 46: s = "invalid Fator"; break;
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
