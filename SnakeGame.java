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
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Colourful checkerboard background
        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                g2.setColor((x + y) % 2 == 0 ? new Color(30, 30, 70) : new Color(40, 40, 90));
                g2.fillRect(x * CELL, y * CELL, CELL, CELL);
            }
        }

        // Food: glowing red/orange apple
        int fx = food.x * CELL, fy = food.y * CELL;
        g2.setColor(new Color(255, 120, 0, 90));
        g2.fillOval(fx - 3, fy - 3, CELL + 6, CELL + 6);
        g2.setPaint(new GradientPaint(fx, fy, new Color(255, 90, 90), fx + CELL, fy + CELL, new Color(200, 0, 40)));
        g2.fillOval(fx + 1, fy + 1, CELL - 2, CELL - 2);
        g2.setColor(new Color(60, 200, 60));
        g2.fillOval(fx + CELL / 2, fy - 2, 6, 6);

        // Snake: rainbow body that shifts along its length, with eyes on the head
        int i = 0;
        for (Point p : snake) {
            float hue = ((i * 0.04f) + score * 0.05f) % 1f;
            g2.setColor(Color.getHSBColor(hue, 0.85f, 1f));
            g2.fillRoundRect(p.x * CELL + 1, p.y * CELL + 1, CELL - 2, CELL - 2, 8, 8);
            if (i == 0) {
                g2.setColor(Color.WHITE);
                int ex = p.x * CELL, ey = p.y * CELL;
                g2.fillOval(ex + 4, ey + 5, 5, 5);
                g2.fillOval(ex + 11, ey + 5, 5, 5);
                g2.setColor(Color.BLACK);
                g2.fillOval(ex + 5 + dx, ey + 6 + dy, 3, 3);
                g2.fillOval(ex + 12 + dx, ey + 6 + dy, 3, 3);
            }
            i++;
        }

        g.setColor(new Color(255, 220, 80));
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("Score: " + score, 8, 20);

        if (gameOver) {
            g.setColor(new Color(255, 90, 120));
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
