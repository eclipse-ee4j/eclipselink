package org.eclipse.persistence.internal.jpa.querydef;

import java.util.List;
import java.util.Vector;

import javax.persistence.criteria.Selection;

import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.ReportQueryResult;

/**
 * <p>
 * <b>Purpose</b>: This is a special subclass of the ReportQuery that constructs Tuple results.
 * <p>
 * <b>Description</b>: A subclass of ReportQuery this query type combines multiple selections into 
 * <p>
 * 
 * @see javax.persistence.criteria CriteriaQuery
 * 
 * @author gyorke
 * @since EclipseLink 1.2
 */
public class TupleQuery extends ReportQuery {
    
    protected List<? super Selection<?>> selections;
    public TupleQuery(List<? super Selection<?>> selections){
        super();
        this.selections = selections;
    }
    
    /**
     * INTERNAL:
     * Construct a result from a row. Either return a ReportQueryResult or just the attribute.
     */
    @Override
    public Object buildObject(AbstractRecord row, Vector toManyJoinData) {
        return new TupleImpl(this.selections, new ReportQueryResult(this, row, toManyJoinData));
    }
}
