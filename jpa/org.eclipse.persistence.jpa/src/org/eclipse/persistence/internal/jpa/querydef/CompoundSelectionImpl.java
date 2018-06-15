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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CompoundSelection;
import javax.persistence.criteria.Selection;

import org.eclipse.persistence.internal.localization.ExceptionLocalization;

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
    //bug 366386 - track items using duplicate alias names
    protected ArrayList<String> duplicateAliasNames;

    public CompoundSelectionImpl(Class javaType, Selection[] subSelections) {
        this(javaType, subSelections, false);
    }

    public CompoundSelectionImpl(Class javaType, Selection[] subSelections, boolean validate) {
        super(javaType, null);
        this.subSelections = new ArrayList();
        //used to validate that an alias is only used once
        java.util.Map tempMap = new java.util.TreeMap();
        for (Selection sel : subSelections) {
            if (validate) {
                if (((SelectionImpl)sel).isCompoundSelection() && !((SelectionImpl)sel).isConstructor()) {
                    throw new IllegalArgumentException(ExceptionLocalization.buildMessage("jpa_criteriaapi_illegal_tuple_or_array_value", new Object[] { sel }));
                }
            }
            String alias = sel.getAlias();
            if (alias != null) {
                if (tempMap.containsKey(alias)) {
                    if (duplicateAliasNames == null) {
                        duplicateAliasNames=new ArrayList<String>();
                    }
                    duplicateAliasNames.add(alias);
                } else {
                    tempMap.put(alias, sel);
                }
            }
            this.subSelections.add(sel);
        }
    }

    /**
     * Whether the selection item is a compound selection
     * @return boolean
     */
    @Override
    public boolean isCompoundSelection(){
        return true;
    }

    /**
     * Return selection items composing a compound selection
     * @return list of selection items
     * @throws IllegalStateException if selection is not a compound
     *           selection
     */
    @Override
    public List<Selection<?>> getCompoundSelectionItems(){
        return this.subSelections;
    }

    /**
     * Returns the collection used to store any duplicate alias names found within this CompoundSelection Item
     * @return list of alias Strings.
     */
    protected List<String> getDuplicateAliasNames() {
        return this.duplicateAliasNames;
    }

    @Override
    public void findRootAndParameters(CommonAbstractCriteriaImpl criteriaQuery){
        for (Selection selection: getCompoundSelectionItems()){
            ((InternalSelection)selection).findRootAndParameters(criteriaQuery);
        }
    }

}
