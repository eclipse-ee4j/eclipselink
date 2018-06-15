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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWOXDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;

public final class MWOXMappingFactory
    extends MWXmlMappingFactory
{
    // **************** Variables *********************************************

    private static MWOXMappingFactory INSTANCE;


    // **************** Static methods ****************************************

    public static synchronized MWOXMappingFactory instance() {
        if (INSTANCE == null) {
            INSTANCE = new MWOXMappingFactory();
        }
        return INSTANCE;
    }


    // **************** Factory methods ***************************************

    public MWAnyObjectMapping createAnyObjectMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String name) {
        return new MWAnyObjectMapping((MWOXDescriptor) descriptor, attribute, name);
    }

    public MWAnyCollectionMapping createAnyCollectionMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String name) {
        return new MWAnyCollectionMapping((MWOXDescriptor) descriptor, attribute, name);
    }

    public MWAnyAttributeMapping createAnyAttributeMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String name) {
        return new MWAnyAttributeMapping((MWXmlDescriptor) descriptor, attribute, name);
    }

    public MWXmlFragmentMapping createXmlFragmentMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String name) {
        return new MWXmlFragmentMapping((MWXmlDescriptor) descriptor, attribute, name);
    }

    public MWXmlFragmentCollectionMapping createXmlFragmentCollectionMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String name) {
        return new MWXmlFragmentCollectionMapping((MWXmlDescriptor) descriptor, attribute, name);
    }

    public MWXmlObjectReferenceMapping createXmlObjectReferenceMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String name) {
        return new MWXmlObjectReferenceMapping((MWXmlDescriptor) descriptor, attribute, name);
    }

    public MWXmlCollectionReferenceMapping createXmlCollectionReferenceMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String name) {
        return new MWXmlCollectionReferenceMapping((MWXmlDescriptor) descriptor, attribute, name);
    }

}
