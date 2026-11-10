/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.test.basic.model.nonnull;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Basic;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class NonnullPropertyBase {
    @Nonnull
    private String inheritedProperty;

    @Nonnull
    @Basic
    public String getInheritedProperty() {
        return inheritedProperty;
    }

    public void setInheritedProperty(String inheritedProperty) {
        this.inheritedProperty = inheritedProperty;
    }
}
