import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RefreshAction implements ActionListener {
    private final MainWindow mainWindow;

    public RefreshAction(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        mainWindow.refreshData();
    }
}