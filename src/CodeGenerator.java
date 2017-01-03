import java.util.ArrayList;


/**
 * 
 * @author Vivien BLUM vivien.blum@u-psud.fr
 *
 */
public class CodeGenerator {
	/**
	 * @deprecated
	 */
	protected TableSymbole symbole;
	protected ArrayList<Integer> tableCpt;
	protected Arbre arbre;
	protected String code = "";
	protected int nbIf = 0;
	protected int nbLoop = 0;
	
	/**
	 * @desc Constructor
	 * @param a [Arbre]
	 * @param symb [TableSymbole]
	 */
	public CodeGenerator(Arbre a, TableSymbole symb){
		arbre =  a;
		symbole = symb;
		tableCpt = new ArrayList<Integer>();
	}
	
	/**
	 * @desc GÃ©nÃ¨re une ligne de code Ã  partir d'un string
	 * @param str [String] code Ã  gÃ©nÃ©rer
	 */
	protected void addLineCode(String str){
		code += str+"\n";
	}
	
	/**
	 * @desc Ajoute un label
	 * @param label [String] nom du label
	 */
	protected void addLabel(String label){
		code += "."+label+"\n";
	}
	
	/**
	 * @desc GÃ©nÃ¨re le label de dÃ©but de programme
	 */
	protected void setStart(){
		addLabel("start");
	}
	
	/**
	 * @desc GÃ©nÃ¨re le label de fin du programme
	 */
	protected void setEnd(){
		addLineCode("halt");
	}
	
	/**
	 * @desc Check si l'arbre est de type INT ou IDENT
	 * @param a [Arbre]
	 * @return [boolean]
	 */
	protected boolean isInt(Arbre a){
		if(a.tok.Classe == TokenClass.TOK_INT || a.tok.Classe == TokenClass.TOK_IDENT){
			return true;
		}
		return false;
	}
	
	/**
	 * @deprecated
	 * @desc Check si le denom est Ã©gal Ã  0
	 * @param a [Arbre]
	 * @return [boolean]
	 * @throws Exception => denominateur Ã©gal Ã  0
	 */
	protected boolean isDenomZero(Arbre a) throws Exception{
		if (buildOperand(a) == 0){
			System.err.println("ERREUR DIVISION par zero");
			throw new Exception();
		}
		return false;
	}
	
	/**
	 * @deprecated
	 * @desc Calcule la valeur d'un expression
	 * @param a
	 * @return
	 * @throws Exception
	 */
	private int buildOperand(Arbre a) throws Exception{
		int res = 0;
		if (isInt(a)){
			res = Integer.parseInt(a.tok.Value);
		}
		else{
			int f0 = buildOperand(a.fils.get(0));
			int f1 = buildOperand(a.fils.get(1));
			switch (a.tok.Classe){
				case TOK_ADD:
					res = f0 + f1;
					break;
				case TOK_LESS:
					res = f0 - f1;
					break;
				case TOK_MULT:
					res = f0 * f1;
					break;
				case TOK_AND:
					res = f0 & f1;
					break;
				case TOK_OR:
					res = f0 | f1;
					break;
				case TOK_DIV:
					if (!isDenomZero(a.fils.get(1))){
						res = f0 / f1;
					}
					break;
				default:
					break;
			}
		}
		return res;
	}
	
	/**
	 * @desc	GÃ©nÃ¨re du code : push la valeur de la variable
	 * @param 	a [Arbre] de type int ou ident
	 */
	protected void push(Arbre a){
		addLineCode("push.i "+a.tok.Value);
	}
	
	/**
	 * @desc	GÃ©nÃ¨re du code : - push pour un int
	 * 							 - get pour un ident
	 * @param 	a [Arbre] de type int ou ident
	 */
	protected void pushElement(Arbre a){
		switch (a.tok.Classe){
			case TOK_INT:
				push(a);
				break;
			case TOK_IDENT:
				get(a);
				break;
			default:
				break;
		}
	}
	
	/**
	 * @desc	GÃ©nÃ¨re du code : get la valeur de la variable Ã  l'indice prÃ©cisÃ©
	 * @param 	a [Arbre] de type ident
	 */
	protected void get(Arbre a){
		//TODO rÃ©cupÃ©rer l'ident de la variable
		addLineCode("get "+"ident "+a.tok.Value);
	}
	
	/**
	 * @desc	GÃ©nÃ¨re du code : set la valeur de la variable Ã  l'indice prÃ©cisÃ©
	 * @param 	a [Arbre] de type ident
	 */
	protected void set(Arbre a){
		//TODO rÃ©cupÃ©rer l'ident de la variable
		addLineCode("set "+"ident "+a.tok.Value);
	}
	
	/**
	 * @desc Ajoute autant de drop qu'il y a de variable Ã  supprimer de la pile
	 * @param n [int] le nombre de variable Ã  supprimer de la pile
	 */
	protected void drop(int n){
		for (int i = 0; i < n; i++){
			addLineCode("drop");
		}
	}
	
	/**
	 * @desc Renvoie le code en fonction de la comparaison Ã  effectuer
	 * @param a [Arbre]
	 * @return str [String] code en focntion de la comparaison
	 */
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
	
	/**
	 * @desc GÃ©nÃ¨re le code pour un if
	 * @param ifTree
	 */
	protected void addIf(Arbre ifTree){
		//On crÃ©e le label
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
		//S'il y a un bloc else
		if (ifTree.fils.size() >= 3){
			addLabel(labelElse);
			genCode(ifTree.fils.get(2));
		}
		addLabel(labelEnd);
	}
	
	/**
	 * @desc GÃ©nÃ¨re le code pour ajotuer une variable Ã  la pile
	 * @param a [Arbre]
	 */
	protected void addVar(Arbre a){
		push(a.fils.get(0));
		incrementCompteur();
	}
	
	/**
	 * @desc Retourne le dernier id du tableau tableCpt
	 * @return index [int]
	 */
	protected int getLastIndex(){
		if (tableCpt != null && !tableCpt.isEmpty()) {
			return tableCpt.size()-1;
		}
		else{
			return 0;
		}
	}
	
	/**
	 * @desc IncrÃ©mente le compteur de variable Ã  l'indice du bloc courant
	 */
	protected void incrementCompteur(){
		int index = getLastIndex();
		int cpt = tableCpt.get(index);
		cpt++;
		tableCpt.set(index, cpt);
	}
	
	/**
	 * @desc GÃ©nÃ¨re le code pour une affectation
	 * @example a = 3;
	 * @param a [Arbre]
	 */
	protected void addAff(Arbre a){
		//On lance la partie Ã  droite de l'affectation
		genCode(a.fils.get(1));
		set(a.fils.get(0));
	}
	
	/**
	 * @desc Ecrit le code pour les opÃ©randes : +, -, *, /, &&, ||
	 * @param op [String] OpÃ©rateur : add, sub, mul, div, and, or
	 * @param a [Arbre]
	 * @version 2
	 */
	protected void addOperand(String op, Arbre a){
		genCode(a.fils.get(0));
		genCode(a.fils.get(1));
		addLineCode(op+".i");
	}
	
	/**
	 * @deprecated
	 * @version 1 Non valable pour le cas du sub
	 * @desc Ecrit le code pour les opÃ©randes : +, -, *, /, &&, ||
	 * @param op [String] OpÃ©rateur : add, sub, mul, div, and, or
	 * @param a [Arbre]
	 */
	protected void addOperandOld(String op, Arbre a){
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
	
	/**
	 * @desc GÃ©nÃ¨re le code d'un +
	 * @param a [Arbre]
	 */
	protected void addAdd(Arbre a){
		addOperand("add", a);
	}
	
	/**
	 * @desc GÃ©nÃ¨re le code d'un -
	 * @param a [Arbre]
	 */
	protected void addSub(Arbre a){
		addOperand("sub", a);
	}
	
	/**
	 * @desc GÃ©nÃ¨re le code d'un /
	 * @param a [Arbre]
	 */
	protected void addDiv(Arbre a){
		//TODO verif div par zero + exception
		genCode(a.fils.get(0));
		try {
//			if (!isDenomZero(a.fils.get(1))){
				genCode(a.fils.get(1));
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		addLineCode("div"+".i");
	}
	
	/**
	 * @desc GÃ©nÃ¨re le code d'un *
	 * @param a [Arbre]
	 */
	protected void addMult(Arbre a){
		addOperand("mul", a);
	}
	
	/**
	 * @desc GÃ©nÃ¨re le code d'un and
	 * @param a [Arbre]
	 */
	protected void addAnd(Arbre a){
		addOperand("and", a);
	}
	
	/**
	 * @desc GÃ©nÃ¨re le code d'un or
	 * @param a [Arbre]
	 */
	protected void addOr(Arbre a){
		addOperand("or", a);
	}
	
	/**
	 * @desc Génère le code d'un not
	 * @param a [Arbre]
	 */
	protected void addNot(Arbre a){
		genCode(a.fils.get(0));
		addLineCode("not");
	}
	
	/**
	 * @desc GÃ©nÃ¨re le code pour une comparaison
	 * @param a [Arbre]
	 */
	protected void addComp(Arbre a){
		pushElement(a.fils.get(0));
		pushElement(a.fils.get(1));
		addLineCode(getComparaison(a)+".i");
	}
	
	/**
	 * @desc Ajoute une fonction
	 * @param a [Arbre]s
	 */
	protected void addFunc(Arbre a){
		//TODO gÃ©rer le fils 0 "var" dans les fonctions
		if (a.tok.Value.equals("main")){
			setStart();
			genCode(a.fils.get(1));
			setEnd();
		}
		else{
			//TODO gÃ©rer les fonctions
			String labelFunc = a.tok.Value+"F";
			addLabel(labelFunc);
			genCode(a.fils.get(1));
		}
	}
	
	/**
	 * @desc Ajoute un return
	 * @param a [Arbre]s
	 */
	protected void addReturn(Arbre a){
		genCode(a.fils.get(0));
		addLineCode("ret");
	}
	
	/**
	 * @desc Ajoute un appel de fonction
	 * @param a [Arbre]s
	 */
	protected void addAppel(Arbre a){
		int nbArg = 0;
		for(Arbre tmp : a.fils.get(0).fils){
			pushElement(tmp);
			nbArg++;
		}
		addLineCode("prep "+a.tok.Value+"F");
		addLineCode("call "+nbArg);
	}
	
	/**
	 * @desc GÃ©nÃ¨re le code d'un loop
	 * @param a [Arbre]
	 */
	protected void addLoop(Arbre a){
		//On traite la premiÃ¨re action si c'est un for
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
	 * @desc GÃ©nÃ¨re le code pour ajouter une constante
	 * @param a [Arbre]
	 */
	protected void addNumber(Arbre a){
		pushElement(a);
	}
	
	/**
	 * @desc Fonction rÃ©cursive pour gÃ©nÃ¨rer le code
	 * @param a [Arbre]
	 */
	public void genCode(Arbre a){
		switch (a.tok.Classe){
			//Gestion du code
			case TOK_RAC: 	//Racine du fichier
				for(Arbre tmp : a.fils){
					genCode(tmp);
				}
				break;
			case TOK_FUNC: //Fonctions //TODO gÃ©rer les fonctions
				addFunc(a);
				break;
			case TOK_SUIT:	//SÃ©quence
				tableCpt.add(0);
				for(Arbre tmp : a.fils){
					genCode(tmp);
				}
				//TODO savoir le nombre de variable Ã  supprimer
				drop(tableCpt.get(getLastIndex()));
				tableCpt.remove(getLastIndex());
				break;
			case TOK_RET:
				addReturn(a);
				break;
			case TOK_APP:
				addAppel(a);
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
			case TOK_NOT:
				addNot(a);
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
				addNumber(a);
				break;
			case TOK_INT:
				addNumber(a);
				break;
			default:
				break;
		}
	}
	
	/**
	 * @desc Renvoie le code gÃ©nÃ©rÃ©
	 * @return code [String]
	 */
	public String getCode(){
		return code;
	}
	
	/**
	 * @desc Renvoie l'arbre
	 * @return arbre [Arbre]
	 */
	public Arbre getArbre(){
		return arbre;
	}

	public static void main(String[] args) throws Exception {
		//On lit le fichier contenant le code
		Reader fic = new Reader("test.txt");
		
		//On l'analyse
		Analyseur anal = new Analyseur(fic.getString());
		Arbre tst = anal.getArbre();
		
		System.out.println(tst.print());
		
		//On gÃ©nÃ¨re le code
		CodeGenerator code = new CodeGenerator(tst, anal.getSymb());
		code.genCode(code.getArbre());
		//System.out.println(code.getCode());
	
		//On Ã©crit le code dans un fichier
		new Writer(fic.getNameFile()+"-compiled.txt", code.getCode());
	}

}
