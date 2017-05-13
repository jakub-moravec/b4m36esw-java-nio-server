package core;

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
     * Adds new word.
     * @param word word
     */
    void addWord(String word){
        word = word.trim();
        if(!"".equals(word) && !uniqueWords.containsKey(word)) {
            uniqueWords.put(word, 1);
        }
    }

    /**
     * Rerturns number of unique words.
     *
     * @return number of unique words
     */
    int getNumberOfUniqueWords() {
        return uniqueWords.size();
    }

    /**
     * Clears the set of unique words.
     */
    void clear() {
        uniqueWords.clear();
    }
}
