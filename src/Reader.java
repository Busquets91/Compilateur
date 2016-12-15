import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Reader {
	protected String str;
	protected FileReader fichier;
	
	public String getString(){
		return str;
	}
	
	public Reader(String filePath) throws IOException{
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
}

