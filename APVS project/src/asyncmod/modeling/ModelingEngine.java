package asyncmod.modeling;

import java.awt.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;


public class ModelingEngine implements Runnable {
    private Scheme scheme;
    private SignalBundle inputs;
    private SignalBundle results;
    private Library library;
    
    private long timecnt;
    private long endtime;
    private List events = null;
    
    public ModelingEngine(String library, String scheme, String signal) {
        Yaml yaml = new Yaml();
        InputStream stream = null;
        
        try {
            stream = new FileInputStream(library);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            this.library = (Library) yaml.load(stream);
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("Неверный документ библиотеки! Куда лез, криворукий пидорас?");
        }
        System.out.println(yaml.dump(this.library));
        
        try {
            stream = new FileInputStream(scheme);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            this.scheme = (Scheme) yaml.load(stream);
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("Неверный документ схемы! Куда лез, криворукий пидорас?");
        }
        System.out.println(yaml.dump(this.scheme));
        
        try {
            stream = new FileInputStream(signal);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Iterable<Object> data = yaml.loadAll(stream);
            if (data.iterator().hasNext()) {
                this.endtime = (Long) data.iterator().next();
            } else {
                throw new Exception("Неверный формат файла сигналов, отсутсвует указание времени моделирования");
            }
            if (data.iterator().hasNext()) {
                this.inputs = (SignalBundle) data.iterator().next();
            } else {
                throw new Exception("Неверный формат файла сигналов, проблема с документом сигналов");
            }
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("Неверный документ сигналов! Куда лез, криворукий пидорас?");
        }
        System.out.println(yaml.dump(this.scheme));
    }
    
    public static void main(String[] args) {
        new ModelingEngine("apvs-library.yaml", "apvs-scheme.yaml", "apvs-signal.yaml");
    }
    
    public void run() {
        timecnt = 0;
        // TODO: моделирование
    }

    
    
}
