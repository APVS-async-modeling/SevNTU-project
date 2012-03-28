package asyncmod.modeling;

public class Contact {
    private String element;
    private String contact;
    private int type;
    
    public Contact() {
        element = contact = null;
        type = 0;
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
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
}
