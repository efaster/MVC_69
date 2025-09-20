// MainGUI.java - Alternative main class to replace console Main.java
import javax.swing.SwingUtilities;

public class MainGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RegistrationGUI().setVisible(true);
        });
    }
}