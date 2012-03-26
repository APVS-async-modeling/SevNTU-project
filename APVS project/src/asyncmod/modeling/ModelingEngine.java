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
    
    private int[] signals;      // ������� � ����� ��
    private int[] internals;    // ��������� ���������
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
            // �������� ��� �������� �� ������� ����� (� ���������� ��������) ��� ����� �������
            if (tmst && stepcnt <= endstep || timecnt <= endtime)
            {
                // TODO: ���������� �������������
                
                // getEvents(time) �������� ��� ������� � ���������� ������ �������, ��� ����� �������������� ������������
                // List<Integer> calcCurciuts() ��������� ������� ���� �����, � ������� ��������� ���������
                // calcContacts() [����� ��������� getContacts(circuit_id)] ��������� ���� ������� ���������
                // createEvents() �������� ��������������� �������
                
                // TODO: ������� ������� ������ �� ������ ���, ��� �������� ����
                
                
                
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
