package hangman;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame{
    Set<String> wordList;
    SortedSet<Character> lettersUsed;
    HashMap<String, Set<String>> currDictionary;
    char currGuess;
    public String lastKey;
    int recursionCount;
    int wordLength;

    public EvilHangmanGame() {
        currDictionary = new HashMap<>();
    }
    @Override
    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
        Scanner scanner = new Scanner(dictionary);
        this.wordLength = wordLength;
        wordList = new HashSet<>();
        lettersUsed = new TreeSet<>();
        try {
            if(!scanner.hasNext()) {
                throw new EmptyDictionaryException();
            }
            while(scanner.hasNext()){
                String word = scanner.next();
                word = word.toLowerCase();
                if(word.length() == wordLength) {
                    wordList.add(word);
                }
            }
            if(wordList.size() == 0){
                throw new EmptyDictionaryException();
            }

        } finally {
            scanner.close();
        }

    }

    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        currDictionary.clear();
        currGuess = Character.toLowerCase(guess);
        String bestKey;

        if(lettersUsed.contains(currGuess)){
            throw new GuessAlreadyMadeException();
        }

        lettersUsed.add(currGuess);

        loadDictionary();

        bestKey = findBestKey();
        lastKey = bestKey;

        wordList = currDictionary.get(bestKey);
        return wordList;
    }

    private String findBestKey() {
        String bestKey = null;
        Set<String> maxKeys = findMaxKeys();
        if(maxKeys.size() == 1) {
            for(String key : maxKeys){
                return key;
            }
        }

        Set<String> fewestLetters = findFewestLetters(maxKeys);
        if(fewestLetters.size() == 1) {
            for(String key : fewestLetters){
                return key;
            }
        }

        bestKey = findRightMost(fewestLetters);

        return bestKey;
    }

    private String findRightMost(Set<String> keys) {
        recursionCount = 0;
        return findRightMostHelper(keys);
    }

    private String findRightMostHelper(Set<String> keys){
        //Base case
        if(keys.size() == 1) {
            for(String key : keys) {
                return key;
            }
        }

        Set<String> rightMoseKeys = new HashSet<>();

        int maxVal = 0;
        for(String key : keys){
            int lettersToSkip = recursionCount;
            for (int i = 0; i < key.length(); i++) {
                char currChar = key.charAt(i);
                if(lettersToSkip == 0 && currChar != '-'){
                    if(i > maxVal) {
                        rightMoseKeys.clear();
                        rightMoseKeys.add(key);
                        maxVal = i;
                    } else if (i == maxVal) {
                        rightMoseKeys.add(key);
                    }
                    break;
                } else if (currChar != '-'){
                    lettersToSkip--;
                }
            }
        }
        recursionCount++;
        return findRightMostHelper(rightMoseKeys);
    }

    private Set<String> findFewestLetters(Set<String> keys) {
        int min = wordLength;
        Set<String> fewestLetters = new HashSet<>();

        for(String key : keys) {
            int numLetters = findNumLetters(key);
            if(numLetters < min){
                fewestLetters.clear();
                min = numLetters;
                fewestLetters.add(key);
            } else if (numLetters == min) {
                fewestLetters.add(key);
            }
        }
        return fewestLetters;
    }

    public int findNumLetters(String key) {
        int numLetters = 0;
        for (int i = 0; i < key.length(); i++) {
            char currChar = key.charAt(i);
            if(currChar != '-'){
                numLetters++;
            }
        }
        return numLetters;
    }

    private Set<String> findMaxKeys(){
        int maxVal = 0;
        Set<String> maxKeys = new HashSet<>();
        Set<String> keys = currDictionary.keySet();
        for(String key : keys){
            Set<String> currSet = currDictionary.get(key);
            if(currSet.size() > maxVal) {
                maxKeys.clear();
                maxVal = currSet.size();
                maxKeys.add(key);
            } else if ( currSet.size() == maxVal) {
                maxKeys.add(key);
            }
        }
        return maxKeys;
    }

    @Override
    public SortedSet<Character> getGuessedLetters() {
        return lettersUsed;
    }

    private void loadDictionary() {
        for(String word : wordList){
            String key = getKey(word);
            Set<String> currSet;
            if(currDictionary.containsKey(key)){
                currSet = currDictionary.get(key);
                currSet.add(word);
            } else {
                currSet = new HashSet<>();
                currSet.add(word);
                currDictionary.put(key, currSet);
            }
        }
    }

    private String getKey(String word){
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            char currChar = word.charAt(i);
            if(currChar == currGuess){
                key.append(currGuess);
            } else {
                key.append('-');
            }
        }
        return key.toString();
    }
}
