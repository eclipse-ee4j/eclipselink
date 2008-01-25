/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
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
