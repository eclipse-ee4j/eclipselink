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
package org.eclipse.persistence.tools.workbench.mappingsmodel.query;

import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;



/**
 * This interface is used to define objects which can be queried upon by TopLink
 * Currently user defined query keys and most types of mappings are queryable
 * 
 * MWQueryables are used when building Expressions
 */
public interface MWQueryable extends MWNode
{
	List subQueryableElements(Filter queryableFilter);
	
	MWQueryable subQueryableElementAt(int index, Filter queryableFilter);
	
	boolean allowsChildren(); //any reference mapping allows children
	
	boolean isLeaf(Filter queryableFilter); //doesn't have sub query elements
	
	String getName();
	
	MWMappingDescriptor getParentDescriptor();
	
	boolean usesAnyOf();
	
	boolean allowsOuterJoin(); //allows null feature
	
	String iconKey();
	
	/**
     * Defines whether the queryable can be traversed to choose
     * a ReadAllQuery ordering.  An example of a traversable
     * Queryable would be an aggregate mapping.  The user could
     * traverse the aggregate mapping in the UI to choose a valid ordering.
	 */
	boolean isTraversableForReadAllQueryOrderable();
        
    /**
     * Defines whether the queryable is a valid ReadAllQuery ordering.  
     * An invalid ordering should have a neediness message as it 
     * will not work at runtime.
     */
    boolean isValidForReadAllQueryOrderable();
	
    /**
     * Defines whether the queryable can be traversed to choose
     * a ReportQuery attribute.  An example of a traversable
     * Queryable would be an aggregate mapping.  The user could
     * traverse the aggregate mapping in the UI to choose a valid ordering.
     */
    boolean isTraversableForReportQueryAttribute();

    /**
     * Defines whether the queryable is a valid ReportQuery attribute.  
     * An invalid attribute should have a neediness message as it 
     * will not work at runtime.
     */
    boolean isValidForReportQueryAttribute();

    /**
     * Defines whether the queryable can be traversed to choose
     * a joined attribute.  An example of a traversable
     * Queryable would be an aggregate mapping.  The user could
     * traverse the aggregate mapping in the UI to choose a valid 
     * joined attribute.
     */
    boolean isTraversableForJoinedAttribute();
   
    /**
     * Defines whether the queryable is a valid as a joined attribute.  
     * An invalid joined attribute should have a neediness message as it 
     * will not work at runtime.
     */	
    boolean isValidForJoinedAttribute();
	
    /**
     * Defines whether the queryable can be traversed to choose
     * a batch-read attribute.  An example of a traversable
     * Queryable would be an aggregate mapping.  The user could
     * traverse the aggregate mapping in the UI to choose a valid 
     * batch-read attribute.
     */
    boolean isTraversableForBatchReadAttribute();
    
    /**
     * Defines whether the queryable is a valid as a batch-read attribute.  
     * An invalid batch-read attribute should have a neediness message as it 
     * will not work at runtime.
     */ 
    boolean isValidForBatchReadAttribute();
	

    boolean isTraversableForQueryExpression();
    boolean isValidForQueryExpression();
}
