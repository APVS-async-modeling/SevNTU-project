package asyncmod.du_model;

public class DUModel {

	private int id;
	private String name;

	public DUModel(String name) {
		this.name = name;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int aNewId){
		this.id = aNewId;
	}

	public String getName() {
		return name;
	}

    public int getCircuitCount() {
        // TODO Auto-generated method stub
        return 0;
    }
	
	// ... etc
	
}