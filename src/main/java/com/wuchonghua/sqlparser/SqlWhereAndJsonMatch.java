package com.wuchonghua.sqlparser;

import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wuchonghua
 * @date 2021/4/15 17:15
 */
public class SqlWhereAndJsonMatch {

    private String sqlWhere;

    private String json;

    public SqlWhereAndJsonMatch(String sqlWhere, String json) {
        this.sqlWhere = sqlWhere;
        this.json = json;
    }

    public boolean match() {
        List<List<Condition>> allConditions = SqlWhereParser.getAllConditions(sqlWhere);
        JSONObject j = JSONObject.parseObject(json);
        return matchAllConditions(allConditions, j);
    }

    private boolean matchAllConditions(List<List<Condition>> allConditions, JSONObject j) {
        for (List<Condition> oneConditions : allConditions) {
            if (matchOneConditions(oneConditions, j)) {
                return true;
            }
        }
        return false;
    }

    private Object getJsonValueBySqlWhereKey(String sqlWhereKey, JSONObject j) {
        if (sqlWhereKey == null || j == null) {
            return null;
        }
        if (sqlWhereKey.contains(".")) {
            String pre = sqlWhereKey.substring(0, sqlWhereKey.indexOf("."));
            return getJsonValueBySqlWhereKey(sqlWhereKey.substring(sqlWhereKey.indexOf(".") + 1), j.getJSONObject(pre));
        } else {
            return j.get(sqlWhereKey);
        }
    }

    private boolean matchOneConditions(List<Condition> oneConditions, JSONObject j) {
        for (Condition condition : oneConditions) {
            String conditionKey = condition.getKey();
            Object jsonValue = getJsonValueBySqlWhereKey(conditionKey, j);
            if (jsonValue == null) {
                return false;
            }
            String stringJsonValue = jsonValue.toString();
            BigDecimal numberJsonValue = null;
            try {
                numberJsonValue = new BigDecimal(stringJsonValue);
            } catch (Exception e) {}
            String operator = condition.getOperator();
            BigDecimal numberConditionValue = condition.getNumberConditionValue();
            String stringConditionValue = condition.getStringConditionValue();
            if (numberConditionValue != null) {
                // sql条件值是一个数字
                if (!compareNumber(numberConditionValue, numberJsonValue, operator)) {
                    return false;
                }
            } else {
                // sql条件是一个字符串
                if ("=".equals(operator)) {
                    if (!stringConditionValue.equals(stringJsonValue)) {
                        return false;
                    }
                } else if ("like".equals(operator)) {
                    char start = stringConditionValue.charAt(0);
                    char end = stringConditionValue.charAt(stringConditionValue.length() - 1);
                    if (start == '%' && end == '%') {
                        if (!stringJsonValue.contains(stringConditionValue.substring(1, stringConditionValue.length() - 1))) {
                            return false;
                        }
                    } else if (end == '%') {
                        if (!stringJsonValue.startsWith(stringConditionValue.substring(0, stringConditionValue.length() - 1))) {
                            return false;
                        }
                    } else if (start == '%') {
                        if (!stringJsonValue.endsWith(stringConditionValue.substring(1))) {
                            return false;
                        }
                    } else {
                        if (!stringConditionValue.equals(stringJsonValue)) {
                            return false;
                        }
                    }
                } else {
                    // 运算符是大于/小于 但是sql的value解析出来又不是数字
                    return false;
                }
            }
        }
        return true;
    }

    private boolean compareNumber(BigDecimal numberConditionValue, BigDecimal numberJsonValue, String operator) {
        if (numberJsonValue == null || numberConditionValue == null) {
            return false;
        }
        if ("=".equals(operator)) {
            if (numberConditionValue.compareTo(numberJsonValue) != 0) {
                return false;
            }
        } else if ("<".equals(operator)) {
            if (numberConditionValue.compareTo(numberJsonValue) <= 0) {
                return false;
            }
        } else if (">".equals(operator)) {
            if (numberConditionValue.compareTo(numberJsonValue) >= 0) {
                return false;
            }
        } else {
            // like运算符
            return false;
        }
        return true;
    }

}
