package controller;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import model.Board;
import model.Board.State;
import model.Game;
import model.Location;
import model.NotImplementedException;
import model.Player;

/**
 * A MinMaxAI is a controller that uses the minimax algorithm to select the next
 * move.  The minimax algorithm searches for the best possible next move, under
 * the assumption that your opponent will also always select the best possible
 * move.
 *
 * <p>The minimax algorithm assigns a score to each possible game configuration
 * g.  The score is assigned recursively as follows: if the game g is over and
 * the player has won, then the score is infinity.  If the game g is over and
 * the player has lost, then the score is negative infinity.  If the game is a
 * draw, then the score is 0.
 * 
 * <p>If the game is not over, then there are many possible moves that could be
 * made; each of these leads to a new game configuration g'.  We can
 * recursively find the score for each of these configurations.
 * 
 * <p>If it is the player's turn, then they will choose the action that
 * maximizes their score, so the score for g is the maximum of all the scores
 * of the g's.  However, if it is the opponent's turn, then the opponent will
 * try to minimize the score for the player, so the score for g is the
 * <em>minimum</em> of all of the scores of the g'.
 * 
 * <p>You can think of the game as defining a tree, where each node in the tree
 * represents a game configuration, and the children of g are all of the g'
 * reachable from g by taking a turn.  The minimax algorithm is then a
 * particular traversal of this tree.
 * 
 * <p>In practice, game trees can become very large, so we apply a few
 * strategies to narrow the set of paths that we search.  First, we can decide
 * to only consider certain kinds of moves.  For five-in-a-row, there are
 * typically at least 70 moves available at each step; but it's (usually) not
 * sensible to go on the opposite side of the board from where all of the other
 * pieces are; by restricting our search to only part of the board, we can
 * reduce the space considerably.
 * 
 * <p>A second strategy is that we can look only a few moves ahead instead of
 * planning all the way to the end of the game.  This requires us to be able to
 * estimate how "good" a given board looks for a player.
 * 
 * <p>This class implements the minimax algorithm with support for these two
 * strategies for reducing the search space.  The abstract method {@link
 * #moves(Board)} is used to list all of the moves that the AI is willing to
 * consider, while the abstract method {@link #estimate(Board)} returns
 * the estimation of how good the board is for the given player.
 */
public abstract class MinMaxAI extends Controller {

	private int numOfMoves;

	/**
	 * Return an estimate of how good the given board is for me.
	 * A result of infinity means I have won.  A result of negative infinity
	 * means that I have lost.
	 */
	protected abstract int estimate(Board b);

	/**
	 * Return the set of moves that the AI will consider when planning ahead.
	 * Must contain at least one move if there are any valid moves to make.
	 */
	protected abstract Iterable<Location> moves(Board b);

	/**
	 * Create an AI that will recursively search for the next move using the
	 * minimax algorithm.  When searching for a move, the algorithm will look
	 * depth moves into the future.
	 *
	 * <p>choosing a higher value for depth makes the AI smarter, but requires
	 * more time to select moves.
	 */
	protected MinMaxAI(Player me, int depth) {
		super(me);
		numOfMoves = depth ;
		// TODO Auto-generated method stub
		//throw new NotImplementedException();
	}

	/**
	 * Return the move that maximizes the score according to the minimax
	 * algorithm described above.
	 */
	
	protected @Override Location nextMove(Game g) {
		
		Iterable<Location> locs = moves(g.getBoard()) ;
		
		int temp = 0 ;
		int score = Integer.MIN_VALUE ;
		Location finalLoc = new Location(0,0);
		
		for( Location loc : locs ) {
			Board copyOfCurrent = g.getBoard().update(this.me, loc) ;
			//copyOfCurrent.update(this.me, loc) ;
			temp = bestMove(copyOfCurrent,1) ;
			if( score <= temp ) {
				score = temp ;
				finalLoc = loc ;
			}
		}
		
		return finalLoc ;
	}
	
	private int bestMove(Board b, int currentDepth) {
		
		int maxScore = Integer.MIN_VALUE;
		int minScore = Integer.MAX_VALUE ;
		int depth = currentDepth ;
		
		//if (b.getWinner() != null ) {
		if (b.getState() == Board.State.HAS_WINNER) {
			if (currentDepth % 2 == 0) 
				return Integer.MIN_VALUE ;
			else
				return Integer.MAX_VALUE;
		} else if (b.getState() == Board.State.DRAW)
			return 0 ;
		else if (currentDepth == numOfMoves) {
			return estimate(b) ;
		} else {
			Iterable<Location> currentMoves = moves(b) ;
			int currScore;
			
			for( Location loc : currentMoves ) {
				Board copyOfCurrent ;
				if (currentDepth % 2 == 1)
					copyOfCurrent = b.update(this.me.opponent(), loc) ;
				else
					copyOfCurrent = b.update(this.me, loc) ;
				
				currScore = bestMove(copyOfCurrent,currentDepth+1) ;
				
					if (minScore >= currScore)
						minScore = currScore ;
				
					if ( maxScore <= currScore )
						maxScore = currScore;
			}
		}
		
		if ( depth % 2 == 1 )
			return minScore ;
		else
			return maxScore ;
	}

//	protected @Override Location nextMove(Game g) {
//		
//		Game copyOfGame = g ;
//		List<Location> available = (List<Location>) moves( g.getBoard() ) ;
//		Location midLoc = new Location(Board.NUM_COLS/2, Board.NUM_ROWS/2 ) ; 
//
//		// wait a bit
//		delay();
//		
//		List<Location> quad = num(available, midLoc) ;
//		
//		int[] sz = new int[quad.size()] ;
//		Game[] ray = new Game[quad.size()];
//		int i = 0 ;
//		
//		for( Location loc : quad ) {
//			if ( copyOfGame.nextTurn() == this.me) {
//				copyOfGame.submitMove(this.me, loc);
//			} else {
//				copyOfGame.submitMove(this.me.opponent(), loc);
//				//this.numOfMoves-- ;
//			}
//			
//			sz[i] = estimate(copyOfGame.getBoard()) ; 
//			ray[i] = copyOfGame ;
//			i++;
//		}
//		
//		this.numOfMoves-- ;
//		
//		int temp = sz[0];
//		int pos = 0 ;
//		
//		// If the nextTurn in the game is for this smart AI, then it will look to maximize
//		// its score. If the next move is for the opponent of this smart AI, it will also
//		// look to maximize it's score and thus minimizing the score of this smart AI.
//		for( int i1 = 0; i1 < sz.length; i1++) {
//			int lg = Math.max(sz[i1], temp) ;
//			if (temp <= lg) 
//				pos = i1;
//		}
//		
//		if (numOfMoves == 0) {
//			return quad.get(pos) ;
//		} else
//			return nextMove(ray[pos]) ;
//
//		// TODO Auto-generated method stub
//		//throw new NotImplementedException();
//	}

	/* Function num returns a list with the least number of locations in it.
	 * This corresponds to the quadrant of the game in which the most number 
	 * of moves have previously been made.
	 */
	private static List<Location> num(List<Location> loc, Location d) {

		int quad1 = 0 ; int quad2 = 0 ; int quad3 = 0 ; int quad4 = 0 ;

		List<Location> qd1 = new ArrayList<Location>() ;
		List<Location> qd2 = new ArrayList<Location>() ;
		List<Location> qd3 = new ArrayList<Location>() ;
		List<Location> qd4 = new ArrayList<Location>() ;

		for( Location loc1 : loc ) {
			// If the current location is in the second quadrant
			if ( loc1.row < d.row) {
				// If this statement is true, location in second quadrant
				if ( loc1.col < d.col ) {
					quad2 += 1 ;
					qd2.add(loc1) ;
				}
				// Location is in the second quadrant
				else {
					quad3 += 1 ;
					qd3.add(loc1) ;
				}
			}
			// If the current location is in the first or fourth quadrant
			else if ( loc1.row > d.row )
				// If this statement is true, then the location is in the fourth quadrant
				if (loc1.col < d.col) {
					quad1 += 1 ;
					qd1.add(loc1) ;
				}
			// Location is in the first quadrant
				else {
					quad4 += 1 ;
					qd4.add(loc1) ;
				}
			else 
				quad1 += 1 ; quad2 += 1 ; quad3 += 1 ; quad4 += 1 ;
		}

		int mn = Math.min(Math.min(quad1, quad2), Math.min(quad3, quad4)) ;

		if ( mn == quad1 ) 
			return qd1 ;
		else if ( mn == quad2 ) 
			return qd2 ;
		else if ( mn == quad3 ) 
			return qd3 ;
		else
			return qd4 ;

	}
}
