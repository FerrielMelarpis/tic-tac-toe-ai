/**
 * @author Emmanuel C. Dunan
 * @author Ferriel Lisandro B. Melarpis
 * @program description : 
 * A tictactoe game with AI implemented
 * using MiniMax Agorithm with AlphaBeta Pruning
 */
package cmsc170;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public class TicTacToe extends JFrame{
	/**
	 * Default Serial Version ID
	 */
	private static final long serialVersionUID = 1L;
	public static final int ROWS = 3;  // ROWS by COLS cells
	public static final int COLS = 3;

	/* variables for drawing UI */
	public int CELL_SIZE = 150; 
	public int CANVAS_WIDTH = CELL_SIZE * COLS;  
	public int CANVAS_HEIGHT = CELL_SIZE * ROWS;
	public int GRID_WIDTH = 8;                   
	public int GRID_WIDTH_HALF = GRID_WIDTH / 2; 
	public int CELL_PADDING = CELL_SIZE / 6;
	public int SYMBOL_SIZE = CELL_SIZE - CELL_PADDING * 2; 
	public int SYMBOL_STROKE_WIDTH = 8; 

	Token[][] board = new Token[ROWS][COLS];
	Board canvas;
	Token player;
	State gameState;
	JLabel status = new JLabel("");
	JPanel topPanel = new JPanel();
	JButton start = new JButton("New Game");
	JButton about = new JButton("About");
	AI bot = new AI(board);
	static boolean isValid = true;

	Boolean playerTurn;
	
	/**
	 * TicTacToe constructor
	 */
	public TicTacToe() {
		canvas = new Board(this);
		canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
		
		topPanel.setSize(new Dimension(100, 100));
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				startGame();
			}
		});
		
		// process the image for the message box
		
		about.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				BufferedImage b = null;
				try{
					File f = new File("src/images/anonicon.jpg");
					b = ImageIO.read(f);
				} catch(FileNotFoundException e0) {
					System.out.println("Picture missing...");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				JOptionPane.showMessageDialog(
						canvas, 
						"AUTHORS\n"
						+ "Emmanuel C. Dunan\n"
						+ "Ferriel Lisandro B. Melarpis\n"
						+ "\nPROGRAM\n"
						+ "Tic Tac Toe game with AI opponent implemented using\n"
						+ "MiniMax Algorithm with AlphaBeta Pruning.\n", 
						"ABOUT", 
						JOptionPane.INFORMATION_MESSAGE,
						new ImageIcon(b));
			}
			
		});
		start.setBackground(Color.CYAN);
		about.setBackground(Color.CYAN);
		topPanel.add(start);
		topPanel.add(about);
		topPanel.setBackground(Color.WHITE);
		
		startGame();
		canvas.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int row = e.getY() / CELL_SIZE;
				int col = e.getX() / CELL_SIZE;

				if(gameState == State.PLAYING) {
					/* check if it is the user's turn to make a move */
					if(playerTurn) {
						/* check if the move executed is valid */
						if(row >= 0 && row < ROWS && col >= 0 && col < COLS && board[row][col] == Token.EMPTY) {
							board[row][col] = player;
							if(checkGame()) 
								gameState = State.LOSE;
							changePlayer();
							/* set flag to indicate AI's turn to move */
							playerTurn = false;
							isValid = true;
						} else {
							isValid = false;
							JOptionPane.showMessageDialog(null,
									"Invalid Move!",
									"Error",
									JOptionPane.ERROR_MESSAGE);
						}
					} 
					/* bot's turn to make a move */
					if(gameState == State.PLAYING && isValid) {		
						AIturn();
						if(checkGame()) {
							gameState = State.WIN;
						}
						changePlayer();
						/* set flag indicating it is the user's turn to make a move */
						playerTurn = true;
					}
				}
				repaint();

				BufferedImage b = null;
				try{
					File f = new File("src/images/trollicon.jpg");
					b = ImageIO.read(f);
				} catch(FileNotFoundException e0) {
					System.out.println("Picture missing...");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				if(gameState == State.DRAW) {
					JOptionPane.showMessageDialog(canvas,
							"Its a draw! You're pretty good at this.",
							"Good Game!",
							JOptionPane.INFORMATION_MESSAGE,
							new ImageIcon(b));
				} else if(gameState == State.LOSE) {
					JOptionPane.showMessageDialog(canvas,
							"You Won! Well played.",
							"Congratulations!",
							JOptionPane.INFORMATION_MESSAGE,
							new ImageIcon(b));
				} else if(gameState == State.WIN) {
					JOptionPane.showMessageDialog(canvas,
							"You Lose! HA-HA!",
							"Too bad! Train harder!",
							JOptionPane.INFORMATION_MESSAGE,
							new ImageIcon(b));
				}
			}
		});

		status = new JLabel("Click anywhere to Start!");
		status.setBorder(BorderFactory.createEmptyBorder(2,5,4,5));
		status.setBackground(Color.GREEN);

		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(topPanel, BorderLayout.NORTH);
		cp.add(canvas, BorderLayout.CENTER);
		cp.add(status, BorderLayout.SOUTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setTitle("Tic-Tac-Toe");
		setVisible(true);
		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension window = new Dimension(getSize().width, getSize().height);
		int x = screen.width - window.width;
		int y = screen.height - window.height;
		setLocation(x/2, y/2);
		setResizable(false);
	}

	/**
	 * Initialize the game
	 */
	protected void startGame() {
		// TODO Auto-generated method stub
		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLS; col++) {
				board[row][col] = Token.EMPTY;
			}
		}
		repaint();

		gameState = State.PLAYING;
		player = Token.X;
		int n = -1;
		BufferedImage b = null, b2 = null;
		try{
			File f = new File("src/images/playicon.png");
			b = ImageIO.read(f);
			File f2 = new File("src/images/trollicon.jpg");
			b2 = ImageIO.read(f2);
		} catch(FileNotFoundException e0) {
			System.out.println("Picture missing...");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while(n == -1) {
			n = JOptionPane.showOptionDialog(
					this, "Pick your turn.", "New Game",
					JOptionPane.YES_NO_OPTION, 
					JOptionPane.QUESTION_MESSAGE,
					new ImageIcon(b),
					new String[]{"I want to go first", "You go first"},
					"I want to go first");
			if(n == JOptionPane.CLOSED_OPTION) {
				JOptionPane.showMessageDialog(this,
						"Scared? Why not play?",
						"Error",
						JOptionPane.ERROR_MESSAGE,
						new ImageIcon(b2));
			}
		}
		playerTurn = true;
		if(n == JOptionPane.NO_OPTION) {
			AIturn();
			changePlayer();
			repaint();
		}
	}

	/*Sequence of commands to determine AI's move */
	public void AIturn() {
		bot.setToken(player);
		int[] move = bot.move();
		board[move[0]][move[1]] = player;
	}

	/* changes current token used in making a move */
	public void changePlayer() {
		if(player == Token.X) {
			player = Token.O;
		} else {
			player = Token.X;
		}
	}

	/*checks updates for game state */
	public boolean checkGame() {
		checkDraw();
		return checkWin(player);
	}

	/*Check if there is already a winner */
	public boolean checkWin(Token player) {

		/* check per rows */
		for(int i=0;i<3;i++) {
			if(player== board[i][0] && player== board[i][1] && player == board[i][2])
				return true;
		}

		/* check per rows */
		for(int i=0;i<3;i++) {
			if(player == board[0][i] && player== board[1][i] && player== board[2][i])
				return true;
		}

		/* check 1st diagonal */
		if(player == board[0][0] && player == board[1][1] && player == board[2][2])
			return true;

		/* check 2nd diagonal */
		if(player== board[0][2] && player == board[1][1] && player== board[2][0])
			return true;

		return false;
	}

	/* checks for draw*/
	public void checkDraw() {

		for(int x=0;x<3;x++) 
			for(int y=0;y<3;y++) {
				if(board[x][y] == Token.EMPTY)
					return;
			}

		gameState = State.DRAW;
	}

	public static void main(String[] args) {
		// new TicTacToe();
		/**
		 * Thread Safety utility for 
		 * multi-threading extension
		 * of the program
		 */
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				new TicTacToe();
			}
		});
	}
}