package asyncmod.modeling;

import java.awt.List;

import asyncmod.du_model.DUModel;

public class ModelingEngine implements Runnable {
    private boolean running;
    private boolean lazy;
    
    private long stepcnt;
    private long timecnt;
    private long endstep;
    private long endtime;
    private boolean tmst;
    private boolean stabile;
    private Object anchor;
    
    private int[] signals;      // сигналы в цепях ДУ
    private int[] internals;    // состояния триггеров
    private DUModel scheme;
    private List events = null;
    
    public ModelingEngine(DUModel model, Object anchor) {
        this.anchor = anchor;
        tmst = false;
        running = true;
        scheme = model;
        signals = new int[scheme.getCircuitCount()];
    }
    public void stop() {
        running = false;
    }
    public void useStep() {
        tmst = true;
    }
    public void useTime() {
        tmst = false;
    }

    // 
    public void run()
    {
        while (running)
        {
            
            // scheme.getInputs()
            // добавить все значения со входных цепей (с фиктивного элемента) как новые события
            if (tmst && stepcnt <= endstep || timecnt <= endtime)
            {
                // TODO: реализация моделирования
                
                // getEvents(time) получить все события с одинаковой меткой времени, они будут обрабатываться обновременно
                // List<Integer> calcCurciuts() получение номеров всех цепей, в которых произошли изменения
                // calcContacts() [через модельный getContacts(circuit_id)] получение всех входных контактов
                // createEvents() создание соответствующих событий
                
                // TODO: сделать удобную модель на основе той, что ссоздаст Олег
                
                
                
            } else
                try {
                    anchor.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
    }

    
    private class Event{
        long time;
        int element;
        int contact;
        int newstate;
    }
}
