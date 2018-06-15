/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// Matt MacIvor - 2.4
package org.eclipse.persistence.testing.jaxb.sun.prefixmapper;

import org.eclipse.persistence.oxm.XMLConstants;

public abstract class NonELPrefixMapperSuperClass extends com.sun.xml.bind.marshaller.NamespacePrefixMapper{

    public abstract String getPreferredPrefix(String arg0, String arg1, boolean arg2);

    public java.lang.String[] getPreDeclaredNamespaceUris() {
        return null;
    }

    public java.lang.String[] getPreDeclaredNamespaceUris2() {
        return null;
    }

    public java.lang.String[] getContextualNamespaceDecls() {
        return null;
    }
}
