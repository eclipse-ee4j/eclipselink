/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.internal.sessions.factories;

import org.eclipse.persistence.oxm.NamespaceResolver;

public class NamespaceResolverWithPrefixes extends NamespaceResolver {

    protected String primaryPrefix = null;
    protected String secondaryPrefix = null;

    public void putPrimary(String ns1, String primaryNamespace) {
        this.primaryPrefix = ns1;
        put(ns1, primaryNamespace);
    }
    public String getPrimaryPrefix() {
        return primaryPrefix;
    }
    public void putSecondary(String ns1, String secondaryNamespace) {
        this.secondaryPrefix = ns1;
        put(ns1, secondaryNamespace);
    }
    public String getSecondaryPrefix() {
        return secondaryPrefix;
    }
}
