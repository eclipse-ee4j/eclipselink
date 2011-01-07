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
import java.util.regex.Pattern;
import static java.sql.Types.BIGINT;
import static java.sql.Types.CHAR;
import static java.sql.Types.CLOB;
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
import org.eclipse.persistence.internal.oxm.schema.model.Any;
import org.eclipse.persistence.internal.oxm.schema.model.ComplexType;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.internal.oxm.schema.model.Sequence;
import org.eclipse.persistence.tools.dbws.jdbc.DbStoredArgument;
import org.eclipse.persistence.tools.dbws.jdbc.DbStoredProcedure;
import static org.eclipse.persistence.internal.xr.QNameTransformer.SCHEMA_QNAMES;
import static org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormat.DEFAULT_SIMPLE_XML_FORMAT_TAG;
import static org.eclipse.persistence.oxm.XMLConstants.BASE_64_BINARY_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.DATE_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.DATE_TIME_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.DECIMAL_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.DOUBLE_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.INT_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.INTEGER_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.STRING_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.TIME_QNAME;

public class Util {

    // type for Stored Procedure Arguments
    public enum InOut {
        IN, OUT, INOUT, RETURN
    }

    public static final String CLASSES =
        "classes";
    public static final String DEFAULT_WSDL_LOCATION_URI =
        "REPLACE_WITH_ENDPOINT_ADDRESS";
    public static final String SWAREF_FILENAME =
        "swaref.xsd";
    public static final String WEB_XML_FILENAME =
        "web.xml";
    public static final String DEFAULT_PLATFORM_CLASSNAME =
        "org.eclipse.persistence.platform.database.OraclePlatform";
    public static final String UNDER_DBWS =
        "_dbws";
    public static final String WSI_SWAREF =
        "swaref";
    public static final String WSI_SWAREF_PREFIX =
        "ref";
    public static final String WSI_SWAREF_URI =
        "http://ws-i.org/profiles/basic/1.1/xsd";
    public static final String WSI_SWAREF_XSD_FILE =
        WSI_SWAREF + ".xsd";
    public static final String PK_QUERYNAME =
        "findByPrimaryKey";
    public static final String THE_INSTANCE_NAME =
        "theInstance";
    public static final String FINDALL_QUERYNAME =
        "findAll";
    public static final String CREATE_OPERATION_NAME =
        "create";
    public static final String UPDATE_OPERATION_NAME =
        "update";
    public static final String REMOVE_OPERATION_NAME =
        "delete";
    public static final String DBWS_PROVIDER_PACKAGE = "_dbws";
    public static final String DBWS_PROVIDER_NAME = "DBWSProvider";
    public static final String DOT_CLASS = ".class";
    static final String DOT_JAVA = ".java";
    public static final String DBWS_PROVIDER_CLASS_FILE = DBWS_PROVIDER_NAME + DOT_CLASS;
    public static final String DBWS_PROVIDER_SOURCE_FILE = DBWS_PROVIDER_NAME + DOT_JAVA;
    static final String PROVIDER_LISTENER = "ProviderListener";
    public static final String PROVIDER_LISTENER_CLASS_FILE = PROVIDER_LISTENER + DOT_CLASS;
    public static final String PROVIDER_LISTENER_SOURCE_FILE = PROVIDER_LISTENER + DOT_JAVA;

    public static final QName SXF_QNAME_CURSOR = new QName("", "cursor of " + DEFAULT_SIMPLE_XML_FORMAT_TAG);
    // TODO - expand to cover more cases
    public static QName getXMLTypeFromJDBCType(Short jdbcType) {
        return getXMLTypeFromJDBCType(jdbcType.intValue());
    }
    public static QName getXMLTypeFromJDBCType(int jdbcType) {
        switch (jdbcType) {
            case CHAR:
            case LONGVARCHAR:
            case VARCHAR:
            case CLOB:
                return STRING_QNAME;
            case BIGINT:
                return INTEGER_QNAME;
            case INTEGER:
            case SMALLINT:
            case TINYINT:
                return INT_QNAME;
            case DECIMAL:
                return DECIMAL_QNAME;
            case DOUBLE:
            case FLOAT:
            case NUMERIC:
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
                    nsURI =
                        schema.getNamespaceResolver().resolveNamespacePrefix(prefix);
                    if (nsURI == null) {
                        nsURI = DEFAULT_NS_PREFIX;
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

    public static boolean noOutArguments(DbStoredProcedure storedProcedure) {

        boolean noOutArguments = true;
        if (storedProcedure.getArguments() != null &&
             storedProcedure.getArguments().size() > 0) {
            for (DbStoredArgument arg : storedProcedure.getArguments()) {
                if (arg.getInOut() ==  InOut.INOUT || arg.getInOut() == InOut.OUT) {
                    noOutArguments = false;
                    break;
                }
            }
        }
        return noOutArguments;
    }

    public static String escapePunctuation(String originalName, boolean isOracle) {
        if (originalName == null || originalName.length() == 0) {
            if (isOracle) {
                return null;
            }
            else {
                return originalName;
            }
        }
        // escape all punctuation except SQL 'LIKE' meta-characters:
        // '_' (underscore) - matches any one character) and
        // '%' (percent ) - matches a string of zero or more characters
        return originalName.trim().replaceAll("[\\p{Punct}&&[^_%]]", "\\\\$0");
    }

    public static boolean sqlMatch(String pattern, String input) {
        if (pattern != null && pattern.length() > 0) {
            String tmp = pattern.replace('_', '.').replace("%", ".*");
            Pattern p = Pattern.compile(tmp, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            return p.matcher(input).matches();
        }
        else {
            return false;
        }
    }
}
