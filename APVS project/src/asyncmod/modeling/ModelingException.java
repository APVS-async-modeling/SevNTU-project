package asyncmod.modeling;

import java.util.HashMap;
import java.util.Map;

import asyncmod.ui.MainWindow;

public class ModelingException extends Exception {

    private static final long serialVersionUID = -243739276529322262L;
    private static final Map<Integer, String> messages;
    
    static {
        messages = new HashMap<Integer, String>();
        messages.put(0x11, "Empty paramether of library.");
        messages.put(0x12, "Empty paramether of scheme.");
        messages.put(0x13, "Empty paramether of signals.");
        messages.put(0x20, "Error in scheme: no definition for specified element is found in library.");
        messages.put(0x30, "Error in circuits: more than one source in circuit.");
        messages.put(0x31, "Error in circuits: direct access to internal memory of element.");
        messages.put(0x32, "Error in circuits: reference to non-existing contact.");
        messages.put(0x33, "Error in circuits: circuit only contains drain contacts.");
        messages.put(0x40, "Error in signals: no such element.");
        messages.put(0x41, "Error is signals: no such contact.");
        messages.put(0x50, "Error in library: wrong definition.");
    }

    public ModelingException(int ID, String text) {
        super(messages.get(ID) + " :: " + text);
        MainWindow.showMessage(messages.get(ID), "Warning");
    }

    public ModelingException(int ID) {
        super(messages.get(ID));
    }
}
