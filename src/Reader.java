import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Reader {
	protected String str;
	protected String path;
	protected FileReader fichier;
	
	public String getString(){
		return str;
	}
	
	public Reader(String filePath) throws IOException{
		path = filePath;
		fichier =  new FileReader(filePath);
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		try {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    str = sb.toString();
		} finally {
		    br.close();
		}
	}
	
	/**
	 * 
	 * @return [String] nom du fichier sans l'extension
	 */
	public String getNameFile(){
		String name = "";
		if (path != null){
			int index = path.indexOf('.');
			if (index >= 0){
				name = path.substring(0, index);
			}
			else{
				name = path;
			}
		}
		return name;
	}
}

