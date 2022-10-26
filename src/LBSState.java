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

    protected boolean savingGrace;


    // LBSState() - Standard Constructor for LBSSolver.
    // For initializing the DFS, this creates state zero, the state correspondent to the game without any moves made.
    public LBSState(LBSLayout layout) {
        this.layout = layout;
        this.solutionSoFar = new ArrayList<>();
        this.movesLeft = layout.numPiles();
        this.savingGrace = false;
    }

    // LBSState() - Override Constructor for LBSSolver.
    // For creating any search state that is not the original game.
    public LBSState(LBSState parentState, LBSLayout parentLayout, int card, int pile, boolean savingGrace){
        LBSSolver solver = new LBSSolver(parentLayout);
        ArrayList<Integer> solution = new ArrayList<>(parentState.getSolutionSoFar());
        this.layout = solver.completeMove(parentLayout,card,pile);
        this.solutionSoFar = solver.updateSolution(solution,card,pile);
        this.movesLeft = layout.numPiles()-1;
        this.savingGrace = savingGrace;
    }

    public boolean getSavingGrace(){return savingGrace;}

    public ArrayList<Integer> getSolutionSoFar() {
        return solutionSoFar;
    }

    public LBSLayout getLayout(){
        return layout;
    }

    public void print(){
        System.out.print("\nLAYOUT: "); layout.print();
        System.out.printf("SOLUTION: " + solutionSoFar.toString());
        System.out.printf("\nSAVING GRACE?: " + savingGrace + "\n");
    }

    @Override
    public int compareTo(LBSState otherState){
        return Integer.compare(getLayout().numPiles(), otherState.getLayout().numPiles());
    }
}
