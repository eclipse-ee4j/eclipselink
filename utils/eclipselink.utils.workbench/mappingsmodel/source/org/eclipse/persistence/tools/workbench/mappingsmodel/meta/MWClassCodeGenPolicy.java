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
package org.eclipse.persistence.tools.workbench.mappingsmodel.meta;

import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregateMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToOneMapping;


public interface MWClassCodeGenPolicy {
	
	MWMethodCodeGenPolicy getMethodCodeGenPolicy(MWMethod method);
	
	void addAccessorCodeGenPolicy(MWMethod method, MWMethodCodeGenPolicy methodCodeGenPolicy);


	String classComment(MWClass mwClass);

	String emptyMethodBodyComment();
	
	String collectionImplementationClassNotDeterminedComment(MWClassAttribute attribute, MWClass concreteValueType);

	String oneToOneMappingThatControlsWritingOfPrimaryKeyComment(MWOneToOneMapping mapping);

	String aggregateMappingDoesNotAllowNullImplementationClassNotDeterminedComment();
	String aggregateMappingDoesNotAllowNullComment(MWAggregateMapping mapping);
}
