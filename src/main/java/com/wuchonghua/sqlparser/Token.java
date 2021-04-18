package com.wuchonghua.sqlparser;

/**
 * @author wuchonghua
 * @date 2021/4/15 8:54
 */
public enum Token {

    INIT, // 初始状态
    KEY, // 接收字段名
    OPERATOR, // 接收运算符
    VALUE, // 接收值
    RELATIONSHIP // and or 连接关系
}
