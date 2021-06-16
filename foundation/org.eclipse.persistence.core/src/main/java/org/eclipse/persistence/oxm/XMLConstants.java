/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.oxm;

import org.eclipse.persistence.internal.oxm.Constants;

/**
 * <p>XMLConstants maintains a list of useful XML constants.</p>
 */
public class XMLConstants extends Constants{

    public static final String SCHEMA_URL = javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
    public static final String SCHEMA_INSTANCE_URL = javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
    public static final String TARGET_NAMESPACE_PREFIX = "toplinktn";

    public static final String XMLNS = javax.xml.XMLConstants.XMLNS_ATTRIBUTE;
    public static final String XMLNS_URL = javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
    public static final String XML_NAMESPACE_PREFIX = javax.xml.XMLConstants.XML_NS_PREFIX;
    public static final String XML_NAMESPACE_URL = javax.xml.XMLConstants.XML_NS_URI;

    public static final String CONTENT_TYPE = "contentType";

    public static final char[] EMPTY_CHAR_ARRAY = new char[0];
    public static final String BOOLEAN_STRING_FALSE = "false";
    public static final String ANY_NAMESPACE_LOCAL = "##local";
    public static final String ANY_NAMESPACE_TARGETNS = "##targetNamespace";

    public static final XMLField DEFAULT_XML_TYPE_ATTRIBUTE = new XMLField(Constants.ATTRIBUTE+ Constants.SCHEMA_TYPE_ATTRIBUTE);

}
