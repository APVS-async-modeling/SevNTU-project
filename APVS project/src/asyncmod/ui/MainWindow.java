package asyncmod.ui;

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

public class MainWindow {

    protected Shell shell;

    private static final int WIDTH = 785;
    private static final int HEIGHT = 518;
    private static final int SPACE_BETWEEN_WINDOWS = 10;

    private Composite composite_1;
    private Composite composite_2;
    private Text statusPanel;
    private Text modelingResultsText;
    private Text errorsText;
    private Text warningText;

    private TimeDiagramsWindow timeDiagramsWindow;

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
        Display display = Display.getDefault();
        createContents();
        shell.open();
        shell.layout();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
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

            @Override
            public void controlResized(final ControlEvent e) {
                setTimeDiagramsWindowBounds();
            }

            @Override
            public void controlMoved(final ControlEvent e) {
                setTimeDiagramsWindowBounds();
            }

            private void setTimeDiagramsWindowBounds() {
                if (timeDiagramsWindow != null) {
                    int xCoord = getRightUpperCornerPosition().x
                            + SPACE_BETWEEN_WINDOWS;
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

        MenuItem mntmOpenLibraryFile = new MenuItem(menu_4, SWT.NONE);
        mntmOpenLibraryFile.setText("Open Library file...");

        MenuItem mntmOpenDiscreteModel = new MenuItem(menu_4, SWT.NONE);
        mntmOpenDiscreteModel.setText("Open Discrete Model file...");

        MenuItem mntmOpenSignalsFile = new MenuItem(menu_4, SWT.NONE);
        mntmOpenSignalsFile.setText("Open Signals file...");

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

        MenuItem mntmStep = new MenuItem(menu_3, SWT.NONE);
        mntmStep.setText("Step");

        MenuItem mntmFullRun = new MenuItem(menu_3, SWT.NONE);
        mntmFullRun.setText("Full Run");

        MenuItem mntmNewItem_1 = new MenuItem(menu_3, SWT.NONE);
        mntmNewItem_1.setText("Run Until...");

        MenuItem mntmReset = new MenuItem(menu_3, SWT.NONE);
        mntmReset.setText("Reset");

        MenuItem menuItemAbout = new MenuItem(menu, SWT.CASCADE);
        menuItemAbout.setText("About");

        Menu menu_2 = new Menu(menuItemAbout);
        menuItemAbout.setMenu(menu_2);

        MenuItem mntmNewItem = new MenuItem(menu_2, SWT.NONE);
        mntmNewItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                showMessage(AboutProgram.Text, "Information");
            }
        });
        mntmNewItem.setText("Program");

        MenuItem mntmTeam = new MenuItem(menu_2, SWT.NONE);
        mntmTeam.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                showMessage(AboutTeam.Text, "Information");
            }
        });
        mntmTeam.setText("Team");

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

        Button btnInitialState = new Button(ControlButtonsComposite, SWT.NONE);
        btnInitialState.setText("Initial State");

        Button btnNewButton = new Button(ControlButtonsComposite, SWT.NONE);
        btnNewButton.setText("Step");

        Button btnNewButton_1 = new Button(ControlButtonsComposite, SWT.NONE);
        btnNewButton_1.setText("Full Run");

        Button btnNewButton_2 = new Button(ControlButtonsComposite, SWT.NONE);
        btnNewButton_2.setText("Run Until...");

        Button btnNewButton_3 = new Button(ControlButtonsComposite, SWT.NONE);
        btnNewButton_3.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                final int coordX = getRightUpperCornerPosition().x
                        + SPACE_BETWEEN_WINDOWS;
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
        btnNewButton_3.setText("Time diagrams...");

        SashForm sashForm = new SashForm(mainComposite, SWT.NONE);
        sashForm.setLayoutData(BorderLayout.CENTER);

        Composite elementsComposite = new Composite(sashForm, SWT.BORDER);
        elementsComposite.setLayout(new FillLayout(SWT.HORIZONTAL));

        ExpandBar bar = new ExpandBar(elementsComposite, SWT.BORDER | SWT.V_SCROLL);

        // ExpandBar customization
        Composite composite = new Composite(bar, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginLeft = layout.marginTop = layout.marginRight = layout.marginBottom = 8;
        layout.verticalSpacing = 10;
        composite.setLayout(layout);
        Label label = new Label(composite, SWT.NONE);
        label.setText("Element 1 description");
        ExpandItem item1 = new ExpandItem(bar, SWT.NONE, 0);
        item1.setText("Element 1");
        item1.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
        item1.setControl(composite);

        composite_1 = new Composite(bar, SWT.NONE);
        layout = new GridLayout(2, false);
        layout.marginLeft = layout.marginTop = layout.marginRight = layout.marginBottom = 8;
        layout.verticalSpacing = 10;
        composite_1.setLayout(layout);
        label = new Label(composite_1, SWT.NONE);
        label.setText("Element 2 description");
        ExpandItem item2 = new ExpandItem(bar, SWT.NONE, 1);
        item2.setText("Element 2");
        item2.setHeight(composite_1.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
        item2.setControl(composite_1);
        new Label(composite_1, SWT.NONE);

        composite_2 = new Composite(bar, SWT.NONE);
        layout = new GridLayout(2, true);
        layout.marginLeft = layout.marginTop = layout.marginRight = layout.marginBottom = 8;
        layout.verticalSpacing = 10;
        composite_2.setLayout(layout);
        label = new Label(composite_2, SWT.NONE);
        label.setText("Element 3 description");
        ExpandItem item3 = new ExpandItem(bar, SWT.NONE, 2);
        item3.setText("Element 3");
        item3.setHeight(composite_2.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
        item3.setControl(composite_2);
        new Label(composite_2, SWT.NONE);
        bar.setSpacing(6);

        Composite modelingStateComposite = new Composite(sashForm, SWT.NONE);
        sashForm.setWeights(new int[] { 1, 3 });

        TabItem LogTab = new TabItem(mainWindowTabFolder, SWT.NONE);
        LogTab.setText("Log");

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

        modelingResultsText = new Text(modelingResultsComposite, SWT.BORDER);
        modelingResultsText.setEditable(false);

        TabItem errorsTab = new TabItem(logTabFolder, SWT.NONE);
        errorsTab.setText("Errors");

        Composite errorsComposite = new Composite(logTabFolder, SWT.NONE);
        errorsTab.setControl(errorsComposite);
        errorsComposite.setLayout(new FillLayout(SWT.HORIZONTAL));

        errorsText = new Text(errorsComposite, SWT.BORDER);
        errorsText.setEditable(false);

        TabItem warningsTab = new TabItem(logTabFolder, SWT.NONE);
        warningsTab.setText("Warnings");

        Composite warningsComposite = new Composite(logTabFolder, SWT.NONE);
        warningsTab.setControl(warningsComposite);
        warningsComposite.setLayout(new FillLayout(SWT.HORIZONTAL));

        warningText = new Text(warningsComposite, SWT.BORDER);
        warningText.setEditable(false);
    }

    public void showMessage(final String text, final String type) {

        int msgWindowType = 0;

        if (type.equals("Alert")) {
            msgWindowType |= SWT.ICON_WARNING;
        }
        if (type.equals("Information")) {
            msgWindowType |= SWT.ICON_INFORMATION;
        }

        MessageBox box = new MessageBox(shell, msgWindowType);
        box.setMessage(text);
        box.open();
    }

    public final Point getRightUpperCornerPosition() {
        int coordX = shell.getBounds().x + shell.getBounds().width;
        int coordY = shell.getBounds().y;
        Point point = new Point(coordX, coordY);
        return point;
    }
}
