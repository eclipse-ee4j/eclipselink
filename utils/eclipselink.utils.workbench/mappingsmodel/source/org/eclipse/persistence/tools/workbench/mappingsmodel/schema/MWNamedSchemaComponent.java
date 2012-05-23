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
package org.eclipse.persistence.tools.workbench.mappingsmodel.schema;

import java.util.Collection;
import java.util.Iterator;

public interface MWNamedSchemaComponent 
	extends MWSchemaComponent 
{
	String getName();
	
	String getNamespaceUrl();
	
	MWNamespace getTargetNamespace();
	
	/** Return the XML Schema type name for this component */
	String componentTypeName();
	
	/** return an iterator of components from this one up to the top named component */
	public Iterator namedComponentChain();
	
	/** return whether the nested component is directly below this component in the name chain */
	boolean directlyOwns(MWNamedSchemaComponent nestedComponent);
	
	void addDirectlyOwnedComponentsTo(Collection directlyOwnedComponents);
	
	String qName();
}
