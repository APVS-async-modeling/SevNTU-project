package asyncmod.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
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
import asyncmod.modeling.Circuit;
import asyncmod.modeling.Contact;
import asyncmod.modeling.Element;
import asyncmod.modeling.Library;
import asyncmod.modeling.ModelingEngine;
import asyncmod.modeling.ModelingException;
import asyncmod.modeling.Scheme;
import asyncmod.modeling.Signal;
import asyncmod.modeling.SignalBundle;
import asyncmod.results_displaying.ModelingResultsDisplayer;
import asyncmod.ui.timediagrams.TimeDiagramsWindow;

public class MainWindow {

    // UI fields!
    //

    protected static Shell shell;    
    protected Display display;
    // sizes
    @SuppressWarnings("unused")
    private static final int WIDTH = 800;
    @SuppressWarnings("unused")
    private static final int HEIGHT = 600;

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
    private Library library;
    private Scheme scheme;
    private SignalBundle signals;
    //private static File libraryFile;
    //private static File discreteModelFile;
    //private static File signalsFile;

    // etc
    private final static Calendar cal = Calendar.getInstance();
    static SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss '('yyyy.MM.dd')'");
    private static ExpandBar bar;
    private MenuItem mntmSaveModelingResults;
        
    private TabItem tabLibraryEditor;
    private Button btnSaveLibrary;
    private Button btnReloadLibrary;
    private TabItem tabSchemeEditor;
    private Button ce_save;
    private Button ce_load;
    private TabItem tabSignalsEditor;
    private Button se_save;
    private Button se_load;
    
    private Composite le_composite;
    private Text le_view;
    
    private Composite ce_composite;
    private Text ce_view;
    private Combo ce_element;
    private Combo ce_type;
    private Combo ce_circuit;
    private Combo ce_source;
    private Combo ce_output;
    private Combo ce_input;
    
    private Composite se_composite;
    private Combo se_signal;
    private Text se_time;
    private Text se_state;
    private Text se_view;
    private Combo ce_circuit2;
    private Combo ce_drain;
    private Button ce_output_add;
    private Button ce_output_del;
    private Button ce_input_del;
    private Button ce_input_add;
    private Button ce_cont_add;
    private Button ce_cont_del;
    private Button ce_circ_add;
    private Button ce_circ_del;
    private Button ce_elem_add;
    private Button ce_elem_del;
    private Button se_add;
    private Button se_del;
    private long lastInit;
       
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

        library = new Library();
        scheme = new Scheme();
        signals = new SignalBundle();
        
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
        shell.setSize(790, 550);
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
                loadLibrary();
                openDiscreteModelMenuItem.setEnabled(true);
                openSignalsFileMenuItem.setEnabled(true);
            }
        });

        openDiscreteModelMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                loadScheme();
            }
        });
        
        openSignalsFileMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                loadSignals();
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
                    timeDiagramsWindow = new TimeDiagramsWindow(shell, SWT.NONE, nodes[nodes.length - 1], "results\\" + lastInit + "-diagrams.log");
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
        
        le_composite = new Composite(mainWindowTabFolder, SWT.NONE);
        tabLibraryEditor.setControl(le_composite);
        le_composite.setLayout(new BorderLayout(0, 0));
        
        le_view = new Text(le_composite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
        le_view.setEditable(false);
        le_view.setLayoutData(BorderLayout.CENTER);
        
        Composite composite_3 = new Composite(le_composite, SWT.NONE);
        composite_3.setLayoutData(BorderLayout.NORTH);
        composite_3.setLayout(new GridLayout(2, false));
        
        btnSaveLibrary = new Button(composite_3, SWT.NONE);
        btnSaveLibrary.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                saveLibrary();
            }
        });
        btnSaveLibrary.setText("Save Library");
        
        btnReloadLibrary = new Button(composite_3, SWT.NONE);
        btnReloadLibrary.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                loadLibrary();
                openDiscreteModelMenuItem.setEnabled(true);
                openSignalsFileMenuItem.setEnabled(true);
            }
        });
        btnReloadLibrary.setText("Reload Library");
        
        tabSchemeEditor = new TabItem(mainWindowTabFolder, SWT.NONE);
        tabSchemeEditor.setText("Scheme Editor");
        
        ce_composite = new Composite(mainWindowTabFolder, SWT.NONE);
        tabSchemeEditor.setControl(ce_composite);
        ce_composite.setLayout(new BorderLayout(0, 0));
        
        Composite composite_2 = new Composite(ce_composite, SWT.NONE);
        composite_2.setLayoutData(BorderLayout.NORTH);
        composite_2.setLayout(new GridLayout(2, false));
        
        ce_save = new Button(composite_2, SWT.NONE);
        ce_save.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                saveScheme();
            }
        });
        ce_save.setText("Save Scheme");
        
        ce_load = new Button(composite_2, SWT.NONE);
        ce_load.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                loadScheme();
            }
        });
        ce_load.setText("Reload Scheme");
        
        Composite composite_6 = new Composite(ce_composite, SWT.NONE);
        composite_6.setLayoutData(BorderLayout.CENTER);
        
        ce_view = new Text(composite_6, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
        ce_view.setEditable(false);
        ce_view.setBounds(277, 0, 489, 429);
        
        ScrolledComposite scrolledComposite_1 = new ScrolledComposite(composite_6, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        scrolledComposite_1.setBounds(0, 0, 271, 429);
        scrolledComposite_1.setExpandHorizontal(true);
        scrolledComposite_1.setExpandVertical(true);
        
        Composite composite_7 = new Composite(scrolledComposite_1, SWT.NONE);
        
        Group ce_elem_grp = new Group(composite_7, SWT.NONE);
        ce_elem_grp.setText("Add elements");
        ce_elem_grp.setBounds(0, 0, 250, 104);
        
        ce_type = new Combo(ce_elem_grp, SWT.READ_ONLY);
        ce_type.setToolTipText("Specify element name");
        ce_type.setBounds(10, 47, 237, 23);
        
        ce_elem_add = new Button(ce_elem_grp, SWT.NONE);
        ce_elem_add.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                schemeEditorAddElement();
            }
        });
        ce_elem_add.setToolTipText("Adds new element of selected type with a specified name or changes type of existing element");
        ce_elem_add.setBounds(10, 76, 75, 25);
        ce_elem_add.setText("Add");
        
        ce_elem_del = new Button(ce_elem_grp, SWT.NONE);
        ce_elem_del.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                schemeEditorDelElement();
            }
        });
        ce_elem_del.setToolTipText("Deletes element with specified name");
        ce_elem_del.setBounds(91, 76, 75, 25);
        ce_elem_del.setText("Delete");
        
        ce_element = new Combo(ce_elem_grp, SWT.NONE);
        ce_element.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String elementName = ce_element.getItem(ce_element.getSelectionIndex());
                String elementType = scheme.getElements().get(elementName);
                ce_type.select(ce_type.indexOf(elementType));
            }
        });
        ce_element.addVerifyListener(new VerifyListener() {
            public void verifyText(VerifyEvent evt) {
                if((ce_element.getText() + evt.text).matches("[A-Za-z][A-Za-z0-9_]*")) evt.doit = true;
                else evt.doit = false;
            }
        });
        ce_element.setBounds(10, 18, 237, 23);
        
        Group ce_circ_grp = new Group(composite_7, SWT.NONE);
        ce_circ_grp.setText("Add circuit");
        ce_circ_grp.setBounds(0, 110, 250, 104);
        
        ce_circ_add = new Button(ce_circ_grp, SWT.NONE);
        ce_circ_add.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                schemeEditorAddCircuit();
            }
        });
        ce_circ_add.setToolTipText("Adds new circuit with a specified name or replaces existing circuit");
        ce_circ_add.setText("Add");
        ce_circ_add.setBounds(10, 76, 75, 25);
        
        ce_circ_del = new Button(ce_circ_grp, SWT.NONE);
        ce_circ_del.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                schemeEditorDelCircuit();
            }
        });
        ce_circ_del.setToolTipText("Deletes circuit with specified name");
        ce_circ_del.setText("Delete");
        ce_circ_del.setBounds(91, 76, 75, 25);
        
        ce_source = new Combo(ce_circ_grp, SWT.READ_ONLY);
        ce_source.setToolTipText("Specify source contact name");
        ce_source.setBounds(10, 47, 237, 23);
        
        ce_circuit = new Combo(ce_circ_grp, SWT.NONE);
        ce_circuit.setBounds(10, 18, 237, 23);
        
        Group ce_cont_grp = new Group(composite_7, SWT.NONE);
        ce_cont_grp.setText("Add contact");
        ce_cont_grp.setBounds(0, 220, 250, 104);
        
        ce_cont_add = new Button(ce_cont_grp, SWT.NONE);
        ce_cont_add.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                schemeEditorAddContact();
            }
        });
        ce_cont_add.setToolTipText("Adds new contact to selected circuit");
        ce_cont_add.setText("Add");
        ce_cont_add.setBounds(10, 76, 75, 25);
        
        ce_cont_del = new Button(ce_cont_grp, SWT.NONE);
        ce_cont_del.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                schemeEditorDelContact();
            }
        });
        ce_cont_del.setToolTipText("Deletes specified contact from specified circuit");
        ce_cont_del.setText("Delete");
        ce_cont_del.setBounds(91, 76, 75, 25);
        
        ce_drain = new Combo(ce_cont_grp, SWT.READ_ONLY);
        ce_drain.setToolTipText("Specify destination contact name");
        ce_drain.setBounds(10, 47, 237, 23);
        
        ce_circuit2 = new Combo(ce_cont_grp, SWT.READ_ONLY);
        ce_circuit2.setToolTipText("Specify circuit name");
        ce_circuit2.setBounds(10, 18, 237, 23);
        
        Group ce_input_grp = new Group(composite_7, SWT.NONE);
        ce_input_grp.setBounds(0, 330, 250, 77);
        ce_input_grp.setText("Add input");
        
        ce_input_add = new Button(ce_input_grp, SWT.NONE);
        ce_input_add.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                schemeEditorAddInput();
            }
        });
        ce_input_add.setToolTipText("Adds new input to scheme");
        ce_input_add.setText("Add");
        ce_input_add.setBounds(10, 49, 75, 25);
        
        ce_input_del = new Button(ce_input_grp, SWT.NONE);
        ce_input_del.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                schemeEditorDelInput();
            }
        });
        ce_input_del.setToolTipText("Deletes specified input");
        ce_input_del.setText("Delete");
        ce_input_del.setBounds(91, 49, 75, 25);
        
        ce_input = new Combo(ce_input_grp, SWT.NONE);
        ce_input.addVerifyListener(new VerifyListener() {
            public void verifyText(VerifyEvent evt) {
                if((ce_input.getText() + evt.text).matches("[A-Za-z][A-Za-z0-9_]*")) evt.doit = true;
                else evt.doit = false;
            }
        });
        ce_input.setToolTipText("Specify input name");
        ce_input.setBounds(10, 20, 237, 23);
        
        Group ce_output_grp = new Group(composite_7, SWT.NONE);
        ce_output_grp.setBounds(0, 413, 250, 77);
        ce_output_grp.setText("Add output");
        
        ce_output_add = new Button(ce_output_grp, SWT.NONE);
        ce_output_add.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                schemeEditorAddOutput();
            }
        });
        ce_output_add.setToolTipText("Adds new output to scheme");
        ce_output_add.setText("Add");
        ce_output_add.setBounds(10, 49, 75, 25);
        
        ce_output_del = new Button(ce_output_grp, SWT.NONE);
        ce_output_del.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                schemeEditorDelOutput();
            }
        });
        ce_output_del.setToolTipText("Deletes specified output");
        ce_output_del.setText("Delete");
        ce_output_del.setBounds(91, 49, 75, 25);
        
        ce_output = new Combo(ce_output_grp, SWT.NONE);
        ce_output.addVerifyListener(new VerifyListener() {
            public void verifyText(VerifyEvent evt) {
                if((ce_output.getText() + evt.text).matches("[A-Za-z][A-Za-z0-9_]*")) evt.doit = true;
                else evt.doit = false;
            }
        });
        ce_output.setToolTipText("Specify output name");
        ce_output.setBounds(10, 20, 237, 23);
        scrolledComposite_1.setContent(composite_7);
        scrolledComposite_1.setMinSize(composite_7.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        
        tabSignalsEditor = new TabItem(mainWindowTabFolder, SWT.NONE);
        tabSignalsEditor.setText("Signals Editor");
        
        se_composite = new Composite(mainWindowTabFolder, SWT.NONE);
        tabSignalsEditor.setControl(se_composite);
        se_composite.setLayout(new BorderLayout(0, 0));
        
        Composite composite_5 = new Composite(se_composite, SWT.NONE);
        composite_5.setLayoutData(BorderLayout.NORTH);
        composite_5.setLayout(new GridLayout(2, false));
        
        se_save = new Button(composite_5, SWT.NONE);
        se_save.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                saveSignals();
            }
        });
        se_save.setText("Save Signals");
        
        se_load = new Button(composite_5, SWT.NONE);
        se_load.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                loadSignals();
            }
        });
        se_load.setText("Reload Signals");
        
        Composite composite_8 = new Composite(se_composite, SWT.NONE);
        composite_8.setLayoutData(BorderLayout.CENTER);
        
        se_view = new Text(composite_8, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
        se_view.setEnabled(true);
        se_view.setEditable(false);
        se_view.setLocation(253, 0);
        se_view.setSize(513, 429);
        
        se_signal = new Combo(composite_8, SWT.READ_ONLY);
        se_signal.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                signalEditorSelectionChanged();
            }
        });
        se_signal.setBounds(10, 10, 237, 23);
        
        se_add = new Button(composite_8, SWT.NONE);
        se_add.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                signalEditorAddSignal();
            }
        });
        se_add.setBounds(10, 351, 75, 25);
        se_add.setText("Add signal");
        
        se_del = new Button(composite_8, SWT.NONE);
        se_del.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                signalEditorDelSignal();
            }
        });
        se_del.setBounds(91, 351, 75, 25);
        se_del.setText("Delete signal");
        
        se_time = new Text(composite_8, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
        se_time.setBounds(10, 39, 237, 150);
        
        se_state = new Text(composite_8, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
        se_state.setBounds(10, 195, 237, 150);
        
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
        schemeEditorSetEnabled(false);
        signalEditorSetEnabled(false);
    }

    private final Point getRightUpperCornerPosition() {
        int coordX = shell.getBounds().x + shell.getBounds().width;
        int coordY = shell.getBounds().y;
        Point point = new Point(coordX, coordY);
        return point;
    }
    
    private void setControlsAndMenusEnabled(boolean state) {
        le_view.setEnabled(true);
        btnReloadLibrary.setEnabled(true);
        btnSaveLibrary.setEnabled(true);
        ce_view.setEnabled(true);
        ce_load.setEnabled(true);
        ce_save.setEnabled(true);        
        se_load.setEnabled(true);
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
            lastInit = System.currentTimeMillis();
            engine = new ModelingEngine(library, scheme, signals, "results\\" + lastInit + "-diagrams.log", "results\\" + lastInit + "-logs.log");
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
        if(timeDiagramsWindow != null) timeDiagramsWindow.redrawTimeDiagrams(true);
    }

    private void stepBackward() {
        index = Math.max(index - 1, 0);
        modelingTimeText.setText(nodes[index] + "ns");
        status("Step backward to node #" + index + " at " + nodes[index] + "ns");
        displayer.updateUITables();
        if(timeDiagramsWindow != null) timeDiagramsWindow.redrawTimeDiagrams(true);
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
        if(timeDiagramsWindow != null) timeDiagramsWindow.redrawTimeDiagrams(true);
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
    
    public void updateExpandBarElements(Collection<Element> libraryElements) {
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
    
    public static Long getCurrentNode() {
        return nodes[index];
    }
        
    //// LIBRARY ////
    public void loadLibrary() {
        final String libraryFilePath = dlgLibrary.open();
        if (libraryFilePath != null) {
            Yaml yaml = new Yaml();
            InputStream stream = null;
            
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
                schemeEditorPrepare();
                signalEditorPrepare();
                le_view.setText(yaml.dump(library));
            } catch(Exception ex) {
                showMessage(Messages.ERROR_WRONG_LIBRARY_DOCUMENT + libraryFilePath, "Error");    
                ex.printStackTrace();
            }
        } else {
            status(Messages.LIBRARY_FILE_NOT_SELECTED);
        }
    }
    
    public void saveLibrary() {
        final String libraryFilePath = dlgLibrary.open();
        if (libraryFilePath != null) {
            Yaml yaml = new Yaml();
            BufferedWriter stream = null;
            try {
                stream = new BufferedWriter(new FileWriter(libraryFilePath));
                stream.write(yaml.dump(library));
                stream.close();
            } catch (FileNotFoundException ex) {
                showMessage(Messages.ERROR_FILE_NOT_FOUND + libraryFilePath, "Error");
            } catch (IOException ex) {
                showMessage(Messages.ERROR_CANT_WRITE_TO_FILE + libraryFilePath, "Error");
            }
            String message = Messages.FILE_SAVED + libraryFilePath;
            status(message);
        } else {
            status(Messages.LIBRARY_FILE_NOT_SELECTED);
        }
    }
    
    //// SCHEME ////
    public void loadScheme() {
        final String discreteModelFilePath = dlgDiscreteModel.open();
        if (discreteModelFilePath != null) {
            Yaml yaml = new Yaml();
            InputStream stream = null;
            try {
                stream = new FileInputStream(discreteModelFilePath);
            } catch (FileNotFoundException ex) {
                showMessage(Messages.ERROR_FILE_NOT_FOUND + discreteModelFilePath, "Error");
            }
            try {
                scheme = (Scheme) yaml.load(stream);
            } catch(Exception ex) {
                showMessage(Messages.ERROR_WRONG_SCHEME_DOCUMENT + discreteModelFilePath, "Error");
            }
            MainWindow.updateExpandBarElements(library.getLibrary().values(), scheme);
            schemeEditorPrepare();
            signalEditorPrepare();
            final String message = Messages.DISCRETE_MODEL_FILE_SELECTED + discreteModelFilePath;
            status(message);
        } else {
            status(Messages.DISCRETE_MODEL_FILE_NOT_SELECTED);
        }
    }
    
    public void saveScheme() {
        final String schemeFilePath = dlgDiscreteModel.open();
        if (schemeFilePath != null) {
            Yaml yaml = new Yaml();
            BufferedWriter stream = null;
            try {
                stream = new BufferedWriter(new FileWriter(schemeFilePath));
                stream.write(yaml.dump(scheme));
                stream.close();
            } catch (FileNotFoundException ex) {
                showMessage(Messages.ERROR_FILE_NOT_FOUND + schemeFilePath, "Error");
            } catch (IOException ex) {
                showMessage(Messages.ERROR_CANT_WRITE_TO_FILE + schemeFilePath, "Error");
            }
            String message = Messages.FILE_SAVED + schemeFilePath;
            status(message);
        } else {
            status(Messages.LIBRARY_FILE_NOT_SELECTED);
        }
    }
    
    public void schemeEditorSetEnabled(boolean enabled) {
        ce_element.setEnabled(enabled);
        ce_type.setEnabled(enabled);
        ce_elem_add.setEnabled(enabled);
        ce_elem_del.setEnabled(enabled);
        ce_circuit.setEnabled(enabled);
        ce_source.setEnabled(enabled);
        ce_circ_add.setEnabled(enabled);
        ce_circ_del.setEnabled(enabled);
        ce_circuit2.setEnabled(enabled);
        ce_drain.setEnabled(enabled);
        ce_cont_add.setEnabled(enabled);
        ce_cont_del.setEnabled(enabled);
        ce_input.setEnabled(enabled);
        ce_input_add.setEnabled(enabled);
        ce_input_del.setEnabled(enabled);
        ce_output.setEnabled(enabled);
        ce_output_add.setEnabled(enabled);
        ce_output_del.setEnabled(enabled);
        ce_load.setEnabled(enabled);
        ce_save.setEnabled(enabled);
    }
    
    public void schemeEditorPrepare() {
        schemeEditorSetEnabled(true);
        boolean libraryCorrect = false;
        boolean schemeCorrect = false;
        ce_element.removeAll();
        ce_circuit.removeAll();
        ce_input.removeAll();
        ce_output.removeAll();
        
        try {
            libraryCorrect = library != null ? ModelingEngine.checkLibrary(library) : false;
            schemeCorrect = libraryCorrect ? scheme != null ? ModelingEngine.checkScheme(library, scheme) : false : false;
        } catch (ModelingException e) {
            MainWindow.showMessage(e.getMessage(), "Error");
            e.printStackTrace();
        }
        
        if(libraryCorrect) {
            ce_type.setItems(library.getLibrary().keySet().toArray(new String[0]));
        }
        if(schemeCorrect) {
            ce_element.setItems(scheme.getElements().keySet().toArray(new String[0]));
            ce_circuit.setItems(scheme.getCircuits().keySet().toArray(new String[0]));
            ce_circuit2.setItems(ce_circuit.getItems());
            
            for(String elementName : scheme.getElements().keySet()) {
                Element element = library.getLibrary().get(scheme.getElements().get(elementName));
                for(int n = 0; n < element.getIcnt(); n++) {
                    ce_drain.add(new Contact(elementName, n).toString());
                }
                for(int n = element.getIcnt(); n < element.getIcnt() + element.getOcnt(); n++) {
                    ce_source.add(new Contact(elementName, n).toString());
                }
            }
            
            for(String inputName : scheme.getInputs()) {
                ce_input.add(inputName);
                ce_source.add(new Contact(inputName, -1).toString());
            }
            
            for(String outputName : scheme.getOutputs()) {
                ce_output.add(outputName);
                ce_drain.add(new Contact(outputName, -1).toString());
            }
            
            Yaml yaml = new Yaml();
            ce_view.setText(yaml.dump(scheme));
            initResetBtn.setEnabled(true);
            initResetMenuItem.setEnabled(true);                    
            
        }
    }
    
    public void schemeEditorAddElement() {
        if(ce_element.getText().length() == 0 || ce_type.getSelectionIndex() == -1) return;
        String elementName = ce_element.getText();
        String elementType = ce_type.getText();
                
        scheme.getElements().put(elementName, elementType);
        Element element = library.getLibrary().get(elementType);
        ce_element.add(elementName);
        for(int n = 0; n < element.getIcnt() + element.getOcnt() + element.getEcnt(); n++)
        {
            se_signal.add(new Contact(elementName, n).toString());
        }
        for(int n = 0; n < element.getIcnt(); n++) {
            ce_drain.add(new Contact(elementName, n).toString());
        }
        for(int n = element.getIcnt(); n < element.getIcnt() + element.getOcnt(); n++) {
            ce_source.add(new Contact(elementName, n).toString());
        }
        schemeEditorSchemeChanged();
    }
    
    public void schemeEditorDelElement() {
        if(ce_element.getSelectionIndex() == -1) return;
        String elementName = ce_element.getText();
        String elementType = scheme.getElements().get(elementName);
        Element element = library.getLibrary().get(elementType);
        
        ce_element.remove(elementName);
        scheme.getElements().remove(elementName);
        List<String> sourceFor = new LinkedList<String>();
  circ: for(String circuitName : scheme.getCircuits().keySet()) {
            Circuit circuit = scheme.getCircuits().get(circuitName);
            for(int n = element.getIcnt(); n < element.getIcnt() + element.getOcnt(); n++) {
                if(circuit.getContacts().contains(new Contact(elementName, n))) {
                    sourceFor.add(circuitName);
                    continue circ;
                }
            }
            for(int n = 0; n < element.getIcnt(); n++) {
                circuit.getContacts().remove(new Contact(elementName, n));
            }
        }
        for(String circuitName : sourceFor) {
            scheme.getCircuits().remove(circuitName);
            ce_circuit.remove(circuitName);
            ce_circuit2.remove(circuitName);
        }
        
        for(int n = 0; n < element.getIcnt() + element.getOcnt() + element.getEcnt(); n++)
        {
            se_signal.remove(new Contact(elementName, n).toString());
        }
        for(int n = 0; n < element.getIcnt(); n++)
        {
            ce_drain.remove(new Contact(elementName, n).toString());
        }
        for(int n = element.getIcnt(); n < element.getIcnt() + element.getOcnt(); n++)
        {
            ce_source.remove(new Contact(elementName, n).toString());
        }
        schemeEditorSchemeChanged();
        signalEditorSignalsChanged();
    }
    
    public void schemeEditorAddCircuit() {
        if(ce_circuit.getText().length() == 0 || ce_source.getSelectionIndex() == -1) return;
        String circuitName = ce_circuit.getText();
        ce_circuit.add(circuitName);
        ce_circuit2.add(circuitName);
        
        Contact contact = new Contact(ce_source.getText());
        if(scheme.getCircuits().containsKey(circuitName)) {
            Circuit circuit = scheme.getCircuits().get(circuitName);
            circuit.getContacts().set(0, contact);
            scheme.getCircuits().put(circuitName, circuit);
        } else {
            Circuit circuit = new Circuit();
            circuit.getContacts().add(contact);
            scheme.getCircuits().put(circuitName, circuit);
        }
        schemeEditorSchemeChanged();
    }
    
    public void schemeEditorDelCircuit() {
        if(ce_circuit.getSelectionIndex() == -1) return;
        String circuitName = ce_circuit.getText();
        ce_circuit.remove(circuitName);
        ce_circuit2.remove(circuitName);
        
        scheme.getCircuits().remove(circuitName);
        
        schemeEditorSchemeChanged();
    }
    
    public void schemeEditorAddContact() {
        if(ce_circuit2.getSelectionIndex() == -1 || ce_drain.getSelectionIndex() == -1) return;
        String circuitName = ce_circuit2.getText();
        String contactName = ce_drain.getText();
        
        scheme.getCircuits().get(circuitName).getContacts().add(new Contact(contactName));
        
        schemeEditorSchemeChanged();
    }
    
    public void schemeEditorDelContact() {
        if(ce_circuit2.getSelectionIndex() == -1 || ce_drain.getSelectionIndex() == -1) return;
        String circuitName = ce_circuit2.getText();
        String contactName = ce_drain.getText();
        
        scheme.getCircuits().get(circuitName).getContacts().remove(new Contact(contactName));
        
        schemeEditorSchemeChanged();
    }
    
    public void schemeEditorAddInput() {
        if(ce_input.getText().length() == 0) return;
        String inputName = ce_input.getText();
        scheme.getInputs().add(inputName);
        ce_input.add(inputName);
        Contact contact = new Contact(inputName, -1);
        ce_source.add(contact.toString());
        se_signal.add(contact.toString());
        
        schemeEditorSchemeChanged();
    }
    
    public void schemeEditorDelInput() {
        if(ce_input.getSelectionIndex() == -1) return;
        String inputName = ce_input.getText();
        Contact contact = new Contact(inputName, -1);
        
        List<String> sourceFor = new LinkedList<String>();
        for(String circuitName : scheme.getCircuits().keySet()) {
            Circuit circuit = scheme.getCircuits().get(circuitName);
            if(circuit.getContacts().contains(contact)) {
                sourceFor.add(circuitName);
            }
        }
        for(String circuitName : sourceFor) {
            scheme.getCircuits().remove(circuitName);
            ce_circuit.remove(circuitName);
            ce_circuit2.remove(circuitName);
        }
        
        scheme.getInputs().remove(inputName);
        ce_input.remove(inputName);
        ce_source.remove(contact.toString());
        se_signal.remove(contact.toString());
        schemeEditorSchemeChanged();
        signalEditorSignalsChanged();
    }
    
    public void schemeEditorAddOutput() {
        if(ce_output.getText().length() == 0) return;
        String outputName = ce_output.getText();
        scheme.getOutputs().add(outputName);
        ce_output.add(outputName);
        Contact contact = new Contact(outputName, -1);
        ce_drain.add(contact.toString());
        se_signal.add(contact.toString());
        schemeEditorSchemeChanged();
    }
    
    public void schemeEditorDelOutput() {
        if(ce_output.getSelectionIndex() == -1) return;
        String outputName = ce_output.getText();
        Contact contact = new Contact(outputName, -1);
        
        for(String circuitName : scheme.getCircuits().keySet()) {
            Circuit circuit = scheme.getCircuits().get(circuitName);
            circuit.getContacts().remove(contact);
        }
        
        scheme.getOutputs().remove(outputName);
        ce_output.remove(outputName);
        ce_drain.remove(contact.toString());
        se_signal.remove(contact.toString());
        
        schemeEditorSchemeChanged();
        signalEditorSignalsChanged();
    }
    
    public void schemeEditorSchemeChanged() {
        Yaml yaml = new Yaml();
        ce_view.setText(yaml.dump(scheme));
    }
    
    //// SIGNALS ////
    public void loadSignals() {
        final String signalsFilePath = dlgSignals.open();
        if (signalsFilePath != null) {
            
            Yaml yaml = new Yaml();
            InputStream stream = null;
            
            try {
                stream = new FileInputStream(signalsFilePath);
            } catch (FileNotFoundException ex) {
                showMessage(Messages.ERROR_FILE_NOT_FOUND + signalsFilePath, "Error");
            }
            try {
                signals = (SignalBundle) yaml.load(stream);
                stream.close();
            } catch(Exception ex) {
                showMessage(Messages.ERROR_WRONG_SIGNALS_DOCUMENT + signalsFilePath, "Error");
            }
            String message = Messages.SIGNALS_FILE_SELECTED + signalsFilePath;
            status(message);
            signalEditorPrepare();
            
            se_signal.select(-1);
            signalEditorSignalsChanged();
            signalEditorSelectionChanged();
        } else {
            status(Messages.SIGNALS_FILE_NOT_SELECTED);
        }
    }
    
    public void saveSignals() {
        final String signalsFilePath = dlgSignals.open();
        if (signalsFilePath != null) {
            Yaml yaml = new Yaml();
            BufferedWriter stream = null;
            try {
                stream = new BufferedWriter(new FileWriter(signalsFilePath));
                stream.write(yaml.dump(signals));
                stream.close();
            } catch (FileNotFoundException ex) {
                showMessage(Messages.ERROR_FILE_NOT_FOUND + signalsFilePath, "Error");
            } catch (IOException ex) {
                showMessage(Messages.ERROR_CANT_WRITE_TO_FILE + signalsFilePath, "Error");
            }
            String message = Messages.FILE_SAVED + signalsFilePath;
            status(message);
            signalEditorPrepare();
            se_signal.select(-1);
            signalEditorSignalsChanged();
            signalEditorSelectionChanged();
        } else {
            status(Messages.SIGNALS_FILE_NOT_SELECTED);
        }
    }
    
    public void signalEditorSetEnabled(boolean enabled) {
        se_signal.setEnabled(enabled);
        se_time.setEnabled(enabled);
        se_state.setEnabled(enabled);
        se_add.setEnabled(enabled);
        se_del.setEnabled(enabled);
        se_load.setEnabled(enabled);
    }
    
    public void signalEditorPrepare() {
        signalEditorSetEnabled(true);
        boolean libraryCorrect = false;
        boolean schemeCorrect = false;
        boolean signalsCorrect = false;
        se_signal.removeAll();
        
        try {
            libraryCorrect = library != null ? ModelingEngine.checkLibrary(library) : false;
            schemeCorrect = libraryCorrect ? scheme != null ? ModelingEngine.checkScheme(library, scheme) : false : false;
        } catch (ModelingException e) {
            MainWindow.showMessage(e.getMessage(), "Error");
            e.printStackTrace();
        }
        
        if(schemeCorrect) {
            for(String input : scheme.getInputs()) {
                se_signal.add(new Contact(input, -1).toString());
            }
            for(String output : scheme.getOutputs()) {
                se_signal.add(new Contact(output, -1).toString());
            }
            for(String elementName : scheme.getElements().keySet()) {
                Element element = library.getLibrary().get(scheme.getElements().get(elementName));
                for(int n = 0; n < element.getIcnt() + element.getOcnt() + element.getEcnt(); n++)
                {
                    se_signal.add(new Contact(elementName, n).toString());
                }
            }
            Yaml yaml = new Yaml();
            se_view.setText(yaml.dump(signals));
        }
        if(signalsCorrect) {
            Contact contact = new Contact(se_signal.getText());
            Signal signal = signals.getSignals().get(contact);
            if(signal != null) {
                StringBuilder sb_time = new StringBuilder();
                StringBuilder sb_state = new StringBuilder();
                for(Long key : signal.getSignalSet().keySet()) {
                    sb_time.append(key).append(' ');
                    sb_state.append(signal.getState(key)).append(' ');
                }
                se_time.setText(sb_time.toString());
                se_state.setText(sb_state.toString());
            } else {
                se_time.setText("");
                se_state.setText("");
            }
        }
    }
    
    private void signalEditorSelectionChanged() {
        if(se_signal.getSelectionIndex() == -1) return;
        Contact contact = new Contact(se_signal.getText());
        Signal signal = signals.getSignals().get(contact);
        if(signal != null) {
            StringBuilder sb_time = new StringBuilder();
            StringBuilder sb_state = new StringBuilder();
            for(Long key : signal.getSignalSet().keySet()) {
                sb_time.append(key).append(' ');
                sb_state.append(signal.getState(key)).append(' ');
            }
            se_time.setText(sb_time.toString());
            se_state.setText(sb_state.toString());
        } else {
            se_time.setText("");
            se_state.setText("");
        }
    }
    
    private void signalEditorSignalsChanged() {
        Yaml yaml = new Yaml();
        se_view.setText(yaml.dump(signals));
    }
    
    private void signalEditorAddSignal() {
        if(se_signal.getSelectionIndex() == -1) return;
        if(signals == null) signals = new SignalBundle();
        Contact contact = new Contact(se_signal.getText());
        Signal signal = new Signal();
        String text = se_time.getText().replaceAll("[^0-9]+", " ").trim();
        if(text.length() < 1) return;
        String[] time = text.split(" ");
        text = se_state.getText().replaceAll("[^0-9]+", " ").trim();
        if(text.length() < 1) return;
        String[] state = text.split(" ");
        for(int n = 0; n < Math.min(time.length, state.length); n++) {
            long ntime = time[n].length() > 0 ? Long.parseLong(time[n]) : -1;
            int nstate = state[n].length() > 0 ? Integer.parseInt(state[n]) : -1;
            signal.getSignalSet().put(ntime, nstate);
        }
        signals.getSignals().put(contact, signal);
        
        signalEditorSelectionChanged();
        signalEditorSignalsChanged();
    }
    
    private void signalEditorDelSignal() {
        if(se_signal.getSelectionIndex() == -1) return;
        if(signals == null) signals = new SignalBundle();
        Contact contact = new Contact(se_signal.getText());
        
        signals.getSignals().remove(contact);
        
        signalEditorSelectionChanged();
        signalEditorSignalsChanged();
    }
}