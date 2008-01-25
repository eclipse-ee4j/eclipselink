/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational;

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;


public interface MWTableReferenceMapping 
	extends MWNode 
{
	// **************** Batch Reading *****************************************
	
	boolean usesBatchReading();
	void setUsesBatchReading(boolean newValue);
		public final static String BATCH_READING_PROPERTY = "usesBatchReading";
	
	
	// **************** Reference *********************************************

	MWReference getReference();
	void setReference(MWReference reference);
		public final static String REFERENCE_PROPERTY = "reference";
	
	Iterator candidateReferences();
	boolean referenceIsCandidate(MWReference reference);
}
