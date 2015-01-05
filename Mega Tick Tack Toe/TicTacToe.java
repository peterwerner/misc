// Peter Werner
// peternwerner@gmail.com

import java.awt.Color;
import java.awt.Dimension;
import java.util.Random; 
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;


// Individual tick tack toe board cell
enum Cell {
    X, O, EMPTY
}

// Tick Tack Toe
public class TicTacToe {

    // Class variables
    private int size = 5;
    private Cell[][] board;
    private Cell markRandom = Cell.X, markPlayer = Cell.O;
    private GamePanel gamePanel = new GamePanel();  // graphics panel
    private int cellSize = 60;  // cell size in pixels
    private boolean[][] scoreMarkers;  // to display which cells are part of streaks
        
    // Default constructor (initialize a board of empty cells)
    public TicTacToe() {
        
        board = new Cell[size][size];
        scoreMarkers = new boolean[size][size];
        clearBoard();
        
        // init graphics
        gamePanel.setPreferredSize(new Dimension(size * cellSize + 1, size * cellSize + 1));
        JFrame frame = new JFrame("Peter's Tick Tack Toe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setContentPane(gamePanel);
        frame.pack();
        frame.setVisible(true);
    }
    
    // Return true and set cell if empty, return false on error
    public boolean setCell(int xPos, int yPos, Cell mark) {
       
        // Error: mark is empty cell
        if(mark == Cell.EMPTY)
            return false;
        // Error: specified cell is not empty
        if(board[xPos][yPos] != Cell.EMPTY)
            return false;
        // Success: set cell to mark
        board[xPos][yPos] = mark;
        return true;
    }
    
    // Return the value of the specified cell
    public Cell getCell(int xPos, int yPos) {
        return board[xPos][yPos];
    }
    
    // Return the board
    public Cell[][] getCell() {
        return board;
    }
    
    // Check if specified cell is empty
    public boolean isEmpty(int xPos, int yPos) {

        if(board[xPos][yPos] == Cell.EMPTY)
            return true;
        return false;
    }
    
    // Clear Board (set every cell to empty)
    public void clearBoard() {
        
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                board[i][j] = Cell.EMPTY;
                scoreMarkers[i][j] = false;
            }
        }    
    }
    
    // Randomly select an empty cell and return its indices
    public int[] moveByRandom() {
        
        // to store indicies of empty board cells
        int[][] emptyIndices = new int[size * size][2];
        // to keep count of how many empty cells there are
        int emptyCount = 0; 
        
        // populate emptyIndices and update emptyCount
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[i].length; j++) {
                
                if(board[i][j] == Cell.EMPTY) {
                    emptyIndices[emptyCount][0] = i;
                    emptyIndices[emptyCount][1] = j;
                    emptyCount++;
                }
            }   
        }
        if(emptyCount == 0)
            return null;
        // Now that we know which cells are empty, and how many there are:
        // Randomly choose one of the cells and return its indices as an int[2]
        Random rand = new Random();
        return emptyIndices[Math.abs(rand.nextInt(emptyCount))];
    }
    
    // Return true if game is over
    public boolean gameEnd() {
        
        // game is over if X has won
        if(numStreaksMark(Cell.X, size) > 0)
            return true;
        // game is over if O has won
        if(numStreaksMark(Cell.O, size) > 0)
            return true;
        // game is over if board is full
        if(boardIsFull()) {
            scoreOfRandom();    scoreOfPlayer();
            return true;
        }
        // otherwise, game has not ended
        return false;
    }
    
    // Calculate Random's score (return 0 if board has at least one cell empty)
    public int scoreOfRandom() {
        return scoreOf(markRandom);
    }
    
    // Calculate Player's score (return 0 if board has at least one cell empty)
    public int scoreOfPlayer() {
        return scoreOf(markPlayer);
    }
    
    // Display the game board in a Jframe
    public void showBoard() {
        
        gamePanel.draw();
    }
    
    /*
    // Class that controls the graphics and displays the game board
    */
    private class GamePanel extends JPanel {
        
        public void paintComponent( Graphics g )
        {
            super.paintComponent( g );
          
            // fill with white
            Color bgColor = Color.white;
            g.setColor(bgColor);
            g.fillRect(0, 0, size * cellSize, size * cellSize);
            
            // enable anti-aliasing so our graphics look nice and smooth
            Graphics2D graphics2D = (Graphics2D)g;
            graphics2D.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);   
            
            // draw each cell and its content
            for(int i = 0; i < size; i++) {
                for(int j = 0; j < size; j++) {
                    
                    // draw the square cell
                    g.setColor(Color.gray);
                    g.drawRect(i * cellSize, j * cellSize, cellSize, cellSize);
                    
                    // color in scoring cells
                    if(scoreMarkers[i][j]) {
                        if(board[i][j] == Cell.X)   g.setColor(Color.red);
                        if(board[i][j] == Cell.O)   g.setColor(Color.blue);
                        g.drawRect((i * cellSize) + 1, (j * cellSize) + 1, (cellSize) - 2, (cellSize) - 2);
                    }

                    // if X, draw an X
                    if(board[i][j] == Cell.X) {
                        g.setColor(Color.red);
                        int[] xPoints = {(int)((i + 0.2) * cellSize), (int)((i + 0.3) * cellSize),
                                         (int)((i + 0.8) * cellSize), (int)((i + 0.7) * cellSize)};
                        int[] yPoints = {(int)((j + 0.3) * cellSize), (int)((j + 0.2) * cellSize),
                                         (int)((j + 0.7) * cellSize), (int)((j + 0.8) * cellSize)};
                        g.fillPolygon(xPoints, yPoints, 4);
                        int[] xPoints2 = {(int)((i + 0.7) * cellSize), (int)((i + 0.8) * cellSize),
                                          (int)((i + 0.3) * cellSize), (int)((i + 0.2) * cellSize)};
                        int[] yPoints2 = {(int)((j + 0.2) * cellSize), (int)((j + 0.3) * cellSize),
                                          (int)((j + 0.8) * cellSize), (int)((j + 0.7) * cellSize)};
                        g.fillPolygon(xPoints2, yPoints2, 4);
                    }
                    
                    // if O, draw an O
                    else if(board[i][j] == Cell.O) {
                        g.setColor(Color.blue);
                        g.fillOval((int)((i + 0.2) * cellSize), (int)((j + 0.2) * cellSize), (int)(0.6 * cellSize), (int)(0.6 * cellSize));
                        g.setColor(bgColor);
                        g.fillOval((int)((i + 0.35) * cellSize), (int)((j + 0.35) * cellSize), (int)(0.3 * cellSize), (int)(0.3 * cellSize));
                    }
                }
            }

        }
        public void draw()
        {
            repaint();
        }
        
    }
    
    /*
    // PRIVATE HELPER METHODS:
    */
    
    // return the score of a certain mark (return 0 if board has at least one empty cell)
    private int scoreOf(Cell mark) {
        
        // check for empty cells (return 0 if one is found)
        if(!boardIsFull())
            return 0;
        // calculate and return score of person
        // size-1 streaks = 3 points, size-2 streaks = 1 point
        return (3 * numStreaksMark(mark, size - 1)) + numStreaksMark(mark, size - 2);
    }
    
    // Return number of streaks uniquely of a given length, of a given cell type
    private int numStreaksMark(Cell mark, int length) {
        
        int sum = 0;
        // check for streaks among all marked cells and iterate sum if there is one
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                
                if(isDiagDownStreakAt(mark, i, j, length))
                    sum++;
                if(isDiagUpStreakAt(mark, i, j, length))
                    sum++;
                if(isHorizontalStreakAt(mark, i, j, length))
                    sum++;
                if(isVerticalStreakAt(mark, i, j, length))
                    sum++;
                
            }
        }
        return sum;
    }
    
    // check if board is full
    private boolean boardIsFull() {
        
        for(int i = 0; i < size; i++)
            for(int j = 0; j < size; j++)
                if(board[i][j] == Cell.EMPTY)
                    return false;
        
        return true;
    }
    
    // Check if there is a streak of a given length, starting at a given cell, of a given cell type
    
        // Horizontal streak
        private boolean isHorizontalStreakAt(Cell mark, int x, int y, int length) {
            
            // false if the streak will take us out of range
            if(x + length > size)
                return false;
            // false if a marked cell preceeds this one (would be part of another streak)
            if(x - 1 >= 0  &&  board[x-1][y] == mark)
                return false;
            // false if this streak preceeds a marked cell (would be part of another streak)
            if(x + length < size  &&  board[x + length][y] == mark)
                return false;
            // false if any cells in the streak are not marked
            for(int i = x; i < x + length; i++)
                if(board[i][y] != mark)
                    return false;
            // all tests passed, set cells to paint, then return true
            for(int i = x; i < x + length; i++)
                scoreMarkers[i][y] = true;
            return true;
        }

        // Vertical streak
        private boolean isVerticalStreakAt(Cell mark, int x, int y, int length) {

            // false if the streak will take us out of range
            if(y + length > size)
                return false;
            // false if a marked cell preceeds this one (would be part of another streak)
            if(y - 1 >= 0  &&  board[x][y-1] == mark)
                return false;
            // false if this streak preceeds a marked cell (would be part of another streak)
            if(y + length < size  &&  board[x][y + length] == mark)
                return false;

            // false if any cells in the streak are not marked
            for(int j = y; j < y + length; j++)
                if(board[x][j] != mark)
                    return false;
            // all tests passed, set cells to paint, then return true
            for(int j = y; j < y + length; j++)
                scoreMarkers[x][j] = true;
            return true;
        }

        // Diagonal down streak
        private boolean isDiagDownStreakAt(Cell mark, int x, int y, int length) {

            // false if the streak will take us out of range
            if(x + length > size  ||  y + length > size)
                return false;
            // false if a marked cell preceeds this one (would be part of another streak)
            if(x - 1 >= 0  &&  y - 1 >= 0  &&  board[x-1][y-1] == mark)
                return false;
            // false if this streak preceeds a marked cell (would be part of another streak)
            if(x + length < size  &&  y + length < size  &&  board[x + length][y + length] == mark)
                return false;

            // false if any cells in the streak are not marked
            for(int i = 0; i < length; i++)
                if(board[i + x][i + y] != mark)
                    return false;
            // all tests passed, set cells to paint, then return true
            for(int i = 0; i < length; i++)
                scoreMarkers[i + x][i + y] = true;
            return true;
        }

        // Diagonal up streak
        private boolean isDiagUpStreakAt(Cell mark, int x, int y, int length) {

            // false if the streak will take us out of range
            if(x + length > size  ||  (y + 1) - length < 0)
                return false;
            // false if a marked cell preceeds this one (would be part of another streak)
            if(x - 1 >= 0  &&  y + 1 < size  &&  board[x-1][y+1] == mark)
                return false;
            // false if this streak preceeds a marked cell (would be part of another streak)
            if(x + length < size  &&  y - length >= 0  &&  board[x + length][y - length] == mark)
                return false;
            // false if any cells in the streak are not marked
            for(int i = 0; i < length; i++)
                if(board[i + x][y - i] != mark)
                    return false;
            // all tests passed, set cells to paint, then return true
            for(int i = 0; i < length; i++)
                scoreMarkers[i + x][y - i] = true;
            return true;
        }
    
}
