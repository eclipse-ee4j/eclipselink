/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta;

/**
 * Interface defining Java metadata required by the
 * TopLink Mapping Workbench.
 *
 * @see java.lang.reflect.Field
 */
public interface ExternalField extends ExternalMember {

    /**
     * Returns an ExternalClassDescription object that identifies
     * the declared type for the field represented by
     * this ExternalField object.
     *
     * @see java.lang.reflect.Field#getType()
     */
    ExternalClassDescription getType();

}
