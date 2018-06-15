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
//     Denise Smith - 2.3.3
package org.eclipse.persistence.internal.oxm.record.namespaces;

import java.util.Map;

import org.eclipse.persistence.oxm.NamespacePrefixMapper;

/**
 * Implementation of NamespacePrefixMapper.  Allows user to set a Map<String, String>
 * or uris to prefixes for to be used during marshal/unmarshal.
 * @since 2.3.3
 */
public class MapNamespacePrefixMapper extends NamespacePrefixMapper{

    private Map<String, String> urisToPrefixes;

    public MapNamespacePrefixMapper(Map<String, String> urisToPrefixes){
        this.urisToPrefixes = urisToPrefixes;
    }

    @Override
    public String getPreferredPrefix(String namespaceUri, String suggestion,
            boolean requirePrefix) {
        if(urisToPrefixes == null){
            return null;
        }else{
            return urisToPrefixes.get(namespaceUri);
        }
    }

    @Override
    public String[] getPreDeclaredNamespaceUris() {

        String[] uris = new String[urisToPrefixes.size()];
        return urisToPrefixes.keySet().toArray(uris);
    }

}
