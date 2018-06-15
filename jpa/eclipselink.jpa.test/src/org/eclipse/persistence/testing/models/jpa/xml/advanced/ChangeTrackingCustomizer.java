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
//     James Sutherland (Oracle) - initial API and implementation
package org.eclipse.persistence.testing.models.jpa.xml.advanced;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.changetracking.DeferredChangeDetectionPolicy;

/**
 * This customizer will revert change tracking to deferred if weaving was not used.
 * @author James Sutherland
 */
public class ChangeTrackingCustomizer implements DescriptorCustomizer {
    public ChangeTrackingCustomizer() {}

    public void customize(ClassDescriptor descriptor) {
        if (System.getProperty("TEST_NO_WEAVING") != null) {
            descriptor.setObjectChangePolicy(new DeferredChangeDetectionPolicy());
        }
    }
}
