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

import javax.json.bind.annotation.JsonbProperty;

/**
 * @author Roman Grigoriadi
 */
public class GenericCrate<T> {

    private String crateStrField;

    @JsonbProperty("adaptedT")
    private T t;

    public GenericCrate(String crateStrField, T t) {
        this.crateStrField = crateStrField;
        this.t = t;
    }

    public GenericCrate() {
    }

    public String getCrateStrField() {
        return crateStrField;
    }

    public T getT() {
        return t;
    }

    public void setCrateStrField(String crateStrField) {
        this.crateStrField = crateStrField;
    }

    public void setT(T t) {
        this.t = t;
    }
}
