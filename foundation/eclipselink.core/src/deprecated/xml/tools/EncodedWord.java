/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package deprecated.xml.tools;


/* Copyright (c) 1999, 2006, Oracle. All rights reserved.  */

/**
 * Name:        EncodedWord.java
 * Description: Encode/Decode to/from UTF8 encoded word
 * Owner:       Simon Wong
 * History:
 *              16-Jun-00 (dmaser)   Added header
 *                                   Check for null string in encode()
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.oxm}
 */
/**
 * This type was created in VisualAge.
 */
public class EncodedWord {
    static final int OUTSIDE_ENCODED_WORD = 1;
    static final int SHIFTING_IN = 2;
    static final int SHIFTING_OUT = 3;
    static final int INSIDE_CHARSET_FIELD = 4;
    static final int INSIDE_QUOTED_FIELD = 5;
    static final int INSIDE_DATA_FIELD = 6;
    static final int INBETWEEN_ENCODED_WORD = 7;
    static final int SHIFTED_IN = 8;
    static final int SHIFTED_OUT = 9;
    static final int ADDRESS_FIELD = 10;
    static final int QUOTED_FIELD = 11;
    static final int COMMENT_FIELD = 12;
    static final int BASE64 = 1;
    static final int QUOTED = 2;

    /**
     * ImapEncodedWord constructor comment.
     */
    public EncodedWord() {
        super();
    }

    /**
     * This method was created in VisualAge.
     */
    public static String decode(String str) {
        int state = OUTSIDE_ENCODED_WORD;
        String decodedStr = "";
        String charsetStr = "";
        String dataStr = "";
        byte[] data;
        String inbetweenStr = "";
        int encoding = BASE64;

        char[] c = new char[1];
        for (int i = 0; i < str.length(); i++) {
            char ch = c[0] = str.charAt(i);
            switch (state) {
            case OUTSIDE_ENCODED_WORD: {
                if (ch == '=') {
                    state = SHIFTING_IN;
                } else {
                    decodedStr += new String(c);
                }

                break;
            }
            case SHIFTING_IN: {
                if (ch == '?') {
                    state = INSIDE_CHARSET_FIELD;
                } else {
                    state = OUTSIDE_ENCODED_WORD;
                    decodedStr += ("=" + new String(c));
                }

                break;
            }
            case INBETWEEN_ENCODED_WORD: {
                inbetweenStr += new String(c);
                if ((ch != ' ') && (ch != '\n') && (ch != '\r') && (ch != '\t')) {
                    if ((ch == '=') && (str.charAt(i + 1) == '?')) {
                        state = SHIFTING_IN;
                    } else {
                        state = OUTSIDE_ENCODED_WORD;
                        decodedStr += inbetweenStr;
                    }
                    inbetweenStr = "";
                }

                break;
            }
            case INSIDE_CHARSET_FIELD: {
                if (ch == '?') {
                    state = INSIDE_QUOTED_FIELD;
                } else {
                    charsetStr += new String(c);
                }

                break;
            }
            case INSIDE_QUOTED_FIELD: {
                if (ch == '?') {
                    state = INSIDE_DATA_FIELD;
                    break;
                }

                if ((ch == 'B') || (ch == 'b')) {
                    encoding = BASE64;
                } else if ((ch == 'Q') || (ch == 'q')) {
                    encoding = QUOTED;
                } else {
                    return str;
                }

                break;
            }
            case INSIDE_DATA_FIELD: {
                if (ch == '?') {
                    state = SHIFTING_OUT;
                } else {
                    dataStr += new String(c);
                }

                break;
            }
            case SHIFTING_OUT: {
                if (ch == '=') {
                    state = INBETWEEN_ENCODED_WORD;

                    charsetStr = "UTF8";//assume UTF-8

                    if (charsetStr == null) {
                        return str;
                    }

                    if (encoding == BASE64) {
                        try {
                            data = dataStr.getBytes();//assume ASCII
                            decodedStr += new String(Base64.base64Decode(data), charsetStr);
                        } catch (Exception e) {
                            return str;
                        }
                    } else if (encoding == QUOTED) {
                        try {
                            decodedStr += new String(quotedDecode(dataStr), charsetStr);
                        } catch (Exception e) {
                            return str;
                        }
                    } else {
                        return str;//error
                    }

                    dataStr = "";
                    charsetStr = "";
                } else {
                    return str;
                }

                break;
            }
            }
        }
        return decodedStr;
    }

    /**
     * This method was created in VisualAge.
     * @param str java.lang.String
     */
    public static String encode(String str) {
        String encodedStr = "";
        String shiftedStr;
        String tempStr = "";
        boolean encode = false;

        // DIEP
        if (str == null) {
            return (null);
        }

        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch < 0x80) {
                continue;
            }
            encode = true;
        }

        if (!encode) {
            return str;
        }

        char[] ch = new char[1];
        String JavaEncoding = (String)System.getProperty("file.encoding");

        //if (JavaEncoding == null) JavaEncoding = "UTF8";
        JavaEncoding = "UTF8";
        String IANACharset = "utf-8";

        int state = SHIFTED_OUT;
        shiftedStr = "=?" + IANACharset + "?Q?";
        byte[] data = null;
        try {
            data = str.getBytes(JavaEncoding);//assume ASCII
        } catch (Exception e) {
        }

        int len = 0;

        for (int i = 0; i < data.length; i++) {
            char c = ch[0] = (char)data[i];
            switch (state) {
            case ADDRESS_FIELD: {
                if (c == '>') {
                    state = SHIFTED_OUT;
                }
                encodedStr += new String(ch);
                len++;
                continue;
            }
            case SHIFTED_OUT: {
                if (c == '"') {
                    state = QUOTED_FIELD;
                    tempStr = new String(ch);
                } else if (c == '<') {
                    state = ADDRESS_FIELD;
                    encodedStr += new String(ch);
                    len++;
                    continue;
                } else if (c == '(') {
                    state = COMMENT_FIELD;
                    tempStr = new String(ch);
                } else if (c == ' ') {
                    encodedStr += new String(ch);
                    len++;
                    continue;
                } else {
                    state = SHIFTED_IN;
                    tempStr = "";
                }

                if ((len + tempStr.length() + shiftedStr.length() + 3) < 73) {
                    encodedStr += (tempStr + shiftedStr);
                    len += (tempStr.length() + shiftedStr.length());
                } else {
                    encodedStr += ("\r\n " + tempStr + shiftedStr);
                    len = 1 + tempStr.length() + shiftedStr.length();
                }

                if (state == SHIFTED_IN) {
                    break;
                }
                continue;
            }
            case COMMENT_FIELD: {
                if (c == ')') {
                    state = SHIFTED_OUT;
                    encodedStr += "?=)";
                    len += 3;
                    break;
                }

                break;
            }
            case QUOTED_FIELD: {
                if (c == '"') {
                    state = SHIFTED_OUT;
                    encodedStr += "?=\"";
                    len += 3;
                }

                break;
            }
            }

            if (state != SHIFTED_OUT) {
                if ((ch[0] > 0x7f) || (ch[0] == '=') || (ch[0] == '?') || (ch[0] == '_')) {
                    if ((len + 3) >= 75) {
                        encodedStr += ("?=\r\n =?" + IANACharset + "?Q?");
                        len = 7 + IANACharset.length();
                    }

                    String hexStr = Long.toHexString((long)ch[0]);
                    encodedStr += ("=" + hexStr.substring(hexStr.length() - 2, hexStr.length()));

                    len += 3;
                } else {
                    if (ch[0] == 0x20) {
                        ch[0] = '_';
                    }
                    if (((len + 1) >= 73) && (state != SHIFTED_OUT)) {
                        encodedStr += ("?=\r\n =?" + IANACharset + "?Q?");
                        len = 7 + IANACharset.length();
                    }

                    encodedStr += new String(ch);
                    len++;
                }
            }
        }

        if (state == SHIFTED_IN) {
            encodedStr += "?=";
        }

        return (encodedStr);
    }

    /**
     * This method was created in VisualAge.
     * @return byte[]
     * @param str java.lang.String
     */
    public static byte[] quotedDecode(String str) {
        int i = 0;
        String decodedStr = "";
        char[] c = new char[1];

        while (i < str.length()) {
            char ch = str.charAt(i);
            if (ch == '_') {
                ch = ' ';
            }

            if (ch == '=') {
                String hexStr = str.substring(i + 1, i + 3);
                c[0] = (char)Short.valueOf(hexStr, 16).shortValue();
                decodedStr += new String(c);
                i += 3;
                continue;
            }

            c[0] = ch;
            decodedStr += new String(c);
            i++;
        }
        try {
            return decodedStr.getBytes("8859_1");
        } catch (Exception e) {
            return decodedStr.getBytes();
        }
    }
}