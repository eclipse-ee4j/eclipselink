/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.sdo;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataGraph;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;
import commonj.sdo.helper.DataHelper;
import commonj.sdo.helper.HelperContext;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.helper.ListWrapper;
import org.eclipse.persistence.sdo.helper.SDODataHelper;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.sdo.helper.XPathEngine;
import org.eclipse.persistence.exceptions.SDOException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.sequenced.SequencedObject;
import org.eclipse.persistence.oxm.sequenced.Setting;

public class SDODataObject implements DataObject, SequencedObject {

    /**
     * Development Guidelines:
     * (1) All no-argument get and single argument set functions that are outside the DataObject interface
     * must be proceeded with a _ underscore in the form _getValue()/_setValue(value).
     * The reason for this is to avoid naming collisions with generated classes that extend SDODataObject
     * where the generated get/set methods for POJO fields would collide with the same function here.
     */
    /** The Type that this DataObject represents */
    private SDOType type;

    // t20070714_bc4j: Add pluggable DO support for BC4J using currentValueStore
    private SDODataObject container;

    /**
     * The (currentValueStore) will maintain the current state of our model
     *   after logged changes - it is a shallow copy of the original, progressively becoming deeper with changes.
     */
    private ValueStore currentValueStore;
    private List openContentProperties;
    private List openContentPropertiesAttributes;
    private Map openContentAliasNames;
    private String containmentPropertyName;
    private SDOChangeSummary changeSummary;
    private List<SDOProperty> instanceProperties;
    private String sdoRef;
    private SDOSequence sequence;
    private DataGraph dataGraph;

    /** hold the current context containing all helpers so that we can preserve inter-helper relationships */
    private HelperContext aHelperContext;

    /**Unique hash ID of this Externalizable class - not required at this point because we serialize the xml representation */

    @SuppressWarnings("unused")
    private String text;

    //static final long serialVersionUID = 5930094058760264198L;

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
        _setCurrentValueStore(new DefaultValueStore());
    }

    /**
     * INTERNAL:
     * Set the HelperContext that will be associated with this DataObject.
     * @param aContext
     */
    public void _setHelperContext(HelperContext aContext) {
        aHelperContext = aContext;
        // reset the HelperContext on ChangeSummary
        if (changeSummary != null) {
            changeSummary.setHelperContext(aHelperContext);
        }
    }

    public Object get(String path) {// path like "a/b/c"
        try {
            return XPathEngine.getInstance().get(path, this);
        } catch (Exception e) {
            // Swallow exception and return null, as per SDO 2.1 spec
            return null;
        }
    }

    public void set(String path, Object value) throws ClassCastException, UnsupportedOperationException, IllegalArgumentException {
        XPathEngine.getInstance().set(path, value, this, false);
    }

    public boolean isSet(String path) {
        return XPathEngine.getInstance().isSet(path, this);
    }

    public void unset(String path) {
        XPathEngine.getInstance().unset(path, this);
    }

    public boolean getBoolean(String path) throws ClassCastException {
        try {
            Boolean value = (Boolean)XPathEngine.getInstance().convertObjectToValueByPath(path, Boolean.class, this);

            if (value == null) {
                return false;
            }
            return value.booleanValue();
        } catch (Exception e) {
            // Swallow exception and return null, as per SDO 2.1 spec
            return false;
        }
    }

    public byte getByte(String path) {
        try {
            Byte value = (Byte)XPathEngine.getInstance().convertObjectToValueByPath(path, Byte.class, this);
            if (value == null) {
                return 0;
            }
            return value.byteValue();
        } catch (Exception e) {
            // Swallow exception and return null, as per SDO 2.1 spec
            return 0;
        }
    }

    public char getChar(String path) {
        try {
            Character value = (Character)XPathEngine.getInstance().convertObjectToValueByPath(path, Character.class, this);
            if (value == null) {
                return '\u0000';
            }
            return value.charValue();
        } catch (Exception e) {
            // Swallow exception and return null, as per SDO 2.1 spec
            return '\u0000';
        }
    }

    public double getDouble(String path) {
        try {
            Double value = (Double)XPathEngine.getInstance().convertObjectToValueByPath(path, Double.class, this);
            if (value == null) {
                return 0.0d;
            }
            return value.doubleValue();
        } catch (Exception e) {
            // Swallow exception and return null, as per SDO 2.1 spec
            return 0.0d;
        }
    }

    public float getFloat(String path) {
        try {
            Float value = (Float)XPathEngine.getInstance().convertObjectToValueByPath(path, Float.class, this);
            if (value == null) {
                return 0.0f;
            }
            return value.floatValue();
        } catch (Exception e) {
            // Swallow exception and return null, as per SDO 2.1 spec
            return 0.0f;
        }
    }

    public int getInt(String path) {
        try {
            Integer value = (Integer)XPathEngine.getInstance().convertObjectToValueByPath(path, Integer.class, this);
            if (value == null) {
                return 0;
            }
            return value.intValue();
        } catch (Exception e) {
            // Swallow exception and return null, as per SDO 2.1 spec
            return 0;
        }
    }

    public long getLong(String path) {
        try {
            Long value = (Long)XPathEngine.getInstance().convertObjectToValueByPath(path, Long.class, this);
            if (value == null) {
                return 0L;
            }
            return value.longValue();
        } catch (Exception e) {
            // Swallow exception and return null, as per SDO 2.1 spec
            return 0L;
        }
    }

    public short getShort(String path) {
        try {
            Short value = (Short)XPathEngine.getInstance().convertObjectToValueByPath(path, Short.class, this);
            if (value == null) {
                return 0;
            }
            return value.shortValue();
        } catch (Exception e) {
            // Swallow exception and return null, as per SDO 2.1 spec
            return 0;
        }
    }

    public byte[] getBytes(String path) {
        try {
            byte[] value = (byte[])XPathEngine.getInstance().convertObjectToValueByPath(path, byte[].class, this);
            return value;
        } catch (Exception e) {
            // Swallow exception and return null, as per SDO 2.1 spec
            return null;
        }
    }

    public BigDecimal getBigDecimal(String path) {
        try {
            BigDecimal value = (BigDecimal)XPathEngine.getInstance().convertObjectToValueByPath(path, BigDecimal.class, this);
            return value;
        } catch (Exception e) {
            // Swallow exception and return null, as per SDO 2.1 spec
            return null;
        }
    }

    public BigInteger getBigInteger(String path) {
        try {
            BigInteger value = (BigInteger)XPathEngine.getInstance().convertObjectToValueByPath(path, BigInteger.class, this);
            return value;
        } catch (Exception e) {
            // Swallow exception and return null, as per SDO 2.1 spec
            return null;
        }
    }

    public SDODataObject getDataObject(String path) throws ClassCastException {
        Object value = get(path);
        if(value instanceof ListWrapper) {
            return (SDODataObject)((ListWrapper)value).get(0);
        }
        return (SDODataObject)value;
    }

    public Date getDate(String path) {
        try {
            Date value = (Date)XPathEngine.getInstance().convertObjectToValueByPath(path, Date.class, this);
            return value;
        } catch (Exception e) {
            // Swallow exception and return null, as per SDO 2.1 spec
            return null;
        }
    }

    public String getString(String path) {
        try {
            String value = (String)XPathEngine.getInstance().convertObjectToValueByPath(path, String.class, this);
            return value;
        } catch (Exception e) {
            // Swallow exception and return null, as per SDO 2.1 spec
            return null;
        }
    }

    public List getList(String path) {
        try {
            return (List)XPathEngine.getInstance().convertObjectToValueByPath(path, List.class, this);
        } catch (Exception e) {
            // Swallow exception and return null, as per SDO 2.1 spec
            return null;
        }
    }

    public void setBoolean(String path, boolean value) {
        convertValueAndSet(path, value);
    }

    public void setByte(String path, byte value) {
        convertValueAndSet(path, value);
    }

    public void setChar(String path, char value) {
        convertValueAndSet(path, value);
    }

    public void setDouble(String path, double value) {
        convertValueAndSet(path, value);
    }

    public void setFloat(String path, float value) {
        convertValueAndSet(path, value);
    }

    public void setInt(String path, int value) {
        convertValueAndSet(path, value);
    }

    public void setLong(String path, long value) {
        convertValueAndSet(path, value);
    }

    public void setShort(String path, short value) {
        convertValueAndSet(path, value);
    }

    public void setBytes(String path, byte[] value) {
        convertValueAndSet(path, value);
    }

    public void setBigDecimal(String path, BigDecimal value) {
        convertValueAndSet(path, value);
    }

    public void setBigInteger(String path, BigInteger value) {
        convertValueAndSet(path, value);
    }

    public void setDataObject(String path, DataObject value) {
        set(path, value);
    }

    public void setDate(String path, Date value) {
        convertValueAndSet(path, value);
    }

    public void setString(String path, String value) {
        convertValueAndSet(path, value);
    }

    public void setList(String path, List value) {
        convertValueAndSet(path, value);
    }

    public Object get(int propertyIndex) throws IllegalArgumentException {
        Property p = getInstanceProperty(propertyIndex);
        return get(p);
    }

    public void set(int propertyIndex, Object value) {
        try {
            Property p = getInstanceProperty(propertyIndex);
            set(p, value);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("PropertyIndex invalid.");
        }
    }

    public boolean isSet(int propertyIndex) {
        Property p = getInstanceProperty(propertyIndex);
        return isSet(p);
    }

    public void unset(int propertyIndex) {
        Property p = getInstanceProperty(propertyIndex);
        unset(p);
    }

    public boolean getBoolean(int propertyIndex) throws IllegalArgumentException, ClassCastException {
        Property property = getInstanceProperty(propertyIndex);
        return getBoolean(property);
    }

    public byte getByte(int propertyIndex) throws IllegalArgumentException, ClassCastException {
        Property property = getInstanceProperty(propertyIndex);
        return getByte(property);
    }

    public char getChar(int propertyIndex) throws IllegalArgumentException, ClassCastException {
        Property property = getInstanceProperty(propertyIndex);
        return getChar(property);
    }

    public double getDouble(int propertyIndex) throws IllegalArgumentException, ClassCastException {
        Property property = getInstanceProperty(propertyIndex);
        return getDouble(property);
    }

    public float getFloat(int propertyIndex) throws IllegalArgumentException, ClassCastException {
        Property property = getInstanceProperty(propertyIndex);
        return getFloat(property);
    }

    public int getInt(int propertyIndex) throws IllegalArgumentException, ClassCastException {
        Property property = getInstanceProperty(propertyIndex);
        return getInt(property);
    }

    public long getLong(int propertyIndex) throws IllegalArgumentException, ClassCastException {
        Property property = getInstanceProperty(propertyIndex);
        return getLong(property);
    }

    public short getShort(int propertyIndex) throws IllegalArgumentException, ClassCastException {
        Property property = getInstanceProperty(propertyIndex);
        return getShort(property);
    }

    public byte[] getBytes(int propertyIndex) throws IllegalArgumentException, ClassCastException {
        Property property = getInstanceProperty(propertyIndex);
        return getBytes(property);
    }

    public BigDecimal getBigDecimal(int propertyIndex) throws IllegalArgumentException, ClassCastException {
        Property property = getInstanceProperty(propertyIndex);
        return getBigDecimal(property);
    }

    public BigInteger getBigInteger(int propertyIndex) throws IllegalArgumentException, ClassCastException {
        Property property = getInstanceProperty(propertyIndex);
        return getBigInteger(property);
    }

    public SDODataObject getDataObject(int propertyIndex) {
        Property property = getInstanceProperty(propertyIndex);
        return getDataObject(property);
    }

    public Date getDate(int propertyIndex) throws IllegalArgumentException, ClassCastException {
        Property property = getInstanceProperty(propertyIndex);
        return getDate(property);
    }

    public String getString(int propertyIndex) throws IllegalArgumentException, ClassCastException {
        Property property = getInstanceProperty(propertyIndex);
        return getString(property);
    }

    public List getList(int propertyIndex) {
        Property property = getInstanceProperty(propertyIndex);
        return getList(property);
    }

    /**
     * @deprecated in SDO 2.1.0.
     */
    public Sequence getSequence(String path) {
        // get property from path
        Object anObject = get(path);

        if ((null == anObject) || !(anObject instanceof DataObject)) {
            // throw exception because there is no way to get the property from a null object in this context
            throw SDOException.sequenceNotFoundForPath(path);
        } else {
            // we cannot use containmentProperty in the many case as the index is missing - use the DO
            return ((DataObject)anObject).getSequence();
        }
    }

    /**
     * @deprecated in SDO 2.1.0.
     */
    public Sequence getSequence(int propertyIndex) {
        // get property
        Property aProperty = getInstanceProperty(propertyIndex);
        return getSequencePrivate(aProperty);
    }

    /**
     * @deprecated in SDO 2.1.0.
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
        if ((aProperty != null) && ((SDOProperty)aProperty).getType().isSequenced() && isSet(aProperty) &&//
                !((SDOProperty)aProperty).getType().isDataType() && !aProperty.isMany()) {
            return ((DataObject)get(aProperty)).getSequence();
        } else {
            throw SDOException.sequenceNotSupportedForProperty(aProperty.getName());
        }
    }

    public SDOSequence getSequence() {
        // sequence object should be null if !sequenced
        return sequence;
    }

    public void setBoolean(int propertyIndex, boolean value) {
        convertValueAndSet(propertyIndex, value);
    }

    public void setByte(int propertyIndex, byte value) {
        convertValueAndSet(propertyIndex, value);
    }

    public void setChar(int propertyIndex, char value) {
        convertValueAndSet(propertyIndex, value);
    }

    public void setDouble(int propertyIndex, double value) {
        convertValueAndSet(propertyIndex, value);
    }

    public void setFloat(int propertyIndex, float value) {
        convertValueAndSet(propertyIndex, value);
    }

    public void setInt(int propertyIndex, int value) {
        convertValueAndSet(propertyIndex, value);
    }

    public void setLong(int propertyIndex, long value) {
        convertValueAndSet(propertyIndex, value);
    }

    public void setShort(int propertyIndex, short value) {
        convertValueAndSet(propertyIndex, value);
    }

    public void setBytes(int propertyIndex, byte[] value) {
        convertValueAndSet(propertyIndex, value);
    }

    public void setBigDecimal(int propertyIndex, BigDecimal value) {
        convertValueAndSet(propertyIndex, value);
    }

    public void setBigInteger(int propertyIndex, BigInteger value) {
        convertValueAndSet(propertyIndex, value);
    }

    public void setDataObject(int propertyIndex, DataObject value) {
        set(propertyIndex, value);
    }

    public void setDate(int propertyIndex, Date value) {
        convertValueAndSet(propertyIndex, value);
    }

    public void setString(int propertyIndex, String value) {
        convertValueAndSet(propertyIndex, value);
    }

    public void setList(int propertyIndex, List value) {
        convertValueAndSet(propertyIndex, value);
    }

    public Object get(Property property) throws IllegalArgumentException {
        if (null == property) {// check null property before null type
            throw new IllegalArgumentException("Argument not Supported.");
        }

        if ((null != type) && !type.isOpen() && property.isOpenContent()) {
            throw new IllegalArgumentException("Argument not Supported.");
        }
        if (property.isMany()) {
            return getList(property);
        }

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
        DataObject propertyDO = aHelperContext.getDataFactory().create(SDOConstants.SDO_URL, SDOConstants.PROPERTY);

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
            Property xmlElementProperty = aHelperContext.getTypeHelper().getOpenContentProperty(SDOConstants.SDOXML_URL, SDOConstants.XMLELEMENT_PROPERTY_NAME);
            propertyDO.set(xmlElementProperty, Boolean.TRUE);
            sdotype = ((SDOTypeHelper)aHelperContext.getTypeHelper()).getTypeForSimpleJavaType(valueClass);
        }
        propertyDO.set("type", sdotype);

        propertyDO.set("many", isMany);

        propertyDO.set("containment", isContainment);
        return aHelperContext.getTypeHelper().defineOpenContentProperty(null, propertyDO);
    }

    public Property defineOpenContentProperty(String name, Object value, Type sdotype) throws UnsupportedOperationException, IllegalArgumentException {
        if(sdotype == null){
          return defineOpenContentProperty(name, value);
        }
        DataObject propertyDO = aHelperContext.getDataFactory().create(SDOConstants.SDO_URL, SDOConstants.PROPERTY);

        propertyDO.set("name", name);
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
                }
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

    public void set(Property property, Object value) throws UnsupportedOperationException, IllegalArgumentException {
        set((SDOProperty) property, value, true);
    }

    public void setInternal(SDOProperty property, Object value, boolean updateSequence) throws UnsupportedOperationException, IllegalArgumentException {
        if (null == getType()) {
            throw new UnsupportedOperationException("Type is null");
        }

        if (property.isOpenContent() && !getType().isOpen()) {// not open and not in types currentValueStore
            throw new IllegalArgumentException("DataObject " + this + " is not Open for property " + property.getName());
        }

        // Note: get() will call setPropertyInternal() if the list is null = not set yet - we need to propagate the updateSequence flag
        Object oldValue = get(property);
        boolean wasSet = isSet(property);
        if (wasSet && (oldValue == value)) {
            return;
        } else {
            _setModified(true);
        }

        if (property.isMany()) {
            List listValue;
            if (null == value) {
                listValue = new ListWrapper(this, property);
            } else {
                try {
                    listValue = (List) value;
                } catch(ClassCastException e) {
                    throw new IllegalArgumentException("Properties with isMany = true can only be set on list values.");
                }
            }
            if (property.isContainment() || isContainedByDataGraph(property)) {
                for(Object next: listValue) {
                    if(next instanceof SDODataObject) {
                        if (parentContains(next)) {
                            throw new IllegalArgumentException("Circular reference.");
                        }
                    }
                }
            }

            ListWrapper oldValueListWrapper = (ListWrapper) oldValue;
            // 20060529: v33: Move clear() out of ListWrapper.addAll()
            // handle clearing of elements which also calls removeContainment(prop) outside of addAll()
            oldValueListWrapper.clear(updateSequence);
            // handle updateContainment and sequences inside addAll()
            oldValueListWrapper.addAll((Collection)value, updateSequence);// for non-default Pluggable impl this function is not required
        } else {
            if (property.isContainment() || isContainedByDataGraph(property)) {
                if (parentContains(value)) {
                    throw new IllegalArgumentException("Circular reference.");
                }

                // detach the oldValue from this dataObject
                if(null != oldValue) {
                    detach(property, oldValue);
                }

                // sets the new value's container and containment prop to this dataobject, detaches from other owner...not right

                /**
                 * Case: set(do) is actually a move(do) between two CS - detach required
                 * Case: set(do) is actually an add(do) on a single CS - detach not required
                 */
                SDODataObject dataObjectValue = (SDODataObject) value;
                if (dataObjectValue != null) {
                    updateContainment(property, dataObjectValue);
                }
            }

            // process pluggable currentValueStore and set [value] as a property of [this] as well as sequences
            setPropertyInternal(property, value, updateSequence);
        }
        if (getType().isOpen() && property.isOpenContent()) {
            addOpenContentProperty(property);
        }
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
    public void set(SDOProperty property, Object value, boolean updateSequence) throws UnsupportedOperationException, IllegalArgumentException {
        if (null == property) {
            throw new IllegalArgumentException("Illegal Argument.");
        }

        if (property.isReadOnly()) {
            throw new UnsupportedOperationException("Property is Readonly." + property.getName() + "  " + getType().getName());
        }

        // Can't set null on a non-nullable property - implementation specific handling, one of:
        // 1) perform an unset operation
        // 2) throw new UnsupportedOperationException("Property ["+ property.getName() +"] is non-nullable");
        // We will perform an unset op...
        if (value == null && !property.isNullable()) {
            unset(property, false, updateSequence);
        } else {
            setInternal(property, value, updateSequence);
        }
    }

    public boolean isSet(Property property) {
        if (null == property) {
            throw SDOException.cannotPerformOperationOnNullArgument("isSet");
        }

        if (property.isMany()) {
            List value = getList(property);
            return !value.isEmpty();
        } else {
            return isSetInternal(property);
        }
    }

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

        if (((SDOProperty)property).isReadOnly()) {
            throw new UnsupportedOperationException("Property is Readonly." + property.getName() + "  " + getType().getName());
        }

        boolean wasSet = isSet(property);

        if (wasSet) {
            if(!fromDelete){
              _setModified(true);
            }
        } else {
            return;
        }

        if (property.isContainment() || isContainedByDataGraph(property)) {
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

    public boolean getBoolean(Property property) throws IllegalArgumentException, ClassCastException {
        Boolean propertyBooleanValue = (Boolean)convertObjectToValue(property, Boolean.class);
        if (propertyBooleanValue == null) {
            return false;
        }
        return propertyBooleanValue.booleanValue();
    }

    public byte getByte(Property property) throws IllegalArgumentException, ClassCastException {
        Byte propertyByteValue = (Byte)convertObjectToValue(property, Byte.class);
        if (propertyByteValue == null) {
            return 0;
        }
        return propertyByteValue.byteValue();
    }

    public char getChar(Property property) throws IllegalArgumentException {
        Character propertyCharValue = (Character)convertObjectToValue(property, Character.class);
        if (propertyCharValue == null) {
            return '\u0000';
        }
        return propertyCharValue.charValue();
    }

    public double getDouble(Property property) throws IllegalArgumentException {
        Double propertyDoubleValue = (Double)convertObjectToValue(property, Double.class);
        if (propertyDoubleValue == null) {
            return 0.0d;
        }
        return propertyDoubleValue.doubleValue();
    }

    public float getFloat(Property property) throws IllegalArgumentException {
        Float propertyFloatValue = (Float)convertObjectToValue(property, Float.class);
        if (propertyFloatValue == null) {
            return 0.0f;
        }
        return propertyFloatValue.floatValue();
    }

    public int getInt(Property property) throws IllegalArgumentException {
        Integer propertyIntegerValue = (Integer)convertObjectToValue(property, Integer.class);
        if (propertyIntegerValue == null) {
            return 0;
        }
        return propertyIntegerValue.intValue();
    }

    public long getLong(Property property) throws IllegalArgumentException {
        Long propertyLongValue = (Long)convertObjectToValue(property, Long.class);
        if (propertyLongValue == null) {
            return 0L;
        }
        return propertyLongValue.longValue();
    }

    public short getShort(Property property) throws IllegalArgumentException {
        Short propertyShortValue = (Short)convertObjectToValue(property, Short.class);
        if (propertyShortValue == null) {
            return 0;
        }
        return propertyShortValue.shortValue();
    }

    public byte[] getBytes(Property property) throws IllegalArgumentException {
        byte[] propertyByteValue = (byte[])convertObjectToValue(property, byte[].class);
        return propertyByteValue;
    }

    public BigDecimal getBigDecimal(Property property) throws IllegalArgumentException {
        BigDecimal propertyDecimalValue = (BigDecimal)convertObjectToValue(property, BigDecimal.class);
        return propertyDecimalValue;
    }

    public BigInteger getBigInteger(Property property) throws IllegalArgumentException {
        BigInteger propertyBigIntegerValue = (BigInteger)convertObjectToValue(property, BigInteger.class);
        return propertyBigIntegerValue;
    }

    public SDODataObject getDataObject(Property property) throws IllegalArgumentException, ClassCastException {
        if(property != null && property.isMany()) {
            List value = (List)get(property);
            if(value.size() > 0) {
                return (SDODataObject)value.get(0);
            } else {
                return null;
            }
        }
        return (SDODataObject)get(property);
    }

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

    public String getString(Property property) {
        String propertyStringValue = (String)convertObjectToValue(property, String.class);
        return propertyStringValue;
    }

    public List getList(Property property) {
        if (null == property) {
            throw SDOException.cannotPerformOperationOnNullArgument("getList");
        }

        if (((type != null) && !type.isOpen() && property.isOpenContent())) {
            throw new IllegalArgumentException("Argument not Supported.");
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

        // get a default empty list
        List theList = new ListWrapper(this, property);
        if (getType().isOpen() && property.isOpenContent()) {
            addOpenContentProperty(property);
        }

        // set the property to a default empty list
        setPropertyInternal((SDOProperty) property, theList, false);
        return theList;
    }

    public void setBoolean(Property property, boolean value) {
        convertValueAndSet(property, value);
    }

    public void setByte(Property property, byte value) {
        convertValueAndSet(property, value);
    }

    public void setChar(Property property, char value) {
        convertValueAndSet(property, value);
    }

    public void setDouble(Property property, double value) {
        convertValueAndSet(property, value);
    }

    public void setFloat(Property property, float value) {
        convertValueAndSet(property, value);
    }

    public void setInt(Property property, int value) {
        convertValueAndSet(property, value);
    }

    public void setLong(Property property, long value) {
        convertValueAndSet(property, value);
    }

    public void setShort(Property property, short value) {
        convertValueAndSet(property, value);
    }

    public void setBytes(Property property, byte[] value) {
        convertValueAndSet(property, value);
    }

    public void setBigDecimal(Property property, BigDecimal value) {
        convertValueAndSet(property, value);
    }

    public void setBigInteger(Property property, BigInteger value) {
        convertValueAndSet(property, value);
    }

    public void setDataObject(Property property, DataObject value) {
        set(property, value);
    }

    public void setDate(Property property, Date value) {
        convertValueAndSet(property, value);
    }

    public void setString(Property property, String value) {
        convertValueAndSet(property, value);
    }

    public void setList(Property property, List value) {
        convertValueAndSet(property, value);
    }

    public DataObject createDataObject(String propertyName) {
        Property aProperty = getInstanceProperty(propertyName);
        return createDataObject(aProperty);
    }

    public SDODataObject createDataObject(int propertyIndex) {
        Property aProperty = getInstanceProperty(propertyIndex);
        return createDataObject(aProperty);
    }

    public SDODataObject createDataObject(Property aProperty) {
        if (aProperty.isContainment()) {
            Type aType = aProperty.getType();
            if (aType != null) {
                return createDataObject(aProperty, aType);
            }
        }
        return null;
    }

    public SDODataObject createDataObject(String propertyName, String namespaceURI, String typeName) {
        Property aProperty = getInstanceProperty(propertyName);
        Type aType = aHelperContext.getTypeHelper().getType(namespaceURI, typeName);
        return createDataObject(aProperty, aType);
    }

    public SDODataObject createDataObject(int propertyIndex, String namespaceURI, String typeName) {
        Property aProperty = getInstanceProperty(propertyIndex);
        Type aType = aHelperContext.getTypeHelper().getType(namespaceURI, typeName);
        return createDataObject(aProperty, aType);
    }

    public SDODataObject createDataObject(Property property, Type aType) {
        SDODataObject created = (SDODataObject) aHelperContext.getDataFactory().create(aType);

        if (property.isMany()) {
            //getList.add will call updateContainment
            ((ListWrapper)getList(property)).add(created, true);
        } else {
            set(property, created);
        }

        _setModified(true);
        created._setCreated(true);

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
            List oldListValue = (List) oldValue;
            for (int i = 0, size = oldListValue.size(); i < size; i++) {
                Object nextValue = oldListValue.get(i);
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
        if ((null == container) && !fromDelete) {
            return;
        }
        boolean isCSRoot = (null != changeSummary) && (changeSummary.getRootObject() == this);

        // Detaching the dataObject that is the root of the CS doesn't need to do anything - truncate the recursive traversal
        if(!fromDelete && isCSRoot){
          return;
        }

        // This is the root of the recursive loop
        detachDeleteRecursivePrivate(fromDelete, !isCSRoot, true);
    }

    /**
     * INTERNAL:
     * Recursively walk the tree and set oldSettings for a detached/deleted object.
     * @param fromDelete
     * @param clearCS (true = clear the cs field) = !(is root of the detach/delete subtree the CS root?)
     * @param isRootOfRecursiveLoop (are we at the root of the detach/delete or inside the subtree)
     */
    private void detachDeleteRecursivePrivate(boolean fromDelete, boolean clearCS, boolean isRootOfRecursiveLoop) {
        // Store flag for (is root of the detach/delete subtree the CS root?) before we modify it when this object has no container
        boolean subTreeRootHasCStoClear = clearCS;
        if (null == getContainer()) {
            clearCS = false;
        }
        if (isRootOfRecursiveLoop || fromDelete) {
            if (null != getContainer()) {
                getContainer()._setModified(true);
                _setContainer(null);
                _setContainmentPropertyName(null);
            }
        }
        _setDeleted(true);

        /** Order here is important - the dataGraph pointer should be cleared preorder before we enter the recursive loop to clear child DO's */
        DataGraph previousDataGraph = getDataGraph();
        // Remove back pointer to containing DataGraph
        setDataGraph(null);

        List instancePropertiesList = getInstanceProperties();
        for (int i = 0, psize = instancePropertiesList.size(); i < psize; i++) {
            SDOProperty nextProperty = (SDOProperty)instancePropertiesList.get(i);
            Object oldValue = get(nextProperty);

            if (!nextProperty.getType().isChangeSummaryType()) {
                // Non-containment nodes have changeSummary and dataGraph pointers if they were in a dataGraph
                if (nextProperty.isContainment() || isContainedByDataGraph(previousDataGraph, nextProperty)) {
                    if (nextProperty.isMany()) {
                        Object manyItem;
                        for (int j = 0, lsize = ((List)oldValue).size(); j < lsize; j++) {
                            manyItem = ((List)oldValue).get(j);
                            detachDeleteRecursivePrivateHelper((SDODataObject)manyItem, fromDelete, clearCS);
                        }
                    } else {
                        detachDeleteRecursivePrivateHelper((SDODataObject)oldValue, fromDelete, clearCS);
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
        if (clearCS || subTreeRootHasCStoClear) {
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
    private void detachDeleteRecursivePrivateHelper(SDODataObject aDataObject, boolean fromDelete, boolean clearCS) {
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
            aDataObject.detachDeleteRecursivePrivate(fromDelete, clearCS, false);
        }
    }

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
        SDOProperty containmentProperty = getContainmentProperty();

        if ((containmentProperty != null) && containmentProperty.isReadOnly()) {
            throw new UnsupportedOperationException("Property is Readonly." + containmentProperty.getName() + "  " + getType().getName());
        }

        if (containmentProperty != null) {
            if (getContainmentProperty().isMany()) {
                List oldList = getContainer().getList(containmentProperty);

                // pass remove containment flag instead of calling remove(this) and detachOrDelete(fromDelete) separately
                // This will invoke creation of an intact list copy before removing its containment and clearing its changeSummary
                ((ListWrapper)oldList).remove(this, fromDelete, updateSequence);
            } else {
                getContainer().unset(containmentProperty, fromDelete, updateSequence);
            }
        } else {
            _setDeleted(true);
            detachOrDelete(fromDelete);
        }
    }

    public void delete() {
        deleteWithSequenceUpdate();
    }

    public SDODataObject getContainer() {
        return container;
    }

    public SDOProperty getContainmentProperty() {
        if ((container != null) && (containmentPropertyName != null)) {
            return container.getInstanceProperty(containmentPropertyName);
        } else {
            return null;
        }
    }

    public DataGraph getDataGraph() {
        return dataGraph;
    }

    public void setDataGraph(DataGraph dataGraph) {
        this.dataGraph = dataGraph;
    }

    public SDOType getType() {
        return type;
    }

    public List getInstanceProperties() {
        if (null == instanceProperties) {
            instanceProperties = new ArrayList();
        }
        return instanceProperties;
    }

    public SDOProperty getProperty(String propertyName) {
        return getInstanceProperty(propertyName);
    }

    public SDOProperty getInstanceProperty(String propertyName) {
        if (getType() == null) {
            throw new UnsupportedOperationException("Type is null");
        }
        SDOProperty property = getType().getProperty(propertyName);
        if (null == property) {
            property = (SDOProperty)_getOpenContentAliasNamesMap().get(propertyName);
            if (null == property) {
                for (int i = 0; i < _getOpenContentProperties().size(); i++) {
                    SDOProperty nextProp = (SDOProperty)_getOpenContentProperties().get(i);
                    if (nextProp.getName().equals(propertyName)) {
                        return nextProp;
                    }
                }
                if (null == property) {
                    for (int i = 0; i < _getOpenContentPropertiesAttributes().size(); i++) {
                        SDOProperty nextProp = (SDOProperty)_getOpenContentPropertiesAttributes().get(i);
                        if (nextProp.getName().equals(propertyName)) {
                           return nextProp;
                        }
                    }
                }
            }
        }
        return property;
    }

    SDOProperty getInstanceProperty(String propertyName, Object value) {
        SDOProperty sdoProperty = getInstanceProperty(propertyName);
        if(null == sdoProperty) {
            sdoProperty = (SDOProperty) defineOpenContentProperty(propertyName, value);
        }
        return sdoProperty;
    }

    /**
     * INTERNAL:
     * @param propertyIndex
     * @return
     * @throws SDOException
     */
    public SDOProperty getInstanceProperty(int propertyIndex) throws IllegalArgumentException {
        try {
            SDOProperty property = getInstancePropertiesArray()[propertyIndex];
            return property;
        } catch (IndexOutOfBoundsException e) {
            throw SDOException.propertyNotFoundAtIndex(e, propertyIndex);
        }
    }

    public SDODataObject getRootObject() {
        if (getContainer() != null) {
            return getContainer().getRootObject();
        }
        return this;
    }

    public SDOChangeSummary getChangeSummary() {
        return changeSummary;
    }

    /**
     * INTERNAL:
     * Set flag created value.
     * @param created   flag created's new value.
     */
    public void _setCreated(boolean created) {
        if (changeSummary != null) {
            changeSummary.setCreated(this, created);
        }
    }

    /**
     * INTERNAL:
     * Set flag modified value.
     * @param modified   flag modified's new value.
     */
    public void _setModified(boolean modified) {
        if (changeSummary != null) {
            if (isLogging()) {
                updateChangeSummaryWithOriginalValues();
            }
        }
    }

    /**
     * INTERNAL:
     * Set flag deleted value.
     * @param deleted   flag deleted's new value.
     */
    private void _setDeleted(boolean deleted) {
        // reduced scope from public to private 17 May 2007 - use the public deprecated public function until we remove it
        if (changeSummary != null) {
            boolean wasDeleted = changeSummary.setDeleted(this, deleted);
            if (wasDeleted && isLogging()) {
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
        changeSummary = (SDOChangeSummary) csm;
        if (getType() != null) {
            SDOProperty changeSummaryProperty = getType().getChangeSummaryProperty();
            if (changeSummaryProperty != null) {
                setChangeSummaryProperty(changeSummaryProperty, csm);
            }
        }
    }

    /**
     * INTERNAL:
      * Recursively Set this DataObject's ChangeSummary as passed in value.
     * @param aChangeSummary   the ChangeSummary taking this DataObject as root.
     */
    public void _setChangeSummary(SDOChangeSummary aChangeSummary) {
        updateChangeSummaryAndDataGraph(aChangeSummary, getDataGraph());
    }

    /**
     * INTERNAL:
     * Recursively set this DataObject's changeSummary and dataGraph.
     * The parent changeSummary and dataGraph may be null.
     * Preconditions: No changeSummary must exist on the child tree.
     * Callers: Typically updateContainment (during a set) or delete/detach
     *   of an object without its own changeSummary property.
     * @param aDataGraph
     */
    private void updateChangeSummaryAndDataGraph(ChangeSummary aChangeSummary, DataGraph aDataGraph) {
        Iterator iterProperties = getInstanceProperties().iterator();
        Property property;
        Object object;
        Object listContainedObject;

        // Add back pointer to containing DataGraph - in preOrder sequence before recursing
        setDataGraph(aDataGraph);

        // Recurse currentValueStore
        while (iterProperties.hasNext()) {
            property = (Property)iterProperties.next();

            // Note: Both opposite currentValueStore cannot be isContainment=true
            // special handling for bidirectional currentValueStore, do a shallow set
            // do not recurse bidirectional currentValueStore that are non-containment
            if (property.isContainment() || isContainedByDataGraph(property)) {
                object = get(property);
                if (object instanceof SDODataObject) {// DataObject child of this DataObject
                    ((SDODataObject)object).updateChangeSummaryAndDataGraph(aChangeSummary, aDataGraph);
                }

                if (object instanceof ListWrapper) {// ListWrapper child of this DataObject
                    Iterator anIterator = ((ListWrapper)object).iterator();
                    while (anIterator.hasNext()) {
                        listContainedObject = anIterator.next();
                        if (listContainedObject instanceof SDODataObject) {
                            ((SDODataObject)listContainedObject).updateChangeSummaryAndDataGraph(aChangeSummary, aDataGraph);
                        }
                    }
                }
            }
        }
        // Set/Clear changeSummary in postOrder sequence
        setChangeSummaryNonRecursive(aChangeSummary);
    }


    /**
     * INTERNAL:
     * Recursively set this DataObject's DataGraph
     * This function serves as a copy of updateChangeSummaryAndDataGraph() to recursively walk and set the dataGraph.
     * that will be run when no recursion occurs in the case that an object (with a changeSummary)
     * is set internally to a tree (without a changeSummary).
     * Callers: Typically updateContainment (during a set) or delete/detach
     *   when the current object is internal with its own changeSummary property.
     * @param aDataGraph
     */
    private void updateDataGraph(DataGraph aDataGraph) {
        Iterator iterProperties = getInstanceProperties().iterator();
        Property property;
        Object object;
        Object listContainedObject;

        // Add back pointer to containing DataGraph - in preOrder sequence before recursing
        setDataGraph(aDataGraph);

        // Recurse currentValueStore
        while (iterProperties.hasNext()) {
            property = (Property)iterProperties.next();

            // Note: Both opposite currentValueStore cannot be isContainment=true
            // special handling for bidirectional currentValueStore, do a shallow set
            // do not recurse bidirectional currentValueStore that are non-containment
            if (property.isContainment() || isContainedByDataGraph(property)) {
                object = get(property);
                if (object instanceof SDODataObject) {// DataObject child of this DataObject
                    ((SDODataObject)object).updateDataGraph(aDataGraph);
                }

                if (object instanceof ListWrapper) {// ListWrapper child of this DataObject
                    Iterator anIterator = ((ListWrapper)object).iterator();
                    while (anIterator.hasNext()) {
                        listContainedObject = anIterator.next();
                        if (listContainedObject instanceof SDODataObject) {
                            ((SDODataObject)listContainedObject).updateDataGraph(aDataGraph);
                        }
                    }
                }
            }
        }
    }

    /**
     * INTERNAL:
     *
     * @param property
     * @param value
     */
    private void setChangeSummaryProperty(SDOProperty property, ChangeSummary value) {
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
                // reset changeSummary
                if (null == changeSummary) {
                    // we are not recursively setting cs - so don't use the set function as child nodes will be replaced later
                    changeSummary = (SDOChangeSummary) cs;
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
            ((SDOChangeSummary)cs).getOriginalValueStores().remove(this);
        }

        // swap sequences (may be UC2(DataObject and sequence change) or UC4 (sequence change only)
        if (getType().isSequenced()) {// assume sequence is !null
            // perform sequence.isDirty check independent of a dataObject.isDirty check
            if (((SDOChangeSummary)cs).isDirty(sequence)) {
                Sequence currentSequence = sequence;
                SDOSequence originalSequence = (SDOSequence)((SDOChangeSummary)cs).getOriginalSequences().get(this);

                // both sequences are either null or set
                if ((null == originalSequence) && (null != currentSequence)) {
                    throw SDOException.oldSequenceNotFound();
                } else {
                    sequence = originalSequence;
                }

                // reset the cache map
                ((SDOChangeSummary)cs).getOldSequences().remove(this);
                // rest the primary map
                ((SDOChangeSummary)cs).getOriginalSequences().remove(this);
            }
        }

        // restore any open content properties to the 3 data structures on SDODataObject, and remove from cs.unsetOCPropsMap
        // see openContentPropertiesMap<String,Property>, openContentProperties(List<Property>). instanceProperties (List<Property>)
        List oldUnsetOCList =  ((SDOChangeSummary)cs).getUnsetOCProperties(this);
        for (int i = 0, size = oldUnsetOCList.size(); i < size; i++) {
             // it is essential that the oc property is removed from the cs or we will get an unsupported op during resetChanges()
             // the property will be added only when logging=true, we reference the first item in the list as it reduces in size
             addOpenContentProperty((Property)oldUnsetOCList.get(0));
        }

        // recursive step: swap valueStores on every contained subtree
        for (Iterator iterProperties = getInstanceProperties().iterator();
                 iterProperties.hasNext();) {
            SDOProperty property = (SDOProperty)iterProperties.next();
            if (!property.getType().isChangeSummaryType()) {
                Object value = get(property);
                if ((null == value) && (null != getChangeSummary())) {
                    // no changes for null properties
                } else {
                    if (property.isMany()) {
                        // check for modified ListWrapper
                        // assumption that we never have a null ListWrapper should still avoid a possible NPE
                        if (null != value) {
                            ((ListWrapper)value).undoChanges(getChangeSummary());
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
        if (isLogging() && (!getChangeSummary().isDirty(this)) && (!getChangeSummary().isCreated(this))) {
            // dont copy containers of many props
            // original valuestore becomes current one (from null)
            getChangeSummary().getOriginalValueStores().put(this, _getCurrentValueStore());
            // we make a shallow copy of the current valuestore arrays at this level in the tree
            currentValueStore = _getCurrentValueStore().copy();

            // handle Sequences only in UC2 where we have modified the container object - not when only the sequence is dirty
            if (getType().isSequenced()) {
                // deep copy the list of settings so a modification of the current sequence will not affect a setting in the originalSequences map
                SDOSequence copySequence = getSequence().copy();

                // store original sequence on ChangeSummary
                getChangeSummary().getOriginalSequences().put(this, copySequence);
            }
        }
    }

    /**
     * INTERNAL:
     * Initialize all old settings related to ChangeSummary and recursively go down
     * the tree to initialize all DataObjects rooted at this DataObject.
     */
    public void resetChanges() {
        // fill all oldSettings when logging is turned on
        if ((container != null) && (containmentPropertyName != null) && (changeSummary != null)) {
            changeSummary.setOldContainer(this, container);
            changeSummary.setOldContainmentProperty(this, container.getInstanceProperty(containmentPropertyName));
        }

        // Note: valueStores will not be switched and originalValueStore will continue to be null until the first modification
        // initialize empty list for current dataObject
        for(int x=0, instancePropertiesSize=instanceProperties.size(); x<instancePropertiesSize; x++) {
            SDOProperty property = instanceProperties.get(x);
            Object value = get(property);

            // #5878436 12-FEB-07 do not recurse into a non-containment relationship unless inside a dataGraph
            if ((property.isContainment() || isContainedByDataGraph(property)) && //
                !property.isMany() && (value != null) && !property.getType().isChangeSummaryType()) {

                ((SDODataObject)value).resetChanges();
            } else {
                // Handle isMany objects and store the oldCont* values for each item in the list
                if (property.isMany()) {
                    if (null != value) {// secondary NPE check
                        List listValue = (List) value;
                        for(int y=0, listValueSize=listValue.size(); y<listValueSize; y++) {
                            Object valueMany = listValue.get(y);

                            // do not recurse into a non-containment relationship unless inside a dataGraph
                            if ((property.isContainment() || isContainedByDataGraph(property)) && //
                                (valueMany != null) && !property.getType().isChangeSummaryType()) {
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
     * @param aType
     */
    public void _setType(Type aType) {
        type = (SDOType) aType;

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

            Property csmProperty = type.getChangeSummaryProperty();
            if (csmProperty != null) {
                SDOChangeSummary aChangeSummary = new SDOChangeSummary(this, aHelperContext);
                aChangeSummary.endLogging();
                _setChangeSummary(aChangeSummary);
            }
        }
    }

    /**
     * INTERNAL:
     * Pluggable Interface for substituting the default Map with a custom Map Class
     * @param currentValueStore
     * void
     */
    public void _setCurrentValueStore(ValueStore aValueStore) {
        currentValueStore = aValueStore;
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
     * Sets the DataObject which contains this DataObject.
     * @param aContainer the DataObject which is the container of this DataObject.
     */
    public void _setContainer(DataObject aContainer) {
        container = (SDODataObject) aContainer;
    }

    public Map _getOpenContentPropertiesAttributesMap() {
        Map openContentPropertiesAttrs = new HashMap();
        for (int i = 0, size = _getOpenContentPropertiesAttributes().size(); i < size; i++) {
            Property next = (Property)_getOpenContentPropertiesAttributes().get(i);
            //if uri is null then attributes won't be prefix qualified
            QName qname = new QName(((SDOProperty)next).getUri(), next.getName());
            openContentPropertiesAttrs.put(qname, get(next));
        }

        return openContentPropertiesAttrs;

    }

    public void _setOpenContentPropertiesAttributesMap(Map openAttributeProperties) {
        Iterator iter = openAttributeProperties.keySet().iterator();

        while (iter.hasNext()) {
            QName nextKey = (QName)iter.next();

            if ((!nextKey.getNamespaceURI().equals(XMLConstants.XMLNS_URL)) && (!((nextKey.getNamespaceURI().equals(XMLConstants.SCHEMA_INSTANCE_URL)) && nextKey.getLocalPart().equals(XMLConstants.SCHEMA_TYPE_ATTRIBUTE)))) {
                Object value = openAttributeProperties.get(nextKey);
                SDOProperty prop = (SDOProperty)aHelperContext.getXSDHelper().getGlobalProperty(nextKey.getNamespaceURI(), nextKey.getLocalPart(), false);
                if (prop == null) {
                    DataObject propDo = aHelperContext.getDataFactory().create(SDOConstants.SDO_URL, SDOConstants.PROPERTY);
                    propDo.set("name", nextKey.getLocalPart());
                    propDo.set("type", SDOConstants.SDO_STRING);
                    propDo.set("many", false);
                    prop = (SDOProperty)aHelperContext.getTypeHelper().defineOpenContentProperty(null, propDo);
                    prop.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.FALSE);
                    prop.setUri(nextKey.getNamespaceURI());

                    set(prop, value);
                } else {
                    set(prop, value);
                }
            }
        }
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
            Type theType = null;
            if (next instanceof XMLRoot) {
                XMLRoot nextXMLRoot = (XMLRoot)next;
                value = nextXMLRoot.getObject();
                propertyName = nextXMLRoot.getLocalName();
                propertyUri = nextXMLRoot.getNamespaceURI();
                if (value instanceof DataObject) {
                    theType = ((DataObject)value).getType();
                } else {
                    theType = ((SDOTypeHelper)aHelperContext.getTypeHelper()).getTypeForSimpleJavaType(value.getClass());
                }
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
                theType = ((DataObject)next).getType();
            }
            else{
              theType = ((SDOTypeHelper)aHelperContext.getTypeHelper()).getTypeForSimpleJavaType(value.getClass());
            }
            if (propertyName != null) {
                SDOProperty prop = (SDOProperty)aHelperContext.getXSDHelper().getGlobalProperty(propertyUri, propertyName, true);
                if (prop == null) {
                    prop = getInstanceProperty(propertyName);
                    if(prop != null){
                      if(prop.getUri() == null && propertyUri != null){
                        prop = createNewProperty(propertyName,propertyUri, theType);
                      }else{
                        if(prop.getUri() != null){
                          if(propertyUri == null){
                            prop = createNewProperty(propertyName,propertyUri, theType);
                          }else if(!prop.getUri().equals(propertyUri)){
                             prop = createNewProperty(propertyName,propertyUri, theType);
                          }
                        }
                      }
                    }else{
                       prop = createNewProperty(propertyName,propertyUri, theType);
                    }
                }

                if (prop.isMany()) {
                    ((ListWrapper)getList(prop)).add(value, false);
                } else {
                    set(prop, value, false);
                }
            }
        }
    }

    private SDOProperty createNewProperty(String propertyName,String propertyUri, Type theType){
       DataObject propDo = aHelperContext.getDataFactory().create(SDOConstants.SDO_URL, SDOConstants.PROPERTY);
       propDo.set("name", propertyName);
       propDo.set("type", theType);
       propDo.set("many", true);
       SDOProperty prop = (SDOProperty)aHelperContext.getTypeHelper().defineOpenContentProperty(null, propDo);
       prop.setUri(propertyUri);
       return prop;
    }

    /**
     * INTERNAL:
     * This function is implemented by SDOType.setOpen() in a mapping setGet/SetMethodName call
     * @return
     */
    public List _getOpenContentPropertiesWithXMLRoots() {
        List returnList = new ArrayList();
        for (int i = 0, size = openContentProperties.size(); i < size; i++) {
            SDOProperty next = (SDOProperty) openContentProperties.get(i);

            XMLRoot root = new XMLRoot();
            String localName = next.getXPath();
            if (next.getType() != null) {
                if (!next.getType().isDataType()) {
                    String uri = next.getUri();
                    root.setNamespaceURI(uri);
                } else {
                    String uri = next.getUri();
                    root.setNamespaceURI(uri);
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
                    if ((next.getType() != null) && (next.getType().getXmlDescriptor() == null)) {
                        nextItem = XMLConversionManager.getDefaultXMLManager().convertObject(nextItem, String.class);
                    }
                    nextRoot.setObject(nextItem);
                    returnList.add(nextRoot);
                }
            } else {
                if ((next.getType() != null) && (next.getType().getXmlDescriptor() == null)) {
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
      * @return the List of open content Properties currently used in this DataObject.
      */
    public List _getOpenContentProperties() {
        if (openContentProperties == null) {
            openContentProperties = new ArrayList();
        }
        return openContentProperties;
    }


    private Map<String, Property> _getOpenContentAliasNamesMap() {
        if (openContentAliasNames == null) {
            openContentAliasNames = new HashMap();
        }
        return openContentAliasNames;
    }

   /**
     * INTERNAL:
     */
    private void convertValueAndSet(Property property, Object originalValue) {
        Object convertedValue;
        if (property.isMany()) {
            if (originalValue == null) {
                convertedValue = new ArrayList();
            } else if (originalValue instanceof List) {
                convertedValue = aHelperContext.getDataHelper().convert(property, originalValue);
            } else {
                List l = new ArrayList();
                l.add(originalValue);
                convertedValue = aHelperContext.getDataHelper().convert(property, l);
            }
        } else {
            convertedValue = aHelperContext.getDataHelper().convert(property, originalValue);
        }
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

        Object obj = unwrapListValue(property, get(property));

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
            if (cls == ClassConstants.List_Class) {
                obj = get(property);
            } else {
                obj = unwrapListValue(property, get(property));
            }
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
     * @param aContainmentPropertyName the name of the property on the containing DataObject which has this DataObject as a value.
     */
    public void _setContainmentPropertyName(String aContainmentPropertyName) {
        containmentPropertyName = aContainmentPropertyName;
    }

    /**
     * INTERNAL:
     * Return the name of the Property of the DataObject containing this data object
     * or null if there is no container.
     * @return the property containing this data object.
     */
    public String _getContainmentPropertyName() {
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
            return getContainer().parentContains(value);
        }
        return false;
    }

    /**
     * INTERNAL:
     * Return whether the current dataObject(this) is part of a dataGraph.
     * Typically this is used to determine whether to treat !isContainment dataObjects as containment while inside a dataGraph.
     * @param aProperty
     * @return
     */
    private boolean isContainedByDataGraph(Property aProperty) {
        // The property is of type DataObject that points to a DataGraph
        return isContainedByDataGraph(getDataGraph(),  (SDOProperty) aProperty);
    }

    /**
     * INTERNAL:
     * Return whether the current dataObject(this) is was part of the passed in dataGraph.
     * Typically this is used to determine whether to treat !isContainment dataObjects as containment while inside a dataGraph.
     * The dataGraph input is used when the current DataGraph pointer has already been cleared
     * @param aDataGraph
     * @param aProperty
     * @return
     */
    private boolean isContainedByDataGraph(DataGraph aDataGraph, SDOProperty aProperty) {
        // The property is of type DataObject that points to a DataGraph
        return (null != aDataGraph) && (null != aProperty.getType()) && !aProperty.getType().isDataType();
    }

    /**
     * INTERNAL:
     * Update containment with flagged update sequence state
     * @param property
     * @param values
     * @param updateSequence
     */
    public void updateContainment(Property property, Collection values, boolean updateSequence) {
        if (property.isContainment() || isContainedByDataGraph(property)) {//

            Object[] valuesArray = values.toArray();
            for(int i=0; i<valuesArray.length; i++){
               Object next = valuesArray[i];
                if (next instanceof SDODataObject) {
                    updateContainment(property, (SDODataObject)next, updateSequence);
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
    public void updateContainment(Property property, SDODataObject aDataObject, boolean updateSequence) {
        if (property.isContainment() || isContainedByDataGraph(property)) {
            boolean wasInNewCS = (getChangeSummary() != null) && //
                (aDataObject.getChangeSummary() != null) && getChangeSummary().equals(aDataObject.getChangeSummary());

            // Remove property or old changeSummary
            if(aDataObject.getContainer() != null) {
                aDataObject.detach(false, updateSequence);
            }

            // Need to know if the DO was deleted and being re-added later on in this method.
            // Since getChangeSummary().isDeleted() will be affected before the code in question
            // is hit, store the value now.
            boolean isDeleted = false;
            if (getChangeSummary() != null) {
                isDeleted = getChangeSummary().isDeleted(aDataObject);
            }

            // Check that the target object is not a changeSummary root - an internal set of an internal changeSummary
            /**
             * The following 2 update functions are kept separate for performance reasons in #6473342..
             * Alternate implementations could have used a Command pattern object or boolean states to enable a
             * single function to perform multiple operations.
             *
             * The DataGraph pointer on all subtrees are always updated regardless of the dataGraph value.
             * However, the ChangeSummary pointer is only updated if the dataObject(value) being updated is not
             * itself an internal changeSummary root.
             * For example: the following would not update the CS to null when we get to dataObject B - as it has its own changeSummary-B
             *  root
             *     -> B
             *             -> CS-B
             *             -> D (String)
             * But, the following would update the CS to CS-root when we get to dataObject B because the changeSummary root is above it
             *  root
             *     -> B
             *             -> D (String)
             *     -> CS-root
             */
            if ((getChangeSummary() != null) && (aDataObject.getType() != null) && //
                    (aDataObject.getType().getChangeSummaryProperty() == null)) {
                // Recursively set the current changeSummary and dataGraph - the CS root is above or absent
                aDataObject.updateChangeSummaryAndDataGraph(getChangeSummary(), getDataGraph());
            } else {
                // Recursively set the dataGraph when this level (value) is the CS root n the subtree
                if(aDataObject.getDataGraph() != getDataGraph()) {
                    aDataObject.updateDataGraph(getDataGraph());
                }
            }

            // add value as a property of (this)
            aDataObject._setContainer(this);
            aDataObject._setContainmentPropertyName(property.getName());

            // We don't setCreated for objects that were previously deleted
            if (!wasInNewCS && (getChangeSummary() != null) && !getChangeSummary().isDeleted(aDataObject)) {
                aDataObject._setCreated(true);
            }

            // If we are adding a previously deleted object, we must cancel out the isDeleted with an isCreated
            // so we end up with all isDeleted, isModified == false
            if ((getChangeSummary() != null) && getChangeSummary().isDeleted(aDataObject)) {
                // explicitly clear the oldSetting and clear the key:value pair in the deleted map
                aDataObject._setDeleted(false);

                // remove oldSetting from map only when we return to original position
                // by comparing an undo() with the projected model after set() - for now keep oldSettings
            }

            // modify container object
            _setModified(true);

            // In the case of re-adding a previously deleted/detached data object, we may need to update
            // the change summary's OriginalValueStores list to ensure isModified correctness
            if (isDeleted) {
                // May need to remove the corresponding entry in the originalValueStore map.
                // Compare the value store to the corresponding entry in the originalValueStore map.
                // Check to see if we're adding into the old parent (undo delete) or into a new parent (move)
                Map originalValueStores = getChangeSummary().getOriginalValueStores();
                ValueStore originalVS = ((ValueStore) originalValueStores.get(aDataObject));
                if (originalVS.equals(aDataObject._getCurrentValueStore())) {
                    // Remove the old value store from the change summary
                    originalValueStores.remove(aDataObject);
                }

                DataObject oldParentDO = getChangeSummary().getOldContainer(aDataObject);
                if (oldParentDO == this) {
                    // The data object was deleted and is now being re-added to same parent object.
                    // May need to remove the corresponding entry in the oldValueStore map.
                    // Compare the parent value store to the corresponding entry in the oldValueStore map.
                    ValueStore originalParentVS = ((ValueStore) originalValueStores.get(oldParentDO));
                    if (originalParentVS.equals(this._getCurrentValueStore())) {
                        // Value stores are equal, now make sure no ListWrappers have been modified
                        // For each typePropertyValue that is a ListWrapper in the currentValueStore, check
                        // the OriginalElements list in the CS to see if that wrapper exists in the list (indicating
                        // the wrapper's list was modified at some point) and if it does, compare the current list
                        // to that original list.  If they are equal, and the value stores are equal, the entries
                        // can be removed from the change summary so isModified will work correctly
                        Object prop;
                        Object oldList;
                        Map originalElements = getChangeSummary().getOriginalElements();
                        List oldParentProps = oldParentDO.getInstanceProperties();
                        for (int i=0; i<oldParentProps.size(); i++) {
                            prop = originalParentVS.getDeclaredProperty(i);
                            oldList = originalElements.get(prop);
                            if (oldList != null) {
                                List oldElements = (List) oldList;
                                List currentElements = ((ListWrapper) prop).getCurrentElements();
                                if (oldElements.size() != currentElements.size()) {
                                    // A ListWrapper has been modified - don't remove the old value store entry
                                    return;
                                }
                                Iterator elementIt = currentElements.iterator();
                                Iterator oldelementIt = oldElements.iterator();
                                while (elementIt.hasNext()) {
                                    if (elementIt.next() != oldelementIt.next()) {
                                        // A ListWrapper has been modified - don't remove the old value store entry
                                        return;
                                    }
                                }
                            }
                        }
                        // Remove the old value store from the change summary
                        originalValueStores.remove(oldParentDO);
                    }
                }
            }
        }
    }

    /**
     * INTERNAL:
     * update containment and the update the sequence value by default.
     * @param property
     * @param value
     */
    public void updateContainment(Property property, SDODataObject value) {
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
        return new SDOExternalizableDelegator(this, aHelperContext);
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
        SDODataObject aParent = targetObject.getContainer();
        if (aParent == null) {
            // check for indexed property if root is a ListWrapper
            return currentPath;
        }

        // Recursive Case: O(log(n)) recursive calls, 1 for each tree level
        // get parent property based on parent property name in target, property will always be set
        String aContainmentPropertyName = targetObject._getContainmentPropertyName();
        SDOProperty aChild = aParent.getInstanceProperty(aContainmentPropertyName);

        // Handle ListWrapper contained DataObjects
        if (aChild.isMany()) {
            int index = (aParent.getList(aChild)).indexOf(targetObject);

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
     *
     * @return
     */
    private SDOProperty[] getInstancePropertiesArray() {
        if ((openContentProperties == null) || openContentProperties.isEmpty()) {
            return getType().getPropertiesArray();
        }

        SDOProperty[] props = getType().getPropertiesArray();
        SDOProperty[] ret = new SDOProperty[openContentProperties.size() + props.length];
        for (int i = 0; i < props.length; i++) {
            ret[i] = props[i];
        }
        for (int i = props.length; i < ret.length; i++) {
            ret[i] = (SDOProperty)openContentProperties.get(i - props.length);
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
        int index = ((SDOProperty)property).getIndexInType();
        if (index == -1) {
            return _getCurrentValueStore().getOpenContentProperty(property);
        } else {
            return _getCurrentValueStore().getDeclaredProperty(index);
        }
    }

    /**
     * INTERNAL:
     * Update the ValueStore with the new property value and update any sequence if it exists.
     * @param property
     * @param value
     * @param updateSequence (truncate call back from sequence when this function was called from sequence)
     */
    public void setPropertyInternal(SDOProperty property, Object value, boolean updateSequence) {
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
        Object origValue = getPropertyInternal(property);

        if (type.isSequenced() && updateSequence//
                 &&!property.getType().isChangeSummaryType() && !aHelperContext.getXSDHelper().isAttribute(property)) {
            // As we do for ListWrappers and DataObjects we will need to remove the previous setting if this set is actually a modify
            // keep sequence code here for backdoor sets
            if(property.isMany()) {
                getSequence().addSettingWithoutModifyingDataObject(property, value);
            } else {
                if(isSet(property)) {
                    getSequence().updateSettingWithoutModifyingDataObject(property, get(property), value);
                } else {
                    getSequence().addSettingWithoutModifyingDataObject(property, value);
                }
            }
        }

        int index = property.getIndexInType();
        if (index == -1) {
            _getCurrentValueStore().setOpenContentProperty(property, value);
        } else {
            _getCurrentValueStore().setDeclaredProperty(index, value);
            SDOProperty oppositeProperty = property.getOpposite();
            if(null != oppositeProperty) {
                if(null != value) {
                    if(property.isMany()) {
                        List<SDODataObject> valueList = (List<SDODataObject>) value;
                        for(SDODataObject valueDO : valueList) {
                            if(this != valueDO.get(oppositeProperty)) {
                                valueDO.setInternal(oppositeProperty, this, true);
                            }
                        }
                    } else {
                        SDODataObject valueDO = (SDODataObject) value;
                        if(oppositeProperty.isMany()) {
                            List oppositeList = valueDO.getList(oppositeProperty);
                            if(!oppositeList.contains(this)) {
                                oppositeList.add(this);
                            }
                        } else {
                            if(this != valueDO.get(oppositeProperty)) {
                                valueDO.setInternal(oppositeProperty, this, true);
                            }
                        }
                    }
                }

                if(null != origValue) {
                    if(property.isMany()) {
                        List<SDODataObject> origValueList = (List<SDODataObject>) origValue;
                        for(SDODataObject origValueDO : origValueList) {
                            if(null != origValueDO.get(oppositeProperty)) {
                                origValueDO.set(oppositeProperty, null);
                            }
                        }
                    } else {
                        DataObject origValueDO = (DataObject) origValue;
                        if(oppositeProperty.isMany()) {
                            List oppositeList = origValueDO.getList(oppositeProperty);
                            if(oppositeList.contains(this)) {
                                oppositeList.remove(this);
                            }
                        } else {
                            if(null != origValueDO.get(oppositeProperty)) {
                                origValueDO.set(oppositeProperty, null);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Add the open content property into all 3 data structures.
     * Remove the property from the unset map.
     * @param property
     */
    public void addOpenContentProperty(Property property) {
        List theList = null;
        if (aHelperContext.getXSDHelper().isAttribute(property)) {
            theList = _getOpenContentPropertiesAttributes();
        }else {
            theList = _getOpenContentProperties();
        }

        if (!theList.contains(property)) {
            if (isLogging()) {
                getChangeSummary().removeUnsetOCProperty(this, property);
            }
            theList.add(property);
            getInstanceProperties().add(property);
            if (((SDOProperty)property).hasAliasNames()) {
                for (int i = 0, size = ((SDOProperty)property).getAliasNames().size(); i < size; i++) {
                    _getOpenContentAliasNamesMap().put((String)((SDOProperty)property).getAliasNames().get(i), property);
                }
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
            getChangeSummary().setUnsetOCProperty(this, property);
        }

        // remove oc property
        _getOpenContentProperties().remove(property);
        _getOpenContentPropertiesAttributes().remove(property);
        getInstanceProperties().remove(property);
        if (((SDOProperty)property).hasAliasNames()) {
            for (int i = 0, size = ((SDOProperty)property).getAliasNames().size(); i < size; i++) {
               _getOpenContentAliasNamesMap().remove(((SDOProperty)property).getAliasNames().get(i));
            }
        }
    }

    /**
     * INTERNAL:
     * Return whether the property (open-content or declared) is set?
     * @param property
     * @return true if set, false otherwise
     */
    public boolean isSetInternal(Property property) {
        int index = ((SDOProperty)property).getIndexInType();
        if (index == -1) {
            return _getCurrentValueStore().isSetOpenContentProperty(property);
        } else {
            return _getCurrentValueStore().isSetDeclaredProperty(index);
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
                _getCurrentValueStore().unsetOpenContentProperty(property);
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
            sequence.removeSettingWithoutModifyingDataObject(property);
        }
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

    public List<Setting> getSettings() {
        if(null != sequence) {
            return getSequence().getSettings();
        } else {
            return null;
        }
    }

    public void _setSdoRef(String newRef) {
        sdoRef = newRef;
    }

    /**
     * Convenience method that unwraps a list and returns the first element,
     * if necessary.
     *
     * @param prop
     * @param val
     * @return
     */
    private Object unwrapListValue(Property prop, Object val) {
        if (prop == null || val == null) {
            return null;
        }
        if (! (prop.isMany() &&  val instanceof ListWrapper) ) {
            return val;
        }
        List l = (ListWrapper) val;
        if (l.size() == 0) {
            return null;
        }
        return l.get(0);
    }

}
