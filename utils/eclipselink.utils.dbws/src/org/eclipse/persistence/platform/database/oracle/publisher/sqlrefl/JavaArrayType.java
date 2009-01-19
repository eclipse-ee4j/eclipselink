package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

public class JavaArrayType extends JavaType {
    public JavaArrayType(Type eleType, SqlReflector reflector, Type sqlType) {
        super((((JavaType)eleType).getTypeName() + "[]"), sqlType);
        m_eleType = eleType;
        m_reflector = reflector;
    }

    public String getTypeName(Map map) {
        if (m_eleType instanceof JavaType) {
            return getTypeName();
        }
        return map.writeTypeName(m_eleType) + "[]";
    }

    public Type getEleType() {
        return m_eleType;
    }

    private Type m_eleType;
    protected SqlReflector m_reflector;
}
