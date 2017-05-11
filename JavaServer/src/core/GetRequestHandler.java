package core;

import utils.HttpUtils;

/**
 * Handler for GET request.
 * Provides response containing number of unique words.
 */
public class GetRequestHandler {

    /**
     * @return HTTP 200 response with number of unique words
     */
    public static String handleRequest() {
        int numberOfUniqueWords = WordsHolder.getInstance().getNuberOfUniqueWords();
        WordsHolder.getInstance().clear();
        return HttpUtils.createHttpResponse(200, "OK", String.valueOf(numberOfUniqueWords));
    }
}