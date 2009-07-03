/*******************************************************************************
* Copyright (c) 1998, 2009 Oracle. All rights reserved.
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 *  This is a complete UnmarshalNamespaceResolver implementation.  This is 
 *  useful when using XML input from sources such as SAX. 
 */
public class StackUnmarshalNamespaceResolver implements UnmarshalNamespaceResolver {

    private Map<String, Stack<String>> namespaceMap;
    private Map<String, Stack<String>> uriToPrefixMap;

    public String getPrefix(String namespaceURI) {
        String prefix = null;
        if(null == prefix && null != uriToPrefixMap) {
            Stack<String> prefixStack = uriToPrefixMap.get(namespaceURI);
            if(prefixStack != null && prefixStack.size() > 0) {
                prefix = prefixStack.peek();
            }
        }
        return prefix;
    }

    public String getNamespaceURI(String prefix) {
        String namespaceURI = null;
        if(prefix == null) {
            prefix = "";
        } 
        if(null != namespaceMap) {
            Stack<String> uriStack = namespaceMap.get(prefix);
            if(uriStack != null && uriStack.size() > 0) {
                namespaceURI = uriStack.peek();
            }
        }
        return namespaceURI;
    }

    public void pop(String prefix) {
        if (null == namespaceMap) {
            return;
        }
        Stack<String> uriStack = namespaceMap.get(prefix);
        String uri = null;
        if(uriStack != null && uriStack.size() > 0) {
            uri = uriStack.pop();
        }
        if(uri != null && uriToPrefixMap != null) {
            Stack<String> prefixStack = uriToPrefixMap.get(uri);
            if(prefixStack != null && prefixStack.size() > 0) {
                prefixStack.pop();
            }
        }
    }

    public void push(String prefix, String namespaceURI) {
        if (null == namespaceMap) {
            namespaceMap = new HashMap<String, Stack<String>>();
        }
        if (uriToPrefixMap == null) {
            uriToPrefixMap = new HashMap<String, Stack<String>>();
        }
        Stack uriStack = namespaceMap.get(prefix);
        if(uriStack == null) {
            uriStack = new Stack<String>();
            namespaceMap.put(prefix, uriStack);
        }
        uriStack.push(namespaceURI);
        Stack<String> prefixStack = uriToPrefixMap.get(namespaceURI);
        if(prefixStack == null) {
            prefixStack = new Stack<String>();
            uriToPrefixMap.put(namespaceURI, prefixStack);
        }
        prefixStack.push(prefix);
    }

    public Set<String> getPrefixes() {
        if(null == namespaceMap) {
            return Collections.EMPTY_SET;
        }
        return namespaceMap.keySet();
    }

}
