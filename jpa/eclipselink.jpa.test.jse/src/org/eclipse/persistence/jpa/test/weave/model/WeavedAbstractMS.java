/*
 * Copyright (c) 2021 Oracle, and/or affiliates. All rights reserved.
 * Copyright (c) 2021 IBM Corporation. All rights reserved.
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
//     IBM - Bug 412391: Add support for weaving hidden variables
package org.eclipse.persistence.jpa.test.weave.model;

import javax.persistence.MappedSuperclass;

/**
 * Simple Entity that exists just so we can use the table in stored procedures
 */
@MappedSuperclass
public abstract class WeavedAbstractMS {
    private String parentOnlyAttribute;
    private Short hiddenAttribute;

    public String getParentOnlyAttribute() {
        return parentOnlyAttribute;
    }
    public void setParentOnlyAttribute(String parentOnlyAttribute) {
        this.parentOnlyAttribute = parentOnlyAttribute;
    }
    public Short getHiddenAttribute() {
        return hiddenAttribute;
    }
    public void setHiddenAttribute(Short hiddenAttribute) {
        this.hiddenAttribute = hiddenAttribute;
    }
}