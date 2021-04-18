package com.wuchonghua.sqlparser.test;

import com.wuchonghua.sqlparser.SqlWhereAndJsonMatch;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author wuchonghua
 * @date 2021/4/15 17:30
 */
public class TestSqlWhereAndJsonMatch {

    @Test
    public void test1() {
        String sqlWhere = "condi3  < '3'     or condi4 like '%5%' and cond5 =6";
        String json = "{\"condi3\":\"3\",\"condi4\":\"1253\",\"cond5\":6}";
        SqlWhereAndJsonMatch match = new SqlWhereAndJsonMatch(sqlWhere, json);
        Assert.assertTrue(match.match());
    }

    @Test
    public void test2() {
        String sqlWhere = "a = 'something'";
        String json = "{\"a\":1,\"b\":{\"c\":1.23},\"d\":\"hello\"}";
        SqlWhereAndJsonMatch match = new SqlWhereAndJsonMatch(sqlWhere, json);
        Assert.assertFalse(match.match());
    }
    @Test
    public void test3() {
        String sqlWhere = "a = 'something'";
        String json = "{\"a\":1,\"b\":{\"c\":1.23},\"d\":\"hello\"}";
        SqlWhereAndJsonMatch match = new SqlWhereAndJsonMatch(sqlWhere, json);
        Assert.assertFalse(match.match());
    }

    @Test
    public void test4() {
        String sqlWhere = "a = 2 and d like 'he%' or a = 1";
        String json = "{\"a\":1,\"b\":{\"c\":1.23},\"d\":\"hello\"}";
        SqlWhereAndJsonMatch match = new SqlWhereAndJsonMatch(sqlWhere, json);
        Assert.assertTrue(match.match());
    }

    @Test
    public void test5() {
        String sqlWhere = "a = 2 or b.c < 3";
        String json = "{\"a\":1,\"b\":{\"c\":1.23},\"d\":\"hello\"}";
        SqlWhereAndJsonMatch match = new SqlWhereAndJsonMatch(sqlWhere, json);
        Assert.assertTrue(match.match());
    }

}
