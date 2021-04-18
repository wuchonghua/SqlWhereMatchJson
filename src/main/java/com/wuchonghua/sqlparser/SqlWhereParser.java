package com.wuchonghua.sqlparser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wuchonghua
 * @date 2021/4/15 17:12
 */
public class SqlWhereParser {

    /**
     *  如果LRU缓存命中 直接返回
     *  否则 使用状态机 分析出sqlWhere的语义 再放入LRU缓存中
     *  返回二维数组 第一层是由or分割的  第二层是由and分割的 只要第二维里有一个为true 整体就是true
     *  例如 condi3 < 3 or condi4 like '%5%' and cond5 =6 返回如下
     *  [[condi3<3], [condi4 like '%5%', cond5=6]]
     */
    public static List<List<Condition>> getAllConditions(String sqlWhere) {
        List<List<Condition>> allConditions = LRUCache.CACHE.get(sqlWhere);
        if (allConditions != null) {
            return allConditions;
        }
        allConditions = new ArrayList<>();
        Condition condition = new Condition();
        List<Condition> oneConditions = new ArrayList<>();
        StringBuilder keyOrValueBuffer = new StringBuilder();
        int len = sqlWhere.length();
        boolean handleValueString = false;
        for (int i = 0; i < len; i++) {
            char curChar = sqlWhere.charAt(i);
            if (TokenManager.getToken() == Token.INIT) {
                if (curChar != ' ') {
                    TokenManager.setToken(Token.KEY);
                    keyOrValueBuffer.append(curChar);
                }
            } else if (TokenManager.getToken() == Token.KEY) {
                if (curChar == '=' || curChar == '>' || curChar == '<' || curChar == ' ') {
                    condition.setKey(keyOrValueBuffer.toString());
                    keyOrValueBuffer.delete(0, keyOrValueBuffer.length());
                    if (curChar == ' ') {
                        TokenManager.setToken(Token.OPERATOR);
                    } else {
                        TokenManager.setToken(Token.VALUE);
                        condition.setOperator(String.valueOf(curChar));
                    }
                } else {
                    keyOrValueBuffer.append(curChar);
                }
            } else if (TokenManager.getToken() == Token.OPERATOR) {
                if (curChar == '=' || curChar == '>' || curChar == '<') {
                    TokenManager.setToken(Token.VALUE);
                    condition.setOperator(String.valueOf(curChar));
                } else if (i + 4 < len && "like ".equalsIgnoreCase(sqlWhere.substring(i, i + 5))) {
                    i += 4;
                    TokenManager.setToken(Token.VALUE);
                    condition.setOperator("like");
                }
            } else if (TokenManager.getToken() == Token.VALUE) {
                if (handleValueString) {
                    if (curChar == '\'') {
                        // 字符串处理结束
                        condition.setStringConditionValue(keyOrValueBuffer.toString());
                        try {
                            // 这个字符串有可能是一个数字
                            condition.setNumberConditionValue(new BigDecimal(keyOrValueBuffer.toString()));
                        } catch (Exception e) {}
                        keyOrValueBuffer.delete(0, keyOrValueBuffer.length());
                        TokenManager.setToken(Token.RELATIONSHIP);
                        handleValueString = false;
                    } else {
                        keyOrValueBuffer.append(curChar);
                    }
                } else {
                    // 可能是字符串处理还没开始 也可能是一个数字
                    if (curChar == '\'') {
                        // 字符串处理开始
                        handleValueString = true;
                    } else {
                        if (curChar != ' ') {
                            // 数字处理过程中
                            keyOrValueBuffer.append(curChar);
                            if (i == len - 1) {
                                // 数字结束
                                condition.setStringConditionValue(keyOrValueBuffer.toString());
                                try {
                                    condition.setNumberConditionValue(new BigDecimal(keyOrValueBuffer.toString()));
                                } catch (Exception e) {}
                                keyOrValueBuffer.delete(0, keyOrValueBuffer.length());
                            }
                        } else if ((curChar == ' ' && keyOrValueBuffer.length() > 0)) {
                            // 数字结束
                            condition.setStringConditionValue(keyOrValueBuffer.toString());
                            try {
                                condition.setNumberConditionValue(new BigDecimal(keyOrValueBuffer.toString()));
                            } catch (Exception e) {}
                            keyOrValueBuffer.delete(0, keyOrValueBuffer.length());
                            TokenManager.setToken(Token.RELATIONSHIP);
                        }
                    }
                }
            } else if (TokenManager.getToken() == Token.RELATIONSHIP) {
                // 把之前的条件放到数组里 开启下一轮条件
                if (i + 2 < len && "or ".equalsIgnoreCase(sqlWhere.substring(i, i + 3))) {
                    oneConditions.add(condition);
                    condition = new Condition();
                    allConditions.add(oneConditions);
                    oneConditions = new ArrayList<>();
                    TokenManager.setToken(Token.INIT);
                    i += 2;
                } else if (i + 3 < len && "and ".equalsIgnoreCase(sqlWhere.substring(i, i + 4))) {
                    oneConditions.add(condition);
                    condition = new Condition();
                    TokenManager.setToken(Token.INIT);
                    i += 3;
                }
            }
        }
        oneConditions.add(condition);
        allConditions.add(oneConditions);
        TokenManager.clear();
        LRUCache.CACHE.put(sqlWhere, allConditions);
        return allConditions;
    }
}
