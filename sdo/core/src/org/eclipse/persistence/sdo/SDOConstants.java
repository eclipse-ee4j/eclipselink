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

import java.util.ArrayList;
import javax.xml.namespace.QName;
import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.impl.HelperProvider;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.UnmarshalKeepAsElementPolicy;
import org.eclipse.persistence.oxm.mappings.XMLAnyCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

/**
 * <p><b>Purpose</b>: Maintain constants in one class
 * <p><b>Responsibilities</b>:<ul>
 * <li> Define and hold constants that are independent of classLoader, context.
 * </ul>
 */
public class SDOConstants {
    // 20070604: use a standard class as opposed to a constant interface (v21.20060731) - see item 17 of (Effective Java)

    /** Strings for known uris and default prefix values */
    public static final String SDO_PREFIX = "sdo";
    public static final String SDOXML_PREFIX = "sdoXML";
    public static final String SDOJAVA_PREFIX = "sdoJava";
    public static final String SDO_URL = "commonj.sdo";
    public static final String SDOXML_URL = "commonj.sdo/xml";
    public static final String SDOJAVA_URL = "commonj.sdo/java";

    /** namespace for custom unique properties */

    // TODO: flag for possible rename and deprecation after EclipseLink move
    public static final String ORACLE_SDO_URL = "org.eclipse.persistence.sdo";
    public static final String MIMETYPE_URL = "http://www.w3.org/2005/05/xmlmime";

    /** open content property to be set when defining a Type via a DataObject for reference relationships */
    public static final String ID_PROPERTY_NAME = "id";

    /**String values for type names in the commonj.sdo namespace */
    public static final String BOOLEAN = "Boolean";
    public static final String BYTE = "Byte";
    public static final String BYTES = "Bytes";
    public static final String CHANGESUMMARY = "ChangeSummaryType";
    public static final String CHARACTER = "Character";
    public static final String DATE = "Date";
    public static final String DATAOBJECT = "DataObject";
    public static final String DATETIME = "DateTime";
    public static final String DAY = "Day";
    public static final String DECIMAL = "Decimal";
    public static final String DOUBLE = "Double";
    public static final String DURATION = "Duration";
    public static final String FLOAT = "Float";
    public static final String INT = "Int";
    public static final String INTEGER = "Integer";
    public static final String LONG = "Long";
    public static final String MONTH = "Month";
    public static final String MONTHDAY = "MonthDay";
    public static final String OBJECT = "Object";
    public static final String PROPERTY = "Property";
    public static final String SHORT = "Short";
    public static final String STRING = "String";
    public static final String STRINGS = "Strings";
    public static final String TIME = "Time";
    public static final String TYPE = "Type";
    public static final String YEAR = "Year";
    public static final String YEARMONTH = "YearMonth";
    public static final String YEARMONTHDAY = "YearMonthDay";
    public static final String URI = "URI";

    /**String values for type names in the commonj.sdo/java namespace */
    public static final String BOOLEANOBJECT = "BooleanObject";
    public static final String BYTEOBJECT = "ByteObject";
    public static final String CHARACTEROBJECT = "CharacterObject";
    public static final String DOUBLEOBJECT = "DoubleObject";
    public static final String FLOATOBJECT = "FloatObject";
    public static final String INTOBJECT = "IntObject";
    public static final String LONGOBJECT = "LongObject";
    public static final String SHORTOBJECT = "ShortObject";

    /**Type objects for types in the commonj.sdo namespace */

    // make the HelperContext global
    public static final HelperContext globalHelperContext = HelperProvider.getDefaultContext();
    public static final SDOType SDO_BOOLEAN = new SDOType(SDO_URL, BOOLEAN, globalHelperContext);
    public static final SDOType SDO_BYTE = new SDOType(SDO_URL, BYTE, globalHelperContext);
    public static final SDOType SDO_BYTES = new SDOType(SDO_URL, BYTES, globalHelperContext);
    public static final SDOType SDO_CHANGESUMMARY = new SDOType(SDO_URL, CHANGESUMMARY, globalHelperContext);
    public static final SDOType SDO_CHARACTER = new SDOType(SDO_URL, CHARACTER, globalHelperContext);
    public static final SDOType SDO_DATE = new SDOType(SDO_URL, DATE, globalHelperContext);
    public static final SDOType SDO_DATETIME = new SDOType(SDO_URL, DATETIME, globalHelperContext);
    public static final SDOType SDO_DATAOBJECT = new SDOType(SDO_URL, DATAOBJECT, globalHelperContext);
    public static final SDOType SDO_DAY = new SDOType(SDO_URL, DAY, globalHelperContext);
    public static final SDOType SDO_DECIMAL = new SDOType(SDO_URL, DECIMAL, globalHelperContext);
    public static final SDOType SDO_DOUBLE = new SDOType(SDO_URL, DOUBLE, globalHelperContext);
    public static final SDOType SDO_DURATION = new SDOType(SDO_URL, DURATION, globalHelperContext);
    public static final SDOType SDO_FLOAT = new SDOType(SDO_URL, FLOAT, globalHelperContext);
    public static final SDOType SDO_INT = new SDOType(SDO_URL, INT, globalHelperContext);
    public static final SDOType SDO_INTEGER = new SDOType(SDO_URL, INTEGER, globalHelperContext);
    public static final SDOType SDO_LONG = new SDOType(SDO_URL, LONG, globalHelperContext);
    public static final SDOType SDO_MONTH = new SDOType(SDO_URL, MONTH, globalHelperContext);
    public static final SDOType SDO_MONTHDAY = new SDOType(SDO_URL, MONTHDAY, globalHelperContext);
    public static final SDOType SDO_OBJECT = new SDOType(SDO_URL, OBJECT, globalHelperContext);
    public static final SDOType SDO_PROPERTY = new SDOType(SDO_URL, PROPERTY, globalHelperContext);
    public static final SDOType SDO_SHORT = new SDOType(SDO_URL, SHORT, globalHelperContext);
    public static final SDOType SDO_STRING = new SDOType(SDO_URL, STRING, globalHelperContext);
    public static final SDOType SDO_STRINGS = new SDOType(SDO_URL, STRINGS, globalHelperContext);
    public static final SDOType SDO_TIME = new SDOType(SDO_URL, TIME, globalHelperContext);
    public static final SDOType SDO_TYPE = new SDOType(SDO_URL, TYPE, globalHelperContext);
    public static final SDOType SDO_YEAR = new SDOType(SDO_URL, YEAR, globalHelperContext);
    public static final SDOType SDO_YEARMONTH = new SDOType(SDO_URL, YEARMONTH, globalHelperContext);
    public static final SDOType SDO_YEARMONTHDAY = new SDOType(SDO_URL, YEARMONTHDAY, globalHelperContext);
    public static final SDOType SDO_URI = new SDOType(SDO_URL, URI, globalHelperContext);

    /** Numeric primitive default instances see p 45 of Java Spec. 4th ed */
    public static final Boolean BOOLEAN_DEFAULT = Boolean.FALSE;
    public static final Byte BYTE_DEFAULT = new Byte((byte)0);
    public static final Character CHARACTER_DEFAULT = new Character('\u0000');
    public static final Double DOUBLE_DEFAULT = new Double(0.0d);
    public static final Float FLOAT_DEFAULT = new Float(0.0f);
    public static final Integer INTEGER_DEFAULT = new Integer(0);
    public static final Long LONG_DEFAULT = new Long(0L);
    public static final Short SHORT_DEFAULT = new Short((short)0);

    /**Type objects for types in the commonj.sdo/java namespace */
    public static final SDOType SDO_BOOLEANOBJECT = new SDOType(SDOJAVA_URL, BOOLEANOBJECT, globalHelperContext);
    public static final SDOType SDO_BYTEOBJECT = new SDOType(SDOJAVA_URL, BYTEOBJECT, globalHelperContext);
    public static final SDOType SDO_CHARACTEROBJECT = new SDOType(SDOJAVA_URL, CHARACTEROBJECT, globalHelperContext);
    public static final SDOType SDO_DOUBLEOBJECT = new SDOType(SDOJAVA_URL, DOUBLEOBJECT, globalHelperContext);
    public static final SDOType SDO_FLOATOBJECT = new SDOType(SDOJAVA_URL, FLOATOBJECT, globalHelperContext);
    public static final SDOType SDO_INTOBJECT = new SDOType(SDOJAVA_URL, INTOBJECT, globalHelperContext);
    public static final SDOType SDO_LONGOBJECT = new SDOType(SDOJAVA_URL, LONGOBJECT, globalHelperContext);
    public static final SDOType SDO_SHORTOBJECT = new SDOType(SDOJAVA_URL, SHORTOBJECT, globalHelperContext);

    /** XML String names and QName constants missing from org.eclipse.persistence.oxm.XMLConstants  */
    public static final String ANY_TYPE = "anyType";
    public static final String ANY_URI = "anyURI";
    public static final String CONTAINMENT = "containment";
    public static final String XML_DURATION = "duration";
    public static final String ENTITIES = "ENTITIES";
    public static final String ENTITY = "ENTITY";
    public static final String GDAY = "gDay";
    public static final String GMONTH = "gMonth";
    public static final String GMONTHDAY = "gMonthDay";
    public static final String GYEAR = "gYear";
    public static final String GYEARMONTH = "gYearMonth";
    public static final String ID = "ID";
    public static final String IDREF = "IDREF";
    public static final String IDREFS = "IDREFS";
    public static final String LANGUAGE = "language";
    public static final String NAME = "Name";
    public static final String NCNAME = "NCName";
    public static final String NEGATIVEINTEGER = "negativeInteger";
    public static final String POSITIVEINTEGER = "positiveInteger";
    public static final String NMTOKEN = "NMTOKEN";
    public static final String NMTOKENS = "NMTOKENS";
    public static final String NONNEGATIVEINTEGER = "nonNegativeInteger";
    public static final String NONPOSITIVEINTEGER = "nonPositiveInteger";
    public static final String NORMALIZEDSTRING = "normalizedString";
    public static final String NOTATION = "NOTATION";
    public static final String TOKEN = "token";
    public static final String UNSIGNEDLONG = "unsignedLong";
    public static final QName ANY_TYPE_QNAME = new QName(XMLConstants.SCHEMA_URL, ANY_TYPE);
    public static final QName ANY_URI_QNAME = new QName(XMLConstants.SCHEMA_URL, ANY_URI);
    public static final QName ENTITIES_QNAME = new QName(XMLConstants.SCHEMA_URL, ENTITIES);
    public static final QName ENTITY_QNAME = new QName(XMLConstants.SCHEMA_URL, ENTITY);
    public static final QName DURATION_QNAME = new QName(XMLConstants.SCHEMA_URL, XML_DURATION);
    public static final QName GDAY_QNAME = new QName(XMLConstants.SCHEMA_URL, GDAY);
    public static final QName GMONTH_QNAME = new QName(XMLConstants.SCHEMA_URL, GMONTH);
    public static final QName GMONTHDAY_QNAME = new QName(XMLConstants.SCHEMA_URL, GMONTHDAY);
    public static final QName GYEAR_QNAME = new QName(XMLConstants.SCHEMA_URL, GYEAR);
    public static final QName GYEARMONTH_QNAME = new QName(XMLConstants.SCHEMA_URL, GYEARMONTH);
    public static final QName ID_QNAME = new QName(XMLConstants.SCHEMA_URL, ID);
    public static final QName IDREF_QNAME = new QName(XMLConstants.SCHEMA_URL, IDREF);
    public static final QName IDREFS_QNAME = new QName(XMLConstants.SCHEMA_URL, IDREFS);
    public static final QName LANGUAGE_QNAME = new QName(XMLConstants.SCHEMA_URL, LANGUAGE);
    public static final QName NAME_QNAME = new QName(XMLConstants.SCHEMA_URL, NAME);
    public static final QName NCNAME_QNAME = new QName(XMLConstants.SCHEMA_URL, NCNAME);
    public static final QName NEGATIVEINTEGER_QNAME = new QName(XMLConstants.SCHEMA_URL, NEGATIVEINTEGER);
    public static final QName POSITIVEINTEGER_QNAME = new QName(XMLConstants.SCHEMA_URL, POSITIVEINTEGER);
    public static final QName NMTOKEN_QNAME = new QName(XMLConstants.SCHEMA_URL, NMTOKEN);
    public static final QName NMTOKENS_QNAME = new QName(XMLConstants.SCHEMA_URL, NMTOKENS);
    public static final QName NONNEGATIVEINTEGER_QNAME = new QName(XMLConstants.SCHEMA_URL, NONNEGATIVEINTEGER);
    public static final QName NONPOSITIVEINTEGER_QNAME = new QName(XMLConstants.SCHEMA_URL, NONPOSITIVEINTEGER);
    public static final QName NORMALIZEDSTRING_QNAME = new QName(XMLConstants.SCHEMA_URL, NORMALIZEDSTRING);
    public static final QName NOTATION_QNAME = new QName(XMLConstants.SCHEMA_URL, NOTATION);
    public static final QName TOKEN_QNAME = new QName(XMLConstants.SCHEMA_URL, TOKEN);
    public static final QName UNSIGNEDLONG_QNAME = new QName(XMLConstants.SCHEMA_URL, UNSIGNEDLONG);
    public static final QName XML_MIME_TYPE_QNAME = new QName(MIMETYPE_URL, "expectedContentTypes");
    public static final String MIMETYPE_NAME = "mimeTypeProperty";
    public static final QName XML_MIME_TYPE_PROPERTY_QNAME = new QName(ORACLE_SDO_URL, MIMETYPE_NAME);
    public static final QName ID_PROPERTY_QNAME = new QName(ORACLE_SDO_URL, ID_PROPERTY_NAME);
    public static final String DOCUMENTATION = "documentation";

    /** Strings and QNames for annotations defined in the commonj.sdo/XML namespace*/
    public static final String SDOXML_ALIASNAME = "aliasName";
    public static final String SDOXML_NAME = "name";
    public static final String SDOXML_PROPERTYTYPE = "propertyType";
    public static final String SDOXML_OPPOSITEPROPERTY = "oppositeProperty";
    public static final String SDOXML_SEQUENCE = "sequence";
    public static final String SDOXML_READONLY = "readOnly";
    public static final String SDOXML_DATATYPE = "dataType";
    public static final String SDOXML_STRING_NAME = "string";
    public static final String SDOXML_MANY = "many";
    public static final QName SDOXML_MANY_QNAME = new QName(SDOXML_URL, SDOXML_MANY);
    public static final QName SDOXML_ALIASNAME_QNAME = new QName(SDOXML_URL, SDOXML_ALIASNAME);
    public static final QName SDOXML_NAME_QNAME = new QName(SDOXML_URL, SDOXML_NAME);
    public static final QName SDOXML_SEQUENCE_QNAME = new QName(SDOXML_URL, SDOXML_SEQUENCE);
    public static final QName SDOXML_READONLY_QNAME = new QName(SDOXML_URL, SDOXML_READONLY);
    public static final QName SDOXML_DATATYPE_QNAME = new QName(SDOXML_URL, SDOXML_DATATYPE);
    public static final QName SDOXML_STRING_QNAME = new QName(SDOXML_URL, SDOXML_STRING_NAME);
    public static final QName SDOXML_PROPERTYTYPE_QNAME = new QName(SDOXML_URL, SDOXML_PROPERTYTYPE);
    public static final QName SDOXML_OPPOSITEPROPERTY_QNAME = new QName(SDOXML_URL, SDOXML_OPPOSITEPROPERTY);

    /** Strings and QNames for annotations defined in the commonj.sdo/XML namespace*/
    public static final String SDOJAVA_PACKAGE = "package";
    public static final String SDOJAVA_INSTANCECLASS = "instanceClass";
    public static final String SDOJAVA_EXTENDEDINSTANCECLASS = "extendedInstanceClass";
    public static final String SDOJAVA_NESTEDINTERFACES = "nestedInterfaces";
    public static final QName SDOJAVA_PACKAGE_QNAME = new QName(SDOJAVA_URL, SDOJAVA_PACKAGE);
    public static final QName SDOJAVA_INSTANCECLASS_QNAME = new QName(SDOJAVA_URL, SDOJAVA_INSTANCECLASS);
    public static final QName SDOJAVA_EXTENDEDINSTANCECLASS_QNAME = new QName(SDOJAVA_URL, SDOJAVA_EXTENDEDINSTANCECLASS);
    public static final QName SDOJAVA_NESTEDINTERFACES_QNAME = new QName(SDOJAVA_URL, SDOJAVA_NESTEDINTERFACES);
    public static final String MIME_TYPE_PROPERTY_NAME = "mimeType";
    public static final SDOProperty MIME_TYPE_PROPERTY = new SDOProperty(globalHelperContext, MIME_TYPE_PROPERTY_NAME);
    public static final SDOProperty MIME_TYPE_PROPERTY_PROPERTY = new SDOProperty(globalHelperContext, MIMETYPE_NAME);
    public static final String XML_SCHEMA_TYPE_NAME = "xmlSchemaType";
    public static final SDOProperty XML_SCHEMA_TYPE_PROPERTY = new SDOProperty(globalHelperContext, XML_SCHEMA_TYPE_NAME);
    public static final SDOProperty DOCUMENTATION_PROPERTY = new SDOProperty(globalHelperContext, DOCUMENTATION);
    public static final String JAVACLASS_PROPERTY_NAME = "javaClass";
    public static final String XMLELEMENT_PROPERTY_NAME = "xmlElement";
    public static final SDOProperty JAVA_CLASS_PROPERTY = new SDOProperty(globalHelperContext, JAVACLASS_PROPERTY_NAME);
    public static final SDOProperty XMLELEMENT_PROPERTY = new SDOProperty(globalHelperContext, XMLELEMENT_PROPERTY_NAME);
    public static final SDOProperty XMLDATATYPE_PROPERTY = new SDOProperty(globalHelperContext, SDOXML_DATATYPE);

    /** open content property to be set when defining a Type via a DataObject for reference relationships */
    public static final SDOProperty ID_PROPERTY = new SDOProperty(globalHelperContext, ID_PROPERTY_NAME);

    /** generate built-in open content property QNames */
    public static final QName MIME_TYPE_QNAME = new QName(ORACLE_SDO_URL, SDOConstants.MIME_TYPE_PROPERTY.getName());
    public static final QName MIME_TYPE_PROPERTY_QNAME = new QName(ORACLE_SDO_URL, SDOConstants.MIME_TYPE_PROPERTY_PROPERTY.getName());
    public static final QName SCHEMA_TYPE_QNAME = new QName(ORACLE_SDO_URL, SDOConstants.XML_SCHEMA_TYPE_PROPERTY.getName());
    public static final QName JAVA_CLASS_QNAME = new QName(ORACLE_SDO_URL, SDOConstants.JAVA_CLASS_PROPERTY.getName());
    public static final QName XML_ELEMENT_QNAME = new QName(ORACLE_SDO_URL, SDOConstants.XMLELEMENT_PROPERTY.getName());
    public static final QName XML_DATATYPE_QNAME = new QName(ORACLE_SDO_URL, SDOConstants.XMLDATATYPE_PROPERTY.getName());
    public static final QName XML_ID_PROPERTY_QNAME = new QName(ORACLE_SDO_URL, SDOConstants.ID_PROPERTY.getName());
    public static final QName DOCUMENTATION_PROPERTY_QNAME = new QName(ORACLE_SDO_URL, DOCUMENTATION);

    /** Strings used when generating javadocs in generated Java source files */
    public static final String JAVADOC_START = "/**";
    public static final String JAVADOC_LINE = " * ";
    public static final String JAVADOC_END = " */";

    /** Strings used when generating classes */
    public static final String JAVA_PACKAGE_NAME_SEPARATOR = ".";
    public static final String JAVA_TYPEGENERATION_DEFAULT_PACKAGE_NAME = "defaultPackage";
    public static final String JAVA_TYPEGENERATION_NO_NAMESPACE = "noNamespace";
    public static final String SDO_IMPL_NAME = "Impl";

    // TODO: verify J2SE6 keywords

    /** The following reserved words are not valid in generated classes with prepended get/set */
    public static final String[] javaReservedWordsList = { "class" };

    /** All no-arg get/ single-arg set public SDO interface functions on SDODataObject must appear in this list.  */
    public static final String[] sdoInterfaceReservedWordsList = { //
        "ChangeSummary",//
        "Container",//
        "ContainmentProperty",//
        "DataGraph",//
        "InstanceProperties",//
        "RootObject",//
        "Sequence",//
        "Type" };

    /** Names for attributes available on ChangeSummary*/
    public static final String CHANGESUMMARY_REF = "ref";
    public static final String CHANGESUMMARY_UNSET = "unset";

    /** Name of source attribute on appinfo*/
    public static final String APPINFO_SOURCE_ATTRIBUTE = "source";

    /** empty string "" */
    public static final String EMPTY_STRING = "";

    /** reflective isSet method name */
    public static final String SDO_ISSET_METHOD_NAME = "isSet";

    /** SDO changeSummary reference path prefix string = # */
    public static final String SDO_CHANGESUMMARY_REF_PATH_PREFIX = "#";
    public static final int SDO_CHANGESUMMARY_REF_PATH_PREFIX_LENGTH = SDO_CHANGESUMMARY_REF_PATH_PREFIX.length();

    /** Pluggable valuestore constants*/
    /** System property key valuestore.impl.class for implementation class DefaultValueStore or org.eclipse.persistence.testing.sdo.helper.pluggable.impl.POJOValueStore  */
    public static final String SDO_PLUGGABLE_MAP_IMPL_CLASS_KEY = "valuestore.impl.class";

    /** default implementation class java.util.HashMap */
    public static final String SDO_PLUGGABLE_MAP_IMPL_CLASS_VALUE = "org.eclipse.persistence.sdo.DefaultValueStore";

    /** default implementation class java.util.HashMap */
    public static final String SDO_DATA_OBJECT_IMPL_CLASS_NAME = "org.eclipse.persistence.sdo.SDODataObject";

    // constants used during helperContext resolutions based on classloader
    // TODO: These matching strings should not be hardcoded to a particular J2EE server - need to test on WebSphere/WebLogic etc...

    /** A classloader toString containing (.web.) means we are running from a web container client */
    public static final String CLASSLOADER_WEB_FRAGMENT = ".web.";

    /** A classloader toString containing (.wrappers) means we are running from a local ejb container client */
    public static final String CLASSLOADER_EJB_FRAGMENT = ".wrappers";

    /** A classloader toString containing (oc4j:) means we are running from an 4 levels up from an ejb container */
    public static final String CLASSLOADER_OC4J_FRAGMENT = "oc4j:";

    /** XPath related constants*/
    /** XPath ns separator ":" */
    public static final String SDO_XPATH_NS_SEPARATOR_FRAGMENT = ":";

    /** XPath separator "/" */
    public static final String SDO_XPATH_SEPARATOR_FRAGMENT = "/";

    /** XPath List index open bracket "[" */
    public static final String SDO_XPATH_LIST_INDEX_OPEN_BRACKET = "[";

    /** XPath List index close bracket "]" */
    public static final String SDO_XPATH_LIST_INDEX_CLOSE_BRACKET = "]";

    /** XPath to a data object from itself  "/" */
    public static final String SDO_XPATH_TO_ROOT = "/";

    /** XPath to sibling or an invalid non-existent node currently = null */
    public static final String SDO_XPATH_INVALID_PATH = null;

    /** Search string concatenated from default package for type generation and the package separator dot */
    public static final String JAVA_TYPE_GENERATION_DEFAULT_PACKAGE_NAME_SEARCH = JAVA_TYPEGENERATION_DEFAULT_PACKAGE_NAME + JAVA_PACKAGE_NAME_SEPARATOR;

    // perform initialization of constants in use by HelperDelegates
    static {
        // in use by initCommonjHashMap()
        SDO_BOOLEAN.setDataType(true);
        SDO_BOOLEAN.setInstanceClass(ClassConstants.PBOOLEAN);

        SDO_BYTE.setDataType(true);
        SDO_BYTE.setInstanceClass(ClassConstants.PBYTE);

        SDO_BYTES.setDataType(true);
        SDO_BYTES.setInstanceClass(ClassConstants.APBYTE);

        SDO_CHARACTER.setDataType(true);
        SDO_CHARACTER.setInstanceClass(ClassConstants.PCHAR);

        SDO_DATAOBJECT.setDataType(false);
        SDO_DATAOBJECT.setInstanceClass(DataObject.class);
        SDO_DATAOBJECT.setAbstract(true);

        SDO_DATE.setDataType(true);
        SDO_DATE.setInstanceClass(ClassConstants.UTILDATE);

        SDO_DATETIME.setDataType(true);
        SDO_DATETIME.setInstanceClass(ClassConstants.STRING);

        SDO_DAY.setDataType(true);
        SDO_DAY.setInstanceClass(ClassConstants.STRING);

        SDO_DECIMAL.setDataType(true);
        SDO_DECIMAL.setInstanceClass(ClassConstants.BIGDECIMAL);

        SDO_DOUBLE.setDataType(true);
        SDO_DOUBLE.setInstanceClass(ClassConstants.PDOUBLE);

        SDO_DURATION.setDataType(true);
        SDO_DURATION.setInstanceClass(ClassConstants.STRING);

        SDO_FLOAT.setDataType(true);
        SDO_FLOAT.setInstanceClass(ClassConstants.PFLOAT);

        SDO_INT.setDataType(true);
        SDO_INT.setInstanceClass(ClassConstants.PINT);

        SDO_INTEGER.setDataType(true);
        SDO_INTEGER.setInstanceClass(ClassConstants.BIGINTEGER);

        SDO_LONG.setDataType(true);
        SDO_LONG.setInstanceClass(ClassConstants.PLONG);

        SDO_MONTH.setDataType(true);
        SDO_MONTH.setInstanceClass(ClassConstants.STRING);

        SDO_MONTHDAY.setDataType(true);
        SDO_MONTHDAY.setInstanceClass(ClassConstants.STRING);

        SDO_OBJECT.setDataType(true);
        SDO_OBJECT.setInstanceClass(ClassConstants.OBJECT);
        SDO_OBJECT.setAbstract(true);

        SDO_SHORT.setDataType(true);
        SDO_SHORT.setInstanceClass(ClassConstants.PSHORT);

        SDO_STRING.setDataType(true);
        SDO_STRING.setInstanceClass(ClassConstants.STRING);

        SDO_STRINGS.setDataType(true);
        SDO_STRINGS.setInstanceClass(ClassConstants.List_Class);

        SDO_TIME.setDataType(true);
        SDO_TIME.setInstanceClass(ClassConstants.STRING);

        SDO_YEAR.setDataType(true);
        SDO_YEAR.setInstanceClass(ClassConstants.STRING);

        SDO_YEARMONTH.setDataType(true);
        SDO_YEARMONTH.setInstanceClass(ClassConstants.STRING);

        SDO_YEARMONTHDAY.setDataType(true);
        SDO_YEARMONTHDAY.setInstanceClass(ClassConstants.STRING);

        SDO_URI.setDataType(true);
        SDO_URI.setInstanceClass(ClassConstants.STRING);

        // TODO: dataType=false will cause copyHelper to fail on equality because default contructor is invoked
        SDO_CHANGESUMMARY.setDataType(true);
        SDO_CHANGESUMMARY.setAbstract(true);

        // 6085307: use changeSummary interface javaClass over impl class (javaClass = CS interface, JavaClassName = CS Impl)
        XMLDescriptor aDescriptor = new XMLDescriptor();
        SDOConstants.SDO_CHANGESUMMARY.setInstanceClass(ChangeSummary.class);
        aDescriptor.setJavaClass(SDOChangeSummary.class);

        // logging attribute
        XMLDirectMapping aMapping = new XMLDirectMapping();

        // add attributes        
        aMapping.setAttributeName("loggingMapping");
        aMapping.setXPath("@logging");
        aMapping.setNullValue(Boolean.TRUE);
        aDescriptor.addMapping(aMapping);

        XMLCompositeDirectCollectionMapping createdMapping = new XMLCompositeDirectCollectionMapping();
        createdMapping.setAttributeName("createdXPaths");
        createdMapping.setXPath("@create");
        createdMapping.useCollectionClass(ArrayList.class);
        ((XMLField)createdMapping.getField()).setUsesSingleNode(true);
        aDescriptor.addMapping(createdMapping);

        XMLCompositeDirectCollectionMapping deletedMapping = new XMLCompositeDirectCollectionMapping();
        deletedMapping.setAttributeName("deletedXPaths");
        deletedMapping.setXPath("@delete");
        deletedMapping.useCollectionClass(ArrayList.class);
        ((XMLField)deletedMapping.getField()).setUsesSingleNode(true);
        aDescriptor.addMapping(deletedMapping);

        XMLAnyCollectionMapping aChangeMapping = new XMLAnyCollectionMapping();
        aChangeMapping.setAttributeName("modifiedDoms");
        aChangeMapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_ALL_AS_ELEMENT);
        aChangeMapping.useCollectionClass(ArrayList.class);
        aDescriptor.addMapping(aChangeMapping);

        SDOConstants.SDO_CHANGESUMMARY.setXmlDescriptor(aDescriptor);

        // these properties are ordered as listed page 74 sect. 8.3 of the spec in "SDO Model for Types and Properties"
        SDOProperty aliasNameProperty = new SDOProperty(globalHelperContext);
        aliasNameProperty.setName("aliasName");
        aliasNameProperty.setMany(true);
        aliasNameProperty.setType(SDO_STRING);
        SDO_PROPERTY.addDeclaredProperty(aliasNameProperty);

        SDOProperty propNameProperty = new SDOProperty(globalHelperContext);
        propNameProperty.setName("name");
        propNameProperty.setType(SDO_STRING);
        SDO_PROPERTY.addDeclaredProperty(propNameProperty);

        SDOProperty manyProperty = new SDOProperty(globalHelperContext);
        manyProperty.setName("many");
        manyProperty.setType(SDO_BOOLEAN);
        SDO_PROPERTY.addDeclaredProperty(manyProperty);

        SDOProperty containmentProperty = new SDOProperty(globalHelperContext);
        containmentProperty.setName("containment");
        containmentProperty.setType(SDO_BOOLEAN);
        SDO_PROPERTY.addDeclaredProperty(containmentProperty);

        SDOProperty defaultProperty = new SDOProperty(globalHelperContext);
        defaultProperty.setName("default");
        defaultProperty.setType(SDO_OBJECT);
        SDO_PROPERTY.addDeclaredProperty(defaultProperty);

        SDOProperty readOnlyProperty = new SDOProperty(globalHelperContext);
        readOnlyProperty.setName("readOnly");
        readOnlyProperty.setType(SDO_BOOLEAN);
        SDO_PROPERTY.addDeclaredProperty(readOnlyProperty);

        SDOProperty typeProperty = new SDOProperty(globalHelperContext);
        typeProperty.setName("type");
        typeProperty.setType(SDO_TYPE);
        SDO_PROPERTY.addDeclaredProperty(typeProperty);

        SDOProperty oppositeProperty = new SDOProperty(globalHelperContext);
        oppositeProperty.setName("opposite");
        oppositeProperty.setType(SDO_PROPERTY);
        SDO_PROPERTY.addDeclaredProperty(oppositeProperty);

        SDOProperty nullableProperty = new SDOProperty(globalHelperContext);
        nullableProperty.setName("nullable");
        nullableProperty.setType(SDO_BOOLEAN);
        SDO_PROPERTY.addDeclaredProperty(nullableProperty);

        SDO_PROPERTY.setOpen(true);

        // these properties are ordered as listed page 74 sect. 8.3 of the spec in "SDO Model for Types and Properties"
        SDOProperty baseTypeProperty = new SDOProperty(globalHelperContext);
        baseTypeProperty.setName("baseType");
        baseTypeProperty.setMany(true);
        baseTypeProperty.setType(SDO_TYPE);
        SDO_TYPE.addDeclaredProperty(baseTypeProperty);

        SDOProperty propertiesProperty = new SDOProperty(globalHelperContext);
        propertiesProperty.setName("property");
        propertiesProperty.setMany(true);
        propertiesProperty.setContainment(true);
        propertiesProperty.setType(SDO_PROPERTY);
        SDO_TYPE.addDeclaredProperty(propertiesProperty);

        SDOProperty typeAliasNameProperty = new SDOProperty(globalHelperContext);
        typeAliasNameProperty.setName("aliasName");
        typeAliasNameProperty.setMany(true);
        typeAliasNameProperty.setType(SDO_STRING);
        SDO_TYPE.addDeclaredProperty(typeAliasNameProperty);

        SDOProperty nameProperty = new SDOProperty(globalHelperContext);
        nameProperty.setName("name");
        nameProperty.setType(SDO_STRING);
        SDO_TYPE.addDeclaredProperty(nameProperty);

        SDOProperty uriProperty = new SDOProperty(globalHelperContext);
        uriProperty.setName("uri");
        uriProperty.setType(SDO_STRING);
        SDO_TYPE.addDeclaredProperty(uriProperty);

        SDOProperty dataTypeProperty = new SDOProperty(globalHelperContext);
        dataTypeProperty.setName("dataType");
        dataTypeProperty.setType(SDO_BOOLEAN);
        SDO_TYPE.addDeclaredProperty(dataTypeProperty);

        SDOProperty openProperty = new SDOProperty(globalHelperContext);
        openProperty.setName("open");
        openProperty.setType(SDO_BOOLEAN);
        SDO_TYPE.addDeclaredProperty(openProperty);

        SDOProperty sequencedProperty = new SDOProperty(globalHelperContext);
        sequencedProperty.setName("sequenced");
        sequencedProperty.setType(SDO_BOOLEAN);
        SDO_TYPE.addDeclaredProperty(sequencedProperty);

        SDOProperty abstractProperty = new SDOProperty(globalHelperContext);
        abstractProperty.setName("abstract");
        abstractProperty.setType(SDO_BOOLEAN);
        SDO_TYPE.addDeclaredProperty(abstractProperty);
        // set the XMLAnyCollectionMapping on the descriptor on SDO_TYPE
        SDO_TYPE.setOpen(true);

        // in use by initCommonjJavaHashMap() 
        SDO_BOOLEANOBJECT.setDataType(true);
        SDO_BOOLEANOBJECT.setInstanceClass(ClassConstants.BOOLEAN);

        SDO_BYTEOBJECT.setDataType(true);
        SDO_BYTEOBJECT.setInstanceClass(ClassConstants.BYTE);

        SDO_CHARACTEROBJECT.setDataType(true);
        SDO_CHARACTEROBJECT.setInstanceClass(ClassConstants.CHAR);

        SDO_DOUBLEOBJECT.setDataType(true);
        SDO_DOUBLEOBJECT.setInstanceClass(ClassConstants.DOUBLE);

        SDO_FLOATOBJECT.setDataType(true);
        SDO_FLOATOBJECT.setInstanceClass(ClassConstants.FLOAT);

        SDO_INTOBJECT.setDataType(true);
        SDO_INTOBJECT.setInstanceClass(ClassConstants.INTEGER);

        SDO_LONGOBJECT.setDataType(true);
        SDO_LONGOBJECT.setInstanceClass(ClassConstants.LONG);

        SDO_SHORTOBJECT.setDataType(true);
        SDO_SHORTOBJECT.setInstanceClass(ClassConstants.SHORT);

        // initialize the built-in open content property values (to be appended into the user defined open content properties map)
        // the SDOTypeHelperDelegate.openContentProperties Map contains both global properties (here) and user defined properties
        Type stringType = SDO_STRING;

        //define open content mimeType property  
        MIME_TYPE_PROPERTY.setType(stringType);
        // define open content mimeType property  property
        MIME_TYPE_PROPERTY_PROPERTY.setType(stringType);
        // define open content schema type property  
        XML_SCHEMA_TYPE_PROPERTY.setType(SDO_TYPE);
        // define open content javaClass property
        JAVA_CLASS_PROPERTY.setType(stringType);
        // define open content xmlelement property
        XMLELEMENT_PROPERTY.setType(stringType);
        //define open content xmldatatype property
        XMLDATATYPE_PROPERTY.setType(SDO_TYPE);
        //define open content idProp property
        ID_PROPERTY.setType(SDO_TYPE);
        //define documentation property  
        DOCUMENTATION_PROPERTY.setType(stringType);
        DOCUMENTATION_PROPERTY.setMany(true);

        /** JIRA-253 set pseudoDefaults on numeric primitives
         * see http://java.sun.com/docs/books/tutorial/java/nutsandbolts/datatypes.html (primary ref)
         * see p.45 of the Java Spec 4th edition (secondary ref)
         **/
        SDO_BOOLEAN.setPseudoDefault(BOOLEAN_DEFAULT);
        SDO_BYTE.setPseudoDefault(BYTE_DEFAULT);
        SDO_CHARACTER.setPseudoDefault(CHARACTER_DEFAULT);
        SDO_DOUBLE.setPseudoDefault(DOUBLE_DEFAULT);
        SDO_FLOAT.setPseudoDefault(FLOAT_DEFAULT);
        SDO_INT.setPseudoDefault(INTEGER_DEFAULT);
        SDO_LONG.setPseudoDefault(LONG_DEFAULT);
        SDO_SHORT.setPseudoDefault(SHORT_DEFAULT);
    }
}