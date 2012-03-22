package asyncmod.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

	private static final int InitialHeight = 518;
	private static final int initialWidth = 785;
	protected Shell shell;
	private Composite composite_1;
	private Composite composite_2;
	private Text txtStatusPanel;
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
	public void open() {
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
	protected void createContents() {
		shell = new Shell();
		shell.setSize(initialWidth, InitialHeight);
		shell.setText("APVS async modeling project");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));

		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);

		MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
		mntmFile.setText("File");

		Menu menu_1 = new Menu(mntmFile);
		mntmFile.setMenu(menu_1);

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
		mntmClose.setText("Exit");

		MenuItem mntmModeling = new MenuItem(menu, SWT.CASCADE);
		mntmModeling.setText("Modeling");

		Menu menu_3 = new Menu(mntmModeling);
		mntmModeling.setMenu(menu_3);

		MenuItem mntmAbout = new MenuItem(menu, SWT.CASCADE);
		mntmAbout.setText("About");

		Menu menu_2 = new Menu(mntmAbout);
		mntmAbout.setMenu(menu_2);

		MenuItem mntmNewItem = new MenuItem(menu_2, SWT.NONE);
		mntmNewItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				showMessage(AboutProgram.Text, "Information");
			}
		});
		mntmNewItem.setText("Program");

		MenuItem mntmTeam = new MenuItem(menu_2, SWT.NONE);
		mntmTeam.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				showMessage(AboutTeam.Text, "Information");
			}
		});
		mntmTeam.setText("Team");

		TabFolder tabFolder = new TabFolder(shell, SWT.NONE);

		TabItem tbtmMainWindow = new TabItem(tabFolder, SWT.NONE);
		tbtmMainWindow.setText("Main Window");

		Composite composite1 = new Composite(tabFolder, SWT.NONE);
		tbtmMainWindow.setControl(composite1);
		composite1.setLayout(new BorderLayout(0, 0));

		txtStatusPanel = new Text(composite1, SWT.BORDER);
		txtStatusPanel.setToolTipText("See \"log\" for more details");
		txtStatusPanel.setEditable(false);
		txtStatusPanel.setText("Status panel");
		txtStatusPanel.setLayoutData(BorderLayout.SOUTH);

		Composite ControlButtonsComposite = new Composite(composite1,
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
				int coordX = shell.getBounds().x + shell.getBounds().width + 7;
				int coordY = shell.getBounds().y;
				if (timeDiagramsWindow == null) {
					timeDiagramsWindow = new TimeDiagramsWindow(shell,SWT.NONE);
					timeDiagramsWindow.open(coordX, coordY);
				} else {
					if(timeDiagramsWindow.isOpened()){
						timeDiagramsWindow.close();
					} else {
						timeDiagramsWindow.open(coordX, coordY);
					}
				}
			}
		});
		btnNewButton_3.setText("Time diagrams...");

		//btnNewButton_3.setImage(SWTResourceManager.getImage(MainWindow.class,"icons\\Nirvana.ico"));
		
		Composite ElementsComposite = new Composite(composite1, SWT.NONE);
		ElementsComposite.setLayoutData(BorderLayout.WEST);
		ElementsComposite.setLayout(new FillLayout(SWT.HORIZONTAL));

		ExpandBar bar = new ExpandBar(ElementsComposite, SWT.BORDER
				| SWT.V_SCROLL);

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

		Composite composite_4 = new Composite(composite1, SWT.BORDER);
		composite_4.setLayoutData(BorderLayout.EAST);

		Composite composite_5 = new Composite(composite1, SWT.BORDER);
		composite_5.setLayoutData(BorderLayout.CENTER);

		TabItem tbtmLog = new TabItem(tabFolder, SWT.NONE);
		tbtmLog.setText("Log");

		Composite composite_3 = new Composite(tabFolder, SWT.NONE);
		tbtmLog.setControl(composite_3);
		composite_3.setLayout(new BorderLayout(0, 0));

		TabFolder tabFolder_1 = new TabFolder(composite_3, SWT.BOTTOM);
		tabFolder_1.setLayoutData(BorderLayout.CENTER);

		TabItem tbtmNewItem = new TabItem(tabFolder_1, SWT.NONE);
		tbtmNewItem.setText("Modeling results");

		Composite modelingResultsComposite = new Composite(tabFolder_1,
				SWT.NONE);
		tbtmNewItem.setControl(modelingResultsComposite);
		modelingResultsComposite.setLayout(new FillLayout(SWT.HORIZONTAL));

		modelingResultsText = new Text(modelingResultsComposite, SWT.BORDER);
		modelingResultsText.setEditable(false);

		TabItem tbtmNewItem_1 = new TabItem(tabFolder_1, SWT.NONE);
		tbtmNewItem_1.setText("Errors");

		Composite errorsComposite = new Composite(tabFolder_1, SWT.NONE);
		tbtmNewItem_1.setControl(errorsComposite);
		errorsComposite.setLayout(new FillLayout(SWT.HORIZONTAL));

		errorsText = new Text(errorsComposite, SWT.BORDER);
		errorsText.setEditable(false);

		TabItem tbtmNewItem_2 = new TabItem(tabFolder_1, SWT.NONE);
		tbtmNewItem_2.setText("Warnings");

		Composite warningsComposite = new Composite(tabFolder_1, SWT.NONE);
		tbtmNewItem_2.setControl(warningsComposite);
		warningsComposite.setLayout(new FillLayout(SWT.HORIZONTAL));

		warningText = new Text(warningsComposite, SWT.BORDER);
		warningText.setEditable(false);
	}
	
	
    public void showMessage(String text, String type)
    {       
        
        int msgWindowType = 0;
                
        if(type.equals("Alert"))msgWindowType|=SWT.ICON_WARNING;
        if(type.equals("Information"))msgWindowType|=SWT.ICON_INFORMATION;
        
        MessageBox box = new MessageBox(shell, msgWindowType);
        box.setMessage(text);
        box.open();
    }
	
}
