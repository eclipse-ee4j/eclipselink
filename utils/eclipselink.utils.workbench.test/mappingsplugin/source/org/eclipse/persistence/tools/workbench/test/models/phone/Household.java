/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

/**
 * Insert the type's description here.
 * Creation date: (9/22/00 10:51:15 AM)
 * @author: Christopher Garrett
 */
public class Household implements Serviceable {
	private int id;
	private Person headOfHousehold;
	private Service service;
/**
 * Company constructor comment.
 */
public Household() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (11/27/00 4:59:42 PM)
 * @return org.eclipse.persistence.tools.workbench.test.models.phone.Person
 */
public Person getHeadOfHousehold() {
	return headOfHousehold;
}
/**
 * Insert the method's description here.
 * Creation date: (11/27/00 4:59:42 PM)
 * @return int
 */
public int getId() {
	return id;
}
/**
 * Insert the method's description here.
 * Creation date: (11/27/00 4:59:42 PM)
 * @return org.eclipse.persistence.tools.workbench.test.models.phone.Service
 */
public Service getService() {
	return service;
}
/**
 * Insert the method's description here.
 * Creation date: (11/27/00 4:59:42 PM)
 * @param newHeadOfHousehold org.eclipse.persistence.tools.workbench.test.models.phone.Person
 */
public void setHeadOfHousehold(Person newHeadOfHousehold) {
	headOfHousehold = newHeadOfHousehold;
}
/**
 * Insert the method's description here.
 * Creation date: (11/27/00 4:59:42 PM)
 * @param newId int
 */
public void setId(int newId) {
	id = newId;
}
/**
 * Insert the method's description here.
 * Creation date: (11/27/00 4:59:42 PM)
 * @param newService org.eclipse.persistence.tools.workbench.test.models.phone.Service
 */
public void setService(Service newService) {
	service = newService;
}
}
