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
package org.eclipse.persistence.tools.workbench.test.models.phone;

import java.util.ArrayList;

import org.eclipse.persistence.sessions.Record;

/**
 * Insert the type's description here.
 * Creation date: (11/27/00 4:46:06 PM)
 * @author: Paul W Fullbright
 */
public class Service {

	private String billingAddress;
	private String servicePlan;
	private Person primaryContactPerson;
	private Contact primaryContact;
	private ArrayList serviceCalls;
	private ArrayList users;
	private ArrayList lineAccounts;
	private int rate;
	
	public final static String[] SERVICE_PLANS = {"Platinum", "Gold", "Standard"};

	// Flags for converting int rate to booleans.
	public final static int LOCAL_FLAG = 1;
	public final static int LONG_DISTANCE_FLAG = 2;
	public final static int DSL_FLAG = 4;
/**
 * Service constructor comment.
 */
public Service() {
	super();
}
/**
 * Calculates and sets the rate based on the values in the database row.
 **/
public int calculateRate(Record row) {
	this.rate = 0;
	if (((Boolean)row.get("HAS_LOCAL")).booleanValue()) this.rate += LOCAL_FLAG;
	if (((Boolean)row.get("HAS_LONG_DISTANCE")).booleanValue()) this.rate += LONG_DISTANCE_FLAG;
	if (((Boolean)row.get("HAS_DSL")).booleanValue()) this.rate += DSL_FLAG;
	
	return this.rate;
}
public boolean hasDslService() {
	return (this.rate & DSL_FLAG) > 0;
}
public boolean hasLocalService() {
	return (this.rate & LOCAL_FLAG) > 0;
}
public boolean hasLongDistanceService() {
	return (this.rate & LONG_DISTANCE_FLAG) > 0;
}
}
