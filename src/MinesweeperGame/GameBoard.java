package MinesweeperGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GameBoard extends JPanel {
	private static final int MINE = 10;

	private int gridSize;
	private Cell[][] cells;
	private ScoreManager scoreManager;
	private JButton giveUpButton;
	private JButton backToMenuButton;

	public static Cell[] reusableStorage = new Cell[8];

	public GameBoard(int gridSize, ScoreManager scoreManager) {
		this.gridSize = gridSize;
		this.scoreManager = scoreManager;
		this.cells = new Cell[gridSize][gridSize];
		setLayout(new BorderLayout());
		initialize();

		setPreferredSize(new Dimension(1000, 800));
	}

	private void initialize() {
		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object source = e.getSource();
				if (source == giveUpButton) {
					revealBoardAndDisplay("You gave up.");
					scoreManager.resetScore();
				} else if (source == backToMenuButton) {
					SwingUtilities.getWindowAncestor(GameBoard.this).dispose();
					new GameBoardMenu();
				} else {
					handleCell((Cell) source);
				}
			}
		};

		giveUpButton = new JButton("Give Up");
		giveUpButton.setFont(new Font("Serif", Font.ITALIC, 20));

		backToMenuButton = new JButton("Back to Menu");
		backToMenuButton.setFont(new Font("Serif", Font.ITALIC, 20));

		giveUpButton.addActionListener(actionListener);
		backToMenuButton.addActionListener(actionListener);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(giveUpButton);
		buttonPanel.add(backToMenuButton);

		add(buttonPanel, BorderLayout.SOUTH);

		MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					handleRightClick((Cell) e.getSource());
				}
			}
		};

		JPanel gamePanel = new JPanel(new GridLayout(gridSize, gridSize));
		add(gamePanel, BorderLayout.CENTER);

		for (int row = 0; row < gridSize; row++) {
			for (int col = 0; col < gridSize; col++) {
				cells[row][col] = new Cell(row, col, actionListener, mouseAdapter);
				gamePanel.add(cells[row][col]);
			}
		}
		scoreManager.initializeScoreLabel();
		createMines();
	}

	public void createMines() {
		resetAllCells();

		final int mineCount = gridSize *gridSize / 40; // Bomben einstellen
		final Random random = new Random();

		Set<Integer> minePositions = new HashSet<>();
		while (minePositions.size() < mineCount) {
			int pos = random.nextInt(gridSize * gridSize);
			minePositions.add(pos);
		}

		for (int pos : minePositions) {
			int row = pos / gridSize;
			int col = pos % gridSize;
			cells[row][col].setValue(MINE);
		}

		for (int row = 0; row < gridSize; row++) {
			for (int col = 0; col < gridSize; col++) {
				if (!cells[row][col].isAMine()) {
					cells[row][col].updateNeighbourCount(cells, gridSize);
				}
			}
		}
	}

	public void revealBoardAndDisplay(String message) {
		for (int row = 0; row < gridSize; row++) {
			for (int col = 0; col < gridSize; col++) {
				if (cells[row][col].isEnabled()) {
					cells[row][col].reveal();
				}
			}
		}
		JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), message, "Game Over", JOptionPane.ERROR_MESSAGE);
		scoreManager.checkScore();
		createMines();
		checkForWinOrLoss();
	}

	private void handleCell(Cell cell) {
		if (!cell.isEnabled() || cell.isFlagged()) {
			return;
		}
		if (cell.isAMine()) {
			cell.setForeground(Color.RED);
			cell.reveal();
			scoreManager.updateScore(cell);
			JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "You clicked on a mine.", "Game Over", JOptionPane.ERROR_MESSAGE);
			resetAllCells();
			createMines();
		} else {
			cell.reveal();
			scoreManager.updateScore(cell);
			if (cell.getValue() == 0) {
				Set<Cell> positions = new HashSet<>();
				positions.add(cell);
				cascade(positions);
			}
		}
		checkForWinOrLoss();
	}

	private void handleRightClick(Cell cell) {
		if (cell.isEnabled()) {
			cell.toggleFlag();
		}
	}

	private void cascade(Set<Cell> positionsToClear) {
		while (!positionsToClear.isEmpty()) {
			Cell cell = positionsToClear.iterator().next();
			positionsToClear.remove(cell);
			cell.reveal();
			scoreManager.updateScore(cell);

			cell.getNeighbours(reusableStorage, cells, gridSize);
			for (Cell neighbour : reusableStorage) {
				if (neighbour == null) {
					break;
				}
				if (neighbour.getValue() == 0 && neighbour.isEnabled()) {
					positionsToClear.add(neighbour);
				} else if (neighbour.isEnabled()) {
					neighbour.reveal();
					scoreManager.updateScore(neighbour);
				}
			}
		}
	}

	private void checkForWinOrLoss() {
		boolean won = true;
		outer:
			for (int row = 0; row < gridSize; row++) {
				for (int col = 0; col < gridSize; col++) {
					if (cells[row][col].isEnabled() && !cells[row][col].isAMine()) {
						won = false;
						break outer;
					}
				}
			}
		if (won) {
			int extraPoints = scoreManager.calculateExtraPoints(cells, gridSize);
			scoreManager.addToScore(extraPoints);
			scoreManager.updateScoreLabel();

			JOptionPane.showMessageDialog(
					SwingUtilities.getWindowAncestor(this), "You have won! Extra points: " + extraPoints, "Congratulations",
					JOptionPane.INFORMATION_MESSAGE
					);

			scoreManager.checkScore();

			resetAllCells();
			createMines();
		}
	}

	private void resetAllCells() {
		for (int row = 0; row < gridSize; row++) {
			for (int col = 0; col < gridSize; col++) {
				cells[row][col].reset();
			}
		}
	}  @Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Image backgroundImage = new ImageIcon(getClass().getResource("Minesweeper.jpeg")).getImage();
		g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
	}
}
