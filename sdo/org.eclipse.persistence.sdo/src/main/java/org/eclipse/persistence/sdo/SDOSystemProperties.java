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
//     Oracle - 4.0.x - initial implementation
package org.eclipse.persistence.sdo;

/**
 * This class provides the list of System properties that are recognized by EclipseLink SDO component.
 * @author rfelcman
 */
public final class SDOSystemProperties {

    private SDOSystemProperties() {
    }

    /**
     * Property controls strictness of {@link commonj.sdo.Type#getInstanceClass()} type checking.
     *
     * <p>
     * See {@link org.eclipse.persistence.sdo.helper.SDOHelperContext#isStrictTypeCheckingEnabled()} for more details.
     * By this property, the initial value can be changed.
     * Default value is {@code true}.
     * </p>
     */
    public static final String SDO_STRICT_TYPE_CHECKING_PROPERTY_NAME = "eclipselink.sdo.strict.type.checking";

    /**
     * Property controls maximum size of helperContexts map.
     * This is way how Least Recently Used (LRU) strategy will be intensive. It should help with used Java heap size issues
     * if there is so many SDOClassLoader instances with so many defined classes.
     * Default value is {@code 1 000 000}.
     */
    public static final String SDO_HELPER_CONTEXTS_MAX_SIZE = "eclipselink.sdo.helper.contexts.max.size";
}
