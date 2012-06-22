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
package org.eclipse.persistence.tools.workbench.test.models.crimescene;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;

public class CrimeScene {
	private long id;
	private String description;
	private Collection keywords;
	private String crimeType;
	private Detective detective;
	private Collection suspects;
	private Victim victim;
	private Collection evidence;
	private long time;
/**
 * CrimeScene constructor comment.
 */
public CrimeScene() {
	super();
	setCrimeType(crimeTypes().elementAt(0));
	setKeywords(new ArrayList());
	setEvidence(new ArrayList());
}
public static void addToDescriptor(ClassDescriptor desc) {
}
	public static Vector<String> crimeTypes() {
		Vector<String> v = new Vector<String>();
		v.addElement("Murder");
		v.addElement("Burglary");
		v.addElement("Petty Theft");
		v.addElement("Indecent Exposure");
		v.addElement("Jaywalking");
		return v;
	}
/**
 * Return this crime's type
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @return java.lang.String
 */
public String getCrimeType() {
	return crimeType;
}
/**
 * Return the description of this crime
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @return java.lang.String
 */
public String getDescription() {
	return description;
}
/**
 * Return the detective in charge of this crime
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @return TOPLink.Course.CrimeScene.model.Detective
 */
public Detective getDetective() {
	return detective;
}
/**
 * Return the evidence found at the crime
 * @author Christopher Garrett
 * @return java.util.ArrayList
 */
public Collection getEvidence() {
	return evidence;
}
/**
 * Return the ID of this object
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @return long
 */
public long getId() {
	return id;
}
/**
 * Return the keywords for this crime scene
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @return java.util.Vector
 */
public Collection getKeywords() {
	return keywords;
}
/**
 * Return the list of suspects
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @return java.util.ArrayList
 */
public Collection getSuspects() {
	return suspects;
}
/**
 * 
 * @return long
 */
public long getTime() {
	return time;
}
/**
 * Return the victim of this crime
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @return TOPLink.Course.CrimeScene.model.Victim
 */
public Victim getVictim() {
	return victim;
}
/**
 * Set the type of this crime (e.g. burglary, murder, etc)
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @param newValue java.lang.String
 */
public void setCrimeType(String newValue) {
	this.crimeType = newValue;
}
/**
 * Set the description of this crime
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @param newValue java.lang.String
 */
public void setDescription(String newValue) {
	this.description = newValue;
}
/**
 * Set the detective in charge of this crime
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @param newValue TOPLink.Course.CrimeScene.model.Detective
 */
public void setDetective(Detective newValue) {
	detective=newValue;
}
/**
 * Set the Vector of the evidence in the crime
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @param newValue java.util.ArrayList
 */
public void setEvidence(Collection newValue) {
	evidence = newValue;
}
/**
 * Set the id of this object
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @param newValue long
 */
public void setId(long newValue) {
	this.id = newValue;
}
/**
 * Set the list of keywords for this crime scene
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @param newValue java.util.ArrayList
 */
public void setKeywords(Collection newValue) {
	keywords = newValue;
}
/**
 * Set the list of suspects for this crime
 * @author Christopher Garrett
 * @param newValue java.util.ArrayList
 */
public void setSuspects(Collection newValue) {
	suspects = newValue;
}
/**
 * 
 * @param newTime long
 */
public void setTime(long newTime) {
	time = newTime;
}
/**
 * Set the victim of this crime
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @param newValue TOPLink.Course.CrimeScene.model.Victim
 */
public void setVictim(Victim newValue) {
	victim = newValue;
}
/**
 * Returns a short description of this crime scene based on its type and date
 * e.g., "Burglary at 10:30 am, January 5"
 * @return a string representation of the receiver
 */
@Override
public String toString() {
	return "" + getCrimeType() + " #" + getId();
}
}
