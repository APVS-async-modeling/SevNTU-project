package asyncmod.modeling;

import java.util.NavigableMap;
import java.util.TreeMap;

public class Signal {
    /*
     * signals
     * 0x00 = 0
     * 0x01 = 1
     * 0x02 = X
     * 0xF0 = p0
     * 0xF1 = p1
     * 0xF2 = pX
     */
    
    protected NavigableMap<Long, Integer> signal;
    
    public Signal() {
        signal = new TreeMap<Long, Integer>();
    }
    
    public Integer getState(long time) {
        Long key = (Long) time;
        key = (signal.floorKey((Long) key));
        return key == null ? -1 : signal.get(key);
    }
    
    public boolean isPredefined(long time) {
        return isPredefined(getState(time));
    }
    
    public boolean isPredefined(int state) {
        return (state & 0xF0) == 0xF0 ? true : false;
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
    
    public NavigableMap<Long, Integer> getSignalSet() {
        return signal;
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
