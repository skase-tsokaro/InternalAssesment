import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ShowProfitAction implements ActionListener {
    private final MainWindow mainWindow;

    public ShowProfitAction(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        mainWindow.openProfitWindow();
    }
}