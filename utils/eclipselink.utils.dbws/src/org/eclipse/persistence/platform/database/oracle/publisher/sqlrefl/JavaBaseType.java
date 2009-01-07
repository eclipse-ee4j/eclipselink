package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

public class JavaBaseType extends JavaType {
    /* Create a predefined Java type */
    public JavaBaseType(String typeName, Field[] fields, Method[] methods, Type sqlType) {
        super(typeName, sqlType);
        m_fields = fields;
        m_methods = methods;
        // System.out.println("[JavaBaseType] " + m_fields.length); //D+
    };

    /* Create an added Java type */
    public JavaBaseType(JavaName javaName, Field[] fields, Method[] methods, Type sqlType) {
        super(javaName, sqlType);
        m_fields = fields;
        m_methods = methods;
        // System.out.println("[JavaBaseType] " + m_fields.length); //D+
    };

    public Field[] getFields(boolean publishedOnly) {
        return m_fields;
    }

    public Field[] getDeclaredFields(boolean publishedOnly) {
        return m_fields;
    }

    public Method[] getDeclaredMethods() {
        return m_methods;
    }

    public boolean hasFields() {
        return m_fields != null && m_fields.length > 0;
    }

    public boolean hasMethods() {
        return m_methods != null && m_methods.length > 0;
    }

    protected Field[] m_fields;
    protected Method[] m_methods;
}
