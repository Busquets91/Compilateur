
public class CodeGenerator {
	protected TableSymbole symbole;
	protected Arbre arbre;
	protected String code = "";
	protected int nbIf = 0;
	protected int nbLoop = 0;
	
	public CodeGenerator(Arbre a, TableSymbole symb){
		arbre =  a;
		symbole = symb;
	}
	
	protected void addLineCode(String str){
		code += str+"\n";
	}
	
	protected void addLabel(String label){
		code += "."+label+"\n";
	}
	
	protected void setStart(){
		addLabel("start");
	}
	
	protected void setEnd(){
		addLabel("halt");
	}
	
	protected void addIf(Arbre ifTree){
		//On crée le label
		String labelIf = "if"+nbIf;
		String labelElse = "else"+nbIf;
		String labelEnd = "endIf"+nbIf;
		nbIf++;
		
		addLineCode("push.i A");	//Push ou get ?
		addLineCode("push.i B");
		switch (ifTree.fils.get(0).tok.Classe){
			case TOK_EQA: 	//==
				addLineCode("cmpeq.i "+"A"+" B");
				break;
			case TOK_DIFF:	//!=
				addLineCode("cmpne.i "+"A"+" B");
				break;
			case TOK_INF:	//<
				addLineCode("cmplt.i "+"A"+" B");
				break;
			case TOK_INFE:	//<=
				addLineCode("cmple.i "+"A"+" B");
				break;
			case TOK_SUP:	//>
				addLineCode("cmpgt.i "+"A"+" B");
				break;
			case TOK_SUPE:	//>=
				addLineCode("cmpge.i "+"A"+" B");
				break;
			default:
				break;
		}
		if (ifTree.fils.size() >= 3){
			addLineCode("jumpf "+labelElse);
		}
		else{
			addLineCode("jumpf "+labelEnd);
		}
		addLabel(labelIf);
		//Bloc du if
		genCode(ifTree.fils.get(1));
		//addLineCode("bloc if");
		//S'il y a un bloc else
		if (ifTree.fils.size() >= 3){
			addLabel(labelElse);
			genCode(ifTree.fils.get(2));
		}
		addLabel(labelEnd);
	}
	
	protected void addVar(Arbre a){
		addLineCode("On ajoute "+a.fils.get(0).tok.Value);
	}
	
	protected void addAff(Arbre a){
		//On lance la partie à droite de l'affectation
		genCode(a.fils.get(1));
		addLineCode("affectation.i "+a.fils.get(0).tok.Value);
	}
	
	protected void addAdd(Arbre a){
		//if (a.fils.get(0).tok == TokenClass.TOK_ADD)
		addLineCode("add.i "+a.fils.get(0).tok.Value+", "+a.fils.get(1).tok.Value);
	}
	
	protected void addSub(Arbre a){
		addLineCode("sub.i "+a.fils.get(0).tok.Value+", "+a.fils.get(1).tok.Value);
	}
	
	protected void addMult(Arbre a){
		for(Arbre tmp : a.fils){
			if (tmp.tok.Classe == TokenClass.TOK_INT || tmp.tok.Classe == TokenClass.TOK_VAR){
				addLineCode("mul.i "+tmp.tok.Value+", "+tmp.tok.Value);
			}
			else{
				genCode(tmp);
			}
		}
		
		/*if (a.fils.get(1).tok.Classe == TokenClass.TOK_INT || a.fils.get(1).tok.Classe == TokenClass.TOK_VAR){
			addLineCode("mul.i "+a.fils.get(0).tok.Value+", "+a.fils.get(1).tok.Value);
		}
		else{
			genCode(a.fils.get(1));
		}*/
	}
	
	protected void addDiv(Arbre a){
		for(Arbre tmp : a.fils){
			if (tmp.tok.Classe == TokenClass.TOK_INT || tmp.tok.Classe == TokenClass.TOK_VAR){
				addLineCode("div.i "+tmp.tok.Value+", "+tmp.tok.Value);
			}
			else{
				genCode(tmp);
			}
		}
	}
	
	protected void addAnd(Arbre a){
		//if (a.fils.get(0).tok == TokenClass.TOK_ADD)
		addLineCode("and "+a.fils.get(0).tok.Value+", "+a.fils.get(1).tok.Value);
	}
	
	protected void addOr(Arbre a){
		//if (a.fils.get(0).tok == TokenClass.TOK_ADD)
		addLineCode("or "+a.fils.get(0).tok.Value+", "+a.fils.get(1).tok.Value);
	}
	
	protected void addLoop(Arbre a){
		//On traite la première action si c'est un for
		if (a.tok.Classe == TokenClass.TOK_FOR){
			addAff(a.fils.get(0));
		}
		//On labelise
		String labelLoop = "loop"+nbIf;
		String labelEnd = "endLoop"+nbIf;
		nbLoop++;
		addLabel(labelLoop);
		switch (a.fils.get(1).fils.get(0).tok.Classe){
			case TOK_EQA: 	//==
				addLineCode("cmpeq.i "+"A"+" B");
				break;
			case TOK_DIFF:	//!=
				addLineCode("cmpne.i "+"A"+" B");
				break;
			case TOK_INF:	//<
				addLineCode("cmplt.i "+"A"+" B");
				break;
			case TOK_INFE:	//<=
				addLineCode("cmple.i "+"A"+" B");
				break;
			case TOK_SUP:	//>
				addLineCode("cmpgt.i "+"A"+" B");
				break;
			case TOK_SUPE:	//>=
				addLineCode("cmpge.i "+"A"+" B");
				break;
			default:
				break;
		}
		addLineCode("jumpf "+labelEnd);
		genCode(a.fils.get(1).fils.get(1));
		addLineCode("jump "+labelLoop);
		
		addLabel(labelEnd);
	}
	
	protected void genCode(Arbre a){
		switch (a.tok.Classe){
			case TOK_SUIT:
				for(Arbre tmp : a.fils){
					genCode(tmp);
				}
				break;
			case TOK_IF:
				addIf(a);
				break;
			case TOK_ADD:
				addAdd(a);
				break;
			case TOK_LESS:
				addSub(a);
				break;
			case TOK_MULT:
				addMult(a);
				break;
			case TOK_DIV:
				addDiv(a);
				break;
			case TOK_AND:
				addAnd(a);
				break;
			case TOK_OR:
				addOr(a);
				break;
			case TOK_FOR:
				addLoop(a);
				break;
			case TOK_WHILE:
				addLoop(a);
				break;
			case TOK_VAR:
				addVar(a);
				break;
			case TOK_AFF:
				addAff(a);
				break;
			default:
				break;
		}
	}
	
	public void constructCode(){
		setStart();
		
		//TODO ajouter les variables à la table des symboles (Construction de la pile)
		genCode(arbre);
		
		//TODO parcourir l'arbre
		
			//TODO gérer tous les token : for, if ...
		
		setEnd();
	}
	
	public String getCode(){
		return code;
	}

	public static void main(String[] args) throws Exception {
		Reader fic = new Reader("/home/tp-home004/apoint1/workspace/Compilateur/src/test.txt");
		Analyseur anal = new Analyseur(fic.getString());
		
		Arbre tst = anal.getArbre();
		CodeGenerator code = new CodeGenerator(tst, anal.getSymb());
		code.constructCode();
		System.out.println(code.getCode());
		System.out.println(tst.print());
	}

}
