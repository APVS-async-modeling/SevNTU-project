package asyncmod.modeling;

import java.util.ArrayList;
import java.util.List;

public class Circuit {
    protected List<Contact> contacts;
    
    public Circuit() {
        contacts = new ArrayList<Contact>();
    }
    
    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }
}
