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

import java.sql.Date;

/** 
 * <p><b>Purpose</b>: Represents an insurance house policy.
 * <p><b>Description</b>: Held in a 1-M from PolicyHolder and has a 1-M to Claim.
 * @see Claim
 * @since TOPLink/Java 1.0
 */

public class HousePolicy extends Policy {
	private Date dateOfConstruction;

/** 
 * Return an example claim instance.
 */

public static HousePolicy example1() 
{
	HousePolicy housePolicy = new HousePolicy();
	housePolicy.setPolicyNumber(101);
	housePolicy.setDescription("Nice house.");
	housePolicy.setDateOfConstruction(new Date(97,5,10));
	housePolicy.setMaxCoverage(50000);
	housePolicy.setDescription("Fire and flood coverage");
	housePolicy.addClaim(HouseClaim.example1());
	housePolicy.addClaim(HouseClaim.example2());
	return housePolicy;
}
/** 
 * Return an example claim instance.
 */

public static HousePolicy example2() 
{
	HousePolicy housePolicy = new HousePolicy();
	housePolicy.setPolicyNumber(102);
	housePolicy.setDescription("Nice house.");
	housePolicy.setDateOfConstruction(new Date(97,5,12));
	housePolicy.setMaxCoverage(11111);
	housePolicy.setDescription("Theft Coverage");
	housePolicy.addClaim(HouseClaim.example4());
	return housePolicy;
}
/** 
 * date on which the house construction started
 */

public Date getDateOfConstruction() 
{
	return dateOfConstruction;
}
/** 
 * date on which the house construction started.
 */

public void setDateOfConstruction(Date dateOfConstruction) 
{
	this.dateOfConstruction = dateOfConstruction;
}
}
