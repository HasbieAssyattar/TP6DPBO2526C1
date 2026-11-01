import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainMenu extends JPanel {
    private int width = 360;
    private int height = 640;
    private Image backgroundImage;

    public MainMenu(ActionListener onStart, ActionListener onExit) {
        setPreferredSize(new Dimension(width, height));
        setLayout(new GridBagLayout());

        try {
            backgroundImage = new ImageIcon(getClass().getResource("assets/background.png")).getImage();
        } catch (Exception e) {
            backgroundImage = null;
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Judul Game
        JLabel titleLabel = new JLabel("FLAPPY BIRD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(255, 215, 0)); // Warna emas / kuning
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 20, 60, 20);
        add(titleLabel, gbc);

        // Tombol Start Game
        JButton startButton = new JButton("START GAME");
        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        startButton.setBackground(new Color(46, 204, 113));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setPreferredSize(new Dimension(200, 45));
        startButton.addActionListener(onStart);
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 20, 10, 20);
        add(startButton, gbc);

        // Tombol Exit Game
        JButton exitButton = new JButton("EXIT");
        exitButton.setFont(new Font("Arial", Font.BOLD, 18));
        exitButton.setBackground(new Color(231, 76, 60));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setPreferredSize(new Dimension(200, 45));
        exitButton.addActionListener(onExit);
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 20, 10, 20);
        add(exitButton, gbc);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, width, height, null);
        } else {
            g.setColor(new Color(78, 192, 202));
            g.fillRect(0, 0, width, height);
        }
    }
}
