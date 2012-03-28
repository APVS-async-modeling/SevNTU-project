package asyncmod.modeling;

public class Element {
    private String name;
    private String descr;
    private long delay;
    private int icnt;
    private int ocnt;
    private int ecnt;
    private int smask;
    private Integer[][] table;
    
    public Element() {
        delay = icnt = ocnt = ecnt = smask = -1;
        table = null;
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
        
        int columns = icnt + ocnt * 2  + ecnt;
        int rows = (int)Math.pow(Integer.bitCount(smask), icnt + ecnt);
        
        if(table.length != rows) return false;
        for(Integer[] row : table) {
            if(row.length != columns) return false;
        }
        
        return true;
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
    public Integer[][] getTable() {
        return table;
    }
    public void setTable(Integer[][] table) {
        this.table = table;
    }
}
