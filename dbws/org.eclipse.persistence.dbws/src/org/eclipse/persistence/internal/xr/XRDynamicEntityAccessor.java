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
package org.eclipse.persistence.internal.xr;

//EclipseLink imports
import org.eclipse.persistence.internal.dynamic.ValuesAccessor;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * <p>
 * <b>INTERNAL:</b> XRDynamicEntityAccessor is used by dynamically generated
 * subclasses of {@link XRDynamicEntity} to 'close over' information for the
 * psuedo-attributes in the propertiesMap inherited from {@link XRDynamicEntity}.
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */

@SuppressWarnings("serial")
public class XRDynamicEntityAccessor extends ValuesAccessor {

    public XRDynamicEntityAccessor(DatabaseMapping mapping) {
        super(mapping);
    }
}
