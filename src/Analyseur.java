import java.util.ArrayList;


public class Analyseur {
	
	private TableSymbole symb;
	private String string;
	private int index;
	private int pos;
	private ArrayList<String> functions;
	private String[] keyWords = {"if", "for", "while", "var", "int", "else", "function", "return", "true", "false"};
	private TokenClass[] keyClasse = {TokenClass.TOK_IF, TokenClass.TOK_FOR, TokenClass.TOK_WHILE, TokenClass.TOK_VAR, TokenClass.TOK_INT, TokenClass.TOK_ELSE, TokenClass.TOK_FUNC, TokenClass.TOK_RET, TokenClass.TOK_TRUE, TokenClass.TOK_FALSE};
	private String[] operator = {"&&", "||", "+", "-", "*", "/", "%", "(", ")", "==", "!=", ">=", "<=", ">", "<", "=", "{", "}", ";", "!"};
	private TokenClass[] keyOperator = {TokenClass.TOK_AND, TokenClass.TOK_OR, TokenClass.TOK_ADD, TokenClass.TOK_LESS,TokenClass.TOK_MULT, TokenClass.TOK_DIV, TokenClass.TOK_MOD, TokenClass.TOK_PO, TokenClass.TOK_PF, TokenClass.TOK_EQA, TokenClass.TOK_DIFF, TokenClass.TOK_SUPE, TokenClass.TOK_INFE, TokenClass.TOK_SUP, TokenClass.TOK_INF, TokenClass.TOK_AFF, TokenClass.TOK_AO, TokenClass.TOK_AF, TokenClass.TOK_PV, TokenClass.TOK_NOT};
	
	public Analyseur(String str){
		this.string = str;
		this.pos = 0;
		this.symb = new TableSymbole();
		this.index = -1;
		this.functions = new ArrayList<String>();
	}
	
	/**************************
	 * Fonctions GET
	 ***************************/
	
	public TableSymbole getSymb(){
		return symb;
	}
	
	public Arbre getArbre() throws Exception{
		this.symb.push();
		Arbre a = function();
		for(Arbre f : a.fils){
			if(f.tok.Value.equals("main") && f.fils.get(0).fils.isEmpty()){
				return a;
			}
		}
		System.err.println("FONCTION \"main\" MANQUANTE OU AVEC TROP D'ARGUMENT");
		throw new Exception();
	}
	
	
	/*********************************
	 * Fonctions de détection de token
	 *********************************/	
	private Boolean strIsToken(String tokenClasse){
		if(string.indexOf(tokenClasse, pos) == pos && !Character.isLetterOrDigit(this.string.charAt(pos+tokenClasse.length()))){
			pos += tokenClasse.length();
			return true;
		}
		return false;
	}
	
	private Boolean opeIsToken(String tokenClasse){
		if(string.indexOf(tokenClasse, pos) == pos){
			pos += tokenClasse.length();
			return true;
		}
		return false;
	}

	/*********************************
	 * Fonctions de parcourt de token
	 *********************************/	
	private Token look(){
		Token tok = new Token();
		int savePos = pos;
		tok = next();
		pos = savePos;
		return tok;
	}
	
	private Token next(){
		Token tok = new Token();
		if(string.length() == pos){
			tok.Classe = TokenClass.TOK_EOF;
			return tok;
		}
		int i;
		for(i = 0; i < keyWords.length; i++){
			if(strIsToken(keyWords[i])){
				tok.Classe = keyClasse[i];
				tok.Value = keyWords[i];
				return tok;
			}
		}
		if(Character.isDigit(this.string.charAt(pos))){
			String entier = "";
			while(string.length() > pos && Character.isDigit(this.string.charAt(pos))){
				entier = entier + this.string.charAt(pos);
				pos ++;
			}
			tok.Classe = TokenClass.TOK_INT;
			tok.Value = entier;
			return tok;
		}
		for(i = 0; i < operator.length; i++){
			if(opeIsToken(operator[i])){
				tok.Classe = keyOperator[i];
				tok.Value = operator[i];
				return tok;
			}
		}
		if(Character.isLetter(this.string.charAt(pos))){
			String ident = "";
			while(string.length() > pos && Character.isLetterOrDigit(this.string.charAt(pos))){
				ident = ident + this.string.charAt(pos);
				pos ++;
			}
			tok.Classe = TokenClass.TOK_IDENT;
			tok.Value = ident;
			return tok;
		}
		pos ++;
		return next();
	}
	

	/*********************************
	 * Vérifie que la chaine de token qui suit est bien une primitive.
	 * Une primitive peut etre:
	 * - l'inverse du primitive : (-P)
	 * - un INT simple (INT)
	 * - un identifier de type variable(ex: test) (IDENT)
	 * - un identifier de type appel à une fonction(ex: test(a, b)) (APPEL)
	 * - une expression entre parathése( (E) )
	 * - si il ne reconnait rien, on renvoie un arbre vide, provoquera une erreur.
	 *********************************/		
	private Arbre primitive() throws Exception{
		//CAS -P
		if(look().Classe == TokenClass.TOK_LESS){
			Arbre a = new Arbre(next());
			Arbre neg = primitive();
			if (neg.tok.Classe != TokenClass.TOK_NULL){
				Token tmp = new Token();
				tmp.Classe = TokenClass.TOK_IDENT;
				tmp.Value = "0";
				a.fils.add(new Arbre(tmp));
				a.fils.add(neg);
				return a;
			}
			return a;
		}
		//CAS INT
		if(look().Classe == TokenClass.TOK_INT){
			return new Arbre(next());
		}
		if(look().Classe == TokenClass.TOK_IDENT){
			Token name = next();
			Arbre appel = new Arbre(new Token(TokenClass.TOK_APP, name.Value));
			//CAS APPEL
			if (look().Classe == TokenClass.TOK_PO){
				next();
				Arbre vars = new Arbre(new Token(TokenClass.TOK_VAR, "var"));
				int i = 0;
				while(look().Classe != TokenClass.TOK_PF){
					Token ident = next();
					if (ident.Classe == TokenClass.TOK_IDENT || ident.Classe == TokenClass.TOK_INT){
						ident.Value = Integer.toString(symb.search(ident.Value).getIndex());
						vars.fils.add(new Arbre(ident));
						i++;
					}
				}
				if(!functions.contains(name.Value + "|" + i)){
					System.err.println(name.Value + " PAS DECALRE OU NOMBRE D'ARGUMENT: " + i + " INCORRECT");
				}
				if(next().Classe == TokenClass.TOK_PF){
					appel.fils.add(vars);
					return appel;
				}
			}
			//CAS IDENT
			else if (symb.search(name.Value) != null){
				name.Value = Integer.toString(symb.search(name.Value).getIndex());
				return new Arbre(name);
			}
			else{
				System.err.println("VARIABLE " + look().Value + " NON DECLARE");
				throw new Exception();
			}
		}
		//CAS (E)
		if(look().Classe != TokenClass.TOK_PO){
			return new Arbre(new Token());
		}
		pos ++;
		Arbre res = additive();
		if (res.tok.Classe != TokenClass.TOK_NULL && look().Classe == TokenClass.TOK_PF){
			pos ++;
			return res;
		}
		else if(res.tok.Classe != TokenClass.TOK_NULL ){
			return res;
		}
		//SI AUCUN DES CAS, ARBRE NUL, PROVOQUERA UNE ERREUR
		return new Arbre(new Token());
	}
	
	/*********************************
	 * Vérifie que la chaine de token qui suit est bien un multiplieur
	 * Un multiplieur peut etre:
	 * - une primitive simple: P
	 * - une multiplication de P et d'un multiplieur: P * M
	 * - une division de P et d'un multiplieur: P / M
	 * - si il ne reconnait rien, on renvoie un arbre vide, provoquera une erreur.
	 *********************************/
	private Arbre multiplieur() throws Exception{
		Arbre p = primitive();
		if(p.tok.Classe == TokenClass.TOK_NULL){
			return new Arbre(new Token());
		}
		if(look().Classe == TokenClass.TOK_MULT || look().Classe == TokenClass.TOK_DIV){
			//CAS P / M et CAS P * M
			Arbre a = new Arbre(next());
			Arbre b = multiplieur();
			if (b.tok.Classe != TokenClass.TOK_NULL){
				a.fils.add(p);
				a.fils.add(b);
				return a;
			}
			System.err.println("ERREUR DANS MULTIPLIEUR");
			throw new Exception();
		}
		// CAS P simple
		return p;
	}
	
	/*********************************
	 * Vérifie que la chaine de token qui suit est bien une additive
	 * Une additive peut etre:
	 * - un multiplieur simple: M
	 * - une addition de M et d'une additive: M + A
	 * - une soustraction de M et d'une additive: M - A
	 * - si il ne reconnait rien, on renvoie un arbre vide, provoquera une erreur.
	 *********************************/
	private Arbre additive() throws Exception{
		Arbre p = multiplieur();
		if(p.tok.Classe == TokenClass.TOK_NULL){
			return new Arbre(new Token());
		}
		if(look().Classe == TokenClass.TOK_ADD || look().Classe == TokenClass.TOK_LESS){
			//CAS M + A et CAS M - A
			Arbre a = new Arbre(next());
			Arbre b = additive();
			if (b.tok.Classe != TokenClass.TOK_NULL){
				a.fils.add(p);
				a.fils.add(b);
				return a;
			}
			System.err.println("ERREUR DANS ADDITIVE");
			throw new Exception();
		}
		// CAS M simple
		return p;
	}
	
	/*********************************
	 * Vérifie que la chaine de token qui suit est bien une comparative
	 * Une comparative peut etre:
	 * - un true ou un false
	 * - une comparaison de deux additive
	 * - si il ne reconnait rien, on renvoie un arbre vide, provoquera une erreur.
	 *********************************/
	private Arbre comparative() throws Exception{
		//CAS TRUE ET FALSE
		if (look().Classe == TokenClass.TOK_TRUE){
			next();
			return new Arbre(new Token(TokenClass.TOK_INT, "1"));
		}
		if (look().Classe == TokenClass.TOK_FALSE){
			next();
			return new Arbre(new Token(TokenClass.TOK_INT, "0"));
		}
		Arbre p = additive();
		if(p.tok.Classe == TokenClass.TOK_NULL){
			return new Arbre(new Token());
		}
		if(look().Classe == TokenClass.TOK_EQA || look().Classe == TokenClass.TOK_DIFF 
				|| look().Classe == TokenClass.TOK_SUPE || look().Classe == TokenClass.TOK_INFE
				|| look().Classe == TokenClass.TOK_SUP || look().Classe == TokenClass.TOK_INF){
			Arbre a = new Arbre(next());
			Arbre b = additive();
			if (b.tok.Classe != TokenClass.TOK_NULL){
				a.fils.add(p);
				a.fils.add(b);
				return a;
			}
			System.err.println("ERREUR DANS COMPARATIVE");
			throw new Exception();
		}
		return new Arbre(new Token());
	}
	
	/*********************************
	 * Vérifie que la chaine de token qui suit est bien une expression logique
	 * Une expression logique peut etre:
	 * - un NOT expression logique : !L
	 * - une comparaison simple: C
	 * - C AND L
	 * - C OR L
	 * - si il ne reconnait rien, on renvoie un arbre vide, provoquera une erreur.
	 *********************************/
	private Arbre logical() throws Exception{
		// CAS !L
		if(look().Classe == TokenClass.TOK_NOT){
			Arbre not = new Arbre(next());
			Arbre log = logical();
			if(log.tok.Classe != TokenClass.TOK_NULL){
				not.fils.add(log);
				return not;
			}
			else{
				System.err.println("ERREUR DANS LOGICAL AVEC LE '!'");
				throw new Exception();
			}
		}
		Arbre p = comparative();
		if(p.tok.Classe == TokenClass.TOK_NULL){
			return new Arbre(new Token());
		}
		//CAS C OR L et C AND L
		if(look().Classe == TokenClass.TOK_AND || look().Classe == TokenClass.TOK_OR){
			Arbre a = new Arbre(next());
			Arbre b = logical();
			if (b.tok.Classe != TokenClass.TOK_NULL){
				a.fils.add(p);
				a.fils.add(b);
				return a;
			}
			System.err.println("ERREUR DANS LOGICAL");
			throw new Exception();
		}
		//CAS C
		return p;
	}
	
	/*********************************
	 * Vérifie que la chaine de token qui suit est bien une affectation
	 * Une affectation est de la forme IDENT = A:
	 * si il ne reconnait rien, on renvoie un arbre vide, provoquera une erreur.
	 *********************************/
	private Arbre affectation() throws Exception{
		if(look().Classe == TokenClass.TOK_IDENT){
			if (symb.search(look().Value) != null){
				Token variable = next();
				variable.Value = Integer.toString(symb.search(variable.Value).getIndex());
				Arbre ident = new Arbre(variable);
				if(look().Classe == TokenClass.TOK_AFF){
					Arbre affect = new Arbre(next());
					Arbre exp = additive();
					if (exp.tok.Classe != TokenClass.TOK_NULL){
						affect.fils.add(ident);
						affect.fils.add(exp);
						return affect;
					}
				}
				else{
					return new Arbre(new Token());					
				}
			}
			else{
				return new Arbre(new Token());	
			}
		}
		return new Arbre(new Token());		
	}
	
	/*********************************
	 * On récupére toute les fonctions et on stock les noms en mémoire.
	 * Une fonction est de la forme: function nom_fonction(var nom_variable, ...){bloc_instructions}
	 * Il peut y avoir aucune variable.
	 *********************************/
	private Arbre function() throws Exception{
		Arbre funcs = new Arbre(new Token(TokenClass.TOK_RAC, "ORIGIN"));
		Arbre func = new Arbre(new Token());
		do{
			func = new Arbre(new Token());
			if(next().Classe == TokenClass.TOK_FUNC && look().Classe == TokenClass.TOK_IDENT){
				String name = next().Value;
				func = new Arbre(new Token(TokenClass.TOK_FUNC, name));
				if (next().Classe == TokenClass.TOK_PO){
					Arbre vars = new Arbre(new Token(TokenClass.TOK_VAR, "var"));
					int i = 0;
					while(look().Classe != TokenClass.TOK_PF){
						Token ident = next();
						if (ident.Classe == TokenClass.TOK_IDENT){
							vars.fils.add(new Arbre(ident));
							i++;
						}
					}
					functions.add(name + "|" + i);
					next();
					if (next().Classe == TokenClass.TOK_AO){
						int mem = index;
						symb.push();
						Arbre content = instruction();
						if (content.tok.Classe != TokenClass.TOK_NULL && next().Classe == TokenClass.TOK_AF){
							func.fils.add(vars);
							func.fils.add(content);
							funcs.fils.add(func);
							symb.pop();
							index = mem;
						}
						else{
							System.err.println("ERREUR DANS CONTENU FONCTION: " + func.tok.Value);
							throw new Exception();
						}
					}
					else{
						System.err.println("ERREUR APRES DECLARATION VARIABLES FONCTION");
						throw new Exception();
					}
				}
				else{
					System.err.println("ERREUR APRES DECLARATION FONCTION");
					throw new Exception();
				}
			}
		}while(func.tok.Classe != TokenClass.TOK_NULL);
		return funcs;
	}

	
	/*********************************
	 * Récupére un bloc d'instruction. Cela peut etre:
	 * - une affectation
	 * - une déclaration de variable
	 * - un bloc if
	 * - une boucle for
	 * - une boucle while
	 * - un return
	 *********************************/
	private Arbre instruction() throws Exception{
		Arbre seq = new Arbre(new Token(TokenClass.TOK_SUIT, " => "));
		Arbre instr = new Arbre(new Token());
		do{
			//AFFECTATION
			instr = affectation();
			if (instr.tok.Classe != TokenClass.TOK_NULL){
				if (next().Classe == TokenClass.TOK_PV){
					seq.fils.add(instr);					
				}
				else{
					System.err.println("POINT VIRGULE MANQUANT");
					throw new Exception();				
				}
			}
			else{
				//DECLARATION
				if(look().Classe == TokenClass.TOK_VAR){
					instr = new Arbre(next());
					if(look().Classe == TokenClass.TOK_IDENT){
						instr.fils.add(new Arbre(next()));
						if (next().Classe == TokenClass.TOK_PV){
							if (symb.searchSameLevel(instr.fils.get(0).tok.Value) == null){
								index += 1;
								symb.define(instr.fils.get(0).tok.Value, index);
								instr.fils.get(0).tok.Value = Integer.toString(index);
							}
							else{
								System.err.println("VARIABLE " + instr.fils.get(0).tok.Value + " DEJA DECLARE AU MEME NIVEAU");
								throw new Exception();
							}
							seq.fils.add(instr);							
						}
						else{
							System.err.println("POINT VIRGULE MANQUANT");
							throw new Exception();				
						}						
					}
					else{
						System.err.println("VAR SANS IDENT");
						throw new Exception();				
					}						
				}
				else{
					//CONDITION
					if(look().Classe == TokenClass.TOK_IF){
						instr = new Arbre(next());
						if(next().Classe == TokenClass.TOK_PO){
							Arbre exp = logical();
							if(exp.tok.Classe != TokenClass.TOK_NULL){
								if(next().Classe == TokenClass.TOK_PF && next().Classe == TokenClass.TOK_AO){
									int mem = index;
									symb.push();
									Arbre instrIf = instruction();
									if(instrIf.tok.Classe != TokenClass.TOK_NULL && next().Classe == TokenClass.TOK_AF){
										instr.fils.add(exp);
										instr.fils.add(instrIf);
										symb.pop();
										index = mem;
										if(look().Classe == TokenClass.TOK_ELSE && next().Classe == TokenClass.TOK_ELSE && next().Classe == TokenClass.TOK_AO){
											mem = index;
											symb.push();
											Arbre instrElse = instruction();
											if(instrElse.tok.Classe != TokenClass.TOK_NULL && next().Classe == TokenClass.TOK_AF){
												instr.fils.add(instrElse);
												symb.pop();
												index = mem;
											}
											else{
												System.err.println("INSTRUCTION MANQUANTE DANS ELSE");
												throw new Exception();	
											}
										}
										seq.fils.add(instr);
									}
									else{
										System.err.println("INSTRUCTION MANQUANTE DANS IF OU ACCOLADE FERMANTE MANQUANTE");
										throw new Exception();	
									}
								}
								else{
									System.err.println("PARENTHESE FERMANTE OU ACCOLADE OUVRANTE MANQUANTE DANS IF");
									throw new Exception();				
								}
							}
							else{
								System.err.println("EXPRESSION MANQUANTE DANS IF");
								throw new Exception();				
							}	
						}
						else{
							System.err.println("PARENTHESE OUVRANTE MANQUANTE DANS IF");
							throw new Exception();				
						}	
					}
					else{
						//BOUCLE FOR
						if(look().Classe == TokenClass.TOK_FOR){
							instr = new Arbre(next());
							if(next().Classe == TokenClass.TOK_PO){
								Arbre aff = affectation();
								if(aff.tok.Classe != TokenClass.TOK_NULL){
									if(next().Classe == TokenClass.TOK_PV){
										Arbre exp = logical();
										if(exp.tok.Classe != TokenClass.TOK_NULL && next().Classe == TokenClass.TOK_PV){
											Arbre instrFor = affectation();
											if(instrFor.tok.Classe != TokenClass.TOK_NULL && next().Classe == TokenClass.TOK_PF && next().Classe == TokenClass.TOK_AO){
												int mem = index;
												symb.push();
												Arbre instrBloc = instruction();
												if(instrBloc.tok.Classe != TokenClass.TOK_NULL && next().Classe == TokenClass.TOK_AF){
													Arbre instrIf = new Arbre(new Token(TokenClass.TOK_IF, "if"));
													instr.fils.add(aff);
													instr.fils.add(instrIf);
													instrBloc.fils.add(instrFor);
													instrIf.fils.add(exp);
													instrIf.fils.add(instrBloc);						
													seq.fils.add(instr);
													symb.pop();
													index = mem;
												}						
												else{
													System.err.println("INSTRUCTION DAND LA BOUCLE FOR INCORRECT OU ACCOLADE MANQUANTE");
													throw new Exception();	
												}
											}
											else{
												System.err.println("INSTRUCTION DE LA BOUCLE FOR INCORRECT OU PARENTHESE MANQUANTE");
												throw new Exception();	
											}
										}
										else{
											System.err.println("EXPRESSION LOGIQUE DE LA BOUCLE FOR INCORRECT OU POINT VIRGULE MANQUANT");
											throw new Exception();	
										}
									}
									else{
										System.err.println("POINT VIRGULE MANQUANT DANS DECLARATION BOUCLE FOR");
										throw new Exception();				
									}
								}
								else{
									System.err.println("EXPRESSION MANQUANTE DANS FOR");
									throw new Exception();				
								}	
							}
							else{
								System.err.println("PARENTHESE OUVRANTE MANQUANTE DANS FOR");
								throw new Exception();				
							}							
						}
						else{
							//BOUCLE WHILE
							if(look().Classe == TokenClass.TOK_WHILE){
								instr = new Arbre(next());
								if(next().Classe == TokenClass.TOK_PO){
									Arbre condition = logical();
									if(condition.tok.Classe != TokenClass.TOK_NULL && next().Classe == TokenClass.TOK_PF && next().Classe == TokenClass.TOK_AO){
										int mem = index;
										symb.push();
										Arbre instrBloc = instruction();
										if(instrBloc.tok.Classe != TokenClass.TOK_NULL && next().Classe == TokenClass.TOK_AF){
											Arbre instrIf = new Arbre(new Token(TokenClass.TOK_IF, "if"));
											instr.fils.add(new Arbre(new Token()));
											instr.fils.add(instrIf);
											instrIf.fils.add(condition);
											instrIf.fils.add(instrBloc);						
											seq.fils.add(instr);
											symb.pop();
											index = mem;
										}
										else{
											System.err.println("INSTRUCTION/ACCOLADE MANQUANTE DANS BOUCLE WHILE");
											throw new Exception();				
										}
									}
									else{
										System.err.println("EXPRESSION MANQUANTE OU PARENTHESE/ACCOLADE MANQUANTE DANS WHILE");
										throw new Exception();				
									}	
								}
								else{
									System.err.println("PARENTHESE OUVRANTE MANQUANTE DANS WHILE");
									throw new Exception();				
								}
							}
							else{
								//RETURN
								if(look().Classe == TokenClass.TOK_RET){
									instr = new Arbre(next());
									Arbre exp = additive();
									if(exp.tok.Classe != TokenClass.TOK_NULL && next().Classe == TokenClass.TOK_PV){
										instr.fils.add(exp);						
										seq.fils.add(instr);
									}
									else{
										System.err.println("ERREUR DANS LE RETURN");
										throw new Exception();				
									}
								}
							}
						}
					}
				}
			}
		}while(instr.tok.Classe != TokenClass.TOK_NULL && look().Classe != TokenClass.TOK_AF);
		return seq;
	}	
}
