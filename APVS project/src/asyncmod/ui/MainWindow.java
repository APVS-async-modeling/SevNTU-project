package asyncmod.ui;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import swing2swt.layout.BorderLayout;
import asyncmod.about.AboutProgram;
import asyncmod.about.AboutTeam;
import asyncmod.modeling.Contact;
import asyncmod.modeling.Event;
import asyncmod.modeling.ModelingEngine;
import asyncmod.modeling.ModelingException;
import asyncmod.modeling.Signal;
import asyncmod.ui.timediagrams.TimeDiagramsWindow;

/**
 * The Class MainWindow.
 */
public class MainWindow {

    // UI fields!
    //

    protected Shell shell;    
    protected Display display;
    // sizes
    private static final int WIDTH = 750;
    private static final int HEIGHT = 518;

    private TimeDiagramsWindow timeDiagramsWindow;

    // buttons
    private Button initResetBtn;    
    private Button stepFwdBtn;
    private Button stepBwdBtn;
    private Button gotoTimeBtn;
    private Button timeDiagramsBtn;

    // text fields
    private static Text statusPanel;
    private static Text modelingResultsText;
    private static Text errorsText;
    private static Text warningText;
    private static Text fullLogText;
    private static Text modelingTimeText;

    //tables
    private static Table tableActiveElements;
    private static Table tableEvents;
    private static Table tableSignals;

    // menu items
    private MenuItem initResetMenuItem;
    private MenuItem stepFwdMenuItem;
    private MenuItem stepBwdMenuItem;
    private MenuItem gotoTimeMenuItem;

    // file dialogs
    private FileDialog dlgLibrary;
    private FileDialog dlgDiscreteModel;
    private FileDialog dlgSignals;    
    private FileDialog dlgResultsSaving;

    // Non-UI fields!
    //
    
    // modeling engine and index to browse thru results
    private ModelingEngine engine;
    private int index;
    private Long[] nodes;

    //files
    private static File libraryFile;
    private static File discreteModelFile;
    private static File signalsFile;

    // etc
    private final static Calendar cal = Calendar.getInstance();
    static SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss '('yyyy.MM.dd')'");
      
    
    /**
     * Launch the application.
     *
     * @param args the arguments
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
        dlgDiscreteModel = new FileDialog(shell, SWT.OPEN | SWT.SINGLE);
        dlgLibrary = new FileDialog(shell, SWT.OPEN | SWT.SINGLE);
        dlgSignals = new FileDialog(shell, SWT.OPEN| SWT.SINGLE);
        dlgResultsSaving = new FileDialog(shell, SWT.SAVE | SWT.SINGLE);
        
        dlgDiscreteModel.setFilterExtensions(new String[] { "*.du;"});
        dlgDiscreteModel.setFilterNames(new String[] { "Discrete Unit files (*.du;)" });
        
        dlgLibrary.setFilterExtensions(new String[] { "*.lb;"});
        dlgLibrary.setFilterNames(new String[] { "Library files (*.lb;)" });
        
        dlgSignals.setFilterExtensions(new String[] { "*.sig;"});
        dlgSignals.setFilterNames(new String[] { "Signals files (*.sig;)" });
        
        dlgResultsSaving.setFilterExtensions(new String[] { "*.res;"});
        dlgResultsSaving.setFilterNames(new String[] { "Modeling results file (*.res;)" });
    }

    protected final void createContents() {
        shell = new Shell();
        shell.setMinimumSize(new Point(500, 390));
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
//                    DUModelController controller = new DUModelController(discreteModelFilePath);
//                    try {
//                        DUModel model = controller.parseDUModelFromFile();
//                    } catch (IOException exc) {
//                        showMessage(exc.getMessage(), "Warning");
//                    }
                    // TODO: show info about DU model in UI!

                    openSignalsFileMenuItem.setEnabled(true);

                    final String message = Messages.DISCRETE_MODEL_FILE_SELECTED + discreteModelFilePath;
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
                    initResetBtn.setEnabled(true);
                    initResetMenuItem.setEnabled(true);
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
        mntmSaveModelingResults.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String fileToResultsSavingPath = dlgResultsSaving.open();
                if(fileToResultsSavingPath != null) {
                    status(Messages.RESULTS_SAVED);
                } else {
                    status(Messages.RESULTS_NOT_SAVED);
                }
            }
        });
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

        initResetMenuItem = new MenuItem(menu_3, SWT.NONE);
        initResetMenuItem.setText("Init/Reset");

        gotoTimeMenuItem = new MenuItem(menu_3, SWT.NONE);
        gotoTimeMenuItem.setText("Goto time...");

        stepFwdMenuItem = new MenuItem(menu_3, SWT.NONE);
        stepFwdMenuItem.setText("Step Forward");

        stepBwdMenuItem = new MenuItem(menu_3, SWT.NONE);
        stepBwdMenuItem.setText("Step Backward");

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

        initResetBtn = new Button(ControlButtonsComposite, SWT.NONE);
        initResetBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                //TODO: do this!
                initReset();
            }
        });
        initResetBtn.setText("Init/Reset");

        stepFwdBtn = new Button(ControlButtonsComposite, SWT.NONE);
        stepFwdBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                //TODO: do this!
                stepFwd();
            }
        });
        stepFwdBtn.setText("Step Forward");

        stepBwdBtn = new Button(ControlButtonsComposite, SWT.NONE);
        stepBwdBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                //TODO: do this!
                stepBwd();
            }
        });
        stepBwdBtn.setText("Step Backward");

        gotoTimeBtn = new Button(ControlButtonsComposite, SWT.NONE);
        gotoTimeBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                //TODO: Create new dialog window, where ask about necessary time
                gotoTime(0);
            }
        });
        gotoTimeBtn.setText("Goto time");

        timeDiagramsBtn = new Button(ControlButtonsComposite, SWT.NONE);
        timeDiagramsBtn.setText("Time diagrams...");
        
        Label lblModelingTime = new Label(ControlButtonsComposite, SWT.NONE);
        lblModelingTime.setEnabled(false);
        lblModelingTime.setAlignment(SWT.CENTER);
        lblModelingTime.setText("Modeling Time:");
        
        modelingTimeText = new Text(ControlButtonsComposite, SWT.BORDER);
        modelingTimeText.setEditable(false);
        modelingTimeText.setToolTipText(Messages.TOOLTIP_MODELING_TIME);
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

        bar.setSpacing(5);

        Composite modelingStateComposite = new Composite(sashForm, SWT.BORDER);
        modelingStateComposite.setLayout(new GridLayout(3, false));
        
        Group grpActiveElementsList = new Group(modelingStateComposite, SWT.NONE);
        grpActiveElementsList.setToolTipText(Messages.TOOLTIP_ACTIVE_ELEMENTS_TABLE);
        GridData gd_grpActiveElementsList = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gd_grpActiveElementsList.widthHint = 134;
        gd_grpActiveElementsList.heightHint = 375;
        grpActiveElementsList.setLayoutData(gd_grpActiveElementsList);
        grpActiveElementsList.setText("Active elements");
        grpActiveElementsList.setLayout(new FillLayout(SWT.HORIZONTAL));
        
        tableActiveElements = new Table(grpActiveElementsList, SWT.BORDER | SWT.FULL_SELECTION | SWT.FILL);
        tableActiveElements.setToolTipText(Messages.TOOLTIP_ACTIVE_ELEMENTS_TABLE);
        tableActiveElements.setLinesVisible(true);
        tableActiveElements.setHeaderVisible(true);
        
        TableColumn tblclmnNewColumn = new TableColumn(tableActiveElements, SWT.CENTER);
        tblclmnNewColumn.setWidth(76);
        tblclmnNewColumn.setText("Element num");
        
        Group grpActionsTable = new Group(modelingStateComposite, SWT.NONE);
        grpActionsTable.setText("Events");
        grpActionsTable.setToolTipText(Messages.TOOLTIP_EVENTS_TABLE);
        GridData gd_grpActionsTable = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gd_grpActionsTable.heightHint = 375;
        gd_grpActionsTable.widthHint = 302;
        grpActionsTable.setLayoutData(gd_grpActionsTable);
        grpActionsTable.setLayout(new FillLayout(SWT.HORIZONTAL));
                
        tableEvents = new Table(grpActionsTable, SWT.BORDER | SWT.FULL_SELECTION);
        tableEvents.setToolTipText(Messages.TOOLTIP_EVENTS_TABLE);
        tableEvents.setLinesVisible(true);
        tableEvents.setHeaderVisible(true);
        
        TableColumn tblclmnTime = new TableColumn(tableEvents, SWT.CENTER);
        tblclmnTime.setWidth(89);
        tblclmnTime.setText("Time");
        
        TableColumn tblclmnElement = new TableColumn(tableEvents, SWT.CENTER);
        tblclmnElement.setWidth(82);
        tblclmnElement.setText("Element");
        
        TableColumn tblclmnContact = new TableColumn(tableEvents, SWT.CENTER);
        tblclmnContact.setWidth(82);
        tblclmnContact.setText("Contact");
        
        TableColumn tblclmnState = new TableColumn(tableEvents, SWT.CENTER);
        tblclmnState.setWidth(82);
        tblclmnState.setText("New state");
        
        TableColumn tblclmnFrom = new TableColumn(tableEvents, SWT.CENTER);
        tblclmnFrom.setWidth(82);
        tblclmnFrom.setText("Created at");
        
        Group grpSignalsMatrix = new Group(modelingStateComposite, SWT.NONE);
        grpSignalsMatrix.setToolTipText(Messages.TOOLTIP_SIGNALS_MATRIX);
        GridData gd_grpSignalsMatrix = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gd_grpSignalsMatrix.heightHint = 375;
        gd_grpSignalsMatrix.widthHint = 170;
        grpSignalsMatrix.setLayoutData(gd_grpSignalsMatrix);
        grpSignalsMatrix.setText("Signals");
        grpSignalsMatrix.setLayout(new FillLayout(SWT.HORIZONTAL));
                        
        tableSignals = new Table(grpSignalsMatrix, SWT.BORDER | SWT.FULL_SELECTION);
        tableSignals.setToolTipText(Messages.TOOLTIP_SIGNALS_MATRIX);
        tableSignals.setLinesVisible(true);
        tableSignals.setHeaderVisible(true);       
        
        TableColumn tblclmnCurcuit = new TableColumn(tableSignals, SWT.CENTER);
        tblclmnCurcuit.setWidth(63);
        tblclmnCurcuit.setText("Contact");
        
        TableColumn tblclmnSignal = new TableColumn(tableSignals, SWT.CENTER);
        tblclmnSignal.setWidth(57);
        tblclmnSignal.setText("Signal");
        
        
        sashForm.setWeights(new int[] { 1, 4 });
        
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

    private final Point getRightUpperCornerPosition() {
        int coordX = shell.getBounds().x + shell.getBounds().width;
        int coordY = shell.getBounds().y;
        Point point = new Point(coordX, coordY);
        return point;
    }
    
    private void setModelingButtonsAndMenuEnabled(boolean state) {
        // buttons:
        initResetBtn.setEnabled(state);
        stepFwdBtn.setEnabled(state);
        stepBwdBtn.setEnabled(state);
        gotoTimeBtn.setEnabled(state);
        // TODO: block timeDiagramsBtn with another buttons when done
        timeDiagramsBtn.setEnabled(true);
        
        // menus:
        initResetMenuItem.setEnabled(state);
        stepFwdMenuItem.setEnabled(state);
        stepBwdMenuItem.setEnabled(state);
        gotoTimeMenuItem.setEnabled(state);
    }
    
    /**
     * Shows the message window.
     * 
     * @param text the text
     * @param type the type of message ("Warning" | "Information").
     */
    public static void showMessage(final String text, final String type) {
        int msgWindowType = 0;
        if (type.equals("Warning")) {
            msgWindowType |= SWT.ICON_WARNING;
            addError(text);
        }
        if (type.equals("Information")) {
            msgWindowType |= SWT.ICON_INFORMATION;
            addToLog(text);
        }
        MessageBox box = new MessageBox(Display.getDefault().getShells()[0], msgWindowType);
        box.setMessage(text);
        box.open();
    }

    /**
     * Sets the status panel text.
     *
     * @param str the input string to set as status.
     */
    public static void status(String str) {
        statusPanel.setText(str);
        addToLog("[Status] " + str);
    }

    /**
     * Adds the warning to the "Warnings" text field.
     *
     * @param str the input string.
     */
    public static void addWarning(String str) {           
        warningText.append(dateFormatter.format(cal.getTime())+ ": "+str + "\n");
        appendLineDelimiter(warningText);
        addToLog("[Warning] " + str);
    }

    /**
     * Adds the input string to the "Errors" textField.
     *
     * @param str the input string.
     */
    public static void addError(String str) {           
        errorsText.append(dateFormatter.format(cal.getTime())+ ": "+str + "\n");
        appendLineDelimiter(errorsText);
        addToLog("[Error] " + str);
    }

    /**
     * Adds the input string to the "Logs" textField.
     *
     * @param str the input string.
     */
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

    public static File getDiscreteModelFile() {
        return discreteModelFile;
    }

    public static File getSignalsFile() {
        return signalsFile;
    }

    public static File getLibraryFile() {
        return libraryFile;
    }
    
    /**
     * Sets the modeling time to the text field.
     * @param modelingTime - new modeling time value.
     */
    public static void setModelingTime(long modelingTime) {
        modelingTimeText.setText(modelingTime + "");
    }
    
    /**
     * Sets the active elements table values.
     *
     * @param values the new active elements table values
     */
    public static void setActiveElementsTableValues(String [][] values) {       
        setTableValues(tableActiveElements, values);
    }

    /**
     * Sets the actions table values.
     *
     * @param values the new actions table values
     */
    public static void setEventsTableValues(String [][] values) {
        setTableValues(tableEvents, values);    
    }

    /**
     * Sets the signals table values.
     *
     * @param values the new signals table values
     */
    public static void setSignalsTableValues(String [][] values) {
        setTableValues(tableSignals, values);
    }

    /**
     * Sets the table values.
     *
     * @param table the table
     * @param values the values
     */
    private static void setTableValues(Table table, String[][] values) {
        try {
            int count = 0;
            table.setItemCount(values.length);
            System.out.println(table.getItems());
            for (TableItem item : table.getItems()) {
                item.setText(values[count]);
                count++;
            }
        } catch (IndexOutOfBoundsException e) {
            showMessage("Cannot set values to table: " + table.getToolTipText(), "Warning");
        }
    }
    
    private void initReset() {
        // Creating the Modeling Core object and launching it
        // TODO Allow to check log and diagram files
        try {
            engine = new ModelingEngine(libraryFile.getAbsolutePath(), discreteModelFile.getAbsolutePath(), 
                    signalsFile.getAbsolutePath(), "test-diagrams.log", "test-logs.log");
        } catch (ModelingException e) {
            // TODO Auto-generated catch block
            status("Error while launching modeling");
            e.printStackTrace();
        }
        if(engine != null) {
            engine.run();
            index = 0;
            setModelingButtonsAndMenuEnabled(true);
            nodes = engine.getEvents().keySet().toArray(new Long[0]);
            modelingTimeText.setText(nodes[0] + "ns");
            status("Modeling was succesful, timerange is " + nodes[0] + "..." + nodes[nodes.length - 1]);
            updateTables();
        } else {
            status("Error while launching modeling");
        }
    }
    
    private void stepFwd() {
        index = Math.min(index + 1, nodes.length - 1);
        modelingTimeText.setText(nodes[index] + "ns");
        status("Step forward to node #" + index + " at " + nodes[index] + "ns");
        updateTables();
    }
    
    private void stepBwd() {
        index = Math.max(index - 1, 0);
        modelingTimeText.setText(nodes[index] + "ns");
        status("Step backward to node #" + index + " at " + nodes[index] + "ns");
        updateTables();
    }
    
    private void gotoTime(long time) {
        Long nearest = engine.getEvents().floorKey(time);
        index = Arrays.binarySearch(nodes, nearest);
        status("Goto node #" + index + " at " + nodes[index] + "ns");
        updateTables();
    }
    
    private void updateTables() {
        String[][] table;
        long time = nodes[index];
        int n;
        
        // signals
        Map<Contact, Signal> signals = engine.getResults().getSignals();
        table = new String[signals.size()][2];
        n = 0;
        for(Contact contact : signals.keySet()) {
            table[n][0] = contact.toString();
            table[n][1] = signals.get(contact).getState(time).toString();
            n++;   
        }
        setSignalsTableValues(table);
        
        // active
        Set<String> active = engine.getActive().get(time);
        if(active != null) {
            table = new String[active.size()][1];
            n = 0;
            for(String elementName : active) {
                table[n][0] = elementName;
                n++;   
            }
            
        } else {
            table = new String[][]{{"<none>"}};
        }
        setActiveElementsTableValues(table);
        
        // events
        NavigableMap<Long, List<Event>> events = engine.getEvents().tailMap(time, false);
        List<String[]> temp = new LinkedList<String[]>();
        for(Long evttime : events.keySet()) {
            for(Event event : events.get(evttime)) {
                if(event.getFrom() <= time) {
                    temp.add(new String[] {evttime.toString(), event.getContact().getElement(), 
                            event.getContact().getCnumber().toString(), event.getNewstate() + "", event.getFrom() + ""});
                }
            }
        }
        table = temp.toArray(new String[0][0]);
        setEventsTableValues(table);
    }
}