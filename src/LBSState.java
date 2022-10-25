import java.util.*;
// LBSState by 200004184, 24/10/2022-XX/10/2022

//
// // Class for LBS game state data structure. Any instance of an LBS game in the search space will be characterized as an LBSState type
// // Allows easy access to a state's layout, current working solution to reach such a point and heuristic value that dictates search priority.
//


public class LBSState implements Comparable<LBSState>{
    protected LBSLayout layout;
    protected ArrayList<Integer> solutionSoFar;

    protected int movesLeft;
    protected double heuristic;


    // LBSState() - Standard Constructor for LBSSolver.
    // For initializing the DFS, this creates state zero, the state correspondent to the game without any moves made.
    public LBSState(LBSLayout layout) {
        this.layout = layout;
        this.solutionSoFar = new ArrayList<>();
        this.movesLeft = layout.numPiles();
        this.heuristic = determineHeuristic(layout, this.movesLeft);
    }

    // LBSState() - Standard Constructor for LBSSolver.
    // For creating any search state that is not the original game.
    public LBSState(LBSState parentState, LBSLayout parentLayout, int card, int pile){
        LBSSolver solver = new LBSSolver(parentLayout);
        ArrayList<Integer> solution = new ArrayList<Integer>(parentState.getSolutionSoFar());
        this.layout = solver.completeMove(parentLayout,card,pile);
        this.solutionSoFar = solver.updateSolution(solution,card,pile);
        this.movesLeft = layout.numPiles()-1;
        this.heuristic = determineHeuristic(layout, this.movesLeft);
    }

    public ArrayList<Integer> getSolutionSoFar() {
        return solutionSoFar;
    }

    public double getHeuristic(){
        return heuristic;
    }

    public LBSLayout getLayout(){
        return layout;
    }

    public double determineHeuristic(LBSLayout layout, int movesLeft){

        LBSChecker checker = new LBSChecker();
        int movesAvailable = 0;

        for(int position = layout.numPiles()-1; position > 0; position--){
            int card = layout.cardAt(position);
            try{
                int oneAhead = layout.cardAt(position-1);
                if(checker.sameSuit(card,oneAhead, layout.numRanks()) || checker.sameRank(card,oneAhead, layout.numRanks())){
                    movesAvailable++;
                }
            } catch(Exception e){}
            try{
                int threeAhead = layout.cardAt(position-3);
                if(checker.sameSuit(card,threeAhead, layout.numRanks()) || checker.sameRank(card,threeAhead, layout.numRanks())){
                    movesAvailable++;
                }
            } catch(Exception e){}
        }
        double heuristic = (double) movesAvailable/movesLeft;
        return(heuristic);
    }

    // compareTo() - comparator override for solver.OrderStates()
    // Determines ordering of two states determined by their heuristic.
    // REFERENCES:
    // [1] https://stackoverflow.com/questions/5805602/how-to-sort-list-of-objects-by-some-property
    public int compareTo(LBSState state) {
        if (Math.abs(1- this.heuristic) > Math.abs(1-state.heuristic)) {
            return 1;
        } else if (Math.abs(this.heuristic) < Math.abs(state.heuristic)) {
            return -1;
        } else {
            return 0;
        }
    }

    public void print(){
        System.out.printf("\nLAYOUT: "); layout.print();
        System.out.printf("SOLUTION: " + solutionSoFar.toString());
        System.out.printf("\nHEURISTIC: " + heuristic + "\n");
    }
}
