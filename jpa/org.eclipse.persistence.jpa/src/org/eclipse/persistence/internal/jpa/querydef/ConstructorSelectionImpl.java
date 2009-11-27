/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Gordon Yorke - Initial development
 *
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.querydef;

import javax.persistence.criteria.Selection;

import org.eclipse.persistence.queries.ConstructorReportItem;

/**
 * <p>
 * <b>Purpose</b>: Contains the implementation of the Selection interface of the JPA
 * criteria API.  Specifically this class represents the Selection of a Constructor.
 * <p>
 * <b>Description</b>: The Selection is the expression describing what should be returned by the query.
 * <p>
 * 
 * @see javax.persistence.criteria Join
 * 
 * @author gyorke
 * @since EclipseLink 1.2
 */
public class ConstructorSelectionImpl extends CompoundSelectionImpl {
    
    public ConstructorSelectionImpl(Class javaType, Selection[] subSelections) {
        super(javaType, subSelections);
    }

    public ConstructorReportItem translate(){
        ConstructorReportItem item = new ConstructorReportItem(this.getAlias());
        item.setResultType(this.getJavaType());
        for(Selection selection : this.getCompoundSelectionItems()){
            if (((SelectionImpl)selection).isCompoundSelection()){
                item.addItem(((ConstructorSelectionImpl)selection).translate());
            }else{
                item.addAttribute(((SelectionImpl)selection).getCurrentNode());
            }
        }
        return item;

    }
    
    public boolean isConstructor(){
        return true;
    }
    
    
    public void findRootAndParameters(AbstractQueryImpl query){
        for (Selection selection: getCompoundSelectionItems()){
            ((InternalSelection)selection).findRootAndParameters(query);
        }
    }

}
