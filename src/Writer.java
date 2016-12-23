import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class Writer {
	protected FileOutputStream file;
	
	/**
	 * @desc Ecrit dans un fichier
	 * @param filePath [String] nom du fichier dans lequel on veut écrire
	 * @param Contenu [String] Contenu à écrire dans le fichier
	 * @return [Writer]
	 */
	public Writer(String filePath, String contenu){
		try
		{
		    FileWriter fw = new FileWriter (new File(filePath));
		 
		   fw.write (contenu);
		 
		    fw.close();
		}
		catch (IOException exception)
		{
		    System.out.println ("Erreur lors de la lecture : " + exception.getMessage());
		}
	}
}
