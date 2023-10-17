/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Gordon Yorke - Initial development
//
package org.eclipse.persistence.internal.jpa.querydef;

import java.lang.reflect.Constructor;

import jakarta.persistence.criteria.Selection;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.queries.ConstructorReportItem;

/**
 * <p>
 * <b>Purpose</b>: Contains the implementation of the Selection interface of the JPA
 * criteria API.  Specifically this class represents the Selection of a Constructor.
 * <p>
 * <b>Description</b>: The Selection is the expression describing what should be returned by the query.
 *
 * @param <X> the type of the selection item
 * @see jakarta.persistence.criteria Join
 *
 * @author gyorke
 * @since EclipseLink 1.2
 */
public class ConstructorSelectionImpl<X> extends CompoundSelectionImpl<X> {

    protected transient Constructor<? extends X> constructor;
    protected Class<?>[] constructorArgTypes;

    public ConstructorSelectionImpl(Class<? extends X> javaType, Selection<?>[] subSelections) {
        super(javaType, subSelections, true);//need to validate selection items
    }

    public ConstructorReportItem translate(){
        ConstructorReportItem item = new ConstructorReportItem(this.getAlias());
        item.setResultType(this.getJavaType());
        item.setConstructor(constructor);
        for(Selection<?> selection : this.getCompoundSelectionItems()) {
            if (selection.isCompoundSelection()){
                item.addItem(((ConstructorSelectionImpl<?>)selection).translate());
            }else{
                ReportItem reportItem = new ReportItem(item.getName()+item.getReportItems().size(),
                        ((SelectionImpl<?>)selection).getCurrentNode());
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
    public void setConstructor(Constructor<? extends X> constructor){
        this.constructor = constructor;
    }

    /**
     * INTERNAL:
     * Set the constructorArgTypes.
     */
    public void setConstructorArgTypes(Class<?>[] constructorArgTypes){
        this.constructorArgTypes = constructorArgTypes;
    }
}
