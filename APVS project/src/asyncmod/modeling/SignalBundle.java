package asyncmod.modeling;

import java.util.HashMap;
import java.util.Map;

public class SignalBundle {
    Map<String, Signal> internals;
    Map<String, Signal> circuits;
    Map<String, Signal> contacts;
    
    public SignalBundle () {
        internals = new HashMap<String, Signal>();
        circuits = new HashMap<String, Signal>();
        contacts = new HashMap<String, Signal>();
    }

    public Map<String, Signal> getInternals() {
        return internals;
    }

    public void setInternals(Map<String, Signal> internals) {
        this.internals = internals;
    }

    public Map<String, Signal> getCircuits() {
        return circuits;
    }

    public void setCircuits(Map<String, Signal> circuits) {
        this.circuits = circuits;
    }

    public Map<String, Signal> getContacts() {
        return contacts;
    }

    public void setContacts(Map<String, Signal> contacts) {
        this.contacts = contacts;
    }
    
    public Signal getInternal(String name) {
        return internals.get(name);
    }
    
    public Signal getCircuits(String name) {
        return circuits.get(name);
    }
    
    public Signal getContact(String name) {
        return contacts.get(name);
    }
}
