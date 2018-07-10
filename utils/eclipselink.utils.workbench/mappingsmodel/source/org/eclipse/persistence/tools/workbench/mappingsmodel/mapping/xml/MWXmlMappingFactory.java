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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectMapMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMappingFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWTransformationMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;

public abstract class MWXmlMappingFactory
    implements MWMappingFactory
{
    // **************** Factory methods ***************************************

    public MWDirectMapping createDirectMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String name) {
        return new MWXmlDirectMapping((MWXmlDescriptor) descriptor, attribute, name);
    }

    public MWDirectCollectionMapping createDirectCollectionMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String name) {
        return new MWXmlDirectCollectionMapping((MWXmlDescriptor) descriptor, attribute, name);
    }

    public MWDirectMapMapping createDirectMapMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String name) {
        throw new UnsupportedOperationException("this is not yet supported, but will be in the future");
    }

    public MWCompositeObjectMapping createCompositeObjectMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String name) {
        return new MWCompositeObjectMapping((MWXmlDescriptor) descriptor, attribute, name);
    }

    public MWCompositeCollectionMapping createCompositeCollectionMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String name) {
        return new MWCompositeCollectionMapping((MWXmlDescriptor) descriptor, attribute, name);
    }

    public MWTransformationMapping createTransformationMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String name) {
        return new MWXmlTransformationMapping((MWXmlDescriptor) descriptor, attribute, name);
    }
}
