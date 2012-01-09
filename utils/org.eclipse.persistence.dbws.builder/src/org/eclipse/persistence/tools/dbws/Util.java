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

package org.eclipse.persistence.tools.dbws;

//javase imports
import java.io.OutputStream;
import java.sql.Types;
import java.util.List;
import java.util.regex.Pattern;
import static java.sql.Types.BIGINT;
import static java.sql.Types.NCHAR;
import static java.sql.Types.CHAR;
import static java.sql.Types.CLOB;
import static java.sql.Types.NCLOB;
import static java.sql.Types.DATE;
import static java.sql.Types.DECIMAL;
import static java.sql.Types.DOUBLE;
import static java.sql.Types.FLOAT;
import static java.sql.Types.INTEGER;
import static java.sql.Types.LONGVARCHAR;
import static java.sql.Types.NUMERIC;
import static java.sql.Types.REAL;
import static java.sql.Types.SMALLINT;
import static java.sql.Types.TIME;
import static java.sql.Types.TIMESTAMP;
import static java.sql.Types.TINYINT;
import static java.sql.Types.VARCHAR;

//java eXtension imports
import javax.xml.namespace.QName;
import static javax.xml.XMLConstants.DEFAULT_NS_PREFIX;
import static javax.xml.XMLConstants.NULL_NS_URI;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

//EclipseLink imports
import org.eclipse.persistence.descriptors.RelationalDescriptor;
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
import static org.eclipse.persistence.internal.xr.QNameTransformer.SCHEMA_QNAMES;
import static org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormat.DEFAULT_SIMPLE_XML_FORMAT_TAG;
import static org.eclipse.persistence.oxm.XMLConstants.BASE_64_BINARY_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.COLON;
import static org.eclipse.persistence.oxm.XMLConstants.DATE_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.DATE_TIME_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.DECIMAL_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.DOUBLE_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.EMPTY_STRING;
import static org.eclipse.persistence.oxm.XMLConstants.INT_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.INTEGER_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.SCHEMA_PREFIX;
import static org.eclipse.persistence.oxm.XMLConstants.STRING_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.TIME_QNAME;
import static org.eclipse.persistence.tools.dbws.XRPackager.__nullStream;

//DDL parser imports
import org.eclipse.persistence.tools.oracleddl.metadata.ArgumentType;
import org.eclipse.persistence.tools.oracleddl.metadata.DatabaseType;
import org.eclipse.persistence.tools.oracleddl.metadata.FunctionType;
import org.eclipse.persistence.tools.oracleddl.metadata.ObjectTableType;
import org.eclipse.persistence.tools.oracleddl.metadata.ObjectType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLType;
import org.eclipse.persistence.tools.oracleddl.metadata.ProcedureType;
import org.eclipse.persistence.tools.oracleddl.metadata.ScalarDatabaseTypeEnum;
import org.eclipse.persistence.tools.oracleddl.metadata.VArrayType;

import static org.eclipse.persistence.tools.oracleddl.metadata.ArgumentTypeDirection.INOUT;
import static org.eclipse.persistence.tools.oracleddl.metadata.ArgumentTypeDirection.OUT;

public class Util {

    // type for Stored Procedure Arguments
    public enum InOut {
        IN, OUT, INOUT, RETURN
    }

    public static final String TOPLEVEL = "TOPLEVEL";
    public static final String CLASSES = "classes";
    public static final String DEFAULT_WSDL_LOCATION_URI =
        "REPLACE_WITH_ENDPOINT_ADDRESS";
    public static final String SWAREF_FILENAME =
    	XMLConstants.SWA_REF.toLowerCase() + ".xsd";
    // leave this duplicate as someone may be referencing it...
    public static final String WSI_SWAREF_XSD_FILE = SWAREF_FILENAME;
    public static final String WEB_XML_FILENAME = "web.xml";
    public static final String DEFAULT_PLATFORM_CLASSNAME =
        "org.eclipse.persistence.platform.database.OraclePlatform";
    public static final String UNDER_DBWS = "_dbws";
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
    public static final String NUMERIC_STR = "NUMERIC";
    public static final String OTHER_STR = "OTHER";
    public static final String RAW_STR = "RAW";
    public static final String REAL_STR = "REAL";
    public static final String SMALLINT_STR = "SMALLINT";
    public static final String STRUCT_STR = "STRUCT";
    public static final String TIME_STR = "TIME";
    public static final String TIMESTAMP_STR = "TIMESTAMP";
    public static final String TINYINT_STR = "TINYINT";
    public static final String UROWID_STR = "UROWID";
    public static final String VARBINARY_STR = "VARBINARY";
    public static final String VARCHAR_STR = "VARCHAR";
    public static final String VARCHAR2_STR = "VARCHAR2";

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
        else if (typeName.equals(BINARY_STR) ||
        		 typeName.equals(LONG_STR)) {
            jdbcType = Types.BINARY;
        }
        else if (typeName.equals(BLOB_STR)) {
            jdbcType = Types.BLOB;
        }
        else if (typeName.equals(CLOB_STR)) {
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
        String typeName = OTHER_STR;
        switch (jdbcType) {
            case Types.NUMERIC:
                typeName = NUMERIC_STR;
                break;
            case Types.VARCHAR:
                typeName = VARCHAR_STR;
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
            case CLOB:
            case NCLOB:
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
        if (outputStream == null | outputStream == __nullStream) {
            return true;
        }
        return false;
    }

    public static String getGeneratedJavaClassName(String tableName, String projectName) {
        String first = tableName.substring(0, 1).toUpperCase();
        String rest = tableName.toLowerCase().substring(1);
        return projectName.toLowerCase() + "." + first + rest;
    }

    public static RelationalDescriptor buildORDescriptor(String tableName, String projectName,
        List<String> requireCRUDOperations, NamingConventionTransformer nct) {
        RelationalDescriptor desc = new RelationalDescriptor();
        String tablenameAlias = nct.generateSchemaAlias(tableName);
        desc.addTableName(tableName);
        desc.setAlias(tablenameAlias);
        String generatedJavaClassName = getGeneratedJavaClassName(tableName, projectName);
        desc.setJavaClassName(generatedJavaClassName);
        desc.useWeakIdentityMap();
        // keep track of which tables require CRUD operations
        if (requireCRUDOperations != null && nct.generateSchemaAlias(tableName).equals(tablenameAlias)) {
            requireCRUDOperations.add(tablenameAlias);
        }
        return desc;
    }

    public static XMLDescriptor buildOXDescriptor(String tableName,  String projectName,
        String targetNamespace, NamingConventionTransformer nct) {
        XMLDescriptor xdesc = new XMLDescriptor();
        String generatedJavaClassName = getGeneratedJavaClassName(tableName, projectName);
        xdesc.setJavaClassName(generatedJavaClassName);
        String tablenameAlias = nct.generateSchemaAlias(tableName);
        xdesc.setAlias(tablenameAlias);
        NamespaceResolver nr = new NamespaceResolver();
        nr.setDefaultNamespaceURI(targetNamespace);
        xdesc.setNamespaceResolver(nr);
        xdesc.setDefaultRootElement(tablenameAlias);
        XMLSchemaURLReference schemaReference = new XMLSchemaURLReference(EMPTY_STRING);
        schemaReference.setSchemaContext(SLASH + tablenameAlias);
        schemaReference.setType(org.eclipse.persistence.platform.xml.XMLSchemaReference.COMPLEX_TYPE);
        xdesc.setSchemaReference(schemaReference);
        return xdesc;
    }

    public static QName buildCustomQName(String typeString, DBWSBuilder builder) {
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
            }
            else {
                nsURI = DEFAULT_NS_PREFIX;
            }
            localPart = typeString.substring(colonIdx+1);
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
        else {
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
     * Indicates if a given ArgumentType is considered 'complex', i.e. it has
     * a data type that is one of PLSQLRecordType, PLSQLCollectionType,
     * VArrayType, ObjectType, or NestedTableType
     */
    public static boolean isArgComplex(ArgumentType argument) {
        DatabaseType argType = argument.getDataType();
        return argType instanceof PLSQLType
                || argType instanceof VArrayType
                || argType instanceof ObjectType
                || argType instanceof ObjectTableType;
    }

    /**
     * Indicates if a given ArgumentType is considered a PL/SQL argument,
     * i.e. it has a data type that is one of PLSQLRecordType, 
     * PLSQLCollectionType, BOOLEAN_TYPE, BINARY_INTEGER_TYPE, 
     * PLS_INTEGER_TYPE, etc. 
     */
    public static boolean isArgPLSQL(ArgumentType argument) {
        DatabaseType argType = argument.getDataType();
        return argType instanceof PLSQLType
        		|| isArgPLSQLScalar(argument);
    }

    /**
     * Indicates if a given ArgumentType is considered a PL/SQL scalar
     * argument, i.e. it has a data type that is one of BOOLEAN_TYPE, 
     * BINARY_INTEGER_TYPE, PLS_INTEGER_TYPE, etc.
     */
    public static boolean isArgPLSQLScalar(ArgumentType argument) {
        DatabaseType argType = argument.getDataType();
        return argType == ScalarDatabaseTypeEnum.BINARY_INTEGER_TYPE        		
            || argType == ScalarDatabaseTypeEnum.BOOLEAN_TYPE
            || argType == ScalarDatabaseTypeEnum.NATURAL_TYPE
            || argType == ScalarDatabaseTypeEnum.PLS_INTEGER_TYPE
            || argType == ScalarDatabaseTypeEnum.POSITIVE_TYPE
            || argType == ScalarDatabaseTypeEnum.SIGN_TYPE;
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
        if (storedProcedure instanceof FunctionType) {
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
        if (storedProcedure instanceof FunctionType) {
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
     */
    public static boolean hasPLSQLArgs(List<ArgumentType> arguments) {
        for (ArgumentType arg : arguments) {
        	if (isArgPLSQL(arg)) {
        		return true;
        	}
        }
        return false;
    }

    /**
     * Indicates if a given List<DatabaseType> contains one or more arguments that
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
     * Indicates if a given List<DatabaseType> contains one or more arguments
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
}