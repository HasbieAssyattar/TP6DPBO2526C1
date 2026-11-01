import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import javax.sound.sampled.*;

public class Logic implements ActionListener, KeyListener {
    int frameWidth = 360;
    int frameHeight = 640;

    int playerStartPosX = frameWidth / 2;
    int playerStartPosY = frameHeight / 2;
    int playerWidth = 34;
    int playerHeight = 24;

    // Atribut posisi dan ukuran pipa
    int pipeStartPosX = frameWidth;
    int pipeStartPosY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    Timer gameLoop;
    Timer pipesCooldown;
    int gravity = 1;
    int pipeVelocityX = -2;

    View view;
    Image birdImage;
    Player player;

    Image lowerPipeImage;
    Image upperPipeImage;
    ArrayList<Pipe> pipes;

    // Status game dan skor
    private int score = 0;
    private boolean gameOver = false;
    private boolean gameStarted = false;

    // Audio latar belakang (BGM)
    private Clip bgmClip;

    public Logic() {
        try {
            birdImage = new ImageIcon(getClass().getResource("assets/bird.png")).getImage();
        } catch (Exception e) {
            birdImage = null;
        }
        player = new Player(playerStartPosX, playerStartPosY, playerWidth, playerHeight, birdImage);

        try {
            lowerPipeImage = new ImageIcon(getClass().getResource("assets/lowerPipe.png")).getImage();
            upperPipeImage = new ImageIcon(getClass().getResource("assets/upperPipe.png")).getImage();
        } catch (Exception e) {
            lowerPipeImage = null;
            upperPipeImage = null;
        }

        pipes = new ArrayList<Pipe>();

        // Timer untuk spawn pipa setiap 1.5 detik
        pipesCooldown = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                placePipes();
            }
        });

        // Timer loop permainan (60 FPS)
        gameLoop = new Timer(1000 / 60, this);

        // Inisialisasi Audio BGM
        initAudio();
    }

    private void initAudio() {
        try {
            URL soundURL = getClass().getResource("assets/bgm.wav");
            if (soundURL != null) {
                AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL);
                bgmClip = AudioSystem.getClip();
                bgmClip.open(ais);
            }
        } catch (Exception e) {
            System.out.println("Audio info: " + e.getMessage());
        }
    }

    public void playBGM() {
        try {
            if (bgmClip != null) {
                bgmClip.setFramePosition(0);
                bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        } catch (Exception e) {
            // Abaikan jika audio tidak tersedia
        }
    }

    public void stopBGM() {
        try {
            if (bgmClip != null && bgmClip.isRunning()) {
                bgmClip.stop();
            }
        } catch (Exception e) {
            // Abaikan jika audio tidak tersedia
        }
    }

    public void setView(View view) {
        this.view = view;
    }

    public Player getPlayer() {
        return player;
    }

    public ArrayList<Pipe> getPipes() {
        return pipes;
    }

    public int getScore() {
        return score;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    // Mulai game dari menu utama
    public void startGame() {
        gameStarted = true;
        restart();
    }

    // Mengulang game kembali dari awal
    public void restart() {
        player.setPosX(playerStartPosX);
        player.setPosY(playerStartPosY);
        player.setVelocityY(0);
        pipes.clear();
        score = 0;
        gameOver = false;

        if (view != null) {
            view.updateScore(score);
            view.repaint();
        }

        pipesCooldown.start();
        gameLoop.start();
        playBGM();
    }

    // Deteksi tabrakan player dengan pipa
    public boolean checkCollision(Player a, Pipe b) {
        return a.getPosX() < b.getPosX() + b.getWidth() &&
               a.getPosX() + a.getWidth() > b.getPosX() &&
               a.getPosY() < b.getPosY() + b.getHeight() &&
               a.getPosY() + a.getHeight() > b.getPosY();
    }

    private void triggerGameOver() {
        gameOver = true;
        gameLoop.stop();
        pipesCooldown.stop();
        stopBGM();
        if (view != null) {
            view.repaint();
        }
    }

    public void move() {
        if (gameOver) {
            return;
        }

        // Pergerakan player vertikal
        player.setVelocityY(player.getVelocityY() + gravity);
        player.setPosY(player.getPosY() + player.getVelocityY());
        player.setPosY(Math.max(player.getPosY(), 0));

        // Cek jika burung jatuh menyentuh batas bawah (kalah)
        if (player.getPosY() + player.getHeight() >= frameHeight) {
            triggerGameOver();
            return;
        }

        // Pergerakan pipa, pengecekan tabrakan, dan penambahan skor
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.setPosX(pipe.getPosX() + pipeVelocityX);

            // Cek jika burung menabrak pipa (kalah)
            if (checkCollision(player, pipe)) {
                triggerGameOver();
                return;
            }

            // Tambah skor jika berhasil melewati pipa
            if (!pipe.isPassed() && player.getPosX() > pipe.getPosX() + pipe.getWidth()) {
                pipe.setPassed(true);
                // Ditambah sekali untuk setiap pasang pipa
                if (i % 2 == 0) {
                    score++;
                    if (view != null) {
                        view.updateScore(score);
                    }
                }
            }
        }
    }

    public void placePipes() {
        int randomPosY = (int) (pipeStartPosY - (pipeHeight / 4) - Math.random() * (pipeHeight / 2));
        int openingSpace = frameHeight / 4;

        Pipe upperPipe = new Pipe(pipeStartPosX, randomPosY, pipeWidth, pipeHeight, upperPipeImage);
        pipes.add(upperPipe);

        Pipe lowerPipe = new Pipe(pipeStartPosX, randomPosY + openingSpace + pipeHeight, pipeWidth, pipeHeight, lowerPipeImage);
        pipes.add(lowerPipe);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        if (view != null) {
            view.repaint();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Tombol Spasi untuk melompat (hanya jika game sedang berjalan)
        if (e.getKeyCode() == KeyEvent.VK_SPACE && !gameOver && gameStarted) {
            player.setVelocityY(-10);
        }

        // Tombol R untuk restart game saat Game Over
        if (e.getKeyCode() == KeyEvent.VK_R && gameOver) {
            restart();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
