package core;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Words holder.
 *
 * Containts all unique words sent by post request.
 *
 * Implements Singleton pattern.
 */
public class WordsHolder {

    /**
     * Singleton instance.
     */
    private static WordsHolder instance;

    /**
     * Set of unique words.
     */
    private static Set<String> uniqueWords = new ConcurrentSkipListSet<>();

    private WordsHolder() {
        //
    }

    /**
     * @return singleton instance
     */
    public static WordsHolder getInstance(){
        if(instance == null) {
            synchronized (WordsHolder.class) {
                if(instance == null) {
                    instance = new WordsHolder();
                }
            }
        }
        return instance;
    }

    /**
     * Adds new set of words.
     *
     * Set is used to be sure that words are unique.
     *
     * The premise is, that it will be faster to add unique words to thread-safe set, than non-unique.
     * @param words words
     */
    public void addWords(Set<String> words){
        uniqueWords.addAll(words);
    }

    /**
     * Rerturns number of unique words.
     *
     * FIXME: make sure that all words from actual post request were added
     * @return number of unique words
     */
    public int getNuberOfUniqueWords() {
        return uniqueWords.size();
    }
}
