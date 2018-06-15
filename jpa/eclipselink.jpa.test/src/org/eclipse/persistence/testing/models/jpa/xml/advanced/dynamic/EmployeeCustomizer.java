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
//     07/05/2010-2.1.1 Guy Pelletier
//       - 317708: Exception thrown when using LAZY fetch on VIRTUAL mapping
package org.eclipse.persistence.testing.models.jpa.xml.advanced.dynamic;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;

public class EmployeeCustomizer implements DescriptorCustomizer {
    public EmployeeCustomizer() {}

    public void customize(ClassDescriptor descriptor) {
        descriptor.setShouldDisableCacheHits(false);
    }
}
