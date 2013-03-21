/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.5 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.oxm;

import javax.xml.namespace.QName;

public class Constants {

    public static final String ANY_NAMESPACE_ANY = "##any";
    public static final String ANY_NAMESPACE_OTHER = "##other";
    public static final Character ATTRIBUTE = '@';
    public static final String BOOLEAN_STRING_TRUE = "true";
    public static final String CDATA = "CDATA";
    public static final char COLON = ':';
    public static final String DEFAULT_XML_ENCODING = "UTF-8";
    public static final char DOT = '.';
    public static final String EMPTY_STRING = "";
    public static final String EXPECTED_CONTENT_TYPES = "expectedContentTypes";
    public static final String JAXB_FRAGMENT = "jaxb.fragment";
    public static final String JAXB_MARSHALLER = "jaxb.marshaller";
    public static final String LEXICAL_HANDLER_PROPERTY = "http://xml.org/sax/properties/lexical-handler";
    public static final Class LOCATOR_CLASS = org.xml.sax.Locator.class;
    public static final String LOCATOR_CLASS_NAME = "org.xml.sax.Locator";
    public static final String NO_NS_SCHEMA_LOCATION = "noNamespaceSchemaLocation";
    public static final Class QNAME_CLASS = QName.class;
    public static final String REF_PREFIX = "ref";
    public static final String REF_URL = "http://ws-i.org/profiles/basic/1.1/xsd";
    public static final String SCHEMA_LOCATION = "schemaLocation";
    public static final String SCHEMA_PREFIX = "xsd";
    public static final String SCHEMA_INSTANCE_PREFIX = "xsi";
    public static final String SCHEMA_NIL_ATTRIBUTE = "nil";
    public static final String SCHEMA_TYPE_ATTRIBUTE = "type";
    public static final String SWAREF_XSD = "http://ws-i.org/profiles/basic/1.1/swaref.xsd";
    public static final String TEXT = "text()";
    public static final String UNKNOWN_OR_TRANSIENT_CLASS = "UNKNOWN_OR_TRANSIENT_CLASS";
    public static final Class URI = java.net.URI.class;
    public static final Class UUID = java.util.UUID.class;
    public static final String VALUE_WRAPPER = "value";
    public static final String XML_MIME_URL = "http://www.w3.org/2005/05/xmlmime";
    public static final String XML_NAMESPACE_SCHEMA_LOCATION = "http://www.w3.org/XML/2001/xml.xsd";
    public static final String XPATH_SEPARATOR = "/";
    public static final String XPATH_INDEX_OPEN = "[";
    public static final String XPATH_INDEX_CLOSED = "]";
    public static final String XOP_PREFIX = "xop";
    public static final String XOP_URL = "http://www.w3.org/2004/08/xop/include";

    // Schema Special values for Double and Float
    public static final String POSITIVE_INFINITY = "INF";
    public static final String NEGATIVE_INFINITY = "-INF";

    // Built-in Schema Types
    public static final String ANY = "any";
    public static final String BASE_64_BINARY = "base64Binary";
    public static final String BOOLEAN = "boolean";
    public static final String BYTE = "byte";
    public static final String DATE = "date";
    public static final String DATE_TIME = "dateTime";
    public static final String DECIMAL = "decimal";
    public static final String DOUBLE = "double";
    public static final String DURATION = "duration";
    public static final String FLOAT = "float";
    public static final String G_DAY = "gDay";
    public static final String G_MONTH = "gMonth";
    public static final String G_MONTH_DAY = "gMonthDay";
    public static final String G_YEAR = "gYear";
    public static final String G_YEAR_MONTH = "gYearMonth";
    public static final String HEX_BINARY = "hexBinary";
    public static final String INT = "int";
    public static final String INTEGER = "integer";
    public static final String LONG = "long";
    public static final String NAME = "Name";
    public static final String NCNAME = "NCName";
    public static final String NEGATIVE_INTEGER = "negativeInteger";
    public static final String NON_NEGATIVE_INTEGER = "nonNegativeInteger";
    public static final String NON_POSITIVE_INTEGER = "nonPositiveInteger";
    public static final String NOTATION = "NOTATION";
    public static final String POSITIVE_INTEGER = "positiveInteger";
    public static final String NORMALIZED_STRING = "normalizedString";
    public static final String QNAME = "QName";
    public static final String QUALIFIED = "qualified";
    public static final String SHORT = "short";
    public static final String STRING = "string";
    public static final String TIME = "time";
    public static final String UNQUALIFIED = "unqualified";
    public static final String UNSIGNED_BYTE = "unsignedByte";
    public static final String UNSIGNED_INT = "unsignedInt";
    public static final String UNSIGNED_SHORT = "unsignedShort";
    public static final String UNSIGNED_LONG = "unsignedLong";
    public static final String ANY_SIMPLE_TYPE = "anySimpleType";
    public static final String ANY_TYPE = "anyType";
    public static final String ANY_URI = "anyURI";
    public static final String SWA_REF = "swaRef";

    // Schema Type QNames
    public static final QName ANY_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, ANY);
    public static final QName ANY_SIMPLE_TYPE_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, ANY_SIMPLE_TYPE);
    public static final QName ANY_TYPE_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, ANY_TYPE);
    public static final QName ANY_URI_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, ANY_URI);
    public static final QName BASE_64_BINARY_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, BASE_64_BINARY);
    public static final QName HEX_BINARY_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, HEX_BINARY);
    public static final QName DATE_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, DATE);
    public static final QName TIME_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, TIME);
    public static final QName DATE_TIME_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, DATE_TIME);
    public static final QName BOOLEAN_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, BOOLEAN);
    public static final QName BYTE_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, BYTE);
    public static final QName DECIMAL_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, DECIMAL);
    public static final QName DOUBLE_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, DOUBLE);
    public static final QName DURATION_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, DURATION);
    public static final QName FLOAT_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, FLOAT);
    public static final QName G_DAY_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, G_DAY);
    public static final QName G_MONTH_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, G_MONTH);
    public static final QName G_MONTH_DAY_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, G_MONTH_DAY);
    public static final QName G_YEAR_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, G_YEAR);
    public static final QName G_YEAR_MONTH_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, G_YEAR_MONTH);
    public static final QName INT_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, INT);
    public static final QName INTEGER_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, INTEGER);
    public static final QName LONG_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, LONG);
    public static final QName NAME_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, NAME);
    public static final QName NCNAME_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, NCNAME);
    public static final QName NEGATIVE_INTEGER_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, NEGATIVE_INTEGER);
    public static final QName NON_NEGATIVE_INTEGER_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, NON_NEGATIVE_INTEGER);
    public static final QName NON_POSITIVE_INTEGER_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, NON_POSITIVE_INTEGER);
    public static final QName NOTATION_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, NOTATION);    
    public static final QName POSITIVE_INTEGER_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, POSITIVE_INTEGER);
    public static final QName NORMALIZEDSTRING_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, NORMALIZED_STRING);
    public static final QName QNAME_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, QNAME);
    public static final QName SHORT_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, SHORT);
    public static final QName STRING_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, STRING);
    public static final QName UNSIGNED_BYTE_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, UNSIGNED_BYTE);
    public static final QName UNSIGNED_INT_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, UNSIGNED_INT);
    public static final QName UNSIGNED_SHORT_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, UNSIGNED_SHORT);
    public static final QName UNSIGNED_LONG_QNAME = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, UNSIGNED_LONG);
    public static final QName SWA_REF_QNAME = new QName(REF_URL, SWA_REF);
    public static final QName EXPECTED_CONTENT_TYPES_QNAME = new QName(XML_MIME_URL, EXPECTED_CONTENT_TYPES);

    public static final MediaType APPLICATION_JSON = new MediaType() {

        @Override
        public boolean isApplicationJSON() {
            return true;
        }

        @Override
        public boolean isApplicationXML() {
            return false;
        }
        
    };

    public static final MediaType APPLICATION_XML = new MediaType() {

        @Override
        public boolean isApplicationJSON() {
            return false;
        }

        @Override
        public boolean isApplicationXML() {
            return true;
        }
        
    };

}