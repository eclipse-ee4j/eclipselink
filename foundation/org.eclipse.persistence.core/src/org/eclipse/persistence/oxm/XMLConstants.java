/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.oxm;

import org.eclipse.persistence.internal.oxm.Constants;

/**
 * <p>XMLConstants maintains a list of useful XML constants.</p>
 *
 * <p>This includes constants for built-in schema types as well as QNames
 * which represent those built-in schema types.  These QName constants can be used,
 * for example, when adding conversion pairs to XMLFields and when adding
 * schema types to an XMLUnionField.</p>
 *
 * <p><em>Code Sample</em></p>
 *
 * <p><code>
 * XMLUnionField unionField = new XMLUnionField("myElement");<br>
 * unionField.addSchemaType(XMLConstants.INTEGER_QNAME);<br>
 * unionField.addSchemaType(XMLConstants.DATE_QNAME);
 * </code></p>
 */
public class XMLConstants extends Constants{
    
    public static final String SCHEMA_URL = javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;    
    public static final String SCHEMA_INSTANCE_URL = javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
    public static final String TARGET_NAMESPACE_PREFIX = "toplinktn";
    
    public static final String XMLNS = javax.xml.XMLConstants.XMLNS_ATTRIBUTE;
    public static final String XMLNS_URL = javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
    public static final String XML_NAMESPACE_PREFIX = javax.xml.XMLConstants.XML_NS_PREFIX;
    public static final String XML_NAMESPACE_URL = javax.xml.XMLConstants.XML_NS_URI;
   
    public final static String CONTENT_TYPE = "contentType";    

    public static final char[] EMPTY_CHAR_ARRAY = new char[0];
    public static final String BOOLEAN_STRING_FALSE = "false";
    public static final String ANY_NAMESPACE_LOCAL = "##local";    
    public static final String ANY_NAMESPACE_TARGETNS = "##targetNamespace";

}