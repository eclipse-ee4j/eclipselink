/* 1998 (c) Oracle Corporation */

package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.Vector;
import org.eclipse.persistence.platform.database.oracle.publisher.PublisherException;

/**
 * Instances of the class </code>Type</code> represent user-declared or predefined types in SQL or
 * PL/SQL that may have fields and methods. Currently, subclasses of </code>Type</code> represent
 * PL/SQL packages, the top-level PL/SQL scope, Oracle 8 user-defined types (objects, varrays and
 * nested tables) and the primitive database types.
 * <p/>
 * This class is analogous to java.lang.Class.
 */
@SuppressWarnings("unchecked")
public abstract class Type {

    protected Name m_name;
    private boolean m_isPrimitive;
    protected int m_typecode;
    private Object m_annotation;
    private String m_hint;
    private Vector m_namedTranslations;

    /** The following methods are adapted from java.lang.Class */

    /**
     * If this Type has a component type, return the Type object that represents the component type;
     * otherwise returns null.
     */
    public Type getComponentType() throws SQLException, PublisherException {
        return null;
    }

    /**
     * Returns an array of Constructor objects reflecting all the constructors declared by this
     * Type. Returns an array of length 0 if this Type object does not declare any constructors.
     */
    public Constructor[] getDeclaredConstructors() throws SecurityException, SQLException {
        return new Constructor[0];
    }

    /**
     * Returns an array of Field objects reflecting all the fields declared by this Type object.
     * Returns an array of length 0 if this Type object declares no fields.
     */
    public Field[] getDeclaredFields(boolean publishedOnly) throws SecurityException, SQLException,
        PublisherException {
        /* derived classes that have fields override this */
        return new Field[0];
    }

    /**
     * Returns an array of Method objects reflecting all the methods declared by this Type object.
     * Returns an array of length 0 if the Type declares no methods
     */
    public Method[] getDeclaredMethods() throws SecurityException, SQLException, PublisherException {
        return new Method[0];
    }

    /**
     * Returns an array of Field objects reflecting all the accessible fields of this Type object.
     * Returns an array of length 0 if this Type object has no accesible fields.
     */
    public Field[] getFields(boolean publishedOnly) throws SecurityException, SQLException,
        PublisherException {
        /* derived classes that have fields override this */
        return new Field[0];
    }

    /**
     * Returns the modifiers for this type, encoded in an integer. The modifiers currently in use
     * are: public (always set) final abstract The modifiers are decoded using the methods of
     * java.lang.reflect.Modifier. If we ever need additional modifiers for C++, we can subclass
     * Modifier.
     */
    public int getModifiers() throws SQLException {
        return Modifier.PUBLIC + Modifier.FINAL;
    }

    /**
     * Returns the fully-qualified name of the type represented by this Type object, as a String.
     */
    public String getName() {
        return m_name.toString();
    }

    /**
     * Returns the Type representing the supertype of the entity represented by this Type. If no
     * supertype, returns null
     */

    public Type getSupertype() throws SQLException, PublisherException {
        return null;
    }

    /**
     * Determines if this Type represents an array type.
     * <p/>
     */
    public boolean isArray() {
        return false;
    }

    /**
     * Determines if this Type represents a primitive type.
     */
    public boolean isPrimitive() {
        return m_isPrimitive;
    }

    /**
     * Converts the Type to a string, as it would appear in an error message.
     */
    public String toString() {
        return getName();
    }

    public int hashCode() {
        return m_name.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        return getName().equals(((Type)obj).getName());
    }

    /**
     * The remaining methods are not adapted from java.lang.Class
     */

    public boolean isPackage() {
        return false;
    }

    public boolean isTable() {
        return false;
    }

    /**
     * Returns the annotation associated with the Type. The annotation was set by the client of the
     * Type using setAnnotation().
     */
    public Object getAnnotation() {
        return m_annotation;
    }

    /**
     * Sets the annotation associated with the Type. The annotation is an arbitary object chosed by
     * the client of the Type.
     */
    public void setAnnotation(Object o) {
        m_annotation = o;
    }

    /**
     * Returns a hint associated with the Type. Usually hints are used for index-by table types.
     */
    public String getHint() {
        return m_hint;
    }

    /**
     * Sets the hint associated with the Type. Usually hints are used for index-by table types.
     */
    public void setHint(String s) {
        m_hint = s;
    }

    public Vector getNamedTranslations() {
        return m_namedTranslations;
    }

    public void setNamedTranslations(Vector v) {
        m_namedTranslations = v;
    }

    /**
     * Return the typecode for this type.
     */
    public int getTypecode() {
        return m_typecode;
    }

    /**
     * set the typecode for this type.
     */
    public void setTypecode(int typecode) {
        m_typecode = typecode;
    }

    /**
     * Return a JDBC typecode for this type.
     */
    public int getJdbcTypecode() {
        // JDBC shares the same typecode for nested table and varrays
        if (getTypecode() == OracleTypes.TABLE) {
            return OracleTypes.ARRAY;
        }
        // PL/SQL TABLE is mapped into SQL VARRAT
        if (getTypecode() == OracleTypes.PLSQL_INDEX_TABLE) {
            return OracleTypes.ARRAY;
        }
        if (getTypecode() == OracleTypes.PLSQL_NESTED_TABLE) {
            return OracleTypes.ARRAY;
        }
        if (getTypecode() == OracleTypes.PLSQL_VARRAY_TABLE) {
            return OracleTypes.ARRAY;
        }
        // PL/SQL RECORD is mapped into SQL OBJECT
        if (getTypecode() == OracleTypes.PLSQL_RECORD) {
            return OracleTypes.STRUCT;
        }
        return getTypecode();
    }

    public boolean hasMethods() throws SQLException, PublisherException {
        /* subclasses with methods override this */
        return false;
    }

    public Name getNameObject() {
        return m_name;
    }

    void setNameObject(Name name) {
        m_name = name;
    }

    protected Type(Name name, int typecode, boolean isPrimitive) {
        m_name = name;
        m_typecode = typecode;
        m_isPrimitive = isPrimitive;
    }

    /**
     * Reports whether this is a PL/SQL type which has user-defined conversion functions to a SQL
     * type.
     */
    public boolean hasConversion() {
        return false;
    }

    /**
     * Returns the PL/SQL function to be used for converting this PL/SQL into a SQL type.
     * <p/>
     * Returns null if this is not a PL/SQL type or if it does not have user-defined conversions.
     */
    public String getOutOfConversion() {
        return null;
    }

    /**
     * Returns the PL/SQL function to be used for converting this PL/SQL into a SQL type, qualified
     * with package name.
     * <p/>
     * Returns null if this is not a PL/SQL type or if it does not have user-defined conversions.
     */
    public String getOutOfConversionQualified() {
        return null;
    }

    /**
     * Returns the PL/SQL function to be used for converting a SQL type into this PL/SQL type,
     * qualified with package name.
     * <p/>
     * Returns null if this is not a PL/SQL type or if it does not have user-defined conversions.
     */
    public String getIntoConversion() {
        return null;
    }

    /**
     * Returns the PL/SQL function to be used for converting a SQL type into this PL/SQL type,
     * qualified with package name.
     * <p/>
     * Returns null if this is not a PL/SQL type or if it does not have user-defined conversions.
     */
    public String getIntoConversionQualified() {
        return null;
    }

}
