package asyncmod.modeling;

public class Contact {
    private String element;
    private String contact;
    
    public Contact() {
        element = contact = null;
    }
    
    public String getElement() {
        return element;
    }
    public void setElement(String element) {
        this.element = element;
    }
    public String getContact() {
        return contact;
    }
    public void setContact(String contact) {
        this.contact = contact;
    }
}