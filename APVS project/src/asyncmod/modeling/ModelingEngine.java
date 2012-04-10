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
}
