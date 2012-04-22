package asyncmod.results_displaying;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

import asyncmod.modeling.Contact;
import asyncmod.modeling.Event;
import asyncmod.modeling.ModelingEngine;
import asyncmod.modeling.Signal;
import asyncmod.ui.MainWindow;

public class ModelingResultsDisplayer {

    private ModelingEngine engine;
    
    public ModelingResultsDisplayer(ModelingEngine engine) {
        this.engine = engine;
    }

    public void updateUITables() {
        String[][] table;
        long time = MainWindow.getCurrentNode();
        int n;

        // signals
        Map<Contact, Signal> signals = engine.getResults().getSignals();
        table = new String[signals.size()][2];
        n = 0;
        for (Contact contact : signals.keySet()) {
            table[n][0] = contact.toString();

            Integer state = signals.get(contact).getState(time);
            table[n][1] = (state == 2) ? "X" : state.toString();
            n++;
        }
        MainWindow.setSignalsTableValues(table);
        
        // active
        Set<String> active = engine.getActive().get(time);
        if(active != null) {
            table = new String[active.size()][1];
            n = 0;
            for(String elementName : active) {
                table[n][0] = elementName;
                n++;
            }

        } else {
            table = new String[][]{{"<none>"}};
        }
        MainWindow.setActiveElementsTableValues(table);
        
        // events
        NavigableMap<Long, List<Event>> events = engine.getEvents().tailMap(time, false);
        List<String[]> temp = new LinkedList<String[]>();
        for(Long evttime : events.keySet()) {
            for(Event event : events.get(evttime)) {
                if(event.getFrom() <= time) {
                    temp.add(new String[] {evttime.toString(), event.getContact().getElement(), 
                            event.getContact().getCnumber().toString(), event.getNewstate() + "", event.getFrom() + ""});
                }
            }
        }
        table = temp.toArray(new String[0][0]);
        MainWindow.setEventsTableValues(table);
    }
    
}
