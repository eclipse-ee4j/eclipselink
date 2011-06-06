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
package org.eclipse.persistence.tools.workbench.test.models.phone;

import java.util.List;

import org.eclipse.persistence.sessions.Record;

public class Address {
	/**
	 * Flags for converting int rate to booleans.
	 **/
	public final static int LOCAL_FLAG = 1;
	public final static int LONG_DISTANCE_FLAG = 2;
	public final static int DSL_FLAG = 4;
	
	
	/**
	 * To test DTF
	 **/
	private String street;
	/**
	 * To test OT
	 **/
	private String buildingType;
	public static final String[] BUILDING_TYPES = new String[] {"Apartment", "Single-family Home", "Multi-family home"};
	/**
	 * To test 1-1
	 **/
	private State state;
	/**
	 * To test 1-M, has a Variable 1-1 back pointer to Person or Company
	 **/
	private List serviceCalls; // (List of ServiceCalls)
	/**
	 * To test M-M
	 **/
	private List providers; // (List of Companies)
	/**
	 * To test DC
	 **/
	private List phoneNumbers; // List of Strings
	/**
	 * To test Transformation
	 * Determined by adding up a bunch of booleans in the database.
	 * For simplicity, it stores it as an int.  Of course, this is never
	 * how you'd do it in the real world, but I'm just coming up with a test
	 * here, so leave me alone.
	 **/
	private int rate;
/**
 * Address constructor comment.
 */
public Address() {
	super();
}
/**
 * Calculates and sets the rate based on the values in the database row.
 **/
public void calculateRate(Record row) {
	this.rate = 0;
	if (((Boolean)row.get("HAS_LOCAL")).booleanValue()) this.rate += LOCAL_FLAG;
	if (((Boolean)row.get("HAS_LONG_DISTANCE")).booleanValue()) this.rate += LONG_DISTANCE_FLAG;
	if (((Boolean)row.get("HAS_DSL")).booleanValue()) this.rate += DSL_FLAG;
}
/**
 * 
 * @return java.lang.String
 */
public java.lang.String getBuildingType() {
	return this.buildingType;
}
/**
 * 
 * @return java.util.List
 */
public java.util.List getPhoneNumbers() {
	return this.phoneNumbers;
}
/**
 * 
 * @return java.util.List
 */
public java.util.List getProviders() {
	return this.providers;
}
/**
 * 
 * @return int
 */
public int getRate() {
	return this.rate;
}
/**
 * 
 * @return java.util.List
 */
public java.util.List getServiceCalls() {
	return this.serviceCalls;
}
/**
 * 
 * @return org.eclipse.persistence.tools.workbench.test.models.phone.State
 */
public State getState() {
	return this.state;
}
/**
 * 
 * @return java.lang.String
 */
public java.lang.String getStreet() {
	return this.street;
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
/**
 * 
 * @param newBuildingType java.lang.String
 */
public void setBuildingType(java.lang.String newBuildingType) {
	this.buildingType = newBuildingType;
}
/**
 * 
 * @param newPhoneNumbers java.util.List
 */
public void setPhoneNumbers(java.util.List newPhoneNumbers) {
	this.phoneNumbers = newPhoneNumbers;
}
/**
 * 
 * @param newProviders java.util.List
 */
public void setProviders(java.util.List newProviders) {
	this.providers = newProviders;
}
/**
 * 
 * @param newRate int
 */
public void setRate(int newRate) {
	this.rate = newRate;
}
/**
 * 
 * @param newServiceCalls java.util.List
 */
public void setServiceCalls(java.util.List newServiceCalls) {
	this.serviceCalls = newServiceCalls;
}
/**
 * 
 * @param newState org.eclipse.persistence.tools.workbench.test.models.phone.State
 */
public void setState(State newState) {
	this.state = newState;
}
/**
 * 
 * @param newStreet java.lang.String
 */
public void setStreet(java.lang.String newStreet) {
	this.street = newStreet;
}
}
