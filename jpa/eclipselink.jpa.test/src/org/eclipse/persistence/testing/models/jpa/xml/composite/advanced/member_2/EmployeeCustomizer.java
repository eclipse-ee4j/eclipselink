/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Guy Pelletier (Oracle), February 28, 2007 
 *        - New test file introduced for bug 217880.  
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.xml.composite.advanced.member_2;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;

public class EmployeeCustomizer implements DescriptorCustomizer {
    public EmployeeCustomizer() {}
    
    public void customize(ClassDescriptor descriptor) {
        descriptor.setShouldDisableCacheHits(false);
    }
}
