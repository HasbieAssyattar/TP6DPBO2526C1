import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class App {
    public static void main(String[] args) {
        JFrame frame = new JFrame("FlappyBird");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(360, 640);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        // Mengatur navigasi antara Main Menu dan layar Game
        CardLayout cardLayout = new CardLayout();
        JPanel rootPanel = new JPanel(cardLayout);

        Logic logika = new Logic();
        View tampilan = new View(logika);
        logika.setView(tampilan);

        // Action Listener untuk tombol Start Game
        ActionListener onStart = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(rootPanel, "GAME");
                tampilan.requestFocus();
                logika.startGame();
            }
        };

        // Action Listener untuk tombol Exit
        ActionListener onExit = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        };

        // Tampilan menu utama awal
        MainMenu menu = new MainMenu(onStart, onExit);

        rootPanel.add(menu, "MENU");
        rootPanel.add(tampilan, "GAME");

        frame.add(rootPanel);
        frame.pack();
        frame.setVisible(true);
    }
}
