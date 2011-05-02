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
package org.eclipse.persistence.sdo.helper.extension;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.logging.AbstractSessionLog;

/**
 * <p><b>Purpose</b>: Common functions in support of SDO.
 * <p><b>Responsibilities</b>:
 * <ul>
 * <li> JAXB 1.0 Name Mangling algorithm functions are provided to support generation of valid class/method names..
 * <li> JSR-222 JAXB 2.0 Java Package Name generation algorithm function follows https://jaxb.dev.java.net/spec-download.html
 * in section D.5.1 "Mapping from a Namespace URI"
 * </ul>
 */
public class SDOUtil {

    /** Valid hexadecimal digits */
	private static final String HEXADECIMAL_DIGITS = "0123456789abcdefABCDEF";
	/** Warning string to signify that the input to the package generator may not be a valid URI */
	private static final String INVALID_URI_WARNING = "SDOUtil: The URI [{0}] used for java package name generation is invalid - generating [{1}].";

	private static final String IS = "is";
    private static final String GET = "get";
    private static final String SET = "set";
	
	/**
	 * INTERNAL: 
	 * Search Java reserved name arrays and report (but don't fix) any naming collisions
	 */
    private static void preProcessJavaReservedNames(String name) {
    	preProcessReservedNames(name, SDOConstants.javaReservedWordsList, "sdo_type_generation_warning_class_name_violates_java_spec");
    }

    /**
     * INTERNAL:
     * Search SDO reserved name arrays and report (but don't fix) any naming collisions
     */
    private static void preProcessSDOReservedNames(String name) {
    	preProcessReservedNames(name, SDOConstants.sdoInterfaceReservedWordsList, "sdo_type_generation_warning_class_name_violates_sdo_spec");
    }

    /**
     * INTERNAL:
     * Search reserved name arrays and report (but don't fix) any naming collisions
     * @param wordArray
     * @param warningLogKey
     */
    private static void preProcessReservedNames(String name, String[] wordArray, String warningLogKey) {
    	// search the reserved words list and recommend alternatives
    	for(int i=0; i < wordArray.length; i++) {
    		if(wordArray[i].equalsIgnoreCase(name)) {    			
                AbstractSessionLog.getLog().log(AbstractSessionLog.WARNING, warningLogKey, //
                	new Object[] { "SDOUtil", name, wordArray[i]});
    		}
    	}
    }
    

    /** allow _ underscores in class/function names */
    private static boolean asWordSeparator = false;

	/**
	 * INTERNAL:
	 * Get default package name when no targetNamespace URI exists.<br>
	 * This function follows the JSR-222 JAXB 2.0 algorithm from https://jaxb.dev.java.net/spec-download.html<br>
	 * @return default Java package name String
	 */
	public static String getDefaultPackageName() {
		return getPackageNameFromURI(SDOConstants.EMPTY_STRING);
	}
	
	/**
	 * INTERNAL:
	 * Get default package name from a namespace URI.<br>
	 * This function follows the JSR-222 JAXB 2.0 algorithm from https://jaxb.dev.java.net/spec-download.html.<br>
	 * @param uriString - a namespace URL or URN
	 * @return Java package name String
	 * @exclude
	 */
	public static String getPackageNameFromURI(String uriString) {
		String strToken;
		String prefix;
		int position = 0;
		StringBuffer pkgName = new StringBuffer();
		if (null == uriString || uriString.equals(SDOConstants.EMPTY_STRING)) {
            AbstractSessionLog.getLog().log(AbstractSessionLog.FINEST, INVALID_URI_WARNING,//
                    new Object[] { uriString, SDOConstants.JAVA_TYPEGENERATION_DEFAULT_PACKAGE_NAME }, false);
			return SDOConstants.JAVA_TYPEGENERATION_DEFAULT_PACKAGE_NAME;
		}

		/**
		 * Step 1: (Remove the schema and ":" part)
		 * An XML namespace is represented by a URI. Since XML Namespace will be
		 * 	mapped to a Java package, it is necessary to specify a default mapping from a
		 * 	URI to a Java package name. The URI format is described in [RFC2396].
		 * 	The following steps describe how to map a URI to a Java package name. The
		 * 	example URI, http://example.org/go/file.xsd, is used to
		 * 	illustrate each step.
		 * 	1. Remove the scheme and ":" part from the beginning of the URI, if present.
		 * Since there is no formal syntax to identify the optional URI scheme, restrict
		 * 	the schemes to be removed to case insensitive checks for schemes
		 * 	"http" and "urn".
		 * 	//example.org/go/file.xsd
		 */
		// Remove only urn: and http: schemes - retain ftp, file, gopher, mail, news, telnet		
		URI uri;
		String originalUriString = uriString;
		String schemePrefix;
		// Save error state so that we can emit a warning after the URI has been processed
		boolean invalidOriginalFormat = false;
		// Save whether we are a supported urn or http scheme
    boolean invalidScheme = false;
		try {
			// Creating a URI object and catching a syntax exception may be a performance hit 
			uri = new URI(uriString);
			schemePrefix = uri.getScheme();			
			// Remove http or urn schemes for valid URI's			
      if(null != schemePrefix){
        if((schemePrefix.equalsIgnoreCase("http") || schemePrefix.equalsIgnoreCase("urn"))) {
          uriString = uri.getSchemeSpecificPart();          
        }else{
          invalidScheme = true;
        }
			}
		} catch (NullPointerException npe) {			
            AbstractSessionLog.getLog().log(AbstractSessionLog.FINEST, INVALID_URI_WARNING,//
                    new Object[] { "null", SDOConstants.JAVA_TYPEGENERATION_DEFAULT_PACKAGE_NAME }, false);
			return SDOConstants.JAVA_TYPEGENERATION_DEFAULT_PACKAGE_NAME;
		} catch (URISyntaxException use) {
			// Warn that the URI is invalid, but process the string into a valid package anyway
			invalidOriginalFormat = true;
			// Remove http or urn schemes for invalid URI's
			if (uriString.length() > 4) {
				prefix = uriString.substring(0, 4);
				if (prefix.equalsIgnoreCase("urn:")) {
					uriString = uriString.substring(4);					
				} else {
					prefix = uriString.substring(0, 5);
					if (prefix.equalsIgnoreCase("http:")) {						
						uriString = uriString.substring(5);						
					}
				}
			}
		} finally {
			/**
			 * Step 2: remove trailing file type, one of .?? or .??? or .html.
			 * //example.org/go/file
			 * Note: The trailing host fragment will be removed for non http|urn schemes such as file:.
			 */
			int potentialPathSepIndex = uriString.lastIndexOf('/'); // Don't handle ? param separator on purpose
			int potentialHostSepIndex = uriString.indexOf('/'); 
			int potentialFileExtIndex = uriString.lastIndexOf('.');			
			/**
			 * When to remove the last .ext or trailing host fragment.
			 * Valid scheme	|  has file ext	= remove/keep last {.[^.]+} fragment
			 * 0 | 0 Remove host prefix			ie: file://site.com -> file.site
			 * 0 | 1 Remove file ext				ie: file://site.com/file.xsd -> file.com.site
			 * 1 | 0 Don't remove host prefix	ie: urn://site.com -> com.site
			 * 1 | 1 Remove file ext				ie: urn://site.com/file.xsd -> com.site
			 */
			// Don't Remove trailing host fragment for http|urn schemes
			if((invalidScheme && potentialFileExtIndex != -1) || //
				((potentialFileExtIndex != -1 && potentialPathSepIndex != -1 && //
				potentialHostSepIndex != -1 && (potentialPathSepIndex - potentialHostSepIndex) > 1))) { // -1's are handled
				String extension = uriString.substring(potentialFileExtIndex);							
        if (extension.length() == 3 || extension.length() == 4	|| extension.equalsIgnoreCase(".html")) {					
					uriString = uriString.substring(0, potentialFileExtIndex);
				}
			}

			/**
			 * Step 3: (split string into word list) 3. Parse the remaining
			 * String into a list of strings using / and : as separators
			 * Treat consecutive separators as a single separator.
			 * {"example.org", "go", "file" }
			 */
			StringTokenizer aTokenizer = new StringTokenizer(uriString, "/:");
			int length = aTokenizer.countTokens();
			if (length == 0) {
				return SDOConstants.JAVA_TYPEGENERATION_DEFAULT_PACKAGE_NAME;
			}

			/**
			 * Step 4: (unescape each escape sequence octet) 4. For each string
			 * in the list produced by previous step, unescape each escape
			 * sequence octet. {"example.org", "go", "file" } Generating a
			 * Java package name 4/19/06 JAXB 2.0 - Final Release 341
			 */
			ArrayList<String> strings = new ArrayList<String>(length);
			while (aTokenizer.hasMoreTokens()) {
				strToken = aTokenizer.nextToken();
				strings.add(decodeUriHexadecimalEscapeSequence(strToken));
			}

			/**
			 * Step 5: replace [-] with [.] if the scheme is a URN 5. If the
			 * scheme is a urn, replace all dashes, -, occurring in the
			 * first component with [.].2
			 */

			/**
			 * Step 6: Apply algorithm described in Section 7.7 Unique Package
			 * Names in [JLS] to derive a unique package name from the
			 * potential internet domain name contained within the first
			 * component. The internet domain name is reversed, component by
			 * component. Note that a leading www. is not considered part of
			 * an internet domain name and must be dropped. If the first
			 * component does not contain either one of the top-level domain
			 * names, for example, com, gov, net, org, edu, or one of the
			 * English two-letter codes identifying countries as specified in
			 * ISO Standard 3166, 1981, this step must be skipped. {org,
			 * example, go, file}
			 */
			strToken = strings.remove(0).toLowerCase();
			// Reuse the Tokenizer - tokenize on package separator
			aTokenizer = new StringTokenizer(strToken, ".");
			// Check for URI's that are composed only of metacharacter package separators
			if(aTokenizer.countTokens() < 1) {
	            AbstractSessionLog.getLog().log(AbstractSessionLog.FINEST, INVALID_URI_WARNING,//
	                    new Object[] { uriString, SDOConstants.JAVA_TYPEGENERATION_DEFAULT_PACKAGE_NAME }, false);
				return SDOConstants.JAVA_TYPEGENERATION_DEFAULT_PACKAGE_NAME;
			} else {
				while (aTokenizer.hasMoreTokens()) {
					strToken = aTokenizer.nextToken();
					if (!strToken.equals("www")) {
						strings.add(0, strToken);
					}
				}
			}

			/**
			 * Step 7: (convert each string to be all lower case) 7. For each
			 * string in the list, convert each string to be all lower case.
			 * {org, example, go, file }
			 */
			position = 0;
			for (String aString : strings) {
				strings.set(position++, aString.toLowerCase());
			}

			/**
			 * Step 8: (convert each string to a valid identifier) 8. For each
			 * string remaining, the following conventions are adopted from
			 * [JLS] Section 7.7, Unique Package Names. Follow step 8a-c
			 * below.
			 */
			position = 0;
			for (String aString : strings) {
				StringBuffer buffer = new StringBuffer();

				/**
				 * Step 8a: If the string component contains a hyphen, or any other
				 * special character not allowed in an identifier, convert it
				 * into an underscore.
				 */
				for (int j = 0; j < aString.length(); j++) {
					char charToken = aString.charAt(j);
					if (Character.isJavaIdentifierPart(charToken)) {
						buffer.append(charToken);
					} else {
						buffer.append('_');
					}
				}

				/**
				 * Step 8b:
				 * From the Java Language Specification section 7.7 b. If any of
				 * the resulting package name components are keywords then
				 * append underscore to them.
				 * We are not performing this step here - and are allowing all java reserved keywords to pass
				 * See the enum com.sun.tools.javac.parser.Token for a list of keywords 
				 */

				/**
				 * Step 8c: If any of the resulting package name components start with
				 * a digit, or any other character that is not allowed as an
				 * initial character of an identifier, have an underscore
				 * prefixed to the component. {org, example, go, file }
				 */
				if (!Character.isJavaIdentifierStart(buffer.charAt(0))) {
					buffer.insert(0, '_');
				}
				if (position++ != 0) {
					buffer.insert(0, '.');
				}

				pkgName.append(buffer.toString());
			}
			if(invalidOriginalFormat) {
	            AbstractSessionLog.getLog().log(AbstractSessionLog.FINEST,//
	            		INVALID_URI_WARNING, new Object[] { originalUriString, pkgName }, false);
			}			
		}
		return pkgName.toString();
	}

	/**
	 * INTERNAL:
	 * Decode Hexadecimal "%hh" escape sequences in a URI.
	 * All escape codes must be valid 2 digit sequences.
	 * 
	 * @param s - URI component
	 * @return URI component with escape sequence decoded into a 
	 */
	private static String decodeUriHexadecimalEscapeSequence(String uri) {
		// This function is used by the Java Package Name generation algorithm that implements JAXB 2.0 D.5.1
		StringBuffer sb = new StringBuffer(uri.length());
		for (int index = 0; index < uri.length(); index++) {
			char c = uri.charAt(index);
			// Escape sequence found - get the hex value and convert
			if (c == '%') {
				if (((index + 2) < uri.length()) && //
					HEXADECIMAL_DIGITS.indexOf(uri.charAt(index + 1)) >= 0 &&//
					HEXADECIMAL_DIGITS.indexOf(uri.charAt(index + 2)) >= 0) {
					// Look ahead 2 digits
					String g = uri.substring(index + 1, index + 3);
					// Convert base 16 to base 10 to char and append
					sb.append((char)Integer.parseInt(g, 16));
					/**
					 * Increase the index by 2 - so we skip the 2 digit hex code after the %
					 * See JAXB 2.0 spec p.348 section D.5.1.4
					 * "For each string in the list produced by step 3.  Unescape each escape sequence octet.
					 * IE: North%20America should be "North America" and later in step 8 
					 * "north_america" not "north_20america"
					 */
					index+=2;					
				} else {
					sb.append(c);
				}
			} else {
				sb.append(c);
			}
		}
		return (sb.toString());
	}

    
    /**
     * INTERNAL:
     * Return a valid Java class name or method name for a given string
     * @param s
     * @param isClass (flag whether called from a method context)
     * @return
     */
    public static String className(String s, boolean isClass) {
        return className(s, true, isClass, true);
    }

    /**
     * INTERNAL:
     * Return a valid Java class name or method name for a given string
     * @param s
     * @param isClass (flag whether called from a method context)
     * @param flag
     * @return
     */
    public static String className(String s, boolean flag, boolean isClass, boolean logOn) {
        // 1) warn if the class conflicts with any Java reserved words
        preProcessJavaReservedNames(s);
        // 2) warn if the class conflicts with any SDO reserved words            
        preProcessSDOReservedNames(s);            

        String[] as = getWordList(s);
        StringBuffer stringbuffer = new StringBuffer();
        StringBuffer stringbuffer1 = new StringBuffer();
        if (as.length == 0) {
            return stringbuffer.toString();
        }

        for (int i = 0; i < as.length; i++) {
            char[] ac = as[i].toCharArray();
            if (Character.isLowerCase(ac[0])) {
                ac[0] = Character.toUpperCase(ac[0]);
            }
            for (int j = 0; j < ac.length; j++) {
                if ((ac[j] >= ' ') && (ac[j] < '\177')) {
                    if ((ac[j] != '_') || !asWordSeparator) {
                        stringbuffer.append(ac[j]);
                    }
                    continue;
                }
                if (flag) {
                    stringbuffer.append(escapeUnicode(stringbuffer1, ac[j]));
                } else {
                    stringbuffer.append(ac[j]);
                }
            }
        }

        String normalizedName = stringbuffer.toString();
        // report a warning whether we were required to normalize the name (beyond initial capitalization)
        if(!s.equals(normalizedName) && logOn) {
        	// log changes
        	int logLevel;
        	// log capitalization-only changes at a lower level
        	if(!s.equalsIgnoreCase(normalizedName)) {
        		logLevel = AbstractSessionLog.INFO;
        	} else {
        		logLevel = AbstractSessionLog.FINER;
        	}
        	
        	// report differently depending on whether the input was a class or function name
        	if(isClass) {
        		AbstractSessionLog.getLog().log(logLevel, //
        			"sdo_type_generation_modified_class_naming_format_to", 
        			new Object[] { "SDOUtil", s, normalizedName});
        	} else {
        		AbstractSessionLog.getLog().log(logLevel, //
            			"sdo_type_generation_modified_function_naming_format_to", 
            			new Object[] { "SDOUtil", s, normalizedName});
        	}
        }

        return normalizedName;
    }

    /**
     * INTERNAL:
     * Return a valid Java method name for a given string
     * @param s
     * @return
     */
    public static String methodName(String s) {
        return methodName(s, true);
    }

    /**
     * INTERNAL:
     * Return a valid Java method name for a given string
     * @param s
     * @param flag
     * @return
     */
    public static String methodName(String s, boolean flag) {
        return className(s, flag, false, true);
    }

    /**
     * INTERNAL:
     * Return a valid Java set method name for a given string
     * @param s
     * @return
     */
    public static String setMethodName(String s) {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append(SET).append(methodName(s));
        return stringbuffer.toString();
    }

    /**
     * INTERNAL:
     * Return a valid Java get method name for a given string. This method will check
     * the returnType to see if it is a boolean/Boolean:  if so, 'is' will be used in
     * the method name instead of 'get'.
     *   
     * @param s
     * @param returnType
     * @return
     */
    public static String getMethodName(String s, String returnType) {
        StringBuffer stringBuffer = new StringBuffer();
        if (returnType.equals(ClassConstants.PBOOLEAN.getName()) || returnType.equals(ClassConstants.BOOLEAN.getName())) {
            stringBuffer.append(IS);
        } else {
            stringBuffer.append(GET);
        }
        stringBuffer.append(SDOUtil.className(s, true, false, false));
        return stringBuffer.toString();
    }
    
    /**
     * INTERNAL:
     * Return a valid Java get method name for a given string. This method will NOT check
     * the returnType to see if it is a boolean/Boolean and all method names will start with
     * "GET"
     *   
     * @param s
     * @param returnType
     * @return
     */
    public static String getBooleanGetMethodName(String s, String returnType){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(SDOUtil.GET);
        stringBuffer.append(SDOUtil.className(s, true, false, false));
        return stringBuffer.toString();    	
    }

    /**
     * INTERNAL:
     * @param s
     * @return
     */
    public static String constantName(String s) {
        StringBuffer stringbuffer = new StringBuffer();
        String[] as = getWordList(s);
        if (as.length > 0) {
            stringbuffer.append(as[0].toUpperCase());
            for (int i = 1; i < as.length; i++) {
                stringbuffer.append('_');
                stringbuffer.append(as[i].toUpperCase());
            }
        }
        return stringbuffer.toString();
    }

    /**
     * 
     * @param s
     * @return
     */
    private static String[] getWordList(String s) {
        java.util.List arraylist = new ArrayList();
        int i = s.length();
        int j = 0;
        do {
            if (j >= i) {
                break;
            }
            for (; (j < i) && isPunct(s.charAt(j)); j++) {
            }
            if (j >= i) {
                break;
            }
            int k = nextBreak(s, j);
            String s1 = (k != -1) ? s.substring(j, k) : s.substring(j);
            arraylist.add(escape(s1));
            if (k == -1) {
                break;
            }
            j = k;
        } while (true);
        return (String[])arraylist.toArray(new String[0]);
    }

    private static boolean isPunct(char c) {
        if ((c == '-') || (c == '.') || (c == ':') || (c == '\267') || (c == '\u0387') || (c == '\u06DD') || (c == '\u06DE')) {
            return true;
        }
        return (c == '_') && asWordSeparator;
    }

    private static boolean isUncased(char c) {
        return Character.isLetter(c) && !Character.isUpperCase(c) && !Character.isLowerCase(c);
    }

    private static int nextBreak(String s, int i) {
        int j = s.length();
        for (int k = i; k < j; k++) {
            char c = s.charAt(k);
            if (((c == '_') && !asWordSeparator) || (k >= (j - 1))) {
                continue;
            }
            char c1 = s.charAt(k + 1);
            if ((c1 == '_') && !asWordSeparator) {
                continue;
            }
            if (isPunct(c1)) {
                return k + 1;
            }
            if (Character.isDigit(c) && !Character.isDigit(c1)) {
                return k + 1;
            }
            if (!Character.isDigit(c) && Character.isDigit(c1)) {
                return k + 1;
            }
            if (Character.isLowerCase(c) && !Character.isLowerCase(c1)) {
                return k + 1;
            }
            if (k < (j - 2)) {
                char c2 = s.charAt(k + 2);
                if ((c2 == '_') && !asWordSeparator) {
                    continue;
                }
                if (Character.isUpperCase(c) && Character.isUpperCase(c1) && Character.isLowerCase(c2)) {
                    return k + 1;
                }
            }
            if (Character.isLetter(c) && !Character.isLetter(c1)) {
                return k + 1;
            }
            if (!Character.isLetter(c) && Character.isLetter(c1)) {
                return k + 1;
            }
            if (isUncased(c) && !isUncased(c1)) {
                return k + 1;
            }
            if (!isUncased(c) && isUncased(c1)) {
                return k + 1;
            }
        }
        return -1;
    }

    private static String escape(String s) {
        int i = s.length();
        for (int j = 0; j < i; j++) {
            if (!Character.isJavaIdentifierPart(s.charAt(j))) {
                StringBuffer stringbuffer = new StringBuffer(s.substring(0, j));
                escape(stringbuffer, s, j);
                return stringbuffer.toString();
            }
        }
        return s;
    }

    private static void escape(StringBuffer stringbuffer, String s, int i) {
        int j = s.length();
        for (int k = i; k < j; k++) {
            char c = s.charAt(k);
            if (Character.isJavaIdentifierPart(c)) {
                stringbuffer.append(c);
                continue;
            }
            stringbuffer.append("_");
            if (c <= '\017') {
                stringbuffer.append("000");
            } else if (c <= '\377') {
                stringbuffer.append("00");
            } else if (c <= '\u0FFF') {
                stringbuffer.append("0");
            }
            stringbuffer.append(Integer.toString(c, 16));
        }
    }

    public static String escapeUnicode(StringBuffer stringbuffer, char c) {
        String s = Integer.toString(c, 16);
        int i = s.length();
        stringbuffer.setLength(6);
        stringbuffer.setCharAt(0, '\\');
        stringbuffer.setCharAt(1, 'u');
        int j = 2;
        for (int k = i; k < 4;) {
            stringbuffer.setCharAt(j, '0');
            k++;
            j++;
        }

        stringbuffer.replace(j, 6, s);
        return stringbuffer.toString();
    }

    public static String getJavaTypeForProperty(SDOProperty property) {
        if (property.isMany() || property.getType().isXsdList()) {
            return "java.util.List";
        } else {
            SDOType propertyType = property.getType();
            if(propertyType.isDataType()) {
                Class instanceClass = propertyType.getInstanceClass();
                if (ClassConstants.ABYTE.equals(instanceClass)) {
                    return "Byte[]";
                } else if (ClassConstants.APBYTE.equals(instanceClass)) {
                    return "byte[]";
                }
            }
            return propertyType.getInstanceClassName();
        }
    }

    public static String getBuiltInType(String typeName) {
        if ((typeName.equals(ClassConstants.PBOOLEAN.getName())) || (typeName.equals(ClassConstants.BOOLEAN.getName()))) {
            return "Boolean";
        } else if ((typeName.equals(ClassConstants.PBYTE.getName())) || (typeName.equals(ClassConstants.BYTE.getName()))) {
            return "Byte";
        } else if (typeName.equals("byte[]") || typeName.equals("Byte[]") ||  (typeName.equals(ClassConstants.APBYTE.getName())) || (typeName.equals(ClassConstants.ABYTE.getName()))) {
            return "Bytes";
        } else if ((typeName.equals(ClassConstants.PCHAR.getName())) || (typeName.equals(ClassConstants.CHAR.getName()))) {
            return "Char";
        } else if ((typeName.equals(ClassConstants.PDOUBLE.getName())) || (typeName.equals(ClassConstants.DOUBLE.getName()))) {
            return "Double";
        } else if ((typeName.equals(ClassConstants.PFLOAT.getName())) || (typeName.equals(ClassConstants.FLOAT.getName()))) {
            return "Float";
        } else if ((typeName.equals(ClassConstants.PLONG.getName())) || (typeName.equals(ClassConstants.LONG.getName()))) {
            return "Long";
        } else if ((typeName.equals(ClassConstants.PSHORT.getName())) || (typeName.equals(ClassConstants.SHORT.getName()))) {
            return "Short";
        } else if ((typeName.equals(ClassConstants.PINT.getName())) || (typeName.equals(ClassConstants.INTEGER.getName()))) {
            return "Int";
        } else if (typeName.equals(ClassConstants.STRING.getName())) {
            return "String";
        } else if (typeName.equals(ClassConstants.BIGINTEGER.getName())) {
            return "BigInteger";
        } else if (typeName.equals(ClassConstants.BIGDECIMAL.getName())) {
            return "BigDecimal";
        } else if (typeName.equals(ClassConstants.UTILDATE.getName())) {
            return "Date";
        } else if (typeName.equals("java.util.List")) {
            return "List";
        }
        return null;
    }

}
