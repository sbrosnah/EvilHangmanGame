package hangman;

public class GuessAlreadyMadeException extends Exception {
    String message;
    public GuessAlreadyMadeException() {
        message = "Guess already made!";
    }

    @Override
    public String toString() {
        return message;
    }
}
