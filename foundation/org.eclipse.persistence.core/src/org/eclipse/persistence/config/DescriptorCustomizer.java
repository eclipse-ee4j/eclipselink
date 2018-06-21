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
     * descriptor is populated from annotations/XML/defaults but before it is
     * initialized.
     */
    public void customize(ClassDescriptor descriptor) throws Exception;
}
