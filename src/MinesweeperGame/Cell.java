package MinesweeperGame;

import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;
import java.util.Objects;

public class Cell extends JButton {
    private final int row;
    private final int col;
    private int value;
    private boolean flagged;
    private boolean revealed;
    private final ActionListener actionListener;
    private final MouseAdapter mouseAdapter;

    private static final Cell[] reusableStorage = new Cell[8];

    private static final Color LIGHT_BLUE = new Color(80, 187, 255);
    private static final Color DEFAULT_BACKGROUND = UIManager.getColor("Button.background");

    private Color numberColor = Color.BLACK;

    public Cell(int row, int col, ActionListener actionListener, MouseAdapter mouseAdapter) {
        this.row = row;
        this.col = col;
        this.actionListener = actionListener;
        this.mouseAdapter = mouseAdapter;

        addActionListener(actionListener);
        addMouseListener(mouseAdapter);
        setText("");
        setBackground(LIGHT_BLUE);

        setRoundedBorder();
    }

    private void setRoundedBorder() {
        Border roundedBorder = new LineBorder(DEFAULT_BACKGROUND, 1, true) {
            @Override
            public void paintBorder(java.awt.Component c, java.awt.Graphics g, int x, int y, int width, int height) {
                java.awt.Graphics2D g2d = (java.awt.Graphics2D) g.create();
                g2d.setColor(getLineColor());
                g2d.draw(new RoundRectangle2D.Double(x, y, width - 1, height - 1, 10, 10));
                g2d.dispose();
            }

            @Override
            public boolean isBorderOpaque() {
                return true;
            }
        };
        setBorder(roundedBorder);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isAMine() {
        return value == Constants.MINE;
    }

    public void reset() {
        setValue(0);
        setEnabled(true);
        setText("");
        setFlagged(false);
        setBackground(LIGHT_BLUE);
        revealed = false;
    }

    private Color NumberColor = Color.BLACK;

    public void setNumberColor(int value) {
        switch (value) {
            case 1:
                numberColor = new Color(111, 216, 244);
                break;
            case 2:
                numberColor = new Color(141, 229, 69);
                break;
            case 3:
                numberColor = new Color(253, 119, 149);
                break;
            case 4:
                numberColor = new Color(239, 20, 0);
                break;
            case 5:
                numberColor = new Color(0, 32, 240);
                break;
            default:
                numberColor = Color.BLACK; // Standard Schwarz
                break;
        }
        repaint();
    }

    public void reveal() {
        if (!flagged) {
            setEnabled(false);
            setBackground(DEFAULT_BACKGROUND);
            if (isAMine() && !revealed) {
                setText("\uD83D\uDCA3\u200B");
            } else {
                setText(String.valueOf(value));
                setNumberColor(value);
            }
            setFont(getFont().deriveFont(20f));
            revealed = true;
        }
    }

    public void updateNeighbourCount(Cell[][] cells, int gridSize) {
        getNeighbours(reusableStorage, cells, gridSize);
        for (Cell neighbour : reusableStorage) {
            if (neighbour == null) {
                break;
            }
            if (neighbour.isAMine()) {
                value++;
            }
        }
    }

    public void getNeighbours(Cell[] container, Cell[][] cells, int gridSize) {
        Arrays.fill(container, null);

        int index = 0;

        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            for (int colOffset = -1; colOffset <= 1; colOffset++) {
                if (rowOffset == 0 && colOffset == 0) {
                    continue;
                }

                int rowValue = row + rowOffset;
                int colValue = col + colOffset;

                if (rowValue >= 0 && rowValue < gridSize && colValue >= 0 && colValue < gridSize) {
                    container[index++] = cells[rowValue][colValue];
                }
            }
        }
    }

    public boolean isFlagged() {
        return flagged;
    }

    public void toggleFlag() {
        setFlagged(!flagged);
    }

    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
        setText(flagged ? "\uD83D\uDEA9" : "");

        if (flagged) {
            setBackground(new Color(255, 228, 159));
        } else {
            setBackground(LIGHT_BLUE);
        }
        setFont(getFont().deriveFont(30f));
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cell cell = (Cell) obj;
        return row == cell.row && col == cell.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();

        if (!getText().isEmpty() && !getText().equals("\uD83D\uDCA3\u200B")) {
            g2d.setColor(numberColor);
            g2d.setFont(getFont());
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2d.drawString(getText(), x, y);
        }

        if (getText().equals("\uD83D\uDEA9")) {
            g2d.setColor(Color.BLACK);
            g2d.setFont(getFont());
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2d.drawString(getText(), x, y);
        }

        if (isAMine() && revealed) {
            g2d.setColor(Color.BLACK);
            g2d.setFont(getFont());
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth("\uD83D\uDCA3\u200B")) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2d.drawString("\uD83D\uDCA3\u200B", x, y);
        }

        g2d.dispose();
    }
}
