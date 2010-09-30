/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

package org.eclipse.persistence.internal.xr;

// Javase imports
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.w3c.dom.Document;

// Java extension libraries
import javax.xml.namespace.QName;

// EclipseLink imports
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import static org.eclipse.persistence.internal.helper.ClassConstants.APBYTE;
import static org.eclipse.persistence.internal.helper.ClassConstants.BIGDECIMAL;
import static org.eclipse.persistence.internal.helper.ClassConstants.BIGINTEGER;
import static org.eclipse.persistence.internal.helper.ClassConstants.BOOLEAN;
import static org.eclipse.persistence.internal.helper.ClassConstants.BYTE;
import static org.eclipse.persistence.internal.helper.ClassConstants.CALENDAR;
import static org.eclipse.persistence.internal.helper.ClassConstants.DOUBLE;
import static org.eclipse.persistence.internal.helper.ClassConstants.FLOAT;
import static org.eclipse.persistence.internal.helper.ClassConstants.INTEGER;
import static org.eclipse.persistence.internal.helper.ClassConstants.LONG;
import static org.eclipse.persistence.internal.helper.ClassConstants.Object_Class;
import static org.eclipse.persistence.internal.helper.ClassConstants.SHORT;
import static org.eclipse.persistence.internal.helper.ClassConstants.STRING;
import static org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormat.DEFAULT_SIMPLE_XML_FORMAT_TAG;
import static org.eclipse.persistence.oxm.XMLConstants.ANY_SIMPLE_TYPE_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.BASE_64_BINARY_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.BOOLEAN_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.BYTE_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.DATE_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.DATE_TIME_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.DECIMAL_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.DOUBLE_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.DURATION_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.FLOAT_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.G_DAY_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.G_MONTH_DAY_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.G_MONTH_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.G_YEAR_MONTH_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.G_YEAR_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.HEX_BINARY_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.INTEGER_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.INT_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.LONG_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.QNAME_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.SHORT_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.STRING_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.TIME_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.UNSIGNED_BYTE_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.UNSIGNED_INT_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.UNSIGNED_SHORT_QNAME;

/**
 * <p><b>INTERNAL</b>: provides useful constants, SQL Column <-> to XML name mapping and
 * a few other misc. features
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */
@SuppressWarnings("serial")
public class Util {

    /**
     * Convert a SQL name to a valid XML name. Because not all characters that
     * are valid in a SQL name is valid in an XML name, they need to be escaped
     * using a special format. See the Oracle paper,"SQL/XML candidate base
     * document", for more detail
     *
     * @param name
     *            the SQL name
     *
     * @return the escaped valid XML name
     */
    public static String sqlToXmlName(String name) {

        int length = name.length();
        if (length == 0) {
            return name;
        }

        String xmlName = new String();
        int beginAt = 1;
        char firstChar = name.charAt(0);
        // escape : to _x003A_
        if (firstChar == ':') {
            xmlName = xmlName + "_x003A_";
        }
        // escape _ of _x to _x005F_
        else if ((length >= 2) && name.substring(0, 2).equals("_x")) {
            xmlName = xmlName + "_x005F_";
        }
        // check to see if it is a valid first character
        else {
            if ((firstChar >= 0xd800) && (firstChar < 0xdc00)) {
                // surrogate
                if (length > 1) {
                    xmlName += hexEscape((firstChar << 16) | (name.charAt(1) & 0xffff));
                    beginAt = 2;
                } else {
                    xmlName += hexEscape(firstChar);
                }
            } else if (isFirstNameChar(firstChar)) {
                xmlName = xmlName + firstChar;
            } else {
                xmlName = xmlName + hexEscape(firstChar);
            }
        }

        // check each following character to see if it is a valid NameChar
        char c;
        for (int x = beginAt; x < length; x++) {
            c = name.charAt(x);

            if ((c >= 0xd800) && (c < 0xdc00)) {
                // surrogate
                if ((x + 1) < length) {
                    xmlName += hexEscape((c << 16) | (name.charAt(x + 1) & 0xffff));
                    x++;
                } else {
                    xmlName += hexEscape(c);
                }
            } else if (!isNameChar(c)) {
                // escape
                xmlName = xmlName + hexEscape(c);
            } else {
                xmlName = xmlName + c;
            }
        }
        return xmlName;
    }

    /**
     * Convert an escaped XML name back to the original SQL name
     *
     * @param name
     *            the escaped XML name
     *
     * @return the original SQL name
     */
    public static String xmlToSqlName(String name) {

        String sqlName = new String();
        int length = name.length();
        boolean unescapeMode = false;
        String hexString = null;
        char c;
        // step through each one
        for (int x = 0; x < length; x++) {
            c = name.charAt(x);
            if (unescapeMode) {
                // we are in unescape mode
                if (((c >= 'A') && (c <= 'F')) || ((c >= '0') && (c <= '9'))) {
                    // gather the hex string from the escape sequence
                    hexString = hexString + c;
                } else if (c == '_') {
                    // done with escape mode
                    unescapeMode = false;
                    int i;
                    int len;
                    if (hexString != null && (len = hexString.length()) > 4) {
                        char i1 = (char) (Integer.parseInt(hexString.substring(0, len - 4), 16));
                        char i2 = (char) (Integer.parseInt(hexString.substring(len - 4), 16));
                        sqlName += i1;
                        sqlName += i2;
                    } else if ((i = Integer.parseInt(hexString, 16)) == 0xffff) {
                    } else {
                        sqlName += (char) i;
                    }
                } else {
                    // invalid char in escape sequence! write everything into
                    // sqlName as is, or we could throw an exception here
                    // in the future
                    sqlName += ("_x" + hexString + c);
                    unescapeMode = false;
                }
            } else {
                if ((c == '_') && ((x + 1) < length) && (name.charAt(x + 1) == 'x')) {
                    // found escape beginning _x
                    // go into unescape mode
                    unescapeMode = true;
                    hexString = new String();
                    x++;
                } else {
                    // just copy src char to destination
                    sqlName += c;
                }
            }
        }
        return sqlName;
    }

    public static String hexEscape(char c) {
        String outPutString;
        outPutString = Integer.toHexString(c);
        switch (outPutString.length()) {
        case 1:
            outPutString = "_x000" + outPutString.toUpperCase(Locale.US) + "_";
            break;
        case 2:
            outPutString = "_x00" + outPutString.toUpperCase(Locale.US) + "_";
            break;
        case 3:
            outPutString = "_x0" + outPutString.toUpperCase(Locale.US) + "_";
            break;
        case 4:
            outPutString = "_x" + outPutString.toUpperCase(Locale.US) + "_";
            break;
        }
        return outPutString;
    }

    public static String hexEscape(int c) {
        String outPutString;
        outPutString = Integer.toHexString(c);
        switch (outPutString.length()) {
        case 1:
        case 5:
            outPutString = "_x000" + outPutString.toUpperCase(Locale.US) + "_";
            break;
        case 2:
        case 6:
            outPutString = "_x00" + outPutString.toUpperCase(Locale.US) + "_";
            break;
        case 3:
        case 7:
            outPutString = "_x0" + outPutString.toUpperCase(Locale.US) + "_";
            break;
        case 4:
        case 8:
            outPutString = "_x" + outPutString.toUpperCase(Locale.US) + "_";
            break;
        }
        return outPutString;
    }

    /**
     * return true if character can be part of a name
     *
     * @param c -
     *            char to be checked
     * @return true/false
     */
    public static boolean isNameChar(char c) {
        // In most cases the character is less than 256, so this check
        // is made fast. For the rest of the characters the check is
        // based on the conformance tests. XML 1.1 has changed the list
        // of chars allowed, and the check is quite simple. So the
        // complete check based on XML 1.0 is not performed.
        boolean res;
        if (c < 256)
            res = (chartype[c] & (FLETTER | FDIGIT | FMISCNAME)) != 0;
        else {
            // [#x2180-#x2182] | [#x3041-#x3094] | [#x30A1-#x30FA] |
            // [#x3105-#x312C] | [#xAC00-#xD7A3]
            // [#x0E47-#x0E4E]
            if (((c >= 0x2180) && (c <= 0x2182)) || ((c >= 0x3041) && (c <= 0x3094))
                || ((c >= 0x30A1) && (c <= 0x30FA)) || ((c >= 0x3105) && (c <= 0x312C))
                || ((c >= 0xAC00) && (c <= 0xD7A3)) || ((c >= 0x0E47) && (c <= 0x0E4E)))
                res = true;
            else {
                if ((c == 0x02FF) || (c == 0x0346) || (c == 0x0362) || (c == 0x0487)
                    || (c == 0x05A2) || (c == 0x05BA) || (c == 0x05BE) || (c == 0x05C0)
                    || (c == 0x05C3) || (c == 0x0653) || (c == 0x06B8) || (c == 0x06B9)
                    || (c == 0x06E9) || (c == 0x06EE) || (c == 0x0904) || (c == 0x093B)
                    || (c == 0x094E) || (c == 0x0955) || (c == 0x0964) || (c == 0x0984)
                    || (c == 0x09C5) || (c == 0x09C9) || (c == 0x09CE) || (c == 0x09D8)
                    || (c == 0x09E4) || (c == 0x0A03) || (c == 0x0A3D) || (c == 0x0A46)
                    || (c == 0x0A49) || (c == 0x0A4E) || (c == 0x0A80) || (c == 0x0A84)
                    || (c == 0x0ABB) || (c == 0x0AC6) || (c == 0x0ACA) || (c == 0x0ACE)
                    || (c == 0x0B04) || (c == 0x0B3B) || (c == 0x0B44) || (c == 0x0B4A)
                    || (c == 0x0B4E) || (c == 0x0B58) || (c == 0x0B84) || (c == 0x0BC3)
                    || (c == 0x0BC9) || (c == 0x0BD6) || (c == 0x0C0D) || (c == 0x0C45)
                    || (c == 0x0C49) || (c == 0x0C54) || (c == 0x0C81) || (c == 0x0C84)
                    || (c == 0x0CC5) || (c == 0x0CC9) || (c == 0x0CD4) || (c == 0x0CD7)
                    || (c == 0x0D04) || (c == 0x0D45) || (c == 0x0D49) || (c == 0x0D4E)
                    || (c == 0x0D58) || (c == 0x0E3F) || (c == 0x0E3B) || (c == 0x0E4F)
                    || (c == 0x0EBA) || (c == 0x0EBE) || (c == 0x0ECE) || (c == 0x0F1A)
                    || (c == 0x0F36) || (c == 0x0F38) || (c == 0x0F3B) || (c == 0x0F3A)
                    || (c == 0x0F70) || (c == 0x0F85) || (c == 0x0F8C) || (c == 0x0F96)
                    || (c == 0x0F98) || (c == 0x0FB0) || (c == 0x0FB8) || (c == 0x0FBA)
                    || (c == 0x20DD) || (c == 0x20E2) || (c == 0x3030) || (c == 0x309B)
                    || (c == 0x066A) || (c == 0x06FA) || (c == 0x0970) || (c == 0x09F2)
                    || (c == 0x0AF0) || (c == 0x0B70) || (c == 0x0C65) || (c == 0x0CE5)
                    || (c == 0x0CF0) || (c == 0x0D70) || (c == 0x0E5A) || (c == 0x0EDA)
                    || (c == 0x0F2A) || (c == 0x02D2) || (c == 0x03FE) || (c == 0x065F)
                    || (c == 0x0E5C) || (c == 0x0C04))
                    res = false;
                else {
                    res =
                        Character.isLetter(c) || Character.isDigit(c) || c == '-' || c == '_'
                            || c == '.';
                    res = true;
                }
            }
        }
        return res;
    }

    /**
     * return true if character can be part of a name
     *
     * @param c -
     *            char to be checked
     * @return true/false
     */
    public static boolean isFirstNameChar(char c) {
        // In most cases the character is less than 256, so this check
        // is made fast. For the rest of the characters the check is
        // based on the conformance tests. XML 1.1 has changed the list
        // of chars allowed, and the check is quite simple. So the
        // complete check based on XML 1.0 is not performed.
        boolean res;
        if (c < 256)
            res = (chartype[c] & (FLETTER | FSTARTNAME)) != 0;
        else {
            // [#x2180-#x2182] | [#x3041-#x3094] | [#x30A1-#x30FA] |
            // [#x3105-#x312C] | [#xAC00-#xD7A3]
            if (((c >= 0x2180) && (c <= 0x2182)) || (c == 0x3007)
                || ((c >= 0x3021) && (c <= 0x3029)) || ((c >= 0x3041) && (c <= 0x3094))
                || ((c >= 0x30A1) && (c <= 0x30FA)) || ((c >= 0x3105) && (c <= 0x312C))
                || ((c >= 0xAC00) && (c <= 0xD7A3)))
                res = true;
            else {
                if ((c == 0x1101) || (c == 0x1104) || (c == 0x1108) || (c == 0x110A)
                    || (c == 0x110D) || (c == 0x113B) || (c == 0x1141) || (c == 0x114D)
                    || (c == 0x114F) || (c == 0x1151) || (c == 0x1156) || (c == 0x1162)
                    || (c == 0x1164) || (c == 0x1166) || (c == 0x116B) || (c == 0x116F)
                    || (c == 0x1174) || (c == 0x119F) || (c == 0x11AC) || (c == 0x11B6)
                    || (c == 0x11B9) || (c == 0x11BB) || (c == 0x11C3) || (c == 0x11F1)
                    || (c == 0x0132) || (c == 0x0133) || (c == 0x013F) || (c == 0x0140)
                    || (c == 0x0149) || (c == 0x017F) || (c == 0x01C4) || (c == 0x01CC)
                    || (c == 0x01F1) || (c == 0x01F3) || (c == 0x0E46) || (c == 0x113F)
                    || (c == 0x01F6) || (c == 0x01F9) || (c == 0x0230) || (c == 0x03D7)
                    || (c == 0x03DD) || (c == 0x03E1) || (c == 0x040D) || (c == 0x0450)
                    || (c == 0x045D) || (c == 0x04EC) || (c == 0x04ED) || (c == 0x06B8)
                    || (c == 0x06BF) || (c == 0x06CF) || (c == 0x0E2F) || (c == 0x0EAF)
                    || (c == 0x0F6A) || (c == 0x4CFF) || (c == 0x212F) || (c == 0x0587))
                    res = false;
                else
                    res = Character.isLetter(c) || c == '_';
            }
        }
        return res;
    }

    /**
     * Char type table
     */
    static final int chartype[] = new int[256];
    static final int FWHITESPACE = 1;
    static final int FDIGIT = 2;
    static final int FLETTER = 4;
    static final int FMISCNAME = 8;
    static final int FSTARTNAME = 16;
    static {
        for (int i = 0; i < 256; i++) {
            char c = (char) i;
            chartype[i] = 0;
            if ((c == 32) || (c == 9) || (c == 13) || (c == 10))
                chartype[i] = FWHITESPACE;
            if (Character.isLetter(c))
                chartype[i] |= FLETTER;
            if (Character.isDigit(c))
                chartype[i] |= FDIGIT;
        }
        chartype['.'] |= FMISCNAME;
        chartype['-'] |= FMISCNAME;
        chartype['_'] |= FMISCNAME | FSTARTNAME;
        chartype[0xb7] |= FMISCNAME; // Extender
    }

    public static Class<?> getClassFromJDBCType(String typeName, DatabasePlatform databasePlatform) {
        Class<?> clz = (Class<?>)databasePlatform.getClassTypes().get(typeName);
        if (clz == null) {
            return Object_Class;
        }
        return clz;
    }

    public static final QName SXF_QNAME = new QName("", DEFAULT_SIMPLE_XML_FORMAT_TAG);
    /*
     *
                            if (xmlField.getSchemaType().equals(DATE_QNAME)) {
                                xmlField.setType(SQLDATE);
                            }
                            else if (xmlField.getSchemaType().equals(TIME_QNAME)) {
                                xmlField.setType(TIME);
                            }
                            else if (xmlField.getSchemaType().equals(DATE_TIME_QNAME)) {
                                xmlField.setType(TIMESTAMP);
                            }

     */
    public static final Map<QName, Class<?>> SCHEMA_2_CLASS;
    static {
      SCHEMA_2_CLASS = new HashMap<QName, Class<?>>() {{
            put(ANY_SIMPLE_TYPE_QNAME,Object_Class);
            put(BASE_64_BINARY_QNAME, APBYTE);
            put(BOOLEAN_QNAME, BOOLEAN);
            put(BYTE_QNAME, BYTE);
            //put(DATE_QNAME, SQLDATE);
            put(DATE_QNAME, CALENDAR);
            //put(DATE_TIME_QNAME, TIMESTAMP);
            put(DATE_TIME_QNAME, CALENDAR);
            put(DECIMAL_QNAME, BIGDECIMAL);
            put(DOUBLE_QNAME, DOUBLE);
            put(DURATION_QNAME, STRING);
            put(FLOAT_QNAME, FLOAT);
            put(G_YEAR_MONTH_QNAME, STRING);
            put(G_YEAR_QNAME, STRING);
            put(G_MONTH_QNAME, STRING);
            put(G_MONTH_DAY_QNAME, STRING);
            put(G_DAY_QNAME, STRING);
            put(HEX_BINARY_QNAME, APBYTE);
            put(INT_QNAME, INTEGER);
            put(INTEGER_QNAME, BIGINTEGER);
            put(LONG_QNAME, LONG);
            put(QNAME_QNAME, QName.class);
            put(SHORT_QNAME, SHORT);
            put(STRING_QNAME, STRING);
            //put(TIME_QNAME, TIME);
            put(TIME_QNAME, CALENDAR);
            put(UNSIGNED_BYTE_QNAME, SHORT);
            put(UNSIGNED_INT_QNAME, LONG);
            put(UNSIGNED_SHORT_QNAME, INTEGER);
        }};
    }

    public static XMLPlatform XML_PLATFORM =
        XMLPlatformFactory.getInstance().getXMLPlatform();
    public static Document TEMP_DOC = XML_PLATFORM.createDocument();
    public static final String DEFAULT_ATTACHMENT_MIMETYPE =
        "application/octet-stream";
    public static final String WEB_INF_DIR =
        "WEB-INF/";
    public static final String WSDL_DIR =
        "wsdl/";
    public static final String[] META_INF_PATHS =
        {"META-INF/", "/META-INF/"};
    public static final String DBWS_SERVICE_XML =
        "eclipselink-dbws.xml";
    public static final String DBWS_OR_LABEL =
        "dbws-or";
    public static final String DBWS_OX_LABEL =
        "dbws-ox";
    public static final String DBWS_OR_XML =
        "eclipselink-" + DBWS_OR_LABEL + ".xml";
    public static final String DBWS_OX_XML =
        "eclipselink-" + DBWS_OX_LABEL + ".xml";
    public static final String DBWS_SCHEMA_XML =
        "eclipselink-dbws-schema.xsd";
    public static final String DBWS_WSDL =
        "eclipselink-dbws.wsdl";
    public static final String DBWS_SESSIONS_XML =
        "eclipselink-dbws-sessions.xml";
    public static final String DBWS_OR_SESSION_NAME_SUFFIX =
        DBWS_OR_LABEL + "-session";
    public static final String DBWS_OX_SESSION_NAME_SUFFIX =
        DBWS_OX_LABEL + "-session";
    public static final String TARGET_NAMESPACE_PREFIX = "ns1";
    public static final String SERVICE_NAMESPACE_PREFIX = "srvc";
    public static final String SERVICE_SUFFIX = "Service";
    public static final String PK_QUERYNAME =
    "findByPrimaryKey";
}
