/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnametransformer;

import org.eclipse.persistence.oxm.XMLNameTransformer;

public class XmlMappingDefaultNameNameTransformer implements XMLNameTransformer {

    public String transformTypeName(String name) {
        return name + "_TYPE";
    }

    public String transformElementName(String name) {
        return name + "_ELEMENT";
    }

    public String transformAttributeName(String name) {
        return name + "_ATTRIBUTE";
    }

    public String transformRootElementName(String name) {
        return name + "_ROOT";
    }

}
