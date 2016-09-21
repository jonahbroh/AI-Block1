import java.util.ArrayList;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.PriorityQueue;
import java.util.HashSet;
import java.util.*;

// SlidingBoard has a public field called size
// A size of 3 means a 3x3 board

// SlidingBoard has a method to getLegalMoves
//   ArrayList<SlidingMove> legalMoves = board.getLegalMoves();

// You can create possible moves using SlidingMove:
// This moves the piece at (row, col) into the empty slot
//   SlidingMove move = new SlidingMove(row, col);

// SlidingBoard can check a single SlidingMove for legality:
//   boolean legal = board.isLegalMove(move);

// SlidingBoard can check if a position is a winning one:
//   boolean hasWon = board.isSolved();

// SlidingBoard can perform a SlidingMove:
//   board.doMove(move);

// You can undo a move by saying the direction of the previous move
// For example, to undo the last move that moved a piece down into
// the empty space from above use:
//   board.undoMove(m, 0);

// You can dump the board to view with toString:
//   System.out.println(board);
class BoardNode{
  SlidingBoard board;
  ArrayDeque queue;
  int size;
  SlidingMove move;

  //Each node contains a board state and a queue of moves that lead to that board state
  public BoardNode(SlidingBoard sb, int s){
    this.board = new SlidingBoard(sb.size);
    board.setBoard(sb);
    this.size = s + 1;
    this.queue = new ArrayDeque<SlidingMove>();
  }

  public BoardNode(SlidingBoard sb, ArrayDeque q, int s, SlidingMove m){
    this.board = new SlidingBoard(sb.size);
    board.setBoard(sb);
    this.size = s + 1;
    this.move = m;
    this.queue = q.clone();
    // this.queue = new ArrayDeque<SlidingMove>();
    // this.queue = q;
  }

  public BoardNode(BoardNode bn){
    this.board = new SlidingBoard(bn.board.size);
    board.setBoard(bn.board);
    this.queue = bn.queue.clone();
    // this.queue = new ArrayDeque<SlidingMove>();
    // this.queue = bn.queue;
  }

  public void addMove(SlidingMove move){
    // System.out.println(queue);
    queue.add(move);
    // System.out.println(queue);
  }

  public int doMove(SlidingMove move){
    int i = board.doMove(move);
    // System.out.println(board.toString());
    return i;
  }

  public void undoMove(SlidingMove move, int dir){
    board.undoMove(move, dir);
  }
}

class AStarBot extends SlidingPlayer {
    Deque<SlidingMove> sol;
    HashSet<String> boardStates = new HashSet();

    // The constructor gets the initial board
    public AStarBot(SlidingBoard _sb) {
        super(_sb);
        sol = Solution(_sb);
    }

    public Deque<SlidingMove> Solution(SlidingBoard board) {
      //Begins with one node containing the initial state
      if(board.isSolved())
        return null;
      PriorityQueue<BoardNode> boardStack = new PriorityQueue<BoardNode>(new MoveComparator(board));
      BoardNode firstNode = new BoardNode(board, 0);
      boardStack.add(firstNode);
      //Maintains a HashSet containing already encountered board states
      boardStates.add(firstNode.board.toString());

      while(true){
        //Removes the head of the queue and prepares all possible moves
        // Collections.sort(boardStack, new MoveComparator(board));
        BoardNode nextBoard = boardStack.poll();
        ArrayList<SlidingMove> legalMoves = nextBoard.board.getLegalMoves();
        int i;
        for(i = 0; i < legalMoves.size(); i++){
          BoardNode newBoard = new BoardNode(nextBoard.board, nextBoard.queue, nextBoard.size, legalMoves.get(i));
          int und = newBoard.doMove(legalMoves.get(i));
          if(!boardStates.contains(newBoard.board.toString())){
            newBoard.addMove(legalMoves.get(i));
            boardStack.add(newBoard);
            boardStates.add(newBoard.board.toString());
            if(newBoard.board.isSolved()){
              return newBoard.queue;
            }
          }
        }
      }
    }

    // Perform a single move based on the current given board state
    public SlidingMove makeMove(SlidingBoard board) {
        System.out.println("Making " + sol.peek());
        System.out.println(board.toString());
        SlidingMove move = sol.remove();
        board.doMove(move);
        return move;
    }
}
class MoveComparator implements Comparator<BoardNode> {
  SlidingBoard board;

  MoveComparator(SlidingBoard b){
    board = new SlidingBoard(b.size);
    board.setBoard(b);
  }

  // Code from Aidan Hendrickson
  public int manhattan (int x1, int y1, int x2, int y2)
  {
    return Math.abs(x1-x2) + Math.abs(y1-y2);
  }
  //returns distance from number at x,y on b1 to same number on b2
  public int findDist (int x1, int y1, int[][] b1, int[][] b2)
  {
    int myNum = b1[x1][y1];
    for (int r = 0; r<b2.length; r++)
    {
      for (int c = 0; c<b2.length; c++)
      {
        if (b2[r][c] == myNum)
          return manhattan(x1,y1,r,c);
      }
    }
    return -1;
  }
  public int guessDistance(SlidingBoard b1, SlidingBoard b2)
  {
    int total = 0;
    for (int r = 0; r<b1.size; r++)
    {
      for (int c = 0; c<b1.size; c++)
      {
        if (b1.board[r][c] != b2.board[r][c])
        {
          int dist = findDist (r, c, b1.board, b2.board);
          total += dist;
        }
      }
    }
    return total;
  }

  //Compares BoardNodes based on their distance from the solution
  public int compare(BoardNode m1, BoardNode m2){
    SlidingBoard testBoard = new SlidingBoard(board.size);
    testBoard.setBoard(board);
    SlidingBoard solvedBoard = new SlidingBoard(board.size);
    int und = testBoard.doMove(m1.move);
    int dist1 = guessDistance(solvedBoard, testBoard) + m1.size;
    testBoard.undoMove(m1.move, und);
    und = testBoard.doMove(m2.move);
    int dist2 = guessDistance(solvedBoard,testBoard) + m2.size;
    return (dist2 - dist1);
  }
}
