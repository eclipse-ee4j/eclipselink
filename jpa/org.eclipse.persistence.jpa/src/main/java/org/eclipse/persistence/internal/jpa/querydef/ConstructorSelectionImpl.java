/*
 * Copyright (c) 2011, 2026 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.FetchParent;
import jakarta.persistence.criteria.Selection;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.queries.ConstructorReportItem;
import org.eclipse.persistence.queries.LoadGroup;
import org.eclipse.persistence.queries.ReportQuery;

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

    private static final long serialVersionUID = -8383364112562934574L;

    protected transient Constructor<? extends X> constructor;
    protected Class<?>[] constructorArgTypes;

    public ConstructorSelectionImpl(Class<? extends X> javaType, Selection<?>[] subSelections) {
        super(javaType, subSelections, true);//need to validate selection items
    }

    public ConstructorReportItem translate(){
        return translate(null);
    }

    /** Translate constructor leaves and retain their fetches on the owning report. */
    public ConstructorReportItem translate(ReportQuery reportQuery){
        ConstructorReportItem item = new ConstructorReportItem(this.getAlias());
        item.setResultType(this.getJavaType());
        item.setConstructor(constructor);
        for(Selection<?> selection : this.getCompoundSelectionItems()) {
            if (selection.isCompoundSelection()){
                item.addItem(((ConstructorSelectionImpl<?>)selection).translate(reportQuery));
            }else{
                ReportItem reportItem = new ReportItem(item.getName()+item.getReportItems().size(),
                        BooleanExpressionImpl.selectionNode(selection));
                if (reportQuery != null && selection instanceof FromImpl<?, ?> from) {
                    // SQL-only joins preserve report cardinality; per-leaf loading completes the graph
                    // before constructor/cursor consumption without losing duplicate collection values.
                    reportItem.setLoadGroup(buildLoadGroup(from));
                    for (Expression fetch : from.findJoinFetches()) {
                        reportQuery.addNonFetchJoinedAttribute(fetch);
                    }
                }
                //bug: 297385 - set type here because the selection already knows the type
                reportItem.setResultType(selection.getJavaType());
                item.addItem(reportItem);
            }
        }
        return item;

    }

    private static LoadGroup buildLoadGroup(FetchParent<?, ?> parent) {
        if (parent.getFetches().isEmpty()) {
            return null;
        }
        LoadGroup group = new LoadGroup();
        addFetchPaths(group, parent, "");
        return group;
    }

    private static void addFetchPaths(LoadGroup group, FetchParent<?, ?> parent, String prefix) {
        for (Fetch<?, ?> fetch : parent.getFetches()) {
            String path = prefix + fetch.getAttribute().getName();
            group.addAttribute(path);
            addFetchPaths(group, fetch, path + '.');
        }
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
