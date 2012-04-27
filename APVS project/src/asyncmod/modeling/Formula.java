package asyncmod.modeling;

import java.util.Stack;

public class Formula {
    String[] components;
    
    public Formula(String formula) {
        components = formula.split(" ");
    }
    public int calculate(int[] array) {
        Stack<Integer> stack = new Stack<Integer>();
        for(String component : components) {
            if(component.matches("\\d")) {
                stack.push(array[Integer.parseInt(component)]);
            } else if(component.length() == 1) {
                switch(component.charAt(0)) {
                    case '~': stack.push(not(stack.pop())); break;
                    case '^': stack.push(xor(stack.pop(), stack.pop())); break;
                    case '&': stack.push(and(stack.pop(), stack.pop())); break;
                    case '|': stack.push(or(stack.pop(), stack.pop())); break;
                    case 'x': case 'X': stack.push(2); break;
                    default: return 2;
                }
            } else {
                return 2;
            }
        }
        return stack.pop();
    }
    
    public int not(int arg0) {
        switch(arg0) {
            case 0: return 1;
            case 1: return 0;
            default: return 2;
        }
    }
    
    public int and(int arg0, int arg1) {
        switch(arg0) {
            case 0: return 0;
            case 1: return arg1;
            default: return arg1 == 0 ? 0 : 2;
        }
    }
    
    public int or(int arg0, int arg1) {
        switch(arg0) {
            case 0: return arg1;
            case 1: return 1;
            default: return arg1 == 1 ? 1 : 2;
        }
    }
    
    public int xor(int arg0, int arg1) {
        switch(arg0) {
            case 0: return arg1;
            case 1: return not(arg1);
            default: return 2;
        }
    }
    
    public boolean check(int formula) {
        int stack = 0;
        int max = -1;
        for(String component : components) {
            if(component.matches("\\d")) {
                stack++;
                int contact = Integer.parseInt(component);
                max = contact > max ? contact : max;
            } else if(component.length() == 1) {
                switch(component.charAt(0)) {
                    case '~': break;
                    case '^': stack -= 1; break;
                    case '&': stack -= 1; break;
                    case '|': stack -= 1; break;
                    case 'x': case 'X': stack++; break;
                    default: return false;
                }
            } else {
                return false;
            }
        }
        return formula > max ? stack == 1 : false;
    }
    
    
    public String getComponents() {
        return this.toString();
    }

    public void setComponents(String components) {
        this.components = components.split(" +");
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        for(String component : components) {
            result.append(component).append(' ');
        }
        return result.toString().trim();
    }
}
