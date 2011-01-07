/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import java.io.Serializable;

/** 
 * <p><b>Purpose</b>: Represents an insurance claim on a policy.
 * <p><b>Description</b>: Held in a private 1:M relationship from Policy
 * @see PolicyHolder
 * @since TOPLink/Java 1.0
 */

public abstract class Claim implements Serializable 
{
	private long id;
	private float amount;
	private Policy policy;

	
public Claim()
{
	this.amount = (float) 0.0;
}
public float getAmount() 
{
	return amount;
}
public long getId() 
{
	return id;
}

/**
 * Return an example claim instance.
 */

public HealthClaim healthClaimExample() 
{
	HealthClaim healthClaim = new HealthClaim();
	healthClaim.setId(200);
	healthClaim.setDisease("Flu");
	healthClaim.setAmount(1000);
	return healthClaim;
}

public Policy getPolicy() 
{
	return policy;
}
public void setAmount(float amount) 
{
	this.amount = amount;
}
public void setId(long id) 
{
	this.id = id;
}
public void setPolicy(Policy policy) 
{
	this.policy = policy;
}
@Override
public String toString()
{
	return org.eclipse.persistence.internal.helper.Helper.getShortClassName(getClass()) + ": " + getAmount();
}
}
