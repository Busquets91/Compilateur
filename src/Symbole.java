
public class Symbole {
	private String ident;
	private int index;
	
	public Symbole(String id, int i){
		this.ident = id;
		this.index = i;
	}
	
	public String getIdent(){
		return this.ident;
	}
	
	public int getIndex(){
		return this.index;
	}
}
