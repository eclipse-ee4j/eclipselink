/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.config;

import org.eclipse.persistence.annotations.Customizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * Customize a {@link ClassDescriptor} when the
 * {@link #customize(ClassDescriptor)} method is called during the
 * loading/population of the mappings. This is typically used to customize
 * dynamically or specify configuration values not available through annotations
 * or XML.
 * 
 * @see Customizer @Customizer to configure using annotations on an entity class
 * @see ClassDescriptor {@link ClassDescriptor} for available customization API
 */
public interface DescriptorCustomizer {

    /**
     * Customize the provided descriptor. This method is called after the
     * descriptor is populated form annotations/XML/defaults but before it is
     * initialized.
     */
    public void customize(ClassDescriptor descriptor) throws Exception;
}
