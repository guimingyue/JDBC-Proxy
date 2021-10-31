package me.guimy.jdbc;

import me.guimy.common.Param;
import me.guimy.common.Param.ParamType;
import me.guimy.util.ParamUtils;
import me.guimy.util.SqlStringUtils;
import me.guimy.meta.TableName;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ExtendedPreparedStatement implements PreparedStatement {
    
    private final Connection connection;
    
    private final String originalSql;
    
    private int resultSetType;
    
    private int resultSetConcurrency;
    
    private int resultSetHoldability;
    
    private final Map<Integer, Integer> placeHolderIdx = new HashMap<>();
    
    private Param[] params;
    
    public ExtendedPreparedStatement(final Connection connection, final String sql) {
        this(connection, sql, -1, -1, -1);
    }
    
    public ExtendedPreparedStatement(final Connection connection, final String sql, final int resultSetType,
                                     final int resultSetConcurrency) {
        this(connection, sql, resultSetType, resultSetConcurrency, -1);
    }
    
    public ExtendedPreparedStatement(final Connection connection, final String sql, final int resultSetType, 
                                     final int resultSetConcurrency, final int resultSetHoldability) {
        super();
        this.connection = connection;
        this.originalSql = sql;
        this.resultSetType = resultSetType;
        this.resultSetConcurrency = resultSetConcurrency;
        this.resultSetHoldability = resultSetHoldability;
        
        // parse SQL，find placeholder index.
        final int startPos = SqlStringUtils.findStartOfStatement(sql);
        final int statementLength = sql.length();
        final char quotedIdentifierChar = '`';
    
        boolean inQuotes = false;
        char quoteChar = 0;
        boolean inQuotedId = false;
    
        for (int i = startPos; i < statementLength; ++i) {
            char c = sql.charAt(i);
        
            if (c == '\\' && i < (statementLength - 1)) {
                i++;
                continue; // next character is escaped
            }
        
            // are we in a quoted identifier? (only valid when the id is not inside a 'string')
            if (!inQuotes && (c == quotedIdentifierChar)) {
                inQuotedId = !inQuotedId;
            } else if (!inQuotedId) {
                //	only respect quotes when not in a quoted identifier
            
                if (inQuotes) {
                    if (((c == '\'') || (c == '"')) && c == quoteChar) {
                        if (i < (statementLength - 1) && sql.charAt(i + 1) == quoteChar) {
                            i++;
                            continue; // inline quote escape
                        }
                    
                        inQuotes = false;
                        quoteChar = 0;
                    }
                } else {
                    if (c == '#' || (c == '-' && (i + 1) < statementLength && sql.charAt(i + 1) == '-')) {
                        // run out to end of statement, or newline, whichever comes first
                        int endOfStmt = statementLength - 1;
                    
                        for (; i < endOfStmt; i++) {
                            c = sql.charAt(i);
                        
                            if (c == '\r' || c == '\n') {
                                break;
                            }
                        }
                    
                        continue;
                    } else if (c == '/' && (i + 1) < statementLength) {
                        // Comment?
                        char cNext = sql.charAt(i + 1);
                    
                        if (cNext == '*') {
                            i += 2;
                        
                            for (int j = i; j < statementLength; j++) {
                                i++;
                                cNext = sql.charAt(j);
                            
                                if (cNext == '*' && (j + 1) < statementLength) {
                                    if (sql.charAt(j + 1) == '/') {
                                        i++;
                                    
                                        if (i < statementLength) {
                                            c = sql.charAt(i);
                                        }
                                    
                                        break; // comment done
                                    }
                                }
                            }
                        }
                    } else if ((c == '\'') || (c == '"')) {
                        inQuotes = true;
                        quoteChar = c;
                    }
                }
            }
        
            if ((c == '?') && !inQuotes && !inQuotedId) {
                // Placeholder.
                placeHolderIdx.put(placeHolderIdx.size(), i);
            }
        }
        this.params = new Param[placeHolderIdx.size()];
    }
    
    @Override
    public ResultSet executeQuery() throws SQLException {
        PreparedStatement pstmt;
        if (params.length > 0) {
            StringBuilder realSql = new StringBuilder(originalSql.substring(0, placeHolderIdx.get(0)));
            int validParamIdx = 0;
            for (int paramIdx = 0; paramIdx < this.params.length; paramIdx++) {
                Param param = this.params[paramIdx];
                if (param.getParamType() == ParamType.TABLE_NAME) {
                    String tableName = param.getValue();
                    realSql.append(tableName);
                } else {
                    String sqlSegment = originalSql.substring(placeHolderIdx.get(paramIdx-1)+1, placeHolderIdx.get(paramIdx)+1);
                    realSql.append(sqlSegment);
                    this.params[validParamIdx++] = param;
                }
            }
            int lastParamIdx = placeHolderIdx.get(this.params.length-1);
            if (lastParamIdx < originalSql.length()) {
                realSql.append(originalSql.substring(lastParamIdx+1));
            }
            pstmt = this.connection.prepareStatement(realSql.toString());
            for (int i = 0; i < validParamIdx; i++) {
                ParamUtils.setParam(pstmt, i+1, this.params[i]);
            }
        } else {
            pstmt = this.connection.prepareStatement(originalSql);
        }
        return pstmt.executeQuery();
    }
    
    @Override
    public int executeUpdate() throws SQLException {
        if (this.params.length > 0) {
            throw new UnsupportedOperationException("unsupported");
        } else {
            PreparedStatement pstmt = this.connection.prepareStatement(originalSql);
            return pstmt.executeUpdate();
        }
    }
    
    @Override
    public void setNull(final int i, final int i1) throws SQLException {
        
    }
    
    @Override
    public void setBoolean(final int i, final boolean b) throws SQLException {
        
    }
    
    @Override
    public void setByte(final int i, final byte b) throws SQLException {
        
    }
    
    @Override
    public void setShort(final int i, final short i1) throws SQLException {
        
    }
    
    @Override
    public void setInt(final int i, final int i1) throws SQLException {
        
    }
    
    @Override
    public void setLong(final int i, final long l) throws SQLException {
        
    }
    
    @Override
    public void setFloat(final int i, final float v) throws SQLException {
        
    }
    
    @Override
    public void setDouble(final int i, final double v) throws SQLException {
        
    }
    
    @Override
    public void setBigDecimal(final int i, final BigDecimal bigDecimal) throws SQLException {
        
    }
    
    @Override
    public void setString(final int i, final String s) throws SQLException {
        this.params[i-1] = ParamUtils.getStringParam(s);
    }
    
    @Override
    public void setBytes(final int i, final byte[] bytes) throws SQLException {
        this.params[i-1] = ParamUtils.getBytesParam(bytes);
    }
    
    @Override
    public void setDate(final int i, final Date date) throws SQLException {
        
    }
    
    @Override
    public void setTime(final int i, final Time time) throws SQLException {
        
    }
    
    @Override
    public void setTimestamp(final int i, final Timestamp timestamp) throws SQLException {
        
    }
    
    @Override
    public void setAsciiStream(final int i, final InputStream inputStream, final int i1) throws SQLException {
        
    }
    
    @Override
    public void setUnicodeStream(final int i, final InputStream inputStream, final int i1) throws SQLException {
        
    }
    
    @Override
    public void setBinaryStream(final int i, final InputStream inputStream, final int i1) throws SQLException {
        
    }
    
    @Override
    public void clearParameters() throws SQLException {
        
    }
    
    @Override
    public void setObject(final int i, final Object o, final int i1) throws SQLException {
        
    }
    
    @Override
    public void setObject(final int i, final Object o) throws SQLException {
        if (o instanceof TableName) {
            this.params[i-1] = ParamUtils.getTableNameParam(((TableName) o).getTableName());
            return;
        }
        this.params[i-1] = ParamUtils.getObjectParam(o);
    }
    
    @Override
    public boolean execute() throws SQLException {
        return false;
    }
    
    @Override
    public void addBatch() throws SQLException {
        
    }
    
    @Override
    public void setCharacterStream(final int i, final Reader reader, final int i1) throws SQLException {
        
    }
    
    @Override
    public void setRef(final int i, final Ref ref) throws SQLException {
        
    }
    
    @Override
    public void setBlob(final int i, final Blob blob) throws SQLException {
        
    }
    
    @Override
    public void setClob(final int i, final Clob clob) throws SQLException {
        
    }
    
    @Override
    public void setArray(final int i, final Array array) throws SQLException {
        
    }
    
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return null;
    }
    
    @Override
    public void setDate(final int i, final Date date, final Calendar calendar) throws SQLException {
        
    }
    
    @Override
    public void setTime(final int i, final Time time, final Calendar calendar) throws SQLException {
        
    }
    
    @Override
    public void setTimestamp(final int i, final Timestamp timestamp, final Calendar calendar) throws SQLException {
        
    }
    
    @Override
    public void setNull(final int i, final int i1, final String s) throws SQLException {
        
    }
    
    @Override
    public void setURL(final int i, final URL url) throws SQLException {
        
    }
    
    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return null;
    }
    
    @Override
    public void setRowId(final int i, final RowId rowId) throws SQLException {
        
    }
    
    @Override
    public void setNString(final int i, final String s) throws SQLException {
        
    }
    
    @Override
    public void setNCharacterStream(final int i, final Reader reader, final long l) throws SQLException {
        
    }
    
    @Override
    public void setNClob(final int i, final NClob nClob) throws SQLException {
        
    }
    
    @Override
    public void setClob(final int i, final Reader reader, final long l) throws SQLException {
        
    }
    
    @Override
    public void setBlob(final int i, final InputStream inputStream, final long l) throws SQLException {
        
    }
    
    @Override
    public void setNClob(final int i, final Reader reader, final long l) throws SQLException {
        
    }
    
    @Override
    public void setSQLXML(final int i, final SQLXML sqlxml) throws SQLException {
        
    }
    
    @Override
    public void setObject(final int i, final Object o, final int i1, final int i2) throws SQLException {
        
    }
    
    @Override
    public void setAsciiStream(final int i, final InputStream inputStream, final long l) throws SQLException {
        
    }
    
    @Override
    public void setBinaryStream(final int i, final InputStream inputStream, final long l) throws SQLException {
        
    }
    
    @Override
    public void setCharacterStream(final int i, final Reader reader, final long l) throws SQLException {
        
    }
    
    @Override
    public void setAsciiStream(final int i, final InputStream inputStream) throws SQLException {
        
    }
    
    @Override
    public void setBinaryStream(final int i, final InputStream inputStream) throws SQLException {
        
    }
    
    @Override
    public void setCharacterStream(final int i, final Reader reader) throws SQLException {
        
    }
    
    @Override
    public void setNCharacterStream(final int i, final Reader reader) throws SQLException {
        
    }
    
    @Override
    public void setClob(final int i, final Reader reader) throws SQLException {
        
    }
    
    @Override
    public void setBlob(final int i, final InputStream inputStream) throws SQLException {
        
    }
    
    @Override
    public void setNClob(final int i, final Reader reader) throws SQLException {
        
    }
    
    @Override
    public ResultSet executeQuery(final String s) throws SQLException {
        return null;
    }
    
    @Override
    public int executeUpdate(final String s) throws SQLException {
        return 0;
    }
    
    @Override
    public void close() throws SQLException {
        
    }
    
    @Override
    public int getMaxFieldSize() throws SQLException {
        return 0;
    }
    
    @Override
    public void setMaxFieldSize(final int i) throws SQLException {
        
    }
    
    @Override
    public int getMaxRows() throws SQLException {
        return 0;
    }
    
    @Override
    public void setMaxRows(final int i) throws SQLException {
        
    }
    
    @Override
    public void setEscapeProcessing(final boolean b) throws SQLException {
        
    }
    
    @Override
    public int getQueryTimeout() throws SQLException {
        return 0;
    }
    
    @Override
    public void setQueryTimeout(final int i) throws SQLException {
        
    }
    
    @Override
    public void cancel() throws SQLException {
        
    }
    
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }
    
    @Override
    public void clearWarnings() throws SQLException {
        
    }
    
    @Override
    public void setCursorName(final String s) throws SQLException {
        
    }
    
    @Override
    public boolean execute(final String s) throws SQLException {
        return false;
    }
    
    @Override
    public ResultSet getResultSet() throws SQLException {
        return null;
    }
    
    @Override
    public int getUpdateCount() throws SQLException {
        return 0;
    }
    
    @Override
    public boolean getMoreResults() throws SQLException {
        return false;
    }
    
    @Override
    public void setFetchDirection(final int i) throws SQLException {
        
    }
    
    @Override
    public int getFetchDirection() throws SQLException {
        return 0;
    }
    
    @Override
    public void setFetchSize(final int i) throws SQLException {
        
    }
    
    @Override
    public int getFetchSize() throws SQLException {
        return 0;
    }
    
    @Override
    public int getResultSetConcurrency() throws SQLException {
        return 0;
    }
    
    @Override
    public int getResultSetType() throws SQLException {
        return 0;
    }
    
    @Override
    public void addBatch(final String s) throws SQLException {
        
    }
    
    @Override
    public void clearBatch() throws SQLException {
        
    }
    
    @Override
    public int[] executeBatch() throws SQLException {
        return new int[0];
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        return null;
    }
    
    @Override
    public boolean getMoreResults(final int i) throws SQLException {
        return false;
    }
    
    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return null;
    }
    
    @Override
    public int executeUpdate(final String s, final int i) throws SQLException {
        return 0;
    }
    
    @Override
    public int executeUpdate(final String s, final int[] ints) throws SQLException {
        return 0;
    }
    
    @Override
    public int executeUpdate(final String s, final String[] strings) throws SQLException {
        return 0;
    }
    
    @Override
    public boolean execute(final String s, final int i) throws SQLException {
        return false;
    }
    
    @Override
    public boolean execute(final String s, final int[] ints) throws SQLException {
        return false;
    }
    
    @Override
    public boolean execute(final String s, final String[] strings) throws SQLException {
        return false;
    }
    
    @Override
    public int getResultSetHoldability() throws SQLException {
        return 0;
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        return false;
    }
    
    @Override
    public void setPoolable(final boolean b) throws SQLException {
        
    }
    
    @Override
    public boolean isPoolable() throws SQLException {
        return false;
    }
    
    @Override
    public void closeOnCompletion() throws SQLException {
        
    }
    
    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return false;
    }
    
    @Override
    public <T> T unwrap(final Class<T> aClass) throws SQLException {
        return null;
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> aClass) throws SQLException {
        return false;
    }
}
