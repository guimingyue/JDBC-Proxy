package me.guimy.util;

import me.guimy.common.Param;
import me.guimy.common.Param.ParamType;

import java.sql.PreparedStatement;
import java.sql.SQLException;

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
    
    public static void setParam(final PreparedStatement pstmt, final int idx, final Param param) throws SQLException {
        switch (param.getParamType()) {
            case BYTES:
                pstmt.setBytes(idx, param.getValue());
            case STRING:
                pstmt.setString(idx, param.getValue());
            case OBJECT:
            default:
                pstmt.setObject(idx, param.getValue());
        }
    }
}
