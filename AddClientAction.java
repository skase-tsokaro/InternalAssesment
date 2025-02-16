import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddClientAction implements ActionListener {
    private final MainWindow mainWindow;

    public AddClientAction(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        mainWindow.openAddClientWindow();
    }
}