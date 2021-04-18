package com.wuchonghua.sqlparser;

import java.math.BigDecimal;

/**
 * @author wuchonghua
 * @date 2021/4/8 21:53
 */
public class Condition {

    private String key;

    private String operator;

    private BigDecimal numberConditionValue;

    private String stringConditionValue;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public BigDecimal getNumberConditionValue() {
        return numberConditionValue;
    }

    public void setNumberConditionValue(BigDecimal numberConditionValue) {
        this.numberConditionValue = numberConditionValue;
    }

    public String getStringConditionValue() {
        return stringConditionValue;
    }

    public void setStringConditionValue(String stringConditionValue) {
        this.stringConditionValue = stringConditionValue;
    }

}
