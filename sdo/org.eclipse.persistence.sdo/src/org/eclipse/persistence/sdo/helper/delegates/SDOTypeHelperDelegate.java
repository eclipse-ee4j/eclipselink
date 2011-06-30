/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
import org.eclipse.persistence.sdo.types.SDOWrapperType;
import org.eclipse.persistence.sdo.types.SDOXMLHelperLoadOptionsType;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.sdo.helper.SDOXMLHelper;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sdo.types.*;
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

    /** Map containing wrapper types */
    private Map<QName, SDOType> wrappersHashMap;

    /** Map of interfaces -> SDOType */
    private Map<Class, SDOType> interfacesToSDOTypeHashMap;

    /** Map of impl classes -> SDOType */
    private Map<Class, SDOType> implClassesToSDOType = new HashMap<Class, SDOType>();

    /** Map containing built-in types for primitive and SDO types */
    private final Map commonjHashMap = new HashMap();

    /** Map containing built-in types for Java types */
    private final Map commonjJavaHashMap = new HashMap();

    /** Map containing built-in types for SDO Types keyed on Java class */
    private static final Map sdoTypeForSimpleJavaType = new HashMap(22);

    /** a HashMap having SDO object as key and corresponding XSD Qname Object as value */
    private final Map sdoToXSDTypes = new HashMap(34);

    /** a HashMap having XSD Qname Object as Key and corresponding SDO Object as value */
    private final Map xsdToSDOType = new HashMap(46);

    /** a HashMap keyed on Qname of defined open content properties */
    private Map openContentProperties;

    /** a List of Anonymous Types */
    private List anonymousTypes;

    // hold the context containing all helpers so that we can preserve inter-helper relationships
    private HelperContext aHelperContext;
    private NamespaceResolver namespaceResolver;

    // SDO Data Types
    private static SDOWrapperType SDO_BOOLEAN_WRAPPER;
    private static SDOWrapperType SDO_BYTE_WRAPPER;
    private static SDOWrapperType SDO_BYTES_WRAPPER;
    private static SDOWrapperType SDO_CHARACTER_WRAPPER;
    private static SDOWrapperType SDO_DATE_WRAPPER;
    private static SDOWrapperType SDO_DATETIME_WRAPPER;
    private static SDOWrapperType SDO_DAY_WRAPPER;
    private static SDOWrapperType SDO_DECIMAL_WRAPPER;
    private static SDOWrapperType SDO_DOUBLE_WRAPPER;
    private static SDOWrapperType SDO_DURATION_WRAPPER;
    private static SDOWrapperType SDO_FLOAT_WRAPPER;
    private static SDOWrapperType SDO_INT_WRAPPER;
    private static SDOWrapperType SDO_INTEGER_WRAPPER;
    private static SDOWrapperType SDO_LONG_WRAPPER;
    private static SDOWrapperType SDO_MONTH_WRAPPER;
    private static SDOWrapperType SDO_MONTHDAY_WRAPPER;
    private static SDOWrapperType SDO_OBJECT_WRAPPER;
    private static SDOWrapperType SDO_SHORT_WRAPPER;
    private static SDOWrapperType SDO_STRING_WRAPPER;
    private static SDOWrapperType SDO_STRINGS_WRAPPER;
    private static SDOWrapperType SDO_TIME_WRAPPER;
    private static SDOWrapperType SDO_URI_WRAPPER;
    private static SDOWrapperType SDO_YEAR_WRAPPER;
    private static SDOWrapperType SDO_YEARMONTH_WRAPPER;
    private static SDOWrapperType SDO_YEARMONTHDAY_WRAPPER;

    private static SDOWrapperType SDO_BOOLEANOBJECT_WRAPPER;
    private static SDOWrapperType SDO_BYTEOBJECT_WRAPPER;
    private static SDOWrapperType SDO_CHARACTEROBJECT_WRAPPER;
    private static SDOWrapperType SDO_DOUBLEOBJECT_WRAPPER;
    private static SDOWrapperType SDO_FLOATOBJECT_WRAPPER;
    private static SDOWrapperType SDO_INTOBJECT_WRAPPER;
    private static SDOWrapperType SDO_LONGOBJECT_WRAPPER;
    private static SDOWrapperType SDO_SHORTOBJECT_WRAPPER;

    // create these maps once to avoid threading issues
    static {
        initSDOTypeForSimpleJavaTypeMap();
    }

    public SDOTypeHelperDelegate(HelperContext aContext) {
        // set context before initializing maps
        aHelperContext = aContext;

        initCommonjHashMap();
        initCommonjJavaHashMap();
        
        reset();
    }

    public void initWrapperTypes() {
        // List of SDO Types and mappings to XSD Types taken from:
        // http://www.osoa.org/download/attachments/36/Java-SDO-Spec-v2.1.0-FINAL.pdf?version=1
        if (null == SDO_BOOLEAN_WRAPPER) {
            SDO_BOOLEAN_WRAPPER = new SDOWrapperType(SDOConstants.SDO_BOOLEAN, SDOConstants.BOOLEAN, this, XMLConstants.BOOLEAN_QNAME, SDOWrapperType.BooleanWrapperImpl.class);
            SDO_BYTE_WRAPPER = new SDOWrapperType(SDOConstants.SDO_BYTE, SDOConstants.BYTE, this, XMLConstants.BYTE_QNAME, SDOWrapperType.ByteWrapperImpl.class);
            SDO_BYTES_WRAPPER = new SDOWrapperType(SDOConstants.SDO_BYTES, SDOConstants.BYTES, this, new QName[] { XMLConstants.BASE_64_BINARY_QNAME, XMLConstants.HEX_BINARY_QNAME }, new Class[] {SDOWrapperType.BytesWrapperImpl.class, SDOWrapperType.Bytes_hexBunaryWrapperImpl.class});
            SDO_CHARACTER_WRAPPER = new SDOWrapperType(SDOConstants.SDO_CHARACTER, SDOConstants.CHARACTER, this, XMLConstants.STRING_QNAME, SDOWrapperType.CharacterWrapperImpl.class);
            SDO_DATE_WRAPPER = new SDOWrapperType(SDOConstants.SDO_DATE, SDOConstants.DATE, this, XMLConstants.DATE_QNAME, SDOWrapperType.DateWrapperImpl.class);
            SDO_DATETIME_WRAPPER = new SDOWrapperType(SDOConstants.SDO_DATETIME, SDOConstants.DATETIME, this, XMLConstants.DATE_TIME_QNAME, SDOWrapperType.DateTimeWrapperImpl.class);
            SDO_DAY_WRAPPER = new SDOWrapperType(SDOConstants.SDO_DAY, SDOConstants.DAY, this, XMLConstants.G_DAY_QNAME, SDOWrapperType.DayWrapperImpl.class);
            SDO_DECIMAL_WRAPPER = new SDOWrapperType(SDOConstants.SDO_DECIMAL, SDOConstants.DECIMAL, this, XMLConstants.DECIMAL_QNAME, SDOWrapperType.DecimalWrapperImpl.class);
            SDO_DOUBLE_WRAPPER = new SDOWrapperType(SDOConstants.SDO_DOUBLE, SDOConstants.DOUBLE, this, XMLConstants.DOUBLE_QNAME, SDOWrapperType.DoubleWrapperImpl.class);
            SDO_DURATION_WRAPPER = new SDOWrapperType(SDOConstants.SDO_DURATION, SDOConstants.DURATION, this, XMLConstants.DURATION_QNAME, SDOWrapperType.DurationWrapperImpl.class);
            SDO_FLOAT_WRAPPER = new SDOWrapperType(SDOConstants.SDO_FLOAT, SDOConstants.FLOAT, this, XMLConstants.FLOAT_QNAME, SDOWrapperType.FloatWrapperImpl.class);
            SDO_INT_WRAPPER = new SDOWrapperType(SDOConstants.SDO_INT, SDOConstants.INT, this, XMLConstants.INT_QNAME, SDOWrapperType.IntWrapperImpl.class);
            SDO_INTEGER_WRAPPER = new SDOWrapperType(SDOConstants.SDO_INTEGER, SDOConstants.INTEGER, this, XMLConstants.INTEGER_QNAME, SDOWrapperType.IntegerWrapperImpl.class);
            SDO_LONG_WRAPPER = new SDOWrapperType(SDOConstants.SDO_LONG, SDOConstants.LONG, this, XMLConstants.LONG_QNAME, SDOWrapperType.LongWrapperImpl.class);
            SDO_MONTH_WRAPPER = new SDOWrapperType(SDOConstants.SDO_MONTH, SDOConstants.MONTH, this, XMLConstants.G_MONTH_QNAME, SDOWrapperType.MonthWrapperImpl.class);
            SDO_MONTHDAY_WRAPPER = new SDOWrapperType(SDOConstants.SDO_MONTHDAY, SDOConstants.MONTHDAY, this, XMLConstants.G_MONTH_DAY_QNAME, SDOWrapperType.MonthDayWrapperImpl.class);
            SDO_OBJECT_WRAPPER = new SDOWrapperType(SDOConstants.SDO_OBJECT, SDOConstants.OBJECT, this, XMLConstants.ANY_SIMPLE_TYPE_QNAME, SDOWrapperType.ObjectWrapperImpl.class);
            SDO_SHORT_WRAPPER = new SDOWrapperType(SDOConstants.SDO_SHORT, SDOConstants.SHORT, this, XMLConstants.SHORT_QNAME, SDOWrapperType.ShortWrapperImpl.class);
            SDO_STRING_WRAPPER = new SDOWrapperType(SDOConstants.SDO_STRING, SDOConstants.STRING, this, XMLConstants.STRING_QNAME, SDOWrapperType.StringWrapperImpl.class);
            SDO_STRINGS_WRAPPER = new SDOWrapperType(SDOConstants.SDO_STRINGS, SDOConstants.STRINGS, this, XMLConstants.STRING_QNAME, SDOWrapperType.StringsWrapperImpl.class);
            SDO_TIME_WRAPPER = new SDOWrapperType(SDOConstants.SDO_TIME, SDOConstants.TIME, this, XMLConstants.TIME_QNAME, SDOWrapperType.TimeWrapperImpl.class);
            SDO_URI_WRAPPER = new SDOWrapperType(SDOConstants.SDO_URI, SDOConstants.URI, this, new QName[] {XMLConstants.ANY_URI_QNAME, XMLConstants.QNAME_QNAME}, new Class[] {SDOWrapperType.URIWrapperImpl.class, SDOWrapperType.URI_QNameWrapperImpl.class});
            SDO_YEAR_WRAPPER = new SDOWrapperType(SDOConstants.SDO_YEAR, SDOConstants.YEAR, this, XMLConstants.G_YEAR_QNAME, SDOWrapperType.YearWrapperImpl.class);
            SDO_YEARMONTH_WRAPPER = new SDOWrapperType(SDOConstants.SDO_YEARMONTH, SDOConstants.YEARMONTH, this, XMLConstants.G_YEAR_MONTH_QNAME, SDOWrapperType.YearMonthWrapperImpl.class);
            SDO_YEARMONTHDAY_WRAPPER = new SDOWrapperType(SDOConstants.SDO_YEARMONTHDAY, SDOConstants.YEARMONTHDAY, this, XMLConstants.DATE_QNAME, SDOWrapperType.YearMonthDayWrapperImpl.class);
            SDO_BOOLEANOBJECT_WRAPPER = new SDOWrapperType(SDOConstants.SDO_BOOLEANOBJECT, SDOConstants.BOOLEANOBJECT, this, XMLConstants.BOOLEAN_QNAME, SDOWrapperType.BooleanObjectWrapperImpl.class);
            SDO_BYTEOBJECT_WRAPPER = new SDOWrapperType(SDOConstants.SDO_BYTEOBJECT, SDOConstants.BYTEOBJECT, this, XMLConstants.BYTE_QNAME, SDOWrapperType.ByteObjectWrapperImpl.class);
            SDO_CHARACTEROBJECT_WRAPPER = new SDOWrapperType(SDOConstants.SDO_CHARACTEROBJECT, SDOConstants.CHARACTEROBJECT, this, XMLConstants.STRING_QNAME, SDOWrapperType.CharacterObjectWrapperImpl.class);
            SDO_DOUBLEOBJECT_WRAPPER = new SDOWrapperType(SDOConstants.SDO_DOUBLEOBJECT, SDOConstants.DOUBLEOBJECT, this, XMLConstants.DOUBLE_QNAME, SDOWrapperType.DoubleObjectWrapperImpl.class);
            SDO_FLOATOBJECT_WRAPPER = new SDOWrapperType(SDOConstants.SDO_FLOATOBJECT, SDOConstants.FLOATOBJECT, this, XMLConstants.FLOAT_QNAME, SDOWrapperType.FloatObjectWrapperImpl.class);
            SDO_INTOBJECT_WRAPPER = new SDOWrapperType(SDOConstants.SDO_INTOBJECT, SDOConstants.INTOBJECT, this, XMLConstants.INT_QNAME, SDOWrapperType.IntObjectWrapperImpl.class);
            SDO_LONGOBJECT_WRAPPER = new SDOWrapperType(SDOConstants.SDO_LONGOBJECT, SDOConstants.LONGOBJECT, this, XMLConstants.LONG_QNAME, SDOWrapperType.LongObjectWrapperImpl.class);
            SDO_SHORTOBJECT_WRAPPER = new SDOWrapperType(SDOConstants.SDO_SHORTOBJECT, SDOConstants.SHORTOBJECT, this, XMLConstants.SHORT_QNAME, SDOWrapperType.ShortObjectWrapperImpl.class);
        }
    }

    public void addWrappersToProject(Project project) {
        SDO_BOOLEAN_WRAPPER.addDescriptorToProject(project);
        SDO_BYTE_WRAPPER.addDescriptorToProject(project);
        SDO_BYTES_WRAPPER.addDescriptorToProject(project);
        SDO_CHARACTER_WRAPPER.addDescriptorToProject(project);
        SDO_DATE_WRAPPER.addDescriptorToProject(project);
        SDO_DAY_WRAPPER.addDescriptorToProject(project);
        SDO_DECIMAL_WRAPPER.addDescriptorToProject(project);
        SDO_DOUBLE_WRAPPER.addDescriptorToProject(project);
        SDO_DURATION_WRAPPER.addDescriptorToProject(project);
        SDO_FLOAT_WRAPPER.addDescriptorToProject(project);
        SDO_INT_WRAPPER.addDescriptorToProject(project);
        SDO_INTEGER_WRAPPER.addDescriptorToProject(project);
        SDO_LONG_WRAPPER.addDescriptorToProject(project);
        SDO_MONTH_WRAPPER.addDescriptorToProject(project);
        SDO_MONTHDAY_WRAPPER.addDescriptorToProject(project);
        SDO_OBJECT_WRAPPER.addDescriptorToProject(project);
        SDO_SHORT_WRAPPER.addDescriptorToProject(project);
        SDO_STRINGS_WRAPPER.addDescriptorToProject(project);
        SDO_TIME_WRAPPER.addDescriptorToProject(project);
        SDO_URI_WRAPPER.addDescriptorToProject(project);
        SDO_YEAR_WRAPPER.addDescriptorToProject(project);
        SDO_YEARMONTH_WRAPPER.addDescriptorToProject(project);
        SDO_YEARMONTHDAY_WRAPPER.addDescriptorToProject(project);

        SDO_STRING_WRAPPER.addDescriptorToProject(project);
        SDO_DATETIME_WRAPPER.addDescriptorToProject(project);        
        
        SDO_BOOLEANOBJECT_WRAPPER.addDescriptorToProject(project);
        SDO_BYTEOBJECT_WRAPPER.addDescriptorToProject(project);
        SDO_CHARACTEROBJECT_WRAPPER.addDescriptorToProject(project);
        SDO_DOUBLEOBJECT_WRAPPER.addDescriptorToProject(project);
        SDO_FLOATOBJECT_WRAPPER.addDescriptorToProject(project);
        SDO_INTOBJECT_WRAPPER.addDescriptorToProject(project);
        SDO_LONGOBJECT_WRAPPER.addDescriptorToProject(project);
        SDO_SHORTOBJECT_WRAPPER.addDescriptorToProject(project);
    }

    /**
     * initializes built-in HashMap commonjHashMap.
     */
    private void initCommonjHashMap() {
        commonjHashMap.put(SDOConstants.BOOLEAN, SDOConstants.SDO_BOOLEAN);
        commonjHashMap.put(SDOConstants.BYTE, SDOConstants.SDO_BYTE);
        commonjHashMap.put(SDOConstants.BYTES, SDOConstants.SDO_BYTES);
        commonjHashMap.put(SDOConstants.CHARACTER, SDOConstants.SDO_CHARACTER);

        SDOType dataObjectType = new SDODataObjectType(this);
        commonjHashMap.put(dataObjectType.getName(), dataObjectType);

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

        SDOType changeSummaryType = new SDOChangeSummaryType(this);
        commonjHashMap.put(changeSummaryType.getName(), changeSummaryType);

        SDOType typeType = new SDOTypeType(this);
        commonjHashMap.put(typeType.getName(), typeType);
    }

    private void initCommonjJavaHashMap() {
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
     * initializes HashMap typesHashMap.
     */
    private void initTypesHashMap() {
        typesHashMap = new HashMap();

        SDOType typeType = this.getType(SDOConstants.SDO_URL, SDOConstants.TYPE);

        SDOType xmlHelperLoadOptionsType = new SDOXMLHelperLoadOptionsType(this, typeType);
        typesHashMap.put(xmlHelperLoadOptionsType.getQName(), xmlHelperLoadOptionsType);

        SDOType openSequencedType = new SDOOpenSequencedType(this);
        typesHashMap.put(openSequencedType.getQName(), openSequencedType);
        implClassesToSDOType.put(openSequencedType.getImplClass(), openSequencedType);

        typesHashMap.put(SDO_BOOLEAN_WRAPPER.getQName(), SDO_BOOLEAN_WRAPPER);
        typesHashMap.put(SDO_BYTE_WRAPPER.getQName(), SDO_BYTE_WRAPPER);
        typesHashMap.put(SDO_BYTES_WRAPPER.getQName(), SDO_BYTES_WRAPPER);
        typesHashMap.put(SDO_CHARACTER_WRAPPER.getQName(), SDO_CHARACTER_WRAPPER);
        typesHashMap.put(SDO_DATE_WRAPPER.getQName(), SDO_DATE_WRAPPER);
        typesHashMap.put(SDO_DATETIME_WRAPPER.getQName(), SDO_DATETIME_WRAPPER);
        typesHashMap.put(SDO_DAY_WRAPPER.getQName(), SDO_DAY_WRAPPER);
        typesHashMap.put(SDO_DECIMAL_WRAPPER.getQName(), SDO_DECIMAL_WRAPPER);
        typesHashMap.put(SDO_DOUBLE_WRAPPER.getQName(), SDO_DOUBLE_WRAPPER);
        typesHashMap.put(SDO_DURATION_WRAPPER.getQName(), SDO_DURATION_WRAPPER);
        typesHashMap.put(SDO_FLOAT_WRAPPER.getQName(), SDO_FLOAT_WRAPPER);
        typesHashMap.put(SDO_INT_WRAPPER.getQName(), SDO_INT_WRAPPER);
        typesHashMap.put(SDO_INTEGER_WRAPPER.getQName(), SDO_INTEGER_WRAPPER);
        typesHashMap.put(SDO_LONG_WRAPPER.getQName(), SDO_LONG_WRAPPER);
        typesHashMap.put(SDO_MONTH_WRAPPER.getQName(), SDO_MONTH_WRAPPER);
        typesHashMap.put(SDO_MONTHDAY_WRAPPER.getQName(), SDO_MONTHDAY_WRAPPER);
        typesHashMap.put(SDO_OBJECT_WRAPPER.getQName(), SDO_OBJECT_WRAPPER);
        typesHashMap.put(SDO_SHORT_WRAPPER.getQName(), SDO_SHORT_WRAPPER);
        typesHashMap.put(SDO_STRING_WRAPPER.getQName(), SDO_STRING_WRAPPER);
        typesHashMap.put(SDO_STRINGS_WRAPPER.getQName(), SDO_STRINGS_WRAPPER);
        typesHashMap.put(SDO_TIME_WRAPPER.getQName(), SDO_TIME_WRAPPER);
        typesHashMap.put(SDO_URI_WRAPPER.getQName(), SDO_URI_WRAPPER);
        typesHashMap.put(SDO_YEAR_WRAPPER.getQName(), SDO_YEAR_WRAPPER);
        typesHashMap.put(SDO_YEARMONTH_WRAPPER.getQName(), SDO_YEARMONTH_WRAPPER);
        typesHashMap.put(SDO_YEARMONTHDAY_WRAPPER.getQName(), SDO_YEARMONTHDAY_WRAPPER);

        typesHashMap.put(SDO_BOOLEANOBJECT_WRAPPER.getQName(), SDO_BOOLEANOBJECT_WRAPPER);
        typesHashMap.put(SDO_BYTEOBJECT_WRAPPER.getQName(), SDO_BYTEOBJECT_WRAPPER);
        typesHashMap.put(SDO_CHARACTEROBJECT_WRAPPER.getQName(), SDO_CHARACTEROBJECT_WRAPPER);
        typesHashMap.put(SDO_DOUBLEOBJECT_WRAPPER.getQName(), SDO_DOUBLEOBJECT_WRAPPER);
        typesHashMap.put(SDO_FLOATOBJECT_WRAPPER.getQName(), SDO_FLOATOBJECT_WRAPPER);
        typesHashMap.put(SDO_INTOBJECT_WRAPPER.getQName(), SDO_INTOBJECT_WRAPPER);
        typesHashMap.put(SDO_LONGOBJECT_WRAPPER.getQName(), SDO_LONGOBJECT_WRAPPER);
        typesHashMap.put(SDO_SHORTOBJECT_WRAPPER.getQName(), SDO_SHORTOBJECT_WRAPPER);
    }

    /**
     * Initializes the wrappersHashMap HashMap.
     */
    private void initWrappersHashMap() {
        wrappersHashMap = new HashMap<QName, SDOType>();

        wrappersHashMap.put(SDOConstants.SDO_BOOLEAN.getQName(), SDO_BOOLEAN_WRAPPER);
        wrappersHashMap.put(SDOConstants.SDO_BYTE.getQName(), SDO_BYTE_WRAPPER);
        wrappersHashMap.put(SDOConstants.SDO_BYTES.getQName(), SDO_BYTES_WRAPPER);
        wrappersHashMap.put(SDOConstants.SDO_CHARACTER.getQName(), SDO_CHARACTER_WRAPPER);
        wrappersHashMap.put(SDOConstants.SDO_DATE.getQName(), SDO_DATE_WRAPPER);
        wrappersHashMap.put(SDOConstants.SDO_DATETIME.getQName(), SDO_DATETIME_WRAPPER);
        wrappersHashMap.put(SDOConstants.SDO_DAY.getQName(), SDO_DAY_WRAPPER);
        wrappersHashMap.put(SDOConstants.SDO_DECIMAL.getQName(), SDO_DECIMAL_WRAPPER);
        wrappersHashMap.put(SDOConstants.SDO_DOUBLE.getQName(), SDO_DOUBLE_WRAPPER);
        wrappersHashMap.put(SDOConstants.SDO_DURATION.getQName(), SDO_DURATION_WRAPPER);
        wrappersHashMap.put(SDOConstants.SDO_FLOAT.getQName(), SDO_FLOAT_WRAPPER);
        wrappersHashMap.put(SDOConstants.SDO_INT.getQName(), SDO_INT_WRAPPER);
        wrappersHashMap.put(SDOConstants.SDO_INTEGER.getQName(), SDO_INTEGER_WRAPPER);
        wrappersHashMap.put(SDOConstants.SDO_LONG.getQName(), SDO_LONG_WRAPPER);
        wrappersHashMap.put(SDOConstants.SDO_MONTH.getQName(), SDO_MONTH_WRAPPER);
        wrappersHashMap.put(SDOConstants.SDO_MONTHDAY.getQName(), SDO_MONTHDAY_WRAPPER);
        wrappersHashMap.put(SDOConstants.SDO_OBJECT.getQName(), SDO_OBJECT_WRAPPER);
        wrappersHashMap.put(SDOConstants.SDO_SHORT.getQName(), SDO_SHORT_WRAPPER);
        wrappersHashMap.put(SDOConstants.SDO_STRING.getQName(), SDO_STRING_WRAPPER);
        wrappersHashMap.put(SDOConstants.SDO_STRINGS.getQName(), SDO_STRINGS_WRAPPER);
        wrappersHashMap.put(SDOConstants.SDO_TIME.getQName(), SDO_TIME_WRAPPER);
        wrappersHashMap.put(SDOConstants.SDO_URI.getQName(), SDO_URI_WRAPPER);
        wrappersHashMap.put(SDOConstants.SDO_YEAR.getQName(), SDO_YEAR_WRAPPER);
        wrappersHashMap.put(SDOConstants.SDO_YEARMONTH.getQName(), SDO_YEARMONTH_WRAPPER);
        wrappersHashMap.put(SDOConstants.SDO_YEARMONTHDAY.getQName(), SDO_YEARMONTHDAY_WRAPPER);

        wrappersHashMap.put(SDOConstants.SDO_BOOLEANOBJECT.getQName(), SDO_BOOLEANOBJECT_WRAPPER);
        wrappersHashMap.put(SDOConstants.SDO_BYTEOBJECT.getQName(), SDO_BYTEOBJECT_WRAPPER);
        wrappersHashMap.put(SDOConstants.SDO_CHARACTEROBJECT.getQName(), SDO_CHARACTEROBJECT_WRAPPER);
        wrappersHashMap.put(SDOConstants.SDO_DOUBLEOBJECT.getQName(), SDO_DOUBLEOBJECT_WRAPPER);
        wrappersHashMap.put(SDOConstants.SDO_FLOATOBJECT.getQName(), SDO_FLOATOBJECT_WRAPPER);
        wrappersHashMap.put(SDOConstants.SDO_INTOBJECT.getQName(), SDO_INTOBJECT_WRAPPER);
        wrappersHashMap.put(SDOConstants.SDO_LONGOBJECT.getQName(), SDO_LONGOBJECT_WRAPPER);
        wrappersHashMap.put(SDOConstants.SDO_SHORTOBJECT.getQName(), SDO_SHORTOBJECT_WRAPPER);

        // Now add these into the impl-to-type Map
        Iterator wrappers = wrappersHashMap.values().iterator();
        while (wrappers.hasNext()) {
            SDOWrapperType wrapper = (SDOWrapperType) wrappers.next();
            Iterator descriptors = wrapper.getDescriptorsMap().values().iterator();
            while (descriptors.hasNext()) {
                XMLDescriptor d = (XMLDescriptor) descriptors.next();
                implClassesToSDOType.put(d.getJavaClass(), wrapper);
            }
        }
    }

    /**
     * initialize the built-in HashMap sdoToXSDTypes
     */
    private void initSdoToXSDType() {
        sdoToXSDTypes.put(SDOConstants.SDO_BOOLEAN, XMLConstants.BOOLEAN_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_BYTE, XMLConstants.BYTE_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_BYTES, XMLConstants.HEX_BINARY_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_CHARACTER, XMLConstants.STRING_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_DATE, XMLConstants.DATE_TIME_QNAME);
        sdoToXSDTypes.put(SDOConstants.SDO_DATETIME, XMLConstants.DATE_TIME_QNAME);

        Type dataObjectType = this.getType(SDOConstants.SDO_URL, SDOConstants.DATAOBJECT);
        sdoToXSDTypes.put(dataObjectType, SDOConstants.ANY_TYPE_QNAME);

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
     * initialize the built-in HashMap xsdToSDOTypes
     */
    private void initXsdToSDOType() {
        xsdToSDOType.put(XMLConstants.ANY_SIMPLE_TYPE_QNAME, SDOConstants.SDO_OBJECT);

        Type dataObjectType = this.getType(SDOConstants.SDO_URL, SDOConstants.DATAOBJECT);
        xsdToSDOType.put(SDOConstants.ANY_TYPE_QNAME, dataObjectType);

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

    public Class getJavaWrapperTypeForSDOType(Type type) {
        SDOType sdoType = (SDOType) type;
        if (sdoType.getInstanceClass() != null) {
            return sdoType.getInstanceClass();
        }
        Class javaClass = null;
        if (sdoType.isSubType()) {
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
    public SDOType getType(String uri, String typeName) {
        // for now, only the build in HashMap will be used to acquire
        // Type.a
        if (typeName == null) {
            return null;
        }

        if ((uri != null) && (uri.equals(SDOConstants.SDO_URL))) {
            return (SDOType)commonjHashMap.get(typeName);
        } else if ((uri != null) && (uri.equals(SDOConstants.SDOJAVA_URL))) {
            return (SDOType)commonjJavaHashMap.get(typeName);
        } else {
            QName qName = new QName(uri, typeName);
            return (SDOType)getTypesHashMap().get(qName);
        }
    }

    public void addType(SDOType newType) {
        if(SDOConstants.SDO_URL.equals(newType.getURI())) {
            commonjHashMap.put(newType.getName(), newType);
        } else if (SDOConstants.SDOJAVA_URL.equals(newType.getURI())) {
            commonjJavaHashMap.put(newType.getName(), newType);
        } else {
            getTypesHashMap().put(newType.getQName(), newType);
        }
    }

    /**
     * Return the Type for this interfaceClass or null if not found.
     * @param interfaceClass is the interface for the DataObject's Type -
     *    type.getInstanceClass();
     * @return the Type for this interfaceClass or null if not found.
     */
    public SDOType getType(Class interfaceClass) {
        // Check interfaces-to-types map first:
        SDOType type = (SDOType) getInterfacesToSDOTypeHashMap().get(interfaceClass);
        if (type != null) {
            return type;
        }

        type = getTypeForSimpleJavaType(interfaceClass);
        if (type != null) {
            return type;
        }
        
        //types keyed on qname
        Iterator iter = getTypesHashMap().keySet().iterator();
        while (iter.hasNext()) {
            QName key = (QName)iter.next();
            SDOType value = (SDOType) getTypesHashMap().get(key);

            if (value.getInstanceClass() == interfaceClass) {
                return value;
            }
        }

        //Check in list of anonymous types
        iter = getAnonymousTypes().iterator();
        while(iter.hasNext()) {
            SDOType value = (SDOType)iter.next();
            
            if(value.getInstanceClass() == interfaceClass) {
                return value;
            }
        }

        //Check in the commonjHashMap as well.
        iter = this.commonjHashMap.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            SDOType value = (SDOType) commonjHashMap.get(key);

            if (value.getInstanceClass() == interfaceClass) {
                return value;
            }
        }
       

        return null; 
    }

    public SDOType getTypeForImplClass(Class implClass) {
        SDOType type = getTypeForSimpleJavaType(implClass);
        if (type != null) {
            return type;
        }

        type = getImplClassesToSDOType().get(implClass);
        if (type != null) {
            return type;
        }

        //Check in the commonjHashMap as well.
        Iterator iter = this.commonjHashMap.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            SDOType value = (SDOType) commonjHashMap.get(key);
            if(!value.isDataType()) {
                if (value.getImplClass() == implClass) {
                    return value;
                }
            }
        }

        return null;
    }

    /**
     * INTERNAL:
     * Used to determine which SDO Type corresponds the given Java simple type
     */
    public SDOType getTypeForSimpleJavaType(Class implClass) {
        return (SDOType)getSDOTypeForSimpleJavaTypeMap().get(implClass);
    }

    public synchronized Type define(DataObject dataObject) {
        List types = new ArrayList();
        Type rootType = define(dataObject, types);
        initializeTypes(types);
        return rootType;

    }

    private void initializeTypes(List types){
        List descriptorsToAdd = new ArrayList(types);
        for (int i = 0; i < types.size(); i++) {
            SDOType nextType = (SDOType)types.get(i);
            if (!nextType.isDataType()) {
                nextType.postInitialize();
            }
        }
        for (int i = 0; i < types.size(); i++) {
            SDOType nextType = (SDOType)types.get(i);
            if ((!nextType.isDataType() && !nextType.isSubType()) && nextType.isBaseType()) {
                nextType.setupInheritance(null);
            } else if (!nextType.isDataType() && nextType.isSubType() && !types.contains(nextType.getBaseTypes().get(0))) {
                SDOType baseType = (SDOType)nextType.getBaseTypes().get(0);
                while (baseType != null && !baseType.isDataType()) {
                    descriptorsToAdd.add(baseType);
                    if (baseType.getBaseTypes().size() == 0) {
                        descriptorsToAdd.add(baseType);
                        // baseType should now be root of inheritance
                        baseType.setupInheritance(null);
                        baseType = null;
                    } else {
                        baseType = (SDOType)baseType.getBaseTypes().get(0);
                    }
                }
            }
        }
        ((SDOXMLHelper)aHelperContext.getXMLHelper()).addDescriptors(descriptorsToAdd);
    }

    /**
     * Define the DataObject as a Type.
     * The Type is available through TypeHelper and DataGraph getType() methods.
     * @param type the DataObject representing the Type.
     * @return the defined Type.
     * @throws IllegalArgumentException if the Type could not be defined.
     */
    public synchronized Type define(DataObject dataObject, List types) {
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

        if (null != type) {
            return type;
        }

        boolean isDataType = dataObject.getBoolean("dataType");
        if(isDataType) {
            type = new SDODataType(uri, name, this);
        } else {
            type = new SDOType(uri, name, this);
            if (dataObject.getBoolean("sequenced")) {
                type.setSequenced(true);
                type.setMixed(true);
            }
        }
        type.setDataType(isDataType);
        addType(type);
        types.add(type);

        type.setAppInfoElements((List) dataObject.get(SDOConstants.APPINFO));

        type.setAbstract(dataObject.getBoolean("abstract"));

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

        if (!type.isDataType()) {
            type.preInitialize(null, null);
        }

        List properties = dataObject.getList("property");

        for (int i = 0; i < properties.size(); i++) {
            Object nextValue = properties.get(i);

            if (nextValue instanceof DataObject) {
                buildPropertyFromDataObject((DataObject)nextValue, type, types);
            }
        }
        type.setOpen(dataObject.getBoolean("open"));

        if (type.isDataType()) {
            QName typeQName = new QName(XMLConstants.SCHEMA_URL, name);
            if (typeHelper.getWrappersHashMap().get(typeQName) != null) {
                // In the case of a non-user-defined simple type, we already have built-in
                // wrappers created, so do not continue on building a new WrapperType.
                return type;
            }

            // Defining a new simple type from a DataObject.
            // See also: SDOTypesGenerator:startNewSimpleType for "types from XSD" equivalent

            // If this simple type is a restriction, get the QName for the base type and
            // include it when creating the WrapperType.  The QName will be used during
            // conversions (eg. "myBinaryElement" could be a restriction of base64Binary
            // or hexBinary.
            QName currentTypeQName = null;
            if (type.isSubType()) {
                SDOType baseType = (SDOType) type.getBaseTypes().get(0);
                currentTypeQName = new QName(XMLConstants.SCHEMA_URL, baseType.getName());
            }

            // Create the new WrapperType
            SDOWrapperType wrapperType = new SDOWrapperType(type, name, this, currentTypeQName);

            // Register WrapperType with maps on TypeHelper
            typeHelper.getWrappersHashMap().put(typeQName, wrapperType);
            typeHelper.getTypesHashMap().put(typeQName, wrapperType);
            typeHelper.getImplClassesToSDOType().put(wrapperType.getXmlDescriptor().getJavaClass(), wrapperType);

            // Add descriptor to XMLHelper
            ArrayList list = new ArrayList(1);
            list.add(wrapperType);
            ((SDOXMLHelper) aHelperContext.getXMLHelper()).addDescriptors(list);
        }

        return type;
    }

    private boolean isBaseTypeBytes(Type theType) {
        if (((SDOType) theType).isSubType()) {
            List baseTypes = ((SDOType)theType).getBaseTypes();
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
        if (objectValue instanceof SDODataObject) {
            if (((SDODataObject)objectValue).getType().isTypeType()) {
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
            } else if (((SDOType)typeValue).isDataType()) {
                if (isBaseTypeBytes(typeValue)) {
                    newProperty.setXsdType(XMLConstants.BASE_64_BINARY_QNAME);
                }
            }
        }

        newProperty.setAppInfoElements((List) dataObject.get(SDOConstants.APPINFO));
        
        if (dataObject.isSet("containment")) {
            newProperty.setContainment(dataObject.getBoolean("containment"));
        } else {
            if (typeValue != null) {
                newProperty.setContainment(!((SDOType)typeValue).isDataType());
            }
        }

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
            if (opposite instanceof SDOProperty) {
                newProperty.setOpposite((SDOProperty)opposite);
                ((SDOProperty)opposite).setOpposite(newProperty);
            } else if(opposite instanceof DataObject) {
                SDOProperty prop = (SDOProperty)typeValue.getProperty(((DataObject)opposite).getString("name"));
                if(prop != null) {
                    newProperty.setOpposite(prop);
                    prop.setOpposite(newProperty);
                }
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
            if (aHelperContext.getXSDHelper().isElement(newProperty) || newProperty.getType().isChangeSummaryType()) {
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
    public synchronized List define(List types) {
        if (types == null) {
            throw new IllegalArgumentException(SDOException.cannotPerformOperationWithNullInputParameter("define", "types"));
        }
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

    /**
     * INTERNAL:
     * Return the map of Wrapper objects (SDOWrapperTypes that wrap a primitive document).
     * @return a HashMap of SDOWrapperTypes, keyed on the XSD type that it wraps.
     */
    public Map getWrappersHashMap() {
        if (wrappersHashMap == null) {
            initWrappersHashMap();
        }
        return wrappersHashMap;
    }

    /**
     * INTERNAL:
     * Set the map of Wrapper objects (SDOWrapperTypes that wrap a primitive document).
     * @param   aMap        a HashMap of SDOWrapperTypes, keyed on the XSD type that it wraps.
     */
    public void setWrappersHashMap(Map aMap) {
        this.wrappersHashMap = aMap;
    }

    public void reset() {
        interfacesToSDOTypeHashMap = new HashMap<Class, SDOType>();
        namespaceResolver = null;

        initWrapperTypes();
        initWrappersHashMap();
        initTypesHashMap();

        initSdoToXSDType();
        initXsdToSDOType();
        initOpenProps();
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
        if (propertyDO == null) {
            throw new IllegalArgumentException(SDOException.cannotPerformOperationWithNullInputParameter("defineOpenContentProperty", "propertyDO"));
        }
        
        String name = propertyDO.getString("name");
        Property propertyToReturn = aHelperContext.getXSDHelper().getGlobalProperty(uri, name, true);
        if (propertyToReturn == null) {
            propertyToReturn = aHelperContext.getXSDHelper().getGlobalProperty(uri, name, false);
        }

        if (propertyToReturn == null) {
            List types = new ArrayList();
            propertyToReturn = buildPropertyFromDataObject(propertyDO, null, types);
            initializeTypes(types);
            defineOpenContentProperty(uri, name, propertyToReturn);
        }

        if (propertyToReturn != null) {
            Object propDOType = propertyDO.get("type");
            SDOType existingType = (SDOType) propertyToReturn.getType();
            boolean typeMismatch = false;
            
            if (propDOType instanceof SDOType) {
                SDOType newType = (SDOType) propDOType;
                if (!newType.getQName().equals(existingType.getQName())) {
                    typeMismatch = true;
                }
            } else if (propDOType instanceof DataObject) {
                DataObject newTypeDO = (DataObject) propDOType;
                if (!newTypeDO.get("name").equals(existingType.getName()) || !newTypeDO.get("uri").equals(existingType.getURI())) {
                    typeMismatch = true;
                }
            }
            
            if (typeMismatch) {
                throw new IllegalArgumentException("Should not be able to redefine a Property with a different Type.");
            }
        }
        
        return propertyToReturn;
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

            XMLDescriptor aDescriptor;
            if (((SDOType)property.getType()).isDataType()) {
                // RICK: Types from DataObjects: isDataType() == true so:
                //   - find the SDOWrapperType from TypeHelper's WrappersHashMap
                //   - add the descriptor to XMLContext's DescriptorsByQName map

                // See also: SDOTypesGenerator:addRootElementToDescriptor

                SDOTypeHelper typeHelper = (SDOTypeHelper) aHelperContext.getTypeHelper();

                SDOWrapperType wrapperType = (SDOWrapperType) typeHelper.getWrappersHashMap().get(((SDOProperty)property).getType().getQName());

                if (wrapperType != null) {
                    QName descriptorQname = new QName(propertyUri, propertyName);
                    ((SDOXMLHelper) aHelperContext.getXMLHelper()).getXmlContext().addDescriptorByQName(descriptorQname, wrapperType.getXmlDescriptor(((SDOProperty)property).getXsdType()));
                }

                return;
            } else {
                aDescriptor = ((SDOType)property.getType()).getXmlDescriptor();
            }

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
        SDOType typeType = this.getType(SDOConstants.SDO_URL, SDOConstants.TYPE);

        openContentProperties = new HashMap();
        openContentProperties.put(SDOConstants.MIME_TYPE_QNAME, SDOConstants.MIME_TYPE_PROPERTY);
        openContentProperties.put(SDOConstants.MIME_TYPE_PROPERTY_QNAME, SDOConstants.MIME_TYPE_PROPERTY_PROPERTY);
        openContentProperties.put(SDOConstants.SCHEMA_TYPE_QNAME, new SDOProperty(aHelperContext, SDOConstants.ORACLE_SDO_URL, SDOConstants.XML_SCHEMA_TYPE_NAME, typeType));
        openContentProperties.put(SDOConstants.JAVA_CLASS_QNAME, SDOConstants.JAVA_CLASS_PROPERTY);
        openContentProperties.put(SDOConstants.XML_ELEMENT_QNAME, SDOConstants.XMLELEMENT_PROPERTY);
        openContentProperties.put(SDOConstants.XML_DATATYPE_QNAME, new SDOProperty(aHelperContext, SDOConstants.SDOXML_URL, SDOConstants.SDOXML_DATATYPE, typeType));
        openContentProperties.put(SDOConstants.XML_ID_PROPERTY_QNAME, SDOConstants.ID_PROPERTY);
        openContentProperties.put(SDOConstants.DOCUMENTATION_PROPERTY_QNAME, SDOConstants.DOCUMENTATION_PROPERTY);
    }

    public Map getInterfacesToSDOTypeHashMap() {
        return interfacesToSDOTypeHashMap;
    }

    public Map<Class, SDOType> getImplClassesToSDOType() {
        return implClassesToSDOType;
    }

    public List getAnonymousTypes() {
        if(this.anonymousTypes == null) {
            anonymousTypes = new ArrayList();
        }
        return anonymousTypes;
    }

}
