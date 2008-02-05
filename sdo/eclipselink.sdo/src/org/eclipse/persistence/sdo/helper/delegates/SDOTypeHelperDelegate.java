/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.sdo.helper.delegates;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.sdo.helper.SDOXMLHelper;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.eclipse.persistence.exceptions.SDOException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;

/**
 * <p><b>Purpose</b>: Helper to provide access to declared SDO Types.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Look up a Type given the uri and typeName or interfaceClass.
 * <li> SDO Types are available through the getType("commonj.sdo", typeName) method.
 * <li> Defines Types from DataObjects.
 * </ul>
 */
public class SDOTypeHelperDelegate implements SDOTypeHelper {

    /** Map containing user defined types */
    private Map typesHashMap;

    /** Map containing built-in types for primitive and SDO types */
    private static final Map commonjHashMap = new HashMap();

    /** Map containing built-in types for Java types */
    private static final Map commonjJavaHashMap = new HashMap();

    /** Map containing built-in types for SDO Types keyed on Java class */
    private static final Map sdoTypeForSimpleJavaType = new HashMap();

    /** a HashMap having SDO object as key and corresponding XSD Qname Object as value */
    private static final Map sdoToXSDTypes = new HashMap();

    /** a HashMap having XSD Qname Object as Key and corresponding SDO Object as value */
    private static final Map xsdToSDOType = new HashMap();

    /** a HashMap keyed on Qname of defined open content properties */
    private Map openContentProperties;
    
    // hold the context containing all helpers so that we can preserve inter-helper relationships
    private HelperContext aHelperContext;
    private NamespaceResolver namespaceResolver;

    // create these maps once to avoid threading issues
    static {
        initCommonjHashMap();
        initCommonjJavaHashMap();
        initXsdToSDOType();
        initSdoToXSDType();
        initSDOTypeForSimpleJavaTypeMap();        
    }

    public SDOTypeHelperDelegate(HelperContext aContext) {
        // set context before initializing maps
        aHelperContext = aContext;
        initTypesHashMap();
        initOpenProps();
    }

    /**
     * initlizes built-in HashMap commonjHashMap.
     */
    private static void initCommonjHashMap() {
        commonjHashMap.put(SDOConstants.BOOLEAN, SDOConstants.SDO_BOOLEAN);
        commonjHashMap.put(SDOConstants.BYTE, SDOConstants.SDO_BYTE);
        commonjHashMap.put(SDOConstants.BYTES, SDOConstants.SDO_BYTES);
        commonjHashMap.put(SDOConstants.CHARACTER, SDOConstants.SDO_CHARACTER);
        commonjHashMap.put(SDOConstants.DATAOBJECT, SDOConstants.SDO_DATAOBJECT);
        commonjHashMap.put(SDOConstants.DATE, SDOConstants.SDO_DATE);
        commonjHashMap.put(SDOConstants.DATETIME, SDOConstants.SDO_DATETIME);
        commonjHashMap.put(SDOConstants.DAY, SDOConstants.SDO_DAY);
        commonjHashMap.put(SDOConstants.DECIMAL, SDOConstants.SDO_DECIMAL);
        commonjHashMap.put(SDOConstants.DOUBLE, SDOConstants.SDO_DOUBLE);
        commonjHashMap.put(SDOConstants.DURATION, SDOConstants.SDO_DURATION);
        commonjHashMap.put(SDOConstants.FLOAT, SDOConstants.SDO_FLOAT);
        commonjHashMap.put(SDOConstants.INT, SDOConstants.SDO_INT);
        commonjHashMap.put(SDOConstants.INTEGER, SDOConstants.SDO_INTEGER);
        commonjHashMap.put(SDOConstants.LONG, SDOConstants.SDO_LONG);
        commonjHashMap.put(SDOConstants.MONTH, SDOConstants.SDO_MONTH);
        commonjHashMap.put(SDOConstants.MONTHDAY, SDOConstants.SDO_MONTHDAY);
        commonjHashMap.put(SDOConstants.OBJECT, SDOConstants.SDO_OBJECT);// !! the Generation of Object is not sure yet !!
        commonjHashMap.put(SDOConstants.SHORT, SDOConstants.SDO_SHORT);
        commonjHashMap.put(SDOConstants.STRING, SDOConstants.SDO_STRING);
        commonjHashMap.put(SDOConstants.STRINGS, SDOConstants.SDO_STRINGS);
        commonjHashMap.put(SDOConstants.TIME, SDOConstants.SDO_TIME);
        commonjHashMap.put(SDOConstants.YEAR, SDOConstants.SDO_YEAR);
        commonjHashMap.put(SDOConstants.YEARMONTH, SDOConstants.SDO_YEARMONTH);
        commonjHashMap.put(SDOConstants.YEARMONTHDAY, SDOConstants.SDO_YEARMONTHDAY);
        commonjHashMap.put(SDOConstants.URI, SDOConstants.SDO_URI);
        commonjHashMap.put(SDOConstants.CHANGESUMMARY, SDOConstants.SDO_CHANGESUMMARY);
        commonjHashMap.put(SDOConstants.TYPE, SDOConstants.SDO_TYPE);
        commonjHashMap.put(SDOConstants.PROPERTY, SDOConstants.SDO_PROPERTY);
    }

    private static void initCommonjJavaHashMap() {
        commonjJavaHashMap.put(SDOConstants.BOOLEANOBJECT, SDOConstants.SDO_BOOLEANOBJECT);
        commonjJavaHashMap.put(SDOConstants.BYTEOBJECT, SDOConstants.SDO_BYTEOBJECT);
        commonjJavaHashMap.put(SDOConstants.CHARACTEROBJECT, SDOConstants.SDO_CHARACTEROBJECT);
        commonjJavaHashMap.put(SDOConstants.DOUBLEOBJECT, SDOConstants.SDO_DOUBLEOBJECT);
        commonjJavaHashMap.put(SDOConstants.FLOATOBJECT, SDOConstants.SDO_FLOATOBJECT);
        commonjJavaHashMap.put(SDOConstants.INTOBJECT, SDOConstants.SDO_INTOBJECT);
        commonjJavaHashMap.put(SDOConstants.LONGOBJECT, SDOConstants.SDO_LONGOBJECT);
        commonjJavaHashMap.put(SDOConstants.SHORTOBJECT, SDOConstants.SDO_SHORTOBJECT);
    }
    
    /**
     * initlizes HashMap typesHashMap.
     */
    private void initTypesHashMap() {
      typesHashMap = new HashMap();
      QName qname = new QName(SDOConstants.ORACLE_SDO_URL, SDOConstants.XMLHELPER_LOAD_OPTIONS);
      typesHashMap.put(qname, SDOConstants.SDO_XMLHELPER_LOAD_OPTIONS);
        QName customTypeQname = new QName(SDOConstants.SDO_OPEN_SEQUENCED.getURI(), SDOConstants.SDO_OPEN_SEQUENCED.getName());
        typesHashMap.put(customTypeQname, SDOConstants.SDO_OPEN_SEQUENCED);
    }

    /**
     * initlize the built-in HashMap sdoToXSDTypes
     */
    private static void initSdoToXSDType() {
        sdoToXSDTypes.put(SDOConstants.SDO_BOOLEAN, XMLConstants.BOOLEAN_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_BYTE, XMLConstants.BYTE_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_BYTES, XMLConstants.HEX_BINARY_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_CHARACTER, XMLConstants.STRING_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_DATE, XMLConstants.DATE_TIME_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_DATETIME, XMLConstants.DATE_TIME_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_DATAOBJECT, SDOConstants.ANY_TYPE_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_DAY, SDOConstants.GDAY_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_DECIMAL, XMLConstants.DECIMAL_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_DOUBLE, XMLConstants.DOUBLE_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_DURATION, SDOConstants.DURATION_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_FLOAT, XMLConstants.FLOAT_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_INT, XMLConstants.INT_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_INTEGER, XMLConstants.INTEGER_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_LONG, XMLConstants.LONG_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_MONTH, SDOConstants.GMONTH_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_MONTHDAY, SDOConstants.GMONTHDAY_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_OBJECT, XMLConstants.ANY_SIMPLE_TYPE_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_SHORT, XMLConstants.SHORT_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_STRING, XMLConstants.STRING_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_STRINGS, XMLConstants.STRING_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_TIME, XMLConstants.TIME_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_YEAR, SDOConstants.GYEAR_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_YEARMONTH, SDOConstants.GYEARMONTH_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_YEARMONTHDAY, XMLConstants.DATE_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_URI, SDOConstants.ANY_URI_QNAME);
        //add commonj.sdo/Java types
        sdoToXSDTypes.put(SDOConstants.SDO_BOOLEANOBJECT, XMLConstants.BOOLEAN_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_BYTEOBJECT, XMLConstants.BYTE_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_CHARACTEROBJECT, XMLConstants.STRING_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_DOUBLEOBJECT, XMLConstants.DOUBLE_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_FLOATOBJECT, XMLConstants.FLOAT_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_INTOBJECT, XMLConstants.INT_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_LONGOBJECT, XMLConstants.LONG_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_SHORTOBJECT, XMLConstants.SHORT_QNAME);
    }

    /**
     * initlize the built-in HashMap xsdToSDOTypes
     */
    private static void initXsdToSDOType() {
        xsdToSDOType.put(XMLConstants.ANY_SIMPLE_TYPE_QNAME, SDOConstants.SDO_OBJECT);
        xsdToSDOType.put(SDOConstants.ANY_TYPE_QNAME, SDOConstants.SDO_DATAOBJECT);
        xsdToSDOType.put(SDOConstants.ANY_URI_QNAME, SDOConstants.SDO_URI);
        xsdToSDOType.put(XMLConstants.BASE_64_BINARY_QNAME, SDOConstants.SDO_BYTES);
        xsdToSDOType.put(XMLConstants.BOOLEAN_QNAME, SDOConstants.SDO_BOOLEAN);
        xsdToSDOType.put(XMLConstants.BYTE_QNAME, SDOConstants.SDO_BYTE);
        xsdToSDOType.put(XMLConstants.DATE_QNAME, SDOConstants.SDO_YEARMONTHDAY);
        xsdToSDOType.put(XMLConstants.DATE_TIME_QNAME, SDOConstants.SDO_DATETIME);
        xsdToSDOType.put(XMLConstants.DECIMAL_QNAME, SDOConstants.SDO_DECIMAL);
        xsdToSDOType.put(XMLConstants.DOUBLE_QNAME, SDOConstants.SDO_DOUBLE);
        xsdToSDOType.put(SDOConstants.DURATION_QNAME, SDOConstants.SDO_DURATION);
        xsdToSDOType.put(SDOConstants.ENTITIES_QNAME, SDOConstants.SDO_STRINGS);
        xsdToSDOType.put(SDOConstants.ENTITY_QNAME, SDOConstants.SDO_STRING);
        xsdToSDOType.put(XMLConstants.FLOAT_QNAME, SDOConstants.SDO_FLOAT);

        xsdToSDOType.put(SDOConstants.GDAY_QNAME, SDOConstants.SDO_DAY);
        xsdToSDOType.put(SDOConstants.GMONTH_QNAME, SDOConstants.SDO_MONTH);
        xsdToSDOType.put(SDOConstants.GMONTHDAY_QNAME, SDOConstants.SDO_MONTHDAY);
        xsdToSDOType.put(SDOConstants.GYEAR_QNAME, SDOConstants.SDO_YEAR);
        xsdToSDOType.put(SDOConstants.GYEARMONTH_QNAME, SDOConstants.SDO_YEARMONTH);

        xsdToSDOType.put(XMLConstants.HEX_BINARY_QNAME, SDOConstants.SDO_BYTES);
        xsdToSDOType.put(SDOConstants.ID_QNAME, SDOConstants.SDO_STRING);
        xsdToSDOType.put(SDOConstants.IDREF_QNAME, SDOConstants.SDO_STRING);
        xsdToSDOType.put(SDOConstants.IDREFS_QNAME, SDOConstants.SDO_STRINGS);

        xsdToSDOType.put(XMLConstants.INT_QNAME, SDOConstants.SDO_INT);
        xsdToSDOType.put(XMLConstants.INTEGER_QNAME, SDOConstants.SDO_INTEGER);
        xsdToSDOType.put(SDOConstants.LANGUAGE_QNAME, SDOConstants.SDO_STRING);
        xsdToSDOType.put(XMLConstants.LONG_QNAME, SDOConstants.SDO_LONG);

        xsdToSDOType.put(SDOConstants.NAME_QNAME, SDOConstants.SDO_STRING);
        xsdToSDOType.put(SDOConstants.NCNAME_QNAME, SDOConstants.SDO_STRING);
        xsdToSDOType.put(SDOConstants.NEGATIVEINTEGER_QNAME, SDOConstants.SDO_INTEGER);

        xsdToSDOType.put(SDOConstants.NMTOKEN_QNAME, SDOConstants.SDO_STRING);

        xsdToSDOType.put(SDOConstants.NMTOKENS_QNAME, SDOConstants.SDO_STRINGS);
        xsdToSDOType.put(SDOConstants.NONNEGATIVEINTEGER_QNAME, SDOConstants.SDO_INTEGER);
        xsdToSDOType.put(SDOConstants.NONPOSITIVEINTEGER_QNAME, SDOConstants.SDO_INTEGER);
        xsdToSDOType.put(SDOConstants.NORMALIZEDSTRING_QNAME, SDOConstants.SDO_STRING);
        xsdToSDOType.put(SDOConstants.NOTATION_QNAME, SDOConstants.SDO_STRING);
        xsdToSDOType.put(SDOConstants.POSITIVEINTEGER_QNAME, SDOConstants.SDO_INTEGER);

        xsdToSDOType.put(XMLConstants.QNAME_QNAME, SDOConstants.SDO_URI);
        xsdToSDOType.put(XMLConstants.SHORT_QNAME, SDOConstants.SDO_SHORT);
        xsdToSDOType.put(XMLConstants.STRING_QNAME, SDOConstants.SDO_STRING);
        xsdToSDOType.put(XMLConstants.TIME_QNAME, SDOConstants.SDO_TIME);

        xsdToSDOType.put(SDOConstants.TOKEN_QNAME, SDOConstants.SDO_STRING);

        xsdToSDOType.put(XMLConstants.UNSIGNED_BYTE_QNAME, SDOConstants.SDO_SHORT);
        xsdToSDOType.put(XMLConstants.UNSIGNED_INT_QNAME, SDOConstants.SDO_LONG);

        xsdToSDOType.put(SDOConstants.UNSIGNEDLONG_QNAME, SDOConstants.SDO_INTEGER);

        xsdToSDOType.put(XMLConstants.UNSIGNED_SHORT_QNAME, SDOConstants.SDO_INT);
    }

    private static void initSDOTypeForSimpleJavaTypeMap() {
        sdoTypeForSimpleJavaType.put(ClassConstants.STRING, SDOConstants.SDO_STRING);
        sdoTypeForSimpleJavaType.put(ClassConstants.BOOLEAN, SDOConstants.SDO_BOOLEANOBJECT);
        sdoTypeForSimpleJavaType.put(ClassConstants.PBOOLEAN, SDOConstants.SDO_BOOLEAN);
        sdoTypeForSimpleJavaType.put(ClassConstants.BYTE, SDOConstants.SDO_BYTEOBJECT);
        sdoTypeForSimpleJavaType.put(ClassConstants.PBYTE, SDOConstants.SDO_BYTE);
        sdoTypeForSimpleJavaType.put(ClassConstants.ABYTE, SDOConstants.SDO_BYTES);
        sdoTypeForSimpleJavaType.put(ClassConstants.APBYTE, SDOConstants.SDO_BYTES);
        sdoTypeForSimpleJavaType.put(ClassConstants.CHAR, SDOConstants.SDO_CHARACTEROBJECT);
        sdoTypeForSimpleJavaType.put(ClassConstants.PCHAR, SDOConstants.SDO_CHARACTER);
        sdoTypeForSimpleJavaType.put(ClassConstants.BIGDECIMAL, SDOConstants.SDO_DECIMAL);
        sdoTypeForSimpleJavaType.put(ClassConstants.UTILDATE, SDOConstants.SDO_DATE);
        sdoTypeForSimpleJavaType.put(ClassConstants.DOUBLE, SDOConstants.SDO_DOUBLEOBJECT);
        sdoTypeForSimpleJavaType.put(ClassConstants.PDOUBLE, SDOConstants.SDO_DOUBLE);
        sdoTypeForSimpleJavaType.put(ClassConstants.FLOAT, SDOConstants.SDO_FLOATOBJECT);
        sdoTypeForSimpleJavaType.put(ClassConstants.PFLOAT, SDOConstants.SDO_FLOAT);
        sdoTypeForSimpleJavaType.put(ClassConstants.INTEGER, SDOConstants.SDO_INTOBJECT);
        sdoTypeForSimpleJavaType.put(ClassConstants.PINT, SDOConstants.SDO_INT);
        sdoTypeForSimpleJavaType.put(ClassConstants.BIGINTEGER, SDOConstants.SDO_INTEGER);
        sdoTypeForSimpleJavaType.put(ClassConstants.LONG, SDOConstants.SDO_LONGOBJECT);
        sdoTypeForSimpleJavaType.put(ClassConstants.PLONG, SDOConstants.SDO_LONG);
        sdoTypeForSimpleJavaType.put(ClassConstants.SHORT, SDOConstants.SDO_SHORTOBJECT);
        sdoTypeForSimpleJavaType.put(ClassConstants.PSHORT, SDOConstants.SDO_SHORT);
    }

    public Class getJavaWrapperTypeForSDOType(Type sdoType) {
        if (sdoType.getInstanceClass() != null) {
            return sdoType.getInstanceClass();
        }
        Class javaClass = null;
        if (sdoType.getBaseTypes() != null) {
            for (int i = 0; i < sdoType.getBaseTypes().size(); i++) {
                Type baseType = (Type)sdoType.getBaseTypes().get(i);
                javaClass = getJavaWrapperTypeForSDOType(baseType);
                if (javaClass != null) {
                    return javaClass;
                }
            }
        }
        return javaClass;
    }

    /**
     * Return the Type specified by typeName with the given uri,
     *   or null if not found.
     * @param uri The uri of the Type - type.getURI();
     * @param typeName The name of the Type - type.getName();
     * @return the Type specified by typeName with the given uri,
     *    or null if not found.
     */
    public Type getType(String uri, String typeName) {
        // for now, only the build in HashMap will be used to acquire
        // Type.a
        if (typeName == null) {
            return null;
        }

        if ((uri != null) && (uri.equals(SDOConstants.SDO_URL))) {
            return (Type)commonjHashMap.get(typeName);
        } else if ((uri != null) && (uri.equals(SDOConstants.SDOJAVA_URL))) {
            return (Type)commonjJavaHashMap.get(typeName);
        } else {
            QName qName = new QName(uri, typeName);
            return (Type)getTypesHashMap().get(qName);
        }
    }

    public Type getOrCreateType(String uri, String typeName, String xsdLocalName) {
        String lookupName = typeName;
        int index = lookupName.indexOf(':');
        if (index != -1) {
            lookupName = lookupName.substring(index + 1, lookupName.length());
        }
        Type getType = getType(uri, lookupName);
        if (getType == null) {
            SDOType newType = new SDOType(uri, lookupName, aHelperContext);
            newType.setXsd(true);
            //newType.setInstanceClass();
            newType.setXsdLocalName(xsdLocalName);
            addType(uri, lookupName, newType);
            return newType;
        }
        return getType;
    }

    private void addType(String uri, String name, Type newType) {
        if ((uri != null) && uri.equals(SDOConstants.SDO_URL)) {
            commonjHashMap.put(name, newType);
        } else if ((uri != null) && uri.equals(SDOConstants.SDOJAVA_URL)) {
            commonjJavaHashMap.put(name, newType);

        } else {
            QName qName = new QName(uri, name);
            getTypesHashMap().put(qName, newType);
        }
    }

    /**
     * Return the Type for this interfaceClass or null if not found.
     * @param interfaceClass is the interface for the DataObject's Type -
     *    type.getInstanceClass();
     * @return the Type for this interfaceClass or null if not found.
     */
    public Type getType(Class interfaceClass) {
        //TODO: possibly have a separate Map
        //TODO: what is uri for qname if package is null or targetnamespace        
        //types keyed on qname				        
        Iterator iter = getTypesHashMap().keySet().iterator();
        while (iter.hasNext()) {
            QName key = (QName)iter.next();
            Type value = (Type)getTypesHashMap().get(key);
            if (value.getInstanceClass() == interfaceClass) {
                return value;
            }
        }

        return null;
    }

    /**
     * INTERNAL:
     * Used to determine which SDO Type corresponds the given Java simple type
     */
    public Type getTypeForSimpleJavaType(Class implClass) {
        return (Type)getSDOTypeForSimpleJavaTypeMap().get(implClass);
    }

    public Type getOrCreateType(Type next) {
        Type exists = getType(next.getURI(), next.getName());
        if (exists == null) {
            addType(next.getURI(), next.getName(), next);
            return next;
        } else {
            return exists;
        }
    }
    
    public Type define(DataObject dataObject) {
        List types = new ArrayList();
        Type rootType = define(dataObject, types);
        initializeTypes(types);             
        return rootType;

    }
    
    private void initializeTypes(List types){        
        for (int i = 0; i < types.size(); i++) {
            SDOType nextType = (SDOType)types.get(i);
            if (!nextType.isDataType()) {
                nextType.postInitialize();
            }
        }        
        ((SDOXMLHelper)aHelperContext.getXMLHelper()).addDescriptors(types);
    }

    /**
     * Define the DataObject as a Type.
     * The Type is available through TypeHelper and DataGraph getType() methods.
     * @param type the DataObject representing the Type.
     * @return the defined Type.
     * @throws IllegalArgumentException if the Type could not be defined.
     */
    public Type define(DataObject dataObject, List types) {
        SDOTypeHelper typeHelper = (SDOTypeHelper)aHelperContext.getTypeHelper();
        if ((dataObject == null) || (dataObject.getType() == null) || (!dataObject.getType().getURI().equals(SDOConstants.SDO_URL)) || (!dataObject.getType().getName().equals(SDOConstants.TYPE))) {
            throw new IllegalArgumentException(SDOException.errorDefiningType());
        }

        String uri = dataObject.getString("uri");
        String name = dataObject.getString("name");

        if (name == null) {
            throw new IllegalArgumentException(SDOException.errorDefiningTypeNoName());
        }

        SDOType type = (SDOType)typeHelper.getType(uri, name);
        if (type == null) {
            type = new SDOType(uri, name, aHelperContext);
            addType(uri, name, type);
            types.add(type);
        } else {
            return type;
        }

        type.setSequenced(dataObject.getBoolean("sequenced"));
        type.setAbstract(dataObject.getBoolean("abstract"));
        type.setDataType(dataObject.getBoolean("dataType"));

        List baseTypes = dataObject.getList("baseType");
        for (int i = 0; i < baseTypes.size(); i++) {
            SDOType baseType = (SDOType)getValueFromObject(baseTypes.get(i), types);
            type.addBaseType(baseType);
        }

        List aliasNames = dataObject.getList("aliasName");
        for (int i = 0; i < aliasNames.size(); i++) {
            Object aliasName = aliasNames.get(i);
            type.getAliasNames().add(aliasName);
        }

        List openProps = ((SDODataObject)dataObject)._getOpenContentProperties();
        for (int i = 0; i < openProps.size(); i++) {
            SDOProperty nextProp = (SDOProperty)openProps.get(i);
            Object value = getValueFromObject(dataObject.get(nextProp), types);
            type.setInstanceProperty(nextProp, value);
        }

        List openPropsAttrs = ((SDODataObject)dataObject)._getOpenContentPropertiesAttributes();
        for (int i = 0; i < openPropsAttrs.size(); i++) {
            SDOProperty nextProp = (SDOProperty)openPropsAttrs.get(i);
            Object value = getValueFromObject(dataObject.get(nextProp), types);
            type.setInstanceProperty(nextProp, value);
        }

        List properties = dataObject.getList("property");

        for (int i = 0; i < properties.size(); i++) {
            Object nextValue = properties.get(i);

            if (nextValue instanceof DataObject) {
                buildPropertyFromDataObject((DataObject)nextValue, type, types);                
            }
        }
        type.setOpen(dataObject.getBoolean("open"));
        
        if (!type.isDataType()) {
            type.preInitialize(null, null);                        
        }
  
        return type;
    }

    private boolean isBaseTypeBytes(Type theType) {
        List baseTypes = theType.getBaseTypes();
        if (baseTypes.size() > 0) {
            Type nextType = (Type)baseTypes.get(0);
            if (nextType == SDOConstants.SDO_BYTES) {
                return true;
            } else {
                return isBaseTypeBytes(nextType);
            }
        }
        return false;
    }

    private Object getValueFromObject(Object objectValue, List types) {
        if (objectValue instanceof DataObject) {
            if (((DataObject)objectValue).getType() == SDOConstants.SDO_TYPE) {                
                return define((DataObject)objectValue, types);                
            }
        }
        return objectValue;
    }

    private SDOProperty buildPropertyFromDataObject(DataObject dataObject, Type containingType, List types) {
        String nameValue = dataObject.getString("name");
        Object typeObjectValue = dataObject.get("type");

        SDOProperty newProperty = new SDOProperty(aHelperContext);

        newProperty.setName(nameValue);
        Type typeValue = (Type)getValueFromObject(typeObjectValue, types);
        newProperty.setType(typeValue);

        if (typeValue != null) {
            if (typeValue == SDOConstants.SDO_BYTES) {
                newProperty.setXsdType(XMLConstants.BASE_64_BINARY_QNAME);
            } else if (typeValue.isDataType()) {
                if (isBaseTypeBytes(typeValue)) {
                    newProperty.setXsdType(XMLConstants.BASE_64_BINARY_QNAME);
                }
            }
        }

        if (dataObject.isSet("containment")) {
            newProperty.setContainment(dataObject.getBoolean("containment"));
        } else {
            if (typeValue != null) {
                newProperty.setContainment(!typeValue.isDataType());
            }
        }

        //newProperty.setContainingType(containingType);
        newProperty.setReadOnly(dataObject.getBoolean("readOnly"));
        newProperty.setMany(dataObject.getBoolean("many"));
        newProperty.setNullable(dataObject.getBoolean("nullable"));

        List aliasNames = dataObject.getList("aliasName");
        for (int i = 0; i < aliasNames.size(); i++) {
            Object aliasName = aliasNames.get(i);
            newProperty.getAliasNames().add(aliasName);
        }

        Object opposite = dataObject.get("opposite");
        if (opposite != null) {
            if (opposite instanceof Property) {
                newProperty.setOpposite((Property)opposite);
            }
        }

        // set the default only if the default on the dataObject is set
        if (dataObject.isSet("default")) {
            newProperty.setDefault(dataObject.get("default"));
        }

        List openProps = ((SDODataObject)dataObject)._getOpenContentProperties();
        for (int i = 0; i < openProps.size(); i++) {
            SDOProperty nextProp = (SDOProperty)openProps.get(i);
            Object value = getValueFromObject(dataObject.get(nextProp), types);
            newProperty.setInstanceProperty(nextProp, value);
        }

        List openPropsAttrs = ((SDODataObject)dataObject)._getOpenContentPropertiesAttributes();
        for (int i = 0; i < openPropsAttrs.size(); i++) {
            SDOProperty nextProp = (SDOProperty)openPropsAttrs.get(i);
            Object value = getValueFromObject(dataObject.get(nextProp), types);
            newProperty.setInstanceProperty(nextProp, value);
        }

        //verify that this property has a type set bug 5768381
        if (newProperty.getType() == null) {
            throw SDOException.noTypeSpecifiedForProperty(newProperty.getName());
        }

        if (containingType != null) {
            ((SDOType)containingType).addDeclaredProperty(newProperty);
            if (aHelperContext.getXSDHelper().isElement(newProperty)) {
                newProperty.setNamespaceQualified(true);
            }
            newProperty.buildMapping(containingType.getURI());
        }

        return newProperty;
    }

    /**
     * Define the list of DataObjects as Types.
     * The Types are available through TypeHelper and DataGraph getType() methods.
     * @param types a List of DataObjects representing the Types.
     * @return the defined Types.
     * @throws IllegalArgumentException if the Types could not be defined.
     */
    public List define(List types) {
        List definedTypes = new ArrayList();
        for (int i = 0; i < types.size(); i++) {
            Type definedType = define((DataObject)types.get(i));
            definedTypes.add(definedType);
        }
        return definedTypes;
    }

    /**
     * A function to access the values stored in xsdToSDOType HashMap
     * @param aType    a SDO Type Object
     * @return         the corresponding  XSD QName Object
     */
    public QName getXSDTypeFromSDOType(Type aType) {
        return (QName)sdoToXSDTypes.get(aType);
    }

    /**
     * A function to access the values stored in sdoToXSDTypes HashMap
     * @param aName
     * @return
     */
    public SDOType getSDOTypeFromXSDType(QName aName) {
        return (SDOType)xsdToSDOType.get(aName);
    }

    public void setTypesHashMap(Map typesHashMap) {
        this.typesHashMap = typesHashMap;
    }

    public Map getTypesHashMap() {
        if (typesHashMap == null) {
            initTypesHashMap();
        }
        return typesHashMap;
    }

    public void reset() {
        initTypesHashMap();
        namespaceResolver = new NamespaceResolver();
        initOpenProps();
        SDOConstants.SDO_OPEN_SEQUENCED.getXmlDescriptor().setNamespaceResolver(null);
    }

    /**
    Define the DataObject as a Property for setting open content.
    The new property or, if already defined, an existing property is returned.
    The containing Type of the open property is not specified by SDO.
    If the specified uri is not null the defined property is accessible through
    TypeHelper.getOpenProperty(uri, propertyName).
    If a null uri is specified, the location and management of the open property
    is not specified by SDO.
    @param uri the namespace URI of the open Property or null.
    @return the defined open Property.
    @throws IllegalArgumentException if the Property could not be defined.
    */
    public Property defineOpenContentProperty(String uri, DataObject propertyDO) {
        String name = propertyDO.getString("name");

        Object propertyToReturn = aHelperContext.getXSDHelper().getGlobalProperty(uri, name, true);
        if (propertyToReturn == null) {
            propertyToReturn = aHelperContext.getXSDHelper().getGlobalProperty(uri, name, false);
        }

        if ((propertyToReturn == null) || (!(propertyToReturn instanceof Property))) {            
            List types = new ArrayList();
            propertyToReturn = buildPropertyFromDataObject(propertyDO, null, types);
            initializeTypes(types);
            defineOpenContentProperty(uri, name, (Property)propertyToReturn);
        }

        return (Property)propertyToReturn;
    }

    /**
     * INTERNAL:
     * @param propertyQName
     * @param property
     */
    private void defineOpenContentProperty(String propertyUri, String propertyName, Property property) {
        if (propertyUri != null) {            
            QName propertyQName = new QName(propertyUri, propertyName);            
            openContentProperties.put(propertyQName, property);
            
            boolean isElement = aHelperContext.getXSDHelper().isElement(property);            
            ((SDOXSDHelper)aHelperContext.getXSDHelper()).addGlobalProperty(propertyQName, property, isElement);
            

            ((SDOProperty)property).setUri(propertyUri);
            XMLDescriptor aDescriptor = ((SDOType)property.getType()).getXmlDescriptor();

            // synchronized threads that access the NonSynchronizedVector tables in XMLDescriptor 
            if (aDescriptor != null) {
                synchronized (aDescriptor) {
                    String rootName = propertyName;
                    String prefix = aDescriptor.getNonNullNamespaceResolver().resolveNamespaceURI(propertyUri);
                    if ((prefix == null) || prefix.equals(SDOConstants.EMPTY_STRING)) {
                        prefix = ((SDOTypeHelper)aHelperContext.getTypeHelper()).getPrefix(propertyUri);
                        aDescriptor.getNonNullNamespaceResolver().put(prefix, propertyUri);
                    }
                    if ((prefix != null) && !prefix.equals(SDOConstants.EMPTY_STRING)) {
                        rootName = prefix + ":" + rootName;
                    }
                    aDescriptor.setDefaultRootElement(rootName);
                    QName elementType = new QName(propertyUri, rootName);
                    aDescriptor.setDefaultRootElementType(elementType);

                    ((SDOXMLHelper)aHelperContext.getXMLHelper()).getXmlContext().storeXMLDescriptorByQName(aDescriptor);
                }
            }
        }
    }

    /**
    Get the open Property with the specified uri and name, or null if
    not found.
    @param uri the namespace URI of the open Property.
    @param propertyName the name of the open Property.
    @return the open Property.
    */
    public Property getOpenContentProperty(String uri, String propertyName) {    
        QName qname = new QName(uri, propertyName);
        return (Property)openContentProperties.get(qname);
    }

    /**
     * INTERNAL:
     * @return
     */
    private static Map getSDOTypeForSimpleJavaTypeMap() {
        return sdoTypeForSimpleJavaType;
    }

    /**
     * INTERNAL:
     * Return the current helperContext associated with this delegate.
     */
    public HelperContext getHelperContext() {
        return aHelperContext;
    }

    /**
     * INTERNAL:
     * Set the current helperContext to be associated with this delegate
     */
    public void setHelperContext(HelperContext helperContext) {
        aHelperContext = helperContext;
    }

    /**
     * INTERNAL:
     * Return the prefix for the given uri, or generate a new one if necessary
     */
    public String getPrefix(String uri) {
        if (uri == null) {
            return null;
        }
        NamespaceResolver nr = getNamespaceResolver();
        String existingPrefixForUri = nr.resolveNamespaceURI(uri);
        if ((existingPrefixForUri == null) || existingPrefixForUri.equals("")) {
            //doesn't exist so generate one          
            //String newPrefix = nr.generatePrefix();
            String newPrefix = generatePrefix(uri);
            nr.put(newPrefix, uri);
            return newPrefix;
        } else {
            //exists so return
            return existingPrefixForUri;
        }
    }

    private String generatePrefix(String uri) {
        NamespaceResolver nr = getNamespaceResolver();
        if (uri.equals(SDOConstants.SDO_URL)) {
            return nr.generatePrefix("sdo");
        } else if (uri.equals(SDOConstants.SDOXML_URL)) {
            return nr.generatePrefix("sdoXML");
        } else if (uri.equals(SDOConstants.SDOJAVA_URL)) {
            return nr.generatePrefix("sdoJava");
        } else {
            return nr.generatePrefix();
        }
    }

    /**
      * INTERNAL:
      * Add the given namespace uri and prefix to the global namespace resolver.
      */
    public String addNamespace(String prefix, String uri) {
        NamespaceResolver nr = getNamespaceResolver();

        String existingPrefixForURI = nr.resolveNamespaceURI(uri);
        if ((existingPrefixForURI != null) && !existingPrefixForURI.equals("")) {
            //if there is already a prefix for this uri return that one
            return existingPrefixForURI;
        }
        String existingUriForPrefix = nr.resolveNamespacePrefix(prefix);
        if (existingUriForPrefix == null) {
            //prefix is not already used and uri doesnt have a prefix so use the prefix passed in 
            nr.put(prefix, uri);
            return prefix;
        } else {
            //this prefix is already used for a different uri so generate a new one               
            prefix = generatePrefix(uri);
            nr.put(prefix, uri);
            return prefix;
        }
    }

    /**
    * INTERNAL:
    * Return the NamespaceResolver
    */
    public NamespaceResolver getNamespaceResolver() {
        if (namespaceResolver == null) {
            namespaceResolver = new NamespaceResolver();
        }
        return namespaceResolver;
    }
    
   /**
    * INTERNAL:
    * Return the Map of Open Content Properties
    */
    public Map getOpenContentProperties(){
      return openContentProperties;
    }
    
    private void initOpenProps() {
        openContentProperties = new HashMap();
        openContentProperties.put(SDOConstants.MIME_TYPE_QNAME, SDOConstants.MIME_TYPE_PROPERTY);
        openContentProperties.put(SDOConstants.MIME_TYPE_PROPERTY_QNAME, SDOConstants.MIME_TYPE_PROPERTY_PROPERTY);
        openContentProperties.put(SDOConstants.SCHEMA_TYPE_QNAME, SDOConstants.XML_SCHEMA_TYPE_PROPERTY);
        openContentProperties.put(SDOConstants.JAVA_CLASS_QNAME, SDOConstants.JAVA_CLASS_PROPERTY);
        openContentProperties.put(SDOConstants.XML_ELEMENT_QNAME, SDOConstants.XMLELEMENT_PROPERTY);
        openContentProperties.put(SDOConstants.XML_DATATYPE_QNAME, SDOConstants.XMLDATATYPE_PROPERTY);
        openContentProperties.put(SDOConstants.XML_ID_PROPERTY_QNAME, SDOConstants.ID_PROPERTY);
        openContentProperties.put(SDOConstants.DOCUMENTATION_PROPERTY_QNAME, SDOConstants.DOCUMENTATION_PROPERTY);
    }
}