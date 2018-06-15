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
package org.eclipse.persistence.testing.sdo.helper.datafactory;

import commonj.sdo.DataObject;

public interface USAddress extends DataObject {
    public java.lang.String getName();

    public void setName(java.lang.String value);

    public java.lang.String getStreet();

    public void setStreet(java.lang.String value);
}
