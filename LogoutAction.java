import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogoutAction implements ActionListener {
    private final MainWindow mainWindow;

    public LogoutAction(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        mainWindow.logOut();
    }
}