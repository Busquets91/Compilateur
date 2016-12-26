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
	 * @desc Génère une ligne de code à partir d'un string
	 * @param str [String] code à générer
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
	 * @desc Génère le label de début de programme
	 */
	protected void setStart(){
		addLabel("start");
	}
	
	/**
	 * @desc Génère le label de fin du programme
	 */
	protected void setEnd(){
		addLabel("halt");
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
	 * @desc	Génère du code : push la valeur de la variable
	 * @param 	a [Arbre] de type int ou ident
	 */
	protected void push(Arbre a){
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
				break;
			case TOK_IDENT:
				get(a);
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
	
	/**
	 * @desc Ajoute autant de drop qu'il y a de variable à supprimer de la pile
	 * @param n [int] le nombre de variable à supprimer de la pile
	 */
	protected void drop(int n){
		for (int i = 0; i < n; i++){
			addLineCode("drop");
		}
	}
	
	/**
	 * @desc Renvoie le code en fonction de la comparaison à effectuer
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
	 * @desc Génère le code pour un if
	 * @param ifTree
	 */
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
		//S'il y a un bloc else
		if (ifTree.fils.size() >= 3){
			addLabel(labelElse);
			genCode(ifTree.fils.get(2));
		}
		addLabel(labelEnd);
	}
	
	/**
	 * @desc Génère le code pour ajotuer une variable à la pile
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
	 * @desc Incrémente le compteur de variable à l'indice du bloc courant
	 */
	protected void incrementCompteur(){
		int index = getLastIndex();
		int cpt = tableCpt.get(index);
		cpt++;
		tableCpt.set(index, cpt);
	}
	
	/**
	 * @desc Génère le code pour une affectation
	 * @example a = 3;
	 * @param a [Arbre]
	 */
	protected void addAff(Arbre a){
		//On lance la partie à droite de l'affectation
		genCode(a.fils.get(1));
		set(a.fils.get(0));
		
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
	
	/**
	 * @desc Génère le code d'un +
	 * @param a [Arbre]
	 */
	protected void addAdd(Arbre a){
		addOperand("add", a);
	}
	
	/**
	 * @desc Génère le code d'un -
	 * @param a [Arbre]
	 */
	protected void addSub(Arbre a){
		addOperand("sub", a);
	}
	
	/**
	 * @desc Génère le code d'un /
	 * @param a [Arbre]
	 */
	protected void addDiv(Arbre a){
		//TODO verif div par zero + exception
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
	
	/**
	 * @desc Génère le code d'un *
	 * @param a [Arbre]
	 */
	protected void addMult(Arbre a){
		addOperand("mul", a);
	}
	
	/**
	 * @desc Génère le code d'un and
	 * @param a [Arbre]
	 */
	protected void addAnd(Arbre a){
		addOperand("and", a);
	}
	
	/**
	 * @desc Génère le code d'un or
	 * @param a [Arbre]
	 */
	protected void addOr(Arbre a){
		addOperand("or", a);
	}
	
	/**
	 * @desc Génère le code pour une comparaison
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
		if (a.tok.Value.equals("main")){
			genCode(a.fils.get(1));
		}
		else{
			//TODO gérer les fonctions
			String labelFunc = "function-"+a.tok.Value;
		}
	}
	
	/**
	 * @desc Génère le code d'un loop
	 * @param a [Arbre]
	 */
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
	 * @desc Génère le code pour ajouter une constante
	 * @param a [Arbre]
	 */
	protected void addNumber(Arbre a){
		pushElement(a);
	}
	
	/**
	 * @desc Fonction récursive pour génèrer le code
	 * @param a [Arbre]
	 */
	protected void genCode(Arbre a){
		switch (a.tok.Classe){
			//Gestion du code
			case TOK_RAC: 	//Racine du fichier
				for(Arbre tmp : a.fils){
					genCode(tmp);
				}
				break;
			case TOK_FUNC: //Fonctions //TODO gérer les fonctions
				addFunc(a);
				break;
			case TOK_SUIT:	//Séquence
				tableCpt.add(0);
				for(Arbre tmp : a.fils){
					genCode(tmp);
				}
				//TODO savoir le nombre de variable à supprimer
				drop(tableCpt.get(getLastIndex()));
				tableCpt.remove(getLastIndex());
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
				break;
			case TOK_INT:
				addNumber(a);
				break;
			default:
				break;
		}
	}

	/**
	 * @desc Construit le code
	 */
	public void constructCode(){
		setStart();
		genCode(arbre);
		setEnd();
	}
	
	/**
	 * @desc Renvoie le code généré
	 * @return code [String]
	 */
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
