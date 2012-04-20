package asyncmod.modeling;

public class Event {
    protected Contact contact;
    protected int newstate;
    protected long from;

    public Event(Contact contact, int newstate, long from) {
        this.contact = contact;
        this.newstate = newstate;
        this.from = from;
    }

    public Contact getContact() {
        return contact;
    }

    public int getNewstate() {
        return newstate;
    }

    public long getFrom() {
        return from;
    }

    public String toString() {
        return contact.toString() + " -> " + newstate;
    }
    
    public boolean knownAt(long time) {
        return time >= from;
    }

}
