/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.jaxbcontext;

import java.util.List;

public class AbstractClassWithGenerics<T> {

    private List<T> a;

    public List<T> getA() {
        return a;
    }

    public void setA(List<T> a) {
        this.a = a;
    }

}
