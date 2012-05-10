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
package org.eclipse.persistence.tools.workbench.test.models.insurance;

/** 
 * <p><b>Purpose</b>: Represents an insurance vehicle claim on a policy.
 * <p><b>Description</b>: Held in a private 1:M relationship from Policy
 * @see Policy
 * @since TOPLink/Java 1.0
 */

public class VehicleClaim extends Claim
{
	private String partDescription;
	private String part;

/**
 * Return an example claim instance.
 */

public static VehicleClaim example1() 
{
	VehicleClaim vehicleClaim = new VehicleClaim();
	vehicleClaim.setId(301);
	vehicleClaim.setPart("dash board");
	vehicleClaim.setPartDescription("the thing above the dash");
	vehicleClaim.setAmount(20000);
	return vehicleClaim;
}
/**
 * Return an example claim instance.
 */

public static VehicleClaim example2() 
{
	VehicleClaim vehicleClaim = new VehicleClaim();
	vehicleClaim.setId(302);
	vehicleClaim.setPart("wheel");
	vehicleClaim.setPartDescription("a round steering type of thing");
	vehicleClaim.setAmount(10000);
	return vehicleClaim;
}
/**
 * Return an example claim instance.
 */

public static VehicleClaim example3() 
{
	VehicleClaim vehicleClaim = new VehicleClaim();
	vehicleClaim.setId(303);
	vehicleClaim.setPart("wheel");
	vehicleClaim.setPartDescription("a round steering type of thing");
	vehicleClaim.setAmount(10000);
	return vehicleClaim;
}
public String getPart() 
{
	return this.part;
}
public String getPartDescription() 
{
	return this.partDescription;
}
public void setPart(String part) 
{
	this.part = part;
}
public void setPartDescription(String partDescription) 
{
	this.partDescription = partDescription;
}
}
