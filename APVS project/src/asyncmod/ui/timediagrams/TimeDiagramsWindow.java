package asyncmod.ui.timediagrams;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import swing2swt.layout.BorderLayout;

public class TimeDiagramsWindow extends Dialog {

	public static int WIDTH = 600;
	public static int HEIGHT = 600;

	protected Shell timeDiagramsShell;

	private boolean isVisible = false;

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
		timeDiagramsShell.addListener(SWT.Traverse, new Listener() {
		    @Override
		    public void handleEvent(Event event) {
		    	// avoid to close time diagrams window by Esc key )
		        if (event.character == SWT.ESC)
		        {
		            event.doit = false;
		        }
		    }
		});
		timeDiagramsShell.addControlListener(new ControlListener() {
			
			@Override
			public void controlResized(ControlEvent e) {				
				WIDTH = timeDiagramsShell.getSize().x;
				HEIGHT = timeDiagramsShell.getSize().y;
			}
			
			@Override
			public void controlMoved(ControlEvent e) {
				
				
			}
		});
		
		timeDiagramsShell.open();
		timeDiagramsShell.layout();
		isVisible = true;
		Display display = getParent().getDisplay();
		while (!timeDiagramsShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return null;
	}

	public void show(int coordX, int coordY) {
		timeDiagramsShell.setBounds(coordX, coordY, WIDTH, HEIGHT);
		isVisible = true;
		timeDiagramsShell.setVisible(isVisible);
	}
	
	public void hide() {
		isVisible = false;
		timeDiagramsShell.setVisible(isVisible);
	}
	
	public boolean isVisible() {
		return isVisible;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents(int coordX, int coordY) {
		timeDiagramsShell = new Shell(getParent(), SWT.BORDER | SWT.RESIZE | SWT.TITLE);
		timeDiagramsShell.setModified(true);
		timeDiagramsShell.setBounds(coordX, coordY, WIDTH, HEIGHT);
		timeDiagramsShell.setText("Time Diagrams Window");
		timeDiagramsShell.setLayout(new BorderLayout(0, 0));

		Scale scale = new Scale(timeDiagramsShell, SWT.NONE);
		scale.setLayoutData(BorderLayout.SOUTH);

		SashForm sashForm = new SashForm(timeDiagramsShell, SWT.NONE);
		sashForm.setLayoutData(BorderLayout.CENTER);
		
		Canvas canvas = new Canvas(sashForm, SWT.NONE);

		Tree tree = new Tree(sashForm, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION);
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		
		initializeTree(tree);
		sashForm.setWeights(new int[] { 4, 1 });

	}
	
	public void setPosition(int coordX, int coordY){
		timeDiagramsShell.setBounds(coordX, coordY, WIDTH, HEIGHT);
	}

	private void initializeTree(Tree tree) {
		for (int i = 0; i < 12; i++) {
			TreeItem item = new TreeItem(tree, SWT.NONE);
			item.setText("Element " + i);
			for (int l = 0; l < 5; l++) {
				TreeItem litem = new TreeItem(item, SWT.NONE);
				litem.setText("Contact " + i);
			}

			tree.addSelectionListener(new SelectionListener() {
				private boolean isCurElementExpanded;

				public void widgetDefaultSelected(SelectionEvent event) {
					final TreeItem root = (TreeItem) event.item;
					isCurElementExpanded = !isCurElementExpanded;
					root.setExpanded(isCurElementExpanded);
				}

				public void widgetSelected(SelectionEvent event) {
					if (event.detail == SWT.CHECK) {
						final TreeItem root = (TreeItem) event.item;
						final boolean isChecked = root.getChecked();
						for (TreeItem childItem : root.getItems()) {
							childItem.setChecked(isChecked);
						}
					}
				}
			});
		}
	}
}
