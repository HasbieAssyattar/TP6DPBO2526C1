import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class View extends JPanel {
    int width = 360;
    int height = 640;

    private Logic logic;
    private Image backgroundImage;
    private JLabel scoreLabel;

    public View(Logic logic) {
        this.logic = logic;
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.cyan);
        setLayout(new BorderLayout());

        // Menampilkan skor di bagian atas
        scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 26));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        add(scoreLabel, BorderLayout.NORTH);

        // Load background image jika ada
        try {
            backgroundImage = new ImageIcon(getClass().getResource("assets/background.png")).getImage();
        } catch (Exception e) {
            backgroundImage = null;
        }

        // Tambahkan fokus dan Key Listener untuk menerima input
        setFocusable(true);
        addKeyListener(logic);
    }

    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Render background image
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, width, height, null);
        }

        // Render player
        Player player = logic.getPlayer();
        if (player != null) {
            g.drawImage(player.getImage(), player.getPosX(), player.getPosY(), player.getWidth(), player.getHeight(), null);
        }

        // Render list of pipes
        ArrayList<Pipe> pipes = logic.getPipes();
        if (pipes != null) {
            for (int i = 0; i < pipes.size(); i++) {
                Pipe pipe = pipes.get(i);
                g.drawImage(pipe.getImage(), pipe.getPosX(), pipe.getPosY(), pipe.getWidth(), pipe.getHeight(), null);
            }
        }

        // Menampilkan pesan Game Over dan info restart saat kalah
        if (logic.isGameOver()) {
            g.setColor(new Color(0, 0, 0, 160));
            g.fillRect(0, 0, width, height);

            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            FontMetrics fm = g.getFontMetrics();
            String goText = "GAME OVER";
            g.drawString(goText, (width - fm.stringWidth(goText)) / 2, height / 2 - 30);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            fm = g.getFontMetrics();
            String scoreText = "Final Score: " + logic.getScore();
            g.drawString(scoreText, (width - fm.stringWidth(scoreText)) / 2, height / 2 + 10);

            String restartText = "Press 'R' to Restart";
            g.drawString(restartText, (width - fm.stringWidth(restartText)) / 2, height / 2 + 45);
        }
    }
}
