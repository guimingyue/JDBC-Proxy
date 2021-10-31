package me.guimy.common;

public class Param {
    
    private Object value;
    
    private ParamType paramType;
    
    public Param(Object value, ParamType paramType) {
        this.value = value;
        this.paramType = paramType;
    }
    
    public <T> T getValue() {
        return (T) value;
    }
    
    public ParamType getParamType() {
        return paramType;
    }
    
    public enum ParamType {
        STRING, 
        BYTES,
        TABLE_NAME,
        OBJECT,
        ;
    }
}
