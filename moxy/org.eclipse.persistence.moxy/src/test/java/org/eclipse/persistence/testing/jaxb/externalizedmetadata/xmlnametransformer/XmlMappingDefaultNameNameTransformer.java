/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnametransformer;

import org.eclipse.persistence.oxm.XMLNameTransformer;

public class XmlMappingDefaultNameNameTransformer implements XMLNameTransformer {

    @Override
    public String transformTypeName(String name) {
        return name + "_TYPE";
    }

    @Override
    public String transformElementName(String name) {
        return name + "_ELEMENT";
    }

    @Override
    public String transformAttributeName(String name) {
        return name + "_ATTRIBUTE";
    }

    @Override
    public String transformRootElementName(String name) {
        return name + "_ROOT";
    }

}
