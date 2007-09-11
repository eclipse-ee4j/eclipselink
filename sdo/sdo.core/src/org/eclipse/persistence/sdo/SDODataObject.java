/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.sdo;

import commonj.sdo.ChangeSummary;
import commonj.sdo.ChangeSummary.Setting;
import commonj.sdo.DataGraph;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;
import commonj.sdo.helper.DataHelper;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.impl.ExternalizableDelegator;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.eclipse.persistence.sdo.helper.ListWrapper;
import org.eclipse.persistence.sdo.helper.SDODataHelper;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.sdo.helper.XPathEngine;
import org.eclipse.persistence.exceptions.SDOException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLRoot;

/**
 * <p><b>Purpose</b>: A data object is a representation of some structured data.
 * It is the fundamental component in the SDO (Service Data Objects) package.
 * Data objects support reflection, path-based access, convenience creation and deletion methods,
 * and the ability to be part of a {@link DataGraph data graph}.
 * <p>
 * Each data object holds its data as a series of {@link Property Properties}.
 * Properties can be accessed by name, property index, or using the property meta object itself.
 * A data object can also contain references to other data objects, through reference-type Properties.
 * <p>
 * A data object has a series of convenience accessors for its Properties.
 * These methods either use a path (String),
 * a property index,
 * or the {@link Property property's meta object} itself, to identify the property.
 * Some examples of the path-based accessors are as follows:
 *<pre>
 * DataObject company = ...;
 * company.get("name");                   is the same as company.get(company.getType().getProperty("name"))
 * company.set("name", "acme");
 * company.get("department.0/name")       is the same as ((DataObject)((List)company.get("department")).get(0)).get("name")
 *                                        .n  indexes from 0 ... implies the name property of the first department
 * company.get("department[1]/name")      [] indexes from 1 ... implies the name property of the first department
 * company.get("department[number=123]")  returns the first department where number=123
 * company.get("..")                      returns the containing data object
 * company.get("/")                       returns the root containing data object
 *</pre>
 * <p> There are general accessors for Properties, i.e., {@link #get(Property) get} and {@link #set(Property, Object) set},
 * as well as specific accessors for the primitive types and commonly used data types like
 * String, Date, List, BigInteger, and BigDecimal.
 */
public class SDODataObject implements DataObject {

    /**
     * Development Guidelines:
     * (1) All no-argument get and single argument set functions that are outside the DataObject interface
     * must be proceeded with a _ underscore in the form _getValue()/_setValue(value).
     * The reason for this is to avoid naming collisions with generated classes that extend SDODataObject
     * where the generated get/set methods for POJO fields would collide with the same function here.
     */
    /** The Type that this DataObject represents */
    private Type type;

    // t20070714_bc4j: Add pluggable DO support for BC4J using currentValueStore
    private DataObject container;

    /**
     * The (currentValueStore) will maintain the current state of our model
     *   after logged changes - it is a shallow copy of the original, progressively becoming deeper with changes.
     */
    private ValueStore currentValueStore;
    private List openContentProperties;
    private List openContentPropertiesAttributes;
    private Map openContentPropertiesMap;
    private String containmentPropertyName;
    private ChangeSummary changeSummary;
    private List instanceProperties;
    private String sdoRef;
    private Sequence sequence;

    /** hold the current context containing all helpers so that we can preserve inter-helper relationships */
    private HelperContext aHelperContext;

    /**Unique hash ID of this Externalizable class - not required at this point because we serialize the xml representation */

    //static final long serialVersionUID = 5930094058760264198L;

    /** The ValueStore implementation class used in construction */
    private static Class pluggableClass;

    static {
        try {
            String pluggableClassName = System.getProperty(SDOConstants.SDO_PLUGGABLE_MAP_IMPL_CLASS_KEY,//
                                                           SDOConstants.SDO_PLUGGABLE_MAP_IMPL_CLASS_VALUE);
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                try {
                    pluggableClass = (Class)AccessController.doPrivileged(new PrivilegedClassForName(pluggableClassName));
                } catch (PrivilegedActionException ex) {
                    if (ex.getCause() instanceof ClassNotFoundException) {
                        throw (ClassNotFoundException)ex.getCause();
                    }
                    throw (RuntimeException)ex.getCause();
                }
            } else {
                pluggableClass = PrivilegedAccessHelper.getClassForName(pluggableClassName);
            }
        } catch (ClassNotFoundException cnfe) {
            // TODO: throw or propagate these properly
            throw new IllegalArgumentException(cnfe.getMessage());
        }
    }

    /**
     * INTERNAL:
     * Private constructor.
     *Use {@link #SDODataObject(HelperContext)} instead
     */
    public SDODataObject() {
        // Implementors should not be calling this constructor directly - please use SDODataFactory
        // however if it is called we will initialize the default implementation of the currentValueStore Map
        // initialize Map Implementation        
        // set currentValueStore Map implementation (replace any that was set in the constructor in newInstance() above)
        try {
            _setCurrentValueStore((ValueStore)pluggableClass.newInstance());
        } catch (IllegalAccessException iae) {
            throw new IllegalArgumentException(iae.getMessage());
        } catch (InstantiationException ie) {
            throw new IllegalArgumentException(ie.getMessage());
        }
    }

    /**
     * INTERNAL:
     * Set the HelperContext that will be associated with this DataObject.
     * @deprecated since OracleAS TopLink 11<i>1</i> (11.1.1.0) 17-May-2007.
     * Use {@link #_setHelperContext(HelperContext)} instead
     * @param aContext
     */
    public void setHelperContext(HelperContext aContext) {
        _setHelperContext(aContext);
    }

    /**
     * INTERNAL:
     * Set the HelperContext that will be associated with this DataObject.
     * @param aContext
     */
    public void _setHelperContext(HelperContext aContext) {
        aHelperContext = aContext;
        // reset the HelperContext on ChangeSummary
        if (getChangeSummary() != null) {
            ((SDOChangeSummary)getChangeSummary()).setHelperContext(aHelperContext);
        }
    }

    /**
     * INTERNAL:
     * Return the HelperContext associated with this DataObject.
     * @deprecated since OracleAS TopLink 11<i>1</i> (11.1.1.0) 17-May-2007.
     * Use {@link #_getHelperContext()} instead
     * @return
     */
    public HelperContext getHelperContext() {
        return _getHelperContext();
    }

    /**
     * INTERNAL:
     * Return the HelperContext associated with this DataObject.
     * @return
     */
    public HelperContext _getHelperContext() {
        return aHelperContext;
    }

    /**
     * Returns the value of a property of either this object or an object reachable from it, as identified by the
     * specified path.
     * @param path the path to a valid object and property.
     * @return the value of the specified property.
     * @see #get(Property)
     */
    public Object get(String path) {// path like "a/b/c"
        return XPathEngine.getInstance().get(path, this);
    }

    /**
     * Sets a property of either this object or an object reachable from it, as identified by the specified path,
     * to the specified value.
     * @param path the path to a valid object and property.
     * @param value the new value for the property.
     * @see #set(Property, Object)
     */
    public void set(String path, Object value) throws ClassCastException, UnsupportedOperationException, IllegalArgumentException {
        XPathEngine.getInstance().set(path, value, this, false);
    }

    /**
     * Returns whether a property of either this object or an object reachable from it, as identified by the specified path,
     * is considered to be set.
     * @param path the path to a valid object and property.
     * @see #isSet(Property)
     */
    public boolean isSet(String path) {
        return XPathEngine.getInstance().isSet(path, this);
    }

    /**
     * Unsets a property of either this object or an object reachable from it, as identified by the specified path.
     * @param path the path to a valid object and property.
     * @see #unset(Property)
     */
    public void unset(String path) {
        XPathEngine.getInstance().unset(path, this);
    }

    /**
     * Returns the value of a <code>boolean</code> property identified by the specified path.
     * @param path the path to a valid object and property.
     * @return the <code>boolean</code> value of the specified property.
     * @see #get(String)
     */
    public boolean getBoolean(String path) throws ClassCastException {
        Boolean value = (Boolean)XPathEngine.getInstance().convertObjectToValueByPath(path, Boolean.class, this);

        //TODO: this is temporary
        if (value == null) {
            return false;
        }
        return value.booleanValue();
    }

    /**
     * Returns the value of a <code>byte</code> property identified by the specified path.
     * @param path the path to a valid object and property.
     * @return the <code>byte</code> value of the specified property.
     * @see #get(String)
     */
    public byte getByte(String path) {
        Byte value = (Byte)XPathEngine.getInstance().convertObjectToValueByPath(path, Byte.class, this);
        if (value == null) {
            return 0;
        }
        return value.byteValue();
    }

    /**
     * Returns the value of a <code>char</code> property identified by the specified path.
     * @param path the path to a valid object and property.
     * @return the <code>char</code> value of the specified property.
     * @see #get(String)
     */
    public char getChar(String path) {
        Character value = (Character)XPathEngine.getInstance().convertObjectToValueByPath(path, Character.class, this);
        if (value == null) {
            return '\u0000';
        }
        return value.charValue();
    }

    /**
     * Returns the value of a <code>double</code> property identified by the specified path.
     * @param path the path to a valid object and property.
     * @return the <code>double</code> value of the specified property.
     * @see #get(String)
     */
    public double getDouble(String path) {
        Double value = (Double)XPathEngine.getInstance().convertObjectToValueByPath(path, Double.class, this);
        if (value == null) {
            return 0.0d;
        }
        return value.doubleValue();
    }

    /**
     * Returns the value of a <code>float</code> property identified by the specified path.
     * @param path the path to a valid object and property.
     * @return the <code>float</code> value of the specified property.
     * @see #get(String)
     */
    public float getFloat(String path) {
        Float value = (Float)XPathEngine.getInstance().convertObjectToValueByPath(path, Float.class, this);
        if (value == null) {
            return 0.0f;
        }
        return value.floatValue();
    }

    /**
     * Returns the value of a <code>int</code> property identified by the specified path.
     * @param path the path to a valid object and property.
     * @return the <code>int</code> value of the specified property.
     * @see #get(String)
     */
    public int getInt(String path) {
        Integer value = (Integer)XPathEngine.getInstance().convertObjectToValueByPath(path, Integer.class, this);
        if (value == null) {
            return 0;
        }
        return value.intValue();
    }

    /**
     * Returns the value of a <code>long</code> property identified by the specified path.
     * @param path the path to a valid object and property.
     * @return the <code>long</code> value of the specified property.
     * @see #get(String)
     */
    public long getLong(String path) {
        Long value = (Long)XPathEngine.getInstance().convertObjectToValueByPath(path, Long.class, this);
        if (value == null) {
            return 0L;
        }
        return value.longValue();
    }

    /**
     * Returns the value of a <code>short</code> property identified by the specified path.
     * @param path the path to a valid object and property.
     * @return the <code>short</code> value of the specified property.
     * @see #get(String)
     */
    public short getShort(String path) {
        Short value = (Short)XPathEngine.getInstance().convertObjectToValueByPath(path, Short.class, this);
        if (value == null) {
            return 0;
        }
        return value.shortValue();
    }

    /**
     * Returns the value of a <code>byte[]</code> property identified by the specified path.
     * @param path the path to a valid object and property.
     * @return the <code>byte[]</code> value of the specified property.
     * @see #get(String)
     */
    public byte[] getBytes(String path) {
        byte[] value = (byte[])XPathEngine.getInstance().convertObjectToValueByPath(path, byte[].class, this);
        return value;
    }

    /**
     * Returns the value of a <code>BigDecimal</code> property identified by the specified path.
     * @param path the path to a valid object and property.
     * @return the <code>BigDecimal</code> value of the specified property.
     * @see #get(String)
     */
    public BigDecimal getBigDecimal(String path) {
        BigDecimal value = (BigDecimal)XPathEngine.getInstance().convertObjectToValueByPath(path, BigDecimal.class, this);
        return value;
    }

    /**
     * Returns the value of a <code>BigInteger</code> property identified by the specified path.
     * @param path the path to a valid object and property.
     * @return the <code>BigInteger</code> value of the specified property.
     * @see #get(String)
     */
    public BigInteger getBigInteger(String path) {
        BigInteger value = (BigInteger)XPathEngine.getInstance().convertObjectToValueByPath(path, BigInteger.class, this);
        return value;
    }

    /**
     * Returns the value of a <code>DataObject</code> property identified by the specified path.
     * @param path the path to a valid object and property.
     * @return the <code>DataObject</code> value of the specified property.
     * @see #get(String)
     */
    public DataObject getDataObject(String path) throws ClassCastException {
        return (DataObject)get(path);
    }

    /**
     * Returns the value of a <code>Date</code> property identified by the specified path.
     * @param path the path to a valid object and property.
     * @return the <code>Date</code> value of the specified property.
     * @see #get(String)
     */
    public Date getDate(String path) {
        Date value = (Date)XPathEngine.getInstance().convertObjectToValueByPath(path, Date.class, this);
        return value;
    }

    /**
     * Returns the value of a <code>String</code> property identified by the specified path.
     * @param path the path to a valid object and property.
     * @return the <code>String</code> value of the specified property.
     * @see #get(String)
     */
    public String getString(String path) {
        String value = (String)XPathEngine.getInstance().convertObjectToValueByPath(path, String.class, this);
        return value;
    }

    /**
     * Returns the value of a <code>List</code> property identified by the specified path.
     * @param path the path to a valid object and property.
     * @return the <code>List</code> value of the specified property.
     * @see #get(String)
     */
    public List getList(String path) {
        return (List)XPathEngine.getInstance().convertObjectToValueByPath(path, List.class, this);
    }

    /**
     * Sets the value of a <code>boolean</code> property identified by the specified path, to the specified value.
     * @param path the path to a valid object and property.
     * @param value the new value for the property.
     * @see #set(String, Object)
     */
    public void setBoolean(String path, boolean value) {
        convertValueAndSet(path, new Boolean(value));
    }

    /**
     * Sets the value of a <code>byte</code> property identified by the specified path, to the specified value.
     * @param path the path to a valid object and property.
     * @param value the new value for the property.
     * @see #set(String, Object)
     */
    public void setByte(String path, byte value) {
        convertValueAndSet(path, new Byte(value));
    }

    /**
     * Sets the value of a <code>char</code> property identified by the specified path, to the specified value.
     * @param path the path to a valid object and property.
     * @param value the new value for the property.
     * @see #set(String, Object)
     */
    public void setChar(String path, char value) {
        convertValueAndSet(path, new Character(value));
    }

    /**
     * Sets the value of a <code>double</code> property identified by the specified path, to the specified value.
     * @param path the path to a valid object and property.
     * @param value the new value for the property.
     * @see #set(String, Object)
     */
    public void setDouble(String path, double value) {
        convertValueAndSet(path, new Double(value));
    }

    /**
     * Sets the value of a <code>float</code> property identified by the specified path, to the specified value.
     * @param path the path to a valid object and property.
     * @param value the new value for the property.
     * @see #set(String, Object)
     */
    public void setFloat(String path, float value) {
        convertValueAndSet(path, new Float(value));
    }

    /**
     * Sets the value of a <code>int</code> property identified by the specified path, to the specified value.
     * @param path the path to a valid object and property.
     * @param value the new value for the property.
     * @see #set(String, Object)
     */
    public void setInt(String path, int value) {
        convertValueAndSet(path, new Integer(value));
    }

    /**
     * Sets the value of a <code>long</code> property identified by the specified path, to the specified value.
     * @param path the path to a valid object and property.
     * @param value the new value for the property.
     * @see #set(String, Object)
     */
    public void setLong(String path, long value) {
        convertValueAndSet(path, new Long(value));
    }

    /**
     * Sets the value of a <code>short</code> property identified by the specified path, to the specified value.
     * @param path the path to a valid object and property.
     * @param value the new value for the property.
     * @see #set(String, Object)
     */
    public void setShort(String path, short value) {
        convertValueAndSet(path, new Short(value));
    }

    /**
     * Sets the value of a <code>byte[]</code> property identified by the specified path, to the specified value.
     * @param path the path to a valid object and property.
     * @param value the new value for the property.
     * @see #set(String, Object)
     */
    public void setBytes(String path, byte[] value) {
        convertValueAndSet(path, value);
    }

    /**
     * Sets the value of a <code>BigDecimal</code> property identified by the specified path, to the specified value.
     * @param path the path to a valid object and property.
     * @param value the new value for the property.
     * @see #set(String, Object)
     */
    public void setBigDecimal(String path, BigDecimal value) {
        convertValueAndSet(path, value);
    }

    /**
     * Sets the value of a <code>BigInteger</code> property identified by the specified path, to the specified value.
     * @param path the path to a valid object and property.
     * @param value the new value for the property.
     * @see #set(String, Object)
     */
    public void setBigInteger(String path, BigInteger value) {
        convertValueAndSet(path, value);
    }

    /**
     * Sets the value of a <code>DataObject</code> property identified by the specified path, to the specified value.
     * @param path the path to a valid object and property.
     * @param value the new value for the property.
     * @see #set(String, Object)
     */
    public void setDataObject(String path, DataObject value) {
        set(path, value);
    }

    /**
     * Sets the value of a <code>Date</code> property identified by the specified path, to the specified value.
     * @param path the path to a valid object and property.
     * @param value the new value for the property.
     * @see #set(String, Object)
     */
    public void setDate(String path, Date value) {
        convertValueAndSet(path, value);
    }

    /**
     * Sets the value of a <code>String</code> property identified by the specified path, to the specified value.
     * @param path the path to a valid object and property.
     * @param value the new value for the property.
     * @see #set(String, Object)
     */
    public void setString(String path, String value) {
        convertValueAndSet(path, value);
    }

    /**
     * Sets the value of a <code>List</code> property identified by the specified path, to the specified value.
     * @param path the path to a valid object and property.
     * @param value the new value for the property.
     * @see #set(String, Object)
     * @see #setList(Property, List)
     */
    public void setList(String path, List value) {
        convertValueAndSet(path, value);
    }

    /**
     * Returns the value of the property at the specified index in {@link Type#getProperties property list}
     * of this object's {@link Type type}.
     * @param propertyIndex the index of the property.
     * @return the value of the specified property.
     * @see #get(Property)
     */
    public Object get(int propertyIndex) throws IllegalArgumentException {
        Property p = getInstanceProperty(propertyIndex);
        return get(p);
    }

    /**
     * Sets the property at the specified index in {@link Type#getProperties property list} of this object's
     * {@link Type type}, to the specified value.
     * @param propertyIndex the index of the property.
     * @param value the new value for the property.
     * @see #set(Property, Object)
     */
    public void set(int propertyIndex, Object value) {
        try {
            Property p = getInstanceProperty(propertyIndex);
            if (p.isReadOnly()) {
                throw new UnsupportedOperationException("Property is readonly.");
            }
            set(p, value);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("PropertyIndex invalid.");
        }
    }

    /**
     * Returns whether the the property at the specified index in {@link Type#getProperties property list} of this object's
     * {@link Type type}, is considered to be set.
     * @param propertyIndex the index of the property.
     * @return whether the specified property is set.
     * @see #isSet(Property)
     */
    public boolean isSet(int propertyIndex) {
        Property p = getInstanceProperty(propertyIndex);
        return isSet(p);
    }

    /**
     * Unsets the property at the specified index in {@link Type#getProperties property list} of this object's {@link Type type}.
     * @param propertyIndex the index of the property.
     * @see #unset(Property)
     */
    public void unset(int propertyIndex) {
        Property p = getInstanceProperty(propertyIndex);
        unset(p);
    }

    /**
     * Returns the value of a <code>boolean</code> property identified by the specified property index.
     * @param propertyIndex the index of the property.
     * @return the <code>boolean</code> value of the specified property.
     * @see #get(int)
     */
    public boolean getBoolean(int propertyIndex) throws IllegalArgumentException, ClassCastException {
        Property property = getInstanceProperty(propertyIndex);
        return getBoolean(property);
    }

    /**
     * Returns the value of a <code>byte</code> property identified by the specified property index.
     * @param propertyIndex the index of the property.
     * @return the <code>byte</code> value of the specified property.
     * @see #get(int)
     */
    public byte getByte(int propertyIndex) throws IllegalArgumentException, ClassCastException {
        Property property = getInstanceProperty(propertyIndex);
        return getByte(property);
    }

    /**
     * Returns the value of a <code>char</code> property identified by the specified property index.
     * @param propertyIndex the index of the property.
     * @return the <code>char</code> value of the specified property.
     * @see #get(int)
     */
    public char getChar(int propertyIndex) throws IllegalArgumentException, ClassCastException {
        Property property = getInstanceProperty(propertyIndex);
        return getChar(property);
    }

    /**
     * Returns the value of a <code>double</code> property identified by the specified property index.
     * @param propertyIndex the index of the property.
     * @return the <code>double</code> value of the specified property.
     * @see #get(int)
     */
    public double getDouble(int propertyIndex) throws IllegalArgumentException, ClassCastException {
        Property property = getInstanceProperty(propertyIndex);
        return getDouble(property);
    }

    /**
     * Returns the value of a <code>float</code> property identified by the specified property index.
     * @param propertyIndex the index of the property.
     * @return the <code>float</code> value of the specified property.
     * @see #get(int)
     */
    public float getFloat(int propertyIndex) throws IllegalArgumentException, ClassCastException {
        Property property = getInstanceProperty(propertyIndex);
        return getFloat(property);
    }

    /**
     * Returns the value of a <code>int</code> property identified by the specified property index.
     * @param propertyIndex the index of the property.
     * @return the <code>int</code> value of the specified property.
     * @see #get(int)
     */
    public int getInt(int propertyIndex) throws IllegalArgumentException, ClassCastException {
        Property property = getInstanceProperty(propertyIndex);
        return getInt(property);
    }

    /**
     * Returns the value of a <code>long</code> property identified by the specified property index.
     * @param propertyIndex the index of the property.
     * @return the <code>long</code> value of the specified property.
     * @see #get(int)
     */
    public long getLong(int propertyIndex) throws IllegalArgumentException, ClassCastException {
        Property property = getInstanceProperty(propertyIndex);
        return getLong(property);
    }

    /**
     * Returns the value of a <code>short</code> property identified by the specified property index.
     * @param propertyIndex the index of the property.
     * @return the <code>short</code> value of the specified property.
     * @see #get(int)
     */
    public short getShort(int propertyIndex) throws IllegalArgumentException, ClassCastException {
        Property property = getInstanceProperty(propertyIndex);
        return getShort(property);
    }

    /**
     * Returns the value of a <code>byte[]</code> property identified by the specified property index.
     * @param propertyIndex the index of the property.
     * @return the <code>byte[]</code> value of the specified property.
     * @see #get(int)
     */
    public byte[] getBytes(int propertyIndex) throws IllegalArgumentException, ClassCastException {
        Property property = getInstanceProperty(propertyIndex);
        return getBytes(property);
    }

    /**
     * Returns the value of a <code>BigDecimal</code> property identified by the specified property index.
     * @param propertyIndex the index of the property.
     * @return the <code>BigDecimal</code> value of the specified property.
     * @see #get(int)
     */
    public BigDecimal getBigDecimal(int propertyIndex) throws IllegalArgumentException, ClassCastException {
        Property property = getInstanceProperty(propertyIndex);
        return getBigDecimal(property);
    }

    /**
     * Returns the value of a <code>BigInteger</code> property identified by the specified property index.
     * @param propertyIndex the index of the property.
     * @return the <code>BigInteger</code> value of the specified property.
     * @see #get(int)
     */
    public BigInteger getBigInteger(int propertyIndex) throws IllegalArgumentException, ClassCastException {
        Property property = getInstanceProperty(propertyIndex);
        return getBigInteger(property);
    }

    /**
     * Returns the value of a <code>DataObject</code> property identified by the specified property index.
     * @param propertyIndex the index of the property.
     * @return the <code>DataObject</code> value of the specified property.
     * @see #get(int)
     */
    public DataObject getDataObject(int propertyIndex) {
        Property property = getInstanceProperty(propertyIndex);
        return getDataObject(property);
    }

    /**
     * Returns the value of a <code>Date</code> property identified by the specified property index.
     * @param propertyIndex the index of the property.
     * @return the <code>Date</code> value of the specified property.
     * @see #get(int)
     */
    public Date getDate(int propertyIndex) throws IllegalArgumentException, ClassCastException {
        Property property = getInstanceProperty(propertyIndex);
        return getDate(property);
    }

    /**
     * Returns the value of a <code>String</code> property identified by the specified property index.
     * @param propertyIndex the index of the property.
     * @return the <code>String</code> value of the specified property.
     * @see #get(int)
     */
    public String getString(int propertyIndex) throws IllegalArgumentException, ClassCastException {
        Property property = getInstanceProperty(propertyIndex);
        return getString(property);
    }

    /**
     * Returns the value of a <code>List</code> property identified by the specified property index.
     * @param propertyIndex the index of the property.
     * @return the <code>List</code> value of the specified property.
     * @see #get(int)
     */
    public List getList(int propertyIndex) {
        Property property = getInstanceProperty(propertyIndex);
        return getList(property);
    }

    /**
     * Returns the value of a <code>Sequence</code> property identified by the specified path.
     * @param path the path to a valid object and property.
     * @return the <code>Sequence</code> value of the specified property.
     * @see #get(String)
     * @deprecated in 2.1.0.
     */
    public Sequence getSequence(String path) {
        // get property from path
        Object anObject = get(path);

        // TODO: handle a sequence with a null Value?
        // TODO: performance hit requires a refactor of instanceof in the absence of a property
        if ((null == anObject) || !(anObject instanceof DataObject)) {
            // throw exception because there is no way to get the property from a null object in this context
            throw SDOException.sequenceNotFoundForPath(path);
        } else {
            // we cannot use containmentProperty in the many case as the index is missing - use the DO
            return ((DataObject)anObject).getSequence();
        }
    }

    /**
     * Returns the value of a <code>Sequence</code> property identified by the specified property index.
     * @param propertyIndex the index of the property.
     * @return the <code>Sequence</code> value of the specified property.
     * @see #get(int)
     * @deprecated in 2.1.0.
     */
    public Sequence getSequence(int propertyIndex) {
        // get property
        Property aProperty = getInstanceProperty(propertyIndex);
        return getSequencePrivate(aProperty);
    }

    /**
     * Returns the value of the specified <code>Sequence</code> property.
     * @param property the property to get.
     * @return the <code>Sequence</code> value of the specified property.
     * @see #get(Property)
     * @deprecated in 2.1.0.
     */
    public Sequence getSequence(Property property) {
        return getSequencePrivate(property);
    }

    /**
     * INTERNAL:
     * Return a Sequence object when the conditions of many=false and dataType=false are met.
     * Throw an UnsupportedOperationException in all other cases.
     */
    private Sequence getSequencePrivate(Property aProperty) {
        // we process only complex types that are non-many
        if ((aProperty != null) && aProperty.getType().isSequenced() && isSet(aProperty) &&//
                !aProperty.getType().isDataType() && !aProperty.isMany()) {
            return ((DataObject)get(aProperty)).getSequence();
        } else {
            throw SDOException.sequenceNotSupportedForProperty(aProperty.getName());
        }
    }

    /**
     * Returns the <code>Sequence</code> for this DataObject.
     * When getType().isSequencedType() == true,
     * the Sequence of a DataObject corresponds to the
     * XML elements representing the values of its Properties.
     * Updates through DataObject and the Lists or Sequences returned
     * from DataObject operate on the same data.
     * When getType().isSequencedType() == false, null is returned.
     * @return the <code>Sequence</code> or null.
     */
    public Sequence getSequence() {
        // sequence object should be null if !sequenced
        return sequence;
    }

    /**
     * Sets the value of a <code>boolean</code> property identified by the specified property index, to the specified value.
     * @param propertyIndex the index of the property.
     * @param value the new value for the property.
     * @see #set(int, Object)
     */
    public void setBoolean(int propertyIndex, boolean value) {
        convertValueAndSet(propertyIndex, new Boolean(value));
    }

    /**
     * Sets the value of a <code>byte</code> property identified by the specified property index, to the specified value.
     * @param propertyIndex the index of the property.
     * @param value the new value for the property.
     * @see #set(int, Object)
     */
    public void setByte(int propertyIndex, byte value) {
        convertValueAndSet(propertyIndex, new Byte(value));
    }

    /**
     * Sets the value of a <code>char</code> property identified by the specified property index, to the specified value.
     * @param propertyIndex the index of the property.
     * @param value the new value for the property.
     * @see #set(int, Object)
     */
    public void setChar(int propertyIndex, char value) {
        convertValueAndSet(propertyIndex, new Character(value));
    }

    /**
     * Sets the value of a <code>double</code> property identified by the specified property index, to the specified value.
     * @param propertyIndex the index of the property.
     * @param value the new value for the property.
     * @see #set(int, Object)
     */
    public void setDouble(int propertyIndex, double value) {
        convertValueAndSet(propertyIndex, new Double(value));
    }

    /**
     * Sets the value of a <code>float</code> property identified by the specified property index, to the specified value.
     * @param propertyIndex the index of the property.
     * @param value the new value for the property.
     * @see #set(int, Object)
     */
    public void setFloat(int propertyIndex, float value) {
        convertValueAndSet(propertyIndex, new Float(value));
    }

    /**
     * Sets the value of a <code>int</code> property identified by the specified property index, to the specified value.
     * @param propertyIndex the index of the property.
     * @param value the new value for the property.
     * @see #set(int, Object)
     */
    public void setInt(int propertyIndex, int value) {
        convertValueAndSet(propertyIndex, new Integer(value));
    }

    /**
     * Sets the value of a <code>long</code> property identified by the specified property index, to the specified value.
     * @param propertyIndex the index of the property.
     * @param value the new value for the property.
     * @see #set(int, Object)
     */
    public void setLong(int propertyIndex, long value) {
        convertValueAndSet(propertyIndex, new Long(value));
    }

    /**
     * Sets the value of a <code>short</code> property identified by the specified property index, to the specified value.
     * @param propertyIndex the index of the property.
     * @param value the new value for the property.
     * @see #set(int, Object)
     */
    public void setShort(int propertyIndex, short value) {
        convertValueAndSet(propertyIndex, new Short(value));
    }

    /**
     * Sets the value of a <code>byte[]</code> property identified by the specified property index, to the specified value.
     * @param propertyIndex the index of the property.
     * @param value the new value for the property.
     * @see #set(int, Object)
     */
    public void setBytes(int propertyIndex, byte[] value) {
        convertValueAndSet(propertyIndex, value);
    }

    /**
     * Sets the value of a <code>BigDecimal</code> property identified by the specified property index, to the specified value.
     * @param propertyIndex the index of the property.
     * @param value the new value for the property.
     * @see #set(int, Object)
     */
    public void setBigDecimal(int propertyIndex, BigDecimal value) {
        convertValueAndSet(propertyIndex, value);
    }

    /**
     * Sets the value of a <code>BigInteger</code> property identified by the specified property index, to the specified value.
     * @param propertyIndex the index of the property.
     * @param value the new value for the property.
     * @see #set(int, Object)
     */
    public void setBigInteger(int propertyIndex, BigInteger value) {
        convertValueAndSet(propertyIndex, value);
    }

    /**
     * Sets the value of a <code>DataObject</code> property identified by the specified property index, to the specified value.
     * @param propertyIndex the index of the property.
     * @param value the new value for the property.
     * @see #set(int, Object)
     */
    public void setDataObject(int propertyIndex, DataObject value) {
        set(propertyIndex, value);
    }

    /**
     * Sets the value of a <code>Date</code> property identified by the specified property index, to the specified value.
     * @param propertyIndex the index of the property.
     * @param value the new value for the property.
     * @see #set(int, Object)
     */
    public void setDate(int propertyIndex, Date value) {
        convertValueAndSet(propertyIndex, value);
    }

    /**
     * Sets the value of a <code>String</code> property identified by the specified property index, to the specified value.
     * @param propertyIndex the index of the property.
     * @param value the new value for the property.
     * @see #set(int, Object)
     */
    public void setString(int propertyIndex, String value) {
        convertValueAndSet(propertyIndex, value);
    }

    /**
     * Sets the value of a <code>List</code> property identified by the specified property index, to the specified value.
     * @param propertyIndex the index of the property.
     * @param value the new value for the property.
     * @see #set(int, Object)
     * @see #setList(Property, List)
     */
    public void setList(int propertyIndex, List value) {
        convertValueAndSet(propertyIndex, value);
    }

    /**
     * Returns the value of the given property of this object.
     * <p>
     * If the property is {@link Property#isMany many-valued},
     * the result will be a {@link java.util.List}
     * and each object in the List will be {@link Type#isInstance an instance of}
     * the property's {@link Property#getType type}.
     * Otherwise the result will directly be an instance of the property's type.
     * @param property the property of the value to fetch.
     * @return the value of the given property of the object.
     * @see #set(Property, Object)
     * @see #unset(Property)
     * @see #isSet(Property)
     */
    public Object get(Property property) throws IllegalArgumentException {
        return get(property, true);
    }

    /**
     * INTERNAL:
     * Returns the value of the given property of this object.
     * <p>
     * If the property is {@link Property#isMany many-valued},
     * the result will be a {@link java.util.List}
     * and each object in the List will be {@link Type#isInstance an instance of}
     * the property's {@link Property#getType type}.
     * Otherwise the result will directly be an instance of the property's type.
     * @param property the property of the value to fetch.
     * @return the value of the given property of the object.
     * @see #set(Property, Object)
     * @see #unset(Property)
     * @see #isSet(Property)
     * @param updateSequence
     * @return
     * @throws IllegalArgumentException
     */
    public Object get(Property property, boolean updateSequence) throws IllegalArgumentException {
        if (null == property) {// check null property before null type
            throw new IllegalArgumentException("Argument not Supported.");
        }

        if ((null != type) && !type.isOpen() && property.isOpenContent()) {
            throw new IllegalArgumentException("Argument not Supported.");
        }
        if (property.isMany()) {
            return getList(property, updateSequence);
        }

        // TODO: handle NULL = (NULL) value and !(!found)
        if (isSet(property)) {
            return getPropertyInternal(property);
        }

        // return the default or a cached pseudo default Object for an unset property
        // JIRA-253: We are wrapping primitive numerics with a wrapper Object
        return property.getDefault();
    }

    /**
     * INTERNAL:
     * Create a dynamic open content property if no property exists for (name).
     * @param name
     * @param value
     * @return Property
     * @throws UnsupportedOperationException
     * @throws IllegalArgumentException
     */
    public Property defineOpenContentProperty(String name, Object value) throws UnsupportedOperationException, IllegalArgumentException {
        DataObject propertyDO = aHelperContext.getDataFactory().create(SDOConstants.SDO_PROPERTY);

        // TODO: see #5770518: we should not be here with a property that was not found in an instanceProperties call IE: value == null
        propertyDO.set("name", name);
        Type sdotype = null;
        boolean isMany = false;
        boolean isContainment = false;
        Class valueClass = value.getClass();

        if (value == null) {
            return null;
        }
        if (value instanceof Collection) {
            if (((Collection)value).size() > 0) {
                Object firstObject = ((Collection)value).iterator().next();
                if (firstObject != null) {
                    valueClass = firstObject.getClass();
                    if (firstObject instanceof DataObject) {
                        if (((DataObject)firstObject).getContainer() == null) {
                            isContainment = true;
                        }
                        sdotype = ((DataObject)firstObject).getType();
                    } else {
                        sdotype = ((SDOTypeHelper)aHelperContext.getTypeHelper()).getTypeForSimpleJavaType(valueClass);
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
            isMany = true;
        } else if (value instanceof DataObject) {
            if (((DataObject)value).getContainer() == null) {
                isContainment = true;
            }
            sdotype = ((DataObject)value).getType();
        } else {
            sdotype = ((SDOTypeHelper)aHelperContext.getTypeHelper()).getTypeForSimpleJavaType(valueClass);
        }
        propertyDO.set("type", sdotype);

        propertyDO.set("many", isMany);

        propertyDO.set("containment", isContainment);
        return aHelperContext.getTypeHelper().defineOpenContentProperty(null, propertyDO);
    }

    /**
     * Sets the value of the given property of the object to the new value.
     * <p>
     * If the property is {@link Property#isMany many-valued},
     * the new value must be a {@link java.util.List}
     * and each object in that list must be {@link Type#isInstance an instance of}
     * the property's {@link Property#getType type};
     * the existing contents are cleared and the contents of the new value are added.
     * Otherwise the new value directly must be an instance of the property's type
     * and it becomes the new value of the property of the object.
     * @param property the property of the value to set.
     * @param value the new value for the property.
     * @see #unset(Property)
     * @see #isSet(Property)
     * @see #get(Property)
     * @throws UnsupportedOperationException
     * @throws IllegalArgumentException
     */
    public void set(Property property, Object value) throws UnsupportedOperationException, IllegalArgumentException {
        set(property, value, true);
    }

    /**
     * INTERNAL:
     * Sets the value of the given property of the object to the new value.
     * <p>
     * The use of a false updateSequence flag is internally implemented during an SDOSequence.add() call.
     * Refactor: we need to abstract this function using a type of Command pattern to handle the sequence context.
     * @param property
     * @param value
     * @param updateSequence
     * @throws UnsupportedOperationException
     * @throws IllegalArgumentException
     */
    public void set(Property property, Object value, boolean updateSequence) throws UnsupportedOperationException, IllegalArgumentException {
        if (null == property) {
            throw new IllegalArgumentException("Illegal Argument.");
        }

        if (property.isReadOnly()) {
            throw new UnsupportedOperationException("Property is Readonly." + property.getName() + "  " + getType().getName());
        }

        //TODO: if property is many do we check that the value is a list and if not create one				
        //TODO: if is many do we clear current list or just replace with the new one
        if (null == getType()) {
            throw new UnsupportedOperationException("Type is null");
        }

        if (property.isOpenContent() && !getType().isOpen()) {// not open and not in types currentValueStore
            throw new IllegalArgumentException("DataObject " + this + " is not Open for property " + property.getName());
        }

        // Note: get() will call setPropertyInternal() if the list is null = not set yet - we need to propagate the updateSequence flag
        Object oldValue = get(property, updateSequence);
        boolean wasSet = isSet(property);
        if (wasSet && (oldValue == value)) {
            return;
        } else {
            _setModified(true);
        }

        if (property.isMany()) {
            if (null == value) {
                // TODO: handle different implementations of List
                value = new ListWrapper(this, property);
            }
            if (!(value instanceof Collection)) {
                throw new IllegalArgumentException("Properties with isMany = true can only be set on list values.");
            }

            // get existing list or empty "default" ListWrapper - values will be cleared and added below
            List listValue = (List)value;
            if (property.isContainment()) {
                //TODO: need to check for circular reference in Many case
                for (int i = 0, size = listValue.size(); i < size; i++) {
                    Object next = listValue.get(i);
                    if (next instanceof SDODataObject) {
                        if (parentContains(next)) {
                            throw new IllegalArgumentException("Circular reference.");
                        }
                    }
                }
            }
            listValue = (List)oldValue;
            // 20060529: v33: Move clear() out of ListWrapper.addAll()
            // handle clearing of elements which also calls removeContainment(prop) outside of addAll()
            ((ListWrapper)listValue).clear(updateSequence);
            // handle updateContainment and sequences inside addAll() 
            ((ListWrapper)listValue).addAll((Collection)value, updateSequence);// for non-default Pluggable impl this function is not required
        } else {
            if (property.isContainment()) {
                if (value instanceof SDODataObject) {
                    if (parentContains(value)) {
                        throw new IllegalArgumentException("Circular reference.");
                    }
                }

                // detach the oldValue from this dataObject
                detach(property, oldValue);

                // sets the new value's container and containment prop to this dataobject, detaches from other owner...not right

                /**
                 * Case: set(do) is actually a move(do) between two CS - detach required
                 * Case: set(do) is actually an add(do) on a single CS - detach not required
                 */
                if ((value != null) && value instanceof DataObject) {
                    updateContainment(property, (DataObject)value);
                }
            }

            // process pluggable currentValueStore and set [value] as a property of [this] as well as sequences
            setPropertyInternal(property, value, updateSequence);
        }
        if (getType().isOpen() && property.isOpenContent()) {
            addOpenContentProperty(property);
        }

        //TODO:When a DataObject is set or added to a containment Property, it is
        //removed from any previous containment Property. Containment cannot have cycles. If a set or
        //add would produce a containment cycle, an exception is thrown.
        //account for Lists in circular cycles    
    }

    /**
     * Returns whether the property of the object is considered to be set.
     * <p>
     * isSet() for many-valued Properties returns true if the List is not empty and
     * false if the List is empty.  For single-valued Properties:
     * <ul><li>If the Property has not been set() or has been unset() then isSet() returns false.</li>
     * <li>If the current value is not the Property's default or null, isSet() returns true.</li>
     * <li>For the remaining cases the implementation may decide between two policies: </li>
     * <ol><li>any call to set() without a call to unset() will cause isSet() to return true, or </li>
     *   <li>the current value is compared to the default value and isSet() returns true when they differ.</li>
     * </ol></ul><p>
     * @param property the property in question.
     * @return whether the property of the object is set.
     * @see #set(Property, Object)
     * @see #unset(Property)
     * @see #get(Property)
     */
    public boolean isSet(Property property) {
        if (null == property) {
            throw SDOException.cannotPerformOperationOnNullArgument("isSet");
        }

        if (property.isMany()) {
            List value = getList(property);
            return !value.isEmpty();
        } else {
            return isSetInternal(property);
            // TODO: handle NULL = (NULL) value and !(!found)
        }
    }

    /**
     * Unsets the property of the object.
     * <p>
     * If the property is {@link Property#isMany many-valued},
     * the value must be an {@link java.util.List}
     * and that list is cleared.
     * Otherwise,
     * the value of the property of the object
     * is set to the property's {@link Property#getDefault default value}.
     * The property will no longer be considered {@link #isSet set}.
     * @param property the property in question.
     * @see #isSet(Property)
     * @see #set(Property, Object)
     * @see #get(Property)
     */
    public void unset(Property property) {
        if (null == property) {
            throw SDOException.cannotPerformOperationOnNullArgument("unset");
        }
        unset(property, false);
    }

    /**
     * INTERNAL:
     * This function is implemented internally to unset the specified property on this DataObject
     * @param property
     * @param fromDelete
     */
    private void unset(Property property, boolean fromDelete) {
        unset(property, fromDelete, true);
    }

    /**
     * INTERNAL:
     * Unset the specified property on this DataObject.
     * The fromDelete parameter specifies whether we are from a delete or unset/detach operation.
     * The updateSequence parameter is used internally to stop a bidirectional update in the SDOSequence
     * when originally called from this Sequence.
     * @param property
     * @param fromDelete
     * @param updateSequence
     */
    public void unset(Property property, boolean fromDelete, boolean updateSequence) {
        if (null == property) {
            throw SDOException.cannotPerformOperationOnNullArgument("unset");
        }

        if (property.isReadOnly()) {
            //TODO: throw UnsupportedOperationException
        }

        boolean wasSet = isSet(property);

        if (wasSet) {
            _setModified(true);
        } else {
            return;
        }

        if (property.isContainment()) {
            Object oldValue = get(property);
            if (oldValue != null) {
                if (property.isMany()) {
                    for (int itemIndex = 0, size = ((List)oldValue).size(); itemIndex < size;
                             itemIndex++) {
                        SDODataObject manyItem = (SDODataObject)((List)oldValue).get(itemIndex);
                        if (manyItem != null) {
                            manyItem.detachOrDelete(fromDelete);
                        }
                    }
                } else {
                    ((SDODataObject)oldValue).detachOrDelete(fromDelete);
                }
            }
        }

        if (wasSet) {
            unsetInternal(property, updateSequence);
        }
    }

    /**
     * Returns the value of the specified <code>boolean</code> property.
     * @param property the property to get.
     * @return the <code>boolean</code> value of the specified property.
     * @see #get(Property)
     */
    public boolean getBoolean(Property property) throws IllegalArgumentException, ClassCastException {
        Boolean propertyBooleanValue = (Boolean)convertObjectToValue(property, Boolean.class);
        if (propertyBooleanValue == null) {
            return false;
        }
        return propertyBooleanValue.booleanValue();
    }

    /**
     * Returns the value of the specified <code>byte</code> property.
     * @param property the property to get.
     * @return the <code>byte</code> value of the specified property.
     * @see #get(Property)
     */
    public byte getByte(Property property) throws IllegalArgumentException, ClassCastException {
        Byte propertyByteValue = (Byte)convertObjectToValue(property, Byte.class);
        if (propertyByteValue == null) {
            return 0;
        }
        return propertyByteValue.byteValue();
    }

    /**
     * Returns the value of the specified <code>char</code> property.
     * @param property the property to get.
     * @return the <code>char</code> value of the specified property.
     * @see #get(Property)
     */
    public char getChar(Property property) throws IllegalArgumentException {
        Character propertyCharValue = (Character)convertObjectToValue(property, Character.class);
        if (propertyCharValue == null) {
            return '\u0000';
        }
        return propertyCharValue.charValue();
    }

    /**
     * Returns the value of the specified <code>double</code> property.
     * @param property the property to get.
     * @return the <code>double</code> value of the specified property.
     * @see #get(Property)
     */
    public double getDouble(Property property) throws IllegalArgumentException {
        Double propertyDoubleValue = (Double)convertObjectToValue(property, Double.class);
        if (propertyDoubleValue == null) {
            return 0.0d;
        }
        return propertyDoubleValue.doubleValue();
    }

    /**
     * Returns the value of the specified <code>float</code> property.
     * @param property the property to get.
     * @return the <code>float</code> value of the specified property.
     * @see #get(Property)
     */
    public float getFloat(Property property) throws IllegalArgumentException {
        Float propertyFloatValue = (Float)convertObjectToValue(property, Float.class);
        if (propertyFloatValue == null) {
            return 0.0f;
        }
        return propertyFloatValue.floatValue();
    }

    /**
     * Returns the value of the specified <code>int</code> property.
     * @param property the property to get.
     * @return the <code>int</code> value of the specified property.
     * @see #get(Property)
     */
    public int getInt(Property property) throws IllegalArgumentException {
        Integer propertyIntegerValue = (Integer)convertObjectToValue(property, Integer.class);
        if (propertyIntegerValue == null) {
            return 0;
        }
        return propertyIntegerValue.intValue();
    }

    /**
     * Returns the value of the specified <code>long</code> property.
     * @param property the property to get.
     * @return the <code>long</code> value of the specified property.
     * @see #get(Property)
     */
    public long getLong(Property property) throws IllegalArgumentException {
        Long propertyLongValue = (Long)convertObjectToValue(property, Long.class);
        if (propertyLongValue == null) {
            return 0L;
        }
        return propertyLongValue.longValue();
    }

    /**
     * Returns the value of the specified <code>short</code> property.
     * @param property the property to get.
     * @return the <code>short</code> value of the specified property.
     * @see #get(Property)
     */
    public short getShort(Property property) throws IllegalArgumentException {
        Short propertyShortValue = (Short)convertObjectToValue(property, Short.class);
        if (propertyShortValue == null) {
            return 0;
        }
        return propertyShortValue.shortValue();
    }

    /**
     * Returns the value of the specified <code>byte[]</code> property.
     * @param property the property to get.
     * @return the <code>byte[]</code> value of the specified property.
     * @see #get(Property)
     */
    public byte[] getBytes(Property property) throws IllegalArgumentException {
        byte[] propertyByteValue = (byte[])convertObjectToValue(property, byte[].class);
        return propertyByteValue;
    }

    /**
     * Returns the value of the specified <code>BigDecimal</code> property.
     * @param property the property to get.
     * @return the <code>BigDecimal</code> value of the specified property.
     * @see #get(Property)
     */
    public BigDecimal getBigDecimal(Property property) throws IllegalArgumentException {
        BigDecimal propertyDecimalValue = (BigDecimal)convertObjectToValue(property, BigDecimal.class);
        return propertyDecimalValue;
    }

    /**
     * Returns the value of the specified <code>BigInteger</code> property.
     * @param property the property to get.
     * @return the <code>BigInteger</code> value of the specified property.
     * @see #get(Property)
     */
    public BigInteger getBigInteger(Property property) throws IllegalArgumentException {
        BigInteger propertyBigIntegerValue = (BigInteger)convertObjectToValue(property, BigInteger.class);
        return propertyBigIntegerValue;
    }

    /**
     * Returns the value of the specified <code>DataObject</code> property.
     * @param property the property to get.
     * @return the <code>DataObject</code> value of the specified property.
     * @see #get(Property)
     */
    public DataObject getDataObject(Property property) throws IllegalArgumentException, ClassCastException {
        return (DataObject)get(property);
    }

    /**
     * Returns the value of the specified <code>Date</code> property.
     * @param property the property to get.
     * @return the <code>Date</code> value of the specified property.
     * @see #get(Property)
     */
    public Date getDate(Property property) {
        if (null == property) {
            throw SDOException.cannotPerformOperationOnNullArgument("getDate");
        }
        if (property.getType().equals(SDOConstants.SDO_STRING)) {
            DataHelper dHelper = aHelperContext.getDataHelper();
            String dateString = (String)get(property);
            return dHelper.toDate(dateString);
        }
        Date propertyDateValue = (Date)convertObjectToValue(property, Date.class);
        return propertyDateValue;
    }

    /**
     * Returns the value of the specified <code>String</code> property.
     * @param property the property to get.
     * @return the <code>String</code> value of the specified property.
     * @see #get(Property)
     */
    public String getString(Property property) {
        String propertyStringValue = (String)convertObjectToValue(property, String.class);
        return propertyStringValue;
    }

    /**
     * Returns the value of the specified <code>List</code> property.
     * The List returned contains the current values.
     * Updates through the List interface operate on the current values of the DataObject.
     * Each access returns the same List object.
     * @param property the property to get.
     * @return the <code>List</code> value of the specified property.
     * @see #get(Property)
     */
    public List getList(Property property) {
        return getList(property, true);
    }

    /**
     * INTERNAL:
     * Returns the value of the specified <code>List</code> property.
     * The List returned contains the current values.
     * Updates through the List interface operate on the current values of the DataObject.
     * Each access returns the same List object.
     * @param property the property to get.
     * @return the <code>List</code> value of the specified property.
     * @see #get(Property)
     * @param updateSequence
     * @return
     */
    private List getList(Property property, boolean updateSequence) {
        if (null == property) {
            throw SDOException.cannotPerformOperationOnNullArgument("getList");
        }

        if (((type != null) && !type.isOpen() && property.isOpenContent())) {
            throw new IllegalArgumentException("Arguement not Supported.");
        }
        if (!property.isMany()) {
            throw new ClassCastException("can not call getList for a property that has isMany false.");
        }

        /*
         * Check the currentValueStore map
         * (1) If the property is stored
         *     (1a) return the ListWrapper or
         *     (1b) return and store an empty default value ListWrapper
         * (2) if the property is not set
         *     (2a) return and store an empty default value ListWrapper
         */
        Object value = getPropertyInternal(property);
        if ((value != null) && value instanceof List) {
            return (List)value;
        }

        // TODO: handle different implementations of List
        // get a default empty list
        List theList = new ListWrapper(this, property);
        if (getType().isOpen() && property.isOpenContent()) {
            addOpenContentProperty(property);
        }

        // set the property to a default empty list
        setPropertyInternal(property, theList, false);
        return theList;
    }

    /**
     * Sets the value of the specified <code>boolean</code> property, to the specified value.
     * @param property the property to set.
     * @param value the new value for the property.
     * @see #set(Property, Object)
     */
    public void setBoolean(Property property, boolean value) {
        convertValueAndSet(property, new Boolean(value));
    }

    /**
     * Sets the value of the specified <code>byte</code> property, to the specified value.
     * @param property the property to set.
     * @param value the new value for the property.
     * @see #set(Property, Object)
     */
    public void setByte(Property property, byte value) {
        convertValueAndSet(property, new Byte(value));
    }

    /**
     * Sets the value of the specified <code>char</code> property, to the specified value.
     * @param property the property to set.
     * @param value the new value for the property.
     * @see #set(Property, Object)
     */
    public void setChar(Property property, char value) {
        convertValueAndSet(property, new Character(value));
    }

    /**
     * Sets the value of the specified <code>double</code> property, to the specified value.
     * @param property the property to set.
     * @param value the new value for the property.
     * @see #set(Property, Object)
     */
    public void setDouble(Property property, double value) {
        convertValueAndSet(property, new Double(value));
    }

    /**
     * Sets the value of the specified <code>float</code> property, to the specified value.
     * @param property the property to set.
     * @param value the new value for the property.
     * @see #set(Property, Object)
     */
    public void setFloat(Property property, float value) {
        convertValueAndSet(property, new Float(value));
    }

    /**
     * Sets the value of the specified <code>int</code> property, to the specified value.
     * @param property the property to set.
     * @param value the new value for the property.
     * @see #set(Property, Object)
     */
    public void setInt(Property property, int value) {
        convertValueAndSet(property, new Integer(value));
    }

    /**
     * Sets the value of the specified <code>long</code> property, to the specified value.
     * @param property the property to set.
     * @param value the new value for the property.
     * @see #set(Property, Object)
     */
    public void setLong(Property property, long value) {
        convertValueAndSet(property, new Long(value));
    }

    /**
     * Sets the value of the specified <code>short</code> property, to the specified value.
     * @param property the property to set.
     * @param value the new value for the property.
     * @see #set(Property, Object)
     */
    public void setShort(Property property, short value) {
        convertValueAndSet(property, new Short(value));
    }

    /**
     * Sets the value of the specified <code>byte[]</code> property, to the specified value.
     * @param property the property to set.
     * @param value the new value for the property.
     * @see #set(Property, Object)
     */
    public void setBytes(Property property, byte[] value) {
        convertValueAndSet(property, value);
    }

    /**
     * Sets the value of the specified <code>BigDecimal</code> property, to the specified value.
     * @param property the property to set.
     * @param value the new value for the property.
     * @see #set(Property, Object)
     */
    public void setBigDecimal(Property property, BigDecimal value) {
        convertValueAndSet(property, value);
    }

    /**
     * Sets the value of the specified <code>BigInteger</code> property, to the specified value.
     * @param property the property to set.
     * @param value the new value for the property.
     * @see #set(Property, Object)
     */
    public void setBigInteger(Property property, BigInteger value) {
        convertValueAndSet(property, value);
    }

    /**
     * Sets the value of the specified <code>DataObject</code> property, to the specified value.
     * @param property the property to set.
     * @param value the new value for the property.
     * @see #set(Property, Object)
     */
    public void setDataObject(Property property, DataObject value) {
        set(property, value);
    }

    /**
     * Sets the value of the specified <code>Date</code> property, to the specified value.
     * @param property the property to set.
     * @param value the new value for the property.
     * @see #set(Property, Object)
     */
    public void setDate(Property property, Date value) {
        convertValueAndSet(property, value);
    }

    /**
     * Sets the value of the specified <code>String</code> property, to the specified value.
     * @param property the property to set.
     * @param value the new value for the property.
     * @see #set(Property, Object)
     */
    public void setString(Property property, String value) {
        convertValueAndSet(property, value);
    }

    /**
     * Sets the value of the specified <code>List</code> property, to the specified value.
     * <p> The new value must be a {@link java.util.List}
     * and each object in that list must be {@link Type#isInstance an instance of}
     * the property's {@link Property#getType type};
     * the existing contents are cleared and the contents of the new value are added.
     * @param property the property to set.
     * @param value the new value for the property.
     * @see #set(Property, Object)
     */
    public void setList(Property property, List value) {
        convertValueAndSet(property, value);
    }

    /**
     * Returns a new {@link DataObject data object} contained by this object using the specified property,
     * which must be a {@link Property#isContainment containment property}.
     * The type of the created object is the {@link Property#getType declared type} of the specified property.
     * @param propertyName the name of the specified containment property.
     * @return the created data object.
     * @see #createDataObject(String, String, String)
     */
    public DataObject createDataObject(String propertyName) {
        Property aProperty = getInstanceProperty(propertyName);
        return createDataObject(aProperty);
    }

    /**
     * Returns a new {@link DataObject data object} contained by this object using the specified property,
     * which must be a {@link Property#isContainment containment property}.
     * The type of the created object is the {@link Property#getType declared type} of the specified property.
     * @param propertyIndex the index of the specified containment property.
     * @return the created data object.
     * @see #createDataObject(int, String, String)
     */
    public DataObject createDataObject(int propertyIndex) {
        Property aProperty = getInstanceProperty(propertyIndex);
        return createDataObject(aProperty);
    }

    /**
     * Returns a new {@link DataObject data object} contained by this object using the specified property,
     * which must be a {@link Property#isContainment containment property}.
     * The type of the created object is the {@link Property#getType declared type} of the specified property.
     * @param aProperty the specified containment property.
     * @return the created data object.
     * @see #createDataObject(Property, Type)
     */
    public DataObject createDataObject(Property aProperty) {
        if (aProperty.isContainment()) {
            Type aType = aProperty.getType();
            if (aType != null) {
                return createDataObject(aProperty, aType);
            }
        }

        //TODO: throw exception?
        return null;
    }

    /**
     * Returns a new {@link DataObject data object} contained by this object using the specified property,
     * which must be a {@link Property#isContainment containment property}.
     * The type of the created object is specified by the packageURI and typeName arguments.
     * The specified type must be a compatible target for the property identified by propertyName.
     * @param propertyName the name of the specified containment property.
     * @param namespaceURI the namespace URI of the package containing the type of object to be created.
     * @param typeName the name of a type in the specified package.
     * @return the created data object.
     * @see #createDataObject(String)
     * @see DataGraph#getType
     */
    public DataObject createDataObject(String propertyName, String namespaceURI, String typeName) {
        Property aProperty = getInstanceProperty(propertyName);
        Type aType = aHelperContext.getTypeHelper().getType(namespaceURI, typeName);
        return createDataObject(aProperty, aType);
    }

    /**
     * Returns a new {@link DataObject data object} contained by this object using the specified property,
     * which must be a {@link Property#isContainment containment property}.
     * The type of the created object is specified by the packageURI and typeName arguments.
     * The specified type must be a compatible target for the property identified by propertyIndex.
     * @param propertyIndex the index of the specified containment property.
     * @param namespaceURI the namespace URI of the package containing the type of object to be created.
     * @param typeName the name of a type in the specified package.
     * @return the created data object.
     * @see #createDataObject(int)
     * @see DataGraph#getType
     */
    public DataObject createDataObject(int propertyIndex, String namespaceURI, String typeName) {
        Property aProperty = getInstanceProperty(propertyIndex);
        Type aType = aHelperContext.getTypeHelper().getType(namespaceURI, typeName);
        return createDataObject(aProperty, aType);
    }

    /**
     * Returns a new {@link DataObject data object} contained by this object using the specified property,
     * which must be of {@link Property#isContainment containment type}.
     * The type of the created object is specified by the type argument,
     * which must be a compatible target for the specifed property.
     * @param property a containment property of this object.
     * @param type the type of object to be created.
     * @return the created data object.
     * @see #createDataObject(int)
     */
    public DataObject createDataObject(Property property, Type aType) {
        DataObject created = aHelperContext.getDataFactory().create(aType);

        if (property.isMany()) {
            //getList.add will call updateContainment
            ((ListWrapper)getList(property)).add(created, false);
        } else {//TODO:  do we need this check if (property.getType().equals(aType)) {
            set(property, created);
            //TODO: should we be calling set
        }

        // TODO: verify refactor
        _setModified(true);
        ((SDODataObject)created)._setCreated(true);

        return created;
    }

    /**
     * INTERNAL:
     * Perform a detach on a DataObject or List of DataObjects
     * @param property
     * @param oldValue (List or Object)
     *@param fromDelete
     */
    private void detach(Property property, Object oldValue) {
        // This function is called internally from set(property, object, boolean) when a detach of the existing object is required
        if (property.isMany()) {
            for (int i = 0, size = ((List)oldValue).size(); i < size; i++) {
                Object nextValue = ((List)oldValue).get(i);
                if ((nextValue != null) && (nextValue instanceof SDODataObject)) {
                    ((SDODataObject)nextValue).detachOrDelete(false);
                }
            }
        } else {
            if ((oldValue != null) && (oldValue instanceof SDODataObject)) {
                ((SDODataObject)oldValue).detachOrDelete(false);
            }
        }
    }

    /**
     * INTERNAL:
     * Recursively walk the tree and set oldSettings for a detached/deleted object.
     * This function performs a single preOrder traversal of the tree.
     * An unset is done for each property if the action = delete
     * Implementors: detach() and delete() via detach
     * @param fromDelete (flag the action true = delete, false = detach)
     */
    public void detachOrDelete(boolean fromDelete) {
        // If we are detaching the root - perform a no-operation
        if ((null == getContainer()) && !fromDelete) {
            return;
        }
        boolean isCSRoot = (null != getChangeSummary()) && (getChangeSummary().getRootObject() == this);

        // This is the root of the recursive loop
        detachDeleteRecursivePrivate(fromDelete, !isCSRoot, true, isCSRoot);
    }

    /**
     * INTERNAL:
     * Recursively walk the tree and set oldSettings for a detached/deleted object.
     * @param fromDelete
     * @param clearCS (true = clear the cs field)
     * @param isRootOfRecursiveLoop (are we at the root of the detach/delete or inside the subtree)
     * @param subTreeRootHasCS (is root of the detach/delete subtree the CS root?)
     */
    private void detachDeleteRecursivePrivate(boolean fromDelete, boolean clearCS, boolean isRootOfRecursiveLoop, boolean subTreeRootHasCS) {
        if (null == getContainer()) {
            clearCS = false;
        }
        if (isRootOfRecursiveLoop || fromDelete) {
            if (null != getContainer()) {
                ((SDODataObject)getContainer())._setModified(true);
                _setContainer(null);
                _setContainmentPropertyName(null);
            }
        }
        _setDeleted(true);

        List instancePropertiesList = getInstanceProperties();
        for (int i = 0, psize = instancePropertiesList.size(); i < psize; i++) {
            Property nextProperty = (Property)instancePropertiesList.get(i);
            Object oldValue = get(nextProperty);

            if (nextProperty.getType() != SDOConstants.SDO_CHANGESUMMARY) {
                if (nextProperty.isContainment()) {
                    if (nextProperty.isMany()) {
                        Object manyItem;
                        for (int j = 0, lsize = ((List)oldValue).size(); j < lsize; j++) {
                            manyItem = ((List)oldValue).get(j);
                            detachDeleteRecursivePrivateHelper((SDODataObject)manyItem, fromDelete, clearCS, subTreeRootHasCS);
                        }
                    } else {
                        detachDeleteRecursivePrivateHelper((SDODataObject)oldValue, fromDelete, clearCS, subTreeRootHasCS);
                    }
                }
                if (fromDelete && !nextProperty.isReadOnly()) {
                    unset(nextProperty, fromDelete);
                }
            }
        }

        /**
         * Bug # 6202793
         * We delete the changeSummary field in the following use cases
         * in addition to when the clearCS field was passed in as or transformed to true.
         * Case 0: detach subtree root from root (with cs) = false
         * Case 1: detach subtree internal from root (with cs) = false
         * Case 2: delete subtree root from root (with cs) = false
         * Case 3: delete subtree internal from root (with cs) = true
         * Case 4: detach subtree root from root (without cs) = false
         * Case 5: detach subtree internal from root (without cs) = false
         * Case 6: delete subtree root from root (without cs) = true
         * Case 7: delete subtree internal from root (without cs) = true
         */
        if (clearCS) {// || // uncomment the following 2 lines to fix 6202793
            //(!subTreeRootHasCS && fromDelete) || // Case 3 and 7 
            //(isRootOfRecursiveLoop && fromDelete && !subTreeRootHasCS)) { // case 6
            _setChangeSummary(null);
        }
    }

    /**
     * INTERNAL:
     * Call detach or delete recursively on aDataObject after possibly changing the flag whether to clear the ChangeSummary pointer at this level
     * @param anObject
     * @param isCSRoot
     * @param fromDelete
     * @param subTreeRootHasCS
     */
    private void detachDeleteRecursivePrivateHelper(SDODataObject aDataObject, boolean fromDelete, boolean clearCS, boolean subTreeRootHasCS) {
        if (aDataObject != null) {
            // Return whether (aDataObject) is the changeSummary root.
            boolean isCSRoot = (aDataObject.getChangeSummary() != null) &&//
            (aDataObject.getChangeSummary().getRootObject() == aDataObject);

            // If we are at the CS root - we do not clear the changeSummary
            if (isCSRoot) {
                clearCS = false;
            } else {
                if (aDataObject.getContainer() != null) {
                    ChangeSummary containerCS = aDataObject.getContainer().getChangeSummary();

                    // If there is no CS field set above this object then clear any ChangeSummary pointer at this level 
                    if (containerCS == null) {
                        clearCS = true;
                    }
                }
            }
            aDataObject.detachDeleteRecursivePrivate(fromDelete, clearCS, false, subTreeRootHasCS);
        }
    }

    /**
     * Removes this DataObject from its container, if any.
     * Same as
     *  getContainer().getList(getContainmentProperty()).remove(this) or
     *  getContainer().unset(getContainmentProperty())
     * depending on getContainmentProperty().isMany() respectively.
     */
    public void detach() {
        detachWithSequenceUpdate();
    }

    /**
     * INTERNAL:
     * Perform a detach action that originated from a detach and update the sequence.
     * Case 01
     */
    private void detachWithSequenceUpdate() {
        detach(false, true);
    }

    /**
     * INTERNAL:
     * Perform a detach action that originated from a delete and update the sequence.
     * Case 11
     */
    private void deleteWithSequenceUpdate() {
        detach(true, true);
    }

    /**
     * INTERNAL:
     * Removes this DataObject from its container, if any.
     * Same as
     *  getContainer().getList(getContainmentProperty()).remove(this) or
     *  getContainer().unset(getContainmentProperty())
     *  depending on getContainmentProperty().isMany() respectively.
     *  @param fromDelete (true = delete action, false = detach/unset)
     *  @param updateSequence
     */
    private void detach(boolean fromDelete, boolean updateSequence) {
        // Note: there is no case10 where fromDelete=true and updateSequence=false
        Property containmentProperty = getContainmentProperty();
        _setDeleted(true);

        if ((containmentProperty != null) && containmentProperty.isReadOnly()) {
            //TODO: throw UnsupportedOperationException                                  
        }

        if (containmentProperty != null) {
            if (getContainmentProperty().isMany()) {
                List oldList = getContainer().getList(containmentProperty);

                // pass remove containment flag instead of calling remove(this) and detachOrDelete(fromDelete) separately
                // This will invoke creation of an intact list copy before removing its containment and clearing its changeSummary
                ((ListWrapper)oldList).remove(this, fromDelete, updateSequence);
            } else {
                ((SDODataObject)getContainer()).unset(containmentProperty, fromDelete, updateSequence);
            }
        } else {
            detachOrDelete(fromDelete);
        }
    }

    /**
     * Remove this object from its container and then unset all its non-{@link Property#isReadOnly readOnly} Properties.
     * If this object is contained by a {@link Property#isReadOnly readOnly} {@link Property#isContainment containment property}, its non-{@link Property#isReadOnly readOnly} Properties will be unset but the object will not be removed from its container.
     * All DataObjects recursively contained by {@link Property#isContainment containment Properties} will also be deleted.
     */
    public void delete() {
        //TODO: unset all non read-only currentValueStore
        //TODO: many shouldn't clear list just remove item from list                
        deleteWithSequenceUpdate();
    }

    /**
     * Returns the containing {@link DataObject data object}
     * or <code>null</code> if there is no container.
     * @return the containing data object or <code>null</code>.
     */
    public DataObject getContainer() {
        return container;
    }

    /**
     * Return the Property of the {@link DataObject data object} containing this data object
     * or <code>null</code> if there is no container.
     * @return the property containing this data object.
     */
    public Property getContainmentProperty() {
        if ((container != null) && (containmentPropertyName != null)) {
            return container.getInstanceProperty(containmentPropertyName);
        } else {
            return null;
        }
    }

    /**
     * Returns the {@link DataGraph data graph} for this object or <code>null</code> if there isn't one.
     * @return the containing data graph or <code>null</code>.
     */
    public DataGraph getDataGraph() {
        throw new UnsupportedOperationException("Method Not Supported.");
    }

    /**
     * Returns the data object's type.
     * <p>
     * The type defines the Properties available for reflective access.
     * @return the type.
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns a read-only List of the Properties currently used in this DataObject.
     * This list will contain all of the Properties in getType().getProperties()
     * and any Properties where isSet(property) is true.
     * For example, Properties resulting from the use of
     * open or mixed XML content are present if allowed by the Type.
     * the List does not contain duplicates.
     * The order of the Properties in the List begins with getType().getProperties()
     * and the order of the remaining Properties is determined by the implementation.
     * The same list will be returned unless the DataObject is updated so that
     * the contents of the List change.
     * @return the List of Properties currently used in this DataObject.
     */
    public List getInstanceProperties() {
        if (null == instanceProperties) {
            instanceProperties = new ArrayList();
        }
        return instanceProperties;
    }

    /**
    * @deprecated replaced by {@link #getInstanceProperty()} in 2.1.0
    */
    public Property getProperty(String propertyName) {
        return getInstanceProperty(propertyName);
    }

    /**
     * Returns the named Property from the current instance currentValueStore,
     * or null if not found.  The instance currentValueStore are getInstanceProperties().
     * @param propertyName the name of the Property
     * @return the named Property from the DataObject's current instance currentValueStore, or null.
     */
    public Property getInstanceProperty(String propertyName) {
        if (getType() == null) {
            throw new UnsupportedOperationException("Type is null");
        }
        Property p = getType().getProperty(propertyName);
        if (null == p) {
            p = (Property)_getOpenContentPropertiesMap().get(propertyName);
        }
        return p;
    }

    /**
     * INTERNAL:
     * @param propertyIndex
     * @return
     * @throws SDOException
     */
    public Property getInstanceProperty(int propertyIndex) throws IllegalArgumentException {
        try {
            Property property = getInstancePropertiesArray()[propertyIndex];
            return property;
        } catch (IndexOutOfBoundsException e) {
            throw SDOException.propertyNotFoundAtIndex(e, propertyIndex);
        }
    }

    /**
     * Returns the root {@link DataObject data object}.
     * @return the root data object.
     */
    public DataObject getRootObject() {
        if (getContainer() != null) {
            return getContainer().getRootObject();
        }
        return this;
    }

    /**
     * Returns the ChangeSummary with scope covering this dataObject, or null
     * if there is no ChangeSummary.
     * @return the ChangeSummary with scope covering this dataObject, or null.
     */
    public ChangeSummary getChangeSummary() {
        return changeSummary;
    }

    /**
     * INTERNAL:
     * Set flag created value.
     * @deprecated since OracleAS TopLink 11<i>1</i> (11.1.1.0) 17-May-2007.
     * Use {@link #_setCreated(boolean)} instead
     * @param created   flag created's new value.
     */
    public void setCreated(boolean created) {
        _setCreated(created);
    }

    /**
     * INTERNAL:
     * Set flag created value.
     * @param created   flag created's new value.
     */
    public void _setCreated(boolean created) {
        if (changeSummary != null) {
            ((SDOChangeSummary)changeSummary).setCreated(this, created);
        }
    }

    /**
     * INTERNAL:
     * Set flag modified value.
     * @deprecated since OracleAS TopLink 11<i>1</i> (11.1.1.0) 17-May-2007.
     * Use {@link #_setModified(boolean)} instead
     * @param modified   flag modified's new value.
     */
    public void setModified(boolean modified) {
        _setModified(modified);
    }

    /**
     * INTERNAL:
     * Set flag modified value.
     * @param modified   flag modified's new value.
     */
    public void _setModified(boolean modified) {
        if (changeSummary != null) {
            ((SDOChangeSummary)changeSummary).setModified(this, modified);
            if (isLogging()) {
                updateChangeSummaryWithOriginalValues();
            }
        }
    }

    /**
     * INTERNAL:
     * Set flag deleted value.
     * @deprecated since OracleAS TopLink 11<i>1</i> (11.1.1.0) 17-May-2007.
     * @param deleted   flag deleted's new value.
     * @deprecated Use {@link #_setDeleted(boolean)} instead
     */
    public void setDeleted(boolean deleted) {
        _setDeleted(deleted);
    }

    /**
     * INTERNAL:
     * Set flag deleted value.
     * @param deleted   flag deleted's new value.
     */
    private void _setDeleted(boolean deleted) {
        // reduced scope from public to private 17 May 2007 - use the public deprecated public function until we remove it
        if (changeSummary != null) {
            ((SDOChangeSummary)changeSummary).setDeleted(this, deleted);
            if (isLogging()) {
                updateChangeSummaryWithOriginalValues();
            }
        }
    }

    /**
     * INTERNAL:
     * @param csm
     */
    private void setChangeSummaryNonRecursive(ChangeSummary csm) {
        // set changeSummary instance using property
        changeSummary = csm;
        if (getType() != null) {
            Property changeSummaryProperty = ((SDOType)getType()).getChangeSummaryProperty();
            if (changeSummaryProperty != null) {
                setChangeSummaryProperty(changeSummaryProperty, csm);
            }
        }
    }

    /**
     * INTERNAL:
     * Set this DataObject's ChangeSummary as passed in value.
     * @deprecated since OracleAS TopLink 11<i>1</i> (11.1.1.0) 17-May-2007.
     * @deprecated Use {@link #_setChangeSummary(ChangeSummary)} instead
     * @param aChangeSummary    the ChangeSummary taking this DataObject as root.
     */
    public void setChangeSummary(ChangeSummary aChangeSummary) {
        _setChangeSummary(aChangeSummary);
    }

    /**
     * INTERNAL:
     * Set this DataObject's ChangeSummary as passed in value.
     * @param aChangeSummary   the ChangeSummary taking this DataObject as root.
     */
    public void _setChangeSummary(ChangeSummary aChangeSummary) {
        Iterator iterProperties = getInstanceProperties().iterator();
        Property p;
        Object o;
        Object listContainedObject;

        // recurse currentValueStore
        while (iterProperties.hasNext()) {
            p = (Property)iterProperties.next();

            // Note: Both opposite currentValueStore cannot be isContainment=true
            // special handling for bidirectional currentValueStore, do a shallow set
            // do not recurse bidirectional currentValueStore that are non-containment
            if (p.isContainment()) {
                o = get(p);
                if (o instanceof SDODataObject) {// child of this DataObject
                    ((SDODataObject)o)._setChangeSummary(aChangeSummary);
                }

                if (o instanceof ListWrapper) {// child of this DataObject
                    Iterator anIterator = ((ListWrapper)o).iterator();
                    while (anIterator.hasNext()) {
                        listContainedObject = anIterator.next();
                        if (listContainedObject instanceof SDODataObject) {
                            ((SDODataObject)listContainedObject)._setChangeSummary(aChangeSummary);
                        }
                    }
                }
            }
        }

        setChangeSummaryNonRecursive(aChangeSummary);
    }

    /**
     * INTERNAL:
     *
     * @param property
     * @param value
     */
    private void setChangeSummaryProperty(Property property, ChangeSummary value) {
        if (property.isOpenContent()) {
            throw new IllegalArgumentException("ChangeSummary can not be on an open content property.");
        }
        if (property.isMany()) {
            throw new IllegalArgumentException("ChangeSummary can not be on a property with many set to true.");
        }

        if (isLogging()) {
            _setModified(true);
        }

        // process pluggable currentValueStore
        setPropertyInternal(property, value, true);
    }

    /**
     * INTERNAL:
     * This function reverses any operations that were performed on this object since change tracking
     * was turned on.  The object is returned to the state when logging was first started.<br/>
     * @param isCSRoot
     * @param cs
     * @param origContainer
     * @param origContainmentPropName
     */
    public void undoChanges(boolean isCSRoot, ChangeSummary cs,//
                            SDODataObject origContainer, String origContainmentPropName) {
        // dont do anything if the changeSummary is null
        if (null == cs) {
            return;
        }

        /**
         * Logic: 4 steps - we defer undo responsibilty to each of DataObject, ListWrapper and Sequence.
         * 1) swap ValueStores (for DataObject)
         * 2) swap ListWrapper (if it exists)
         * 3) swap Sequence (if it exists)
         * 4) #5928954 restore openContent properties to the 3 data structures on SDODataObject -
         *     openContentPropertiesMap<String,Property>, openContentProperties(List<Property>). instanceProperties (List<Property>)
         *
         * Use Cases:
         *   UC1: no change
         *     doDirty=false seqDirty=false
         *   UC2: change non-sequenced do
         *     doDirty=false seqDirty=false
         *   UC3: change sequenced do
         *     doDirty=true. SeqDirty=true
         *   UC4: change unstructured text on sequence only
         *     [doDirty=false seqDirty=true] - special case - verify that we do not do it twice (here and in UC3)
         *   UC5: unset(oc property), undoChanges(), get("open property")
         *
         */

        // base step: swap valueStores at the current node
        if (((SDOChangeSummary)cs).isDirty(this)) {
            // reset only if changes were done       
            if (!isCSRoot) {
                // TODO: verify we can ignore state of cs, container, contPropertyName - because no nested CSs are allowed
                // reset changeSummary
                if (null == changeSummary) {
                    // we are not recursively setting cs - so don't use the set function as child nodes will be replaced later
                    changeSummary = cs;
                }

                // reset container
                if (null == container) {
                    _setContainer(origContainer);
                }

                // reset containmentProperty name
                if (null == containmentPropertyName) {
                    _setContainmentPropertyName(origContainmentPropName);
                }
            }

            // swap valueStores   		
            _setCurrentValueStore((ValueStore)((SDOChangeSummary)cs).getOriginalValueStores().get(this));
            // return original to null (no changes)
            // TODO: we do not need to clear this map if resetChanges() is always called after undoChanges
            ((SDOChangeSummary)cs).getOriginalValueStores().remove(this);
        }

        // swap sequences (may be UC2(DataObject and sequence change) or UC4 (sequence change only)
        if (getType().isSequenced()) {// assume sequence is !null
            // perform sequence.isDirty check independent of a dataObject.isDirty check
            if (((SDOChangeSummary)cs).isDirty((SDOSequence)sequence)) {
                Sequence currentSequence = sequence;
                Sequence originalSequence = (Sequence)((SDOChangeSummary)cs).getOriginalSequences().get(this);

                // both sequences are either null or set
                if ((null == originalSequence) && (null != currentSequence)) {
                    throw SDOException.oldSequenceNotFound();
                } else {
                    sequence = originalSequence;
                }

                // TODO: we do not need to clear this map if resetChanges() is always called after undoChanges
                // reset the cache map
                ((SDOChangeSummary)cs).getOldSequences().remove(this);
                // rest the primary map
                ((SDOChangeSummary)cs).getOriginalSequences().remove(this);
            }
        }

        // restore any open content properties to the 3 data structures on SDODataObject, and remove from cs.unsetOCPropsMap
        // see openContentPropertiesMap<String,Property>, openContentProperties(List<Property>). instanceProperties (List<Property>)
        Map oldUnsetOCPropertyMap = ((SDOChangeSummary)cs).getUnsetOCPropertiesMap();

        // Iterate the list of properties by container key DataObject key (will not be null) 
        for (Iterator iterContainers = oldUnsetOCPropertyMap.keySet().iterator();
                 iterContainers.hasNext();) {
            List oldUnsetOCList = ((List)oldUnsetOCPropertyMap.get(iterContainers.next()));
            for (int i = 0, size = oldUnsetOCList.size(); i < size; i++) {
                // it is essential that the oc property is removed from the cs or we will get an unsupported op during resetChanges()
                // the property will be added only when logging=true, we reference the first item in the list as it reduces in size 
                addOpenContentProperty((Property)oldUnsetOCList.get(0));
            }
        }

        // recursive step: swap valueStores on every contained subtree
        for (Iterator iterProperties = getInstanceProperties().iterator();
                 iterProperties.hasNext();) {
            SDOProperty property = (SDOProperty)iterProperties.next();
            if (property.getType() != SDOConstants.SDO_CHANGESUMMARY) {
                Object value = get(property);
                if ((null == value) && (null != getChangeSummary())) {
                    // no changes for null properties
                } else {
                    if (property.isMany()) {
                        // check for modified ListWrapper
                        // assumption that we never have a null ListWrapper should still avoid a possible NPE
                        if (null != value) {
                            ((ListWrapper)value).undoChanges((SDOChangeSummary)getChangeSummary());
                            if (!property.getType().isDataType()) {
                                for (Iterator iterMany = ((List)value).iterator();
                                         iterMany.hasNext();) {
                                    Object valueMany = iterMany.next();
                                    if (null != valueMany) {// handle micro-corner case ListWrapper is null
                                        ((SDODataObject)valueMany).undoChanges(false, changeSummary, this, property.getName());
                                    }
                                }
                            }
                        }
                    } else {
                        if (!property.getType().isDataType() && (null != value)) {
                            ((SDODataObject)value).undoChanges(false, changeSummary, this, property.getName());
                        }
                    }
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Process ValueStore changes on any set/delete/detach/unset
     * when logging is on.
     */
    private void updateChangeSummaryWithOriginalValues() {

        /**
         * ValueStore processing:
         * We switch the current VS to original VS.
         * The current VS will be a shallow copy of the original for this dataobject at its first change
         * If the VS is already copied don't reswap them again
         *
         * Case: Move/Reset
         *  a detach of a child modifies its parent - trigering a copy of parent's ValueStore
         *  a set on the parent will trigger another copy - but we will skip this one as we already
         *  have a copy of the original.
         *  Issue: if we reset the child back the same place (in effect doing our own undo)
         *  do we optimize this and remove the copy - for now a real undoChanges() will do the same.
         *
         */

        // dont store an original sequence if there are already is one in the map
        if (isLogging() && (!((SDOChangeSummary)getChangeSummary()).isDirty(this))) {
            // dont copy containers of many props
            // TODO: Do not shallowcopy child nodes
            // original valuestore becomes current one (from null)                              
            ((SDOChangeSummary)getChangeSummary()).getOriginalValueStores().put(this, _getCurrentValueStore());
            // we make a shallow copy of the current valuestore arrays at this level in the tree        
            currentValueStore = _getCurrentValueStore().copy();

            // handle Sequences only in UC2 where we have modified the container object - not when only the sequence is dirty
            if (getType().isSequenced()) {
                // deep copy the list of settings so a modification of the current sequence will not affect a setting in the originalSequences map
                SDOSequence copySequence = ((SDOSequence)getSequence()).copy();

                // store original sequence on ChangeSummary 
                ((SDOChangeSummary)getChangeSummary()).getOriginalSequences().put(this, copySequence);
            }
        }
    }

    /**
     * INTERNAL:
     * Initialize all old settings related to ChangeSummary and recursively go down
     * the tree to initialize all DataObjects rooted at this DataObject.
     * TODO: FIX
     */
    public void resetChanges() {
        // fill all oldSettings when logging is turned on
        if ((container != null) && (containmentPropertyName != null) && (changeSummary != null)) {
            ((SDOChangeSummary)changeSummary).setOldContainer(this, container);
            ((SDOChangeSummary)changeSummary).setOldContainmentProperty(this,// 
                                                                        container.getInstanceProperty(containmentPropertyName));
        }

        // Note: valueStores will not be switched and originalValueStore will continue to be null until the first modification
        // initialize empty list for current dataObject        
        for (Iterator iterProperties = getInstanceProperties().iterator();
                 iterProperties.hasNext();) {
            SDOProperty property = (SDOProperty)iterProperties.next();
            Object value = get(property);

            // #5878436 12-FEB-07 do not recurse into a non-containment relationship
            if (property.isContainment() && !property.isMany() && (value != null) && (property.getType() != SDOConstants.SDO_CHANGESUMMARY)) {
                // TODO: Do not shallowcopy child nodes
                ((SDODataObject)value).resetChanges();
            } else {
                // Handle isMany objects and store the oldCont* values for each item in the list            
                if (property.isMany()) {
                    if (null != value) {// secondary NPE check
                        for (Iterator iterMany = ((ListWrapper)value).iterator();
                                 iterMany.hasNext();) {
                            Object valueMany = iterMany.next();

                            // do not recurse into a non-containment relationship
                            if (property.isContainment() && (valueMany != null) && (property.getType() != SDOConstants.SDO_CHANGESUMMARY)) {
                                // TODO: Do not shallowcopy child nodes
                                ((SDODataObject)valueMany).resetChanges();
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * INTERNAL:
     * @deprecated since OracleAS TopLink 11<i>1</i> (11.1.1.0) 17-May-2007.
     * Use {@link #_setType(Type)} instead
     * @param aType
     */
    public void setType(Type aType) {
        _setType(aType);
    }

    /**
     * INTERNAL:
     * @param aType
     */
    public void _setType(Type aType) {
        type = aType;

        if (getInstanceProperties().isEmpty()) {
            if (type != null) {
                getInstanceProperties().addAll(type.getProperties());
            }
            getInstanceProperties().addAll(_getOpenContentProperties());
        }

        if (type != null) {
            _getCurrentValueStore().initialize(this);
            // sequence is not initialized unless type.isSequenced=true, null otherwise
            if (type.isSequenced()) {
                sequence = new SDOSequence(this);
            }

            Property csmProperty = ((SDOType)type).getChangeSummaryProperty();
            if (csmProperty != null) {
                ChangeSummary aChangeSummary = new SDOChangeSummary(this, aHelperContext);
                aChangeSummary.endLogging();
                _setChangeSummary(aChangeSummary);
            }
        }
    }

    /**
     * INTERNAL:
     * Pluggable Interface for substituting the default Map with a custom Map Class
     * @deprecated since OracleAS TopLink 11<i>1</i> (11.1.1.0) 17-May-2007.
     * @param currentValueStore
     * void
     * @deprecated Use {@link #_setCurrentValueStore(ValueStore)} instead
     */
    public void setCurrentValueStore(ValueStore aValueStore) {
        _setCurrentValueStore(aValueStore);
    }

    /**
     * INTERNAL:
     * Pluggable Interface for substituting the default Map with a custom Map Class
     * @param currentValueStore
     * void
     */
    public void _setCurrentValueStore(ValueStore aValueStore) {
        currentValueStore = aValueStore;

        // TODO: verify that we are not resetting the originalValueStore (only if logging is on)
        //originalValueStore = currentValueStore;
    }

    /**
     * INTERNAL:
     * Map interface into the currentValueStore of this DataObject
     * @deprecated since OracleAS TopLink 11<i>1</i> (11.1.1.0) 17-May-2007.
     * Use {@link #_getCurrentValueStore()} instead
     * @return
     */
    public ValueStore getCurrentValueStore() {
        return _getCurrentValueStore();
    }

    /**
     * INTERNAL:
     * Map interface into the currentValueStore of this DataObject.<br>
     * Note: Implementers of the {@link ValueStore ValueStore} interface require this accessor.
     * @return
     */
    public ValueStore _getCurrentValueStore() {
        // Note: do not remove this function as it is the accessor into the pluggable ValueStore
        // Notification of any change to this API is required
        return currentValueStore;
    }

    /**
     * INTERNAL:
     * Return the current ValueStore
     * @deprecated since OracleAS TopLink 11<i>1</i> (11.1.1.0) 03-Feb-2007.
     * Use {@link #_setValueStore(ValueStore)} instead
      */
    public void setValueStore(ValueStore anOriginalValueStore) {
        _setValueStore(anOriginalValueStore);
    }

    /**
     * INTERNAL:
     * Return the current ValueStore
     * @deprecated since OracleAS TopLink 11<i>1</i> (11.1.1.0) 17-May-2007.
     * This method is replaced by the the new public setCurrentValueStore.
      */
    public void _setValueStore(ValueStore anOriginalValueStore) {
        _setCurrentValueStore(anOriginalValueStore);
    }

    /**
     * INTERNAL:
     * @deprecated since OracleAS TopLink 11<i>1</i> (11.1.1.0) 03-Feb-2007.
     * Use {@link #_getValueStore()} instead
     */
    public ValueStore getValueStore() {
        return _getValueStore();
    }

    /**
     * INTERNAL:
     * @deprecated since OracleAS TopLink 11<i>1</i> (11.1.1.0) 17-May-2007.
     * This method is replaced by the new public getCurrentValueStore.
     */
    public ValueStore _getValueStore() {
        return _getCurrentValueStore();
    }

    /**
     * INTERNAL:
     * Sets the DataObject which contains this DataObject.
     * @deprecated since OracleAS TopLink 11<i>1</i> (11.1.1.0) 17-May-2007.
     * Use {@link #_setContainer(DataObject)} instead
     * @param aContainer the DataObject which is the container of this DataObject.
     */
    public void setContainer(DataObject aContainer) {
        _setContainer(aContainer);
    }

    /**
     * INTERNAL:
     * Sets the DataObject which contains this DataObject.
     * @param aContainer the DataObject which is the container of this DataObject.
     */
    public void _setContainer(DataObject aContainer) {
        container = aContainer;
    }

    public Map _getOpenContentPropertiesAttributesMap() {
        Map openContentPropertiesAttrs = new HashMap();
        for (int i = 0, size = _getOpenContentPropertiesAttributes().size(); i < size; i++) {
            Property next = (Property)_getOpenContentPropertiesAttributes().get(i);
            Object value = get(next);
            String uri = null;

            //null means attributes won't be prefix qualified
            QName qname = new QName(uri, next.getName());
            openContentPropertiesAttrs.put(qname, value);
        }

        return openContentPropertiesAttrs;

    }

    public void _setOpenContentPropertiesAttributesMap(Map openAttributeProperties) {
        Iterator iter = openAttributeProperties.keySet().iterator();

        while (iter.hasNext()) {
            QName nextKey = (QName)iter.next();

            if (!nextKey.getNamespaceURI().equals(XMLConstants.XMLNS_URL)) {
                Object value = openAttributeProperties.get(nextKey);
                set(nextKey.getLocalPart(), value);
            }
        }
    }

    /**
     * INTERNAL:
     * This function is implemented by SDOType.setOpen() in a mapping setGet/SetMethodName call
     * @deprecated since OracleAS TopLink 11<i>1</i> (11.1.1.0) 17-May-2007.
     * @param openContentPropertiesWithXMLRoots
     * @deprecated Use {@link #_setOpenContentPropertiesWithXMLRoots(List)} instead
     */
    public void setOpenContentPropertiesWithXMLRoots(List openContentPropertiesWithXMLRoots) {
        _setOpenContentPropertiesWithXMLRoots(openContentPropertiesWithXMLRoots);
    }

    /**
     * INTERNAL:
     * This function is implemented by SDOType.setOpen() in a mapping setGet/SetMethodName call
     * @param openContentPropertiesWithXMLRoots
     */
    public void _setOpenContentPropertiesWithXMLRoots(List openContentPropertiesWithXMLRoots) {
        for (int i = 0, size = openContentPropertiesWithXMLRoots.size(); i < size; i++) {
            Object next = openContentPropertiesWithXMLRoots.get(i);
            String propertyName = null;
            String propertyUri = null;
            Object value = null;
            if (next instanceof XMLRoot) {
                XMLRoot nextXMLRoot = (XMLRoot)next;
                value = nextXMLRoot.getObject();
                propertyName = nextXMLRoot.getLocalName();
                propertyUri = nextXMLRoot.getNamespaceURI();
            } else if (next instanceof DataObject) {
                value = next;
                String qualifiedName = ((SDOType)((DataObject)next).getType()).getXmlDescriptor().getDefaultRootElement();
                int colonIndex = qualifiedName.indexOf(":");
                if (colonIndex > -1) {
                    String prefix = qualifiedName.substring(0, colonIndex);
                    if ((prefix != null) && !prefix.equals("")) {
                        propertyUri = ((SDOType)((DataObject)next).getType()).getXmlDescriptor().getNonNullNamespaceResolver().resolveNamespacePrefix(prefix);
                    }
                    propertyName = qualifiedName.substring(colonIndex + 1, qualifiedName.length());
                } else {
                    propertyName = qualifiedName;
                }
            }
            if (propertyName != null) {
                SDOProperty prop = (SDOProperty)aHelperContext.getTypeHelper().getOpenContentProperty(propertyUri, propertyName);

                if (prop != null) {
                    if (prop.isMany()) {
                        ((ListWrapper)getList(prop)).add(value, false);
                    } else {
                        set(prop, value);
                    }
                }
            }
        }
    }

    /**
     * INTERNAL:
     * This function is implemented by SDOType.setOpen() in a mapping setGet/SetMethodName call
     * @deprecated since OracleAS TopLink 11<i>1</i> (11.1.1.0) 17-May-2007.
     * @deprecated Use {@link #_getOpenContentPropertiesWithXMLRoots()} instead
     * @return
     */
    public List getOpenContentPropertiesWithXMLRoots() {
        return _getOpenContentPropertiesWithXMLRoots();
    }

    /**
     * INTERNAL:
     * This function is implemented by SDOType.setOpen() in a mapping setGet/SetMethodName call
     * @return
     */
    public List _getOpenContentPropertiesWithXMLRoots() {
        List returnList = new ArrayList();
        for (int i = 0, size = openContentProperties.size(); i < size; i++) {
            Property next = (Property)openContentProperties.get(i);

            XMLRoot root = new XMLRoot();
            String localName = ((SDOProperty)next).getXPath();
            String prefix = null;
            if (next.getType() != null) {
                if (!next.getType().isDataType()) {
                    String uri = next.getType().getURI();

                    root.setNamespaceURI(uri);
                    NamespaceResolver nr = ((SDOType)next.getType()).getXmlDescriptor().getNamespaceResolver();
                    if ((nr != null) && (uri != null)) {
                        prefix = nr.resolveNamespaceURI(uri);
                    }
                } else {
                    String uri = getType().getURI();

                    root.setNamespaceURI(uri);
                    NamespaceResolver nr = ((SDOType)getType()).getXmlDescriptor().getNamespaceResolver();
                    if ((nr != null) && (uri != null)) {
                        prefix = nr.resolveNamespaceURI(uri);
                    }
                }
            }

            root.setLocalName(localName);

            Object value = get(next);
            if (next.isMany()) {
                for (int j = 0, sizel = ((List)value).size(); j < sizel; j++) {
                    XMLRoot nextRoot = new XMLRoot();
                    nextRoot.setNamespaceURI(root.getNamespaceURI());
                    nextRoot.setLocalName(root.getLocalName());
                    Object nextItem = ((List)value).get(j);
                    if ((next.getType() != null) && (((SDOType)next.getType()).getXmlDescriptor() == null)) {
                        nextItem = XMLConversionManager.getDefaultXMLManager().convertObject(nextItem, String.class);
                    }
                    nextRoot.setObject(nextItem);
                    returnList.add(nextRoot);
                }
            } else {
                if ((next.getType() != null) && (((SDOType)next.getType()).getXmlDescriptor() == null)) {
                    value = XMLConversionManager.getDefaultXMLManager().convertObject(value, String.class);
                }
                root.setObject(value);
                returnList.add(root);
            }
        }
        return returnList;
    }

    /**
     * INTERNAL:
     * Returns a list of the Properties currently used in this DataObject which are not
     * included in getType().getProperties
     * @deprecated since OracleAS TopLink 11<i>1</i> (11.1.1.0) 17-May-2007.
     * Use {@link #_getOpenContentProperties()} instead
     * @return the List of open content Properties currently used in this DataObject.
      */
    public List getOpenContentProperties() {
        return _getOpenContentProperties();
    }

    /**
     * INTERNAL:
      * Returns a list of the Properties currently used in this DataObject which are not
      * included in getType().getProperties
      * @return the List of open content Properties currently used in this DataObject.
      */
    public List _getOpenContentProperties() {
        if (openContentProperties == null) {
            openContentProperties = new ArrayList();
        }
        return openContentProperties;
    }

    /**
     * INTERNAL:
     * @deprecated since OracleAS TopLink 11<i>1</i> (11.1.1.0) 17-May-2007.
     * Use {@link #_getOpenContentPropertiesMap()} instead
     * @return
     */
    public Map getOpenContentPropertiesMap() {
        return _getOpenContentPropertiesMap();
    }

    /**
     * INTERNAL:
     * Return the map of open content properties
     * @return
     */
    private Map _getOpenContentPropertiesMap() {
        if (null == openContentPropertiesMap) {
            openContentPropertiesMap = new HashMap();
        }
        return openContentPropertiesMap;
    }

    /**
     * INTERNAL:
     */
    private void convertValueAndSet(Property property, Object originalValue) {
        Object convertedValue = aHelperContext.getDataHelper().convert(property, originalValue);
        set(property, convertedValue);
    }

    /**
    * INTERNAL:
    */
    private void convertValueAndSet(int propertyIndex, Object originalValue) {
        Property property = getInstanceProperty(propertyIndex);

        Object convertedValue = aHelperContext.getDataHelper().convert(property, originalValue);
        set(property, convertedValue);
    }

    /**
     * INTERNAL:
     */
    private void convertValueAndSet(String path, Object originalValue) {
        XPathEngine.getInstance().set(path, originalValue, this, true);
    }

    /**
     * INTERNAL:
     *
     * @param property
     * @param cls
     * @return
     * @throws ClassCastException
     * @throws IllegalArgumentException
     */
    public Object convertObjectToValue(Property property, Class cls) throws ClassCastException, IllegalArgumentException {
        //ie get String - convert(thisprop, String.class)    
        if (null == property) {
            throw new IllegalArgumentException(SDOException.cannotPerformOperationOnNullArgument("convertObjectToValue"));
        }

        Object obj = get(property);

        try {
            return ((SDODataHelper)aHelperContext.getDataHelper()).convertValueToClass(property, obj, cls);
        } catch (Exception e) {
            throw new ClassCastException("An error occurred during converison or an unsupported conversion was attempted.");
        }
    }

    /**
     * INTERNAL:
     *
     * @param property
     * @param position
     * @param cls
     * @return
     * @throws ClassCastException
     * @throws IllegalArgumentException
     */
    public Object convertObjectToValue(Property property, int position, Class cls) throws ClassCastException, IllegalArgumentException {
        if (null == property) {
            throw new IllegalArgumentException(SDOException.cannotPerformOperationOnNullArgument("convertObjectToValue"));

        }

        if ((cls == ClassConstants.List_Class) && !property.isMany()) {
            throw new ClassCastException("can not call getList for a property that has isMany false.");
        }

        Object obj;
        if (position == -1) {
            obj = get(property);
        } else {
            obj = getList(property).get(position);
        }

        try {
            return ((SDODataHelper)aHelperContext.getDataHelper()).convertValueToClass(property, obj, cls);
        } catch (Exception e) {
            throw new ClassCastException("An error occurred during conversion or an unsupported conversion was attempted.");
        }
    }

    /**
     * INTERNAL:
     * Sets the name of the property on the containing DataObject which contains this DataObject as a value.
     * @deprecated since OracleAS TopLink 11<i>1</i> (11.1.1.0) 17-May-2007.
     * Use {@link #_setContainmentPropertyName(String)} instead
     * @param aContainmentPropertyName the name of the property on the containing DataObject which has this DataObject as a value.
     */
    public void setContainmentPropertyName(String aContainmentPropertyName) {
        _setContainmentPropertyName(aContainmentPropertyName);
    }

    /**
     * INTERNAL:
     * Sets the name of the property on the containing DataObject which contains this DataObject as a value.
     * @param aContainmentPropertyName the name of the property on the containing DataObject which has this DataObject as a value.
     */
    public void _setContainmentPropertyName(String aContainmentPropertyName) {
        containmentPropertyName = aContainmentPropertyName;
    }

    /**
     * INTERNAL:
     * Return the name of the Property of the DataObject containing this data object
     * or null if there is no container.
     * @deprecated since OracleAS TopLink 11<i>1</i> (11.1.1.0) 17-May-2007.
     * @return the property containing this data object.
     * @deprecated Use {@link #_getContainmentPropertyName()} instead
     */
    public String getContainmentPropertyName() {
        return _getContainmentPropertyName();
    }

    /**
     * INTERNAL:
     * Return the name of the Property of the DataObject containing this data object
     * or null if there is no container.     *
     * @return the property containing this data object.
     */
    private String _getContainmentPropertyName() {
        return containmentPropertyName;
    }

    /**
     * INTERNAL:
     *
     * @param value
     * @return
     */
    public boolean parentContains(Object value) {
        if ((value == null) || (!(value instanceof DataObject))) {
            return false;
        }
        if ((getContainer() != null) && (getContainmentProperty() != null)) {
            if (value.equals(getContainer())) {
                return true;
            }
            return ((SDODataObject)getContainer()).parentContains(value);
        }
        return false;
    }

    /**
     * INTERNAL:
     * Update containment with flagged update sequence state
     * @param property
     * @param values
     * @param updateSequence
     */
    public void updateContainment(Property property, Collection values, boolean updateSequence) {
        if (property.isContainment()) {
            Iterator valuesIter = values.iterator();
            while (valuesIter.hasNext()) {
                Object next = valuesIter.next();
                if (next instanceof DataObject) {
                    updateContainment(property, (DataObject)next, updateSequence);
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Update containment on the specified collection of values and default to true = update the sequence
     * @param property
     * @param values
     */
    public void updateContainment(Property property, Collection values) {
        updateContainment(property, values, true);
    }

    /**
     * INTERNAL:
     * Update containment on the dataObject with specified update sequence state
     * @param property
     * @param value
     */
    public void updateContainment(Property property, DataObject value, boolean updateSequence) {
        if (property.isContainment()) {
            boolean wasInNewCS = (getChangeSummary() != null) && (((SDODataObject)value).getChangeSummary() != null)//
             &&getChangeSummary().equals(((SDODataObject)value).getChangeSummary());

            // remove property or old changeSummary
            ((SDODataObject)value).detach(false, updateSequence);
            // set current changeSummary
            if ((getChangeSummary() != null) && (((SDODataObject)value).getType() != null)//
                     &&(((SDOType)((SDODataObject)value).getType()).getChangeSummaryProperty() == null) && (getChangeSummary() != null)) {
                ((SDODataObject)value)._setChangeSummary(getChangeSummary());
            }

            // add value as a property of (this)
            ((SDODataObject)value)._setContainer(this);
            ((SDODataObject)value)._setContainmentPropertyName(property.getName());

            // We don't setCreated for objects that were previously deleted
            if (!wasInNewCS && (getChangeSummary() != null) && !getChangeSummary().isDeleted(value)) {
                ((SDODataObject)value)._setCreated(true);
            }

            // If we are adding a previously deleted object, we must cancel out the isDeleted with an isCreated
            // so we end up with all isDeleted, isModified == false
            if ((getChangeSummary() != null) && getChangeSummary().isDeleted(value)) {
                // explicitly clear the oldSetting and clear the key:value pair in the deleted map
                ((SDODataObject)value)._setDeleted(false);

                // remove oldSetting from map only when we return to original position
                // by comparing an undo() with the projected model after set() - for now keep oldSettings                
            }

            // modify container object
            _setModified(true);
        }
    }

    /**
     * INTERNAL:
     * update containment and the update the sequence value by default.
     * @param property
     * @param value
     */
    public void updateContainment(Property property, DataObject value) {
        updateContainment(property, value, true);
    }

    /**
     * INTERNAL:
     * Return the changeSummary logging state
     * @return
     */
    private boolean isLogging() {
        return ((changeSummary != null) && changeSummary.isLogging());
    }

    /**
     * INTERNAL:
     * Defined in SDO 2.01 spec on page 65 Externalizable function is called by
     * ObjectStream.writeObject() A replacement object for serialization can be
     * called here.
     * <p/>Security Note:
     *     This public function exposes a data replacement vulnerability where an outside client
     *     can gain access and modify their non-final constants.
     *     We may need to wrap the GZIP streams in some sort of encryption when we are not
     *     using HTTPS or SSL/TLS on the wire.
     *
     * @see org.eclipse.persistence.sdo.SDOResolvable
     */
    public Object writeReplace() {
        // JIRA129: pass the helperContext to the constructor to enable non-static contexts
        return new ExternalizableDelegator(this, aHelperContext);
    }

    /**
     * INTERNAL:
     * Return the SDO Path from the root to the current internal node
     *
     * Prereq: We know that the targetObject will always have a parent as called
     * from getPath()
     *
     * Matching conditions:
     *   Iterate up the tree
     *   return a non-null string for the XPath at when we reach the root node
     *
     * Function is partially based on SDOCopyHelper.copy(DataObject dataObject)
     *
     * Performance: This function is O(log n) where n=# of children in the tree
     *
     * @param currentPath
     * @param targetObject
     * @param aSeparator (XPath separator is written only between elements not for the first call)
     * @return String (representing the XPath)
     */
    private String getPathPrivate(String currentPath,//
                                  SDODataObject targetObject,//
                                  String aSeparator) {
        // Base Case: check we are at the root (get parent first)
        SDODataObject aParent = (SDODataObject)targetObject.getContainer();
        if (aParent == null) {
            // check for indexed property if root is a ListWrapper
            return currentPath;
        }

        // Recursive Case: O(log(n)) recursive calls, 1 for each tree level
        // get parent property based on parent property name in target, property will always be set
        String aContainmentPropertyName = targetObject._getContainmentPropertyName();
        SDOProperty aChild = (SDOProperty)aParent.getInstanceProperty(aContainmentPropertyName);

        // Handle ListWrapper contained DataObjects
        if (aChild.isMany()) {
            int index = (aParent.getList(aChild)).indexOf(targetObject);

            // TODO: throw exception on index = -1 (not found)
            return getPathPrivate(aContainmentPropertyName +// 
                                  SDOConstants.SDO_XPATH_LIST_INDEX_OPEN_BRACKET +// 
                                  (1 + index) +// [indexes] start at 1
                                  SDOConstants.SDO_XPATH_LIST_INDEX_CLOSE_BRACKET +//
                                  aSeparator +//
                                  currentPath,//
                                  aParent,//
                                  SDOConstants.SDO_XPATH_SEPARATOR_FRAGMENT);
        }

        // recursive call to non ListWrappers
        return getPathPrivate(aContainmentPropertyName +//
                              aSeparator +//
                              currentPath,//
                              aParent,//
                              SDOConstants.SDO_XPATH_SEPARATOR_FRAGMENT);
    }

    /**
     * INTERNAL:
     * Return an SDO Path string from root of the caller to itself
     * @deprecated since OracleAS TopLink 11<i>1</i> (11.1.1.0) 17-May-2007.
     * Use {@link #_getPath()} instead
     * @return String
     */
    public String getPath() {
        return _getPath();
    }

    /**
     * INTERNAL:
     * Return an SDO Path string from root of the caller to itself
     * @return String
     */
    public String _getPath() {
        // Base Case: The internal node is actually the root
        if (getContainer() == null) {
            return SDOConstants.SDO_XPATH_TO_ROOT;
        } else {
            // Recursive Case: call private recursive reverse O(logn) traversal
            // function on root object
            return getPathPrivate(SDOConstants.EMPTY_STRING,//
                                  this,// 
                                  SDOConstants.EMPTY_STRING);
        }
    }

    /**
     * INTERNAL:
     * Return the XPath or SDO path from the anObject to the current internal node
     *
     * Prereq: We know that the targetObject will always have a parent as called
     * from getPath()
     *   We require a ChangeSummary object for when there are deleted
     *   objects in the path
     *
     * Matching conditions:
     *   Iterate up the tree
     *   return a non-null string for the XPath when we reach the target node
     *
     * Function is partially based on SDOCopyHelper.copy(DataObject dataObject)
     * Performance: This function is O(log n) where n=# of children in the tree
     *
     * @param currentPath
     * @param targetObject
     * @param currentObject
     * @param aSeparator (XPath separator is written only between elements - not for the first call)
     * @param useXPathFormat - true for XPath format, false for SDO path format
     * @return String (representing the XPath)
     */
    private String getPathFromAncestorPrivate(SDOChangeSummary aChangeSummary,//
                                              String currentPath,//
                                              SDODataObject targetDO,//
                                              SDODataObject currentObject,//
                                              String aSeparator,//
                                              String lastContainmentProperty,//
                                              boolean useXPathFormat) {
        SDOChangeSummary currentChangeSummary = null;

        // Base Case: check we are at the target object first
        if (currentObject == targetDO) {
            // check for indexed property if root is a ListWrapper
            return currentPath;
        }

        // Recursive Case: O(log(n)) recursive calls, 1 for each tree level
        // get parent property based on parent property name in target, property will always be set
        // check containment for cases where we are searching for a sibling
        String parentContainmentPropertyName = null;
        String parentContainmentPropertyString = null;
        String parentContainmentPropertyXPath = null;
        Object parent = null;

        // for already deleted dataobjects  - isDeleted=false, changeSummary= null - use oldContainer
        if (null == currentObject.getContainer()) {
            // check changeSummary
            if ((SDOChangeSummary)getChangeSummary() != null) {
                // use this changeSummary
                currentChangeSummary = (SDOChangeSummary)getChangeSummary();
                // object deleted had a changeSummary
                parent = currentChangeSummary.getOldContainer(currentObject);
            } else {
                // object deleted was a child of an object that had a changeSummary
                // TODO: get oldContainer from target's changeSummary
                if (null == aChangeSummary) {
                    if ((SDOChangeSummary)targetDO.getChangeSummary() != null) {
                        // use this changeSummary
                        currentChangeSummary = (SDOChangeSummary)targetDO.getChangeSummary();
                        parent = currentChangeSummary.getOldContainer(this);
                        // TODO: if aChangeSummary==null object is deleted and changeSummary is off (none passed or on targetDO)
                    }
                } else {
                    // TODO: Verify object is a isMany deleted object
                    parent = aChangeSummary.getOldContainer(currentObject);
                    // use this changeSummary
                    currentChangeSummary = aChangeSummary;
                }
            }

            // handle (at root) case for non-deleted objects for the cases
            // case: ancestor not found
            // case: ancestor is actually sibling
            if (null == parent) {
                return SDOConstants.SDO_XPATH_INVALID_PATH;
            } else {
                // The changeSummary should always be null for any deleted/detached/unset object except the root which will not enter here
                // object deleted was a child of an object that had a changeSummary
                // TODO: get oldContainmentProperty from target's changeSummary
                if (null == aChangeSummary) {
                    if ((SDOChangeSummary)targetDO.getChangeSummary() != null) {
                        Property parentPropertyOfDeletedObject = ((SDOChangeSummary)targetDO.getChangeSummary())//
                        .getOldContainmentProperty(this);
                        parentContainmentPropertyName = parentPropertyOfDeletedObject.getName();
                        // get XPath using SDO path - block
                        parentContainmentPropertyXPath = ((SDOProperty)parentPropertyOfDeletedObject)//
                            .getXmlMapping().getField().getName();
                    }
                } else {
                    // use changeSummary parameter to get old parent and search for copy in oldSettings
                    if (aChangeSummary.getOldContainmentProperty(currentObject) != null) {
                        Property parentPropertyOfDeletedObject = aChangeSummary.getOldContainmentProperty(currentObject);
                        parentContainmentPropertyName = parentPropertyOfDeletedObject.getName();
                        // get XPath using SDO path - block
                        parentContainmentPropertyXPath = ((SDOProperty)parentPropertyOfDeletedObject)//
                            .getXmlMapping().getField().getName();
                    } else {
                        // TODO : Code coverage test case required
                        // get using parent
                        Property parentPropertyOfDeletedObject = aChangeSummary.getOldContainmentProperty(//
                        (SDODataObject)parent);
                        parentContainmentPropertyName = parentPropertyOfDeletedObject.getName();
                        // get XPath using SDO path - block
                        parentContainmentPropertyXPath = ((SDOProperty)parentPropertyOfDeletedObject)//
                            .getXmlMapping().getField().getName();
                    }
                }
            }
        } else {
            // normal non-deleted non-changeSummary case
            parent = currentObject.getContainer();
            // use XPath or SDO path
            parentContainmentPropertyName = currentObject._getContainmentPropertyName();
            // get XPath using SDO path - block
            if (currentObject.getContainer().getInstanceProperty(parentContainmentPropertyName) != null) {
                parentContainmentPropertyXPath = ((SDOProperty)currentObject.getContainer()//
                    .getInstanceProperty(parentContainmentPropertyName))//
                    .getXmlMapping().getField().getName();
            } else {
                // TODO: verify we are not 
                //   parentContainmentPropertyXPath = EMPTY_STRING;
            }
        }

        // parentContainmentPropertyName will not be null - it may be EMPTY_STRING
        // get the current node as referenced by its parent
        SDOProperty aChild = (SDOProperty)((SDODataObject)parent).getInstanceProperty(parentContainmentPropertyName);

        // use XPath or SDO path in recursive calls
        if (useXPathFormat) {
            parentContainmentPropertyString = parentContainmentPropertyXPath;
        } else {
            parentContainmentPropertyString = parentContainmentPropertyName;
        }

        // Handle ListWrapper contained DataObjects
        if (aChild.isMany()) {
            int index = (((SDODataObject)parent).getList(aChild)).indexOf(currentObject);

            // TODO: throw exception on index = -1 (not found)
            if (index < 0) {

                /*
                * The current object has been deleted and was part of a ListWrapper (isMany=true)
                * Get the parent of this indexed list item and check the oldSetting (List) for the
                * original position of the indexed (Deleted) object
                */
                SDOChangeSummary aParentChangeSummary = (SDOChangeSummary)((SDODataObject)parent).getChangeSummary();
                if (null == aParentChangeSummary) {
                    // use the current objects changesummary if available
                    aParentChangeSummary = (SDOChangeSummary)getChangeSummary();
                }
                if (aParentChangeSummary == null) {
                    // use passed in change summary
                    aParentChangeSummary = aChangeSummary;
                }
                if (aParentChangeSummary == null) {
                    // use passed in change summary
                    aParentChangeSummary = currentChangeSummary;
                }
                if (aParentChangeSummary != null) {
                    // get the list containing the old value of the item
                    Setting anOldSetting = aParentChangeSummary.getOldValue((DataObject)parent, aChild);
                    if (anOldSetting != null) {
                        // get index directly from oldSettings based on current object - where parent was not deleted
                        List aDeletedParent = (List)anOldSetting.getValue();

                        // bug# 5587042: we will assume that index is never < 0 and remove handling code for this case where we lookup anOldSetting directly instead of via the deepCopies map
                        index = aDeletedParent.indexOf(aParentChangeSummary.getDeepCopies().get(currentObject));
                    } else {
                        // bug# 5587042: we will assume that oldSetting is never null and remove handling code for this case where we would hardcode to list.size()
                    }
                } else {
                    // get out of infinite loop
                }

                // see: testGetXPathFromAncestorDeletedFromChildToAncestorInsideListWrapperLoggingOn
            }

            // recursive call to list wrappers
            return getPathFromAncestorPrivate(currentChangeSummary,//
                                              parentContainmentPropertyString +// 
                                              SDOConstants.SDO_XPATH_LIST_INDEX_OPEN_BRACKET +// 
                                              (1 + index) +// [indexes] start at 1
                                              SDOConstants.SDO_XPATH_LIST_INDEX_CLOSE_BRACKET +//
                                              aSeparator +//
                                              currentPath,//
                                              targetDO,//
                                              (SDODataObject)parent,//
                                              SDOConstants.SDO_XPATH_SEPARATOR_FRAGMENT,// we pass an empty separator so we have \a\b and not a\b\
                                              parentContainmentPropertyString,//
                                              useXPathFormat);
        }

        // recursive call to non ListWrappers
        return getPathFromAncestorPrivate(currentChangeSummary,//        		
                                          parentContainmentPropertyString +//
                                          aSeparator +//
                                          currentPath,//
                                          targetDO,//
                                          (SDODataObject)parent,//
                                          SDOConstants.SDO_XPATH_SEPARATOR_FRAGMENT,// we pass an empty separator so we have \a\b and not a\b\
                                          parentContainmentPropertyString,//
                                          useXPathFormat);
    }

    /**
     * INTERNAL:
     * Return an SDO Path string from anObject to itself
     * Constraints:<br>
     *   The anObject is an ancestor of the object and not a sibling<br>
     *   The changeSummary can be used as passed in or from the targetDO for deleted objects
     * @param aChangeSummary - parent changeSummary (for deleted object)
     * @param targetDO - internal or root target data object
     * @param useXPathFormat - true or false for SDO path format
     * @return String
     */
    public String getPathFromAncestor(SDOChangeSummary aChangeSummary,//
                                      SDODataObject targetDO, boolean useXPathFormat) {

        /*
         * Algorithm:
         *   (1) Intact (non-deleted) objects:
         *     - recursively iterate up the container of each DataObject, recording property names as we go
         *   (2) Deleted objects:
         *     - use the changeSummary to get the deleted object with oldContainer state
         *     - recursively iterate up the oldContainer as in (1)
         *    Issues:
         *     - a deleted indexed object inside a ListWrapper will not retain its original index
         */

        // Base Case: The internal node is actually the root
        // Base Case: The source and target objects are equal
        // checking if this and target are equal will handle both cases above
        if (this == targetDO) {
            // return "" empty string and handle at implementor
            return SDOConstants.EMPTY_STRING;
        } else {
            // Recursive Case: call private recursive reverse O(logn) traversal
            // function on current object
            return getPathFromAncestorPrivate(aChangeSummary,//
                                              SDOConstants.EMPTY_STRING,//
                                              targetDO,//
                                              this,//
                                              SDOConstants.EMPTY_STRING,// we pass an empty separator so we have \a\b and not a\b\
                                              SDOConstants.EMPTY_STRING,//
                                              useXPathFormat);
        }
    }

    /**
     * INTERNAL:
     * Get path for non-deleted DataObjects<br>
     * ChangeSummary is not required and is set to null.<br>
     * Assumptions:<br>
     *     target node is an ancestor of the source (this)
     * @param targetDO
     * @param useXPathFormat - true for XPath format, false for SDO path format
     * @return
     * String
     */
    public String getPathFromAncestor(SDODataObject targetDO, boolean useXPathFormat) {
        // default to no changeSummary
        return getPathFromAncestor(null, targetDO, useXPathFormat);
    }

    /**
     * INTERNAL:
     * Get path for non-deleted DataObjects<br>
     * ChangeSummary is not required and is set to null.<br>
     * Assumptions:<br>
     *     target node is an ancestor of the source (this)
     * @param targetDO
     * useXPathFormat - true for XPath format, false for SDO path format
     * @return
     * String
     */
    public String getPathFromAncestor(SDODataObject targetDO) {
        // Implementors: SDOMarshalListener
        // default to no changeSummary and xpath format
        return getPathFromAncestor(null, targetDO, true);
    }

    /**
     * INTERNAL:
     * @deprecated since OracleAS TopLink 11<i>1</i> (11.1.1.0) 17-May-2007.
     * Use {@link #_getPathFromRoot()} instead
     * @return
     */
    public String getPathFromRoot() {
        return _getPathFromRoot();
    }

    /**
     * INTERNAL:
     * @deprecated since OracleAS TopLink 11<i>1</i> (11.1.1.0) 17-May-2007.
     * @return
     */
    public String _getPathFromRoot() {
        // check if we are already at the root - return path to self
        StringBuffer aBuffer = new StringBuffer(SDOConstants.SDO_XPATH_TO_ROOT);
        if (null != getContainer()) {
            aBuffer.append(getPathFromAncestor((SDODataObject)getRootObject(), true));
        }
        return aBuffer.toString();
    }

    /**
     * INTERNAL:
     *
     * @return
     */
    private Property[] getInstancePropertiesArray() {
        if ((openContentProperties == null) || openContentProperties.isEmpty()) {
            return ((SDOType)getType()).getPropertiesArray();
        }

        Property[] props = ((SDOType)getType()).getPropertiesArray();
        Property[] ret = new Property[openContentProperties.size() + props.length];
        for (int i = 0; i < props.length; i++) {
            ret[i] = props[i];
        }
        for (int i = props.length; i < ret.length; i++) {
            ret[i] = (Property)openContentProperties.get(i - props.length);
        }
        return ret;
    }

    /**
     * INTERNAL:
     * Get the value of the property (open-content or declared)..
     * @param property
     * @return
     */
    public Object getPropertyInternal(Property property) {
        if (property.isOpenContent()) {
            return _getCurrentValueStore().getOpenContentProperty(property.getName());
        } else {
            return _getCurrentValueStore().getDeclaredProperty(((SDOProperty)property).getIndexInType());
        }
    }

    /**
     * INTERNAL:
     * Update the ValueStore with the new property value and update any sequence if it exists.
     * @param property
     * @param value
     * @param updateSequence (truncate call back from sequence when this function was called from sequence)
     */
    public void setPropertyInternal(Property property, Object value, boolean updateSequence) {
        if (property.isOpenContent()) {
            _getCurrentValueStore().setOpenContentProperty(property.getName(), value);
        } else {
            _getCurrentValueStore().setDeclaredProperty(((SDOProperty)property).getIndexInType(), value);
        }

        /*
         * Update sequence object if element and without invoking a back pointer from sequence.add() to this dataObject.
         * The sequence != null required in case where non-public type.setSequenced(true) called after type define.
         * Assume: aHelperContext is assumed not to be null.
         * ChangeSummaries are not part of sequences.
         *
         * See JIRA-242, bug#6031657: A modify operation will need logic to either
         * 1 - update the existing setting in place
         * 2 - add a new setting and remove the previous one - TBD
         *
         * The following code needs to check for !attribute instead of !element because
         * we have 3 states( !attribute, !element and (!attribute && !element))
         * - two of which are valid for sequence setting creation.
         */
        if (type.isSequenced() && (sequence != null) && updateSequence//
                 &&(property.getType() != SDOConstants.SDO_CHANGESUMMARY) && !aHelperContext.getXSDHelper().isAttribute(property)) {
            // As we do for ListWrappers and DataObjects we will need to remove the previous setting if this set is actually a modify
            // keep sequence code here for backdoor sets           
            ((SDOSequence)sequence).addWithoutUpdate(property, value);
        }
    }

    /**
     * INTERNAL:
     * Add the open content property into all 3 data structures.
     * Remove the property from the unset map.
     * @param property
     */
    public void addOpenContentProperty(Property property) {
        Property prop = (Property)_getOpenContentPropertiesMap().get(property.getName());
        if (prop == null) {
            // remove from unset map if previously unset
            if (isLogging()) {
                ((SDOChangeSummary)getChangeSummary()).removeUnsetOCProperty(this, property);
            }

            _getOpenContentPropertiesMap().put(property.getName(), property);
            if (aHelperContext.getXSDHelper().isAttribute(property)) {
                _getOpenContentPropertiesAttributes().add(property);
            } else {
                _getOpenContentProperties().add(property);
            }

            getInstanceProperties().add(property);
            for (int i = 0, size = property.getAliasNames().size(); i < size; i++) {
                _getOpenContentPropertiesMap().put(property.getAliasNames().get(i), property);
            }
        }
    }

    /**
     * INTERNAL:
     * Remove the open content property (property) from all 3 data structures.
     * Add the property to the unset map.
     * We restore this OC property in undoChanges
     * @param property
     */
    public void removeOpenContentProperty(Property property) {
        // save OC property in changeSummary for use during cs.copy and undoChanges
        if (isLogging()) {
            ((SDOChangeSummary)getChangeSummary()).setUnsetOCProperty(this, property);
        }

        // remove oc property
        _getOpenContentPropertiesMap().remove(property.getName());
        _getOpenContentProperties().remove(property);
        _getOpenContentPropertiesAttributes().remove(property);
        getInstanceProperties().remove(property);
        for (int i = 0, size = property.getAliasNames().size(); i < size; i++) {
            _getOpenContentPropertiesMap().remove(property.getAliasNames().get(i));
        }
    }

    /**
     * INTERNAL:
     * Return whether the property (open-content or declared) is set?
     * @param property
     * @return true if set, false otherwise
     */
    public boolean isSetInternal(Property property) {
        if (property.isOpenContent()) {
            return _getCurrentValueStore().isSetOpenContentProperty(property.getName());
        } else {
            return _getCurrentValueStore().isSetDeclaredProperty(((SDOProperty)property).getIndexInType());
        }
    }

    /**
     * INTERNAL:
     * Unset the property on the ValueStore interface and update the sequence if it exists.
     * Used by implementers that require
     * @param property
     * @param updateSequence
     */
    private void unsetInternal(Property property, boolean updateSequence) {
        if (property.isMany()) {
            getList(property).clear();
            if (property.isOpenContent()) {
                removeOpenContentProperty(property);
            } else {
                _getCurrentValueStore().unsetDeclaredProperty(((SDOProperty)property).getIndexInType());
            }
        } else {
            if (property.isOpenContent()) {
                _getCurrentValueStore().unsetOpenContentProperty(property.getName());
                removeOpenContentProperty(property);
            } else {
                _getCurrentValueStore().unsetDeclaredProperty(((SDOProperty)property).getIndexInType());
            }
        }

        /*
         * Update sequence object if element and without invoking a back pointer from sequence.add() to this dataObject.
         * The sequence != null required in case where non-public type.setSequenced(true) called after type define
         */
        if (type.isSequenced() && (sequence != null) && updateSequence && aHelperContext.getXSDHelper().isElement(property)) {
            // remove all instances of the property from the sequence
            ((SDOSequence)sequence).remove(property.getName(), property.getType().getURI(), false);
        }
    }

    /**
     * INTERNAL:
     * @deprecated since OracleAS TopLink 11<i>1</i> (11.1.1.0) 17-May-2007.
     * @deprecated Use {@link #_getSdoRef()} instead
     * @return
     */
    public String getSdoRef() {
        return _getSdoRef();
    }

    /**
     * INTERNAL:
     * Return the sdoref attribute value during unmarshaling
     * @return
     */
    public String _getSdoRef() {
        return sdoRef;
    }

    public void _setOpenContentPropertiesAttributes(List openContentPropertiesAttributes) {
        this.openContentPropertiesAttributes = openContentPropertiesAttributes;
    }

    public List _getOpenContentPropertiesAttributes() {
        if (openContentPropertiesAttributes == null) {
            openContentPropertiesAttributes = new ArrayList();
        }
        return openContentPropertiesAttributes;
    }
}