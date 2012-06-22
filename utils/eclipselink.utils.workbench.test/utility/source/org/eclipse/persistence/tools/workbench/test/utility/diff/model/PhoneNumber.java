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

public class PhoneNumber {
	private String areaCode;
	private String exchange;
	private String number;
	private String extension;
	private String comment;


	PhoneNumber(String areaCode, String exchange, String number, String extension) {
		super();
		this.areaCode = areaCode;
		this.exchange = exchange;
		this.number = number;
		this.extension = extension;
		this.comment = "";
	}

	PhoneNumber(String areaCode, String exchange, String number) {
		this(areaCode, exchange, number, null);
	}

	public String getAreaCode() {
		return this.areaCode;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getExchange() {
		return this.exchange;
	}
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public String getNumber() {
		return this.number;
	}
	public void setNumber(String number) {
		this.number = number;
	}

	public String getExtension() {
		return this.extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getComment() {
		return this.comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}

	public String toString() {
		return "(" + this.areaCode + ") " + this.exchange + "-" + this.number + " x" + this.extension;
	}

}
