/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/ 
package org.eclipse.persistence.sdo.helper.extension;

import java.util.ArrayList;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.logging.AbstractSessionLog;

/**
 * <p><b>Purpose</b>: Common functions in support of SDO.
 * <p><b>Responsibilities</b>:
 * <ul>
 * <li> JAXB 1.0 Name Mangling algorithm functions are provided to support generation of valid class/method names..
 * </ul>
 */
public class SDOUtil {

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
    

	/*
     * The following JAXB 1.0 functions are originally from the XDK XMLUtil.java class. 
     * Used here instead of creating a direct dependency.
     */
    /** allow _ underscores in class/function names */
    private static boolean asWordSeparator = false;
    
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
        		logLevel = AbstractSessionLog.WARNING;
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
        stringbuffer.append("set").append(methodName(s));
        return stringbuffer.toString();
    }

    /**
     * INTERNAL:
     * Return a valid Java get method name for a given string
     * @param s
     * @return
     */
    public static String getMethodName(String s) {
        StringBuffer stringbuffer = new StringBuffer();
        // only log the setMethodName call so we do not get double logs
        stringbuffer.append("get").append(className(s, true, false, false));
        return stringbuffer.toString();
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

    private static String escapeUnicode(StringBuffer stringbuffer, char c) {
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
}
