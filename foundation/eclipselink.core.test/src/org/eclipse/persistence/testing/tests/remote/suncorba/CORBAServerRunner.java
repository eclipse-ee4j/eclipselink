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
package org.eclipse.persistence.testing.tests.remote.suncorba;

import org.eclipse.persistence.sessions.*;

public class CORBAServerRunner extends Thread
{
    protected Session session;
public CORBAServerRunner(Session session)
{
    this.session = session;
}
public void run()
{
    CORBAServerManagerController.start(this.session);
}
}
