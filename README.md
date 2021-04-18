实现一个类SQL where匹配JSON数据的过程
-要求：
1. SQL需要支持and, or, =, >, <, like这几个操作符，可以不支持括号；
2. JSON数据可以不支持多层嵌套，支持则更优；
3. JSON反序列化过程可引用外部组件；
4. 提供单元测试用例；
5. 假设“若干条固定的SQL where匹配由消息系统持续传入的多条JSON数据”的场景，性能越高越佳。

输入
1.JSON字符串
2.SQL字符串
输出
true 或 false
-示例：
JSON 如：
```
{
    "a": 1, 
    "b": {
        "c": 1.23
    },
    "d": "hello" 
}
```
SQL 如:a = "something",输出为 false,
SQL 如:a = 2 and d like "he%" or a = 1,输出为 true,
可以不支持多层嵌套，支持则更优，SQL如：a = 2 or b.c < 3,输出为true
