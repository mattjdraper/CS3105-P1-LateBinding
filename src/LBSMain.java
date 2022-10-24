import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

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
       
        if(args.length < 1) { printUsage(); return; };


	switch (args[0].toUpperCase()) {

		case "GEN":
			if(args.length < 2) { printUsage(); return; };
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
			// [1]:
			// [2]:
			// [3]:
			// //
			//
			// // Dated 23/10/22-XX/10/22

			// Instantiate a checker object to access functions regarding the checking of the LBS solutions
			solver = new LBSSolver(layout);

			// End Sequence
			stdInScanner.close();
			return;

		case "GRACESOLVE":

		case "SOLVEGRACE":
			if (args.length<2 || args[1].equals("-")) {
				layout = new LBSLayout(readIntArray(stdInScanner));
			}
			else { 
				layout = new LBSLayout(readIntArray(args[1]));
			}
			
			// YOUR CODE HERE

			stdInScanner.close();
			return;

		case "CHECK":
			// Provided code for reading input of CHECK functionality.
			if (args.length < 2 || 
			    ( args[1].equals("-") && args.length < 3) || 
			    ( args[1].equals("-") && args[2].equals("-"))
			   ) 
			{ printUsage(); return; };
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
			// // REFERENCES:
			// [1]:
			// [2]:
			// [3]:
			// //
			//
			// // Dated 21/10/22-22/10/22

			// Instantiate a checker object to access functions regarding the checking of the LBS solutions
			checker = new LBSChecker();

			// Temp storage for testing outputs
			// System.out.printf("\nRANKS: " + Integer.toString(layout.numRanks()));
			// System.out.printf("\nSUITS: " + Integer.toString(layout.numSuits()));
			// System.out.printf("\nNUMBER PILES: " + Integer.toString(layout.numPiles()));
			// System.out.printf("\nLBS LAYOUT: "); layout.print();
			// System.out.printf("\nPROPOSED SOLUTION: " + workingList.toString() + "\n");

			// Handle edge cases that fail, to be improved
			if(checker.invalidSetup(layout, workingList) == true){
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
			if (args.length < 2 || 
			    ( args[1].equals("-") && args.length < 3) || 
			    ( args[1].equals("-") && args[2].equals("-"))
			   ) 
			   { printUsage(); return; };
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

			// YOUR CODE HERE
                        
			stdInScanner.close();
			return;

		default : 
			printUsage(); 
			return;

		}

	
	}
}
