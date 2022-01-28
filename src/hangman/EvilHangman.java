package hangman;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class EvilHangman {
    int wordLength;
    int guesses;
    EvilHangmanGame game;
    Scanner scanner;
    StringBuilder wordWithGuesses;
    String blankKey;

    public EvilHangman(EvilHangmanGame game, int wordLength, int guesses) {
        this.wordLength = wordLength;
        this.guesses = guesses;
        this.game = game;
        wordWithGuesses = initializeWordWithGuesses();
        blankKey = wordWithGuesses.toString();
    }

    private StringBuilder initializeWordWithGuesses(){
        StringBuilder word = new StringBuilder();
        for (int i = 0; i < wordLength; i++) {
            word.append('-');
        }
        return word;
    }

    public static void main(String[] args) {
        try {
            File fileName = new File(args[0]);
            int wordLength = Integer.parseInt(args[1]);
            int guesses = Integer.parseInt(args[2]);

            EvilHangmanGame game = new EvilHangmanGame();
            game.startGame(fileName, wordLength);

            EvilHangman gameLoop = new EvilHangman(game, wordLength, guesses);
            gameLoop.run();
        } catch (IOException e){

        } catch (EmptyDictionaryException e) {

        } catch (GuessAlreadyMadeException e){
            System.out.println(e.toString());
        }
    }

    public void run() throws GuessAlreadyMadeException{
        scanner = new Scanner(System.in);

        try {
            Set<String> remainingWords = null;
            while(guesses > 0){
                char guess = promptUser();
                remainingWords = game.makeGuess(guess);
                String lastKey = game.lastKey;
                updateWordWithGuesses(lastKey);
                int numLetters = game.findNumLetters(lastKey);
                int totalLetters = game.findNumLetters(wordWithGuesses.toString());
                if(lastKey.equals(blankKey)){
                    guesses--;
                    System.out.printf("Sorry, there are no %c%n", guess);
                } else {
                    System.out.printf("Yes, there is %d %c%n", numLetters, guess);
                    if(totalLetters == wordLength){
                        break;
                    }
                }
                System.out.print("\n");
            }
            if(guesses > 0){
                System.out.printf("You win! You guessed the word: %s", wordWithGuesses.toString());
            } else {
                Iterator<String> itr = remainingWords.iterator();
                String word = itr.next();
                System.out.printf("Sorry, you lost! The word was: %s", word);
            }

        } finally {
            scanner.close();
        }

    }

    public void updateWordWithGuesses(String key){
        for (int i = 0; i < key.length(); i++) {
            char currChar = key.charAt(i);
            if(currChar != '-'){
                wordWithGuesses.insert(i, currChar);
                wordWithGuesses.deleteCharAt(i + 1);
            }
        }
    }

    public char promptUser(){
        System.out.printf("You have %d guesses left%n", guesses);
        Set<Character> usedLetters = game.getGuessedLetters();
        System.out.print("Used letters: ");
        System.out.println(usedLetters);
        System.out.printf("Word: %s%n", wordWithGuesses.toString());

        char inputChar;
        while(true){
            //I will accept all character inputs given with a whitespace between them
            System.out.print("Enter guess: ");
            String input = scanner.next();
            int lower = 'a';
            int upper = 'z';
            input = input.toLowerCase();

            if(input.equals("\n")){
                continue;
            }
            if(input.length() > 1) {
                System.out.print("Invalid input! ");
                continue;
            }
            inputChar = input.charAt(0);
            if(inputChar < lower || inputChar > upper) {
                System.out.print("Invalid input! ");
                continue;
            } else if (usedLetters.contains(inputChar)){
                System.out.print("Guess already made! ");
                continue;
            }
            break;
        }
        return inputChar;
    }
}
