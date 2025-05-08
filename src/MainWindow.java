package src;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;

public class MainWindow {
    private JFrame frame;

    public MainWindow() {
        initialize();
    }

    public void initialize() {
        frame = new JFrame();
        frame.setTitle("Game Ban Ga");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800,500);        
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));

        panel.setBackground(Color.black);

        Button button = new Button("Test");
        
        panel.add(button);

        frame.add(panel, BorderLayout.CENTER);

    }

    public void show() {
        this.frame.setVisible(true);
    }
}
