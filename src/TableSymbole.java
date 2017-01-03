
import java.util.HashMap;
import java.util.Objects;
import java.util.Stack;


public class TableSymbole {
	
	private Stack<HashMap<Integer, Symbole>> pile;
	private int rang;
	
	public Symbole search(String ident){
		for(int i = pile.size()-1; i >= 0 ; i--){
			for (Integer key : pile.elementAt(i).keySet()){
				if (Objects.equals(ident, pile.elementAt(i).get(key).getIdent())){
					return pile.elementAt(i).get(key);
				}
			}
		}
		return null;
	}
	
	public Symbole searchSameLevel(String ident){
		for (Integer key : pile.elementAt(pile.size()-1).keySet()){
			if (Objects.equals(ident, pile.elementAt(pile.size()-1).get(key).getIdent())){
				return pile.elementAt(pile.size()-1).get(key);
			}
		}
		return null;
	}
	
	public Symbole define(String ident, int index) throws Exception{
		Symbole newSymb = new Symbole(ident, index);
		HashMap<Integer, Symbole> currentHm = pile.peek();
		for (Integer key : currentHm.keySet()){
			if (Objects.equals(ident , currentHm.get(key).getIdent())){
				System.err.println("VARIABLE " + ident + " NON DECLARE");
				throw new Exception();
			}
		}
		currentHm.put(rang, newSymb);
		rang ++;
		return newSymb;
	}
	
	public void push(){
		HashMap<Integer, Symbole> hm = new HashMap<Integer, Symbole>();
		pile.push(hm);
		this.rang = 0;
	}
	
	public void pop(){
		pile.pop();
		if(pile.isEmpty()){
			this.rang = 0;
		}
		else{
			this.rang = pile.peek().size();
		}
	}
	
	public TableSymbole(){
		this.pile = new Stack<HashMap<Integer, Symbole>>();
		this.rang = 0;
	}
	
	public void print(){
		for(HashMap<Integer, Symbole> hm : pile){
			System.out.println("-------------------");
			for (Integer key : hm.keySet()){
				System.out.println(hm.get(key).getIdent());
			}
		}
	}
	
	public Stack<HashMap<Integer, Symbole>> getPile(){
		return pile;
	}
	
	public int getRang(){
		return rang;
	}
}
