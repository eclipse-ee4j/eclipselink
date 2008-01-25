/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
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
