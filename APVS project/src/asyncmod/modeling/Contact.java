package asyncmod.modeling;

public class Contact implements Comparable<Contact> {
    protected String element;
    protected Integer contact;
    
    public Contact(String element, Integer contact) {
        this.element = element;
        this.contact = contact;
    }
    
    public Contact(String contact) {
        this.element = contact.substring(0, contact.indexOf('='));
        this.contact = Integer.parseInt(contact.substring(contact.indexOf('=') + 1));
    }
    
    public String getContact() {
        return toString();
    }

    public void setContact(String contact) {
        this.element = contact.substring(0, contact.indexOf('='));
        this.contact = Integer.parseInt(contact.substring(contact.indexOf('=') + 1));
    }
    
    public String toString() {
        return element + "=" + contact;
    }
    
    public int hashCode() {
        return toString().hashCode();//element.hashCode() + contact.hashCode();
    }
    
    public boolean equals(Contact other) {
        return this.element.equals(other.element) && this.contact.equals(other.contact);
    }
    public boolean equals(Object other) {
        return equals((Contact) other);
    }
    public int compareTo(Contact other) {
        return this.element.equals(other.element) 
                ? this.contact.compareTo(other.contact)
                : this.element.compareTo(other.element);
    }
}
