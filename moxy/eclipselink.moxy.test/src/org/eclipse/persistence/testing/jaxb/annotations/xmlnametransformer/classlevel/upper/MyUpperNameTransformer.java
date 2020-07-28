/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - 2.3
package org.eclipse.persistence.testing.jaxb.annotations.xmlnametransformer.classlevel.upper;

import java.beans.Introspector;

import org.eclipse.persistence.oxm.XMLNameTransformer;

public class MyUpperNameTransformer implements XMLNameTransformer{

    public String transformTypeName(String name) {
        return name.toUpperCase();
    }

    public String transformElementName(String name) {
        return name.toUpperCase();
    }

    public String transformAttributeName(String name) {
        return name.toUpperCase();
    }

    public String transformRootElementName(String name) {
         name = name.substring(name.lastIndexOf('.') + 1);
        return name.toUpperCase();
    }

}
