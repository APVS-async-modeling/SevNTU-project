package asyncmod.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.wb.swt.SWTResourceManager;

public class MainWindow {

	protected Shell shell;
	private Composite composite_1;
	private Composite composite_2;
	private Label lblElementDescription;

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
		shell.setImage(SWTResourceManager.getImage(MainWindow.class, "/javax/swing/plaf/metal/icons/ocean/collapsed.gif"));
		shell.setSize(697, 436);
		shell.setText("APVS async modeling project");
		shell.setLayout(null);

		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);

		MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
		mntmFile.setText("File");

		Menu menu_1 = new Menu(mntmFile);
		mntmFile.setMenu(menu_1);

		MenuItem mntmOpen = new MenuItem(menu_1, SWT.NONE);
		mntmOpen.setText("Open...");

		MenuItem mntmSave = new MenuItem(menu_1, SWT.NONE);
		mntmSave.setText("Save...");

		MenuItem mntmClose = new MenuItem(menu_1, SWT.NONE);
		mntmClose.setText("Close");

		MenuItem mntmModeling = new MenuItem(menu, SWT.CASCADE);
		mntmModeling.setText("Modeling");

		Menu menu_3 = new Menu(mntmModeling);
		mntmModeling.setMenu(menu_3);

		MenuItem mntmStart = new MenuItem(menu_3, SWT.NONE);
		mntmStart.setText("Start");

		MenuItem mntmStep = new MenuItem(menu_3, SWT.NONE);
		mntmStep.setText("Step");

		MenuItem mntmStop = new MenuItem(menu_3, SWT.NONE);
		mntmStop.setText("Stop");

		MenuItem mntmAbout = new MenuItem(menu, SWT.CASCADE);
		mntmAbout.setText("About");

		Menu menu_2 = new Menu(mntmAbout);
		mntmAbout.setMenu(menu_2);

		MenuItem mntmNewItem = new MenuItem(menu_2, SWT.NONE);
		mntmNewItem.setText("Program");

		MenuItem mntmTeam = new MenuItem(menu_2, SWT.NONE);
		mntmTeam.setText("Team");

		ExpandBar bar = new ExpandBar(shell, SWT.BORDER | SWT.V_SCROLL);
		bar.setLocation(10, 24);
		bar.setSize(188, 195);

		Composite composite = new Composite(bar, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginLeft = layout.marginTop = layout.marginRight = layout.marginBottom = 8;
		layout.verticalSpacing = 10;
		composite.setLayout(layout);
		Label label;
		lblElementDescription = new Label(composite, SWT.NONE);
		lblElementDescription.setText("Element 1 description");
		ExpandItem item1 = new ExpandItem(bar, SWT.NONE, 0);
		item1.setExpanded(true);
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
		bar.setSpacing(5);

	}
}
