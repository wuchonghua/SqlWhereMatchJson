package com.wuchonghua.sqlparser;

/**
 * @author wuchonghua
 * @date 2021/4/15 8:55
 */
public class TokenManager {

    private static ThreadLocal<Token> tokenManager = new ThreadLocal<Token>(){
        @Override
        protected Token initialValue() {
            return Token.INIT;
        }
    };

    public static Token getToken() {
        return tokenManager.get();
    }

    public static void setToken(Token token) {
        tokenManager.set(token);
    }

    public static void clear() {
        tokenManager.remove();
    }
}
