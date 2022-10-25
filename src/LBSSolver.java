import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

// LBSSolver by 200004184, 23/10/2022-XX/10/2022
//
// // Class containing functions accessible within LBSMain to attempt to solve a given game of Late Binding Solitaire.
// // Including methods to ...
//

public class LBSSolver {

    protected int movesToWin;
    protected boolean solutionFound;
    protected LBSLayout startingLayout;

    protected HashMap<Integer,ArrayList<Integer>> seenStates;
    protected String finalSolution;



    // LBSSolver() - Standard Constructor for LBSSolver.
    public LBSSolver(LBSLayout layout) {
        this.movesToWin = layout.numPiles()-1;
        this.solutionFound = false;
        this.startingLayout = layout;
        this.seenStates  = new HashMap<>();
        this.finalSolution = null;

    }

    // getMovesToWin() - returns movesToWin variable.
    public int getMovesToWin(){
        return movesToWin;
    }

    // getSolutionFound() - returns the solutionFound variable.
    public boolean getSolutionFound(){
        return solutionFound;
    }

    // getFinalSolution() - returns the finalSolution variable.
    public String getFinalSolution(){return finalSolution;}

    // DFS_Expand() - Expand a given game state within Depth First Search Algorithm.
    // - The parent state is the current 'best looking' game layout sorted by the heuristic.
    // - Create a new state for every possible move you can perform on said layout. Implemented iteratively.
    // - Return ArrayList containing all new states generated from single moves on the parent game.
    public ArrayList<LBSState> DFS_Expand(LBSState state){
        
        // Instantiate an LBSChecker for access to move validity functions.
        LBSChecker checker = new LBSChecker();
        // Create copy of the parent state's layout to pass by value into potential new state constructors.
        LBSLayout parentLayout = state.getLayout();
        // Create a storage list of newly made states formed by expanding the parent across all possible moves.
        ArrayList<LBSState> newStates = new ArrayList<>();

        // Loop from the back of the deck checking valid moves avaiable from each card.
        // If a valid move exists, create a new state whereby said move is enacted and add to new state list.
        for(int position = parentLayout.numPiles()-1; position > 0; position--){

            // Get the card in the list we will check if there exist valid moves for.
            int card = parentLayout.cardAt(position);

            // Expand Parent State at card k if there is a valid move one pile ahead. Add this new State to list.
            try{
                // Find the card one pile ahead of the current card.
                int oneAhead = parentLayout.cardAt(position-1);
                // Test if there is a valid move between the current card and the one ahead.
                // // To go onward and create the state (including expensive generateHeuristic() operation) must not invoke a state seen before.
                if((checker.sameSuit(card,oneAhead, parentLayout.numRanks()) || checker.sameRank(card,oneAhead, parentLayout.numRanks()))
                    && !seenState(card,position-1,parentLayout)){
                    // Check State has not been seen previously in search before instantiating a new State for this move.
                    // // Featuring a bit of a messy bodge workaround to ensure the parent layout is passed by value instead of reference.
                    // // Tried to avoid this but ultimately it had to stay to get the algorithm to work.
                    LBSLayout layoutCopy = new LBSLayout(parentLayout);
                    // Create a new state for the search space, after enacting the said move and documenting it for the solution.
                    LBSState newState = new LBSState(state, layoutCopy, card, position-1);
                    // Test if the new state is a dead end or in fact a solution.
                    if(newState.getHeuristic() != 0) {
                        // Only store new states that are not dead ends. States with no further moves are identified by a heuristic of zero.
                        newStates.add(newState);
                        // Add the new game configuration instance to the seenStates Hashmap to prevent backtracking to this state in future DFS.
                        seenStates.put(newState.getLayout().numPiles(), newState.getLayout().getDeck());
                        // Test if the new state is a solution. If it is a solution, end the search early
                        solutionFound = solutionTest(newState);
                        if(solutionFound){
                            break;
                        }
                    }
                }
            } catch(Exception e){}

            // Expand Parent State at card k if there is a valid move three piles ahead. Add this new State to list.
            // // Same procedure as the previous try/catch block but testing against the card three piles ahead instead.
            try{
                int threeAhead = parentLayout.cardAt(position-3);
                if((checker.sameSuit(card,threeAhead, parentLayout.numRanks()) || checker.sameRank(card,threeAhead, parentLayout.numRanks()))
                    && !seenState(card,position-3,parentLayout)){
                    LBSLayout layoutCopy = new LBSLayout(parentLayout);
                    LBSState newState = new LBSState(state, layoutCopy, card, position-3);
                    if(newState.getHeuristic() != 0) {
                        newStates.add(newState);
                        seenStates.put(newState.getLayout().numPiles(), newState.getLayout().getDeck());
                        solutionFound = solutionTest(newState);
                        if(solutionFound){
                            break;
                        }
                    }
                }
            } catch(Exception e){}

        }
        // The parent state is fully expanded generating new states for all enactable moves on the layout.
        // Return a list of these new states.
        return newStates;
    }

    // orderStates() - Order all unexpanded states by Heuristic. State with the largest Heuristic first in the list
    // - Given the original list of states and the new list found by Depth First Expansion of the 'best-looking' state
    //   merge the two lists, adding the new state at the first instance newState.heuristic > someOtherState.heuristic.
    // - Return ArrayList containing all ordered unexpanded states
    public ArrayList<LBSState> orderStates(ArrayList<LBSState> searchStates, ArrayList<LBSState> newStates){
        searchStates.addAll(newStates);
        Collections.sort(searchStates);
        return searchStates;
    }

    // completeMove()
    // - Given a layout and move to conduct, complete said move and return the updated game layout
    // - Differs to layout.movePiles() as algorithm requires the updated layout returned, instead of void.
    public LBSLayout completeMove(LBSLayout layout, int card, int pile){
        int cardPosition = layout.cardPosition(card);
        layout.movePiles(pile, card);
        layout.removePile(cardPosition);
        return layout;
    }

    // seenStateTest()
    // - Check whether a game proposed by a potential move would invoke a state already seen in DFS
    // - Return true if the new state would be seen before, false otherwise.
    public boolean seenState(int card, int moveTo, LBSLayout parentLayout){
        // Bodge Solution - copy the current game layout for manipulation
        LBSLayout layoutCopy = new LBSLayout(parentLayout);
        LBSLayout newLayout = completeMove(layoutCopy, card, moveTo);
        // Extract the new game layout after computing result of move, see if it exists in hashmap.
        ArrayList<Integer> deck = newLayout.getDeck();
        if(seenStates.containsValue(deck)){
            //System.out.println("SEEN STATE BEFORE");
        }
        return seenStates.containsValue(deck);
    }

    // updateSolution()
    // - Given the parent States working solution and move enacted in new state, add the new move to solution
    public ArrayList<Integer> updateSolution(ArrayList<Integer> solution, int card, int pile){
        solution.add(card);
        solution.add(pile);
        return solution;
    }

    // solutionTest()
    // For every new state created within Depth First Search, test if it is in fact a .
    // Criteria for a state to be a solution is having a single pile of cards in the game layout.
    // If this is true, write the pathway to find such a solution to reference variable and prepare for early search termination.
    private boolean solutionTest(LBSState newState){
        if (newState.getLayout().numPiles() == 1) {
            finalSolution = formatSolution(newState);
            return true;
        }
        else {
            return false;
        }
    }

    // formatSolution(): convert ArrayList<> of move indexes to a string for output.
    private String formatSolution(LBSState state){
        ArrayList<Integer> solution = state.getSolutionSoFar();
        String output = "";
        output = output.concat(Integer.toString(movesToWin));
        for (Integer num : solution) {
            output = output.concat(" ");
            output = output.concat(Integer.toString(num));
        }
        return output;
    }
}