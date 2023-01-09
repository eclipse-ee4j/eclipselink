/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.services.wildfly;

import org.eclipse.persistence.services.ClassSummaryDetailBase;

/**
 * The class is used internally by the Portable JMX Framework to convert
 * model specific classes into Open Types so that the attributes of model class can
 * be exposed by MBeans.
 *
 * @since EclipseLink 4.0.1
 */
public class ClassSummaryDetail extends ClassSummaryDetailBase {

    static {
        COMPOSITE_TYPE_TYPENAME = "org.eclipse.persistence.services.wildfly";
        COMPOSITE_TYPE_DESCRIPTION = "org.eclipse.persistence.services.wildfly.ClassSummaryDetail";
    }

    /**
     * Construct a ClassSummaryDetail instance. The PropertyNames annotation is used
     * to be able to construct a ClassSummaryDetail instance out of a CompositeData
     * instance. See MXBeans documentation for more details.
     */
    public ClassSummaryDetail(String className, String cacheType, String configuredSize, String currentSize , String parentClassName) {
        super(className, cacheType, configuredSize, currentSize , parentClassName);
    }
}
