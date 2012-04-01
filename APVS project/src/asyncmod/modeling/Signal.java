package asyncmod.modeling;

import java.util.NavigableMap;
import java.util.TreeMap;

public class Signal {
    NavigableMap<Long, Integer> states;
    
    public Signal() {
        states = new TreeMap<Long, Integer>();
    }
    
    public int getState(long time) {
        Long key = states.floorKey(time);
        return key == null ? -1 : states.get(key);
    }
    
    public boolean isPredefined(long time) {
        return isPredefined(getState(time));
    }
    
    public boolean isPredefined(int state) {
        if (state == -1) return false; 
        return (state & 0xF0) == 0xF0 ? true : false;
    }

    public NavigableMap<Long, Integer> getStates() {
        return states;
    }

    public void setStates(NavigableMap<Long, Integer> states) {
        this.states = states;
    }
    

}
