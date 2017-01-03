import java.util.ArrayList;


public class Arbre {
	
	public Token tok;
	public ArrayList<Arbre> fils;
	
	public Arbre(Token t){
		tok = t;
		fils = new ArrayList<Arbre>();
	}
	
	public String print(){
		String p = "";
		p += tok.Value;
		for(Arbre a : fils){
			p += "[" + a.print() + "]";
		}
		return p;
	}

}
