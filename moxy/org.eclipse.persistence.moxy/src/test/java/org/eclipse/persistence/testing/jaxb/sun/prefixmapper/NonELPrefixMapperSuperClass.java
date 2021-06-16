/*
 * Copyright (c) 2011, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
// Matt MacIvor - 2.4
package org.eclipse.persistence.testing.jaxb.sun.prefixmapper;

public abstract class NonELPrefixMapperSuperClass extends org.glassfish.jaxb.runtime.marshaller.NamespacePrefixMapper{

    @Override
    public abstract String getPreferredPrefix(String arg0, String arg1, boolean arg2);

    @Override
    public java.lang.String[] getPreDeclaredNamespaceUris() {
        return null;
    }

    @Override
    public java.lang.String[] getPreDeclaredNamespaceUris2() {
        return null;
    }

    @Override
    public java.lang.String[] getContextualNamespaceDecls() {
        return null;
    }
}
