package cmsc170;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;


public class Board extends JPanel {
	/**
	 * Default Serial Version ID 
	 */
	private static final long serialVersionUID = 1L;
	TicTacToe game;
	/*
	 * Board Constructor
	 */
	Board(TicTacToe g) {
		this.game = g;
	}
	/**
	 * Draw the board
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(Color.WHITE);

		/* draw grids */
		g.setColor(Color.LIGHT_GRAY);
		for(int row = 1; row< TicTacToe.ROWS; ++row) {
			g.fillRoundRect(0, game.CELL_SIZE * row - game.GRID_WIDTH_HALF, game.CANVAS_WIDTH - 1, game.GRID_WIDTH, game.GRID_WIDTH, game.GRID_WIDTH);
		}

		for(int col = 1; col< TicTacToe.COLS; ++col) {
			g.fillRoundRect(game.CELL_SIZE * col - game.GRID_WIDTH_HALF,0, game.GRID_WIDTH, game.CANVAS_HEIGHT - 1, game.GRID_WIDTH, game.GRID_WIDTH);
		}

		/*draw tokens */
		Graphics2D g2d = (Graphics2D)g;
		g2d.setStroke(new BasicStroke(game.SYMBOL_STROKE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		for(int row = 0; row<TicTacToe.ROWS;row++) {
			for(int col = 0; col<TicTacToe.COLS; col++) {
				int x1 = col * game.CELL_SIZE + game.CELL_PADDING;
				int y1 = row * game.CELL_SIZE + game.CELL_PADDING;
				if(game.board[row][col] == Token.X) {
					g2d.setColor(Color.RED);
					int x2 = (col+1) * game.CELL_SIZE - game.CELL_PADDING;
					int y2 = (row+1) * game.CELL_SIZE - game.CELL_PADDING;

					g2d.drawLine(x1, y1, x2, y2);
					g2d.drawLine(x2, y1, x1, y2);
				} else if(game.board[row][col] == Token.O) {
					g2d.setColor(Color.BLACK);
					g2d.drawOval(x1, y1, game.SYMBOL_SIZE, game.SYMBOL_SIZE);
				}
			}
		}

		/* update status label*/
		if(game.gameState == State.PLAYING) {
			game.status.setForeground(Color.BLACK);

			if(game.player == Token.X) {
				game.status.setText("Player X's Turn");
			} else {
				game.status.setText("Player O's Turn");
			}
		} else if(game.gameState == State.DRAW) {
			game.status.setForeground(Color.BLACK);
			game.status.setText("DRAW!");
		} else if(game.gameState == State.WIN) {
			game.status.setForeground(Color.BLACK);
			game.status.setText("AI WON!");
		} else if(game.gameState == State.LOSE) {
			game.status.setForeground(Color.BLACK);
			game.status.setText("Player WON!!");
		}

		if(!TicTacToe.isValid) {
			game.status.setForeground(Color.BLACK);
			game.status.setText("Invalid Move!");
		}
	}
}
