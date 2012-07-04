/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     04/07/2012-2.5 Guy Pelletier    
 *       - 384275: Customizer from a mapped superclass is not overridden by an entity customizer
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.inheritance;

import java.io.Serializable;

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
