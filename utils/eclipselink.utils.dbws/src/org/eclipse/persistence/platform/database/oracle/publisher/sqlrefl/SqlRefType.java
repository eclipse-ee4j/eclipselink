/* 1998 (c) Oracle Corporation */
package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

/**
 * Instances of the class </code>SqlRefType</code> represent SQL REF types.
 */
public class SqlRefType extends SqlType {
    /**
     * Construct a SqlRef Type
     */
    SqlRefType(SqlName sqlName, SqlType refeeType, SqlType parentType, boolean generateMe,
        SqlReflectorImpl reflector) {
        super(null, OracleTypes.REF, false, parentType, reflector);

        m_refeeType = refeeType;
    }

    public int hashCode() {
        return m_refeeType.hashCode() ^ 1;
    }

    /**
     * Returns the fully-qualified name of the type represented by this Type object, as a String.
     */
    public String getName() {
        return "REF " + m_refeeType.getName();
    }

    public String getTargetTypeName() {
        if (m_refeeType instanceof SqlType) {
            return "REF " + ((SqlType)m_refeeType).getTargetTypeName();
        }
        else {
            return getName();
        }
    }

    public String getTypeName() {
        if (m_refeeType instanceof SqlType) {
            return "REF " + ((SqlType)m_refeeType).getTypeName();
        }
        else {
            return getName();
        }
    }

    /**
     * If this Type has a component type, return the Type object that represents the component type;
     * otherwise returns null.
     */
    public Type getComponentType() {
        return m_refeeType;
    }

    SqlType m_refeeType;
}
