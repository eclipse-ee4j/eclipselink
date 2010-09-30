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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
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