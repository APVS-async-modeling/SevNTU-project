package asyncmod.ui.timediagrams;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class TimeDiagramsWindow extends Dialog {

	protected Object result;
	protected Shell shlTimeDiagramsWindow;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public TimeDiagramsWindow(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlTimeDiagramsWindow.open();
		shlTimeDiagramsWindow.layout();
		Display display = getParent().getDisplay();
		while (!shlTimeDiagramsWindow.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlTimeDiagramsWindow = new Shell(getParent(), getStyle());
		shlTimeDiagramsWindow.setSize(600, 600);
		shlTimeDiagramsWindow.setText("Time Diagrams Window");

	}

}
