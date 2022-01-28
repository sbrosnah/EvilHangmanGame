package hangman;

public class EmptyDictionaryException extends Exception {
	//Thrown when dictionary file is empty or no words in dictionary match the length asked for
    String message;
    public EmptyDictionaryException() {
        message = "Empty dictionary!";
    }

    @Override
    public String toString() {
        return message;
    }
}
