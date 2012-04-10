package asyncmod.modeling;

public class Event {
    protected Contact contact;
    protected int newstate;
    
    public Event(Contact contact, int newstate) {
        this.contact = contact;
        this.newstate = newstate;
    }
    
    public Contact getContact() {
        return contact;
    }
    public void setContact(Contact contact) {
        this.contact = contact;
    }
    
    public int getNewstate() {
        return newstate;
    }
    public void setNewstate(int newstate) {
        this.newstate = newstate;
    }
    
    public String toString() {
        return contact.toString() + " -> " + newstate;
    }
    
}
