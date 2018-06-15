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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

public class MySKU {
    private String SKUValue;

    public MySKU(String theValue) {
        SKUValue = theValue;
    }

    public void setSKUValue(String SKUValue) {
        this.SKUValue = SKUValue;
    }

    public String getSKUValue() {
        return SKUValue;
    }

    public String toString() {
        return SKUValue;
    }
}
