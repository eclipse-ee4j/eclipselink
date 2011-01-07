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

/**
 * Insert the type's description here.
 * Creation date: (9/22/00 10:51:15 AM)
 * @author: Christopher Garrett
 */
public class Company implements Serviceable {
	private int id;
	private String name;
	private Service service;
/**
 * Company constructor comment.
 */
public Company() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (11/27/00 4:58:39 PM)
 * @return int
 */
public int getId() {
	return id;
}
/**
 * 
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
}
/**
 * Insert the method's description here.
 * Creation date: (11/27/00 4:58:39 PM)
 * @return org.eclipse.persistence.tools.workbench.test.models.phone.Service
 */
public Service getService() {
	return service;
}
/**
 * Insert the method's description here.
 * Creation date: (11/27/00 4:58:39 PM)
 * @param newId int
 */
public void setId(int newId) {
	id = newId;
}
/**
 * 
 * @param newName java.lang.String
 */
public void setName(java.lang.String newName) {
	name = newName;
}
/**
 * Insert the method's description here.
 * Creation date: (11/27/00 4:58:39 PM)
 * @param newService org.eclipse.persistence.tools.workbench.test.models.phone.Service
 */
public void setService(Service newService) {
	service = newService;
}
}
