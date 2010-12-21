package org.eclipse.persistence.internal.jpa.querydef;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import javax.persistence.criteria.Selection;

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
     * @param resultElement  tuple result element
     * @return value of result element
     * @throws IllegalArgument exception if result element
     *         does not correspond to an element in the
     *         query result tuple
     */
    public <X> X get(TupleElement<X> tupleElement){
        return (X) this.get(this.selections.indexOf(tupleElement));
    }

    /**
     * Get the value of the tuple result element to which the
     * specified alias has been assigned.
     * @param alias  alias assigned to result element
     * @return type of the result element
     * @throws IllegalArgument exception if alias
     *         does not correspond to an element in the
     *         query tuple result or type is incorrect
     */
    public <X> X get(String alias, Class<X> type){
        return (X) this.get(alias);
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
    public Object get(String alias){
        return this.rqr.get(alias);
    }

    /**
     * Get the value of the element at the specified
     * position in the result tuple. The first position is 0.
     * @param i  position in result tuple
     * @param type  type of the result element
     * @return value of the result element
     * @throws IllegalArgument exception if i exceeds
     *         length of result tuple or type is incorrect
     */
    public <X> X get(int i, Class<X> type){
        return (X) this.get(i);
    }
    

    /**
     * Get the value of the element at the specified
     * position in the result tuple. The first position is 0.
     * @param i  position in result tuple
     * @return value of the result element
     * @throws IllegalArgument exception if i exceeds
     *         length of result list
     */
    public Object get(int i){
        return this.rqr.getByIndex(i);
    }

    /**
     * Return the values of the result tuple as an array.
     * @return result element values
     */
    public Object[] toArray(){
        return this.rqr.getResults().toArray();
    }

    /**
     * Return the elements of the tuple
     */
    public List<TupleElement<?>> getElements(){
        return (List<TupleElement<?>>) this.selections;
    }

}
