package me.guimy.util;

public class SqlStringUtils {
    
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
}
