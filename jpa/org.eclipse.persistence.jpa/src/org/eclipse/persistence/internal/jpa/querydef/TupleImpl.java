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

import java.io.Serializable;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import javax.persistence.criteria.Selection;

import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.queries.ReportQueryResult;

public class TupleImpl implements Tuple, Serializable{
    protected List<? super Selection<?>> selections;
    protected ReportQueryResult rqr;

    public TupleImpl(List<? super Selection<?>> selections,ReportQueryResult rqr){
        this.selections = selections;
        this.rqr = rqr;
    }

    /**
     * Get the value of the specified result element.
     * @param tupleElement  tuple result element
     * @return value of result element
     * @throws IllegalArgumentException if result element
     *         does not correspond to an element in the
     *         query result tuple
     */
    @Override
    public <X> X get(TupleElement<X> tupleElement){
        int index = this.selections.indexOf(tupleElement);
        if (index==-1) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                    "jpa_criteriaapi_no_corresponding_element_in_result", new Object[]{tupleElement}));
        }
        return (X) this.get(index);
    }

    /**
     * Get the value of the tuple result element to which the
     * specified alias has been assigned.
     * @param alias  alias assigned to result element
     * @return type of the result element
     * @throws IllegalArgumentException if alias
     *         does not correspond to an element in the
     *         query tuple result or type is incorrect
     */
    @Override
    public <X> X get(String alias, Class<X> type){
        Object result = this.get(alias);
        if (type==null || !(result==null || type.isInstance(result))) {
            throw new IllegalArgumentException( ExceptionLocalization.buildMessage(
                    "jpa_criteriaapi_invalid_result_type", new Object[]{alias, type, result}));
        }
        return (X) result;
    }

    /**
     * Get the value of the tuple element to which the
     * specified alias has been assigned.
     * @param alias  alias assigned to tuple element
     * @return value of the tuple element
     * @throws IllegalArgumentException if alias
     *         does not correspond to an element in the
     *         query result tuple
     */
    @Override
    public Object get(String alias){
        //don't use the ReportQueryResult's get(string) since it returns null when the name is invalid
        int index = this.rqr.getNames().indexOf(alias);
        if (index == -1) {
            throw new IllegalArgumentException( ExceptionLocalization.buildMessage(
                    "jpa_criteriaapi_no_corresponding_element_in_result", new Object[]{alias}));
        }

        return get(index);
    }

    /**
     * Get the value of the element at the specified
     * position in the result tuple. The first position is 0.
     * @param i  position in result tuple
     * @param type  type of the result element
     * @return value of the result element
     * @throws IllegalArgumentException if i exceeds
     *         length of result tuple or type is incorrect
     */
    @Override
    public <X> X get(int i, Class<X> type){
        Object result = this.get(i);
        if (type==null || !(result==null || type.isInstance(result))) {
            throw new IllegalArgumentException( ExceptionLocalization.buildMessage(
                    "jpa_criteriaapi_invalid_result_type", new Object[]{i, type, result}));
        }
        return (X) result;
    }


    /**
     * Get the value of the element at the specified
     * position in the result tuple. The first position is 0.
     * @param i  position in result tuple
     * @return value of the result element
     * @throws IllegalArgumentException if i exceeds
     *         length of result list
     */
    @Override
    public Object get(int i){
        if (i<0 || (i >= this.rqr.getResults().size()) ) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                    "jpa_criteriaapi_invalid_result_index", new Object[]{i, this.rqr.getResults().size()}));
        }
        return this.rqr.getByIndex(i);
    }

    /**
     * Return the values of the result tuple as an array.
     * @return result element values
     */
    @Override
    public Object[] toArray(){
        return this.rqr.getResults().toArray();
    }

    /**
     * Return the elements of the tuple
     */
    @Override
    public List<TupleElement<?>> getElements(){
        return (List<TupleElement<?>>) this.selections;
    }

}
