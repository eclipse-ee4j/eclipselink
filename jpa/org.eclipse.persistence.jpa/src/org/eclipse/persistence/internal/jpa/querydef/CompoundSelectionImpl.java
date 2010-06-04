/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CompoundSelection;
import javax.persistence.criteria.Selection;

/**
 * <p>
 * <b>Purpose</b>: Contains the implementation of the Selection interface of the JPA
 * criteria API.
 * <p>
 * <b>Description</b>: The Selection is the expression describing what should be returned by the query.
 * <p>
 * 
 * @see javax.persistence.criteria Join
 * 
 * @author gyorke
 * @since EclipseLink 1.2
 * 
 */

public class CompoundSelectionImpl extends SelectionImpl implements CompoundSelection{
    
    protected ArrayList<Selection<?>> subSelections;

    public CompoundSelectionImpl(Class javaType, Selection[] subSelections) {
        super(javaType, null);
        this.subSelections = new ArrayList();
        for (Selection sel : subSelections){
            if (((InternalSelection)sel).isFrom()){
                ((FromImpl)sel).isLeaf = false;
            }
            this.subSelections.add(sel);
        }
    }

    /**
     * Whether the selection item is a compound selection
     * @return boolean 
     */
    public boolean isCompoundSelection(){
        return true;
    }

    /**
     * Return selection items composing a compound selection
     * @return list of selection items
     * @throws IllegalStateException if selection is not a compound
     *           selection
     */
    public List<Selection<?>> getCompoundSelectionItems(){
        return (List<Selection<?>>)this.subSelections;
    }
    
    public void findRootAndParameters(AbstractQueryImpl criteriaQuery){
        for (Selection selection: getCompoundSelectionItems()){
            ((InternalSelection)selection).findRootAndParameters(criteriaQuery);
        }
    }

}
