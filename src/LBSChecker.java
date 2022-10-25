import java.util.ArrayList;

// LBSChecker by 200004184, 20/10/2022-23/10/2022
//
// // Class containing functions accessible within LBSMain to check a given solution of Late Binding Solitaire.
// // Including methods to determine validity of moves, for both standard LBS games and with saving grace.
//

public class LBSChecker {

    protected int moves;
    protected ArrayList<Integer> solution;

    // LBSChecker() - Standard Constructor for LBSChecker.
    public LBSChecker(){
        this.solution = new ArrayList<Integer>(0);
        this.moves = 0;
    }

    // sameSuit() - Test whether two cards are of the same suit.
    // - For two cards to be of the same suit in a k-ranks, l-suit game. They must be in the same k'th grouping.
    // - Integer division by this k determines what 'k'th group' a card falls in.
    // - Adjust as (card - 1 / k) due to card counting beginning from one instead of zero.
    // - Return true if card to move / pile to move unto have same suit, false otherwise
    public boolean sameSuit(int card1, int card2, int noRanks){
        if( (card1-1) / noRanks == (card2-1) / noRanks) {
            return true;
        }
        else {
            return false;
        }
    }

    // sameRank() - Test whether two cards are of the same rank.
    // - For two cards to be of the same rank in a k-ranks, l-suit game. They must be the same value mod k.
    // - Every k cards, you will arrive at the same rank. Testing if cards are equal mod k gives suitable rank test.
    // - Return true if card to move / pile to move unto have same rank, false otherwise
    public boolean sameRank(int card1, int card2, int noRanks){
        if (card1 % noRanks == card2 % noRanks) {
            return true;
        }
        else {
            return false;
        }
    }

    // validMove() - Test whether suggested LBS action moves a card either one or three piles ahead.
    // - Given the pile position of the card to be moved and the pile to move unto, ensure these are either one or three piles apart.
    // - Return true if cards one or three piles apart, false otherwise.
    public boolean validMove(int pile1_location, int pile2_location){
        if(pile1_location - pile2_location == 1 || pile1_location - pile2_location == 3){
            return true;
        }
        else{
            return false;
        }
    }

    // validCards() - Test whether the card proposed to move and the pile suggested to move upon take valid integer values in the game.
    // - The suggested card to move cannot be negative, nor of a value larger than the number of cards in the proposed deck.
    // - The suggested pile to move upon cannot be negative, nor be larger than the current number of piles in the game.
    // - Return true card/pile criteria met, false otherwise.
    public boolean validCards(int card, int pilePostion, LBSLayout layout){
        if (card < 0 || pilePostion < 0){
            return false;
        }
        else if (card > layout.cardsInDeck()|| pilePostion > layout.numPiles()){
            return false;
        }
        else{
            return true;
        }
    }

    // invalidSetup() - Test edge-cases that propose invalid initial constructions of either game layout or proposed solution
    // - invalidSetup is run before the main checking algorithm to invalidate any potential code-breaking/exception throwing game cases immediately.
    // - Invalid cases tested against are described within the source code. Each return true, leading to LBSMain CHECK to terminate early and output false.
    // - Return true if the proposed game layout or solution are improperly presented, false otherwise.
    public boolean invalidSetup(LBSLayout startingLayout, ArrayList<Integer> startingSolution){
        // Test against any instance that proposes an empty layout or solution file.
        if(startingSolution.isEmpty() || startingLayout.numPiles() == 0){
            return true;
        }
        // Test against any instances that proposes a solution not equal to (n-1) moves, where n is the number of piles at the beginning.
        else if (startingSolution.size() != 2*startingLayout.numPiles()-1){
            return true;
        }
        // Test against any instances that proposes a solution in less than (n-1) steps.
        else if (startingLayout.numPiles()-1 != startingSolution.get(0)){
            return true;
        }
        else{
            return false;
        }
    }

}