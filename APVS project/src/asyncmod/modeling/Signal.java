package asyncmod.modeling;

import java.util.NavigableMap;
import java.util.TreeMap;

public class Signal {
    protected NavigableMap<Long, Integer> signal;
    
    public Signal() {
        signal = new TreeMap<Long, Integer>();
    }
    
    public Integer getState(Number time) {
        Long key = (Long) time;
        key = (signal.floorKey((Long) key));
        return key == null ? -1 : signal.get(key);
    }
    
    public boolean isPredefined(Number time) {
        return isPredefined(getState(time));
    }
    
    public boolean isPredefined(Integer state) {
        if (state == -1) return false; 
        return (state & 0x0100) == 0x0100 ? true : false;
    }

    public String[] getSignal() {
        String[] array = new String[signal.size()];
        int n = 0;
        for(Long key : signal.keySet()) {
            array[n] = key.longValue() + "=" + signal.get(key);
            n++;
        }
        return array;
    }
    
    public void setSignal(String[] array) {
        for(int n = 0; n < array.length; n++) {
            Long time = Long.parseLong(array[n].substring(0, array[n].indexOf('=')));
            Integer state = Integer.parseInt(array[n].substring(array[n].indexOf('=') + 1));
            signal.put(time, state);
        }
    }
    
    public String toString() {
        return signal.toString();
    }

}
