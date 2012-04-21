package asyncmod.modeling;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.yaml.snakeyaml.Yaml;


public class ModelingEngine implements Runnable {
    private Scheme scheme;
    private SignalBundle signals;
    private SignalBundle results;
    private Library library;
    
    private long timecnt;
    private long endtime;
    private NavigableMap<Long, List<Event>> events;
    private NavigableMap<Long, Set<String>> active;
    
    public boolean correct = false;
    
    String diagrams, logs;
    
    public ModelingEngine(String library, String scheme, String signals, String diagrams, String logs) throws ModelingException {
        Yaml yaml = new Yaml();
        InputStream stream = null;
        this.diagrams = diagrams;
        this.logs = logs;
        
        try {
            stream = new FileInputStream(library);
        } catch (FileNotFoundException e) {
            throw new ModelingException(0x01, library);
        }
        try {
            this.library = (Library) yaml.load(stream);
        } catch(Exception e) {
            throw new ModelingException(0x10);
        }
        
        try {
            stream = new FileInputStream(scheme);
        } catch (FileNotFoundException e) {
            throw new ModelingException(0x01, scheme);
        }
        try {
            this.scheme = (Scheme) yaml.load(stream);
        } catch(Exception e) {
            throw new ModelingException(0x11);
        }
        
        try {
            stream = new FileInputStream(signals);
        } catch (FileNotFoundException e) {
            throw new ModelingException(0x01, signals);
        }
        try {
            this.signals = (SignalBundle) yaml.load(stream);
        } catch(Exception e) {
            throw new ModelingException(0x12);
        }
    }
    
    public void run() {
        try {
            correct = check();
        } catch (ModelingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(correct) {
            try {
                simulate();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    private boolean check() throws ModelingException {
        // checking elements
        for(String elementName : scheme.elements.keySet()) {
            String elementType = scheme.elements.get(elementName);
            Element element = library.library.get(elementType);
            if(element == null) {
                throw new ModelingException(0x20, "'" + elementName + "' [" + elementType + "]");
            }
        }
        
        // checking circuits
        for(String circuitName : scheme.circuits.keySet()) {
            Circuit circuit = scheme.circuits.get(circuitName);
            int outputs = 0;
            for(Contact contact : circuit.contacts) {
                Element element = library.library.get(scheme.elements.get(contact.element));
                if(element == null) {
                    if(scheme.inputs.contains(contact.element)) {
                        outputs++;
                    } else if(!scheme.outputs.contains(contact.element)) {
                        throw new ModelingException(0x32, "Контакт " + contact.toString() + " в цепи " + circuitName);
                    }
                } else {
                    if(element.isOutput(contact.cnumber)) {
                        outputs++;
                    } else if(element.isInternal(contact.cnumber)) {
                        throw new ModelingException(0x31, "Контакт " + contact.toString() + " в цепи " + circuitName);
                    } else if(contact.cnumber >= element.icnt + element.ocnt + element.ecnt) {
                        throw new ModelingException(0x32, "Контакт " + contact.toString() + " в цепи " + circuitName);
                    }
                }
            }
            if(outputs > 1) {
                throw new ModelingException(0x30, circuitName + ": " + circuit.contacts.toString());
            } else if(outputs == 0) {
                throw new ModelingException(0x33, circuitName + ": " + circuit.contacts.toString());
            }
        }
        
        // checking signals
        for(Contact contact : signals.signals.keySet()) {
            if(scheme.inputs.contains(contact.element) || scheme.outputs.contains(contact.element));
            else if(scheme.elements.containsKey(contact.element)) {
                Element element = library.library.get(scheme.elements.get(contact.element));
                if(contact.cnumber >= element.icnt + element.ocnt + element.ecnt) {
                    throw new ModelingException(0x41, contact.toString());
                }
            } else {
                throw new ModelingException(0x40, contact.toString());
            }
        }
        
        // checking library
        for(String elementName : library.library.keySet()) {
            Element element = library.library.get(elementName);
            if(!element.check()) {
                throw new ModelingException(0x50, elementName);
            }
        }
        
        return true;
    }

    public void simulate() throws IOException {
        events = new TreeMap<Long, List<Event>>();
        active = new TreeMap<Long, Set<String>>();
        results = new SignalBundle();
        
        events.put(-1L, null);
        // converting input signals to modeling events
        for (Contact contact : signals.getSignals().keySet()) {
            Signal signal = (Signal) signals.getSignals().get(contact);
            for (Long time : signal.signal.keySet()) {
                int state = signal.getState(time);
                if (events.containsKey(time)) {
                    events.get(time).add(new Event(contact, state, -1));
                } else {
                    events.put(time, new LinkedList<Event>());
                    events.get(time).add(new Event(contact, state, -1));
                }
            }
        }

        // calculating end time for modeling as last event time + double delay of scheme for serial connection of all elements in it
        timecnt = 0;
        long latency = 0;
        for (String elementName : scheme.elements.keySet()) {
            Element element = library.library.get(scheme.elements.get(elementName));
            latency += element.delay;
        }
        endtime = events.lastKey() + latency * 2;
        
        // creating dummy signals for each input/output/internal contact of elements in scheme 
        for (String elementName : scheme.getElements().keySet()) {
            Element element = library.library.get(scheme.elements.get(elementName));
            for (int n = 0; n < element.icnt + element.ocnt + element.ecnt; n++) {
                Signal signal = new Signal();
                signal.signal.put(-1L, 0x02);
                results.signals.put(new Contact(elementName, n), signal);
            }
        }
        for (String inputName : scheme.getInputs()) {
            Signal signal = new Signal();
            signal.signal.put(-1L, 0x002);
            results.signals.put(new Contact(inputName, -1), signal);
        }
        for (String outputName : scheme.getOutputs()) {
            Signal signal = new Signal();
            signal.signal.put(-1L, 0x02);
            results.signals.put(new Contact(outputName, -1), signal);
        }
        
        // buffered output streams for logging and result saving
        BufferedWriter logwriter = new BufferedWriter(new FileWriter(logs, false));
        BufferedWriter diawriter = new BufferedWriter(new FileWriter(diagrams, false));
        
        while(timecnt < endtime) {
            // cetime - closest key from 'events' table, represents closest event to current modeling time
            Long cetime = events.ceilingKey(timecnt);
            if (cetime != null) {
                timecnt = cetime;
            } else {
                timecnt = endtime;
                continue;
            }
            logwriter.write("Modeling at time " + timecnt + "\n");
            
            // processing events at timecnt
            if (events.containsKey(timecnt)) {
                for (Event event : events.get(timecnt)) {
                    Element source = scheme.inputs.contains(event.contact.element) ? null : library.library.get(scheme.elements.get(event.contact.element));
                    
                    Signal sourceSignal = results.signals.get(event.contact);
                    if(sourceSignal.isPredefined(timecnt)) {
                        logwriter.write("  Signal on output '" + event.contact + "' is predefined and cannot be changed\n");
                        continue;
                    } else if(sourceSignal.getState(timecnt) == event.newstate) {
                        logwriter.write("  Signal on output '" + event.contact + "' is remains unchanged\n");
                        continue;
                    } else {
                        sourceSignal.signal.put(timecnt, event.newstate);
                    }
                    
                    if (source != null && (source.isInput(event.contact.cnumber) || source.isInternal(event.contact.cnumber))) {
                        if (source.isInput(event.contact.cnumber)) {
                            logwriter.write("  Input #" + event.contact.cnumber + " of element '" + event.contact.element + "' is changed to " + event.newstate + "\n");
                        } else if (source.isInternal(event.contact.cnumber)) {
                            logwriter.write("  Internal state #" + (event.contact.cnumber - source.icnt - source.ocnt) + " of element '" + event.contact.cnumber + "' is changed to " + event.newstate + "\n");
                        }
                        if (active.containsKey(timecnt)) {
                            active.get(timecnt).add(event.contact.element);
                        } else {
                            active.put(timecnt, new TreeSet<String>());
                            active.get(timecnt).add(event.contact.element);
                        }
                    }
                    else {
                        if (source == null) {
                            logwriter.write("  Scheme input '" + event.contact.element + "' is changed to " + event.newstate + "\n");
                        } else {
                            logwriter.write("  Output #" + (event.contact.cnumber - source.icnt) + " of element '" + event.contact.element + "' is changed to " + event.newstate + "\n");
                        }
                        for (String circuitName : scheme.circuits.keySet()) {
                            Circuit circuit = scheme.circuits.get(circuitName);
                            if (circuit.contacts.contains(event.contact)) {
                                logwriter.write("    Circuit '" + circuitName + "' is changed to " + event.newstate + "\n");
                                logwriter.write("      Contacts involved: " + circuit.getContacts() + "\n");
                                for (Contact destinationContact : circuit.getContacts()) {
                                    Signal destinationSignal = results.signals.get(destinationContact);
                                    if (destinationContact.equals(event.contact)) {
                                        continue;
                                    } else if(destinationSignal.isPredefined(timecnt)) {
                                        logwriter.write("  Signal on input '" + destinationContact + "' is predefined and cannot be changed\n");
                                        continue;
                                    } else if(destinationSignal.getState(timecnt) == event.newstate) {
                                        logwriter.write("        Signal on  '" + destinationContact + "' is remains unchanged\n");
                                        continue;
                                    } else {
                                        destinationSignal.signal.put(timecnt, event.newstate);
                                    }
                                    
                                    if (scheme.outputs.contains(destinationContact.element)) {
                                        logwriter.write("        Scheme output '" + destinationContact.element + "' is changed to " + event.newstate + "\n");
                                    } else {
                                        Element destination = library.library.get(scheme.elements.get(destinationContact.element));
                                        if (destination.isInput(destinationContact.cnumber)) {
                                            logwriter.write("        Input #" + destinationContact.cnumber + " of element '" + destinationContact.element + "' is changed to " + event.newstate + "\n");
                                            if (active.containsKey(timecnt)) {
                                                active.get(timecnt).add(destinationContact.element);
                                            } else {
                                                active.put(timecnt, new TreeSet<String>());
                                                active.get(timecnt).add(destinationContact.element);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } 
                }
            }
            
            // processing active elements at timecnt and creating new events
            if (active.containsKey(timecnt)) {
                for (String elementName : active.get(timecnt)) {
                    // get the number of columns in a ToT for element
                    Element element = library.library.get(scheme.elements.get(elementName));
                    int[] array = new int[element.table[0].length];
                    // forming inputs
                    for (int n = 0; n < element.icnt; n++) {
                        array[n] = results.signals.get(new Contact(elementName, n)).getState(timecnt);
                    }
                    for (int n = 0; n < element.ecnt; n++) {
                        array[element.icnt + n] = results.signals.get(new Contact(elementName, element.icnt + element.ocnt + n)).getState(timecnt);
                    }
                    // calculating outputs and next state of internals
                    element.process(array);
                    // creating new events
                    // if state of outputs isn't changed it still will be added as new event and hadnled at next routine
                    long nexttime = timecnt + element.delay;
                    if (!events.containsKey(nexttime)) {
                        events.put(nexttime, new LinkedList<Event>());
                    }
                    for (int n = 0; n < element.ecnt; n++) {
                        int state = array[element.icnt + element.ecnt + n];
                        Contact contact = new Contact(elementName, element.icnt + element.ocnt + n);
                        events.get(nexttime).add(new Event(contact, state, timecnt));
                    }
                    for (int n = 0; n < element.ocnt; n++) {
                        int state = array[element.icnt + element.ecnt * 2 + n];
                        Contact contact = new Contact(elementName, element.icnt + n);
                        events.get(nexttime).add(new Event(contact, state, timecnt));
                    }
                }
            }
            logwriter.write("Modeling at time " + timecnt + " is over\n\n");
            /*Yaml yaml = new Yaml();
            logwriter.write(yaml.dump(results) + "\n\n");*/
            timecnt += 1;
        }
        
        Long[] nodes = events.keySet().toArray(new Long[0]);
        diawriter.write("TIME");
        for(int n = 0; n < nodes.length; n++) {
            diawriter.write("\t" + nodes[n]);
        }
        diawriter.write("\n");
        
        for(Contact contact : results.signals.keySet())
        {
            Signal signal = results.signals.get(contact);
            diawriter.write(contact.toString() + "");
            for(int n = 0; n < nodes.length; n++) {
                diawriter.write("\t" + signal.getState(nodes[n]));
            }
            diawriter.write("\n");
        }
        
        logwriter.close();
        diawriter.close();
    }

    public Scheme getScheme() {
        return scheme;
    }
    public SignalBundle getResults() {
        return results;
    }
    public Library getLibrary() {
        return library;
    }
    public NavigableMap<Long, List<Event>> getEvents() {
        return events;
    }
    public NavigableMap<Long, Set<String>> getActive() {
        return active;
    }
}
