package core;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Words holder.
 *
 * Containts all unique words sent by post request.
 *
 * Implements Singleton pattern.
 */
class WordsHolder {

    /**
     * Singleton instance.
     */
    static final WordsHolder INSTANCE = new WordsHolder();

    /**
     * Set of unique words.
     */
    private static ConcurrentHashMap<String, Integer> uniqueWords = new ConcurrentHashMap<String, Integer>();

    private WordsHolder() {
        //
    }

    /**
     * Adds new set of words.
     *
     * Set is used to be sure that words are unique.
     *
     * The premise is, that it will be faster to add unique words to thread-safe set, than non-unique.
     * @param words words
     */
    void addWords(List<String> words){
        for (String word : words) {
            if(!uniqueWords.containsKey(word)) {
                uniqueWords.put(word, 1);
            }
        }
    }

    /**
     * Rerturns number of unique words.
     *
     * @return number of unique words
     */
    int getNumberOfUniqueWords() {
        return uniqueWords.keySet().size();
    }

    /**
     * Clears the set of unique words.
     */
    void clear() {
        uniqueWords.clear();
    }
}
