import java.util.ArrayList;


public class Arbre {
	
	public Token tok;
	public ArrayList<Arbre> fils;
	public Arbre next;
	
	public Arbre(Token t, ArrayList<Arbre> f){
		tok = t;
		next = null;
		fils = f;
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
