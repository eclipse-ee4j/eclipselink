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
package org.eclipse.persistence.tools.workbench.test.models.phone;

/**
 * A simple interface shared by the Person and Company classes.  This allows us to have
 * a 1-M from Address to Service Calls.  The back-pointer is a variable 1-1 to Addressible.
 */
public interface Addressable {
    public Address getAddress();
    public void setAddress(Address address);
}
