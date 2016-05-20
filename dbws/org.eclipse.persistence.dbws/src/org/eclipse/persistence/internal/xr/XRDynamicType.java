/*******************************************************************************
 * Copyright (c) 1998, 2016 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.dynamic.DynamicType;

//javase imports

//EclipseLink imports
import org.eclipse.persistence.internal.dynamic.DynamicTypeImpl;

/**
 * Dummy implementation of {@link DynamicType}
 * @author mnorman
 *
 */
public class XRDynamicType extends DynamicTypeImpl {

    public XRDynamicType() {
        super();
    }

    @Override
    public String getName() {
        return "<null>";
    }
}
