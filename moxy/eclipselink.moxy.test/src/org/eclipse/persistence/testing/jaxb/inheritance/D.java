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
package org.eclipse.persistence.testing.jaxb.inheritance;

import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.testing.jaxb.events.Employee;

@XmlTransient
public class D extends C{
	protected int ddd;

	public int getDdd() {
		return ddd;
	}

	public void setDdd(int ddd) {
		this.ddd = ddd;
	}

}
