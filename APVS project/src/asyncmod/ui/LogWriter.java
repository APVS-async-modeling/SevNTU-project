package asyncmod.ui;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

public class LogWriter extends BufferedWriter {

    public LogWriter(Writer arg0) {        
        super(arg0);
    }
    
    @Override
    public void write(String str) throws IOException {
        MainWindow.addToModelingResults("[Modeling] " + str);
        super.write(str);
    }
    
}
