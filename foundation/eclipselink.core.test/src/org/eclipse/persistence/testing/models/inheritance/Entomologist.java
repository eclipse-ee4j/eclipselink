/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.inheritance;

import java.util.Collection;

import org.eclipse.persistence.indirection.ValueHolderInterface;

public class Entomologist {
	protected int id;
	protected String name;
	protected ValueHolderInterface insectCollection;
	
	public Collection getInsectCollection() {
		return (Collection)insectCollection.getValue();
	}
	public void setInsectCollection(Collection insectCollection) {
		this.insectCollection.setValue(insectCollection);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

}
