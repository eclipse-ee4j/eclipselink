/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.models.phone;

/**
 * A simple interface shared by the Person and Company classes.  This allows us to have
 * a 1-M from Address to Service Calls.  The back-pointer is a variable 1-1 to Addressible.
 */
public interface Addressable {
	public Address getAddress();
	public void setAddress(Address address);
}
