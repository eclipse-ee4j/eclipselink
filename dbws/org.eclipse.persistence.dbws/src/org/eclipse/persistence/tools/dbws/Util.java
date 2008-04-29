/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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

// javase imports
import static java.sql.Types.BIGINT;
import static java.sql.Types.CHAR;
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
import javax.xml.namespace.QName;
import static javax.xml.XMLConstants.DEFAULT_NS_PREFIX;
import static javax.xml.XMLConstants.NULL_NS_URI;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

// EclipseLink imports
import org.eclipse.persistence.internal.oxm.schema.model.Any;
import org.eclipse.persistence.internal.oxm.schema.model.ComplexType;
import org.eclipse.persistence.internal.oxm.schema.model.Element;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.internal.oxm.schema.model.Sequence;
import org.eclipse.persistence.tools.dbws.jdbc.DbStoredArgument;
import org.eclipse.persistence.tools.dbws.jdbc.DbStoredProcedure;
import static org.eclipse.persistence.internal.xr.QNameTransformer.SCHEMA_QNAMES;
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

    public static final String DEFAULT_WSDL_LOCATION_URI =
        "http://localhost:8888/";
    public static final String META_INF_DIR =
        "META-INF/";
    public static final String WEB_INF_DIR =
        "WEB-INF/";
    public static final String SWAREF_FILENAME =
        "swaref.xsd";
    public static final String WEB_XML_FILENAME =
        "web.xml";
    public static final String DEFAULT_PLATFORM_CLASSNAME = 
        "org.eclipse.persistence.platform.database.oracle.OraclePlatform";
    public static final String ORACLE_WEBSERVICES_FILENAME =
        "oracle-webservices.xml";
    public static final String ORACLE_WEBSERVICES_FILE =
        WEB_INF_DIR + ORACLE_WEBSERVICES_FILENAME;
    public static final String WSDL_FILENAME =
        "eclipselink-dbws.wsdl";
    public static final String WSDL_FILE =
        WEB_INF_DIR + "wsdl/" + WSDL_FILENAME;
    public static final String WAR_CLASSES_DIR =
        "classes/";
    public static final String INDEX_DOT_HTML = "index.html";
    public static final String INDEX_HTML =
        "<html>\n" +
        "  <head>\n" +
        "    <title>Empty DBWS Page</title>\n" +
        "  </head>\n" +
        "  <body>\n" +
        "    <p>Empty DBWS Page</p>\n" +
        "  </body>\n" +
        "</html>";
    public static final String ORACLE_WEBSERVICES_DESCRIPTOR =
        "<oracle-webservices>\n" +
        "  <provider-description>\n" +
        "    <wsdl-file>/" + WSDL_FILE + "</wsdl-file>\n" +
        "    <provider-port>\n" +
        "      <provider-name>DBWSProvider</provider-name>\n" +
        "      <implementation-class>oracle.toplink.internal.dbws.oraws.OracleWSDBWSProvider</implementation-class>\n" +
        "      <servlet-link>Provider</servlet-link>\n" +
        "    </provider-port>\n" +
        "  </provider-description>\n" +
        "</oracle-webservices>";
    public static final String WEB_XML_PREAMBLE =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<web-app\n" +
        "  xmlns=\"http://java.sun.com/xml/ns/j2ee\"\n" +
        "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
        "  xsi:schemaLocation=\"http://java.sun.com/xml/ns/j2ee" +
        "  http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd\"\n" +
        "  version=\"2.4\"\n" +
        "  >\n" +
        "  <servlet>\n" +
        "    <servlet-name>Provider</servlet-name>\n" +
        "    <servlet-class>oracle.j2ee.ws.server.provider.ProviderServlet</servlet-class>\n" +
        "    <load-on-startup>1</load-on-startup>\n" +
        "  </servlet>\n" +
        "  <servlet-mapping>\n" +
        "    <servlet-name>Provider</servlet-name>\n" +
        "    <url-pattern>";
          // contextRoot ^^ here
    public static final String WEB_XML_POSTSCRIPT =
            "</url-pattern>\n" +
        "  </servlet-mapping>\n" +
        "  <welcome-file-list>\n" +
        "    <welcome-file>index.html</welcome-file>\n" +
        "  </welcome-file-list>\n" +
        "</web-app>\n";
    public static final String WSI_SWAREF_XSD_FILE =
        "swaref.xsd";
    public static final String WSI_SWAREF_XSD =
        "<xsd:schema targetNamespace=\"http://ws-i.org/profiles/basic/1.1/xsd\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n " +
        "  <xsd:simpleType name=\"swaRef\">\n " +
        "    <xsd:restriction base=\"xsd:anyURI\"/>\n " +
        "  </xsd:simpleType>\n " +
        "</xsd:schema>";
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

    public static final QName SXF_QNAME = new QName("", "sxfType");
    // TODO - expand to cover more cases
    public static QName getXMLTypeFromJDBCType(Short jdbcType) {
        return getXMLTypeFromJDBCType(jdbcType.intValue());
    }
    public static QName getXMLTypeFromJDBCType(int jdbcType) {
        switch (jdbcType) {
        case CHAR:
        case LONGVARCHAR:
        case VARCHAR:
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
        <xsd:complexType name="sxfType">
          <xsd:sequence>
            <xsd:any minOccurs="0"/>
          </xsd:sequence>
        </xsd:complexType>
        <xsd:element name="simple-xml-format" type="sxfType"/>
      </xsd:schema>
    */
    public static void addSimpleXMLFormat(Schema schema) {
        ComplexType anyType = new ComplexType();
        anyType.setName("sxfType");
        Sequence anySequence = new Sequence();
        Any any = new Any();
        any.setMinOccurs("0");
        anySequence.addAny(any);
        anyType.setSequence(anySequence);
        schema.addTopLevelComplexTypes(anyType);
        Element wrapperElement = new Element();
        wrapperElement.setName("simple-xml-format");
        wrapperElement.setType("sxfType");
        schema.addTopLevelElement(wrapperElement);
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
}
