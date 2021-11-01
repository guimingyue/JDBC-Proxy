package me.guimy.util;

import me.guimy.common.Param;
import me.guimy.common.Param.ParamType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class ParamUtils {
    
    public static Param getStringParam(final String s) {
        return new Param(s, ParamType.STRING);
    }
    
    public static Param getBytesParam(final byte[] bytes) {
        return new Param(bytes, ParamType.BYTES);
    }
    
    public static Param getTableNameParam(final String tableName) {
        return new Param(tableName, ParamType.TABLE_NAME);
    }
    
    public static Param getObjectParam(final Object o) {
        return new Param(o, ParamType.OBJECT);
    }
    
    public static Param getNullParam() {
        return new Param(ParamType.NULL);
    }
    
    public static void setParam(final PreparedStatement pstmt, final int idx, final Param param) throws SQLException {
        switch (param.getParamType()) {
            case NULL:
                pstmt.setNull(idx, Types.OTHER);
                break;
            case BOOLEAN:
                pstmt.setBoolean(idx, param.getValue());
                break;
            case BYTE:
                pstmt.setByte(idx, param.getValue());
                break;
            case SHORT:
                pstmt.setShort(idx, param.getValue());
                break;
            case INT:
                pstmt.setInt(idx, param.getValue());
                break;
            case LONG:
                pstmt.setLong(idx, param.getValue());
                break;
            case FLOAT:
                pstmt.setFloat(idx, param.getValue());
                break;
            case DOUBLE:
                pstmt.setDouble(idx, param.getValue());
                break;
            case BYTES:
                pstmt.setBytes(idx, param.getValue());
                break;
            case STRING:
                pstmt.setString(idx, param.getValue());
                break;
            case OBJECT:
            default:
                pstmt.setObject(idx, param.getValue());
        }
    }
    
    public static Param getBooleanParam(final boolean b) {
        return new Param(b, ParamType.BOOLEAN);
    }
    
    public static Param getByteParam(final byte b) {
        return new Param(b, ParamType.BYTE);
    }
    
    public static Param getShortParam(final short s) {
        return new Param(s, ParamType.SHORT);
    }
    
    public static Param getIntParam(final int i) {
        return new Param(i, ParamType.INT);
    }
    
    public static Param getLongParam(final long l) {
        return new Param(l, ParamType.LONG);
    }
    
    public static Param getFloatParam(final float v) {
        return new Param(v, ParamType.FLOAT);
    }
    
    public static Param getDoubleParam(final double v) {
        return new Param(v, ParamType.DOUBLE);
    }
}
