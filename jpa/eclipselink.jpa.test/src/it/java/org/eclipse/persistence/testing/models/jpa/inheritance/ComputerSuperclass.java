/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     04/07/2012-2.5 Guy Pelletier
//       - 384275: Customizer from a mapped superclass is not overridden by an entity customizer
package org.eclipse.persistence.testing.models.jpa.inheritance;

import java.io.Serializable;

import jakarta.persistence.MappedSuperclass;

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
