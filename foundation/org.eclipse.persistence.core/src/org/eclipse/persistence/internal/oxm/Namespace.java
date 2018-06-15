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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.oxm;


/**
 * INTERNAL:
 * This class represents a Namespace. It is used to persist a collection of
 * Namespaces to Deployment XML
 * @author  mmacivor
 * @since   release specific (what release of product did this appear in)
 */
public class Namespace {
    protected String prefix;
    protected String namespaceURI;

    public Namespace() {
    }

    public Namespace(String prefix, String namespaceURI) {
        this.prefix = prefix;
        this.namespaceURI = namespaceURI;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String newPrefix) {
        prefix = newPrefix;
    }

    public String getNamespaceURI() {
        return namespaceURI;
    }

    public void setNamespaceURI(String newURI) {
        namespaceURI = newURI;
    }
}
