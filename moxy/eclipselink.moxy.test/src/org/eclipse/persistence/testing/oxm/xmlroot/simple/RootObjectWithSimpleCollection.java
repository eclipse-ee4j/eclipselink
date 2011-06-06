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
package org.eclipse.persistence.testing.oxm.xmlroot.simple;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.oxm.mappings.directcollection.Employee;

public class RootObjectWithSimpleCollection {
	private List<String> theList;

	public RootObjectWithSimpleCollection() {
		theList = new ArrayList();
	}

	public List<String> getTheList() {
		return theList;
	}

	public void setTheList(List<String> theList) {
		this.theList = theList;
	}

	public boolean equals(Object object) {
		if (!(object instanceof RootObjectWithSimpleCollection))
			return false;
		RootObjectWithSimpleCollection theObject = (RootObjectWithSimpleCollection) object;

		if (this.getTheList() == null && theObject.getTheList() != null) {
			return false;
		}
		if (theObject.getTheList() == null && this.getTheList() != null) {
			return false;
		}

		if ((this.getTheList() == null && theObject.getTheList() == null)
				|| (this.getTheList().isEmpty() && theObject.getTheList()
						.isEmpty())
				|| ((this.getTheList().containsAll(theObject.getTheList())) && (theObject
						.getTheList().containsAll(this.getTheList())))) {

			return true;
		}
		return false;
	}
}
