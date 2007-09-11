/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.model.type;

import commonj.sdo.DataObject;

public interface USAddress extends DataObject {
    public java.lang.String getName();

    public void setName(java.lang.String value);

    public java.lang.String getStreet();

    public void setStreet(java.lang.String value);
}