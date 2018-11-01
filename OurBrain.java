/**
New brain, new rules
*/

import java.util.Arrays;

public class OurBrain implements Brain {
 /**
  Given a piece and a board, returns a move object that represents
  the best play for that piece, or returns null if no play is possible.
  See the Brain interface for details.
 */
 public Brain.Move bestMove(Board board, Piece piece, int limitHeight, Brain.Move move) {
  // Allocate a move object if necessary
  if (move==null) move = new Brain.Move();
  
  double bestScore = 1e20;
  int bestX = 0;
  int bestY = 0;
  Piece bestPiece = null;
  Piece current = piece;
  
  // loop through all the rotations
  while (true) {
   final int yBound = limitHeight - current.getHeight()+1;
   final int xBound = board.getWidth() - current.getWidth()+1;
   
   // For current rotation, try all the possible columns
   for (int x = 0; x<xBound; x++) {
    int y = board.dropHeight(current, x);
    if (y<yBound) { // piece does not stick up too far
     int result = board.place(current, x, y);
     if (result <= Board.PLACE_ROW_FILLED) {
      if (result == Board.PLACE_ROW_FILLED) board.clearRows();
      
      double score = rateBoard(board);
      
      if (score<bestScore) {
       bestScore = score;
       bestX = x;
       bestY = y;
       bestPiece = current;
      }
     }
     
     board.undo(); // back out that play, loop around for the next
    }
   }
   
   current = current.nextRotation();
   if (current == piece) break; // break if back to original rotation
  }
  
  if (bestPiece == null) return(null); // could not find a play at all!
  else {
   move.x=bestX;
   move.y=bestY;
   move.piece=bestPiece;
   move.score = bestScore;
   return(move);
  }
 }
 
 
 /*
  A simple brain function.
  Given a board, produce a number that rates
  that board position -- larger numbers for worse boards.
  This version just counts the height
  and the number of "holes" in the board.
  See Tetris-Architecture.html for brain ideas.
 */
 public double rateBoard(Board board) {
  final int width = board.getWidth();
  final int maxHeight = board.getMaxHeight();
  double loss = 0.0;

  if (maxHeight > board.getHeight()) loss = 1.0;
  
  // prioritize filling a row up by summing the
  // number of blocks per row squared 
  int pieces_away = 0;
  
  // calculate the number of exposed edges
  int exposed = 0;

  // calculate the number of holes
  int holes = 0;

  // calculate average height
  int sumHeight = 0;

  // essentially initializes as bottom of board
  boolean[] prev_row = new boolean[width];
  Arrays.fill(prev_row, true);
  boolean prev_space;

  // if this isn't pointless to calculate
  if (loss != 1.0){

    for (int r = 0; r <= maxHeight; r++){
      // calculate how filled up a row is
      int num_filled = board.getRowWidth(r);
      pieces_away += (int) Math.pow(num_filled, 2);

      // calculate exposed edges

      // keep track of lefthand space
      // initializes as left side of the board
      prev_space = true;

      for (int c = 0; c < width; c++){
        
        // if there's a block here
        if (board.getGrid(c,r)){
          
          // check if left side is exposed
          if (!prev_space) exposed++;
          
          // check if bottom side is exposed
          // also indicates the presence of a hole
          if (!prev_row[c]) {
            exposed++;
            holes++;
          }
          
          // set the prev_space and row
          prev_space = true;
          prev_row[c] = true;
        }

        // if there's no block here
        else{

          // check if right side of structure is exposed
          if (prev_space) exposed++;
          
          // check if top side of structure is exposed
          if (prev_row[c]) exposed++;
          
          prev_space = false;
          prev_row[c] = false;
        }

      }
    }
  }
  

  
  //   

  return (double) (10000 * loss + 4 * exposed + 3 * pieces_away + 1.5 * holes);
 }

}
