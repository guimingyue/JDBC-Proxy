package me.guimy.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import me.guimy.meta.TableName;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ExtendedDruidDataSourceTest {
    
    @Test
    public void test() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setMinIdle(1);
        druidDataSource.setUrl("jdbc:h2:mem:test;");
        druidDataSource.setTestOnBorrow(false);
        druidDataSource.setRemoveAbandoned(true);
        druidDataSource.setRemoveAbandonedTimeout(600);
    
        ExtendedDataSource dataSource = new ExtendedDataSource(druidDataSource);
        
        JdbcUtils.execute(dataSource, "CREATE TABLE user (id INT, name VARCHAR(40))");
        JdbcUtils.execute(dataSource, "insert into user values(20,'name1')");
        JdbcUtils.execute(dataSource, "insert into user values(30,'name2')");
        List<Map<String, Object>> res = JdbcUtils.executeQuery(dataSource, "select id, name from ? where id >= ? order by id desc", 
                new TableName("user"), 20);
        Assert.assertEquals(2, res.size());
    
        res = JdbcUtils.executeQuery(dataSource, "select id, name from ? where id >= ? and name=? order by id desc ",
                new TableName("user"), 20, "name1");
        Assert.assertEquals(1, res.size());
    }
    
}
