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
 *              dclarke - initial JPA Employee example using XML (bug 217884)
 *              mbraeuer - annotated version
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.performance2;

import javax.persistence.*;

/**
 * The SmallProject class demonstrates usage of a way to limit a subclass to its parent's table when JOINED
 * inheritance is used.  This avoids having to have an empty SMALLPROJECT table by setting the table to that
 * of the superclass.
 */
@Entity
@Table(name = "P2_SPROJECT")
public class SmallProject extends Project {

	private SmallProject() {
		super();
	}

	public SmallProject(String name, String description) {
		this();
		setName(name);
		setDescription(description);
	}

}
