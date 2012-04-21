package asyncmod.modeling;

public class Contact implements Comparable<Contact> {
    protected String element;
    protected Integer cnumber;
    
    public Contact(String element, Integer cnumber) {
        this.element = element;
        this.cnumber = cnumber;
    }
    
    public Contact(String contact) {
        this.element = contact.substring(0, contact.indexOf('='));
        this.cnumber= Integer.parseInt(contact.substring(contact.indexOf('=') + 1));
    }
    
    
    
    public String getElement() {
        return element;
    }

    public Integer getCnumber() {
        return cnumber;
    }
    
    public String getContact() {
        return toString();
    }

    public void setContact(String contact) {
        this.element = contact.substring(0, contact.indexOf('='));
        this.cnumber = Integer.parseInt(contact.substring(contact.indexOf('=') + 1));
    }
    
    public String toString() {
        return element + "=" + cnumber;
    }
    
    public int hashCode() {
        return toString().hashCode();//element.hashCode() + contact.hashCode();
    }
    
    public boolean equals(Contact other) {
        return this.element.equals(other.element) && this.cnumber.equals(other.cnumber);
    }
    public boolean equals(Object other) {
        return equals((Contact) other);
    }
    public int compareTo(Contact other) {
        return this.element.equals(other.element) 
                ? this.cnumber.compareTo(other.cnumber)
                : this.element.compareTo(other.element);
    }
}
