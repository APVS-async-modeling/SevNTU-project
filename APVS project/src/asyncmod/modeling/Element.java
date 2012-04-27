package asyncmod.modeling;

import java.util.Map;
import java.util.TreeMap;

public class Element {
    protected String name;
    protected String descr;
    protected long delay;
    protected int icnt;
    protected int ocnt;
    protected int ecnt;
    
    /* inputs + internal(t) + internal(t+1) outputs */ 
    
    //protected Map<Integer, String> cnames;
    protected Map<Integer, Formula> formulas;
    
    public Element() {
        formulas = new TreeMap<Integer, Formula>();
        //cnames = new HashMap<Integer, String>();
        delay = icnt = ocnt = ecnt = -1;
    }
    
    public void calculate(int[] array) {
        for(Integer contact : formulas.keySet()) {
            array[contact] = formulas.get(contact).calculate(array);
        }
    }
    
    public boolean check() {
        if(name == null) return false;
        if(descr == null) return false;
        if(delay == -1) return false;
        if(icnt == -1) return false;
        if(ocnt == -1) return false;
        if(ecnt == -1) return false;
        //if(cnames == null) return false;
        //if(cnames.size() != icnt + ecnt *2 + ocnt) return false;
        if(formulas == null) return false;
        if(formulas.size() != ecnt + ocnt) return false;
        for(Integer formula : formulas.keySet()) {
            if(!formulas.get(formula).check(formula)) return false;
        }
        return true;
    }
    
    public boolean isInput(int contact) {
        return contact < icnt;
    }
    public boolean isOutput(int contact) {
        return contact >= icnt && contact < icnt + ocnt;
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
    
    /*public String[] getCnames() {
        return cnames;
    }
    public void setCnames(String[] cnames) {
        this.cnames = cnames;
    }*/

    public Map<Integer, Formula> getFormulas() {
        return formulas;
    }

    public void setFormulas(Map<Integer, Formula> formulas) {
        this.formulas = formulas;
    }
    
}
