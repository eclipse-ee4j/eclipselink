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
*     bdoughan - June 25/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.internal.oxm.record.namespaces;

import java.util.Set;

/**
 * An abstraction to represent a namespace resolver for unmarshalling based on 
 * the XML input. 
 */
public interface UnmarshalNamespaceResolver {

    /**
     * Return the namespace URI for the specified prefix at the current scope. 
     */
    public String getNamespaceURI(String prefix);

    /**
     * Return the prefix for the specified namesapce URI at the current scope. 
     */
    public String getPrefix(String namespaceURI);

    /**
     * Associate a prefix and a namespace URI.  Note that this will override
     * any previous associations for the specified prefix until a corresponding 
     * "pop" call is made for this prefix.
     */
    public void push(String prefix, String namespaceURI);

    /**
     *  Remove the last declared namespace URI binding for this prefix.  Note 
     *  this will reveal the previous namespace URI binding for this prefix if
     *  there was one.  
     */
    public void pop(String prefix);

    /**
     * Return the set of prefixes currently associated with a namespace URI. 
     */
    public Set<String> getPrefixes();

}
