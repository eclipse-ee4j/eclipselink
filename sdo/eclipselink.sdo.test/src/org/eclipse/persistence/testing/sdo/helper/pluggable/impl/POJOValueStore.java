/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  

/*
   DESCRIPTION
    The POJOMap class is an example implementation of the ValueStore interface.


   PRIVATE CLASSES


   NOTES
     For implementing teams (BC4J is the first group) please refer to the following locations.....
      JUnit Test Framework
        tltest/source/sdo/org/eclipse/persistence/testing/sdo/helper/pluggable/PluggableTest.java
        tltest/source/sdo/org/eclipse/persistence/testing/sdo/helper/pluggable/impl/POJOValueStore.java (abstract)
        tltest/source/sdo/org/eclipse/persistence/testing/sdo/helper/pluggable/impl/POJOValueStoreReadWrite.java

     The ValueStore interface exposes the properties Map in SDODataObject to extension or replacement.
     The implementing class must override or extend the get/set/isSet/unset ValueStore functions so that
     any calls routed through the SDO API are routed through to the underlying Data-Model that ValueStore wraps.

     Possible implementors such as BC4J(ADF), Fabric or the JPA must maintain DataObject integrity
     which includes containment and changeSummary.

     Note: when using a HelperContext other than the default contructor created static one - via the jvm parameter,
                you will need to either pass in a new POJOValueStore(aHelperContext), or
                reset the helperContext instance directly on the valueStore in order to maintain context integrity

     Setup:
       Before using the ValueStore interface the type tree must be defined by loading in the
       XSD schema file that matches the plugged in model if using dynamic classes.

     API Design Spec Documentation (Includes internal design and end user docs)
       Oracle Files at
       http://files.oraclecorp.com/content/MySharedFolders/ST%20Functional%20Specs/AS11gR1/TopLink/SDO/SDO_PluggableDO_BC4J_DesignSpec.doc

   MODIFIED    (MM/DD/YY)
     mfobrien    05/16/07 -
     dmahar      11/23/06 -
     dmahar      11/23/06 -
     mfobrien    07/28/06 - POJOValueStore creation
     mfobrien    08/21/06 - refactor from implementing ValueStore to implementing Pluggable
     mfobrien    08/24/06 - move Pluggable.set() functionality to HashMap.put() - remove Pluggable interface
                                    - ATG performance updates refactor requires non-Map ValueStore interface
     mfobrien    09/04/06 - split POJO read/write functionality into subclasses
     mfobrien    11/20/06 - refactor static HelperProvider into a instantiable HelperContext - JIRA129
     mfobrien    11/28/06 - a HelperContext instance must be passed in via
                                             custom constructor or the default static context will be used
     mfobrien    02/02/07 - Introduce new shallow copy() function in support of ChangeSummary undo

 */
package org.eclipse.persistence.testing.sdo.helper.pluggable.impl;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.impl.HelperProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.persistence.sdo.DefaultValueStore;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.ValueStore;
import org.eclipse.persistence.sdo.helper.ListWrapper;

/**
 * Implements ValueStore using an internal HashMap for SDO wrapped POJO objects.
 * Maintains reference to wrapping SDO and embedded POJO
 * Internal POJO Classes follow standard JavaBean naming standards for get/set methods<br>
 * An empty argument constructor is provided.
 */
public abstract class POJOValueStore implements ValueStore {

    /*
     * The following two instance variables are an example of a way to track
     * (1) the POJO object
     * (2) the wrapping SDO that contains the Type tree that is used to wrap POJO subtrees
     *
     * Support for these two objects that are outside the Map interface is implementation dependent.
     */

    // container (of Types from XSD) for wrapping POJO 
    protected DataObject dataObject;

    // container for actual POJO (embedded wrappable) object
    protected Object object;

    // Map of cached SDODataObject wrappers around POJO objects
    protected Map properties;

    /** prefix name for get[Field]() get functions - model class must follow standard get naming format */
    public static final String METHOD_PREFIX_FOR_GET = "get";

    /** prefix name for set[Field]() set functions - model class must follow standard set naming format */
    public static final String METHOD_PREFIX_FOR_SET = "set";

    /** prefix name for isSet[Field]() isSet/get functions - model class must match implementation here */
    public static final String METHOD_PREFIX_FOR_ISSET = "get";

    /** prefix name for unSet[Field]() unSet/reset functions - model class must match implementation here */
    public static final String METHOD_PREFIX_FOR_UNSET = "set";

    /** error message for missing property name */
    public static final String ERROR_PLUGGABLE_MSG_PROPERTY_NAME_NULL//
     = "Property name parameter is null";

    /** error message for missing prefix */
    public static final String ERROR_PLUGGABLE_MSG_PREFIX_NULL//
     = "Prefix parameter is null";

    /** error message for missing Object name */
    public static final String ERROR_PLUGGABLE_MSG_OBJECT_NULL//
     = "Object parameter is null";
    protected HelperContext aHelperContext;

    /**
     * The bean specification requires an empty constructor definition
     */
    public POJOValueStore() {
        // init properties
        properties = new HashMap();
        // use static helperContext
        aHelperContext = HelperProvider.getDefaultContext();
    }

    // pass in a specific static or instance HelperContext
    public POJOValueStore(HelperContext aContext) {
        // init properties
        properties = new HashMap();
        aHelperContext = aContext;
    }

    // implemented by subclass
    public abstract POJOValueStore getInstance(HelperContext aContext);

    /**
     * [Out of Map Interface] Pluggable Implementors must maintain a pointer to the wrapper object<br>
     * Get the DataObject that is used to wrap the embedded POJO object.<br>
     * This object is the same type as the POJO: IE: Address is wrapped by AddressImpl
     * @return
     */
    protected DataObject getDataObject() {
        return dataObject;
    }

    /**
     * [Out of Map Interface] Pluggable Implementors must maintain a pointer to the wrapper object<br>
     * Set the DataObject that is used to wrap the embedded POJO object.<br>
     * This object is the same type as the POJO: IE: Address is wrapped by AddressImpl.<br>
     * This function is called by the client during SDObject initialization as well as internally during
     * put() or get() function calls against the POJO.<br>
     * @param dataObject
     */
    protected void setDataObject(DataObject dataObject) {
        this.dataObject = dataObject;
    }

    /**
     * [Out of Map Interface] Pluggable Implementors must maintain a pointer to the wrappable embedded object<br>
     * This function returns the POJO object
     * @return Object
     */
    public Object getObject() {
        return object;
    }

    /**
     * [Out of Map Interface] Pluggable Implementors must maintain a pointer to the wrappable embedded object<br>
     * This function is called by the client during SDObject initialization as well as internally during
     * put() or get() function calls against the POJO.<br>
     * @param object
     */
    public void setObject(Object object) {
        this.object = object;
    }

    public Object getDeclaredProperty(int propertyIndex) {
        //Property property = (Property)((SDOType)dataObject.getType()).getPropertiesArray()[propertyIndex];
        return get(((Property)((SDOType)dataObject.getType()).getPropertiesArray()[propertyIndex]));
    }

    public Object getOpenContentProperty(Property property) {
        return get(property);
    }

    public void setDeclaredProperty(int propertyIndex, Object value) {
        // get property via index
        Property property = (Property)((SDOType)dataObject.getType()).getPropertiesArray()[propertyIndex];
        set(property, value, false);
    }

    public void setOpenContentProperty(Property property, Object value) {
        set(property, value, false);        
    }

    public boolean isSetDeclaredProperty(int propertyIndex) {
        Property property = (Property)((SDOType)dataObject.getType()).getPropertiesArray()[propertyIndex];
        return isSet(property);
    }

    public boolean isSetOpenContentProperty(Property property) {
        return isSet(property);
    }

    public void unsetDeclaredProperty(int propertyIndex) {
        Property property = (Property)((SDOType)dataObject.getType()).getPropertiesArray()[propertyIndex];
        unset(property);
    }

    public void unsetOpenContentProperty(Property property) {
        unset(property);
    }

    public void initialize(DataObject aDataObject) {
        dataObject = aDataObject;
    }

    /*
     * TestCase: A set of a List
     */
    public void setManyProperty(Property property, Object value) {
        set(property, value, false);
        //((SDODataObject)dataObject).updateContainment(property, (List)value);
    }

    // Map functions slated to be overridden to support Pluggable POJO's

    /**
     * Use reflection to get/set on an object<br>
     * The subclass will handle whether to update the underlying POJO.<br>
     * Constraints:
     *     The aParamObject must be a POJO or contain a POJO inside a ListWrapper
     * @param isGetMethod
     * @param aReceiverObject
     * @param methodPrefix
     * @param propertyName
     * @param aParamObject
     * @return
     */
    protected abstract Object invokeMethodPrivate(//
    boolean isMany,//
    boolean isGetMethod,//
    Object aReceiverObject,// 
    String methodPrefix,// 
    Property property,//
    Object aParamObject);

    /*
     * Prerequisites: all objects follow standard javabean method naming get/set[field name]
     * (non-Javadoc)
     * @see java.util.HashMap#get(java.lang.Object)
     */
    private Object get(Object key) {
        if (!(key instanceof Property)) {
            // TODO: Throw exception? only String keys are supported        	
            return null;
        }
        Property property = (Property)key;
        Object value = null;

        if (property != null) {
            // check map for wrapped object first
            if (properties.containsKey(property)) {
                value = properties.get(property);
            } else {
                value = invokeMethodPrivate(false, true, object, METHOD_PREFIX_FOR_GET, property, null);
                // check if we are getting a simple type
                if (property.getType().isDataType()) {
                    // save the simple type in our map
                    properties.put(property, value);
                    return value;
                } else {
                    // we are returning a complex type - check if it is many valued
                    if (property.isMany()) {
                        // The POJO list has not been wrapped by a ListWrapper
                        // We need to create a new list,
                        // iterate all the POJO's in the list,
                        // wrap them in a DataObject and add them to the new List,
                        // put the ListWrapper into this property map.
                        // create a new ListWrapper
                        ListWrapper aList = null;//new ListWrapper((SDODataObject)dataObject, property);

                        if (value != null) {
                            Iterator anIterator = ((List)value).iterator();
                            ArrayList aWrappedList = new ArrayList();
                            while (anIterator.hasNext()) {
                                Object objectToWrap = anIterator.next();
                                if (!(objectToWrap instanceof SDODataObject) &&//
                                        !(objectToWrap instanceof ListWrapper)) {
                                    // wrap object
                                    SDODataObject aWrappedObject = wrapPrivate(objectToWrap, property);
                                    aWrappedList.add(aWrappedObject);
                                } else {
                                    aWrappedList.add(objectToWrap);
                                }
                            }

                            // don't update containment while wrapping previously unset objects
                            // For non-default Pluggable ValueStore implementations
                            // The SDObjects inside this ListWrapper are special case wrappers with no previous containment
                            // We do not call updateContainment on the SDO Wrapper objects surrounding the POJO's
                            // otherwise the containment of this list will be removed in the embedded detach() call
                            // create a new ListWrapper
                            aList = new ListWrapper((SDODataObject)dataObject, property, aWrappedList);
                            //aList.addAll(false, (Collection)aWrappedList);                            
                        } else {
                            aList = new ListWrapper((SDODataObject)dataObject, property);
                        }

                        // save the wrapper in our map
                        properties.put(property, aList);
                        // return the new list
                        value = aList;
                    } else {// single DataObject
                        value = wrapPrivate(value, property);
                        // save the wrapper in our map
                        properties.put(property, value);
                    }
                }
            }
        } else {
            throw new IllegalArgumentException(ERROR_PLUGGABLE_MSG_PROPERTY_NAME_NULL);
        }
        return value;
    }

    /*
     * Unset the property in the properties map as well as the embedded POJO
     * (non-Javadoc)
     */
    private void unset(Object key) {
        // null propertyName is handled by DataObject
        if (key == null) {
            throw new IllegalArgumentException(ERROR_PLUGGABLE_MSG_PROPERTY_NAME_NULL);
        }

        /**
         * get and remove old object from map
         * See Issue: 20060831-2: References to POJO????????s are not updated without refresh after unset()
         * 1.    get a (anAddressImpl) reference to the wrapped address pojo
         * 2.    unset the address on employee
         * 3.    the embedded pojo in anAddressImpl references the old object because it was not garbage collected
         */
        Object oldSDOWrapper = properties.remove(key);

        // TODO: VERIFY: unset the POJO from the wrapper (update ext references)
        if (oldSDOWrapper instanceof SDODataObject) {
            ((POJOValueStore)((SDODataObject)oldSDOWrapper)._getCurrentValueStore()).setObject(null);
        }

        // remove object from POJO also
        if (key instanceof Property) {
            // get default value for this object
            Object defaultValue = ((Property)key).getDefault();

            // TODO: Handle primitives - wait for Nillable spec.
            set((Property)key, defaultValue, true);
        }
    }

    // overload set so that we don't cache for unset(property, field)
    private Object set(Property property, Object value, boolean calledFromUnSet) {        
            // TODO: verified issue20060822-1: empty ListWrapper's are stored as null in POJOs
            if (value instanceof List && (((List)value).size() < 1)) {
                value = null;
            }

            // null propertyName is handled by DataObject            
            Object aReturn = null;
            if (property != null) {
                // simple case
                if ((property != null) && (property.getType() != null) && property.getType().isDataType()) {
                    // save the simple type in our map
                    if (!calledFromUnSet) {
                        properties.put(property, value);
                    }

                    // Use reflection and a method pointer to set property value
                    aReturn = invokeMethodPrivate(false, false, object, METHOD_PREFIX_FOR_SET, property, value);
                } else {
                    if ((property != null) && property.isMany()) {
                        //((SDODataObject)dataObject).updateContainment(property, (List)value);
                        // Handle the collection case:
                        // We need to invoke a set on the container pojo.
                        // If the value is a ListWrapper (possibly a wrapped "default" empty or unset value)
                        // extract out the POJO as the set operand
                        // cache the wrapped object
                        if (!calledFromUnSet) {
                            properties.put(property, value);// set on first set only
                        }

                        // iterate out the SDO's from the list - pass a null value directly to the POJO
                        ArrayList aWrappedList = null;
                        if (value != null) {
                            Iterator anIterator = ((List)value).iterator();
                            aWrappedList = new ArrayList();
                            while (anIterator.hasNext()) {
                                Object objectToWrap = anIterator.next();
                                aWrappedList.add(((POJOValueStore)((SDODataObject)objectToWrap)//
                                ._getCurrentValueStore()).getObject());
                            }
                        }

                        // invoke a set on the POJO
                        aReturn = invokeMethodPrivate(true, false, object, METHOD_PREFIX_FOR_SET,//
                                                      property, aWrappedList);
                    } else {
                        // cache the wrapped object
                        if (!calledFromUnSet) {
                            properties.put(property, value);
                        }

                        // get the embeded POJO from the wrapped object - pass a null value directly to the POJO
                        Object pojoValue = null;
                        if (value != null) {
                            pojoValue = ((POJOValueStore)((SDODataObject)value)._getCurrentValueStore()).getObject();
                        }

                        // invoke a set on the POJO
                        aReturn = invokeMethodPrivate(true, false, object, METHOD_PREFIX_FOR_SET,//
                                                      property, pojoValue);
                    }
                }
            } else {
                throw new IllegalArgumentException(ERROR_PLUGGABLE_MSG_PROPERTY_NAME_NULL);
            }
            return aReturn;        
    }

    // out of interface functions

    /**
     * IsSet is checked in dataobject, if it is false we do not call get - we return the default value
     * @param anObject
     * @return
     */
    private boolean isSet(Property property) {
        // null propertyName is handled by DataObject
        boolean isSet = false;
        Object value = null;

        // check the cache first for previously set values
        // Note: this check must match the logic in set for unset calls (cache must be cleared)
        if (properties.containsKey(property)) {
            isSet = true;
        } else {
            if (property != null) {
                // integrity checking - embedded POJO should not be null
                // issue 20060830-1: isSet status of null POJO's = false
                if (object == null) {
                    // TODO: Throw exception
                    System.out.println("_POJOValueStore.isSet(" + property.getName() + ") - parent object is null");
                }

                // handle many valued isSet
                if (object instanceof List) {
                    // TODO: This needs better refactoring - check first element class
                    value = ((List)object).get(0);
                    if(value != null){
                        isSet = true;
                    }
                } else {
                    // Use reflection and a method pointer to get property value
                    value = invokeMethodPrivate(false, true, object, METHOD_PREFIX_FOR_ISSET,//
                                                property, (Object[])null);
                    // TODO: verify that isSet(property)=null -> false
                    if (value != null) {
                        isSet = true;
                    }
                }
            } else {
                throw new IllegalArgumentException(ERROR_PLUGGABLE_MSG_PROPERTY_NAME_NULL);
            }
        }
        return isSet;
    }

    /**
     * Wrap the embedded POJO object with an SDO.
     * Containment integrity is updated here without using the createDataObject wrapper
     * around a DataFactory call.
     * @param anObject
     * @param property
     * @return
     */
    private SDODataObject wrapPrivate(Object anObject, Property property) {
        SDODataObject aDataObject = null;

        // direct call to factory
        Type type = property.getType();
        if (type != null) {

            /*
            Note: when using a HelperContext other than the default contructor created static one - via the jvm parameter,
            you will need to either pass in a new POJOValueStore(aHelperContext), or
            reset the helperContext instance directly on the valueStore in order to maintain context integrity
            */

            // create DO directly do not use createDataObject->set->removeContainment->get loop
            aDataObject = (SDODataObject)aHelperContext.getDataFactory().create(type);

            // set types
            aDataObject.getType();

            // set containment
            aDataObject._setContainer(dataObject);
            aDataObject._setContainmentPropertyName(property.getName());

            ((SDODataObject)dataObject)._setModified(true);
            aDataObject._setCreated(true);
        } else {
            // TODO: throw exception
            return null;
        }

        // set changeSummary
        // The following code section overwrites the default ValueStore
        // Why? normally you would not change the ValueStore impl class in a JVM, therefore
        // we set and cache the ValueStore cache on the first read from the system property
        // in a testing environment where we test multiple implementations we need the code section below
        // NOTE: valueStore cannot be changed after first DO intitialization - the classname is a static variable on DataObject
        // directly create the valueStore (discard the default if it is a default implementation)
        try {
            POJOValueStore aPluggableMap = (POJOValueStore)Class.forName(this.getClass().getName()).newInstance();

            //aPluggableMap = (POJOValueStore)anEmployee.getValueStore();
            // set valueStore
            aDataObject._setCurrentValueStore(aPluggableMap);
        } catch (ClassNotFoundException cnfe) {
            // TODO: throw or propagate these properly
            throw new IllegalArgumentException(cnfe.getMessage());
        } catch (IllegalAccessException iae) {
            throw new IllegalArgumentException(iae.getMessage());
        } catch (InstantiationException ie) {
            throw new IllegalArgumentException(ie.getMessage());
        }

        // Proceed: set wrapped object
        ((POJOValueStore)aDataObject._getCurrentValueStore()).setObject(anObject);
        // set root DataObject
        ((POJOValueStore)aDataObject._getCurrentValueStore()).setDataObject(aDataObject);
        return aDataObject;
    }

    public ValueStore copy() {
        // Option 1: just return yourself - no ChangeSummary functionality will work with this approach
        //return this;
        // Option 2: return a shallow copy of this custom ValueStore
        POJOValueStore anOriginalValueStore = getInstance(((SDODataObject)dataObject)._getHelperContext());

        // container (of Types from XSD) for wrapping POJO        
        anOriginalValueStore.setDataObject(dataObject);
        // container for actual POJO (embedded wrappable) object        
        anOriginalValueStore.setObject(object);

        // shallow copy properties to new HashMap on original
        HashMap aHashMap = new HashMap();

        // Map of cached SDODataObject wrappers around POJO objects
        aHashMap.putAll(properties);
        anOriginalValueStore.properties = aHashMap;

        return anOriginalValueStore;
    }
}