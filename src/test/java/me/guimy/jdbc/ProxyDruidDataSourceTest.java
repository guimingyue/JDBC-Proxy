package me.guimy.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import me.guimy.meta.TableName;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.Assert;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ProxyDruidDataSourceTest {
    
    @Test
    public void testQuickStart() throws SQLException {
        // step 1: create datasource
        JdbcDataSource jdbcDataSource = new JdbcDataSource();
        jdbcDataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        // step 2: proxy datasource
        ProxyDataSource dataSource = new ProxyDataSource(jdbcDataSource);
        // step 3: JDBC operation
        Connection conn = dataSource.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement("CREATE TABLE user (id INT primary key, name VARCHAR(40), age INT)");
        preparedStatement.executeUpdate();
        preparedStatement.close();
        
        preparedStatement = conn.prepareStatement("insert into ? values(?,?, ?)");
        preparedStatement.setObject(1, new TableName("user"));
        preparedStatement.setLong(2, 20);
        preparedStatement.setString(3, "name");
        preparedStatement.setInt(4, 10);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        
        preparedStatement = conn.prepareStatement("select id, name, age from ? where id >= ? order by id desc");
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
    }
    
    @Test
    public void testProxyH2DataSource() throws SQLException {
        JdbcDataSource jdbcDataSource = new JdbcDataSource();
        jdbcDataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        testProxy0(jdbcDataSource);
    }
    
    @Test
    public void testProxyDruidDataSource() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setMinIdle(1);
        druidDataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        druidDataSource.setTestOnBorrow(false);
        druidDataSource.setRemoveAbandoned(true);
        druidDataSource.setRemoveAbandonedTimeout(600);
        testProxy0(druidDataSource);
    }
    
    void testProxy0(DataSource ds) throws SQLException {
        ProxyDataSource dataSource = new ProxyDataSource(ds);
    
        JdbcUtils.execute(dataSource, "CREATE TABLE user (id INT primary key, name VARCHAR(40), age INT)");
        JdbcUtils.execute(dataSource, "insert into user values(20,'name1', 13)");
        JdbcUtils.execute(dataSource, "insert into user values(30,'name2', 14)");
        List<Map<String, Object>> res = JdbcUtils.executeQuery(dataSource, "select id, name from ? where id >= ? order by id desc",
                new TableName("user"), 20);
        Assert.assertEquals(2, res.size());
    
        res = JdbcUtils.executeQuery(dataSource, "select id, name from ? where id >= ? and name=? order by id desc ",
                new TableName("user"), 20, "name1");
        Assert.assertEquals(1, res.size());
    
        res = JdbcUtils.executeQuery(dataSource, "select id, name from user where id >= ? and name=? and age=? order by id desc ",
                20, "name1", 14);
        Assert.assertEquals(0, res.size());
    
        res = JdbcUtils.executeQuery(dataSource, "select id, name from user where id >= ? and name=? and age=? order by id desc ",
                20, "name1", 13);
        Assert.assertEquals(1, res.size());
    }
    
}
