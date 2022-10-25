import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

// LBSSolver by 200004184, 23/10/2022-XX/10/2022
//
// // Class containing functions accessible within LBSMain to attempt to solve a given game of Late Binding Solitaire.
// // Including methods to ...
//

public class LBSSolver {

    protected int movesToWin;
    protected boolean solutionFound;
    protected LBSLayout startingLayout;

    protected ArrayList<ArrayList<Integer>> seenStates;
    protected String finalSolution;



    // LBSSolver() - Standard Constructor for LBSSolver.
    public LBSSolver(LBSLayout layout) {
        this.movesToWin = layout.numPiles()-1;
        this.solutionFound = false;
        this.startingLayout = layout;
        this.seenStates  = new ArrayList<>();
        this.finalSolution = null;

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

        // Loop from the back of the deck checking valid moves available from each card.
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
                if(checker.sameSuit(card,oneAhead, parentLayout.numRanks()) || checker.sameRank(card,oneAhead, parentLayout.numRanks())){
                    // Check State has not been seen previously in search before instantiating a new State for this move.
                    // // Featuring a bit of a messy bodge workaround to ensure the parent layout is passed by value instead of reference.
                    // // Tried to avoid this, but ultimately it had to stay to get the algorithm to work.
                    LBSLayout layoutCopy = new LBSLayout(parentLayout);
                    // Create a new state for the search space.
                    LBSState newState = new LBSState(state, layoutCopy, card, position-1, state.getSavingGrace());
                    if(!seenStates.contains(newState.getLayout().getGameLayout())) {

                        newStates.add(newState);

                        // Add the new game configuration instance to the seenStates Hashmap to prevent backtracking to this state in future DFS.
                        seenStates.add(newState.getLayout().getGameLayout());

                        // Test if the new state is a solution. If it is a solution, end the search early
                        solutionFound = solutionTest(newState);
                        if (solutionFound) {
                            break;
                        }
                    }
                }
            } catch(Exception e){}

            // Expand Parent State at card k if there is a valid move three piles ahead. Add this new State to list.
            // // Same procedure as the previous try/catch block but testing against the card three piles ahead instead.
            try{
                int threeAhead = parentLayout.cardAt(position-3);
                if(checker.sameSuit(card,threeAhead, parentLayout.numRanks()) || checker.sameRank(card,threeAhead, parentLayout.numRanks())){
                    LBSLayout layoutCopy = new LBSLayout(parentLayout);
                    LBSState newState = new LBSState(state, layoutCopy, card, position-3, state.getSavingGrace());
                    if(!seenStates.contains(newState.getLayout().getGameLayout())) {

                        newStates.add(newState);

                        seenStates.add(newState.getLayout().getGameLayout());

                        solutionFound = solutionTest(newState);
                        if (solutionFound) {
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

    // DFS_Expand_SG() - Expand a given game state within Depth First Search Algorithm with Saving Grace available.
    // - code directly copied from DFS_Expand() except where annotated to implement SG.
    public ArrayList<LBSState> DFS_Expand_SG(LBSState state){

        LBSChecker checker = new LBSChecker();

        LBSLayout parentLayout = state.getLayout();

        ArrayList<LBSState> newStates = new ArrayList<>();

        for(int position = parentLayout.numPiles()-1; position > 0; position--){

            int card = parentLayout.cardAt(position);

            try{
                // Saving Grace move to pile-1
                LBSLayout layoutCopy = new LBSLayout(parentLayout);
                LBSState newState = new LBSState(state, layoutCopy, card, position-1, true);
                if(!seenStates.contains(newState.getLayout().getGameLayout())) {
                    newStates.add(newState);
                    seenStates.add(newState.getLayout().getGameLayout());
                    solutionFound = solutionTest(newState);
                    if (solutionFound) {
                        break;
                    }
                }
            } catch(Exception e){}

            try{
                // Saving Grace move to pile-3
                LBSLayout layoutCopy = new LBSLayout(parentLayout);
                LBSState newState = new LBSState(state, layoutCopy, card, position-3, true);
                if(!seenStates.contains(newState.getLayout().getGameLayout())) {
                    newStates.add(newState);
                    seenStates.add(newState.getLayout().getGameLayout());
                    solutionFound = solutionTest(newState);
                    if (solutionFound) {
                        break;
                    }
                }
            } catch(Exception e){}
        }

        return newStates;
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
    public boolean solutionTest(LBSState newState){
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