package asyncmod.modeling;


import java.util.HashMap;
import java.util.Map;

public class SignalBundle {
    protected Map<Contact, Signal> signals;
    
    public SignalBundle () {
        signals = new HashMap<Contact, Signal>();
    }

    public void setSignal(Map<Contact, Signal> contacts) {
        this.signals = contacts;
    }

    public Map<Contact, Signal> getSignals() {
        return signals;
    }

    public void setSignals(Map<Contact, Signal> signals) {
        this.signals = signals;
    }
}
