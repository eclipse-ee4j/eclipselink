package org.eclipse.persistence.internal.jpa.querydef;

import jakarta.persistence.criteria.CriteriaSelect;
import org.eclipse.persistence.queries.DatabaseQuery;

/**
 * Internal interface to access CriteriaQuery result type.
 *
 * @param <T> the type of the result
 */
public interface CriteriaSelectInternal<T> extends CriteriaSelect<T> {

    /**
     * Get the type of the result.
     *
     * @return the type of the result
     */
    Class<T> getResultType();

    /**
     * Translates from the criteria query to a EclipseLink Database Query.
     */
    DatabaseQuery translate();

}
