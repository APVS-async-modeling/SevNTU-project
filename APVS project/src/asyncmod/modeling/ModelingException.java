package asyncmod.modeling;

import java.util.HashMap;
import java.util.Map;

public class ModelingException extends Exception {

    private static final long serialVersionUID = -243739276529322262L;
    public static final Map<Integer, String> messages;
    
    static {
        messages = new HashMap<Integer, String>();
        messages.put(0x01, "Указанный файл не найден");
        messages.put(0x10, "Неверный документ библиотеки");
        messages.put(0x11, "Неверный документ схемы");
        messages.put(0x12, "Неверный документ тестов");
        messages.put(0x20, "Для указанного элемента не найдено определение в библиотеке элементов");
        messages.put(0x30, "Ошибка в цепях схемы, более одного выходного контакта элемента в цепи");
        messages.put(0x31, "Ошибка в цепях схемы, прямое обращение к внутренней переменной элемента");
        messages.put(0x32, "Ошибка в цепях схемы, обращение к несуществующему контакту");
    }
    
    
    public ModelingException(int ID, String text) {
        super(messages.get(ID) + " :: " + text);
    }
    
    public ModelingException(int ID) {
        super(messages.get(ID));
    }
}
