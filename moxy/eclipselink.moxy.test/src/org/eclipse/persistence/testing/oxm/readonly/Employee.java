/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.readonly;

import java.util.Vector;
/**
 *  @version 1.0
 *  @author  mmacivor
 *  @since   10g
 */

public class Employee 
{
	public String firstName;
	public String firstName2; //non-readonly mapping
	
	public Address primaryAddress;
	public Address primaryAddress2;
	
	public Vector primaryResponsibilities;
	public Vector primaryResponsibilities2;
	
	public Vector otherAddresses;
	public Vector otherAddresses2;
	
	public Vector normalHours;
	public Vector normalHours2;
}
