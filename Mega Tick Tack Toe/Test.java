// Peter Werner
// peternwerner@gmail.com

import java.util.Scanner;

public class Test {
    
    static TicTacToe game = new TicTacToe();
    
    public static void main(String[] args) {
        
        game.showBoard();
        
        Scanner in = new Scanner(System.in);
        
        while(true) {
            
            int coords[] = game.moveByRandom();
            game.setCell(coords[0], coords[1], Cell.X);
            System.out.println("Random has made a move: " + coords[0] + ", " + coords[1]);
                        
            game.showBoard();
            handleEndGame(in);
            
            
            boolean retry = true;
            while(retry) {
                System.out.println("Enter horizontal coordinate (0-4 where 0 is left):");
                coords[0] = in.nextInt();
                System.out.println("Enter vertical coordinate (0-4 where 0 is top):");
                coords[1] = in.nextInt();
                if(coords[0] >= 0 && coords[0] < 5 && coords[1] >= 0 && coords[1] < 5 && game.isEmpty(coords[0], coords[1]))
                    retry = false;
                if(retry)
                    System.out.println("Invalid input! Try again!");
            }
            
            game.setCell(coords[0], coords[1], Cell.O);
            
            game.showBoard();
            handleEndGame(in);
        }
    }
    
    private static void handleEndGame(Scanner in) {
        
        if(game.gameEnd()) {
            System.out.println("GAME IS OVER - enter anything to play again");
            if(game.scoreOfPlayer() != 0  ||  game.scoreOfRandom() != 0)
                System.out.println("GAME IS OVER (stalemate) - Random's score = " + game.scoreOfRandom() + ". Player's score = " + game.scoreOfPlayer());
            in.next();
            game.clearBoard();
        }
        
    }
    
}
