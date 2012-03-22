package asyncmod.du_model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class DUModelParser {

	String filePath;

	public DUModelParser(String filepath) {
		this.filePath = filepath;
	}

	public DUModel parseDUModelFromFile() throws FileNotFoundException{
		DUModel duModel = new DUModel();
		
		// parsing from file.		
		BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));
		//int id = Integer.parseInt(br.readLine());
		//duModel.setId(id);
		
		// etc
		return duModel; 
	}

}
