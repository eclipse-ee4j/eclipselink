/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland (Oracle) - initial API and implementation  
 ******************************************************************************/  
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
