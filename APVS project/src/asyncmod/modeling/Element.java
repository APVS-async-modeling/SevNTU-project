package asyncmod.modeling;

public class Element {
    protected String name;
    protected String descr;
    protected long delay;
    protected int icnt;
    protected int ocnt;
    protected int ecnt;
    protected int smask;
    protected String[] cnames;
    protected Integer[][] table;
    
    public Element() {
        delay = icnt = ocnt = ecnt = smask = -1;
        cnames = null;
        table = null;
    }
    
    public void process(int[] array) {
        for(Integer[] row : table) {
            if(row.length != array.length) {
                return;
            }
            else {
                boolean equal = true;
                for(int n = 0; n < icnt + ecnt; n++) {
                    equal &= (row[n] == array[n]);
                }
                if(equal) {
                    for(int n = ocnt + ecnt; n < array.length; n++) {
                        array[n] = row[n];
                    }
                    return;
                } else {
                    continue;
                }
            }
        }
    }
    
    public boolean checkIntegrity(String message) {
        if(name == null) return false;
        if(descr == null) return false;
        if(delay == -1) return false;
        if(icnt == -1) return false;
        if(ocnt == -1) return false;
        if(ecnt == -1) return false;
        if(smask == -1) return false;
        if(table == null) return false;
        if(cnames == null) return false;
        
        int columns = icnt + ocnt * 2  + ecnt;
        int rows = (int)Math.pow(Integer.bitCount(smask) + 1, icnt + ecnt);
        
        if(table.length != rows) return false;
        if(cnames.length != columns) return false;
        for(Integer[] row : table) {
            if(row.length != columns) return false;
        }
        return true;
    }
    
    public boolean isInput(int contact) {
        return contact < icnt;
    }
    public boolean isOutput(int contact) {
        return contact >= icnt && contact < ocnt;
    }
    public boolean isInternal(int contact) {
        return contact >= icnt + ocnt && contact < icnt + ocnt + ecnt;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescr() {
        return descr;
    }
    public void setDescr(String descr) {
        this.descr = descr;
    }
    public long getDelay() {
        return delay;
    }
    public void setDelay(long delay) {
        this.delay = delay;
    }
    public int getIcnt() {
        return icnt;
    }
    public void setIcnt(int icnt) {
        this.icnt = icnt;
    }
    public int getOcnt() {
        return ocnt;
    }
    public void setOcnt(int ocnt) {
        this.ocnt = ocnt;
    }
    public int getEcnt() {
        return ecnt;
    }
    public void setEcnt(int ecnt) {
        this.ecnt = ecnt;
    }
    public int getSmask() {
        return smask;
    }
    public void setSmask(int smask) {
        this.smask = smask;
    }
    public String[] getCnames() {
        return cnames;
    }
    public void setCnames(String[] cnames) {
        this.cnames = cnames;
    }
    public Integer[][] getTable() {
        return table;
    }
    public void setTable(Integer[][] table) {
        this.table = table;
    }
}
