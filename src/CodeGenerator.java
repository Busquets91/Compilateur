/**
 * 
 * @author Vivien BLUM vivien.blum@u-psud.fr
 *
 */
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
	
	/**
	 * @desc Check if the value is 0
	 * @param Arbre a
	 * @return boolean:	true : zero 
	 * 					false: not zero
	 * @deprecated
	 */
	protected boolean isZero(Arbre a){
		System.out.println("bite");
		return false;
	}
	
	/**
	 * @desc Check if the last value insert in the stack is 0
	 * @return boolean:	true : zero 
	 * 					false: not zero
	 * @deprecated
	 */
	protected boolean isFirstElementZero(){
		// dépiler la valeur
		return false;
	}
	
	/**
	 * @desc Génère du code : Si le dernier élément de la pile est 0
	 * 							=> On jump à la fin du programme
	 * @deprecated
	 */
	protected void checkIsLastElementIsZero(){
		
	}
	
	/**
	 * @desc	Génère du code : push la valeur de la variable
	 * @param 	a [Arbre] de type int ou ident
	 */
	protected void push(Arbre a){
		/*switch (a.tok.Classe){
			case TOK_INT:
				addLineCode("push.i "+a.tok.Value);
				break;
			case TOK_IDENT:
				addLineCode("push.i "+a.tok.Value);
				break;
			default:
				break;
		}*/
		addLineCode("push.i "+a.tok.Value);
	}
	
	/**
	 * @desc	Génère du code : - push pour un int
	 * 							 - get pour un ident
	 * @param 	a [Arbre] de type int ou ident
	 */
	protected void pushElement(Arbre a){
		switch (a.tok.Classe){
			case TOK_INT:
				push(a);
				//addLineCode("push.i "+a.tok.Value);
				break;
			case TOK_IDENT:
				get(a);
				//addLineCode("push.i "+a.tok.Value);
				break;
			default:
				break;
		}
	}
	
	/**
	 * @desc	Génère du code : get la valeur de la variable à l'indice précisé
	 * @param 	a [Arbre] de type ident
	 */
	protected void get(Arbre a){
		//TODO récupérer l'ident de la variable
		addLineCode("get "+"ident "+a.tok.Value);
	}
	
	/**
	 * @desc	Génère du code : set la valeur de la variable à l'indice précisé
	 * @param 	a [Arbre] de type ident
	 */
	protected void set(Arbre a){
		//TODO récupérer l'ident de la variable
		addLineCode("set "+"ident "+a.tok.Value);
	}
	
	protected String getComparaison(Arbre a){
		String str = "";
		switch (a.tok.Classe){
			case TOK_EQA: 	//==
				str = "cmpeq";
				break;
			case TOK_DIFF:	//!=
				str = "cmpne";
				break;
			case TOK_INF:	//<
				str = "cmplt";
				break;
			case TOK_INFE:	//<=
				str = "cmple";
				break;
			case TOK_SUP:	//>
				str = "cmpgt";
				break;
			case TOK_SUPE:	//>=
				str = "cmpge";
				break;
			default:
				break;
		}
		return str;
	}
	
	protected void addIf(Arbre ifTree){
		//On crée le label
		String labelIf = "if"+nbIf;
		String labelElse = "else"+nbIf;
		String labelEnd = "endIf"+nbIf;
		nbIf++;
		
		//On ajoute la comparaison
		genCode(ifTree.fils.get(0));

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
		push(a.fils.get(0));
	}
	
	protected void addAff(Arbre a){
		//On lance la partie à droite de l'affectation
		genCode(a.fils.get(1));
		set(a.fils.get(0));
		
	}
	
	protected void addAdd(Arbre a){
		addOperand("add", a);
	}
	
	protected void addSub(Arbre a){
		addOperand("sub", a);
	}
	
	/**
	 * @desc Ecrit le code pour les opérandes : +, -, *, /, &&, ||
	 * @param op [String] Opérateur : add, sub, mul, div, and, or
	 * @param a [Arbre]
	 */
	protected void addOperand(String op, Arbre a){
		if (isInt(a.fils.get(0)) && isInt(a.fils.get(1))){
			pushElement(a.fils.get(0));
			pushElement(a.fils.get(1));
			addLineCode(op+".i");
		}
		else{
			if (!isInt(a.fils.get(0)) && !isInt(a.fils.get(1))){
				genCode(a.fils.get(0));
				genCode(a.fils.get(1));
			}
			else if (!isInt(a.fils.get(0))){
				genCode(a.fils.get(0));
				pushElement(a.fils.get(1));
			}
			else if (!isInt(a.fils.get(1))){
				genCode(a.fils.get(1));
				pushElement(a.fils.get(0));
				
			}
			addLineCode(op+".i");
		}
	}
	
	protected void addMult(Arbre a){
		addOperand("mul", a);
	}
	
	protected void addDiv(Arbre a){
		//TODO addOperand + verif div par zero + exception
		String op = "div";
		if (isInt(a.fils.get(0)) && isInt(a.fils.get(1))){
			//Num
			pushElement(a.fils.get(0));
			//Denum
			pushElement(a.fils.get(1));
			addLineCode(op+".i");
		}
		else{
			if (!isInt(a.fils.get(0)) && !isInt(a.fils.get(1))){
				genCode(a.fils.get(0));
				genCode(a.fils.get(1));
			}
			else if (!isInt(a.fils.get(0))){
				genCode(a.fils.get(0));
				pushElement(a.fils.get(1));
			}
			else if (!isInt(a.fils.get(1))){
				genCode(a.fils.get(1));
				pushElement(a.fils.get(0));
			}
			addLineCode(op+".i");
		}
	}
	
	
	protected void addAnd(Arbre a){
		addOperand("and", a);
	}
	
	protected void addOr(Arbre a){
		addOperand("or", a);
	}
	
	protected void addComp(Arbre a){
		pushElement(a.fils.get(0));
		pushElement(a.fils.get(1));
		addLineCode(getComparaison(a)+".i");
	}
	
	protected void addFunc(Arbre a){
		if (a.tok.Value.equals("main")){
			genCode(a.fils.get(1));
		}
		else{
			//TODO gérer les fonctions
			String labelFunc = "function-"+a.tok.Value;
		}
	}
	
	protected void addLoop(Arbre a){
		//On traite la première action si c'est un for
		if (a.tok.Classe == TokenClass.TOK_FOR){
			addAff(a.fils.get(0));
		}
		//On labelise
		String labelLoop = "loop"+nbLoop;
		String labelEnd = "endLoop"+nbLoop;
		nbLoop++;
		addLabel(labelLoop);
				
		//On lance la comparaison
		genCode(a.fils.get(1).fils.get(0));

		addLineCode("jumpf "+labelEnd);
		genCode(a.fils.get(1).fils.get(1));
		addLineCode("jump "+labelLoop);
		
		addLabel(labelEnd);
	}
	
	/**
	 * 
	 * @deprecated 
	 */
	protected void addValue(Arbre a){
	}
	
	/**
	 * 
	 * 
	 */
	protected void addNumber(Arbre a){
		pushElement(a);
	}
	
	protected void genCode(Arbre a){
		switch (a.tok.Classe){
			//Gestion du code
			case TOK_RAC: 	//Racine du fichier
//				genCode(a.fils.get(0));
				for(Arbre tmp : a.fils){
					genCode(tmp);
				}
				break;
			case TOK_FUNC: //Fonctions //TODO gérer les fonctions
				addFunc(a);
//				genCode(a.fils.get(1));
				break;
			case TOK_SUIT:	//Séquence
				//TODO ajouter var rang n
				addSymb();	
				for(Arbre tmp : a.fils){
					genCode(tmp);
				}
				//TODO supprimer variable rang n
				break;
			//Operand
			case TOK_ADD:	// +
				addAdd(a);
				break;
			case TOK_LESS:	// -
				addSub(a);
				break;
			case TOK_MULT:	// *
				addMult(a);
				break;
			case TOK_DIV:	// /
				addDiv(a);
				break;
			case TOK_AND:	// &&
				addAnd(a);
				break;
			case TOK_OR:	// ||
				addOr(a);
				break;
			//Operand comparaison
			case TOK_EQA: 	// ==
				addComp(a);
				break;
			case TOK_DIFF:	// !=
				addComp(a);
				break;
			case TOK_INF:	// <
				addComp(a);
				break;
			case TOK_INFE:	// <=
				addComp(a);
				break;
			case TOK_SUP:	// >
				addComp(a);
				break;
			case TOK_SUPE:	// >=
				addComp(a);
				break;
			//Boucles & conditions
			case TOK_IF:	// If
				addIf(a);
				break;
			case TOK_FOR:	// Loop : for
				addLoop(a);
				break;
			case TOK_WHILE: // Loop : while
				addLoop(a);
				break;
			//Gestion des variables
			case TOK_VAR:	// var
				addVar(a);
				break;
			case TOK_AFF:	// =
				addAff(a);
				break;
			case TOK_IDENT:
				addValue(a);
				break;
			case TOK_INT:
				addNumber(a);
				break;
			default:
				break;
		}
	}
	
	public void constructCode(){
		setStart();
		
		genCode(arbre);
		
		setEnd();
	}
	
	public String getCode(){
		return code;
	}

	public static void main(String[] args) throws Exception {
		//On lit le fichier contenant le code
		Reader fic = new Reader("test.txt");
		
		//On l'analyse
		Analyseur anal = new Analyseur(fic.getString());
		
		Arbre tst = anal.getArbre();
		System.out.println(tst.print());
		
		//On génère le code
		CodeGenerator code = new CodeGenerator(tst, anal.getSymb());
		code.constructCode();
		//System.out.println(code.getCode());
		
		//On écrit le code dans un fichier
		new Writer(fic.getNameFile()+"-compiled.txt", code.getCode());
		
	}

}
