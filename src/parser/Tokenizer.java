package parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for tokenizing raw SQL strings into tokens.
 */
public class Tokenizer {
    
    /**
     * Converts an SQL-like string into a list of tokens.
     */
    public List<String> tokenize(String sql) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < sql.length(); i++) {
            char c = sql.charAt(i);
            
            if (c == '\'') {
                inQuotes = !inQuotes;
                currentToken.append(c);
            } else if (!inQuotes && (c == ' ' || c == ',' || c == '(' || c == ')' || c == '=')) {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
                if (c != ' ') {
                    tokens.add(String.valueOf(c));
                }
            } else {
                currentToken.append(c);
            }
        }
        
        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }
        
        return tokens;
    }
}
