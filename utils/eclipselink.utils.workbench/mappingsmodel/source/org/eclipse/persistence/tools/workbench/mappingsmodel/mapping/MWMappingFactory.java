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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;


public interface MWMappingFactory
{
    MWDirectMapping createDirectMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String name);

    MWDirectCollectionMapping createDirectCollectionMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String name);

    MWDirectMapMapping createDirectMapMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String name);

    MWTransformationMapping createTransformationMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String name);
}
