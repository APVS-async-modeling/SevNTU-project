package asyncmod.ui.timediagrams;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import swing2swt.layout.BorderLayout;
import asyncmod.ui.MainWindow;
import asyncmod.ui.Messages;

public class TimeDiagramsWindow extends Dialog {

    // UI variables
    protected Shell timeDiagramsShell;
    private static int WIDTH = 500;
    private static int HEIGHT = 500;
    private boolean isVisible = false;
    private Tree tree;

    // Non-UI variables
    private String pathToDiagramsLogFile;
    private BufferedReader fr = null;    
    private int LastModelingTime;
    
    private int width;
    private int height = 6000;
    private int divide = 1;  
    
    private Canvas canvas;
    
    Color WHITE = new Color(Display.getDefault(), 255, 255, 255); // белый цвет
    Color LIGHT_GRAY = new Color(Display.getDefault(), 240, 240, 240); // цвет фона временных диаграмм
    Color DIAGRAMS_X_COLOR = new Color(Display.getDefault(), 127, 127, 127); // цвет неопределенных состояний
    Color DIAGRAMS_X_TEST = new Color(Display.getDefault(), 17, 127, 127); // цвет неопределенных состояний
    private Image image;
    private List<String> lines;
    private String [][] parsedLines;
    
    public TimeDiagramsWindow(final Shell parent, final int style, long lastModelingTime, String pathToDiagramsLogFile) {
        super(parent, style);
        this.pathToDiagramsLogFile = pathToDiagramsLogFile;
        this.LastModelingTime = (int)lastModelingTime;
        this.width = LastModelingTime + 100;
    }

    public Object open(final int coordX, final int coordY) {       
        try {
            fr = new BufferedReader(new FileReader(new File(pathToDiagramsLogFile)));
            lines = new ArrayList<String>();
            String line;
            while ((line = fr.readLine()) != null) {
                lines.add(line.replaceAll("\t+", "           "));
            }            
            parsedLines = parseLines(lines);
            
        } catch (FileNotFoundException e) {
            MainWindow.showMessage(Messages.TIME_DIAGRAMS_COULD_NOT_OPEN_LOGFILE + this.pathToDiagramsLogFile, "Error");
            e.printStackTrace();
        } catch (IOException e1) {
            MainWindow.showMessage(Messages.TIME_DIAGRAMS_COULD_NOT_READ_LOGFILE + this.pathToDiagramsLogFile, "Error");
            e1.printStackTrace();
        }

        createContents(coordX, coordY);  
        
        timeDiagramsShell.open();
        isVisible = true;
        MainWindow.status(Messages.TIME_DIAGRAMS_OPEN);
        timeDiagramsShell.layout();
        Display display = getParent().getDisplay();
        while (!timeDiagramsShell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        return null;
    }

    private String[][] parseLines(List<String> lines) {
        int wordsNum = lines.get(0).split("\\s+").length;
        String[][] result = new String [lines.size()][wordsNum];
        int count = 0;
        for(String line: lines) {
            String []words = line.split("\\s+");
            for(int i=0; i<wordsNum; i++){
                result[count][i] = words[i];
            }
            count++;
        }
        return result;
    }

    public void show(int coordX, int coordY) {
        timeDiagramsShell.setBounds(coordX, coordY, WIDTH, HEIGHT);
        isVisible = true;
        timeDiagramsShell.setVisible(isVisible);
        MainWindow.status(Messages.TIME_DIAGRAMS_SHOW);
    }

    public void hide() {
        isVisible = false;
        timeDiagramsShell.setVisible(isVisible);
        MainWindow.status(Messages.TIME_DIAGRAMS_HIDE);
    }

    public boolean isVisible() {
        return isVisible;
    }

    private void createContents(int coordX, int coordY) {
        timeDiagramsShell = new Shell(getParent(), SWT.BORDER | SWT.RESIZE | SWT.TITLE);
        timeDiagramsShell.setSize(563, 498);
        timeDiagramsShell.setMinimumSize(new Point(300, 300));
        timeDiagramsShell.setBounds(coordX, coordY, WIDTH, HEIGHT);
        timeDiagramsShell.setText("Time Diagrams Window");
        timeDiagramsShell.setLayout(new BorderLayout(0, 0));
        
        SashForm sashForm = new SashForm(timeDiagramsShell, SWT.NONE);
        sashForm.setLayoutData(BorderLayout.CENTER);
       
        timeDiagramsShell.addControlListener(new ControlListener() {
            public void controlResized(ControlEvent arg0) {                
                canvas.redraw();
            }
            public void controlMoved(ControlEvent arg0) {
                canvas.redraw();
            }
        });
        
        timeDiagramsShell.addListener(SWT.Traverse, new Listener() {
            public void handleEvent(final Event event) {
                // hide the time diagrams window by Esc key )
                if (event.character == SWT.ESC) {
                    hide();
                    event.doit = false;
                }
            }
        });
        
        timeDiagramsShell.addControlListener(new ControlListener() {
            public void controlResized(final ControlEvent e) {
                WIDTH = timeDiagramsShell.getSize().x;
                HEIGHT = timeDiagramsShell.getSize().y;
            }
            public void controlMoved(final ControlEvent e) { }
        });

        image = new Image(Display.getDefault(), width, height);
        final Point origin = new Point(0, 0);

        canvas = new Canvas(sashForm, SWT.NO_BACKGROUND | SWT.V_SCROLL | SWT.H_SCROLL);
        canvas.setRedraw(true);

        final ScrollBar hBar = canvas.getHorizontalBar();
        hBar.addListener(SWT.Selection, new Listener() {
          public void handleEvent(Event e) {
            int hSelection = hBar.getSelection();
            int destX = -hSelection - origin.x;
            Rectangle rect = image.getBounds();
            canvas.scroll(destX, 0, 0, 0, rect.width, rect.height, false);
            origin.x = -hSelection;
          }
        });
        final ScrollBar vBar = canvas.getVerticalBar();
        vBar.addListener(SWT.Selection, new Listener() {
          public void handleEvent(Event e) {
            int vSelection = vBar.getSelection();
            int destY = -vSelection - origin.y;
            Rectangle rect = image.getBounds();
            canvas.scroll(0, destY, 0, 0, rect.width, rect.height, false);
            origin.y = -vSelection;
          }
        });
        canvas.addListener(SWT.Resize, new Listener() {
          public void handleEvent(Event e) {
            Rectangle rect = image.getBounds();
            Rectangle client = canvas.getClientArea();
            hBar.setMaximum(rect.width);
            vBar.setMaximum(rect.height);
            hBar.setThumb(Math.min(rect.width, client.width));
            vBar.setThumb(Math.min(rect.height, client.height));
            int hPage = rect.width - client.width;
            int vPage = rect.height - client.height;
            int hSelection = hBar.getSelection();
            int vSelection = vBar.getSelection();
            if (hSelection >= hPage) {
              if (hPage <= 0)
                hSelection = 0;
              origin.x = -hSelection;
            }
            if (vSelection >= vPage) {
              if (vPage <= 0)
                vSelection = 0;
              origin.y = -vSelection;
            }
            canvas.redraw();
          }
        });
        canvas.addListener(SWT.Paint, new Listener() {
          public void handleEvent(Event e) {
            GC gc = e.gc;
            redrawTimeDiagrams();
            gc.drawImage(image, origin.x, origin.y);            
          }
        });

        tree = new Tree(sashForm, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION);
        tree.setHeaderVisible(true);
        tree.setLinesVisible(true);

        tree.addSelectionListener(new SelectionListener() {
            private boolean isCurElementExpanded;

            public void widgetDefaultSelected(SelectionEvent event) {
                final TreeItem root = (TreeItem) event.item;
                isCurElementExpanded = !isCurElementExpanded;
                root.setExpanded(isCurElementExpanded);
            }

            public void widgetSelected(SelectionEvent event) {
                if (event.detail == SWT.CHECK) {
                    final TreeItem root = (TreeItem) event.item;
                    final boolean isChecked = root.getChecked();
                    for (TreeItem childItem : root.getItems()) {
                        childItem.setChecked(isChecked);
                    }
                    canvas.redraw();                  
                }
            }
        });

        initializeTree();
        sashForm.setWeights(new int[] { 2, 1 });        
    }

    public void setPosition(int coordX, int coordY) {
        timeDiagramsShell.setBounds(coordX, coordY, WIDTH, HEIGHT);
    }

    private void initializeTree() {
        for (String line : lines) {
            if (line != null) {
                String[] words = line.split("\\s+");
                if (words.length > 0) {
                    String[] elementAndContact = words[0].split("=");
                    if (elementAndContact.length == 2)
                    {
                        String element = elementAndContact[0];
                        String contact = elementAndContact[1];
                        addElement(element, contact);
                    }
                }
            }
        }
    }

    private void addElement(String element, String contact) {
        int indexOfItem = getItemIndexByElementName(element);
        if (indexOfItem == -1) { // add a new element
            TreeItem item = new TreeItem(tree, SWT.NONE);
            item.setText(element);
            if (!"-1".equals(contact))
            {
                TreeItem litem = new TreeItem(item, SWT.NONE);
                litem.setText(contact);
            }
        } else { // add a contact to existing element
            TreeItem litem = new TreeItem(tree.getItem(indexOfItem), SWT.NONE);
            litem.setText(contact);
        }
    }

    private int getItemIndexByElementName(String elementName) {
        int result = -1;
        Item [] items = tree.getItems();
        for(int i = 0; i < items.length; i++) {
            if(items[i].getText().equals(elementName)) {
                return i;
            }
        }
        return result;
    }

    public void redrawTimeDiagrams(boolean redrawCanvas) {
        redrawTimeDiagrams();
        if(redrawCanvas) {
            canvas.redraw();
        }
    }
    
    private void redrawTimeDiagrams() {

        GC gc = new GC(image);
        gc.setBackground(LIGHT_GRAY);

        gc.fillRectangle(0, 0, width, height); // залить слабо серым цветом прямоугольник

        int contactTextDistance = 9; // расстояние между левым краем и подписью контакта
        int distanceBetweenLines = 50; // расстояние между линиями

        int cursorPosition = 35; // начальный сдвиг курсора вниз по оси Y

        for (TreeItem element : getSelectedTreeItems()) { // каждый элемент            
            TreeItem parent = element.getParentItem();
            String contactLabel;

            if (parent == null) {
                contactLabel = element.getText();
            } else {
                contactLabel = parent.getText() + "=" + element.getText();
            }
            
            otrisovkaOdnoiDiagrammy(gc, contactTextDistance, cursorPosition, contactLabel);
            cursorPosition += distanceBetweenLines; // сдвинуть курсор вниз по оси Y к следующей линии
        }
        gc.dispose(); // обязательно сделать это в конце отрисовки!        
    }

    private TreeItem [] getSelectedTreeItems() {
        List <TreeItem> selectedItems = new LinkedList<TreeItem>();
        for (TreeItem element : tree.getItems()) { // каждый элемент
            if (element.getItemCount() == 0) {
                if(element.getChecked()){
                    selectedItems.add(element);
                }
            }
            else {
                for (TreeItem contact : element.getItems()) { // каждый контакт
                    if(contact.getChecked()){
                        selectedItems.add(contact);
                    }
                }
            }
        }
        return selectedItems.toArray(new TreeItem[]{});
    }

    private void otrisovkaOdnoiDiagrammy(GC gc, int contactTextDistance, int arrowStartYPosition, String contactLabel) {   
        int arrowStartXPosition = contactTextDistance + 33;
        gc.drawString(contactLabel, contactTextDistance, arrowStartYPosition); // нарисовать подпись контакта
        strelka(gc, arrowStartXPosition, arrowStartYPosition, width - 15, arrowStartYPosition, 7); // нарисовать линию со стрелочкой для текущего контакта         
        risuemPodpisiPodLiniei(gc, arrowStartXPosition, arrowStartYPosition);
        if(!contactLabel.contains("=")) {
            contactLabel+="=-1";
        }
        drawDigramData(gc, arrowStartXPosition, arrowStartYPosition, contactLabel); // нарисовать сами 0, 1 , Х на диаграмме        
    }

    private void drawDigramData(GC gc, int arrowStartXPosition, int arrowStartYPosition, String contactLabel) {
        String modelingTime = MainWindow.getCurrentNode() + "";    

        List<Integer> points = new ArrayList<Integer>();

        int lowLevelHeight = 4; // 0
        int highLevelHeight = 18; // 1
        int xHeight = 16; // X
        
        int rowIndex = getRowIndex(contactLabel);
                
        // first point
        points.add(arrowStartXPosition); // X
        points.add(arrowStartYPosition - lowLevelHeight); // Y

        int previous = 0;
        int previousDisplacement = -1;
        
        for(int j=0; j<getLastColumnIndex(modelingTime); j++) {
            int data = Integer.parseInt(parsedLines[rowIndex][j+1]);
            int displacement = Integer.parseInt(parsedLines[0][j+1]);
            switch(data) {
            case 0:
                if(previous == 1){ // из 1 в 0
                    // 1
                    points.add(arrowStartXPosition + displacement); // X
                    points.add(arrowStartYPosition - highLevelHeight); // Y
                    // 0
                    points.add(arrowStartXPosition + displacement); // X
                    points.add(arrowStartYPosition - lowLevelHeight); // Y
                } else if(previous == 0){ // из 0 в 0
                    // 0
                    points.add(arrowStartXPosition + displacement); // X
                    points.add(arrowStartYPosition - lowLevelHeight); // Y
                } else { // из X в 0
                    gc.setBackground(DIAGRAMS_X_TEST);
                    int x = arrowStartXPosition + previousDisplacement;
                    int y = arrowStartYPosition - 1 - xHeight;
                    int h = xHeight;
                    int w = displacement - previousDisplacement;
                    gc.fillRectangle(new Rectangle(x, y, w, h));
                    gc.setBackground(LIGHT_GRAY);
                }
                previous = 0;
                break;
            case 1:
                if(previous == 0) { // из 0 в 1
                    //0
                    points.add(arrowStartXPosition + displacement); // X           
                    points.add(arrowStartYPosition - lowLevelHeight); // Y
                    //1
                    points.add(arrowStartXPosition + displacement); // X
                    points.add(arrowStartYPosition - highLevelHeight); // Y
                } else if(previous == 1){ // из 1 в 1
                    //1
                    points.add(arrowStartXPosition + displacement); // X           
                    points.add(arrowStartYPosition - highLevelHeight); // Y
                } else { // из Х в 1
                    // 0
                    points.add(arrowStartXPosition + displacement); // X           
                    points.add(arrowStartYPosition - lowLevelHeight); // Y
                    // 1
                    points.add(arrowStartXPosition + displacement); // X
                    points.add(arrowStartYPosition - highLevelHeight); // Y
                    // X
                    gc.setBackground(DIAGRAMS_X_TEST);
                    int x = arrowStartXPosition + previousDisplacement;
                    int y = arrowStartYPosition - 1 - xHeight;
                    int h = xHeight;
                    int w = displacement - previousDisplacement;
                    gc.fillRectangle(new Rectangle(x, y, w, h));
                    gc.setBackground(LIGHT_GRAY);
                }
                previous = 1;
                break;
            case 2: 
                if(previousDisplacement != -1) {
                    // X
                    gc.setBackground(DIAGRAMS_X_COLOR);
                    int x = arrowStartXPosition + previousDisplacement;
                    int y = arrowStartYPosition - 1 - xHeight;
                    int h = xHeight;
                    int w = displacement - previousDisplacement;
                    gc.fillRectangle(new Rectangle(x, y, w, h));
                    gc.setBackground(LIGHT_GRAY);
                    previous = 2;                    
                }
                previousDisplacement = displacement;
                break;
            }
        }

        Integer[] temp = points.toArray(new Integer[] {});
        int[] pointsToDraw = new int[temp.length];
        for (int i = 0; i < temp.length; i++) {
            pointsToDraw[i] = temp[i];
        }
        gc.drawPolyline(pointsToDraw);

    }

    private int getLastColumnIndex(String modelingTime) {
        int result = 0;
        for (int j = 0; j < parsedLines[0].length; j++) {
            if (modelingTime.equals(parsedLines[0][j])) {
                result = j;
                break;
            }
        }
        return result;
    }

    private int getRowIndex(String contactLabel) {
        int result = 0;
        for (int i = 0; i < parsedLines.length; i++) {
            if(contactLabel.equals(parsedLines[i][0])) {
                result = i;
                break;
            }
        }
        return result;
    }
    
    private void risuemPodpisiPodLiniei(GC gc, int x, int y) {     
        double step = (int) Math.sqrt(LastModelingTime + 0.0);
        for (double i = 0; i <= LastModelingTime; i += step) {                       
            int currentPosition = (int)i; // координата х
            сhertocka(gc, currentPosition + x, y, 2);
            String curPosition = currentPosition + "";
            gc.drawString(curPosition, currentPosition + x - (curPosition.length() * 5 / 2), y + 13);
        }
    }

    /** Отрисовка вертикальной черточки +5 и -5 от заданной центральной точки.*/
    private void сhertocka(GC gc, int x, int y, int size) {
        gc.drawLine(x, y - size, x, y + size);
    }

    private void strelka(GC gc, int x1, int y1, int x2, int y2, int size) {
        gc.drawLine(x1, y1, x2, y2);
        gc.drawLine(x2, y2, x2 - size, y2 - size); // 1-ая половинка стрелочки
        gc.drawLine(x2, y2, x2 - size, y2 + size); // 2-ая половинка стрелочки
        gc.drawLine(x2 - size, y2 - size, x2 - size, y2 + size); // соединяем задние точки стрелки
    }

}
