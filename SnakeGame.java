import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Random;

/** Simple Snake game built with Java Swing. Arrow keys / WASD to move, SPACE to restart. */
public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private static final int CELL = 20, COLS = 25, ROWS = 25;
    private static final int DELAY = 110;

    private final LinkedList<Point> snake = new LinkedList<>();
    private final Random random = new Random();
    private final Timer timer = new Timer(DELAY, this);
    private Point food;
    private int dx, dy, nextDx, nextDy, score;
    private boolean gameOver;

    public SnakeGame() {
        setPreferredSize(new Dimension(COLS * CELL, ROWS * CELL));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        reset();
    }

    private void reset() {
        snake.clear();
        snake.add(new Point(COLS / 2, ROWS / 2));
        snake.add(new Point(COLS / 2 - 1, ROWS / 2));
        snake.add(new Point(COLS / 2 - 2, ROWS / 2));
        dx = nextDx = 1;
        dy = nextDy = 0;
        score = 0;
        gameOver = false;
        placeFood();
        timer.start();
    }

    private void placeFood() {
        do {
            food = new Point(random.nextInt(COLS), random.nextInt(ROWS));
        } while (snake.contains(food));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dx = nextDx;
        dy = nextDy;
        Point head = snake.getFirst();
        Point newHead = new Point(head.x + dx, head.y + dy);

        boolean hitWall = newHead.x < 0 || newHead.y < 0 || newHead.x >= COLS || newHead.y >= ROWS;
        if (hitWall || snake.contains(newHead)) {
            gameOver = true;
            timer.stop();
        } else {
            snake.addFirst(newHead);
            if (newHead.equals(food)) {
                score++;
                placeFood();
            } else {
                snake.removeLast();
            }
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.RED);
        g.fillOval(food.x * CELL, food.y * CELL, CELL, CELL);

        boolean head = true;
        for (Point p : snake) {
            g.setColor(head ? Color.GREEN : new Color(0, 170, 0));
            g.fillRect(p.x * CELL, p.y * CELL, CELL - 1, CELL - 1);
            head = false;
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("Score: " + score, 8, 18);

        if (gameOver) {
            g.setFont(new Font("SansSerif", Font.BOLD, 32));
            String msg = "Game Over";
            int w = g.getFontMetrics().stringWidth(msg);
            g.drawString(msg, (getWidth() - w) / 2, getHeight() / 2);
            g.setFont(new Font("SansSerif", Font.PLAIN, 16));
            String sub = "Press SPACE to restart";
            w = g.getFontMetrics().stringWidth(sub);
            g.drawString(sub, (getWidth() - w) / 2, getHeight() / 2 + 30);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP: case KeyEvent.VK_W:
                if (dy == 0) { nextDx = 0; nextDy = -1; } break;
            case KeyEvent.VK_DOWN: case KeyEvent.VK_S:
                if (dy == 0) { nextDx = 0; nextDy = 1; } break;
            case KeyEvent.VK_LEFT: case KeyEvent.VK_A:
                if (dx == 0) { nextDx = -1; nextDy = 0; } break;
            case KeyEvent.VK_RIGHT: case KeyEvent.VK_D:
                if (dx == 0) { nextDx = 1; nextDy = 0; } break;
            case KeyEvent.VK_SPACE:
                if (gameOver) reset(); break;
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Snake");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new SnakeGame());
            frame.pack();
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
