package me.guimy.jdbc;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * Proxy Connection, can only be created by {@link ProxyDataSource#getConnection}
 */
public class ProxyConnection implements Connection {
    
    private Connection connection;
    
    ProxyConnection(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public Statement createStatement() throws SQLException {
        return connection.createStatement();
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql) throws SQLException {
        return new ProxyPreparedStatement(connection, sql);
    }
    
    @Override
    public CallableStatement prepareCall(final String sql) throws SQLException {
        return null;
    }
    
    @Override
    public String nativeSQL(final String sql) throws SQLException {
        return null;
    }
    
    @Override
    public void setAutoCommit(final boolean autoCommit) throws SQLException {
        connection.setAutoCommit(autoCommit);
    }
    
    @Override
    public boolean getAutoCommit() throws SQLException {
        return connection.getAutoCommit();
    }
    
    @Override
    public void commit() throws SQLException {
        connection.commit();
    }
    
    @Override
    public void rollback() throws SQLException {
        connection.rollback();
    }
    
    @Override
    public void close() throws SQLException {
        if (connection.isClosed()) {
            return;
        }
        connection.close();
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        return connection.isClosed();
    }
    
    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return connection.getMetaData();
    }
    
    @Override
    public void setReadOnly(final boolean readOnly) throws SQLException {
        
    }
    
    @Override
    public boolean isReadOnly() throws SQLException {
        return false;
    }
    
    @Override
    public void setCatalog(final String catalog) throws SQLException {
        
    }
    
    @Override
    public String getCatalog() throws SQLException {
        return null;
    }
    
    @Override
    public void setTransactionIsolation(final int level) throws SQLException {
        
    }
    
    @Override
    public int getTransactionIsolation() throws SQLException {
        return 0;
    }
    
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }
    
    @Override
    public void clearWarnings() throws SQLException {
        
    }
    
    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency) throws SQLException {
        return connection.createStatement();
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        return new ProxyPreparedStatement(connection, sql, resultSetType, resultSetConcurrency);
    }
    
    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        return null;
    }
    
    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return null;
    }
    
    @Override
    public void setTypeMap(final Map<String, Class<?>> map) throws SQLException {
        
    }
    
    @Override
    public void setHoldability(final int holdability) throws SQLException {
        
    }
    
    @Override
    public int getHoldability() throws SQLException {
        return 0;
    }
    
    @Override
    public Savepoint setSavepoint() throws SQLException {
        return null;
    }
    
    @Override
    public Savepoint setSavepoint(final String name) throws SQLException {
        return null;
    }
    
    @Override
    public void rollback(final Savepoint savepoint) throws SQLException {
        
    }
    
    @Override
    public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
        
    }
    
    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        return null;
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        return new ProxyPreparedStatement(connection, sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }
    
    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        return null;
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) throws SQLException {
        throw new UnsupportedOperationException("unsupported");
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final String[] columnNames) throws SQLException {
        throw new UnsupportedOperationException("unsupported");
    }
    
    @Override
    public Clob createClob() throws SQLException {
        return null;
    }
    
    @Override
    public Blob createBlob() throws SQLException {
        return null;
    }
    
    @Override
    public NClob createNClob() throws SQLException {
        return null;
    }
    
    @Override
    public SQLXML createSQLXML() throws SQLException {
        return null;
    }
    
    @Override
    public boolean isValid(final int timeout) throws SQLException {
        return false;
    }
    
    @Override
    public void setClientInfo(final String name, final String value) throws SQLClientInfoException {
        
    }
    
    @Override
    public void setClientInfo(final Properties properties) throws SQLClientInfoException {
        
    }
    
    @Override
    public String getClientInfo(final String name) throws SQLException {
        return null;
    }
    
    @Override
    public Properties getClientInfo() throws SQLException {
        return null;
    }
    
    @Override
    public Array createArrayOf(final String typeName, final Object[] elements) throws SQLException {
        return null;
    }
    
    @Override
    public Struct createStruct(final String typeName, final Object[] attributes) throws SQLException {
        return null;
    }
    
    @Override
    public void setSchema(final String schema) throws SQLException {
        connection.setSchema(schema);        
    }
    
    @Override
    public String getSchema() throws SQLException {
        return connection.getSchema();
    }
    
    @Override
    public void abort(final Executor executor) throws SQLException {
        connection.abort(executor);
    }
    
    @Override
    public void setNetworkTimeout(final Executor executor, final int milliseconds) throws SQLException {
        connection.setNetworkTimeout(executor, milliseconds);
    }
    
    @Override
    public int getNetworkTimeout() throws SQLException {
        return connection.getNetworkTimeout();
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {
        return new ProxyPreparedStatement(connection, sql, autoGeneratedKeys);
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        return connection.unwrap(iface);
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return connection.isWrapperFor(iface);
    }
    
    public Connection getConnection() {
        return connection;
    }
}
