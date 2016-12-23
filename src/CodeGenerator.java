import java.util.HashMap;
import java.util.Stack;

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
	
	protected void addSymb(){
		/*Stack<HashMap<Integer, Symbole>> pile = new Stack<HashMap<Integer, Symbole>>();
		pile = symbole.getPile();
		
		for(HashMap<Integer, Symbole> hm : pile){
			System.out.println("-------------------");
			for (Integer key : hm.keySet()){
				System.out.println(hm.get(key).getIdent());
				System.out.println(symbole.getRang());
			}
		}*/
	}
	
	protected boolean isInt(Arbre a){
		if(a.tok.Classe == TokenClass.TOK_INT || a.tok.Classe == TokenClass.TOK_IDENT){
			return true;
		}
		return false;
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
				addLineCode("cmpeq.i");
				break;
			case TOK_DIFF:	//!=
				addLineCode("cmpne.i");
				break;
			case TOK_INF:	//<
				addLineCode("cmplt.i");
				break;
			case TOK_INFE:	//<=
				addLineCode("cmple.i");
				break;
			case TOK_SUP:	//>
				addLineCode("cmpgt.i");
				break;
			case TOK_SUPE:	//>=
				addLineCode("cmpge.i");
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
		//System.out.println(a.fils.get(0).tok.Value);
		addLineCode("push.i "+"idX");
	}
	
	protected void addAff(Arbre a){
		//On lance la partie à droite de l'affectation
		genCode(a.fils.get(1));
		addLineCode("affectation "+a.fils.get(0).tok.Value);
		addLineCode("push.i "+"valX");
		addLineCode("get "+"idX");
	}
	
	protected void addAdd(Arbre a){
		addOperand("add", a);
	}
	
	protected void addSub(Arbre a){
		addOperand("sub", a);
	}
	
	protected void addOperand(String op, Arbre a){
		if (isInt(a.fils.get(0)) && isInt(a.fils.get(1))){
			addLineCode("push.i "+a.fils.get(0).tok.Value);
			addLineCode("push.i "+a.fils.get(1).tok.Value);
			addLineCode(op+".i");
		}
		else{
			if (!isInt(a.fils.get(0)) && !isInt(a.fils.get(1))){
				genCode(a.fils.get(0));
				genCode(a.fils.get(1));
			}
			else if (!isInt(a.fils.get(0))){
				genCode(a.fils.get(0));
				addLineCode("push.i "+a.fils.get(1).tok.Value);
			}
			else if (!isInt(a.fils.get(1))){
				genCode(a.fils.get(1));
				addLineCode("push.i "+a.fils.get(0).tok.Value);
				
			}
			addLineCode(op+".i");
		}
	}
	
	protected void addMult(Arbre a){
		addOperand("mul", a);
	}
	
	protected void addDiv(Arbre a){
		//TODO addOperand + verif div par zero
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
		addOperand("and", a);
	}
	
	protected void addOr(Arbre a){
		addOperand("or", a);
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
		//TODO function addComparaison
		switch (a.fils.get(1).fils.get(0).tok.Classe){
			case TOK_EQA: 	//==
				addLineCode("cmpeq.i");
				break;
			case TOK_DIFF:	//!=
				addLineCode("cmpne.i");
				break;
			case TOK_INF:	//<
				addLineCode("cmplt.i");
				break;
			case TOK_INFE:	//<=
				addLineCode("cmple.i");
				break;
			case TOK_SUP:	//>
				addLineCode("cmpgt.i");
				break;
			case TOK_SUPE:	//>=
				addLineCode("cmpge.i");
				break;
			default:
				break;
		}
		addLineCode("jumpf "+labelEnd);
		genCode(a.fils.get(1).fils.get(1));
		addLineCode("jump "+labelLoop);
		
		addLabel(labelEnd);
	}
	
	protected void addValue(Arbre a){
		//addLineCode("push.i "+a.tok.Value);
	}
	
	protected void addNumber(Arbre a){
		//addLineCode("push.i "+a.tok.Value);
	}
	
	protected void genCode(Arbre a){
		switch (a.tok.Classe){
			case TOK_RAC:
				genCode(a.fils.get(0));
				break;
			case TOK_FUNC: //TODO gérer les fonctions
				genCode(a.fils.get(1));
				break;
			case TOK_SUIT:
				//TODO ajouter var rang n
				addSymb();	
				for(Arbre tmp : a.fils){
					genCode(tmp);
				}
				//TODO supprimer variable rang n
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
				//addVar(a);
				break;
			case TOK_AFF:
				addAff(a);
				break;
			case TOK_IDENT:
				addValue(a);
				break;
			case TOK_INT:
				addValue(a);
				break;
			default:
				break;
		}
	}
	
	public void constructCode(){
		setStart();
		
		//TODO ajouter les variables à la table des symboles (Construction de la pile)
		genCode(arbre);
		
		setEnd();
	}
	
	public String getCode(){
		return code;
	}

	public static void main(String[] args) throws Exception {
		Reader fic = new Reader("test.txt");
		Analyseur anal = new Analyseur(fic.getString());
		
		Arbre tst = anal.getArbre();
		System.out.println(tst.print());
		CodeGenerator code = new CodeGenerator(tst, anal.getSymb());
		code.constructCode();
		System.out.println(code.getCode());
		
	}

}
