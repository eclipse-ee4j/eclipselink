/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     bdoughan - June 25/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.internal.oxm.record.namespaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.persistence.internal.oxm.Constants;

/**
 *  This is a complete UnmarshalNamespaceResolver implementation.  This is
 *  useful when using XML input from sources such as SAX.
 */
public class StackUnmarshalNamespaceResolver implements UnmarshalNamespaceResolver {

    private Map<String, List<String>> namespaceMap;
    private Map<String, List<String>> uriToPrefixMap;

    public StackUnmarshalNamespaceResolver(){
        namespaceMap = new HashMap<String, List<String>>();
        uriToPrefixMap = new HashMap<String, List<String>>();
    }

    @Override
    public String getPrefix(String namespaceURI) {
        List<String> prefixes = uriToPrefixMap.get(namespaceURI);
        if(prefixes != null){
            int size = prefixes.size();
            if(size > 0) {
                return prefixes.get(size - 1);
            }
        }
        return null;
    }

    @Override
    public String getNamespaceURI(String prefix) {
        if(prefix == null) {
            prefix = Constants.EMPTY_STRING;
        }

        List<String> uris = namespaceMap.get(prefix);
        if(uris != null){
            int size = uris.size();
            if(size > 0) {
                return uris.get(size - 1);
            }
        }
        return null;
    }

    @Override
    public void pop(String prefix) {
        List<String> uris = namespaceMap.get(prefix);
        if(uris != null){
            int size = uris.size();
            if(size > 0) {
                String uri = uris.remove(size - 1);
                if(size == 1) {
                    //if there was only 1 uri, when it's removed, remove this prefix
                    //from the map
                    namespaceMap.remove(prefix);
                }
                if(uri != null) {
                    List<String> prefixes = uriToPrefixMap.get(uri);
                    if(prefixes != null){
                        int prefixesSize = prefixes.size();
                        if(prefixesSize == 1) {
                            uriToPrefixMap.remove(uri);
                        }
                        else {
                            prefixes.remove(prefixesSize - 1);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void push(String prefix, String namespaceURI) {
        List<String> uris = namespaceMap.get(prefix);
        if(uris == null) {
            uris = new ArrayList<String>();
            namespaceMap.put(prefix, uris);
        }
        uris.add(namespaceURI);
        List<String> prefixes = uriToPrefixMap.get(namespaceURI);
        if(prefixes == null) {
            prefixes = new ArrayList<String>();
            uriToPrefixMap.put(namespaceURI, prefixes);
        }
        prefixes.add(prefix);
    }

    @Override
    public Set<String> getPrefixes() {
        return namespaceMap.keySet();
    }

}
