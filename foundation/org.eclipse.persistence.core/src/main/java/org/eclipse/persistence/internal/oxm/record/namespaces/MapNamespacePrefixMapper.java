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
//     Denise Smith - 2.3.3
package org.eclipse.persistence.internal.oxm.record.namespaces;

import java.util.Map;

import org.eclipse.persistence.oxm.NamespacePrefixMapper;

/**
 * Implementation of NamespacePrefixMapper.  Allows user to set a {@code Map<String, String>}
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
