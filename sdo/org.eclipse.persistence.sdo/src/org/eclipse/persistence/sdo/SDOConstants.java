/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

import javax.xml.namespace.QName;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.impl.*;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.sdo.types.*;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;

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

    public static final String XMLHELPER_LOAD_OPTIONS ="LoadOptions";
    public static final String TYPE_LOAD_OPTION = "type";
    public static final String ATTACHMENT_MARSHALLER_OPTION = "attachmentMarshaller";
    public static final String ATTACHMENT_UNMARSHALLER_OPTION = "attachmentUnmarshaller";

    public static HelperContext globalHelperContext;
    private static SDOTypeHelper sdoTypeHelper;

    /** Numeric primitive default instances see p 45 of Java Spec. 4th ed */
    public static final Boolean BOOLEAN_DEFAULT = Boolean.FALSE;
    public static final Byte BYTE_DEFAULT = (byte)0;
    public static final Character CHARACTER_DEFAULT = '\u0000';
    public static final Double DOUBLE_DEFAULT = 0.0d;
    public static final Float FLOAT_DEFAULT = 0.0f;
    public static final Integer INTEGER_DEFAULT = 0;
    public static final Long LONG_DEFAULT = 0L;
    public static final Short SHORT_DEFAULT = (short)0;

    public static final SDOType SDO_BOOLEAN = new SDODataType(SDO_URL, BOOLEAN, ClassConstants.PBOOLEAN, sdoTypeHelper, BOOLEAN_DEFAULT);
    public static final SDOType SDO_BYTE = new SDODataType(SDO_URL, BYTE, ClassConstants.PBYTE, sdoTypeHelper, BYTE_DEFAULT);
    public static final SDOType SDO_BYTES = new SDODataType(SDO_URL, BYTES, ClassConstants.APBYTE, sdoTypeHelper);
    public static final SDOType SDO_CHARACTER = new SDODataType(SDO_URL, CHARACTER, ClassConstants.PCHAR, sdoTypeHelper, CHARACTER_DEFAULT);
    public static final SDOType SDO_DATE = new SDODataType(SDO_URL, DATE, ClassConstants.UTILDATE, sdoTypeHelper);
    public static final SDOType SDO_DATETIME = new SDODataType(SDO_URL, DATETIME, ClassConstants.STRING, sdoTypeHelper);
    public static final SDOType SDO_DAY = new SDODataType(SDO_URL, DAY, ClassConstants.STRING, sdoTypeHelper);
    public static final SDOType SDO_DECIMAL = new SDODataType(SDO_URL, DECIMAL, ClassConstants.BIGDECIMAL, sdoTypeHelper);
    public static final SDOType SDO_DOUBLE = new SDODataType(SDO_URL, DOUBLE, ClassConstants.PDOUBLE, sdoTypeHelper, DOUBLE_DEFAULT);
    public static final SDOType SDO_DURATION = new SDODataType(SDO_URL, DURATION, ClassConstants.STRING, sdoTypeHelper);
    public static final SDOType SDO_FLOAT = new SDODataType(SDO_URL, FLOAT, ClassConstants.PFLOAT, sdoTypeHelper, FLOAT_DEFAULT);
    public static final SDOType SDO_INT = new SDODataType(SDO_URL, INT, ClassConstants.PINT, sdoTypeHelper, INTEGER_DEFAULT);
    public static final SDOType SDO_INTEGER = new SDODataType(SDO_URL, INTEGER, ClassConstants.BIGINTEGER, sdoTypeHelper);
    public static final SDOType SDO_LONG = new SDODataType(SDO_URL, LONG, ClassConstants.PLONG, sdoTypeHelper, LONG_DEFAULT);
    public static final SDOType SDO_MONTH = new SDODataType(SDO_URL, MONTH, ClassConstants.STRING, sdoTypeHelper);
    public static final SDOType SDO_MONTHDAY = new SDODataType(SDO_URL, MONTHDAY, ClassConstants.STRING, sdoTypeHelper);
    public static final SDOType SDO_OBJECT = new SDODataType(SDO_URL, OBJECT, ClassConstants.OBJECT, sdoTypeHelper);
    public static final SDOType SDO_SHORT = new SDODataType(SDO_URL, SHORT, ClassConstants.PSHORT, sdoTypeHelper, SHORT_DEFAULT);
    public static final SDOType SDO_STRING = new SDODataType(SDO_URL, STRING, ClassConstants.STRING, sdoTypeHelper);
    public static final SDOType SDO_STRINGS = new SDODataType(SDO_URL, STRINGS, ClassConstants.List_Class, sdoTypeHelper);
    public static final SDOType SDO_TIME = new SDODataType(SDO_URL, TIME, ClassConstants.STRING, sdoTypeHelper);
    public static final SDOType SDO_YEAR = new SDODataType(SDO_URL, YEAR, ClassConstants.STRING, sdoTypeHelper);
    public static final SDOType SDO_YEARMONTH = new SDODataType(SDO_URL, YEARMONTH, ClassConstants.STRING, sdoTypeHelper);
    public static final SDOType SDO_YEARMONTHDAY = new SDODataType(SDO_URL, YEARMONTHDAY, ClassConstants.STRING, sdoTypeHelper);
    public static final SDOType SDO_URI = new SDODataType(SDO_URL, URI, ClassConstants.STRING, sdoTypeHelper);

    /**Type objects for types in the commonj.sdo/java namespace */
    public static final SDOType SDO_BOOLEANOBJECT = new SDODataType(SDOJAVA_URL, BOOLEANOBJECT, ClassConstants.BOOLEAN, sdoTypeHelper);
    public static final SDOType SDO_BYTEOBJECT = new SDODataType(SDOJAVA_URL, BYTEOBJECT, ClassConstants.BYTE, sdoTypeHelper);
    public static final SDOType SDO_CHARACTEROBJECT = new SDODataType(SDOJAVA_URL, CHARACTEROBJECT, ClassConstants.CHAR, sdoTypeHelper);
    public static final SDOType SDO_DOUBLEOBJECT = new SDODataType(SDOJAVA_URL, DOUBLEOBJECT, ClassConstants.DOUBLE, sdoTypeHelper);
    public static final SDOType SDO_FLOATOBJECT = new SDODataType(SDOJAVA_URL, FLOATOBJECT, ClassConstants.FLOAT, sdoTypeHelper);
    public static final SDOType SDO_INTOBJECT = new SDODataType(SDOJAVA_URL, INTOBJECT, ClassConstants.INTEGER, sdoTypeHelper);
    public static final SDOType SDO_LONGOBJECT = new SDODataType(SDOJAVA_URL, LONGOBJECT, ClassConstants.LONG, sdoTypeHelper);
    public static final SDOType SDO_SHORTOBJECT = new SDODataType(SDOJAVA_URL, SHORTOBJECT, ClassConstants.SHORT, sdoTypeHelper);

    static {
        try {
            globalHelperContext = HelperProvider.getDefaultContext();
            sdoTypeHelper = (SDOTypeHelper) globalHelperContext.getTypeHelper();
        } catch(Throwable throwable) {
            globalHelperContext = null;
            sdoTypeHelper = null;
        }
    }

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
    public static final String APPINFO = "appinfo";

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
    public static final String XMLELEMENT_PROPERTY_NAME = "xmlElement";
    public static final QName SDOXML_MANY_QNAME = new QName(SDOXML_URL, SDOXML_MANY);
    public static final QName SDOXML_ALIASNAME_QNAME = new QName(SDOXML_URL, SDOXML_ALIASNAME);
    public static final QName SDOXML_NAME_QNAME = new QName(SDOXML_URL, SDOXML_NAME);
    public static final QName SDOXML_SEQUENCE_QNAME = new QName(SDOXML_URL, SDOXML_SEQUENCE);
    public static final QName SDOXML_READONLY_QNAME = new QName(SDOXML_URL, SDOXML_READONLY);
    public static final QName SDOXML_DATATYPE_QNAME = new QName(SDOXML_URL, SDOXML_DATATYPE);
    public static final QName SDOXML_STRING_QNAME = new QName(SDOXML_URL, SDOXML_STRING_NAME);
    public static final QName SDOXML_PROPERTYTYPE_QNAME = new QName(SDOXML_URL, SDOXML_PROPERTYTYPE);
    public static final QName SDOXML_OPPOSITEPROPERTY_QNAME = new QName(SDOXML_URL, SDOXML_OPPOSITEPROPERTY);
    public static final QName XML_ELEMENT_QNAME = new QName(SDOXML_URL, SDOConstants.XMLELEMENT_PROPERTY_NAME);
    public static final SDOProperty XMLELEMENT_PROPERTY = new SDOProperty(globalHelperContext, XMLELEMENT_PROPERTY_NAME, SDO_BOOLEAN);

    /** Strings and QNames for annotations defined in the commonj.sdo/XML namespace*/
    private static final String SDOJAVA_PACKAGE = "package";
    public static final String SDOJAVA_INSTANCECLASS = "instanceClass";
    public static final String SDOJAVA_EXTENDEDINSTANCECLASS = "extendedInstanceClass";
    public static final String SDOJAVA_NESTEDINTERFACES = "nestedInterfaces";
    public static final QName SDOJAVA_PACKAGE_QNAME = new QName(SDOJAVA_URL, SDOJAVA_PACKAGE);
    public static final QName SDOJAVA_INSTANCECLASS_QNAME = new QName(SDOJAVA_URL, SDOJAVA_INSTANCECLASS);
    public static final QName SDOJAVA_EXTENDEDINSTANCECLASS_QNAME = new QName(SDOJAVA_URL, SDOJAVA_EXTENDEDINSTANCECLASS);
    public static final QName SDOJAVA_NESTEDINTERFACES_QNAME = new QName(SDOJAVA_URL, SDOJAVA_NESTEDINTERFACES);
    public static final String MIME_TYPE_PROPERTY_NAME = "mimeType";
    public static final SDOProperty MIME_TYPE_PROPERTY = new SDOProperty(globalHelperContext, MIME_TYPE_PROPERTY_NAME, SDOConstants.SDO_STRING);
    public static final SDOProperty MIME_TYPE_PROPERTY_PROPERTY = new SDOProperty(globalHelperContext, MIMETYPE_NAME, SDOConstants.SDO_STRING);
    public static final String XML_SCHEMA_TYPE_NAME = "xmlSchemaType";
    public static final SDOProperty DOCUMENTATION_PROPERTY = new SDOProperty(globalHelperContext, DOCUMENTATION, SDOConstants.SDO_STRING);
    public static final String JAVACLASS_PROPERTY_NAME = "javaClass";

    public static final SDOProperty JAVA_CLASS_PROPERTY = new SDOProperty(globalHelperContext, SDOJAVA_URL, JAVACLASS_PROPERTY_NAME, SDOConstants.SDO_STRING);

    /** open content property to be set when defining a Type via a DataObject for reference relationships */
    public static final SDOProperty ID_PROPERTY = new SDOProperty(globalHelperContext, ID_PROPERTY_NAME, SDOConstants.SDO_STRING);

    public static final SDOProperty APPINFO_PROPERTY = new SDOProperty(globalHelperContext, APPINFO, SDOConstants.SDO_OBJECT, true);

    /** generate built-in open content property QNames */
    public static final QName MIME_TYPE_QNAME = new QName(ORACLE_SDO_URL, MIME_TYPE_PROPERTY.getName());
    public static final QName MIME_TYPE_PROPERTY_QNAME = new QName(ORACLE_SDO_URL, MIME_TYPE_PROPERTY_PROPERTY.getName());
    public static final QName SCHEMA_TYPE_QNAME = new QName(ORACLE_SDO_URL, XML_SCHEMA_TYPE_NAME);
    public static final QName JAVA_CLASS_QNAME = new QName(SDOJAVA_URL, JAVA_CLASS_PROPERTY.getName());
    public static final QName XML_DATATYPE_QNAME = new QName(SDOXML_URL, SDOXML_DATATYPE);
    public static final QName XML_ID_PROPERTY_QNAME = new QName(ORACLE_SDO_URL, ID_PROPERTY.getName());
    public static final QName DOCUMENTATION_PROPERTY_QNAME = new QName(ORACLE_SDO_URL, DOCUMENTATION);
    public static final QName APPINFO_PROPERTY_QNAME = new QName(ORACLE_SDO_URL, APPINFO);

    /** Strings used when generating javadocs in generated Java source files */
    public static final String JAVADOC_START = "/**";
    public static final String JAVADOC_LINE = " * ";
    public static final String JAVADOC_END = " */";

    /** Strings used when generating classes */
    public static final String JAVA_PACKAGE_NAME_SEPARATOR = ".";
    public static final String JAVA_TYPEGENERATION_DEFAULT_PACKAGE_NAME = "defaultPackage";
    public static final String JAVA_TYPEGENERATION_NO_NAMESPACE = "noNamespace";
    public static final String SDO_IMPL_NAME = "Impl";

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
    public static final String SDO_HELPER_CONTEXT = "sdoHelperContext";

    /** Name of source attribute on appinfo*/
    public static final String APPINFO_SOURCE_ATTRIBUTE = "source";

    /** empty string "" */
    public static final String EMPTY_STRING = "";

    /** reflective isSet method name */
    public static final String SDO_ISSET_METHOD_NAME = "isSet";

    /** SDO changeSummary reference path prefix string = # */
    public static final String SDO_CHANGESUMMARY_REF_PATH_PREFIX = "#";
    public static final int SDO_CHANGESUMMARY_REF_PATH_PREFIX_LENGTH = SDO_CHANGESUMMARY_REF_PATH_PREFIX.length();

    /** default implementation class java.util.HashMap */
    public static final String SDO_DATA_OBJECT_IMPL_CLASS_NAME = "org.eclipse.persistence.sdo.SDODataObject";

    // constants used during helperContext resolutions based on classloader
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

    static {
        if(null != sdoTypeHelper) {
            sdoTypeHelper.reset();
        }
    }
}
