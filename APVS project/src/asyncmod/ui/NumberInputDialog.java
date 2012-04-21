package asyncmod.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class NumberInputDialog extends Dialog {
  Long value;

  /**
   * @param parent
   */
  public NumberInputDialog(Shell parent) {
    super(parent);
  }

  /**
   * @param parent
   * @param style
   */
  public NumberInputDialog(Shell parent, int style) {
    super(parent, style);
  }

  /**
   * Makes the dialog visible.
   * 
   * @return
   */
  public Long open() {
    Shell parent = getParent();
    final Shell shell =
      new Shell(parent, SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL);
    shell.setText("Modeling time input");

    shell.setLayout(new GridLayout(2, true));

    Label label = new Label(shell, SWT.NULL);
    label.setText("Please enter new modeling time:");

    final Text text = new Text(shell, SWT.SINGLE | SWT.BORDER);

    final Button buttonOK = new Button(shell, SWT.PUSH);
    buttonOK.setText("Ok");
    buttonOK.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
    Button buttonCancel = new Button(shell, SWT.PUSH);
    buttonCancel.setText("Cancel");

    text.addListener(SWT.Modify, new Listener() {
      public void handleEvent(Event event) {
        try {
          value = Long.parseLong(text.getText());
          buttonOK.setEnabled(true);
        } catch (Exception e) {
          buttonOK.setEnabled(false);
        }
      }
    });

    buttonOK.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        shell.dispose();
      }
    });

    buttonCancel.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        value = null;
        shell.dispose();
      }
    });
    
    shell.addListener(SWT.Traverse, new Listener() {
      public void handleEvent(Event event) {
        if(event.detail == SWT.TRAVERSE_ESCAPE)
          event.doit = false;
      }
    });

    text.setText("");
    shell.pack();
    shell.open();

    Display display = parent.getDisplay();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }

    return value;
  }
}