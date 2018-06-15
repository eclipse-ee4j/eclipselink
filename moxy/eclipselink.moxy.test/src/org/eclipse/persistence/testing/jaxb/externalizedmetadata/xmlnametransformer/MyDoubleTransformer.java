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
// Denise Smith - 2.3
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnametransformer;

import org.eclipse.persistence.oxm.XMLNameTransformer;

public class MyDoubleTransformer implements XMLNameTransformer{

    public String transformTypeName(String name) {
        String shortName =  name.substring(name.lastIndexOf('.') + 1);
        return shortName + shortName;
    }

    public String transformRootElementName(String name) {
        return name + name;
    }

    public String transformElementName(String name) {
        return name + name;
    }

    public String transformAttributeName(String name) {
        return name + name;
    }

}
