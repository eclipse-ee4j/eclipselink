/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational;

import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryable;
import org.eclipse.persistence.tools.workbench.utility.node.Problem;


public interface MWQueryableArgumentParent extends MWNode {

    void propertyChanged(Undoable container, String propertyName, Object oldValue, Object newValue);

    MWQuery getParentQuery();
    
    void addQueryableNullProblemTo(List currentProblems);
    
    boolean isQueryableValid(MWQueryable queryable);
    
    Problem queryableInvalidProblem(MWQueryable queryable);
}
