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

import java.util.Date;

public class ServiceCall {
    private int id;
    private Date time;
    private Serviceable serviceUser;
/**
 * ServiceCall constructor comment.
 */
public ServiceCall() {
    super();
}
/**
 * Insert the method's description here.
 * Creation date: (11/27/00 5:25:21 PM)
 * @return int
 */
public int getId() {
    return this.id;
}
/**
 * Insert the method's description here.
 * Creation date: (11/27/00 5:25:21 PM)
 * @return org.eclipse.persistence.tools.workbench.test.models.phone.Serviceable
 */
public Serviceable getServiceUser() {
    return this.serviceUser;
}
/**
 *
 * @return java.util.Date
 */
public java.util.Date getTime() {
    return this.time;
}
/**
 * Insert the method's description here.
 * Creation date: (11/27/00 5:25:21 PM)
 * @param newId int
 */
public void setId(int newId) {
    this.id = newId;
}
/**
 * Insert the method's description here.
 * Creation date: (11/27/00 5:25:21 PM)
 * @param newServiceUser org.eclipse.persistence.tools.workbench.test.models.phone.Serviceable
 */
public void setServiceUser(Serviceable newServiceUser) {
    serviceUser = newServiceUser;
}
/**
 *
 * @param newTime java.util.Date
 */
public void setTime(java.util.Date newTime) {
    this.time = newTime;
}
}
