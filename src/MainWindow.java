package src;

import javax.swing.JFrame;

public class MainWindow {
    private JFrame window;

    public MainWindow() {
        initialize();
    }

    public void initialize() {
        window = new JFrame();
        this.window.setTitle("Game Ban Ga");
        this.window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.window.setSize(800,500);        
        this.window.setLocationRelativeTo(null);
        this.window.setResizable(false);
    }

    public void show() {
        this.window.setVisible(true);
    }
}
