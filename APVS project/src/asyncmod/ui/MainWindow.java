package asyncmod.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MenuItem;

public class MainWindow {

	protected Shell shlApvsAsyncModeling;

	/**
	 * Launch the application.
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
		shlApvsAsyncModeling.open();
		shlApvsAsyncModeling.layout();
		while (!shlApvsAsyncModeling.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlApvsAsyncModeling = new Shell();
		shlApvsAsyncModeling.setSize(697, 436);
		shlApvsAsyncModeling.setText("APVS async modeling project");
		
		Menu menu = new Menu(shlApvsAsyncModeling, SWT.BAR);
		shlApvsAsyncModeling.setMenuBar(menu);
		
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

	}
}
