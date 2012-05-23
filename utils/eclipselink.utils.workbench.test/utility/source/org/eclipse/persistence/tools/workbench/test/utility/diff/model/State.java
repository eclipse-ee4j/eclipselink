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
package org.eclipse.persistence.tools.workbench.test.utility.diff.model;

import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

/**
 * 
 */
public final class State {
	private String abbreviation;
	private String name;


	private State(String abbreviation, String name) {
		super();
		this.abbreviation = abbreviation;
		this.name = name;
	}

	public String getAbbreviation() {
		return this.abbreviation;
	}

	public String getName() {
		return this.name;
	}

	public String toString() {
		return StringTools.buildToStringFor(this, this.abbreviation);
	}

	public final static State AL	= new State("AL", "Alabama");
	public final static State AK	= new State("AK", "Alaska");
	public final static State AZ	= new State("AZ", "Arizona");
	public final static State AR	= new State("AR", "Arkansas");
	public final static State CA	= new State("CA", "California");
	public final static State CO	= new State("CO", "Colorado");
	public final static State CT	= new State("CT", "Connecticut");
	public final static State DE	= new State("DE", "Delaware");
	public final static State DC	= new State("DC", "District of Columbia");
	public final static State FL	= new State("FL", "Florida");
	public final static State GA	= new State("GA", "Georgia");
	public final static State HI	= new State("HI", "Hawaii");
	public final static State ID	= new State("ID", "Idaho");
	public final static State IL	= new State("IL", "Illinois");
	public final static State IN	= new State("IN", "Indiana");
	public final static State IA	= new State("IA", "Iowa");
	public final static State KS	= new State("KS", "Kansas");
	public final static State KY	= new State("KY", "Kentucky");
	public final static State LA	= new State("LA", "Louisiana");
	public final static State ME	= new State("ME", "Maine");
	public final static State MD	= new State("MD", "Maryland");
	public final static State MA	= new State("MA", "Massachusetts");
	public final static State MI	= new State("MI", "Michigan");
	public final static State MN	= new State("MN", "Minnesota");
	public final static State MS	= new State("MS", "Mississippi");
	public final static State MO	= new State("MO", "Missouri");
	public final static State MT	= new State("MT", "Montana");
	public final static State NE	= new State("NE", "Nebraska");
	public final static State NV	= new State("NV", "Nevada");
	public final static State NH	= new State("NH", "New Hampshire");
	public final static State NJ	= new State("NJ", "New Jersey");
	public final static State NM	= new State("NM", "New Mexico");
	public final static State NY	= new State("NY", "New York");
	public final static State NC	= new State("NC", "North Carolina");
	public final static State ND	= new State("ND", "North Dakota");
	public final static State OH	= new State("OH", "Ohio");
	public final static State OK	= new State("OK", "Oklahoma");
	public final static State OR	= new State("OR", "Oregon");
	public final static State PA	= new State("PA", "Pennsylvania");
	public final static State RI	= new State("RI", "Rhode Island");
	public final static State SC	= new State("SC", "South Carolina");
	public final static State SD	= new State("SD", "South Dakota");
	public final static State TN	= new State("TN", "Tennessee");
	public final static State TX	= new State("TX", "Texas");
	public final static State UT	= new State("UT", "Utah");
	public final static State VT	= new State("VT", "Vermont");
	public final static State VA	= new State("VA", "Virginia");
	public final static State WA	= new State("WA", "Washington");
	public final static State WV	= new State("WV", "West Virginia");
	public final static State WI	= new State("WI", "Wisconsin");
	public final static State WY	= new State("WY", "Wyoming");
}
