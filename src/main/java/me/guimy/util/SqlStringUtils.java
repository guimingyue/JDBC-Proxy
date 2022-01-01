package me.guimy.util;

import java.util.HashMap;
import java.util.Map;

public class SqlStringUtils {
    
    static final char QUOTED_IDENTIFIER_CHAR = '`';
    
    public static boolean startsWithIgnoreCase(String searchIn, int startAt, String searchFor) {
        return searchIn.regionMatches(true, startAt, searchFor, 0, searchFor.length());
    }
    
    public static boolean startsWithIgnoreCaseAndWs(String searchIn, String searchFor, int beginPos) {
        if (searchIn == null) {
            return searchFor == null;
        }
        
        int inLength = searchIn.length();
        
        for (; beginPos < inLength; beginPos++) {
            if (!Character.isWhitespace(searchIn.charAt(beginPos))) {
                break;
            }
        }
        
        return startsWithIgnoreCase(searchIn, beginPos, searchFor);
    }
    
    public static int findStartOfStatement(String sql) {
        int statementStartPos = 0;
        
        if (startsWithIgnoreCaseAndWs(sql, "/*", 0)) {
            statementStartPos = sql.indexOf("*/");
            
            if (statementStartPos == -1) {
                statementStartPos = 0;
            } else {
                statementStartPos += 2;
            }
        } else if (startsWithIgnoreCaseAndWs(sql, "--", 0) || startsWithIgnoreCaseAndWs(sql, "#", 0)) {
            statementStartPos = sql.indexOf('\n');
            
            if (statementStartPos == -1) {
                statementStartPos = sql.indexOf('\r');
                
                if (statementStartPos == -1) {
                    statementStartPos = 0;
                }
            }
        }
        
        return statementStartPos;
    }
    
    static int skipEscape(String sql, int i) {
        while (sql.charAt(i) == '\\' && i < (sql.length() - 1)) {
            i++;
        }
        return i;
    }
    
    static int runToNewLineOrEnd(String sql, int i) {
        int endOfStmt = sql.length() - 1;
        for (; i < endOfStmt; i++) {
            char c = sql.charAt(i);
            if (c == '\r' || c == '\n') {
                break;
            }
        }
        return i;
    }
    
    static int skipComment(String sql, int i) {
        int statementLength = sql.length();
        char cNext = sql.charAt(i + 1);
        if (cNext == '*') {
            // Comment
            i += 2;
        
            for (int j = i; j < statementLength; j++) {
                i++;
                cNext = sql.charAt(j);
            
                if (cNext == '*' && (j + 1) < statementLength) {
                    if (sql.charAt(j + 1) == '/') {
                        i++;
                        break; // comment done
                    }
                }
            }
        }
        return i;
    }
    
    /**
     * Find all placeholder index by parsing SQL text.
     * @param sql
     * @return
     */
    public static Map<Integer, Integer> parsePlaceholderIndex(final String sql) {
        Map<Integer, Integer> placeHolderIdx = new HashMap<>();
        final int statementLength = sql.length();
        final int startPos = findStartOfStatement(sql);
    
        boolean inQuotes = false;
        char quoteChar = 0;
        boolean inQuotedId = false;
    
        for (int i = startPos; i < statementLength; ++i) {
            i = skipEscape(sql, i);
            if (i >= statementLength) {
                break;
            }
            char c = sql.charAt(i);
            // are we in a quoted identifier? (only valid when the id is not inside a 'string')
            if (!inQuotes && (c == QUOTED_IDENTIFIER_CHAR)) {
                inQuotedId = !inQuotedId;
            } else if (!inQuotedId) {
                //	only respect quotes when not in a quoted identifier
                if (inQuotes) {
                    if (((c == '\'') || (c == '"')) && c == quoteChar) {
                        if (i < statementLength - 1 && sql.charAt(i + 1) == quoteChar) {
                            i++;
                            continue; // inline quote escape
                        }
                        inQuotes = false;
                        quoteChar = 0;
                    }
                } else {
                    if (c == '#' || (c == '-' && (i + 1) < statementLength && sql.charAt(i + 1) == '-')) {
                        // run out to end of statement, or newline, whichever comes first
                        i = runToNewLineOrEnd(sql, i);
                        continue;
                    } else if (c == '/' && (i + 1) < statementLength) {
                        i = skipComment(sql, i);
                        if (i < statementLength) {
                            c = sql.charAt(i);
                        }
                    } else if (c == '\'' || c == '"') {
                        inQuotes = true;
                        quoteChar = c;
                    }
                }
            }
        
            if ((c == '?') && !inQuotes && !inQuotedId) {
                placeHolderIdx.put(placeHolderIdx.size(), i);
            }
        }
        return placeHolderIdx;
    } 
}
