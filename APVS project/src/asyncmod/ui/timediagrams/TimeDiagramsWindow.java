package asyncmod.ui.timediagrams;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;

public class TimeDiagramsWindow extends Dialog {

	protected Object result;
	protected Shell shlTimeDiagramsWindow;

	private boolean isOpened = false;
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public TimeDiagramsWindow(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open(int coordX, int coordY) {
		createContents(coordX, coordY);
		shlTimeDiagramsWindow.open();
		shlTimeDiagramsWindow.layout();
		isOpened = true;
		Display display = getParent().getDisplay();
		while (!shlTimeDiagramsWindow.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	public void close() {
		isOpened = false;
		shlTimeDiagramsWindow.close();
	}
	
	public boolean isOpened(){
		return isOpened;
	}
	/**
	 * Create contents of the dialog.
	 */
	private void createContents(int coordX, int coordY) {
		shlTimeDiagramsWindow = new Shell(getParent(), SWT.BORDER | SWT.RESIZE | SWT.TITLE | SWT.RIGHT_TO_LEFT);
		shlTimeDiagramsWindow.setModified(true);
		shlTimeDiagramsWindow.setBounds(coordX, coordY, 600, 600);
		shlTimeDiagramsWindow.setText("Time Diagrams Window");

	}

}
