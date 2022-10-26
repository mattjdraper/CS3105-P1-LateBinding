import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

// Starter by Ian Gent, Oct 2022
//
// // This class is provided to save you writing some of the basic parts of the code
// // Also to provide a uniform command line structure
//
// // You may freely edit this code if you wish, e.g. adding methods to it. 
// // Obviously we are aware the starting point is provided so there is no need to explicitly credit us
// // Please clearly mark any new code that you have added/changed to make finding new bits easier for us
//
//
// // Edit history:
// // V1 released 3 Oct 2022
//
//

// LBSMain implementation by 200004184, 21/10/22-XX/10/22
//
// // Late Binding Solitaire Methods of CHECK, SOLVE, GRACECHECK constructed within the starter code provided
// // References used within each function stated within source code.
//

public class LBSMain {

      public static void printUsage() { 
          System.out.println("Input not recognised.  Usage is:");
          System.out.println("java LBSmain GEN|CHECK|SOLVE|GRACECHECK|GRACESOLVE <arguments>"  ); 
          System.out.println("     GEN arguments are seed [numpiles=17] [numranks=13] [numsuits=4] ");
          System.out.println("                       all except seed may be omitted, defaults shown");
          System.out.println("     SOLVE/GRACESOLVE argument is file]");
          System.out.println("                     if file is or is - then stdin is used");
          System.out.println("     CHECK/GRACECHECK argument is file1 [file2]");
          System.out.println("                     if file1 - then stdin is used");
          System.out.println("                     if file2 is ommitted or is - then stdin is used");
          System.out.println("                     at least one of file1/file2 must be a filename and not stdin");
	}


      public static ArrayList<Integer> readIntArray(String filename) {
        // File opening sample code from
        // https://www.w3schools.com/java/java_files_read.asp
	ArrayList<Integer> result  ;
	Scanner reader;
        try {
			File file = new File(filename);
			reader = new Scanner(file);
			result=readIntArray(reader);
			reader.close();
			return result;
            }
        catch (FileNotFoundException e) {
			System.out.println("File not found");
			e.printStackTrace();
            }
	// drop through case
	return new ArrayList<Integer>(0);
	
        }

      public static ArrayList<Integer> readIntArray(Scanner reader) {
	  ArrayList<Integer> result = new ArrayList<Integer>(0);
          while( reader.hasNextInt()  ) {
              result.add(reader.nextInt());
          }
	  return result;
      }


	public static void main(String[] args) {

	Scanner stdInScanner = new Scanner(System.in);
	ArrayList<Integer> workingList;

        LBSLayout layout;
        LBSChecker checker;
        LBSSolver solver;

        int seed ;
        int ranks ;
        int suits ;
        int numpiles ;
       
        if(args.length < 1) { printUsage(); return; }


		switch (args[0].toUpperCase()) {

		case "GEN":
			if(args.length < 2) { printUsage(); return; }
			seed = Integer.parseInt(args[1]);
			numpiles = (args.length < 3 ? 17 : Integer.parseInt(args[2])) ;
			ranks = (args.length < 4 ? 13 : Integer.parseInt(args[3])) ;
			suits = (args.length < 5 ? 4 : Integer.parseInt(args[4])) ;


			layout = new LBSLayout(ranks,suits);
			layout.randomise(seed,numpiles);
			layout.print();
			stdInScanner.close();
			return;
			
		case "SOLVE":
			// Provided code for reading input of SOLVE functionality
			if (args.length<2 || args[1].equals("-")) {
				layout = new LBSLayout(readIntArray(stdInScanner));
			}
			else { 
				layout = new LBSLayout(readIntArray(args[1]));
			}

			// 200004184 SOLVE Implementation
			//
			// // Given a LBS game layout, find a proposed solution or determine if game is unsolvable
			//
			// // REFERENCES:
			// [1]: https://stackoverflow.com/questions/8452672/java-howto-arraylist-push-pop-shift-and-unshift
			// [2]: https://www.baeldung.com/java-pass-by-value-or-pass-by-reference
			// [3]: https://javarevisited.blogspot.com/2011/02/how-hashmap-works-in-java.html#axzz7igW7lWWg
			// [4]: https://stackoverflow.com/questions/5805602/how-to-sort-list-of-objects-by-some-property
			// //
			//
			// // Dated 23/10/22-25/10/22

			// Instantiate a checker object to access functions regarding the checking of the LBS solutions
			solver = new LBSSolver(layout);

			// Create an initial state object from the user provided game of accordion.
			LBSState initialState = new LBSState(layout);

			// Construct a storage list of ordered States to expand in DFS until solution found or list becomes empty.
			Stack<LBSState> searchStates = new Stack<>();
			searchStates.push(initialState);

			// Create flag variable that sets to true and terminates DFS if a solution state is reached.
			// // Initialize this flag by testing if the submitted game is in fact a one pile solution.
			boolean solutionFound = solver.solutionTest(initialState);

			// Conduct DFS on Late Binding Solitaire game as specified in L04-Search-1 Slides 31-32.
			while(!solutionFound && !searchStates.isEmpty()){
				ArrayList<LBSState> newStates;

				LBSState currentState = searchStates.pop();

				newStates = solver.DFS_Expand(currentState);

				solutionFound = solver.getSolutionFound();

				searchStates.addAll(newStates);
			}
			// Depth First Search ended.
			// // If this was due to a solution being found, form appropriate output.
			// // Otherwise, form the accepted output for the problem being deemed unsolvable.
			if(solutionFound){
				System.out.println(solver.getFinalSolution());
			}
			else{
				System.out.println("-1");
			}
			// End Sequence
			stdInScanner.close();
			return;

		case "GRACESOLVE":

		case "SOLVEGRACE":
			// Provided code for reading input of SOLVEGRACE functionality.
			if (args.length<2 || args[1].equals("-")) {
				layout = new LBSLayout(readIntArray(stdInScanner));
			}
			else { 
				layout = new LBSLayout(readIntArray(args[1]));
			}

			// 200004184 GRACESOLVE Implementation
			//
			// // Given a LBS game layout, find a proposed solution or determine if game is unsolvable with Saving Grace.
			//
			// // Dated 25/10/22

			// Code directly copied from SOLVE functionality except where annotated to implement Saving Grace.

			solver = new LBSSolver(layout);
			boolean solutionFoundSG = solver.getSolutionFound();

			LBSState initialStateSG = new LBSState(layout);
			Stack<LBSState> searchStatesSG = new Stack<>();
			searchStatesSG.push(initialStateSG);


			while(!solutionFoundSG && !searchStatesSG.isEmpty()){

				LBSState currentState = searchStatesSG.pop();
				currentState.print();

				ArrayList<LBSState> newStates = solver.SG_Expand(currentState);

				solutionFoundSG = solver.getSolutionFound();

				searchStatesSG.addAll(newStates);
			}

			if(solutionFoundSG){
				System.out.println(solver.getFinalSolution());
				stdInScanner.close();
				return;
			}

			else{
				System.out.println("-1");
				stdInScanner.close();
				return;
			}


		case "CHECK":
			// Provided code for reading input of CHECK functionality.
			if (args.length < 2 || 
			    ( args[1].equals("-") && args.length < 3) || 
			    ( args[1].equals("-") && args[2].equals("-"))
			   ) 
			{ printUsage(); return; }
			if (args[1].equals("-")) {
				layout = new LBSLayout(readIntArray(stdInScanner));
			}
			else { 
				layout = new LBSLayout(readIntArray(args[1]));
			}
			if (args.length < 3 || args[2].equals("-")) {
				workingList = readIntArray(stdInScanner);
			}
			else { 
				workingList = readIntArray(args[2]);
			}

			// 200004184 CHECK Implementation
			//
			// // Given a LBS game layout, test whether a proposed solution on input stream results in solved game.
			//
			// // Dated 21/10/22-22/10/22

			// Instantiate a checker object to access functions regarding the checking of the LBS solutions
			checker = new LBSChecker();

			// Handle edge cases that fail, to be improved
			if(checker.invalidSetup(layout, workingList)){
				System.out.println("false");
				return;
			}

			// Store counter of how many moves have been made. Stop reading input after n-1 moves.
			int count = 0;

			// Extract the first digit of the proposed solution, the stated number of moves to complete.
			int movesToWin = workingList.get(0);
			workingList.remove(0);


			while (count != movesToWin){

				int card = workingList.get(0);
				int pilePosition = workingList.get(1);

				// Ensure the move submitted has a valid card suggested to move and valid pile index to move to.
				if(checker.validCards(card,pilePosition,layout)) {

					int cardPosition = layout.cardPosition(card);
					int pileCard = layout.cardAt(pilePosition);

					// Test if a valid move, if so then rearrange the piles and repeat procedure until finished or failure.
					// Also ensure that the suggested move is one or three piles away.
					if ((checker.sameSuit(card, pileCard, layout.numRanks()) || checker.sameRank(card, pileCard, layout.numRanks()))
							&& checker.validMove(cardPosition, pilePosition)) {

						// Place the subject card onto the target pile if a valid move.
						layout.movePiles(pilePosition, card);

						// Remove the transferred pile from the game layout, shuffling all other piles one closer to the start.
						layout.removePile(cardPosition);

						// Remove the completed move instructions from the solution queue, next attempted move now scheduled at array positions 0/1
						workingList.remove(0);
						workingList.remove(0);

						// Increment the counter and continue the proposed solution.
						count++;
					} else {
						System.out.println("false");
						return;
					}
				} else {
					System.out.println("false");
					return;
				}

			}
			// All moves of the proposed LBS solution are valid.
			System.out.println("true");
			stdInScanner.close();
			return;

		case "GRACECHECK":

		case "CHECKGRACE":
			// Provided code for reading input of CHECKGRACE functionality.
			if (args.length < 2 || 
			    ( args[1].equals("-") && args.length < 3) || 
			    ( args[1].equals("-") && args[2].equals("-"))
			   ) 
			   { printUsage(); return; }
			if (args[1].equals("-")) {
				layout = new LBSLayout(readIntArray(stdInScanner));
			}
			else { 
				layout = new LBSLayout(readIntArray(args[1]));
			}
			if (args.length < 3 || args[2].equals("-")) {
				workingList = readIntArray(stdInScanner);
			}
			else { 
				workingList = readIntArray(args[2]);
			}
			// 200004184 CHECKGRACE Implementation
			//
			// // Given a LBS game layout, test whether a proposed solution on input stream results in solved game with a saving grace.
			//
			// // Dated 25/10/22

			// Code directly copied from CHECK functionality except where annotated to implement Saving Grace.

			checker = new LBSChecker();

			if(checker.invalidSetup(layout, workingList)){
				System.out.println("false");
				return;
			}

			// Relabelling of a few vars required due to shared local scope with CHECK.
			int graceCount = 0;
			int graceMovesToWin = workingList.get(0);
			workingList.remove(0);

			// Create flag variable to mark whether the proposed solution has used it's saving grace yet.
			boolean savingGrace = false;

			while (graceCount != graceMovesToWin) {

				int card = workingList.get(0);
				int pilePosition = workingList.get(1);

				if (checker.validCards(card, pilePosition, layout)) {

					int cardPosition = layout.cardPosition(card);
					int pileCard = layout.cardAt(pilePosition);

					if ((checker.sameSuit(card, pileCard, layout.numRanks()) || checker.sameRank(card, pileCard, layout.numRanks()))
							&& checker.validMove(cardPosition, pilePosition)) {
						// Place the subject card onto the target pile if a valid move.
						layout.movePiles(pilePosition, card);

						layout.removePile(cardPosition);

						workingList.remove(0);
						workingList.remove(0);

						// Increment the counter and continue the proposed solution.
						graceCount++;
					} else if (!savingGrace && checker.validMove(cardPosition, pilePosition)) {
						layout.movePiles(pilePosition, card);
						layout.removePile(cardPosition);

						workingList.remove(0);
						workingList.remove(0);

						graceCount++;

						savingGrace = true;
					} else {
						System.out.println("false");
						return;
					}
				}
				else {
					System.out.println("false");
					return;
				}
			}
			System.out.println("true");
			stdInScanner.close();
			return;

		default : 
			printUsage();

		}

	
	}
}
