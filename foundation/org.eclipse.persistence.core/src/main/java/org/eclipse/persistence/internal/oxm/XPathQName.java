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
//     Denise Smith - initial implementation (2.4)
package org.eclipse.persistence.internal.oxm;

import javax.xml.namespace.QName;

/**
 * <p>This class represents the pairing of a local name and a namespace uri.
 * Additionally it can be marked as namespace aware or not.  When an XPathQName is not
 * namespace aware then the namespace uri will be ignored during equality checks even
 * if an namespaceUri is set.</p>
 *
 * @since Eclipselink 2.4
 */
public class XPathQName {
    private boolean isNamespaceAware;
    private String localName;
    private String namespaceUri;

    /**
     * Create a new XPathQName.
     * namespaceUri will be "" and isNamespaceAware will be true.
     */
    public XPathQName(){
        isNamespaceAware = true;
        this.namespaceUri  = Constants.EMPTY_STRING;
    }

    /**
     * Create a new XPathQName with the specified local name.
     * namespaceUri will be null and isNamespaceAware will be set based on the MediaType.
     * @param localName The local name to associate with this XPathQName
     */
    public XPathQName(String localName, boolean namespaceAware){
        this(Constants.EMPTY_STRING, localName, namespaceAware);
    }

    /**
     * Create a new XPathQName with the specified local name and namespace
     * @param namespaceUri The namespace to associate with this XPathQName
     * @param localName The local name to associate with this XPathQName
     * @param namespaceAware set if namespaces should be processed or ignored
     */
    public XPathQName(String namespaceUri, String localName, boolean namespaceAware){
        if(namespaceUri == null) {
            this.namespaceUri = Constants.EMPTY_STRING;
        }else{
            this.namespaceUri = namespaceUri;
        }
        this.localName = localName;
        isNamespaceAware = namespaceAware;
    }

    /**
     * Create a new XPathQName with the specified QName
     * @param qname The QName to build this XPathQName from
     * @param namespaceAware set if namespaces should be processed or ignored
     */
    public XPathQName(QName qname, boolean namespaceAware){
        this(qname.getNamespaceURI(), qname.getLocalPart(), namespaceAware);
    }

    @Override
    public int hashCode() {
        if(null == localName) {
            return 1;
        } else {
            return localName.hashCode();
        }
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null || !(obj instanceof XPathQName)){
            return false;
        }
        XPathQName qName = (XPathQName)obj;
        if(isNamespaceAware && qName.isNamespaceAware){
             if(namespaceUri == null){
                    if(qName.namespaceUri != null){
                        return false;
                    }
                }else{
                    if(qName.namespaceUri == null){
                        return false;
                    }else if(namespaceUri != qName.namespaceUri && !(namespaceUri.equals(qName.namespaceUri))){
                        return false;
                    }
                }
        }
        return ((localName == qName.localName) || ((localName != null) && localName.equals(qName.localName)));
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if(null != namespaceUri && namespaceUri.length() > 0) {
            stringBuilder.append('{');
            stringBuilder.append(namespaceUri);
            stringBuilder.append('}');
        }
        if(null != localName) {
            stringBuilder.append(localName);
        }
        return stringBuilder.toString();
    }

}
