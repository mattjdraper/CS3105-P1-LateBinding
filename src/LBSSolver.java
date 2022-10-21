import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

// LBSChecker by 200004184, 20/10/2022-XX/10/2022
//
// // Class containing functions accessible within LBSMain to operate on a given game of Late Binding Solitaire.
// // Including methods to determine validity of moves, ...
//

public class LBSSolver {
    //
    public void tester(){
        System.out.println("test");
    }
    //
    public boolean sameSuit(int card1, int card2, int noSuits){
        if( card1/noSuits == card2/noSuits){return true;}
        else{return false;}
    }
    //
    public boolean sameRank(int card1, int card2, int noRanks){
        if( card1%noRanks == card2%noRanks){return true;}
        else{return false;}
    }
    //
}