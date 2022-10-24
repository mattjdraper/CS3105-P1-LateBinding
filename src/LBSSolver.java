import java.util.ArrayList;

// LBSSolver by 200004184, 23/10/2022-XX/10/2022
//
// // Class containing functions accessible within LBSMain to attempt to solve a given game of Late Binding Solitaire.
// // Including methods to ...
//

public class LBSSolver {

    protected int moves;
    protected LBSLayout layout;
    protected ArrayList<Integer> workingSolution;
    protected ArrayList<Integer> solution;

    // LBSSolver() - Standard Constructor for LBSSolver.
    public LBSSolver(LBSLayout layout) {
        this.moves = 0;
        this.layout = layout;
        this.solution = new ArrayList<Integer>(0);
        this.solution = new ArrayList<Integer>(0);
    }

}