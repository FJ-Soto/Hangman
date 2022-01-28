import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class HangmanGame {

	public static void main(String[] args) {
		try {
			Scanner kbd = new Scanner(System.in);
			Scanner fileIn = new Scanner (new File(FileCheck()));
			ArrayList<String> words = new ArrayList<String>();
			String answer = "no";
			ReadFile(fileIn, words);
			int count = 0;
			do { 
				String word = words.get(count++);
				char[] wordLetters = word.toCharArray(), foundLetters = new char [wordLetters.length], 
						usedLetters = new char [26];
				int totalLetters = LetterCount(wordLetters), guessCount = 0, found = 0, wrongCount = 0;
				char guess;
				for (int i = 1; i <= 50; i++) {
					System.out.printf(i!=50? "-":"%n");
				}
				System.out.println("If you want to guess, type 0 instead of a letter!");
				System.out.printf("The word/phrase has %d %s: ", totalLetters, totalLetters==1? "letter":"letters");
				ShowBlanks(word, foundLetters);
				do {
					guess = InputedChar(kbd);
					if (Character.isLetter(guess) && CheckIfNewGuess(guess, usedLetters)) {
						AddNewLetterToUsed(guessCount++, usedLetters, guess);
						if (FoundInWord(guess, wordLetters)) {
							found += Reoccurences(guess, wordLetters);
						} else {
							wrongCount++;
							DeathProgress(wrongCount, word, guess, false);
						}
						System.out.println();
						RevealMechanism(found, totalLetters, wrongCount, guess, wordLetters, foundLetters);
					} else if (guess=='0') {
						found = IsGuessCorrect(kbd, word)? totalLetters: found;
						boolean done = found==totalLetters;
						wrongCount+= done? 0:1;
						DeathProgress(wrongCount, word, guess, done);
					} else if (!Character.isLetter(guess)) {
						System.out.printf("Not a letter!%n");
					} else {
						LettersTriedAlready(usedLetters);
					}
					DrawManIfWin(word, totalLetters, found);
				} while(GameInProgress(found, totalLetters, wrongCount)); 
				System.out.printf(count!=words.size()? "Play again? ":"");
				answer = count!=words.size()? kbd.nextLine():"no";
			} while (answer.toLowerCase().contains("y"));
			kbd.close();
			fileIn.close();
		} catch (Exception file) {
			System.out.println("File error: " + file.getMessage());
			System.out.println("Make sure you've typed a word/phrase that contains letters into the document.");
		}		
	}

//	METHODS BELOW	//

//	Reads file.	
	private static void ReadFile(Scanner fileIn, ArrayList<String> words) {
		boolean random = fileIn.nextLine().toLowerCase().contains("true");
		fileIn.nextLine(); fileIn.nextLine();
		WordsToList(fileIn, random, words);	
	}

//	Creates an array with words.
	private static void WordsToList(Scanner fileIn, boolean random, ArrayList<String> words) {
		while(fileIn.hasNextLine()) {
			String word = fileIn.nextLine().trim();
			if(!word.isEmpty() && IsWord(word)) {
				words.add(word);
			}
		}		
		if (random) {
			Collections.shuffle(words);
		}
	}

//	Blanks out all letters. usedLetters is used to preserve original array intact.
	private static void ShowBlanks(String word, char[] usedLetters) {
		for (int i = 0; i < word.length(); i++) {
			usedLetters[i] = Character.isLetter(word.charAt(i))? '_':word.charAt(i);
		}
		for (char e: usedLetters) {
			System.out.printf("%s", e=='_'? e + " ": e);
		}
		System.out.println();
	}

//	Adds letter to array.
	private static void AddNewLetterToUsed(int guessCount, char[] usedLetters, char guess) {
		usedLetters[guessCount] = guess;		
	}
	
//	Shows letters that have been tried.
	private static void LettersTriedAlready(char[] usedLetters) {
		System.out.printf("You've tried that already, try a different letter!%n");
		System.out.print("You've tried: ");
		for (char e: usedLetters) {
			System.out.print(Character.toUpperCase(e) + " ");
		}
		System.out.println();
	}
	
//	Shows the letters that have been discovered.
	private static void RevealFoundLetters(char guess, char[] wordLetters, char[] foundLetters) {
		for (int i = 0; i < wordLetters.length; i++) {
			if (Character.toLowerCase(wordLetters[i]) == guess) {
				foundLetters[i] = wordLetters[i];
			}
		}
		for (char e: foundLetters) {
			System.out.print(e+ " ");
		}
	}

//	Shows letters found when adequate.
	private static void RevealMechanism(int found, int totalLetters, int wrongCount, char guess,
			char[] wordLetters, char[] foundLetters) {
		if (wrongCount < 7 && found!=totalLetters) {
			System.out.print(found>0? "You've found: ":"");
			RevealFoundLetters(guess, wordLetters, foundLetters);
		}	
	}

//	Tells and shows progress of user.
	private static void DeathProgress(int wrongCount, String word, char guess, boolean done) {
		int remainingLives = 7-wrongCount;
		if (guess!='0') {
			System.out.printf("\"%s\" is not in the word/phrase! You have %d %s left.%n",
					guess, remainingLives, remainingLives==1? "life":"lives");
			DrawHangman(wrongCount, word);
		} else {
			System.out.printf(done? "":"You have %d %s left.%n", remainingLives, remainingLives==1? "life":"lives");
			DrawHangman(wrongCount, word);
		}
	}

//	Draws happy-man.
	private static void DrawManIfWin(String word, int totalLetters, int found) {
		if (found==totalLetters) {
			System.out.printf("%nYOU GOT IT!%n");
			try {
				Scanner figure = new Scanner(new File("figure.txt"));
				while (figure.hasNextLine() && !figure.nextLine().contains("WIN")) {}
				for(int i = 0; i < 9; i++) {
					System.out.println(figure.nextLine());
				}
				figure.close();
				} catch (Exception error) {
					System.out.println("Error can't find happy man: "+ error.getMessage());
				}	
			System.out.printf("The word/phrase was: \"%s\"%n", word);
		}
	}

//	Draws the man according to wrong guesses.
	private static void DrawHangman(int wrongCount, String word) {
		if(wrongCount<=7 && wrongCount!=0) {
			try {
			Scanner figure = new Scanner(new File("figure.txt"));
			while (figure.hasNextLine() && !figure.nextLine().contains(Integer.toString(wrongCount))) {}
			System.out.println(wrongCount==7? "WRONG! GAME OVER!":"");
			for(int i = 0; i < 9; i++) {
				System.out.println(figure.nextLine());
			}
			System.out.printf(wrongCount==7? ("The word/phrase was: \"" + word +"\""):"");
			figure.close();
			} catch (Exception error) {
				System.out.println("Error can't find man: "+ error.getMessage());
			}
		}
	}
	
//	Returns the user's guess while checking for validity .
	private static char InputedChar(Scanner kbd) {
		String input;
		do {
			System.out.printf("%nEnter a letter! ");
			input = kbd.nextLine().trim().toLowerCase();
		} while ((input.length()!=1));
		return input.charAt(0);
	}

//	Calculates total repeats.
	private static int HowManyRepeats(char[] wordLetters, char guess) {
		int repeats = 0;
		for (char e: wordLetters) {
			repeats += guess==Character.toLowerCase(e)? 1:0;
			}
		return repeats;
	}

//	Returns total repeats of guess.
	private static int Reoccurences(char guess, char[] wordLetters) {
		int repeats = HowManyRepeats(wordLetters, guess), found = 0;
		found+=repeats;
		System.out.printf("The letter \"%s\" appears %d %s.%n", guess, repeats, repeats==1? "time":"times");
		return found;
	}
	
//	Returns number of letters in word.
	private static int LetterCount(char[] wordLetters) {
		int letters = 0;
		for (char e: wordLetters) {
			letters += Character.isLetter(e)? 1:0;
		}
		return letters;
	}

// 	Returns default file if the non-default (hangman.txt) file has words; otherwise, returns hangmandefault.txt.
	private static String FileCheck() {
		boolean empty = true;
		try {
			Scanner check = new Scanner(new File("hangman.txt"));
			for (int i = 0; i < 3; i++) {
				check.nextLine();
			}
			while (check.hasNextLine() && empty) {
				empty = check.nextLine().trim().isEmpty();
			}
			check.close();
		} catch (Exception error) {
			System.out.println("Error: " + error.getMessage());
		}
		return empty? "hangmandefault.txt": "hangman.txt";
	}
	
//	Determines if line has words.
	private static boolean IsWord(String word) {
		char[] wordLetters= word.toCharArray();
		boolean hasWords = false;
		for (char e: wordLetters) {
			if(Character.isLetter(e)) {
				hasWords = true;
			}
		}
		return hasWords;
	}

//	Determines if the inputed character is new.
	private static boolean CheckIfNewGuess(char guess, char[] usedLetters) {
		for(int i = 0; i < usedLetters.length; i++) {
			if(!(usedLetters[i] == 0) && usedLetters[i] == guess ) {
				return false;
			}
		}
		return true;
	}

//	Determines if guess is part of word.
	private static boolean FoundInWord(char guess, char[] wordLetters) {
		for(char e: wordLetters) {
			if (guess==Character.toLowerCase(e)) {
				return true;
			}
		}
		return false;
	}
		
//	States if the user's guess was right.
	private static boolean IsGuessCorrect(Scanner kbd, String word) {
		System.out.print("Enter your guess (enter special characters too!): ");
		String userGuess = kbd.nextLine();
		if(userGuess.equalsIgnoreCase(word)) {
			return true;
		} else {
			System.out.println("Oops, not quite right.");
			return false;
		}
	}

//	Determines if game is still in progress.
	private static boolean GameInProgress(int found, int totalLetters, int wrongCount) {
		return (found<totalLetters && wrongCount<7);
	}
}