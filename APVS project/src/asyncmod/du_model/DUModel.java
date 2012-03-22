package asyncmod.du_model;

public class DUModel {

	private static DUModel singleDuModelObject;

	int id = 0; // sample id
	String name;

	public int getId() {
		return this.id;
	}
	
	public void setId(int aNewId){
		this.id = aNewId;
	}
	// etc
	
}