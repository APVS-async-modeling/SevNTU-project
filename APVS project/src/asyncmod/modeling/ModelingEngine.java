package asyncmod.modeling;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;


public class ModelingEngine implements Runnable {
    private Scheme scheme;
    private SignalBundle signals;
    private SignalBundle results;
    private Library library;
    
    private long timecnt;
    private long endtime;
    private NavigableMap<Long, List<Event>> events;
    private NavigableMap<Long, Set<String>> active;
    
    public ModelingEngine(String library, String scheme, String signal, long time) {
        Yaml yaml = new Yaml();
        InputStream stream = null;
        endtime = time;
        
        try {
            stream = new FileInputStream(library);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            this.library = (Library) yaml.load(stream);
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("Неверный документ библиотеки!");
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
            System.err.println("Неверный документ схемы!");
        }
        System.out.println(yaml.dump(this.scheme));
        
        try {
            stream = new FileInputStream(signal);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            this.signals = (SignalBundle) yaml.load(stream);
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("Неверный документ сигналов!");
        }
        System.out.println(yaml.dump(this.signals));
    }
    
    public static void main(String[] args) {
        new ModelingEngine("apvs-library.yaml", "apvs-scheme.yaml", "apvs-signal.yaml", 3000).run();
        //new ModelingEngine().test();
    }
    
    public void run() {
        timecnt = 0;
        events = new TreeMap<Long, List<Event>>();
        active = new TreeMap<Long, Set<String>>();
        results = new SignalBundle();
        
        for(Contact contact : signals.getSignals().keySet()) {
            Signal signal = (Signal) signals.getSignals().get(contact);
            for(Long time : signal.signal.keySet()) {
                int state = signal.getState(time);
                if (events.containsKey(time)) {
                    events.get(time).add(new Event(contact, state));
                } else {
                    events.put(time, new LinkedList<Event>());
                    events.get(time).add(new Event(contact, state));
                }
            }
        }
        
        /*for(String elementName : scheme.getElements().keySet()) {
            String elementType = scheme.getElements().get(elementName);
            Element element = elementType != null ? library.getLibrary().get(elementType) : null;
            if(element == null) {
                System.out.println("Unknown exception while modeling caused by broken library or scheme file");
            } else {
                for(int n = 0; n < element.icnt + element.ocnt; n++) {
                    Signal signal = new Signal();
                    signal.signal.put((long) 0, 255);
                    results.signals.put(new Contact(elementName, n), signal);
                }
            }
        }
        for(String inputName : scheme.getInputs()) {
            Signal signal = new Signal();
            signal.signal.put((long) 0, 255);
            results.signals.put(new Contact(inputName, -1), signal);
        }
        for(String outputName : scheme.getOutputs()) {
            Signal signal = new Signal();
            signal.signal.put((long) 0, 255);
            results.signals.put(new Contact(outputName, -1), signal);
        }*/
        
        while(timecnt < endtime) {
            Long curevttime = events.ceilingKey(timecnt);
            Long curacttime = active.ceilingKey(timecnt);
            Long curtime;
            if(curevttime == null && curacttime != null) {
                curtime = curacttime;
            } else if(curevttime != null && curacttime == null) {
                curtime = curevttime;
            } else if(curevttime != null && curacttime != null) {
                curtime = Math.max(curevttime, curacttime);
            } else {
                timecnt = endtime;
                continue;
            }
            timecnt = curtime;
            System.out.println("\nModeling at time " + timecnt);
            /* выборка событий */
            if(events.containsKey(curtime)) {
                for(Event event : events.get(curtime)) {
                    Contact sourceContact = event.getContact();
                    String sourceElementName = (String) sourceContact.element;
                    int sourceContactNumber = (Integer) sourceContact.contact;
                    String sourceElementType = scheme.getElements().get(sourceElementName);
                    Element sourceElement = sourceElementType != null ? library.getLibrary().get(sourceElementType) : null;
                    
                    if((sourceElement == null && scheme.getInputs().contains(sourceElementName)) 
                            || (sourceElement != null && sourceContactNumber >= sourceElement.icnt && sourceContactNumber < sourceElement.icnt + sourceElement.ocnt)) {
                        if(sourceElement == null) {
                            System.out.println("Scheme input '" + sourceContact.element + "' is changed to " + event.newstate);
                        } else {
                            System.out.println("Output #" + (sourceContactNumber - sourceElement.icnt) + " of element '" + sourceElementName + "' is changed to " + event.newstate);
                        }
                        Signal sourceSignal = results.signals.get(sourceContact);
                        if(sourceSignal != null) {
                            sourceSignal.signal.put(timecnt, event.newstate);
                        } else {
                            sourceSignal = new Signal();
                            sourceSignal.signal.put(timecnt, event.newstate);
                        }
                        
                        for(String circuitName : scheme.getCircuits().keySet()) {
                            Circuit circuit = scheme.getCircuits().get(circuitName);
                            if(circuit.getContacts().contains(sourceContact)) {
                                System.out.println("  Circuit '" + circuitName + "' is changed to " + event.newstate);
                                System.out.println("    Contacts involved: " + circuit.getContacts() + "");
                                for(Contact destinationContact : circuit.getContacts()) {
                                    if(scheme.getOutputs().contains(destinationContact.element)) {
                                        System.out.println("      Scheme output '" + destinationContact.element + "' is changed to " + event.newstate);
                                    } else if(destinationContact.equals(sourceContact)) {
                                        continue;
                                    } else {
                                        String destinationElementName = (String) destinationContact.element;
                                        String destinationElementType = scheme.getElements().get(destinationElementName);
                                        Element destinationElement = destinationElementType != null ? library.getLibrary().get(destinationElementType) : null;
                                        if (destinationElement == null) {
                                            System.out.println("Unknown exception while modeling caused by broken library or scheme file");
                                        } else if((Integer)(destinationContact.contact) < destinationElement.icnt) {
                                            System.out.println("      Input #" + destinationContact.contact + " of element '" + destinationElementName + "' is changed to " + event.newstate);
                                            if(active.containsKey(curtime)) {
                                                active.get(curtime).add(destinationElementName);
                                            } else {
                                                active.put(curtime, new TreeSet<String>());
                                                active.get(curtime).add(destinationElementName);
                                            }
                                        }
                                    }
                                    Signal destinationSignal = results.signals.get(destinationContact);
                                    if(destinationSignal != null) {
                                        destinationSignal.signal.put(timecnt, event.newstate);
                                    } else {
                                        destinationSignal = new Signal();
                                        destinationSignal.signal.put(timecnt, event.newstate);
                                    }
                                }
                            }
                        }
                    } else if(sourceElement != null) {
                        if(sourceContactNumber >= (sourceElement.icnt + sourceElement.ocnt)) {
                            System.out.println("Internal state #" + (sourceContactNumber - sourceElement.icnt - sourceElement.ocnt) + " of element '" + sourceElementName + "' is changed to " + event.newstate);
                        } else if(sourceContactNumber < sourceElement.icnt) {
                            System.out.println("Input #" + (sourceContactNumber - sourceElement.icnt) + " of element '" + sourceElementName + "' is changed to " + event.newstate);
                        }
                        Signal sourceSignal = results.signals.get(sourceContact);
                        if(sourceSignal != null) {
                            sourceSignal.signal.put(timecnt, event.newstate);
                        }
                        if(active.containsKey(curtime)) {
                            active.get(curtime).add(sourceElementName);
                        } else {
                            active.put(curtime, new TreeSet<String>());
                            active.get(curtime).add(sourceElementName);
                        }
                    } else {
                        System.out.println("Unknown exception while modeling caused by broken library or scheme file");
                    }
                }
            }
            /* обработка изменений */
            if(active.containsKey(curtime)) {
                for(String elementName : active.get(curtime)) {
                    String elementType = scheme.getElements().get(elementName);
                    Element element = elementType != null ? library.getLibrary().get(elementType) : null;
                    if(element != null) {
                        int[] array = new int[element.icnt + element.ecnt * 2 + element.ocnt];
                        for(int n = 0; n < element.icnt; n++) {
                            array[n] = results.signals.get(new Contact(elementName, n)).getState(curtime);
                        }
                        for(int n = 0; n < element.ecnt; n++) {
                            array[element.icnt + n] = results.signals.get(new Contact(elementName, element.icnt + element.ocnt + n)).getState(curtime);
                        }
                        element.process(array);
                        long nexttime = curtime + element.delay;
                        if (!events.containsKey(nexttime)) {
                            events.put(nexttime, new LinkedList<Event>());
                        }
                        for(int n = 0; n < element.ecnt; n++) {
                            int state = array[element.icnt + element.ecnt + n];
                            Contact contact = new Contact(elementName, element.icnt + element.ocnt + n);
                            events.get(nexttime).add(new Event(contact, state));
                        }
                        for(int n = 0; n < element.ocnt; n++) {
                            int state = array[element.icnt + element.ecnt * 2 + n];
                            Contact contact = new Contact(elementName, element.icnt + n);
                            events.get(nexttime).add(new Event(contact, state));
                        }
                    } else {
                        System.out.println("No such element found");
                    }
                }
            }
            timecnt += 1;
            System.out.println(timecnt);
        }
    }

    public ModelingEngine() {
        
    }
    
    
    public void test() {
        Yaml yaml = new Yaml();
        
        scheme = new Scheme();
        library = new Library();
        signals = new SignalBundle();
        
        Element elem;
        elem = new Element();
        elem.name = "2_AND";
        elem.delay = 75;
        elem.descr = "2-input 2-state AND";
        elem.ecnt = 0;
        elem.icnt = 2;
        elem.ocnt = 1;
        elem.smask = 3;
        elem.cnames = new String[]{
                "IN_1", "IN_2", "!OU_1"
        };
        elem.table = new Integer[][]{
                {0, 0, 0},
                {0, 1, 0},
                {0, -1, 0},
                {1, 0, 0},
                {1, 1, 1},
                {1, -1, -1}
        };
        library.library.put(elem.name, elem);
        
        elem = new Element();
        elem.name = "3_AND";
        elem.delay = 75;
        elem.descr = "3-input 2-state AND";
        elem.ecnt = 0;
        elem.icnt = 3;
        elem.ocnt = 1;
        elem.smask = 3;
        elem.table = new Integer[][]{
                {0, 0, 0, 0},
                {0, 0, 1, 0},
                {0, 0, -1, 0},
                {0, 1, 0, 0},
                {0, 1, 1, 0},
                {0, 1, -1, 0},
                {0, -1, 0, 0},
                {0, -1, 1, 0},
                {0, -1, -1, 0},
                {1, 0, 0, 0},
                {1, 0, 1, 0},
                {1, 0, -1, 0},
                {1, 1, 0, 0},
                {1, 1, 1, 1},
                {1, 1, -1, -1},
                {1, -1, 0, 0},
                {1, -1, 1, -1},
                {1, -1, -1, -1},
                {-1, 0, 0, 0},
                {-1, 0, 1, 0},
                {-1, 0, -1, 0},
                {-1, 1, 0, 0},
                {-1, 1, 1, -1},
                {-1, 1, -1, -1},
                {-1, -1, 0, 0},
                {-1, -1, 1, -1},
                {-1, -1, -1, -1}
        };
        library.library.put(elem.name, elem);
        
        
        scheme.elements.put("1STU", "3_AND");
        scheme.elements.put("1STL", "3_AND");
        scheme.elements.put("2STU", "2_AND");
        scheme.elements.put("2STL", "2_AND");
        scheme.elements.put("3STU", "2_AND");
        scheme.elements.put("3STL", "2_AND");
        scheme.elements.put("4STU", "2_AND");
        scheme.elements.put("4STL", "2_AND");
        
        scheme.inputs.add("J_IN");
        scheme.inputs.add("C_IN");
        scheme.inputs.add("K_IN");
        
        scheme.outputs.add("Q_OUT");
        scheme.outputs.add("NQ_OUT");
        
        Circuit circ;
        circ = new Circuit();
        circ.contacts.add(new Contact("J_IN", -1));
        circ.contacts.add(new Contact("1STU", 1));
        scheme.circuits.put("C_01", circ);
        
        circ = new Circuit();
        circ.contacts.add(new Contact("C_IN", -1));
        circ.contacts.add(new Contact("1STU", 2));
        circ.contacts.add(new Contact("1STL", 0));
        scheme.circuits.put("C_02", circ);
        
        circ = new Circuit();
        circ.contacts.add(new Contact("K_IN", -1));
        circ.contacts.add(new Contact("1STL", 1));
        scheme.circuits.put("C_03", circ);
        
        circ = new Circuit();
        circ.contacts.add(new Contact("1STU", 3));
        circ.contacts.add(new Contact("2STU", 0));
        circ.contacts.add(new Contact("3STU", 0));
        scheme.circuits.put("C_04", circ);
        
        circ = new Circuit();
        circ.contacts.add(new Contact("1STL", 3));
        circ.contacts.add(new Contact("2STL", 1));
        circ.contacts.add(new Contact("3STL", 1));
        scheme.circuits.put("C_05", circ);
        
        circ = new Circuit();
        circ.contacts.add(new Contact("2STU", 2));
        circ.contacts.add(new Contact("3STU", 1));
        circ.contacts.add(new Contact("2STL", 0));
        scheme.circuits.put("C_06", circ);
        
        circ = new Circuit();
        circ.contacts.add(new Contact("2STL", 2));
        circ.contacts.add(new Contact("3STL", 0));
        circ.contacts.add(new Contact("2STU", 1));
        scheme.circuits.put("C_07", circ);
        
        circ = new Circuit();
        circ.contacts.add(new Contact("3STU", 2));
        circ.contacts.add(new Contact("4STU", 0));
        scheme.circuits.put("C_08", circ);
        
        circ = new Circuit();
        circ.contacts.add(new Contact("3STL", 2));
        circ.contacts.add(new Contact("4STL", 1));
        scheme.circuits.put("C_09", circ);
        
        circ = new Circuit();
        circ.contacts.add(new Contact("4STU", 2));
        circ.contacts.add(new Contact("1STL", 2));
        circ.contacts.add(new Contact("4STL", 0));
        circ.contacts.add(new Contact("Q_OUT", -1));
        scheme.circuits.put("C_10", circ);
        
        circ = new Circuit();
        circ.contacts.add(new Contact("4STL", 2));
        circ.contacts.add(new Contact("1STU", 0));
        circ.contacts.add(new Contact("4STU", 1));
        circ.contacts.add(new Contact("NQ_OUT", -1));
        scheme.circuits.put("C_11", circ);
        
        System.out.println(yaml.dump(scheme));
        System.out.println(yaml.dump(library));
        
        Signal sign;
        sign = new Signal();
        sign.signal.put(0000L, 0);
        sign.signal.put(1000L, 1);
        sign.signal.put(2000L, 0);
        signals.signals.put(new Contact("J_IN", -1), sign);
        
        sign = new Signal();
        sign.signal.put((long) 0, 0);
        sign.signal.put((long) 1000, 1);
        sign.signal.put((long) 2000, 0);
        signals.signals.put(new Contact("K_IN", -1), sign);
        
        sign = new Signal();
        sign.signal.put((long) 0, 1);
        sign.signal.put((long) 500, 0);
        sign.signal.put((long) 1000, 1);
        sign.signal.put((long) 1500, 0);
        sign.signal.put((long) 2000, 1);
        sign.signal.put((long) 2500, 0);
        signals.signals.put(new Contact("C_IN", -1), sign);
        
        System.out.println(yaml.dump(signals));
    }
}
