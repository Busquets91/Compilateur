
public class Token {

	public TokenClass Classe;
	public String Value;
	
	public Token(){
		this.Classe = TokenClass.TOK_NULL;
		this.Value = "";
	}
	
	public Token(TokenClass classe, String val){
		this.Classe = classe;
		this.Value = val;
	}
}
