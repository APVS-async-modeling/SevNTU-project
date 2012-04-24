package asyncmod.ui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;

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
import org.yaml.snakeyaml.Yaml;

import swing2swt.layout.BorderLayout;
import asyncmod.about.AboutProgram;
import asyncmod.about.AboutTeam;
import asyncmod.modeling.Element;
import asyncmod.modeling.Library;
import asyncmod.modeling.ModelingEngine;
import asyncmod.modeling.ModelingException;
import asyncmod.modeling.Scheme;
import asyncmod.results_displaying.ModelingResultsDisplayer;
import asyncmod.ui.timediagrams.TimeDiagramsWindow;

public class MainWindow {

    // UI fields!
    //

    protected static Shell shell;    
    protected Display display;
    // sizes
    private static final int WIDTH = 790;
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
    private static ModelingEngine engine;
    private static int index;
    private static Long[] nodes;
    
    // Module which will display the modeling results in UI
    private ModelingResultsDisplayer displayer;

    //files
    private static File libraryFile;
    private static File discreteModelFile;
    private static File signalsFile;

    // etc
    private final static Calendar cal = Calendar.getInstance();
    static SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss '('yyyy.MM.dd')'");
    private static ExpandBar bar;
    private MenuItem mntmSaveModelingResults;    
    private Text textLibraryEditor;
    private Text textSchemeEditor;
    private Text textSignalsEditor;
    private TabItem tabLibraryEditor;
    private Button btnSaveLibrary;
    private Button btnReloadLibrary;
    private TabItem tabSchemeEditor;
    private Button btnSaveScheme;
    private Button btnReloadScheme;
    private TabItem tabSignalsEditor;
    private Button btnSaveSignals;
    private Button btnReloadSignals;
    private static BufferedReader br;
    private static BufferedWriter bw;
      
    
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
                bar.redraw();
            }

            public void controlMoved(final ControlEvent e) {
                updateTimeDiagramsWindowPosition();
                bar.redraw();
            }

            private void updateTimeDiagramsWindowPosition() {
                if (timeDiagramsWindow != null) {
                    int xCoord = getRightUpperCornerPosition().x + UIConstants.SPACE_BETWEEN_WINDOWS;
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
                    readFileToTextField(textLibraryEditor, libraryFile);
                    openDiscreteModelMenuItem.setEnabled(true);
                    
                    Yaml yaml = new Yaml();
                    InputStream stream = null;
                    Library library = null;
                    
                    try {
                        stream = new FileInputStream(libraryFilePath);
                    } catch (FileNotFoundException ex) {
                        showMessage(Messages.ERROR_FILE_NOT_FOUND + libraryFilePath, "Error");
                    }
                    try {
                        library = (Library) yaml.load(stream);
                        updateExpandBarElements(library.getLibrary().values());
                        String message = Messages.LIBRARY_FILE_SELECTED + libraryFilePath;
                        status(message);
                    } catch(Exception ex) {
                        showMessage(Messages.ERROR_WRONG_LIBRARY_DOCUMENT + libraryFilePath, "Error");                        
                    }
                    
                    textLibraryEditor.setEnabled(true);
                    btnReloadLibrary.setEnabled(true);
                    btnSaveLibrary.setEnabled(true);
                    
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
                    readFileToTextField(textSchemeEditor, discreteModelFile);
//                    DUModelController controller = new DUModelController(discreteModelFilePath);
//                    try {
//                        DUModel model = controller.parseDUModelFromFile();
//                    } catch (IOException exc) {
//                        showMessage(exc.getMessage(), "Error");
//                    }

                    textSchemeEditor.setEnabled(true);
                    btnReloadScheme.setEnabled(true);
                    btnSaveScheme.setEnabled(true);
                    
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
                    readFileToTextField(textSignalsEditor, signalsFile);
                    textSignalsEditor.setEnabled(true);
                    btnReloadSignals.setEnabled(true);
                    btnSaveSignals.setEnabled(true);                    
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

        mntmSaveModelingResults = new MenuItem(menu_5, SWT.NONE);
        mntmSaveModelingResults.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String fileToResultsSavingPath = dlgResultsSaving.open();
                if(fileToResultsSavingPath != null) {
                    try { 
                        FileWriter fr = new FileWriter(new File(fileToResultsSavingPath));
                        fr.write(modelingResultsText.getText() + "\n");
                        fr.close();                        
                    } catch (IOException ex){
                        
                    }
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
        initResetMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                initReset();
            }
        });
        initResetMenuItem.setText("Init/Reset");

        gotoTimeMenuItem = new MenuItem(menu_3, SWT.NONE);
        gotoTimeMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                gotoTime();
            }
        });
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
        MainWindowTab.setText("Modeling");

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
                initReset();
            }
        });
        initResetBtn.setText("Init/Reset");

        stepFwdBtn = new Button(ControlButtonsComposite, SWT.NONE);
        stepFwdBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                stepForward();
            }
        });
        stepFwdBtn.setText("Step Forward");

        stepBwdBtn = new Button(ControlButtonsComposite, SWT.NONE);
        stepBwdBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                stepBackward();
            }
        });
        stepBwdBtn.setText("Step Backward");

        gotoTimeBtn = new Button(ControlButtonsComposite, SWT.NONE);
        gotoTimeBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {                
                gotoTime();
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
                final int coordX = getRightUpperCornerPosition().x + UIConstants.SPACE_BETWEEN_WINDOWS;
                final int coordY = getRightUpperCornerPosition().y;
                if (timeDiagramsWindow == null) {
                    // singleton instance of TimeDiagram
                    timeDiagramsWindow = new TimeDiagramsWindow(shell, SWT.NONE, nodes[nodes.length - 1], "test-diagrams.log");
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
        elementsComposite.setLayout(new BorderLayout(0, 0));
        
        Group grpElementTypes = new Group(elementsComposite, SWT.NONE);
        grpElementTypes.setText("Available element types:");
        grpElementTypes.setLayoutData(BorderLayout.CENTER);
        grpElementTypes.setLayout(new FillLayout(SWT.HORIZONTAL));
        
        bar = new ExpandBar(grpElementTypes, SWT.BORDER | SWT.V_SCROLL);                
        bar.setSpacing(5);

        Composite modelingStateComposite = new Composite(sashForm, SWT.BORDER);
        modelingStateComposite.setLayout(new GridLayout(3, false));
        
        Group grpActiveElementsList = new Group(modelingStateComposite, SWT.NONE);
        grpActiveElementsList.setToolTipText(Messages.TOOLTIP_ACTIVE_ELEMENTS_TABLE);
        GridData gd_grpActiveElementsList = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gd_grpActiveElementsList.widthHint = 81;
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
        gd_grpSignalsMatrix.widthHint = 112;
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
        
        tabLibraryEditor = new TabItem(mainWindowTabFolder, SWT.NONE);
        tabLibraryEditor.setText("Library Editor");
        
        Composite composite = new Composite(mainWindowTabFolder, SWT.NONE);
        tabLibraryEditor.setControl(composite);
        composite.setLayout(new BorderLayout(0, 0));
        
        textLibraryEditor = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
        textLibraryEditor.setLayoutData(BorderLayout.CENTER);
        
        Composite composite_3 = new Composite(composite, SWT.NONE);
        composite_3.setLayoutData(BorderLayout.NORTH);
        composite_3.setLayout(new GridLayout(2, false));
        
        btnSaveLibrary = new Button(composite_3, SWT.NONE);
        btnSaveLibrary.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                saveFileFromTextField(textLibraryEditor, libraryFile);
            }
        });
        btnSaveLibrary.setText("Save Library");
        
        btnReloadLibrary = new Button(composite_3, SWT.NONE);
        btnReloadLibrary.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                readFileToTextField(textLibraryEditor, libraryFile);
            }
        });
        btnReloadLibrary.setText("Reload Library");
        
        tabSchemeEditor = new TabItem(mainWindowTabFolder, SWT.NONE);
        tabSchemeEditor.setText("Scheme Editor");
        
        Composite composite_1 = new Composite(mainWindowTabFolder, SWT.NONE);
        tabSchemeEditor.setControl(composite_1);
        composite_1.setLayout(new BorderLayout(0, 0));
        
        textSchemeEditor = new Text(composite_1, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
        textSchemeEditor.setLayoutData(BorderLayout.CENTER);
        
        Composite composite_2 = new Composite(composite_1, SWT.NONE);
        composite_2.setLayoutData(BorderLayout.NORTH);
        composite_2.setLayout(new GridLayout(2, false));
        
        btnSaveScheme = new Button(composite_2, SWT.NONE);
        btnSaveScheme.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                saveFileFromTextField(textSchemeEditor, discreteModelFile);
            }
        });
        btnSaveScheme.setText("Save Scheme");
        
        btnReloadScheme = new Button(composite_2, SWT.NONE);
        btnReloadScheme.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                readFileToTextField(textSchemeEditor, discreteModelFile);
            }
        });
        btnReloadScheme.setText("Reload Scheme");
        
        tabSignalsEditor = new TabItem(mainWindowTabFolder, SWT.NONE);
        tabSignalsEditor.setText("Signals Editor");
        
        Composite composite_4 = new Composite(mainWindowTabFolder, SWT.NONE);
        tabSignalsEditor.setControl(composite_4);
        composite_4.setLayout(new BorderLayout(0, 0));
        
        textSignalsEditor = new Text(composite_4, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
        textSignalsEditor.setLayoutData(BorderLayout.CENTER);
        
        Composite composite_5 = new Composite(composite_4, SWT.NONE);
        composite_5.setLayoutData(BorderLayout.NORTH);
        composite_5.setLayout(new GridLayout(2, false));
        
        btnSaveSignals = new Button(composite_5, SWT.NONE);
        btnSaveSignals.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                saveFileFromTextField(textSignalsEditor, signalsFile);
            }
        });
        btnSaveSignals.setText("Save Signals");
        
        btnReloadSignals = new Button(composite_5, SWT.NONE);
        btnReloadSignals.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                readFileToTextField(textSignalsEditor, signalsFile);
            }
        });
        btnReloadSignals.setText("Reload Signals");
        
        TabItem LogTab = new TabItem(mainWindowTabFolder, SWT.NONE);
        LogTab.setText("Logs and results");

        Composite logComposite = new Composite(mainWindowTabFolder, SWT.NONE);
        LogTab.setControl(logComposite);
        logComposite.setLayout(new BorderLayout(0, 0));

        TabFolder logTabFolder = new TabFolder(logComposite, SWT.BOTTOM);
        logTabFolder.setLayoutData(BorderLayout.CENTER);

        TabItem modelingResultsTab = new TabItem(logTabFolder, SWT.NONE);
        modelingResultsTab.setText("Modeling results");

        Composite modelingResultsComposite = new Composite(logTabFolder, SWT.NONE);
        modelingResultsTab.setControl(modelingResultsComposite);
        modelingResultsComposite.setLayout(new FillLayout(SWT.HORIZONTAL));

        modelingResultsText = new Text(modelingResultsComposite, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
        modelingResultsText.setEditable(false);

        TabItem warningsTab = new TabItem(logTabFolder, SWT.NONE);
        warningsTab.setText("Warnings");

        Composite warningsComposite = new Composite(logTabFolder, SWT.NONE);
        warningsTab.setControl(warningsComposite);
        warningsComposite.setLayout(new FillLayout(SWT.HORIZONTAL));

        warningText = new Text(warningsComposite, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
        warningText.setEditable(false);

        TabItem errorsTab = new TabItem(logTabFolder, SWT.NONE);
        errorsTab.setText("Errors");

        Composite errorsComposite = new Composite(logTabFolder, SWT.NONE);
        errorsTab.setControl(errorsComposite);
        errorsComposite.setLayout(new FillLayout(SWT.HORIZONTAL));

        errorsText = new Text(errorsComposite, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
        errorsText.setEditable(false);
        
        TabItem logsTab = new TabItem(logTabFolder, SWT.NONE);
        logsTab.setText("Logs");
        
        Composite fullLogComposite = new Composite(logTabFolder, SWT.NONE);
        logsTab.setControl(fullLogComposite);
        fullLogComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
        
        fullLogText = new Text(fullLogComposite, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
        fullLogText.setEditable(false);
              
        setControlsAndMenusEnabled(false);        
    }

    private final Point getRightUpperCornerPosition() {
        int coordX = shell.getBounds().x + shell.getBounds().width;
        int coordY = shell.getBounds().y;
        Point point = new Point(coordX, coordY);
        return point;
    }
    
    private void setControlsAndMenusEnabled(boolean state) {
        textLibraryEditor.setEnabled(state);
        btnReloadLibrary.setEnabled(state);
        btnSaveLibrary.setEnabled(state);
        textSchemeEditor.setEnabled(state);
        btnReloadScheme.setEnabled(state);
        btnSaveScheme.setEnabled(state);        
        textSignalsEditor.setEnabled(state);
        btnReloadSignals.setEnabled(state);
        btnSaveSignals.setEnabled(state);
        initResetBtn.setEnabled(state);
        stepFwdBtn.setEnabled(state);
        stepBwdBtn.setEnabled(state);
        gotoTimeBtn.setEnabled(state);
        timeDiagramsBtn.setEnabled(state);
        initResetMenuItem.setEnabled(state);
        stepFwdMenuItem.setEnabled(state);
        stepBwdMenuItem.setEnabled(state);
        gotoTimeMenuItem.setEnabled(state);
        mntmSaveModelingResults.setEnabled(state);
    }

    /**
     * Shows the message window.
     * 
     * @param text the text
     * @param type the type of message ("Error" | "Information").
     */
    public static void showMessage(final String text, final String type) {
        int msgWindowType = 0;
        if (type.equals("Error")) {
            msgWindowType |= SWT.ICON_ERROR;
            addError(text);
        }
        if (type.equals("Information")) {
            msgWindowType |= SWT.ICON_INFORMATION;
            addToLog(text);
        }
        MessageBox box = new MessageBox(shell, msgWindowType | SWT.OK);
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
        warningText.append(dateFormatter.format(cal.getTime()) + ": " + str + "\n");
        appendLineDelimiter(warningText);
        addToLog("[Warning] " + str);
    }

    /**
     * Adds the input string to the "Errors" textField.
     *
     * @param str the input string.
     */
    public static void addError(String str) {
        errorsText.append(dateFormatter.format(cal.getTime()) + ": " + str + "\n");
        appendLineDelimiter(errorsText);
        addToLog("[Error] " + str);
    }

    /**
     * Adds the input string to the "Logs" textField.
     *
     * @param str the input string.
     */
    public static void addToLog(String str) {
        fullLogText.append(dateFormatter.format(cal.getTime()) + ": " + str + "\n");
        appendLineDelimiter(fullLogText);
    }

    /**
     * Adds the input string to the "Logs" textField.
     *
     * @param str the input string.
     */
    public static void addToModelingResults(String str) {
        modelingResultsText.append(dateFormatter.format(cal.getTime()) + ": " + str);
        appendLineDelimiter(modelingResultsText);
    }
    
    private static void appendLineDelimiter(Text textField) {
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
            for (TableItem item : table.getItems()) {
                item.setText(values[count]);
                count++;
            }
        } catch (IndexOutOfBoundsException e) {
            showMessage("Cannot set values to table: " + table.getToolTipText(), "Error");
        }
    }
    
    private void initReset() {        
        // Creating the Modeling Core object and launching it
        // TODO Allow to check log and diagram files
        try {
            engine = new ModelingEngine(libraryFile.getAbsolutePath(), discreteModelFile.getAbsolutePath(), 
                    signalsFile.getAbsolutePath(), "test-diagrams.log", "test-logs.log");
            
            displayer = new ModelingResultsDisplayer(engine);
            
        } catch (ModelingException e) {
            MainWindow.showMessage(e.getMessage(), "Error");
            status(Messages.ERROR_WHILE_LAUNCHING_MODELING);
            e.printStackTrace();
        }
        if(engine != null) {
            engine.run();
            if(engine.correct) {
                index = 0;
                setControlsAndMenusEnabled(true);
                nodes = engine.getEvents().keySet().toArray(new Long[0]);
                modelingTimeText.setText(nodes[0] + "ns");
                status("Modeling was succesful, timerange is " + nodes[0] + "..." + nodes[nodes.length - 1]);
                displayer.updateUITables();
            } else {
                status(Messages.ERROR_WHILE_MODELING_SCHEME);
            }
        } else {
            status(Messages.ERROR_WHILE_LAUNCHING_MODELING);
        }
    }
    
    private void stepForward() {
        index = Math.min(index + 1, nodes.length - 1);
        modelingTimeText.setText(nodes[index] + "ns");
        status("Step forward to node #" + index + " at " + nodes[index] + "ns");
        displayer.updateUITables();
        if(tableEvents.getItemCount() == 0){
            showMessage("Modeling completed. See modeling log for more results. ", "Information");
        }
    }

    private void stepBackward() {
        index = Math.max(index - 1, 0);
        modelingTimeText.setText(nodes[index] + "ns");
        status("Step backward to node #" + index + " at " + nodes[index] + "ns");
        displayer.updateUITables();
    }
    
    private void gotoTime() {
        status(Messages.MODELING_TIME_CHANGING);
        NumberInputDialog dialog = new NumberInputDialog(shell);
        Long time = dialog.open();
        if (time != null) {
            Long nearest = engine.getEvents().floorKey(time);
            if (nearest == null)
                nearest = nodes[0];
            index = Arrays.binarySearch(nodes, nearest);
            modelingTimeText.setText(nearest + "ns");
            status("Goto node #" + index + " at " + nodes[index] + "ns");
            displayer.updateUITables();
        } else {
            status(Messages.MODELING_TIME_DOESNT_CHANGED);
        }
    }
        
    public static void updateExpandBarElements(Collection<Element> libraryElements, Scheme scheme) {
        for (ExpandItem item : bar.getItems()) {
            item.dispose();
        }
        for (Element element : libraryElements) {
            String elementName = element.getName();
            String number = getSuchElementsCount(scheme.getElements(), elementName);            
            String elementDescription = element.getDescr();
            String delay = String.valueOf(element.getDelay());
            String inputsCount = String.valueOf(element.getIcnt());
            String outputsCount = String.valueOf(element.getOcnt());            
            String elementOfSuchType = getNamesOfElements (scheme.getElements(), elementName);
            
            Composite composite = new Composite(bar, SWT.NONE);
            composite.setLayout(new GridLayout(1, false));
            Text textField = new Text(composite, SWT.WRAP | SWT.CENTER | SWT.MULTI);
            textField.setEditable(false);
            textField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            textField.setText(elementDescription + "; \ndelay = " + delay + "\n inputs count = " + inputsCount
                    + ";\n outputs count = " + outputsCount + "\n"+ elementOfSuchType);
            ExpandItem item = new ExpandItem(bar, SWT.NONE, 0);
            item.setExpanded(false);
            item.setText(elementName + " [" + number + "] ");
            item.setControl(composite);
            composite.pack();
            composite.pack(true);
            item.setHeight(textField.computeSize(SWT.DEFAULT, SWT.DEFAULT).y + 30);
        }
        for (ExpandItem item : bar.getItems()) {
            item.setExpanded(true);
        }
        bar.redraw();
    }

    private static String getNamesOfElements(Map<String, String> elements, String elementName) {
        int count = 0;
        String result = "";
        for (String name : elements.values()) {
            if (name.equals(elementName)) {
                result += elements.keySet().toArray(new String[] {})[count] + ", ";
            }
            count++;
        }
        if(result.length() == 0){
            return "Scheme doesn`t contain elements of this type.";
        }
        return "Elements: [" + result.substring(0, result.length() - 2) + "]";
    }
    
    public static void updateExpandBarElements(Collection<Element> libraryElements) {
        for (ExpandItem item : bar.getItems()) {
            item.dispose();
        }
        for (Element element : libraryElements) {
            String elementName = element.getName();         
            String elementDescription = element.getDescr();
            String delay = String.valueOf(element.getDelay());
            String inputsCount = String.valueOf(element.getIcnt());
            String outputsCount = String.valueOf(element.getOcnt());            
            Composite composite = new Composite(bar, SWT.NONE);
            composite.setLayout(new GridLayout(1, false));
            Text textField = new Text(composite, SWT.WRAP | SWT.CENTER | SWT.MULTI);
            textField.setEditable(false);
            textField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            textField.setText(elementDescription + "; \ndelay = " + delay + "\n inputs count = " + inputsCount
                    + ";\n outputs count = " + outputsCount);
            ExpandItem item = new ExpandItem(bar, SWT.NONE, 0);
            item.setExpanded(false);
            item.setText(elementName);
            item.setControl(composite);
            composite.pack();
            composite.pack(true);
            item.setHeight(textField.computeSize(SWT.DEFAULT, SWT.DEFAULT).y + 10);
        }        
        for (ExpandItem item : bar.getItems()) {
            item.setExpanded(true);
        }
        bar.redraw();
    }
    
    private static String getSuchElementsCount(Map<String, String> elements, String elementName) {
        int result = 0;
        for (String elementDesc : elements.values()) {
            if (elementDesc.equals(elementName)) {
                result++;
            }
        }
        return result + "";
    }
    
    public static void readFileToTextField(Text textField, File file) {
        textField.setText("");
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            while((line = br.readLine()) != null){
                textField.append("\n" + line);
            }
        } catch (IOException e) {
            showMessage("Can`t read file " + file.getAbsolutePath(), "Error");
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void saveFileFromTextField(Text textField, File file) {
        try {
            bw = new BufferedWriter(new FileWriter(file));
            bw.write(textField.getText());
        } catch (IOException e) {
            showMessage("Can`t write file " + file.getAbsolutePath(), "Error");
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static Long getCurrentNode() {
        return nodes[index];
    }
}