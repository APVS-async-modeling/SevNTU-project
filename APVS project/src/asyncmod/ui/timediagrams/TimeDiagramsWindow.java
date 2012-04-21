package asyncmod.ui.timediagrams;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import swing2swt.layout.BorderLayout;
import asyncmod.ui.MainWindow;
import asyncmod.ui.Messages;
import org.eclipse.swt.graphics.Point;

public class TimeDiagramsWindow extends Dialog {

    protected Shell timeDiagramsShell;

    private static int WIDTH = 500;
    private static int HEIGHT = 500;
    private boolean isVisible = false;
    private BufferedReader fr = null;

    private Tree tree;

    private List<String> lines;

    private Label label;

    /**
     * Create the dialog.
     * 
     * @param parent
     * @param style
     */
    public TimeDiagramsWindow(final Shell parent, final int style) {
        super(parent, style);
    }

    /**
     * * Open the dialog.
     * 
     * @return the result
     */
    public Object open(final int coordX, final int coordY) {

        try {
            fr = new BufferedReader(new FileReader(new File("test-diagrams.log")));
            lines = new ArrayList<String>();
            String line;

            while ((line = fr.readLine()) != null) {
                lines.add(line);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        createContents(coordX, coordY);
        timeDiagramsShell.addListener(SWT.Traverse, new Listener() {
            public void handleEvent(final Event event) {
                // avoid to close time diagrams window by Esc key )
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

            public void controlMoved(final ControlEvent e) {

            }
        });

        timeDiagramsShell.open();
        MainWindow.status(Messages.TIME_DIAGRAMS_OPEN);
        timeDiagramsShell.layout();
        isVisible = true;
        Display display = getParent().getDisplay();
        while (!timeDiagramsShell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        return null;
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

    /**
     * Create contents of the dialog.
     */
    private void createContents(int coordX, int coordY) {
        timeDiagramsShell = new Shell(getParent(), SWT.BORDER | SWT.RESIZE | SWT.TITLE);
        timeDiagramsShell.setMinimumSize(new Point(300, 300));
        timeDiagramsShell.setBounds(coordX, coordY, WIDTH, HEIGHT);
        timeDiagramsShell.setText("Time Diagrams Window");
        timeDiagramsShell.setLayout(new BorderLayout(0, 0));
        
        SashForm sashForm = new SashForm(timeDiagramsShell, SWT.NONE);
        sashForm.setLayoutData(BorderLayout.CENTER);
        
        ScrolledComposite scrolledComposite = new ScrolledComposite(sashForm, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);
        
        label = new Label(scrolledComposite, SWT.NONE);
        scrolledComposite.setContent(label);
        scrolledComposite.setMinSize(label.computeSize(SWT.DEFAULT, SWT.DEFAULT));
               
        label.addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent event) {            
                GC gc = event.gc;
                gc.drawText("Time diagrams will be here =) Field size: "+ event.height + " x " +event.width, 50, 190);
                gc.dispose();
            }
        });

        timeDiagramsShell.addControlListener(new ControlListener() {

            public void controlResized(ControlEvent arg0) {
                label.redraw();
            }

            public void controlMoved(ControlEvent arg0) {
                label.redraw();
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
                    repaintTimeDiagrams();
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
        if(indexOfItem == -1) { // add a new element
            TreeItem item = new TreeItem(tree, SWT.NONE);
            item.setText(element);
        } else { // add a contact to existing element
            TreeItem litem = new TreeItem(tree.getItem(indexOfItem), SWT.NONE);
            litem.setText(contact);
        }
    }

    private int getItemIndexByElementName(String elementName) {        
        int result = -1;
        Item [] items = tree.getItems();  
        for(int i=0; i< items.length; i++) {
            if(items[i].getText().equals(elementName)) {
                return i;
            }
        }        
        return result;
    }
    
    private void repaintTimeDiagrams() {
        Image image = new Image(Display.getDefault(), timeDiagramsShell.getBounds().width, timeDiagramsShell.getBounds().height);
        GC gc = new GC(image);
        gc.drawLine(0, 0, 150, 150);
        
        label.setImage(image);
    }
    
}
