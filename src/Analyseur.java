import java.util.ArrayList;


public class Analyseur {
	
	private TableSymbole symb;
	private String string;
	private int pos;
	private String[] keyWords = {"if", "for", "while", "var", "int", "else"};
	private TokenClass[] keyClasse = {TokenClass.TOK_IF, TokenClass.TOK_FOR, TokenClass.TOK_WHILE, TokenClass.TOK_VAR, TokenClass.TOK_INT, TokenClass.TOK_ELSE};
	private String[] operator = {"&&", "||", "+=", "-=", "*=", "/=", "+", "-", "*", "/", "%", "(", ")", "==", "!=", ">=", "<=", ">", "<", "=", "{", "}", ";"};
	private TokenClass[] keyOperator = {TokenClass.TOK_AND, TokenClass.TOK_OR, TokenClass.TOK_ADDE, TokenClass.TOK_LESSE, TokenClass.TOK_MULTE, TokenClass.TOK_DIVE, TokenClass.TOK_ADD, TokenClass.TOK_LESS,TokenClass.TOK_MULT, TokenClass.TOK_DIV, TokenClass.TOK_MOD, TokenClass.TOK_PO, TokenClass.TOK_PF, TokenClass.TOK_EQA, TokenClass.TOK_DIFF, TokenClass.TOK_SUPE, TokenClass.TOK_INFE, TokenClass.TOK_SUP, TokenClass.TOK_INF, TokenClass.TOK_AFF, TokenClass.TOK_AO, TokenClass.TOK_AF, TokenClass.TOK_PV};
	
	public Analyseur(String str){
		this.string = str;
		this.pos = 0;
		this.symb = new TableSymbole();
	}
	
	public TableSymbole getSymb(){
		return symb;
	}
	
	public Arbre getArbre() throws Exception{
		this.symb.push();
		return instruction();
	}
	
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
	
	private Arbre primitive() throws Exception{
		if(look().Classe == TokenClass.TOK_LESS){
			pos ++;
			Arbre neg = primitive();
			if (neg.tok.Classe != TokenClass.TOK_NULL){
				Token tmp = new Token();
				tmp.Classe = TokenClass.TOK_LESS;
				tmp.Value = "-";
				Arbre a = new Arbre(tmp, new ArrayList<Arbre>());
				a.fils.add(neg);
				return a;
			}
			return new Arbre(next(), new ArrayList<Arbre>());
		}
		if(look().Classe == TokenClass.TOK_INT){
			return new Arbre(next(), new ArrayList<Arbre>());
		}
		if(look().Classe == TokenClass.TOK_IDENT){
			if (symb.search(look().Value) != null){
				return new Arbre(next(), new ArrayList<Arbre>());
			}
			else{
				System.err.println("VARIABLE " + look().Value + " NON DECLARE");
				throw new Exception();
			}
		}
		if(look().Classe != TokenClass.TOK_PO){
			return new Arbre(new Token(), new ArrayList<Arbre>());
		}
		pos ++;
		Arbre res = expression();
		if (res.tok.Classe != TokenClass.TOK_NULL && look().Classe == TokenClass.TOK_PF){
			pos ++;
			return res;
		}
		else if(res.tok.Classe != TokenClass.TOK_NULL ){
			return res;
		}
		return new Arbre(new Token(), new ArrayList<Arbre>());
	}
	
	private Arbre multiplieur() throws Exception{
		Arbre p = primitive();
		if(p.tok.Classe == TokenClass.TOK_NULL){
			return new Arbre(new Token(), new ArrayList<Arbre>());
		}
		if(look().Classe == TokenClass.TOK_MULT || look().Classe == TokenClass.TOK_DIV){
			Arbre a = new Arbre(next(), new ArrayList<Arbre>());
			Arbre b = multiplieur();
			if (b.tok.Classe != TokenClass.TOK_NULL){
				a.fils.add(p);
				a.fils.add(b);
				return a;
			}
			System.err.println("ERREUR DANS MULTIPLIEUR");
			throw new Exception();
		}
		return p;
	}
	
	private Arbre additive() throws Exception{
		Arbre p = multiplieur();
		if(p.tok.Classe == TokenClass.TOK_NULL){
			return new Arbre(new Token(), new ArrayList<Arbre>());
		}
		if(look().Classe == TokenClass.TOK_ADD || look().Classe == TokenClass.TOK_LESS){
			Arbre a = new Arbre(next(), new ArrayList<Arbre>());
			Arbre b = additive();
			if (b.tok.Classe != TokenClass.TOK_NULL){
				a.fils.add(p);
				a.fils.add(b);
				return a;
			}
			System.err.println("ERREUR DANS ADDITIVE");
			throw new Exception();
		}
		return p;
	}
	
	private Arbre comparative() throws Exception{
		Arbre p = additive();
		if(p.tok.Classe == TokenClass.TOK_NULL){
			return new Arbre(new Token(), new ArrayList<Arbre>());
		}
		if(look().Classe == TokenClass.TOK_EQA || look().Classe == TokenClass.TOK_DIFF 
				|| look().Classe == TokenClass.TOK_SUPE || look().Classe == TokenClass.TOK_INFE
				|| look().Classe == TokenClass.TOK_SUP || look().Classe == TokenClass.TOK_INF){
			Arbre a = new Arbre(next(), new ArrayList<Arbre>());
			Arbre b = additive();
			if (b.tok.Classe != TokenClass.TOK_NULL){
				a.fils.add(p);
				a.fils.add(b);
				return a;
			}
			System.err.println("ERREUR DANS COMPARATIVE");
			throw new Exception();
		}
		return p;
	}
	
	private Arbre logical() throws Exception{
		Arbre p = comparative();
		if(p.tok.Classe == TokenClass.TOK_NULL){
			return new Arbre(new Token(), new ArrayList<Arbre>());
		}
		if(look().Classe == TokenClass.TOK_AND || look().Classe == TokenClass.TOK_OR){
			Arbre a = new Arbre(next(), new ArrayList<Arbre>());
			Arbre b = logical();
			if (b.tok.Classe != TokenClass.TOK_NULL){
				a.fils.add(p);
				a.fils.add(b);
				return a;
			}
			System.err.println("ERREUR DANS LOGICAL");
			throw new Exception();
		}
		return p;
	}
	
	private Arbre expression() throws Exception{
		return logical();
	}
	
	private Arbre affectation() throws Exception{
		if(look().Classe == TokenClass.TOK_IDENT){
			if (symb.search(look().Value) != null){
				Arbre ident = new Arbre(next(), new ArrayList<Arbre>());
				if(look().Classe == TokenClass.TOK_AFF){
					Arbre affect = new Arbre(next(), new ArrayList<Arbre>());
					Arbre exp = expression();
					if (exp.tok.Classe != TokenClass.TOK_NULL){
						affect.fils.add(ident);
						affect.fils.add(exp);
						return affect;
					}
					System.err.println("ERREUR DANS AFFECTATION");
					throw new Exception();
				}
				return ident;
			}
			else{
				System.err.println("VARIABLE " + look().Value + " NON DECLARE");
				throw new Exception();
			}
		}
		return new Arbre(new Token(), new ArrayList<Arbre>());		
	}
	
	private Arbre instruction() throws Exception{
		Arbre seq = new Arbre(new Token(TokenClass.TOK_SUIT, " => "), new ArrayList<Arbre>());
		Arbre instr = new Arbre(new Token(), new ArrayList<Arbre>());
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
					instr = new Arbre(next(), new ArrayList<Arbre>());
					if(look().Classe == TokenClass.TOK_IDENT){
						instr.fils.add(new Arbre(next(), new ArrayList<Arbre>()));
						if (next().Classe == TokenClass.TOK_PV){
							if (symb.searchSameLevel(instr.fils.get(0).tok.Value) == null){
								symb.define(instr.fils.get(0).tok.Value);
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
						instr = new Arbre(next(), new ArrayList<Arbre>());
						if(next().Classe == TokenClass.TOK_PO){
							Arbre exp = expression();
							if(exp.tok.Classe != TokenClass.TOK_NULL){
								if(next().Classe == TokenClass.TOK_PF && next().Classe == TokenClass.TOK_AO){
									symb.push();
									Arbre instrIf = instruction();
									if(instrIf.tok.Classe != TokenClass.TOK_NULL && next().Classe == TokenClass.TOK_AF){
										instr.fils.add(exp);
										instr.fils.add(instrIf);
										symb.pop();
										if(next().Classe == TokenClass.TOK_ELSE && next().Classe == TokenClass.TOK_AO){
											symb.push();
											Arbre instrElse = instruction();
											if(instrElse.tok.Classe != TokenClass.TOK_NULL && next().Classe == TokenClass.TOK_AF){
												instr.fils.add(instrElse);
												symb.pop();
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
							instr = new Arbre(next(), new ArrayList<Arbre>());
							if(next().Classe == TokenClass.TOK_PO){
								Arbre aff = affectation();
								if(aff.tok.Classe != TokenClass.TOK_NULL){
									if(next().Classe == TokenClass.TOK_PV){
										Arbre exp = expression();
										if(exp.tok.Classe != TokenClass.TOK_NULL && next().Classe == TokenClass.TOK_PV){
											Arbre instrFor = affectation();
											if(instrFor.tok.Classe != TokenClass.TOK_NULL && next().Classe == TokenClass.TOK_PF && next().Classe == TokenClass.TOK_AO){
												symb.push();
												Arbre instrBloc = instruction();
												if(instrBloc.tok.Classe != TokenClass.TOK_NULL && next().Classe == TokenClass.TOK_AF){
													Arbre instrIf = new Arbre(new Token(TokenClass.TOK_IF, "if"), new ArrayList<Arbre>());
													instr.fils.add(aff);
													instr.fils.add(instrIf);
													instrBloc.fils.add(instrFor);
													instrIf.fils.add(exp);
													instrIf.fils.add(instrBloc);						
													seq.fils.add(instr);
													symb.pop();
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
								instr = new Arbre(next(), new ArrayList<Arbre>());
								if(next().Classe == TokenClass.TOK_PO){
									Arbre condition = expression();
									if(condition.tok.Classe != TokenClass.TOK_NULL && next().Classe == TokenClass.TOK_PF && next().Classe == TokenClass.TOK_AO){
										symb.push();
										Arbre instrBloc = instruction();
										if(instrBloc.tok.Classe != TokenClass.TOK_NULL && next().Classe == TokenClass.TOK_AF){
											Arbre instrIf = new Arbre(new Token(TokenClass.TOK_IF, "if"), new ArrayList<Arbre>());
											instr.fils.add(new Arbre(new Token(), new ArrayList<Arbre>()));
											instr.fils.add(instrIf);
											instrIf.fils.add(condition);
											instrIf.fils.add(instrBloc);						
											seq.fils.add(instr);
											symb.pop();
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
						}
					}
				}
			}
		}while(instr.tok.Classe != TokenClass.TOK_NULL);
		return seq;
	}
	
	public ArrayList<Token> getTokTable(){
		Token tok = new Token();
		ArrayList<Token> tab = new ArrayList<Token>();
		do{
			tok = this.next();
			tab.add(tok);
		}while(tok.Classe != TokenClass.TOK_EOF);
		return tab;
	}
	
	public static void main(String args[]) throws Exception{
		//FICHIER
		Reader fic = new Reader("/home/tp-home004/apoint1/workspace/Compilateur.zip_expanded/Compilateur/Analyseur/src/test.txt");
		Analyseur anal = new Analyseur(fic.getString());
		
		//Analyseur anal = new Analyseur("var alexis; alexis = 3;var yo;var vivien; if(yo == alexis - vivien){var vivien; vivien = 0;}else{alexis = 1000;}var i;while(i==0){var vi; vi = 5;}");
		
		anal.symb.push();
		Arbre tst = anal.instruction();
		System.out.println(tst.print());
	}
}
