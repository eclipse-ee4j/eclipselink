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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Selection;

import org.eclipse.persistence.expressions.Expression;
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
 * @since EclipseLink 2.0
 */
public class ConstructorSelectionImpl<X> extends SelectionImpl<X> {
    
    protected List<Selection<?>> subSelections;
    
    public <T> ConstructorSelectionImpl(Class<X> javaType, Expression expressionNode){
        super(javaType, expressionNode);
        this.subSelections = new ArrayList<Selection<?>>();
    }

    public boolean isConstructor(){
        return true;
    }
    
    public void setSelections(List<Selection<?>> selections){
        this.subSelections = selections;
    }
    
    public void setSelections(Selection<?>[] selections){
        this.subSelections.clear();
        for (Selection<?> selection : selections){
            this.subSelections.add(selection);
        }
    }
    
    public List<Selection<?>> getSelections(){
        return this.subSelections;
    }
    
    public ConstructorReportItem translate(){
        ConstructorReportItem item = new ConstructorReportItem(this.getAlias());
        item.setResultType(this.getJavaType());
        ((ConstructorSelectionImpl)this).getSelections();
        for(Selection selection : this.getSelections()){
            if (((SelectionImpl)selection).isConstructor()){
                item.addItem(((ConstructorSelectionImpl)selection).translate());
            }else{
                item.addAttribute(((SelectionImpl)selection).getCurrentNode());
            }
        }
        return item;

    }
}
