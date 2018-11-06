/**
New brain, new rules
*/
// import java.util.concurrent.TimeUnit;

import java.util.Arrays;

public class BirdBrain implements Brain {
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
  int[] bestargs = new int[6];
  try
  {
      Thread.sleep(0);
  }
  catch(InterruptedException ex)
  {
      Thread.currentThread().interrupt();
  }
  
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
      
      int[] args = rateBoard(board);
      // 4.4837779129 , 0.1583384994 , 2.2257590042 , 7.0806747145 , 0.0095125652
      //double score = 4.4837779129 * args[0] + 0.1583384994 * args[1] + 2.2257590042 * args[2] + 7.0806747145 * args[3] + 0.0095125652 * args[4] + -1 * args[5];
      // double score = 1.5604142221 * args[0] + 0.0275316212 * args[1] + 1.4234453418 * args[2] + 0.4866095922 * args[3] + 0.0445529734 * args[4] + 0.1919100671 * args[5];
      double score = 10.7838260732 * args[0] + -0.0001418397 * args[1] + 7.4414077632 * args[2] + 21.0827708304 * args[3] + 0.0016289420* args[4] + -1 * args[5];

      // double score = args[0] * tallest_height + args[1] * num_blocks + args[2] * row_trans + args[3] * col_trans + args[4] * num_holes + args[5] * deep_holes;

      if (score<bestScore) {
       bestargs = args;
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
   System.out.println("Tallest Height: "+ bestargs[0]);
   System.out.println("Number of Blocks: "+ bestargs[1]);
   System.out.println("Row Trans: "+ bestargs[2]);
   System.out.println("Col Trans: "+ bestargs[3]);
   System.out.println("Holes: "+ bestargs[4]);
   System.out.println("Deep Holes: "+ bestargs[5]);
   move.x=bestX;
   move.y=bestY;
   move.piece=bestPiece;
   move.score = bestScore;

   System.out.println(board.getHeight());
   System.out.println(board.getWidth());

  for (int i = 0; i < board.getHeight(); i++) {
    boolean last_block = true;
    for (int j = 0; j < board.getWidth(); j++) {
      boolean new_block = board.getGrid(j, i);
      if (new_block) {
        //System.out.println(i + "  " + j);
      }
    }
  }


   return(move);
  }
 }

 private int depth_of_hole(Board board, int row, int col) {
  int depth = 0;
  for (int j = col; j >= 0; j--) {
    boolean left = true;
    boolean right = true;
    if (row > 0) {
      left = board.getGrid(j, row-1);
    }
    if (row < board.getWidth() - 1) {
      right = board.getGrid(j, row+1);
    }
    if (left && right) {
      depth++;
    }
    else {
      return depth;
    }
  }
  return depth;
}
 
 /*
  A simple brain function.
  Given a board, produce a number that rates
  that board position -- larger numbers for worse boards.
  This version just counts the height
  and the number of "holes" in the board.
  See Tetris-Architecture.html for brain ideas.
 */
 public int[] rateBoard(Board board) {
  // final int width = board.getWidth();
  // final int maxHeight = board.getMaxHeight();
  // double loss = 0.0;

  // if (maxHeight > board.getHeight()) loss = 1.0;
  
  // // prioritize filling a row up by summing the
  // // number of blocks per row squared 
  // int pieces_away = 0;
  
  // // calculate the number of exposed edges
  // int exposed = 0;

  // // calculate the number of holes
  // int holes = 0;

  // // calculate average height
  // int sumHeight = 0;

  // // essentially initializes as bottom of board
  // boolean[] prev_row = new boolean[width];
  // Arrays.fill(prev_row, true);
  // boolean prev_space;

  // // if this isn't pointless to calculate
  // if (loss != 1.0){

  //   for (int r = 0; r <= maxHeight; r++){
  //     // calculate how filled up a row is
  //     int num_filled = board.getRowWidth(r);
  //     pieces_away += (int) Math.pow(num_filled, 2);

  //     // calculate exposed edges

  //     // keep track of lefthand space
  //     // initializes as left side of the board
  //     prev_space = true;

  //     for (int c = 0; c < width; c++){
        
  //       // if there's a block here
  //       if (board.getGrid(c,r)){
          
  //         // check if left side is exposed
  //         if (!prev_space) exposed++;
          
  //         // check if bottom side is exposed
  //         // also indicates the presence of a hole
  //         if (!prev_row[c]) {
  //           exposed++;
  //           holes++;
  //         }
          
  //         // set the prev_space and row
  //         prev_space = true;
  //         prev_row[c] = true;
  //       }

  //       // if there's no block here
  //       else{

  //         // check if right side of structure is exposed
  //         if (prev_space) exposed++;
          
  //         // check if top side of structure is exposed
  //         if (prev_row[c]) exposed++;
          
  //         prev_space = false;
  //         prev_row[c] = false;
  //       }

  //     }
  //   }
  // }

  // NEW STUFF HERE
  int tallest_height = board.getMaxHeight();
  int num_blocks = 0;
  int row_trans = 0;
  int col_trans = 0;
  int num_holes = 0;
  int deep_holes = 0;


  int cols = board.getWidth();
  int rows = board.getHeight();



  // row transition
  for (int row = rows-1; row >= 0 ; row--) {
    boolean last_block = true;
    for (int col = 0; col < cols; col++) {
      boolean new_block = board.getGrid(col, row);
      if (new_block) {
        num_blocks++;
      }
      if (new_block != last_block) {
        row_trans++;
      }
      last_block = new_block;
    }
    if (!last_block) {
      row_trans++;
    }
  }

  // col transition
  for (int col = 0; col < cols; col++) {
    boolean last_block = true;
    boolean above = false;
    for (int row = rows; row >= 0; row--) {
      boolean new_block = board.getGrid(col, row);
      if (above) {
        if (!new_block) {
          num_holes++;
        }
      }
      else {
        if (new_block) {
          above = true;
          // check for how deep the hole is
          deep_holes += depth_of_hole(board, col, row);
        }
      }
      if (new_block != last_block) {
        col_trans++;
      }
      last_block = new_block;
    }
    if (!last_block) {
      col_trans++;
    }
  }
  boolean test = false;
  if (test) {
    // return (double) args[0] * tallest_height + args[1] * num_blocks + args[2] * row_trans + args[3] * col_trans + args[4] * num_holes + args[5] * deep_holes;
  }
  else {
    // System.out.println("Tallest Height: "+ tallest_height);
    // System.out.println("Number of Blocks: "+ num_blocks);
    // System.out.println("Row Trans: "+ row_trans);
    // System.out.println("Col Trans: "+ col_trans);
    // System.out.println("Holes: "+ num_holes);
    // System.out.println("Deep Holes: "+ deep_holes);
    int[] arr = {tallest_height, num_blocks, row_trans, col_trans, num_holes, deep_holes};
    return arr;
    // 0.0384668057 , -0.0155424025 , 0.0412990277 , -0.0558436342 , 0.0210189077 , 0.0347794277
    // return (double) 1.5604142221 * tallest_height + 0.0275316212 * num_blocks + 1.4234453418 * row_trans + 0.4866095922 * col_trans + 0.0445529734 * num_holes + 0.1919100671 * deep_holes;
    // return (double) 0.0384668057 * tallest_height + -0.0155424025 * num_blocks + -0.0558436342 * row_trans + 0.0210189077 * col_trans + 0.0347794277 * num_holes + 0.0347794277 * deep_holes;
  }
  int[] bleh = {};
  return bleh;

  // num holes
  // for (int i = 0; i < cols; i++) {
  //   boolean above = false;
  //   for (int j == 0; j < rows; j++) {
  //     if (above) {
  //       if (!board.getGrid(j, i)) {
  //         holes++;
  //       }
  //     }
  //     else {
  //       if (board.getGrid(j, i)) {
  //         above = true;
  //       }
  //     }
  //   }
  // }




  // System.out.println("updated this file");
  

  
  //  BEST SO FAR (SCORE 6000-ish): 0.0035379112 , 0.0036936799 , -0.0000031202
  //  SCORE: 23305.0. Weights: 0.0001753196 , 0.0003242803 , -0.0000001234 , 0.0010865025
  // return (double) (2.5 * exposed - 0.001 * pieces_away + 1.25 * holes); // TESTER

  // Good-ish
  // return (double) (0.0001753196 * loss + 0.0003242803* exposed + -0.0000001234 * pieces_away + 0.0010865025* holes);

  // try fixing holes at 0.1
  // return (double) (-0.0000586004 * loss + 0.0282577101 * exposed + -0.0000096741 * pieces_away + 0.1 * holes);

  // return (double) (args[0] * loss + args[1] * exposed + args[2] * pieces_away + 0.1 * holes);
 }

}
