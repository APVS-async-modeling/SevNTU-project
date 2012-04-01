package asyncmod.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import swing2swt.layout.BorderLayout;
import asyncmod.about.AboutProgram;
import asyncmod.about.AboutTeam;
import asyncmod.ui.timediagrams.TimeDiagramsWindow;
import org.eclipse.swt.layout.GridData;

public class MainWindow {

    // UI!
    // 

    protected Shell shell;
    protected Display display;
    // sizes
    private static final int WIDTH = 785;
    private static final int HEIGHT = 518;
    // buttons
    private Button initialStateBtn;
    private Button stepBtn;
    private Button fullRunBtn;
    private Button runUntilBtn;
    private Button timeDiagramsBtn;
    // text fields
    private static Text statusPanel;
    private static Text modelingResultsText;
    private static Text errorsText;
    private static Text warningText;
    private static Text fullLogText;
    private TimeDiagramsWindow timeDiagramsWindow;
    // file dialogs
    private FileDialog dlgLibrary;
    private FileDialog dlgDiscreteModel;
    private FileDialog dlgSignals;

    // Non - UI!
    //

    //files
    private File libraryFile;
    private File discreteModelFile;
    private File signalsFile;
    // etc
    private final static Calendar cal = Calendar.getInstance();
    static SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss '('yyyy.MM.dd')'");
    private MenuItem resetMenuItem;
    private MenuItem runUntilMenuItem;
    private MenuItem fullRunMenuItem;
    private MenuItem stepMenuItem;

    /**
     * Launch the application.
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            MainWindow window = new MainWindow();
            window.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Open the window.
     */
    public final void open() {
        display = Display.getDefault();     
        
        createContents();
        configureFileDialogs();
        
        shell.open();
        shell.layout();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    private void configureFileDialogs() {
        dlgDiscreteModel = new FileDialog(shell, SWT.SINGLE);
        dlgLibrary = new FileDialog(shell, SWT.SINGLE);
        dlgSignals = new FileDialog(shell, SWT.SINGLE);
        
        dlgDiscreteModel.setFilterExtensions(new String[] { "*.du;"});
        dlgDiscreteModel.setFilterNames(new String[] { "Discrete Unit files (*.du;)" });
        
        dlgLibrary.setFilterExtensions(new String[] { "*.lb;"});
        dlgLibrary.setFilterNames(new String[] { "Library files (*.lb;)" });
        
        dlgSignals.setFilterExtensions(new String[] { "*.sig;"});
        dlgSignals.setFilterNames(new String[] { "Signals files (*.sig;)" });
    }

    /**
     * Create contents of the window.
     */
    protected final void createContents() {
        shell = new Shell();
        shell.setSize(WIDTH, HEIGHT);
        shell.setText("APVS async modeling project");
        shell.setLayout(new FillLayout(SWT.HORIZONTAL));

        shell.addControlListener(new ControlListener() {
            public void controlResized(final ControlEvent e) {
                updateTimeDiagramsWindowPosition();
            }

            public void controlMoved(final ControlEvent e) {
                updateTimeDiagramsWindowPosition();
            }

            private void updateTimeDiagramsWindowPosition() {
                if (timeDiagramsWindow != null) {
                    int xCoord = getRightUpperCornerPosition().x + Constants.SPACE_BETWEEN_WINDOWS;
                    int yCoord = getRightUpperCornerPosition().y;
                    timeDiagramsWindow.setPosition(xCoord, yCoord);
                }
            }
        });

        Menu menu = new Menu(shell, SWT.BAR);
        shell.setMenuBar(menu);

        MenuItem menuItemFile = new MenuItem(menu, SWT.CASCADE);
        menuItemFile.setText("File");

        Menu menu_1 = new Menu(menuItemFile);
        menuItemFile.setMenu(menu_1);

        MenuItem mntmOpen_1 = new MenuItem(menu_1, SWT.CASCADE);
        mntmOpen_1.setText("Open");

        Menu menu_4 = new Menu(mntmOpen_1);
        mntmOpen_1.setMenu(menu_4);

        MenuItem openLibraryFileMenuItem = new MenuItem(menu_4, SWT.NONE);
        openLibraryFileMenuItem.setText("Open Library file...");

        final MenuItem openDiscreteModelMenuItem = new MenuItem(menu_4, SWT.NONE);
        openDiscreteModelMenuItem.setEnabled(false);
        openDiscreteModelMenuItem.setText("Open Discrete Model file...");

        final MenuItem openSignalsFileMenuItem = new MenuItem(menu_4, SWT.NONE);
        openSignalsFileMenuItem.setEnabled(false);
        openSignalsFileMenuItem.setText("Open Signals file...");
      
        openLibraryFileMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final String libraryFilePath = dlgLibrary.open();
                if (libraryFilePath != null) {
                    libraryFile = new File(libraryFilePath);   
                    openDiscreteModelMenuItem.setEnabled(true);
                    String message = Messages.LIBRARY_FILE_SELECTED + libraryFilePath;
                    status(message);
                } else {
                    status(Messages.LIBRARY_FILE_NOT_SELECTED);
                }
            }
        });
        
        openDiscreteModelMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final String discreteModelFilePath = dlgDiscreteModel.open();
                if (discreteModelFilePath != null) {
                    discreteModelFile = new File(discreteModelFilePath);
                    openSignalsFileMenuItem.setEnabled(true);
                    String message = Messages.DISCRETE_MODEL_FILE_SELECTED + discreteModelFilePath;
                    status(message);                    
                } else {
                    status(Messages.DISCRETE_MODEL_FILE_NOT_SELECTED);
                }
            }
        });
        
        openSignalsFileMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final String signalsFilePath = dlgSignals.open();
                if (signalsFilePath != null) {
                    signalsFile = new File(signalsFilePath);
                    setModelingButtonsAndMenuEnabled(true);
                    String message = Messages.SIGNALS_FILE_SELECTED + signalsFilePath;
                    status(message);
                } else {
                    status(Messages.SIGNALS_FILE_NOT_SELECTED);
                }
            }
        });    
        
        MenuItem mntmSave = new MenuItem(menu_1, SWT.CASCADE);
        mntmSave.setText("Save");

        Menu menu_5 = new Menu(mntmSave);
        mntmSave.setMenu(menu_5);

        MenuItem mntmSaveModelingResults = new MenuItem(menu_5, SWT.NONE);
        mntmSaveModelingResults.setText("Save modeling results...");

        MenuItem mntmClose = new MenuItem(menu_1, SWT.NONE);
        mntmClose.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                shell.close();
            }
        });
        mntmClose.setText("Exit");

        MenuItem menuItemModeling = new MenuItem(menu, SWT.CASCADE);
        menuItemModeling.setText("Modeling");

        Menu menu_3 = new Menu(menuItemModeling);
        menuItemModeling.setMenu(menu_3);

        stepMenuItem = new MenuItem(menu_3, SWT.NONE);
        stepMenuItem.setText("Step");

        fullRunMenuItem = new MenuItem(menu_3, SWT.NONE);
        fullRunMenuItem.setText("Full Run");

        runUntilMenuItem = new MenuItem(menu_3, SWT.NONE);
        runUntilMenuItem.setText("Run Until...");

        resetMenuItem = new MenuItem(menu_3, SWT.NONE);
        resetMenuItem.setText("Reset");

        MenuItem menuItemAbout = new MenuItem(menu, SWT.CASCADE);
        menuItemAbout.setText("About");

        Menu menu_2 = new Menu(menuItemAbout);
        menuItemAbout.setMenu(menu_2);

        MenuItem mntmNewItem = new MenuItem(menu_2, SWT.NONE);
        mntmNewItem.setText("Program");
        mntmNewItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                showMessage(AboutProgram.Text, "Information");
            }
        });

        MenuItem mntmTeam = new MenuItem(menu_2, SWT.NONE);
        mntmTeam.setText("Team");
        mntmTeam.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                showMessage(AboutTeam.Text, "Information");
            }
        });

        TabFolder mainWindowTabFolder = new TabFolder(shell, SWT.NONE);

        TabItem MainWindowTab = new TabItem(mainWindowTabFolder, SWT.NONE);
        MainWindowTab.setText("Main Window");

        Composite mainComposite = new Composite(mainWindowTabFolder, SWT.NONE);
        MainWindowTab.setControl(mainComposite);
        mainComposite.setLayout(new BorderLayout(0, 0));

        statusPanel = new Text(mainComposite, SWT.BORDER);
        statusPanel.setToolTipText("See \"log\" for more details");
        statusPanel.setEditable(false);
        statusPanel.setText("Status panel");
        statusPanel.setLayoutData(BorderLayout.SOUTH);

        Composite ControlButtonsComposite = new Composite(mainComposite,
                SWT.BORDER);
        ControlButtonsComposite.setLayoutData(BorderLayout.NORTH);
        RowLayout rl_ControlButtonsComposite = new RowLayout(SWT.HORIZONTAL);
        rl_ControlButtonsComposite.marginLeft = 10;
        rl_ControlButtonsComposite.spacing = 10;
        rl_ControlButtonsComposite.pack = false;
        rl_ControlButtonsComposite.fill = true;
        ControlButtonsComposite.setLayout(rl_ControlButtonsComposite);

        initialStateBtn = new Button(ControlButtonsComposite, SWT.NONE);
        initialStateBtn.setText("Initial State");

        stepBtn = new Button(ControlButtonsComposite, SWT.NONE);
        stepBtn.setText("Step");

        fullRunBtn = new Button(ControlButtonsComposite, SWT.NONE);
        fullRunBtn.setText("Full Run");

        runUntilBtn = new Button(ControlButtonsComposite, SWT.NONE);
        runUntilBtn.setText("Run Until...");

        timeDiagramsBtn = new Button(ControlButtonsComposite, SWT.NONE);
        timeDiagramsBtn.setText("Time diagrams...");
        timeDiagramsBtn.setEnabled(false);
        timeDiagramsBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final int coordX = getRightUpperCornerPosition().x + Constants.SPACE_BETWEEN_WINDOWS;
                final int coordY = getRightUpperCornerPosition().y;
                if (timeDiagramsWindow == null) {
                    // singleton instance of TimeDiagram
                    timeDiagramsWindow = new TimeDiagramsWindow(shell, SWT.NONE);
                    timeDiagramsWindow.open(coordX, coordY);
                } else {
                    if (timeDiagramsWindow.isVisible()) {
                        timeDiagramsWindow.hide();
                    } else {
                        timeDiagramsWindow.show(coordX, coordY);
                    }
                }
            }
        });

        SashForm sashForm = new SashForm(mainComposite, SWT.NONE);
        sashForm.setLayoutData(BorderLayout.CENTER);

        Composite elementsComposite = new Composite(sashForm, SWT.BORDER);
        elementsComposite.setLayout(new FillLayout(SWT.HORIZONTAL));

        ExpandBar bar = new ExpandBar(elementsComposite, SWT.BORDER | SWT.V_SCROLL);

        // ExpandBar customization
        Composite composite = new Composite(bar, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));
        Text textField = new Text(composite, SWT.WRAP | SWT.CENTER | SWT.MULTI);
        textField.setEditable(false);
        textField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        textField.setText("Element 1 description");
        ExpandItem item1 = new ExpandItem(bar, SWT.NONE, 0);
        item1.setExpanded(true);
        item1.setText("Element 1");
        item1.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
        item1.setControl(composite);

        bar.setSpacing(6);

        Composite modelingStateComposite = new Composite(sashForm, SWT.NONE);
        sashForm.setWeights(new int[] { 1, 3 });

        TabItem LogTab = new TabItem(mainWindowTabFolder, SWT.NONE);
        LogTab.setText("Additional");

        Composite logComposite = new Composite(mainWindowTabFolder, SWT.NONE);
        LogTab.setControl(logComposite);
        logComposite.setLayout(new BorderLayout(0, 0));

        TabFolder logTabFolder = new TabFolder(logComposite, SWT.BOTTOM);
        logTabFolder.setLayoutData(BorderLayout.CENTER);

        TabItem modelingResultsTab = new TabItem(logTabFolder, SWT.NONE);
        modelingResultsTab.setText("Modeling results");

        Composite modelingResultsComposite = new Composite(logTabFolder,
                SWT.NONE);
        modelingResultsTab.setControl(modelingResultsComposite);
        modelingResultsComposite.setLayout(new FillLayout(SWT.HORIZONTAL));

        modelingResultsText = new Text(modelingResultsComposite, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.MULTI);
        modelingResultsText.setEditable(false);

        TabItem errorsTab = new TabItem(logTabFolder, SWT.NONE);
        errorsTab.setText("Errors");

        Composite errorsComposite = new Composite(logTabFolder, SWT.NONE);
        errorsTab.setControl(errorsComposite);
        errorsComposite.setLayout(new FillLayout(SWT.HORIZONTAL));

        errorsText = new Text(errorsComposite, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.MULTI);
        errorsText.setEditable(false);

        TabItem warningsTab = new TabItem(logTabFolder, SWT.NONE);
        warningsTab.setText("Warnings");

        Composite warningsComposite = new Composite(logTabFolder, SWT.NONE);
        warningsTab.setControl(warningsComposite);
        warningsComposite.setLayout(new FillLayout(SWT.HORIZONTAL));

        warningText = new Text(warningsComposite, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.MULTI);
        warningText.setEditable(false);
        
        TabItem logsTab = new TabItem(logTabFolder, SWT.NONE);
        logsTab.setText("Logs");
        
        Composite fullLogComposite = new Composite(logTabFolder, SWT.NONE);
        logsTab.setControl(fullLogComposite);
        fullLogComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
        
        fullLogText = new Text(fullLogComposite, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.MULTI);
        fullLogText.setEditable(false);
        
        setModelingButtonsAndMenuEnabled(false);
    }

    public final Point getRightUpperCornerPosition() {
        int coordX = shell.getBounds().x + shell.getBounds().width;
        int coordY = shell.getBounds().y;
        Point point = new Point(coordX, coordY);
        return point;
    }
    
    private void setModelingButtonsAndMenuEnabled(boolean state) {
        // buttons:
        initialStateBtn.setEnabled(state);
        stepBtn.setEnabled(state);
        fullRunBtn.setEnabled(state);
        runUntilBtn.setEnabled(state);
        timeDiagramsBtn.setEnabled(state);
        
        // menus:
        stepMenuItem.setEnabled(state);
        fullRunMenuItem.setEnabled(state);
        runUntilMenuItem.setEnabled(state);
        resetMenuItem.setEnabled(state);
    }
    
    public void showMessage(final String text, final String type) {

        int msgWindowType = 0;

        if (type.equals("Alert")) {
            msgWindowType |= SWT.ICON_WARNING;
            addError(text);
        }
        if (type.equals("Information")) {
            msgWindowType |= SWT.ICON_INFORMATION;
            addToLog(text);            
        }

        MessageBox box = new MessageBox(shell, msgWindowType);
        box.setMessage(text);
        box.open();
    }
       
    public static void status(String str){
        statusPanel.setText(str);
        addToLog("[Status] " + str);
    }

    public static void addWarning(String str) {           
        warningText.append(dateFormatter.format(cal.getTime())+ ": "+str + "\n");
        appendLineDelimiter(warningText);
        addToLog("[Warning] " + str);
    }

    public static void addError(String str) {           
        errorsText.append(dateFormatter.format(cal.getTime())+ ": "+str + "\n");
        appendLineDelimiter(errorsText);
        addToLog("[Error] " + str);
    }

    public static void addToLog(String str) {     
        fullLogText.append(dateFormatter.format(cal.getTime())+ ": "+str + "\n");
        appendLineDelimiter(fullLogText);        
    }

    private static void appendLineDelimiter(Text textField){
//        for(int i=0; i<150; i++) {
//            textField.append(UI.LINE_DELIMITER_SYMBOL);
//        }
//        textField.append("\n");
    }
}