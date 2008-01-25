/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml;

import java.util.ListIterator;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWReferenceObjectMapping;


public interface MWXmlReferenceMapping extends MWXmlMapping, MWReferenceObjectMapping {

	public final static String XML_FIELD_PAIRS_LIST = "xmlFieldPairs";
	
	public MWXmlDescriptor referenceDescriptor();
	
	public ListIterator<MWXmlFieldPair> xmlFieldPairs();
	
	public int xmlFieldPairsSize();
	
	public MWXmlFieldPair xmlFieldPairAt(int index);
	
	public MWXmlFieldPair addFieldPair(String sourceXpath, String targetXpath);
	
	public void addFieldPair(MWXmlFieldPair xmlFieldPair);
	
	public MWXmlFieldPair buildEmptyFieldPair();
	
	public void removeXmlFieldPair(MWXmlFieldPair xmlFieldPair);
	
	public void clearXmlFieldPairs(); 
	
	public boolean sourceFieldMayUseCollectionXpath();
	
	public MWDescriptor getReferenceDescriptor();
	
	public void setReferenceDescriptor(MWDescriptor newValue);
	
}
