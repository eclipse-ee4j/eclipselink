/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
//     04/07/2012-2.5 Guy Pelletier
//       - 384275: Customizer from a mapped superclass is not overridden by an entity customizer
package org.eclipse.persistence.testing.models.jpa21.advanced.inheritance;

import javax.persistence.MappedSuperclass;

import org.eclipse.persistence.annotations.Customizer;
import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;

@MappedSuperclass
@Customizer(ComputerSuperclass.class)
public class ComputerSuperclass implements DescriptorCustomizer {
    public ComputerSuperclass() {}

    // Since the computer class specifies its Customizer this customizer class
    // should not be called.
    // Verified In EntityManagerJUnitTestCase - testOverriddenCustomizer
    public void customize(ClassDescriptor descriptor) {
        descriptor.setAlias("InvalidAliasName");
    }
}
