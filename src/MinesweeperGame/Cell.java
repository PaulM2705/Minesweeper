package MinesweeperGame;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.Arrays;
import java.util.Objects;

public class Cell extends JButton {
    private final int row;
    private final int col;
    private int value;
    private boolean flagged;
    private final ActionListener actionListener;
    private final MouseAdapter mouseAdapter;
    
    private static final Cell[] reusableStorage = new Cell[8];

    public Cell(int row, int col, ActionListener actionListener, MouseAdapter mouseAdapter) {
        this.row = row;
        this.col = col;
        this.actionListener = actionListener;
        this.mouseAdapter = mouseAdapter;

        addActionListener(actionListener);
        addMouseListener(mouseAdapter);
        setText("");
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
    }

    public void reveal() {
        if (!flagged) {
            setEnabled(false);
            setText(isAMine() ? "\uD83D\uDCA3\u200B" : String.valueOf(value));
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
                    container[index++] = cells[rowValue][colValue]; // Assign the neighbor cell to the container array
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
}
