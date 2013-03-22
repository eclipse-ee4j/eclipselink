/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - April 7, 2011 
 ******************************************************************************/
package org.eclipse.persistence.jaxb;

import org.eclipse.persistence.oxm.XMLNameTransformer;

/**
 *  Class called to transform Java names to XML names.
 *  Implements the XMLNameTransformer interface. 
 *
 */
public class DefaultXMLNameTransformer implements XMLNameTransformer {

    private static final String EMPTY_STRING = "";
    private static final Character DOT_CHAR = '.';
    private static final Character DOLLAR_SIGN_CHAR = '$';
	
	
    /**
     *By default convert class names to xml names based the rules defined in
     *the JAXB specification
    */
    public String transformRootElementName(String className) {
        String elementName = null;
        if (className.indexOf(DOLLAR_SIGN_CHAR) != -1) {
            elementName = decapitalize(className.substring(className.lastIndexOf(DOLLAR_SIGN_CHAR) + 1));
        } else {
            elementName = decapitalize(className.substring(className.lastIndexOf(DOT_CHAR) + 1));
        }
          
        //the following satisfies a TCK requirement
        if (elementName.length() >= 3) {
            int idx = elementName.length() - 1;
            if (Character.isDigit(elementName.charAt(idx - 1))) {
                elementName = elementName.substring(0, idx) + Character.toUpperCase(elementName.charAt(idx));
            }
        }
        return elementName;		
     }
	
    /**
     * By default convert class names to xml names based the rules defined in
     * the JAXB specification
     */ 
    public String transformTypeName(String className) {
        String typeName = EMPTY_STRING;
        if (className.indexOf(DOLLAR_SIGN_CHAR) != -1) {
            typeName = decapitalize(className.substring(className.lastIndexOf(DOLLAR_SIGN_CHAR) + 1));
        } else {
            typeName = decapitalize(className.substring(className.lastIndexOf(DOT_CHAR) + 1));
        }

        //now capitalize any characters that occur after a "break"
        boolean inBreak = false;
        StringBuffer toReturn = new StringBuffer(typeName.length());
        for (int i = 0; i < typeName.length(); i++) {
            char next = typeName.charAt(i);
            if (Character.isDigit(next)) {
                if (!inBreak) {
                    inBreak = true;
                }
                toReturn.append(next);
            } else {
                if (inBreak) {
                    toReturn.append(Character.toUpperCase(next));
                    inBreak = false;
                } else {
                    toReturn.append(next);
                }
            }
        }
        return toReturn.toString();
		
    }

    private String decapitalize(String javaName) {
        char[] name = javaName.toCharArray();
        int i = 0;
        while (i < name.length && (Character.isUpperCase(name[i]))){
            i++;
        }
        if (i > 0) {
            if(name.length > i && Character.isLetter(name[i])){
                i --;
            }
            name[0] = Character.toLowerCase(name[0]);
            for (int j = 1; j < i; j++) {
                name[j] = Character.toLowerCase(name[j]);
            }
            return new String(name);
        } else {
            return javaName;
        }
    }

    /**
     * By default do not make changes to element names
     */
    public String transformElementName(String name) {
        return name;
    }

    /**
     * By default do not make changes to attribute names
     */
    public String transformAttributeName(String name) {
        return name;
    }

}
