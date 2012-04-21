package asyncmod.modeling;

import java.util.HashMap;
import java.util.Map;

import asyncmod.ui.MainWindow;

public class ModelingException extends Exception {

    private static final long serialVersionUID = -243739276529322262L;
    private static final Map<Integer, String> messages;
    
    static {
        messages = new HashMap<Integer, String>();
        messages.put(0x01, "Указанный файл не найден.");
        messages.put(0x10, "Неверный документ библиотеки.");
        messages.put(0x11, "Неверный документ схемы.");
        messages.put(0x12, "Неверный документ тестов.");
        messages.put(0x20, "Для указанного элемента не найдено определение в библиотеке элементов.");
        messages.put(0x30, "Ошибка в цепях схемы, более одного выходного контакта элемента в цепи.");
        messages.put(0x31, "Ошибка в цепях схемы, прямое обращение к внутренней переменной элемента.");
        messages.put(0x32, "Ошибка в цепях схемы, обращение к несуществующему контакту.");
        messages.put(0x33, "Ошибка в цепях схемы, цепь объединяет только входные контакты.");
        messages.put(0x40, "Ошибка в сигналах, такого элемента не существует.");
        messages.put(0x41, "Ошибка в сигналах, такого контакта не существует.");
        messages.put(0x50, "Ошибка в библиотечном определении элемента.");
    }

    public ModelingException(int ID, String text) {
        super(messages.get(ID) + " :: " + text);
        MainWindow.showMessage(messages.get(ID), "Warning");
    }

    public ModelingException(int ID) {
        super(messages.get(ID));
    }
}
