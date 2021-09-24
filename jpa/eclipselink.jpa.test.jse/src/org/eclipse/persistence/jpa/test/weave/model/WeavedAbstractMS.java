/*******************************************************************************
 * Copyright (c) 2021 Oracle and/or its affiliates, IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     09/24/2021 - Will Dazey
 *       - 412391 : Add support for weaving hidden variables
 ******************************************************************************/
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