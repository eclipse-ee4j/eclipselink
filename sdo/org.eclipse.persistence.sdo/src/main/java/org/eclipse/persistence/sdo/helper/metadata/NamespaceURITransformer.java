/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     rick.barkhouse - added as part of fix for SDO 2.1.1 TCK 'sdoPathXSDQNameTest'
package org.eclipse.persistence.sdo.helper.metadata;

import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.mappings.transformers.FieldTransformer;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.sessions.Session;

public class NamespaceURITransformer implements FieldTransformer {

    private static final char HASH = '#';

    AbstractTransformationMapping transformationMapping;
    private NamespaceResolver namespaceResolver;

    public NamespaceURITransformer() {
        super();
    }

    @Override
    public void initialize(AbstractTransformationMapping mapping) {
        transformationMapping = mapping;
        namespaceResolver = ((XMLDescriptor) mapping.getDescriptor()).getNamespaceResolver();
    }

    @Override
    public Object buildFieldValue(Object instance, String fieldName, Session session) {
        if (null == instance) {
            return null;
        }

        String value = (String) transformationMapping.getAttributeValueFromObject(instance);
        if(value == null){
            return null;
        }
        int index = value.lastIndexOf(HASH);
        if (index > -1) {
            String namespaceURI = value.substring(0, index);
            if(null == namespaceResolver) {
                // If there is no NamespaceResolver, then assume the URI is in the default namespace
                return namespaceURI;
            }
            String prefix = namespaceResolver.resolveNamespaceURI(namespaceURI);
            if (prefix == null) {
                return namespaceURI;
            }
            return null;
        } else {
            return null;
        }
    }

}
