import java.util.Random;
import java.util.ArrayList;

public class RandomBot extends OthelloPlayer {

    // makeMove gets a current OthelloBoard game state as input
    // and then returns an OthelloMove object

    // Your bot knows what color it is playing
    //    because it has a playerColor int field

    // Your bot can get an ArrayList of legal moves:
    //    ArrayList<OthelloMove> moves = board.legalMoves(playerColor);

    // The constructor for OthelloMove needs the row, col, and player color ints.
    // For example, play your token in row 1, col 2:
    //   OthelloMove m = new OthelloMove(1, 2, playerColor);

    // OthelloBoard objects have a public size field defining the
    // size of the game board:
    //   board.size

    // You can ask the OthelloBoard if a particular OthelloMove
    //  flanks in a certain direction.
    // For example:
    //  board.flanksLeft(m) will return true if you can capture pieces to the left of move, m

    // You can ask the board what the current score is.
    //  This is just the difference in checker counts
    //  return the point differential in black's favor
    //  +3 means black is up by 3
    //  -5 means white is up by 5
    // int score = board.getBoardScore();

    // OthelloBoard has a toString:
    //  System.out.println(board);

    // OthelloPlayer superclass has a method to get the color for your opponent:
    //  int opponentColor = getOpponentColor();


    public AssholeBot(Integer _color) {
        super(_color);
    }

    public OthelloMove AssholeMove(MoveNode move){
      int moveCount = 0;
      int maxCount = 0;
      OthelloMove finalMove;
      for(OthelloMove m : move.
        for(int i = 0; i < 6; i++){

        }
    }

    public OthelloBoard cloneBoard(OthelloBoard b){
      OthelloBoard board = new OthelloBoard(b.size(), false);
      board.board = b.board.clone();
      return board;
    }


    //Needs global variables:
    //OUR_AVERAGE_TIME - average time of our move
    //THEIR_AVERAGE_TIME - average time of their move
    //MOVE_COUNT - incremented every move
    //END_TIME - time at the end of the last move
    //START_TIME - time at the start of the current move
    //DEPTH - depth
    //ASSHOLE_COUNT - number of turns asshole mode has been on for
    //If checkOurTime returns positive, increase DEPTH, if it returns negative, decrease it
    //If checkTheirTime returns negative, initiate asshole protocol, if it returns positive, turn it off
    //If ASSHOLE_COUNT
    public int checkOurTime(){
      long duration = START_TIME - System.currentTimeMillis();
      OUR_AVERAGE_TIME = ((AVERAGE_TIME*MOVE_COUNT-1)+duration)/MOVE_COUNT;
      if(OUR_AVERAGE_TIME > 900){
        return -1;
      }
      else if(OUR_AVERAGE_TIME < 500){
        return 1;
      }
      else{
        return 0;
      }
    }

    public int checkTheirTime(){
      long duration = System.currentTimeMillis() - END_TIME;
      THEIR_AVERAGE_TIME = ((AVERAGE_TIME*MOVE_COUNT-1)+duration)/MOVE_COUNT;
      if(THEIR_AVERAGE_TIME > 950){
        return -1;
      }
      else if(THEIR_AVERAGE_TIME > 1000){
        return 1;
      }
      else{
        return 0;
      }
    }

    public OthelloMove makeMove(OthelloBoard board) {

        ArrayList<OthelloMove> moves = board.legalMoves(playerColor);

        Random r = new Random();
        int chosenMoveIndex = r.nextInt(moves.size());

        return moves.get(chosenMoveIndex);
    }
}

public class OthelloNode{
  OthelloBoard board;
  OthelloMove move;
  int score;
  OthelloNode parent;

  public OthelloNode(OthelloBoard b, OthelloMove m){

  }
}
