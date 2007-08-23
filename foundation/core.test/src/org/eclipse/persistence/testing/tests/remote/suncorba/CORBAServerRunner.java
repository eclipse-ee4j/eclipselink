/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
