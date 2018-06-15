/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink

package org.eclipse.persistence.tools.dbws;

//javase imports
import static java.sql.Types.BIGINT;
import static java.sql.Types.CHAR;
import static java.sql.Types.CLOB;
import static java.sql.Types.DATE;
import static java.sql.Types.DECIMAL;
import static java.sql.Types.DOUBLE;
import static java.sql.Types.FLOAT;
import static java.sql.Types.INTEGER;
import static java.sql.Types.LONGVARCHAR;
import static java.sql.Types.NCHAR;
import static java.sql.Types.NCLOB;
import static java.sql.Types.NUMERIC;
import static java.sql.Types.NVARCHAR;
import static java.sql.Types.REAL;
import static java.sql.Types.ROWID;
import static java.sql.Types.SMALLINT;
import static java.sql.Types.TIME;
import static java.sql.Types.TIMESTAMP;
import static java.sql.Types.TINYINT;
import static java.sql.Types.VARCHAR;
import static javax.xml.XMLConstants.DEFAULT_NS_PREFIX;
import static javax.xml.XMLConstants.NULL_NS_URI;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static org.eclipse.persistence.internal.helper.ClassConstants.BIGDECIMAL;
import static org.eclipse.persistence.internal.helper.ClassConstants.BOOLEAN;
import static org.eclipse.persistence.internal.helper.ClassConstants.JavaSqlDate_Class;
import static org.eclipse.persistence.internal.helper.ClassConstants.STRING;
import static org.eclipse.persistence.internal.oxm.Constants.BASE_64_BINARY_QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.COLON;
import static org.eclipse.persistence.internal.oxm.Constants.DATE_QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.DATE_TIME_QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.DECIMAL_QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.DOUBLE_QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.EMPTY_STRING;
import static org.eclipse.persistence.internal.oxm.Constants.INTEGER_QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.INT_QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.SCHEMA_PREFIX;
import static org.eclipse.persistence.internal.oxm.Constants.STRING_QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.TIME_QNAME;
import static org.eclipse.persistence.internal.xr.QNameTransformer.SCHEMA_QNAMES;
import static org.eclipse.persistence.internal.xr.Util.OPAQUE;
import static org.eclipse.persistence.internal.xr.XRDynamicClassLoader.COLLECTION_WRAPPER_SUFFIX;
import static org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormat.DEFAULT_SIMPLE_XML_FORMAT_TAG;
import static org.eclipse.persistence.tools.dbws.XRPackager.__nullStream;
import static org.eclipse.persistence.tools.oracleddl.metadata.ArgumentTypeDirection.INOUT;
import static org.eclipse.persistence.tools.oracleddl.metadata.ArgumentTypeDirection.OUT;

import java.io.OutputStream;
import java.sql.Types;
import java.util.List;
import java.util.regex.Pattern;

//java eXtension imports
import javax.xml.namespace.QName;

//EclipseLink imports
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.oxm.schema.model.Any;
import org.eclipse.persistence.internal.oxm.schema.model.ComplexType;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.internal.oxm.schema.model.Sequence;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.internal.xr.QueryOperation;
import org.eclipse.persistence.internal.xr.XRServiceModel;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.schema.XMLSchemaURLReference;
//DDL parser imports
import org.eclipse.persistence.tools.oracleddl.metadata.ArgumentType;
import org.eclipse.persistence.tools.oracleddl.metadata.BinaryType;
import org.eclipse.persistence.tools.oracleddl.metadata.BlobType;
import org.eclipse.persistence.tools.oracleddl.metadata.CharType;
import org.eclipse.persistence.tools.oracleddl.metadata.ClobType;
import org.eclipse.persistence.tools.oracleddl.metadata.DatabaseType;
import org.eclipse.persistence.tools.oracleddl.metadata.DecimalType;
import org.eclipse.persistence.tools.oracleddl.metadata.DoubleType;
import org.eclipse.persistence.tools.oracleddl.metadata.FloatType;
import org.eclipse.persistence.tools.oracleddl.metadata.FunctionType;
import org.eclipse.persistence.tools.oracleddl.metadata.LongRawType;
import org.eclipse.persistence.tools.oracleddl.metadata.NCharType;
import org.eclipse.persistence.tools.oracleddl.metadata.NClobType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLCursorType;
import org.eclipse.persistence.tools.oracleddl.metadata.ProcedureType;
import org.eclipse.persistence.tools.oracleddl.metadata.RawType;
import org.eclipse.persistence.tools.oracleddl.metadata.RealType;
import org.eclipse.persistence.tools.oracleddl.metadata.ScalarDatabaseTypeEnum;
import org.eclipse.persistence.tools.oracleddl.metadata.TimeStampType;
import org.eclipse.persistence.tools.oracleddl.metadata.VarChar2Type;

public class Util {

    // type for Stored Procedure Arguments
    public enum InOut {
        IN, OUT, INOUT, RETURN
    }

    public static final String TOPLEVEL = "TOPLEVEL";
    public static final String CLASSES = "classes";
    public static final String DEFAULT_WSDL_LOCATION_URI = "REPLACE_WITH_ENDPOINT_ADDRESS";
    public static final String SWAREF_FILENAME = XMLConstants.SWA_REF.toLowerCase() + ".xsd";
    // leave this duplicate as someone may be referencing it...
    public static final String WSI_SWAREF_XSD_FILE = SWAREF_FILENAME;
    public static final String WEB_XML_FILENAME = "web.xml";
    public static final String DEFAULT_PLATFORM_CLASSNAME = "org.eclipse.persistence.platform.database.OraclePlatform";
    public static final String DOM_PLATFORM_CLASSNAME = "org.eclipse.persistence.oxm.platform.DOMPlatform";
    public static final String UNDER_DBWS = "_dbws";
    public static final String OX_PRJ_SUFFIX = "-dbws-ox";
    public static final String OR_PRJ_SUFFIX = "-dbws-or";
    public static final String XML_BINDINGS = "xml-bindings";
    public static final String XML_MIME_PREFIX = "xmime";
    public static final String WSI_SWAREF = XMLConstants.SWA_REF;
    public static final String WSI_SWAREF_PREFIX = XMLConstants.REF_PREFIX;
    public static final String WSI_SWAREF_URI = XMLConstants.REF_URL;
    public static final String WSI_SWAREF_XSD =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n" +
        "<xsd:schema targetNamespace=\"" + WSI_SWAREF_URI + "\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"> \n" +
        "  <xsd:simpleType name=\"" + WSI_SWAREF + "\"> \n" +
        "    <xsd:restriction base=\"xsd:anyURI\"/> \n" +
        "  </xsd:simpleType> \n" +
        "</xsd:schema>";
    public static final String THE_INSTANCE_NAME = "theInstance";
    public static final String FINDALL_QUERYNAME = "findAll";
    public static final String CREATE_OPERATION_NAME = "create";
    public static final String UPDATE_OPERATION_NAME = "update";
    public static final String REMOVE_OPERATION_NAME = "delete";
    public static final String DBWS_PROVIDER_PACKAGE = "_dbws";
    public static final String DBWS_PROVIDER_NAME = "DBWSProvider";
    public static final String DOT_CLASS = ".class";
    static final String DOT_JAVA = ".java";
    public static final String DBWS_PROVIDER_CLASS_FILE = DBWS_PROVIDER_NAME + DOT_CLASS;
    public static final String DBWS_PROVIDER_SOURCE_FILE = DBWS_PROVIDER_NAME + DOT_JAVA;
    static final String PROVIDER_LISTENER = "ProviderListener";
    public static final String PROVIDER_LISTENER_CLASS_FILE = PROVIDER_LISTENER + DOT_CLASS;
    public static final String PROVIDER_LISTENER_SOURCE_FILE = PROVIDER_LISTENER + DOT_JAVA;

    public static final String AT_SIGN = "@";
    public static final String COMMA = ",";
    public static final String SEMICOLON = ";";
    public static final String SINGLE_SPACE = " ";
    public static final String PERCENT = "%";
    public static final String UNDERSCORE = "_";

    public static final String BUILDING_QUERYOP_FOR = "Building QueryOperation for ";
    public static final String APP_OCTET_STREAM = "application/octet-stream";
    public static final String TYPE_STR = "Type";
    public static final String CURSOR_STR = "CURSOR";
    public static final String CURSOR_OF_STR = "cursor of ";
    public static final String OPEN_PAREN = "{";
    public static final String CLOSE_PAREN = "}";
    public static final String SLASH = "/";
    public static final String DOT = ".";
    public static final String OPEN_BRACKET = "(";
    public static final String CLOSE_BRACKET = ")";
    public static final String OPEN_SQUARE_BRACKET = "[";
    public static final String CLOSE_SQUARE_BRACKET = "]";
    public static final String SLASH_TEXT = "/text()";

    public static final String ARRAY_STR = "ARRAY";
    public static final String BIGINT_STR = "BIGINT";
    public static final String BINARY_STR = "BINARY";
    public static final String BLOB_STR = "BLOB";
    public static final String BOOLEAN_STR = "BOOLEAN";
    public static final String CHAR_STR = "CHAR";
    public static final String CLOB_STR = "CLOB";
    public static final String DATE_STR = "DATE";
    public static final String DECIMAL_STR = "DECIMAL";
    public static final String DOUBLE_STR = "DOUBLE";
    public static final String FLOAT_STR = "FLOAT";
    public static final String INTEGER_STR = "INTEGER";
    public static final String LONG_STR = "LONG";
    public static final String LONGRAW_STR = "LONG RAW";
    public static final String LONGVARBINARY_STR = "LONGVARBINARY";
    public static final String NCHAR_STR = "NCHAR";
    public static final String NCLOB_STR = "NCLOB";
    public static final String NUMBER_STR = "NUMBER";
    public static final String NUMERIC_STR = "NUMERIC";
    public static final String NVARCHAR_STR = "NVARCHAR";
    public static final String NVARCHAR2_STR = "NVARCHAR2";
    public static final String OTHER_STR = "OTHER";
    public static final String RAW_STR = "RAW";
    public static final String REAL_STR = "REAL";
    public static final String ROWID_STR = "ROWID";
    public static final String ROWTYPE_STR = "%ROWTYPE";
    public static final String SMALLINT_STR = "SMALLINT";
    public static final String STRUCT_STR = "STRUCT";
    public static final String TIME_STR = "TIME";
    public static final String TIMESTAMP_STR = "TIMESTAMP";
    public static final String TINYINT_STR = "TINYINT";
    public static final String UROWID_STR = "UROWID";
    public static final String VARBINARY_STR = "VARBINARY";
    public static final String VARCHAR_STR = "VARCHAR";
    public static final String VARCHAR2_STR = "VARCHAR2";
    public static final String BINARY_INTEGER_STR = "BINARY_INTEGER";
    public static final String PLS_INTEGER_STR = "PLS_INTEGER";
    public static final String NATURAL_STR = "NATURAL";
    public static final String POSITIVE_STR = "POSITIVE";
    public static final String SIGNTYPE_STR = "SIGNTYPE";
    public static final String BINARY_INTEGER_TYPE_STR = "BinaryInteger";
    public static final String PLS_BOOLEAN_TYPE_STR = "PLSQLBoolean";
    public static final String PLS_INTEGER_TYPE_STR = "PLSQLInteger";
    public static final String NATURAL_TYPE_STR = "Natural";
    public static final String POSITIVE_TYPE_STR = "Positive";
    public static final String SIGNTYPE_TYPE_STR = "SignType";

    public static final String SYS_XMLTYPE_STR = "SYS.XMLTYPE";
    public static final String XMLTYPE_STR = "XMLTYPE";
    public static final String _TYPE_STR = "_TYPE";

    public static final String ARRAY_CLS_STR = "java.sql.Array";
    public static final String OPAQUE_CLS_STR = "java.sql.Struct";
    public static final String ROWID_CLS_STR = "java.sql.RowId";
    public static final String STRUCT_CLS_STR = "oracle.sql.OPAQUE";

    /**
     * Return the JDBC type (as int) for a given type name.
     */
    public static int getJDBCTypeFromTypeName(String typeName) {
        int jdbcType = Types.OTHER;
        if (typeName.equals(NUMERIC_STR)) {
            jdbcType = Types.NUMERIC;
        }
        else if (typeName.equals(VARCHAR_STR)) {
            jdbcType = Types.VARCHAR;
        }
        else if (typeName.equals(VARCHAR2_STR)) {
            jdbcType = Types.VARCHAR;
        }
        else if (typeName.equals(NVARCHAR_STR)) {
            jdbcType = Types.NVARCHAR;
        }
        else if (typeName.equals(NVARCHAR2_STR)) {
            jdbcType = Types.NVARCHAR;
        }
        else if (typeName.equals(DATE_STR)) {
            jdbcType = Types.DATE;
        }
        else if (typeName.equals(TIME_STR)) {
            jdbcType = Types.TIME;
        }
        else if (typeName.equals(TIMESTAMP_STR)) {
            jdbcType = Types.TIMESTAMP;
        }
        else if (typeName.equals(DECIMAL_STR)) {
            jdbcType = Types.DECIMAL;
        }
        else if (typeName.equals(INTEGER_STR)) {
            jdbcType = Types.INTEGER;
        }
        else if (typeName.equals(CHAR_STR)) {
            jdbcType = Types.CHAR;
        }
        else if (typeName.equals(NCHAR_STR)) {
            jdbcType = Types.NCHAR;
        }
        else if (typeName.equals(FLOAT_STR)) {
            jdbcType = Types.FLOAT;
        }
        else if (typeName.equals(REAL_STR)) {
            jdbcType = Types.REAL;
        }
        else if (typeName.equals(DOUBLE_STR)) {
            jdbcType = Types.DOUBLE;
        }
        else if (typeName.equals(BINARY_STR)) {
            jdbcType = Types.BINARY;
        }
        else if (typeName.equals(BLOB_STR)) {
            jdbcType = Types.BLOB;
        }
        else if (typeName.equals(CLOB_STR) ||
                 typeName.equals(LONG_STR))  {
            jdbcType = Types.CLOB;
        }
        else if (typeName.equals(NCLOB_STR)) {
            jdbcType = Types.NCLOB;
        }
        else if (typeName.equals(RAW_STR)) {
            jdbcType = Types.VARBINARY;
        }
        else if (typeName.equals(LONGRAW_STR)) {
            jdbcType = Types.LONGVARBINARY;
        }
        else if (typeName.equals(ROWID_STR)) {
            jdbcType = Types.VARCHAR;
        }
        else if (typeName.equals(UROWID_STR)) {
            jdbcType = Types.VARCHAR;
        }
        else if (typeName.equals(BIGINT_STR)) {
            jdbcType = Types.BIGINT;
        }
        else if (typeName.equals(STRUCT_STR)) {
            jdbcType = Types.STRUCT;
        }
        else if (typeName.equals(ARRAY_STR)) {
            jdbcType = Types.ARRAY;
        }
        else if (typeName.equals(ROWID_STR)) {
            jdbcType = Types.ROWID;
        } else if (typeName.equalsIgnoreCase(XMLTYPE_STR) ||
                  (typeName.equalsIgnoreCase(SYS_XMLTYPE_STR))) {
            jdbcType = Types.VARCHAR;
        }
        else if (typeName.equals(BOOLEAN_STR)  ||
                 typeName.equals(INTEGER_STR)  ||
                 typeName.equals(SMALLINT_STR) ||
                 typeName.equals(TINYINT_STR)) {
            jdbcType = Types.INTEGER;
        }
        return jdbcType;
    }

    /**
     * Return the JDBC type name for a given JDBC type code.
     */
    public static String getJDBCTypeNameFromType(int jdbcType) {
        String typeName = null;
        switch (jdbcType) {
            case Types.NUMERIC:
                typeName = NUMERIC_STR;
                break;
            case Types.VARCHAR:
                typeName = VARCHAR_STR;
                break;
            case Types.NVARCHAR:
                typeName = NVARCHAR_STR;
                break;
            case Types.DECIMAL:
                typeName = DECIMAL_STR;
                break;
            case Types.CHAR:
                typeName = CHAR_STR;
                break;
            case Types.NCHAR:
                typeName = NCHAR_STR;
                break;
            case Types.FLOAT:
                typeName = FLOAT_STR;
                break;
            case Types.REAL:
                typeName = REAL_STR;
                break;
            case Types.DOUBLE:
                typeName = DOUBLE_STR;
                break;
            case Types.BINARY:
                typeName = BINARY_STR;
                break;
            case Types.BLOB:
                typeName = BLOB_STR;
                break;
            case Types.CLOB:
                typeName = CLOB_STR;
                break;
            case Types.NCLOB:
                typeName = NCLOB_STR;
                break;
            case Types.VARBINARY:
                typeName = VARBINARY_STR;
                break;
            case Types.LONGVARBINARY:
                typeName = LONGVARBINARY_STR;
                break;
            case Types.DATE:
                typeName = DATE_STR;
                break;
            case Types.TIME:
                typeName = TIME_STR;
                break;
            case Types.TIMESTAMP:
                typeName = TIMESTAMP_STR;
                break;
            case Types.BIGINT:
                typeName = BIGINT_STR;
                break;
            case Types.ARRAY:
                typeName = ARRAY_STR;
                break;
            case Types.STRUCT:
                typeName = STRUCT_STR;
                break;
            case Types.ROWID:
                typeName = ROWID_STR;
                break;
            default:
                typeName = OTHER_STR;
                break;
        }
        return typeName;
    }

    /**
     * Return the JDBC enum name for a given JDBC type name.
     */
    public static String getJDBCEnumNameFromTypeName(String typeName) {
        if (getJDBCTypeFromTypeName(typeName) != Types.OTHER) {
            typeName += _TYPE_STR;
        }
        return typeName;
    }

    public static final QName SXF_QNAME_CURSOR = new QName("", "cursor of " + DEFAULT_SIMPLE_XML_FORMAT_TAG);
    /**
     * Return the XML type (as a QName) associated with the
     * given JDBC type name.
     */
    public static QName getXMLTypeFromJDBCTypeName(String jdbcTypeName) {
        return getXMLTypeFromJDBCType(getJDBCTypeFromTypeName(jdbcTypeName));
    }
    /**
     * Return the XML type (as a QName) associated with the
     * given JDBC type.
     */
    public static QName getXMLTypeFromJDBCType(Short jdbcType) {
        return getXMLTypeFromJDBCType(jdbcType.intValue());
    }
    /**
     * Return the XML type (as a QName) associated with the
     * given JDBC type.
     */
    public static QName getXMLTypeFromJDBCType(int jdbcType) {
        switch (jdbcType) {
            case CHAR:
            case NCHAR:
            case LONGVARCHAR:
            case VARCHAR:
            case NVARCHAR:
            case CLOB:
            case NCLOB:
            case ROWID:
                return STRING_QNAME;
            case BIGINT:
                return INTEGER_QNAME;
            case INTEGER:
            case SMALLINT:
            case TINYINT:
                return INT_QNAME;
            case NUMERIC:
            case DECIMAL:
                return DECIMAL_QNAME;
            case DOUBLE:
            case FLOAT:
            case REAL:
                return DOUBLE_QNAME;
            case DATE:
                return DATE_QNAME;
            case TIME:
                return TIME_QNAME;
            case TIMESTAMP:
                return DATE_TIME_QNAME;
        default:
            return BASE_64_BINARY_QNAME;
        }
    }

    public static QName qNameFromString(String qNameAsString, Schema schema) {

        QName qName = null;
        String nsURI = null;
        String prefix = null;
        String localPart = null;
        if (qNameAsString != null) {
            if (qNameAsString.charAt(0) == '{') {
                int endOfNamespaceURI = qNameAsString.indexOf('}');
                if (endOfNamespaceURI == -1) {
                    throw new IllegalArgumentException(
                       "cannot create QName from \"" + qNameAsString + "\", missing closing \"}\"");
                }
                nsURI = qNameAsString.substring(1, endOfNamespaceURI);
                localPart = qNameAsString.substring(endOfNamespaceURI + 1);
            }
            else {
                int colonIdx = qNameAsString.indexOf(':');
                if (colonIdx > 0) {
                    prefix = qNameAsString.substring(0, colonIdx);
                    localPart = qNameAsString.substring(colonIdx+1);
                    nsURI = schema.getNamespaceResolver().resolveNamespacePrefix(prefix);
                    if (nsURI == null) {
                        if (prefix.equalsIgnoreCase(SCHEMA_PREFIX)) {
                            nsURI = W3C_XML_SCHEMA_NS_URI;
                        }
                        else {
                            nsURI = DEFAULT_NS_PREFIX;
                        }
                    }
                }
                else {
                    localPart = qNameAsString;
                }
            }
            // check for W3C_XML_SCHEMA_NS_URI - return TL_OX pre-built QName's
            if (W3C_XML_SCHEMA_NS_URI.equals(nsURI)) {
                qName = SCHEMA_QNAMES.get(localPart);
                if (qName == null) { // unknown W3C_XML_SCHEMA_NS_URI type ?
                    qName = new QName(W3C_XML_SCHEMA_NS_URI, localPart,
                        prefix == null ? DEFAULT_NS_PREFIX : prefix);
                }
            }
            else {
                qName = new QName(nsURI == null ? NULL_NS_URI : nsURI,
                    localPart, prefix == null ? DEFAULT_NS_PREFIX : prefix);
            }
        }
        return qName;
    }

    /**
     * Returns the class name associated with a given JDBC/SQL type code.
     *
     */
    public static String getClassNameForType(int jdbcType) {
        String typeName = null;
        switch (jdbcType) {
            case Types.NUMERIC:
                typeName = ClassConstants.NUMBER.getName();
                break;
            case Types.DECIMAL:
                typeName = ClassConstants.BIGDECIMAL.getName();
                break;
            case Types.CHAR:
                typeName = ClassConstants.CHAR.getName();
                break;
            case Types.FLOAT:
                typeName = ClassConstants.FLOAT.getName();
                break;
            case Types.DOUBLE:
            case Types.REAL:
                typeName = ClassConstants.DOUBLE.getName();
                break;
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
                typeName = ClassConstants.ABYTE.getName();
                break;
            case Types.BLOB:
                typeName = ClassConstants.BLOB.getName();
                break;
            case Types.CLOB:
                typeName = ClassConstants.CLOB.getName();
                break;
            case Types.NCLOB:
                typeName = "java.sql.NClob";
                break;
            case Types.DATE:
                typeName = ClassConstants.SQLDATE.getName();
                break;
            case Types.TIME:
                typeName = ClassConstants.TIME.getName();
                break;
            case Types.TIMESTAMP:
                typeName = ClassConstants.TIMESTAMP.getName();
                break;
            case Types.BIGINT:
                typeName = ClassConstants.BIGINTEGER.getName();
                break;
            case Types.ARRAY:
                typeName = ARRAY_CLS_STR;
                break;
            case Types.STRUCT:
                typeName = STRUCT_CLS_STR;
                break;
            case Types.ROWID:
                typeName = ROWID_CLS_STR;
                break;
            case OPAQUE:
                typeName = OPAQUE_CLS_STR;
                break;
            default:
                typeName = ClassConstants.STRING.getName();
                break;
        }
        return typeName;
    }

    /**
     * Return a DatabaseType instance for a given JDCBType.  If applicable, precision
     * and scale values will be applied.  The default type instance will be
     * VarChar2Type.
     *
     */
    public static DatabaseType buildTypeForJDBCType(int jdbcType, int precision, int scale) {
        DatabaseType type = null;
        switch (jdbcType) {
            case Types.BINARY:
                type = new BinaryType();
                break;
            case Types.BLOB:
                type = new BlobType();
                break;
            case Types.CHAR:
                type = new CharType();
                break;
            case Types.CLOB:
                type = new ClobType();
                break;
            case Types.DATE:
                type = ScalarDatabaseTypeEnum.DATE_TYPE;
                break;
            case Types.BIGINT:
                type = ScalarDatabaseTypeEnum.BIGINT_TYPE;
                break;
            case Types.DECIMAL:
            case Types.NUMERIC:
                type = new DecimalType(precision, scale);
                break;
            case Types.DOUBLE:
                type = new DoubleType(precision, scale);
                break;
            case Types.FLOAT:
                type = new FloatType(precision, scale);
                break;
            case Types.LONGVARBINARY:
                type = new LongRawType();
                break;
            case Types.NCHAR:
                type = new NCharType();
                break;
            case Types.NCLOB:
                type = new NClobType();
                break;
            case Types.REAL:
                type = new RealType(precision, scale);
                break;
            case Types.TIME:
                type = ScalarDatabaseTypeEnum.TIME_TYPE;
                break;
            case Types.TIMESTAMP:
                type = new TimeStampType();
                break;
            case Types.VARBINARY:
                type = new RawType();
                break;
            default:
                type = new VarChar2Type();
                break;
        }
        return type;
    }

    /**
     * Get the attribute class for a given DatabaseType.
     */
    public static Class<?> getAttributeClassForDatabaseType(DatabaseType dbType) {
        if (!dbType.isComposite()) {
            String typeName = dbType.getTypeName();
            if (NUMBER_STR.equals(typeName) || NUMERIC_STR.equals(typeName)) {
                return BIGDECIMAL;
            }
            if (INTEGER_STR.equals(typeName)) {
                return org.eclipse.persistence.internal.helper.ClassConstants.INTEGER;
            }
            if (BOOLEAN_STR.equals(typeName)) {
                return BOOLEAN;
            }
            if (DATE_STR.equals(typeName)) {
                return JavaSqlDate_Class;
            }
        }
        return STRING;
    }

    /*
      <?xml version="1.0" encoding="UTF-8"?>
      <xsd:schema
        xmlns:xsd="http://www.w3.org/2001/XMLSchema"
        >
        <xsd:complexType name="simple-xml-format">
          <xsd:sequence>
            <xsd:any minOccurs="0"/>
          </xsd:sequence>
        </xsd:complexType>
      </xsd:schema>
    */
    public static void addSimpleXMLFormat(Schema schema) {
        ComplexType anyType = new ComplexType();
        anyType.setName(DEFAULT_SIMPLE_XML_FORMAT_TAG);
        Sequence anySequence = new Sequence();
        Any any = new Any();
        any.setMinOccurs("0");
        anySequence.addAny(any);
        anyType.setSequence(anySequence);
        schema.addTopLevelComplexTypes(anyType);
    }

    public static boolean noOutArguments(ProcedureType storedProcedure) {

        boolean noOutArguments = true;
        if (storedProcedure.getArguments() != null &&
             storedProcedure.getArguments().size() > 0) {
            for (ArgumentType arg : storedProcedure.getArguments()) {
                if (arg.getDirection() ==  INOUT || arg.getDirection() == OUT) {
                    noOutArguments = false;
                    break;
                }
            }
        }
        return noOutArguments;
    }

    public static String escapePunctuation(String originalName) {
        if (originalName == null || originalName.length() == 0) {
            return originalName;
        }
        // escape all punctuation except SQL 'LIKE' meta-characters:
        // '_' (underscore) - matches any one character) and
        // '%' (percent ) - matches a string of zero or more characters
        return originalName.trim().replaceAll("[\\p{Punct}&&[^_%]]", "\\\\$0");
    }

    public static boolean isNullStream(OutputStream outputStream) {
        return (outputStream == null | outputStream == __nullStream);
    }

    /**
     * Returns a Java class name based on a given name and project.  The returned
     * string  will be  in the format  'projectname.Name'.   For  example,  given
     * the name 'EMPLOYEE'  and projectName 'TEST', the  method would return  the
     * string 'test.Employee'.
     *
     */
    public static String getGeneratedJavaClassName(String name, String projectName) {
        String first = name.substring(0, 1).toUpperCase();
        String rest = name.toLowerCase().substring(1);
        return projectName.toLowerCase() + DOT + first + rest;
    }

    /**
     * Returns an alias (typically used as a descriptor alias) based on a given
     * table or type name.  The returned string will contain an upper case
     * first char, with the remaining chars in lower case format.
     */
    public static String getGeneratedAlias(String tableName) {
        String first = tableName.substring(0, 1).toUpperCase();
        String rest = tableName.toLowerCase().substring(1);
        return first.concat(rest);
    }

    /**
     * Returns a Java class name based on a given name and project.  The  returned
     * string  will be  in the format  'projectname.Name_CollectionWrapper'.   For
     * example, given the name 'EMPLOYEE' and projectName 'TEST', the method would
     * return the string 'test.Employee_CollectionWrapper'.
     *
     */
    public static String getGeneratedWrapperClassName(String name, String projectName) {
        return getGeneratedJavaClassName(name, projectName) + COLLECTION_WRAPPER_SUFFIX;
    }

    /**
     * Build a RelationalDescriptor for a given table.
     */
    public static RelationalDescriptor buildORDescriptor(String tableName, String projectName,
        List<String> requireCRUDOperations, NamingConventionTransformer nct) {
        RelationalDescriptor desc = new RelationalDescriptor();
        desc.addTableName(tableName);
        String tableAlias = getGeneratedAlias(tableName);
        desc.setAlias(tableAlias);
        desc.setJavaClassName(getGeneratedJavaClassName(tableName, projectName));
        desc.useWeakIdentityMap();
        // keep track of which tables require CRUD operations
        if (requireCRUDOperations != null) {
            requireCRUDOperations.add(tableAlias);
        }
        return desc;
    }

    /**
     * Build an XMLDescriptor for a given table.
     */
    public static XMLDescriptor buildOXDescriptor(String tableName, String projectName, String targetNamespace, NamingConventionTransformer nct) {
        return buildOXDescriptor(getGeneratedAlias(tableName), nct.generateSchemaAlias(tableName), getGeneratedJavaClassName(tableName, projectName), targetNamespace);
    }

    /**
     * Build an XMLDescriptor for a given table.
     */
    public static XMLDescriptor buildOXDescriptor(String tableAlias, String schemaAlias, String generatedJavaClassName, String targetNamespace) {
        XMLDescriptor xdesc = new XMLDescriptor();
        xdesc.setAlias(tableAlias);
        xdesc.setJavaClassName(generatedJavaClassName);

        NamespaceResolver nr = new NamespaceResolver();
        nr.setDefaultNamespaceURI(targetNamespace);
        xdesc.setNamespaceResolver(nr);
        xdesc.setDefaultRootElement(schemaAlias);

        XMLSchemaURLReference schemaReference = new XMLSchemaURLReference(EMPTY_STRING);
        schemaReference.setSchemaContext(SLASH + schemaAlias);
        schemaReference.setType(org.eclipse.persistence.platform.xml.XMLSchemaReference.COMPLEX_TYPE);
        xdesc.setSchemaReference(schemaReference);
        return xdesc;
    }

    public static QName buildCustomQName(String typeString, DBWSBuilder builder) {
        // for %TYPE and %ROWTYPE we'll need to replace the '%' with '_'
        if (typeString.contains(PERCENT)) {
            typeString = typeString.replace(PERCENT, UNDERSCORE);
        }

        QName qName = null;
        String nsURI = null;
        String prefix = null;
        String localPart = null;
        int colonIdx = typeString.indexOf(COLON);
        if (colonIdx > 0) {
            prefix = typeString.substring(0, colonIdx);
            nsURI = builder.schema.getNamespaceResolver().resolveNamespacePrefix(prefix);
            if (prefix.equalsIgnoreCase(SCHEMA_PREFIX)) {
                nsURI = W3C_XML_SCHEMA_NS_URI;
            } else {
                nsURI = DEFAULT_NS_PREFIX;
            }
            localPart = typeString.substring(colonIdx+1);
            if (W3C_XML_SCHEMA_NS_URI.equals(nsURI)) {
                qName = SCHEMA_QNAMES.get(localPart);
                if (qName == null) { // unknown W3C_XML_SCHEMA_NS_URI type ?
                    qName = new QName(W3C_XML_SCHEMA_NS_URI, localPart,
                        prefix == null ? DEFAULT_NS_PREFIX : prefix);
                }
            } else {
                qName = new QName(nsURI, localPart, prefix);
            }
        } else {
            qName = qNameFromString(OPEN_PAREN + builder.getTargetNamespace() +
                CLOSE_PAREN + typeString, builder.schema);
        }
        return qName;
    }

    public static boolean requiresSimpleXMLFormat(XRServiceModel serviceModel) {
        boolean requiresSimpleXMLFormat = false;
        for (Operation operation : serviceModel.getOperationsList()) {
            if (operation instanceof QueryOperation) {
                QueryOperation qo = (QueryOperation)operation;
                if (qo.getResult().isSimpleXMLFormat()) {
                    requiresSimpleXMLFormat = true;
                    break;
                }
            }
        }
        return requiresSimpleXMLFormat;
    }

    public static boolean sqlMatch(String pattern, String input) {
        if (pattern != null && pattern.length() > 0) {
            //add support for positive 'empty' pattern matching
            String tmp = "\\A\\Z|" + pattern.replace('_', '.').replace("%", ".*");
            Pattern p = Pattern.compile(tmp, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            return p.matcher(input == null ? "" : input).matches();
        }
        return false;
    }

    /**
     * Indicates if a given DatabaseType is considered 'complex', i.e.
     * is one of PLSQLRecordType, PLSQLCollectionType, VArrayType,
     * ObjectType, or NestedTableType
     */
    public static boolean isTypeComplex(DatabaseType dbType) {
        return dbType.isPLSQLType()
                || (dbType.isPLSQLCursorType() && !((PLSQLCursorType)dbType).isWeaklyTyped())
                || dbType.isVArrayType()
                || dbType.isObjectType()
                || dbType.isBlobType()
                || dbType.isObjectTableType();
    }

    /**
     * Indicates if a given ArgumentType is considered 'complex', i.e. it has
     * a data type that is one of PLSQLRecordType, PLSQLCollectionType,
     * VArrayType, ObjectType, or NestedTableType
     */
    public static boolean isArgComplex(ArgumentType argument) {
        return isTypeComplex(argument.getEnclosedType());
    }

    /**
     * Indicates if a given ArgumentType is considered a PL/SQL argument,
     * i.e. it has a data type that is one of PLSQLRecordType,
     * PLSQLCollectionType, BOOLEAN_TYPE, BINARY_INTEGER_TYPE,
     * PLS_INTEGER_TYPE, etc.
     */
    public static boolean isArgPLSQL(ArgumentType argument) {
        DatabaseType argType = argument.getEnclosedType();
        return argType.isPLSQLType() || isArgPLSQLScalar(argument);
    }

    /**
     * Indicates if a given ArgumentType is considered a PL/SQL scalar
     * argument, i.e. it has a data type that is one of BOOLEAN_TYPE,
     * BINARY_INTEGER_TYPE, PLS_INTEGER_TYPE, etc.
     */
    public static boolean isArgPLSQLScalar(ArgumentType argument) {
        DatabaseType argType = argument.getEnclosedType();
        return argType == ScalarDatabaseTypeEnum.BINARY_INTEGER_TYPE
            || argType == ScalarDatabaseTypeEnum.BOOLEAN_TYPE
            || argType == ScalarDatabaseTypeEnum.NATURAL_TYPE
            || argType == ScalarDatabaseTypeEnum.PLS_INTEGER_TYPE
            || argType == ScalarDatabaseTypeEnum.POSITIVE_TYPE
            || argType == ScalarDatabaseTypeEnum.SIGN_TYPE;
    }

    /**
     * Indicates if a given argument type name is considered a PL/SQL scalar
     * argument, i.e. is one of BOOLEAN, BINARY_INTEGER, PLS_INTEGER, etc.
     */
    public static boolean isArgPLSQLScalar(String argTypeName) {
        return argTypeName.equals("BOOLEAN")
            || argTypeName.equals("PLS_INTEGER")
            || argTypeName.equals("BINARY_INTEGER")
            || argTypeName.equals("NATURAL")
            || argTypeName.equals("POSITIVE")
            || argTypeName.equals("SIGNTYPE");
    }

    /**
     * Return the Oracle PL/SQL name for a given PL/SQL scalar type.
     */
    public static String getOraclePLSQLTypeForName(String typeName) {
        if (typeName.equals(BINARY_INTEGER_STR))
            return BINARY_INTEGER_TYPE_STR;
        if (typeName.equals(BOOLEAN_STR))
            return PLS_BOOLEAN_TYPE_STR;
        if (typeName.equals(PLS_INTEGER_STR))
            return PLS_INTEGER_TYPE_STR;
        if (typeName.equals(NATURAL_STR))
            return NATURAL_TYPE_STR;
        if (typeName.equals(POSITIVE_STR))
            return POSITIVE_TYPE_STR;
        if (typeName.equals(SIGNTYPE_STR))
            return SIGNTYPE_TYPE_STR;
        return null;
    }

    /**
     * Indicates if a given ProcedureType contains one or more arguments that
     * are considered 'complex', i.e. PLSQLRecordType, PLSQLCollectionType,
     * VArrayType, ObjectType, or NestedTableType.
     *
     * Note that for FunctionType the return argument is tested as well.
     */
    public static boolean hasComplexArgs(ProcedureType storedProcedure) {
        for (ArgumentType arg : storedProcedure.getArguments()) {
            if (isArgComplex(arg)) {
                return true;
            }
        }
        if (storedProcedure.isFunctionType()) {
            if (isArgComplex(((FunctionType)storedProcedure).getReturnArgument())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Indicates if a given ProcedureType contains  one or more  PL/SQL
     * arguments, i.e. PLSQLRecordType, PLSQLCollectionType, or scalars
     * BOOLEAN_TYPE, BINARY_INTEGER_TYPE, PLS_INTEGER_TYPE, etc.
     *
     * Note that for FunctionType the return argument is tested as well.
     */
    public static boolean hasPLSQLArgs(ProcedureType storedProcedure) {
        for (ArgumentType arg : storedProcedure.getArguments()) {
            if (isArgPLSQL(arg)) {
                return true;
            }
        }
        if (storedProcedure.isFunctionType()) {
            if (isArgPLSQL(((FunctionType)storedProcedure).getReturnArgument())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Indicates if a given list  of ArgumentTypes contains  one or more
     * PL/SQL arguments, i.e.  PLSQLRecordType, PLSQLCollectionType,  or
     * scalars BOOLEAN_TYPE, BINARY_INTEGER_TYPE, PLS_INTEGER_TYPE, etc.
     *
     * In addition, an optional argument must be treated as PL/SQL as
     * default/optional parameter handling is done via
     * PLSQLStoredProcedureCall
     */
    public static boolean hasPLSQLArgs(List<ArgumentType> arguments) {
        for (ArgumentType arg : arguments) {
            if (isArgPLSQL(arg) || arg.optional()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Indicates if a given List&lt;ArgumentType&gt; contains one or more arguments that
     * are considered 'complex', i.e. PLSQLRecordType, PLSQLCollectionType,
     * VArrayType, ObjectType, or NestedTableType
     */
    public static boolean hasComplexArgs(List<ArgumentType> arguments) {
        for (ArgumentType arg : arguments) {
            if (isArgComplex(arg)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Indicates if a given List&lt;ArgumentType&gt; contains one or more arguments
     * that are considered PL/SQL scalars, i.e. PLSQLBoolean, PLSQLInteger,
     * BinaryInteger, etc.
     */
    public static boolean hasPLSQLScalarArgs(List<ArgumentType> arguments) {
        for (ArgumentType arg : arguments) {
            if (isArgPLSQLScalar(arg)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Indicates if a given List&lt;ArgumentType&gt; contains a PL/SQL Cursor
     * type.
     */
    public static boolean hasPLSQLCursorArg(List<ArgumentType> arguments) {
        for (ArgumentType arg : arguments) {
            if (arg.isPLSQLCursorType()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Convenience method used to determine if the java class and/or java type name
     * should be set on a given procedure argument.  This will typically be used
     * when calling addNamedInOutputArgument on a stored procedure call.
     *
     */
    public static boolean shouldSetJavaType(String typeName) {
        if (typeName.equals(ClassConstants.STRING.getName())) {
            return false;
        }
        return true;
    }
}
