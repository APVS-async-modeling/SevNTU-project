package asyncmod.du_model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class DUModelController {

	DUModel duModel; // current DU model object
	
	String filePath;

	public DUModelController(String filepath) {
		this.filePath = filepath;
	}

	public DUModel parseDUModelFromFile() throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));

		String modelName = br.readLine();		
		duModel = new DUModel(modelName);

		// file parsing...		
		int id = Integer.parseInt(br.readLine());
		duModel.setId(id);

		// etc
		return duModel;
	}

	public DUModel getDUModel () {
		return duModel; // returns null if model haven`t be parsed yet.
	}

}
