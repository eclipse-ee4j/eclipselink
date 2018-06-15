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
package org.eclipse.persistence.testing.models.interfaces;

public class Asset {

    public CompanyAsset asset;

    public Asset() {
        super();
    }

    public Object clone() {
        Asset object = new Asset();
        object.asset = (CompanyAsset)this.asset.clone();
        return object;
    }

    public CompanyAsset getAsset() {
        return this.asset;
    }

    public java.math.BigDecimal getSerNum() {
        return null;
    }

    public void setAsset(CompanyAsset ca) {
        this.asset = ca;
    }
}
