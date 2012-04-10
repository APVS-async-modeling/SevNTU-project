package asyncmod.modeling;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Scheme {
    
    protected Map<String, String> elements;
    protected Map<String, Circuit> circuits;
    protected List<String> inputs;
    protected List<String> outputs;
    
    public Scheme() {
        elements = new LinkedHashMap<String, String>();
        circuits = new LinkedHashMap<String, Circuit>();
        inputs = new LinkedList<String>();
        outputs = new LinkedList<String>();
        
    }
    
    public Map<String, String> getElements() {
        return elements;
    }
    public void setElements(Map<String, String> elements) {
        this.elements = elements;
    }
    public Map<String, Circuit> getCircuits() {
        return circuits;
    }
    public void setCircuits(Map<String, Circuit> circuits) {
        this.circuits = circuits;
    }
    public List<String> getInputs() {
        return inputs;
    }
    public void setInputs(List<String> inputs) {
        this.inputs = inputs;
    }
    public List<String> getOutputs() {
        return outputs;
    }
    public void setOutputs(List<String> outputs) {
        this.outputs = outputs;
    }
    
    
}

