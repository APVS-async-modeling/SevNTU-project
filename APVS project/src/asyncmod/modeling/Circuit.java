package asyncmod.modeling;

import java.util.LinkedList;
import java.util.List;

public class Circuit {
    private List<Contact> contacts;
    
    public Circuit() {
        contacts = new LinkedList<Contact>();
    }
    
    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

}
