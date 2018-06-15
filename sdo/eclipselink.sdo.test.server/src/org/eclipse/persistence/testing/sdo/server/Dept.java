/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     etang - April 12/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.sdo.server;

public interface Dept {

    public java.lang.Integer getDeptno();

    public void setDeptno(java.lang.Integer value);

    public java.lang.String getDname();

    public void setDname(java.lang.String value);

    public java.lang.String getLoc();

    public void setLoc(java.lang.String value);

}
