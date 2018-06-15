/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Gordon Yorke - Initial development
//
package org.eclipse.persistence.internal.jpa.querydef;

import java.lang.reflect.Constructor;

import javax.persistence.criteria.Selection;

import org.eclipse.persistence.internal.queries.ReportItem;
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

    protected transient Constructor constructor;
    protected Class[] constructorArgTypes;

    public ConstructorSelectionImpl(Class javaType, Selection[] subSelections) {
        super(javaType, subSelections, true);//need to validate selection items
    }

    public ConstructorReportItem translate(){
        ConstructorReportItem item = new ConstructorReportItem(this.getAlias());
        item.setResultType(this.getJavaType());
        item.setConstructor(constructor);
        for(Selection selection : this.getCompoundSelectionItems()){
            if (((SelectionImpl)selection).isCompoundSelection()){
                item.addItem(((ConstructorSelectionImpl)selection).translate());
            }else{
                ReportItem reportItem = new ReportItem(item.getName()+item.getReportItems().size(),
                        ((SelectionImpl)selection).getCurrentNode());
                //bug: 297385 - set type here because the selection already knows the type
                reportItem.setResultType(selection.getJavaType());
                item.addItem(reportItem);
            }
        }
        return item;

    }

    @Override
    public boolean isConstructor(){
        return true;
    }

    /**
     * INTERNAL:
     * Set the constructor.
     */
    public void setConstructor(Constructor constructor){
        this.constructor = constructor;
    }

    /**
     * INTERNAL:
     * Set the constructorArgTypes.
     */
    public void setConstructorArgTypes(Class[] constructorArgTypes){
        this.constructorArgTypes = constructorArgTypes;
    }
}
