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

import org.eclipse.persistence.oxm.XMLConstants;

/**
 *  This is a complete UnmarshalNamespaceResolver implementation.  This is 
 *  useful when using XML input from sources such as SAX. 
 */
public class StackUnmarshalNamespaceResolver implements UnmarshalNamespaceResolver {
	
    private Map<String, Stack<String>> namespaceMap;
    private Map<String, Stack<String>> uriToPrefixMap;
    
	public StackUnmarshalNamespaceResolver(){
		namespaceMap = new HashMap<String, Stack<String>>();
		uriToPrefixMap = new HashMap<String, Stack<String>>();
	}

    public String getPrefix(String namespaceURI) {
        String prefix = null;
        if(null == prefix) {
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
            prefix = XMLConstants.EMPTY_STRING;
        } 
        
        Stack<String> uriStack = namespaceMap.get(prefix);
        if(uriStack != null && uriStack.size() > 0) {
            namespaceURI = uriStack.peek();
        }        
        return namespaceURI;
    }

    public void pop(String prefix) {        
        Stack<String> uriStack = namespaceMap.get(prefix);        
        if(uriStack != null && uriStack.size() > 0) {
            String uri = uriStack.pop();
            if(uri != null) {
                Stack<String> prefixStack = uriToPrefixMap.get(uri);
                if(prefixStack != null && prefixStack.size() > 0) {
                    prefixStack.pop();
                }
            }
        }        
    }

    public void push(String prefix, String namespaceURI) {                
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
        return namespaceMap.keySet();
    }

}
