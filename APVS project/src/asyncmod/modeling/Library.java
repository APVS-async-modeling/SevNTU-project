package asyncmod.modeling;

import java.util.LinkedHashMap;
import java.util.Map;

public class Library {
    private Map<String, Element> library;
    
	public Library() {
	    library = new LinkedHashMap<String, Element>();
	}

    public Map<String, Element> getLibrary() {
        return library;
    }
    public void setLibrary(Map<String, Element> library) {
        this.library = library;
    }
}
 