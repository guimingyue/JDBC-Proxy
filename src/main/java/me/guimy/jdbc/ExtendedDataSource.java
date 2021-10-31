package me.guimy.jdbc;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class ExtendedDataSource implements DataSource {
    
    private final DataSource dataSource;
    
    public ExtendedDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        return new ExtendedConnection(dataSource.getConnection());
    }
    
    @Override
    public Connection getConnection(final String s, final String s1) throws SQLException {
        return new ExtendedConnection(dataSource.getConnection(s, s1));
    }
    
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }
    
    public void setLogWriter(final PrintWriter printWriter) throws SQLException {
        
    }
    
    public void setLoginTimeout(final int i) throws SQLException {
        
    }
    
    public int getLoginTimeout() throws SQLException {
        return 0;
    }
    
    public Logger getParentLogger()  {
        return null;
    }
    
    public <T> T unwrap(final Class<T> aClass) throws SQLException {
        return null;
    }
    
    public boolean isWrapperFor(final Class<?> aClass) throws SQLException {
        return false;
    }
}
