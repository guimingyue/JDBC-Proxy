# JDBC-Proxy
JDBC-Proxy is a extensible plugin for the standard JDBC API.

## Use Case

### SQL Rewrite including table name

If you want to rewrite sql and the table names of the output sql will be rewrited too, so you can use the `me.guimy.jdbc.ProxyPreparedStatement` 
to set the new table name with the `java.sql.PreparedStatement` API。

```java
// step 1: create datasource, H2 Database in this example
JdbcDataSource jdbcDataSource = new JdbcDataSource();
jdbcDataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");

// step 2: proxy datasource
ProxyDataSource dataSource = new ProxyDataSource(jdbcDataSource);

// step 3: JDBC operation
Connection conn = dataSource.getConnection();

// step 3.1 create a table
PreparedStatement preparedStatement = conn.prepareStatement("CREATE TABLE user (id INT primary key, name VARCHAR(40), age INT)");
preparedStatement.executeUpdate();
preparedStatement.close();

// step 3.2 insert with table name as a placeholder
preparedStatement = conn.prepareStatement("insert into ? values(?,?, ?)");
// set table name 
preparedStatement.setObject(1, new TableName("user"));
preparedStatement.setLong(2, 20);
preparedStatement.setString(3, "name");
preparedStatement.setInt(4, 10);
preparedStatement.executeUpdate();
preparedStatement.close();

// step 3.3 select with table name as a placeholder
preparedStatement = conn.prepareStatement("select id, name, age from ? where id >= ? order by id desc");
// set table name
preparedStatement.setObject(1, new TableName("user"));
preparedStatement.setLong(2, 20);
ResultSet resultSet = preparedStatement.executeQuery();
Assert.assertTrue(resultSet.next());
Assert.assertEquals(20, resultSet.getLong(1));
Assert.assertEquals("name", resultSet.getString(2));
Assert.assertEquals(10, resultSet.getInt(3));

resultSet.close();
preparedStatement.close();
conn.close();
```
