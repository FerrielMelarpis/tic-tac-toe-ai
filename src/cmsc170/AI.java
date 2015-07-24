package cmsc170;

import java.util.ArrayList;
import java.util.List;


public class AI {
	int ROWS = TicTacToe.ROWS;
	int COLS  = TicTacToe.COLS;

	Token[][] board;
	Token token;
	Token oppToken;
	/*
	 * AI Constructor
	 */
	public AI(Token[][] game)  {
		board = game;
	}

	/**
	 * set the token to be used by the AI
	 * @param t
	 */
	public void setToken(Token t) {
		this.token = t;
		if(this.token == Token.X) {
			this.oppToken = Token.O;
		} else {
			this.oppToken = Token.X;
		}
	}

	/**
	 * ai's turn to make a move
	 * @return integer array
	 */
	int [] move() {
		int[] chosenMove= new int[2];
		int[] result = alphabeta(2, token, Integer.MIN_VALUE, Integer.MAX_VALUE);

		/* get coordinates of chosen move */
		chosenMove[0] = result[1];
		chosenMove[1] = result[2];

		return chosenMove;
	}
	/**
	 * MiniMax Algorithm with AlphaBeta Pruning 
	 * @param depth
	 * @param player
	 * @param alpha
	 * @param beta
	 * @return integer array {score, x, y}
	 */
	int[] alphabeta(int depth, Token player, int alpha, int beta) {
		// TODO Auto-generated method stub
		List<int[]>children;
		int score;
		int row=-1;
		int col = -1;
		int[] vector = new int[3];

		/*depth is 0 or the node is terminal*/
		if(depth == 0 || Actions().isEmpty()) {
			score = calculateScore();
			vector[0] = score;
			vector[1] = row;
			vector[2] = col;
			return vector;
		}

		/* AI's turn */
		if(player.equals(this.token)) {
			children = Actions();
			/*execute all possible moves */
			for(int[] child : children) {
				board[child[0]][child[1]] = player;

				/*compare alpha to utility value of move done*/
				vector = alphabeta(depth-1,this.oppToken,alpha,beta);
				if(vector[0] > alpha) {
					alpha = vector[0];
					row = child[0];
					col = child[1];
				}
				/*undo move*/
				board[child[0]][child[1]] = Token.EMPTY;
				/*cut off*/
				if(beta <= alpha)
					break;
			}
		} else {
			/*user's turn*/
			children = Actions();
			/*execute all possible moves*/
			for(int[] child : children) {
				board[child[0]][child[1]] = player;
				vector = alphabeta(depth-1,this.token,alpha,beta);
				/*compare utility of move done to beta's value*/
				if(vector[0] < beta) {
					beta = vector[0];
					row = child[0];
					row = child[1];
				}
				/*undo move*/
				board[child[0]][child[1]] = Token.EMPTY;
				/*cut-off*/
				if(beta <= alpha)
					break;
			}
		}

		/*score is alpha if it is the AI's move if not score is beta*/
		if(player == this.token)
			vector[0] = alpha;
		else
			vector[0] = beta;
		vector[1] = row;
		vector[2] = col;

		/*return score and chosen move's coordinate*/
		return vector;
	}

	/**
	 * Return all possible moves for the current board state
	 * @return
	 */
	private List<int[]> Actions() {
		List<int[]> moves = new ArrayList<int[]>(); // allocate List

		// Search for empty cells and add to the List
		for (int row = 0; row < ROWS; ++row) {
			for (int col = 0; col < COLS; ++col) {
				if (this.board[row][col] == Token.EMPTY) {
					moves.add(new int[] {row, col});
				}
			}
		}
		return moves;
	}
	/* method that calculate scores per row */
	private int calculateRows() {
		int score = 0;
		for(int row=0; row < 3; row++)
			score += evaluateLine(row, 0, row, 1, row, 2);
		return score;
	}
	/* method that calculate scores per column */
	private int calculateCols() {
		int score = 0;
		for(int col=0; col < 3; col++)
			score += evaluateLine(0, col, 1, col, 2, col);
		return score;
	}
	/* method that calculate scores per diagonal */
	private int calculateCross() {
		return (evaluateLine(0, 0, 1, 1, 2, 2) + evaluateLine(0, 2, 1, 1, 2, 0));
	}

	/**
	 * 1 line with ai's token = 1 point, with user's token = -1 point
	 * 1 line with 2 consecutive ai tokens = 10 points, with 2 consecutive user's token = -10 points
	 * 1 line with 3 consecutive ai tokens = 100 points, with 3 consecutive user's token = -100 points
	 * other scenarios gives 0 points
	 * @return ai score for the current board state
	 */
	private int calculateScore() {
		return (calculateRows() + calculateCols() + calculateCross());
	}

	/**
	 * checks 3 consecutive cells where you can win
	 * (3 Rows, 3 Columns, 2 Diagonals)calculateScore
	 * @return score for the current line
	 */
	private int evaluateLine(int row1, int col1, int row2, int col2, int row3, int col3) {
		int score = 0;

		/**
		 * check first cell
		 */
		if (this.board[row1][col1] == this.token) {
			score = 1;
		} else if (this.board[row1][col1] == oppToken) {
			score = -1;
		}

		/**
		 * check 2 consecutive cells
		 */
		if (this.board[row2][col2] == this.token) {
			if (score == 1) {
				score = 10;
			} else if (score == -1) {
				return 0;
			} else {
				score = 1;
			}
		} else if (this.board[row2][col2] == oppToken) {
			if (score == -1) {
				score = -10;
			} else if (score == 1) {
				return 0;
			} else {
				score = -1;
			}
		}

		/**
		 * check 3 consecutive cells
		 */
		if (this.board[row3][col3] == this.token) {
// 			if (score == 1) {
// 				score = 10;
// 			} else if (score == 10) {
// 				score = 100;
			if(score > 0) {
				score *= 10;
			} else if (score < 0) {
				return 0;
			} else {
				score = 1;
			}
		} else if (this.board[row3][col3] == oppToken) {
			/*if (score == -1) {
				score = -10;
			} else if (score == -10) {
			*/	score = -100;
			if(score < 0) {
				score *= 10;
			} else if (score > 1) {
				return 0;
			} else {
				score = -1;
			}
		}
		return score;
	}
}