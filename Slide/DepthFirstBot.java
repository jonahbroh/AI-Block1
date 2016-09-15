import java.util.ArrayList;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.HashSet;

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

  //Each node contains a board state and a queue of moves that lead to that board state
  public BoardNode(SlidingBoard sb){
    this.board = new SlidingBoard(sb.size);
    board.setBoard(sb);
    this.queue = new ArrayDeque<SlidingMove>();
  }

  public BoardNode(SlidingBoard sb, ArrayDeque q){
    this.board = new SlidingBoard(sb.size);
    board.setBoard(sb);
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

class DepthFirstBot extends SlidingPlayer {
    Deque<SlidingMove> sol;
    HashSet<String> boardStates = new HashSet();

    // The constructor gets the initial board
    public DepthFirstBot(SlidingBoard _sb) {
        super(_sb);
        sol = Solution(_sb);
    }

    public Deque<SlidingMove> Solution(SlidingBoard board) {
      //Begins with one node containing the initial state
      if(board.isSolved())
        return null;
      Deque<BoardNode> boardStack = new ArrayDeque<BoardNode>();
      BoardNode firstNode = new BoardNode(board);
      boardStack.push(firstNode);
      //Maintains a HashSet containing already encountered board states
      boardStates.add(firstNode.board.toString());

      while(true){
        //Removes the head of the queue and prepares all possible moves
        BoardNode nextBoard = boardStack.pop();
        ArrayList<SlidingMove> legalMoves = nextBoard.board.getLegalMoves();
        int i;
        for(i = 0; i < legalMoves.size(); i++){
          BoardNode newBoard = new BoardNode(nextBoard.board, nextBoard.queue);
          int und = newBoard.doMove(legalMoves.get(i));
          if(!boardStates.contains(newBoard.board.toString())){
            newBoard.addMove(legalMoves.get(i));
            boardStack.push(newBoard);
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
