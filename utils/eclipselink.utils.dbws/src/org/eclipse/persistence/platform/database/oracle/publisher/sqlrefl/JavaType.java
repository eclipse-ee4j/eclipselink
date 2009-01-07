package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

public abstract class JavaType extends Type {
    /* Create a predefined Java type */
    public JavaType(String typeName, Type sqlType) {
        this(new JavaName(null, typeName), sqlType);
    };

    /* Create an added Java type */
    public JavaType(JavaName javaName, Type sqlType) {
        super(javaName, OracleTypes.JAVA_TYPE, false);
        m_sqlType = sqlType;
    };

    public JavaName getJavaName() {
        return (JavaName)m_name;
    }

    public String getTypeName() {
        return m_name.getUseClass();
    }

    public String getTypeName(Map map) {
        return getTypeName();
    }

    public int getTypecode() {
        if (m_sqlType != null) {
            return m_sqlType.m_typecode;
        }
        return m_typecode;
    }

    protected Type m_sqlType; // null for CURSOR element type
}
