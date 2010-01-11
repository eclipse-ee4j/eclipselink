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
