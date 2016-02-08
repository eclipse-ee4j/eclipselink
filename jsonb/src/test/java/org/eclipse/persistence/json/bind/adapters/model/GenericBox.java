/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.persistence.json.bind.adapters.model;

/**
 * @author Roman Grigoriadi
 */
public class GenericBox<X> {

    private String strField;

    private X x;

    public GenericBox(String strField, X x) {
        this.strField = strField;
        this.x = x;
    }

    public GenericBox() {
    }

    public X getX() {
        return x;
    }

    public String getStrField() {
        return strField;
    }

    public void setStrField(String strField) {
        this.strField = strField;
    }

    public void setX(X x) {
        this.x = x;
    }
}
