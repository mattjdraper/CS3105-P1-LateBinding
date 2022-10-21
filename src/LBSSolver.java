import java.util.ArrayList;
import java.lang.Math;

// LBSChecker by 200004184, 20/10/2022-XX/10/2022
//
// // Class containing functions accessible within LBSMain to operate on a given game of Late Binding Solitaire.
// // Including methods to determine validity of moves, ...
//

public class LBSSolver {


    protected int moves;
    protected ArrayList<Integer> solution;

    public LBSSolver(){
        this.solution = new ArrayList<Integer>(0);
        this.moves = 0;
    }

    //
    public void tester(){
        System.out.println("test");
    }
    //
    public boolean sameSuit(int card1, int card2, int noRanks){
        if( card1 / noRanks == card2 / noRanks) {
            return true;
        }
        else {
            return false;
        }
    }
    //
    public boolean sameRank(int card1, int card2, int noRanks){
        if (card1 % noRanks == card2 % noRanks) {
            return true;
        }
        else {
            return false;
        }
    }
    //
    public boolean validMove(int pile1_location, int pile2_location){
        if(Math.abs(pile1_location - pile2_location) == 1 || Math.abs(pile1_location - pile2_location) == 3){
            return true;
        }
        else{
            return false;
        }
    }
}