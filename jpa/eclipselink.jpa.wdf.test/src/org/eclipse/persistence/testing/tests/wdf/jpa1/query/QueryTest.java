/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.tests.wdf.jpa1.query;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;

public abstract class QueryTest extends JPA1Base {

    protected void assertInvalidQueryWithParameters(String queryString, Set<InputParameterHolder> parameters) {
        boolean isPassed = false;
        String message;
        EntityManager em = getEnvironment().getEntityManager();
        try {
            executeQueryWithParameters(queryString, parameters, em);
            message = "Failed to detect invalid query:'" + queryString + "'";
        } catch (IllegalArgumentException IAex) {
            isPassed = true;
            message = IAex.getMessage();
        }
        verify(isPassed, message);
    }

    /**
     * @param queryString
     * @param parameters
     * @param em
     */
    private void executeQueryWithParameters(String queryString, Set<InputParameterHolder> parameters, EntityManager em) {
        Query query = em.createQuery(queryString);
        for (InputParameterHolder holder : parameters) {

            setParameterHolder(query, holder);
        }
    }

    /**
     * @param query
     * @param holder
     */
    private void setParameterHolder(Query query, InputParameterHolder holder) {
        if (holder.isTemporal()) {
            if (holder.getType() == ParameterType.POSITIONAL) {
                if (holder.getValue() instanceof Date) {
                    query.setParameter((Integer) holder.getIdentitfier(), (Date) holder.getValue(), holder.getTemporalType());
                } else if (holder.getValue() instanceof Calendar) {
                    query.setParameter((Integer) holder.getIdentitfier(), (Calendar) holder.getValue(), holder
                            .getTemporalType());
                }
            } else if (holder.getType() == ParameterType.NAMED) {
                if (holder.getValue() instanceof Date) {
                    query.setParameter((String) holder.getIdentitfier(), (Date) holder.getValue(), holder.getTemporalType());
                } else if (holder.getValue() instanceof Calendar) {
                    query
                            .setParameter((String) holder.getIdentitfier(), (Calendar) holder.getValue(), holder
                                    .getTemporalType());
                }
            }
        } else {
            if (holder.getType() == ParameterType.POSITIONAL) {
                query.setParameter((Integer) holder.getIdentitfier(), holder.getValue());
            } else if (holder.getType() == ParameterType.NAMED) {
                query.setParameter((String) holder.getIdentitfier(), holder.getValue());
            }
        }
    }

    protected void assertValidQueryWithParameters(String queryString, Set<InputParameterHolder> parameters) {
        boolean isPassed = false;
        String message;
        EntityManager em = getEnvironment().getEntityManager();
        try {
            executeQueryWithParameters(queryString, parameters, em);
            message = "Test successfully passed.";
            isPassed = true;
        } catch (IllegalArgumentException IAex) {
            message = IAex.getMessage();
        }
        verify(isPassed, message);
    }

    protected void assertParameterValid(Query query, InputParameterHolder holder) {
        setParameterHolder(query, holder);
    }

    protected void assertParameterInvalid(Query query, InputParameterHolder holder) {
        boolean passed = false;
        try {
            setParameterHolder(query, holder);
        } catch (IllegalArgumentException e) {
            passed = true;
        }
        verify(passed, ("missing IllegalArgumentException"));
    }

    protected void assertValidUpdateExecution(String queryString) {
        boolean isPassed = false;
        String message;
        EntityManager em = getEnvironment().getEntityManager();
        try {
            getEnvironment().beginTransaction(em);
            Query q = em.createQuery(queryString);
            q.executeUpdate();
            message = "Query '" + queryString + "' was successfully created.";
            isPassed = true;
            getEnvironment().commitTransactionAndClear(em);
        } catch (Exception ex) {
            getEnvironment().rollbackTransactionAndClear(em);
            message = ex.getMessage();
        }
        verify(isPassed, message);
    }

    protected void assertInvalidUpdateExecution(String queryString) {
        boolean isPassed = false;
        String message;
        EntityManager em = getEnvironment().getEntityManager();
        try {
            getEnvironment().beginTransaction(em);
            Query q = em.createQuery(queryString);
            q.executeUpdate();
            message = "Failed to detect invalid query:'" + queryString + "'";
            getEnvironment().commitTransactionAndClear(em);
        } catch (Exception ex) {
            isPassed = true;
            message = ex.getMessage();
            getEnvironment().rollbackTransactionAndClear(em);
        }
        verify(isPassed, message);
    }

    protected void assertValidQuery(String queryString) {
        EntityManager em = getEnvironment().getEntityManager();
        try {
            em.createQuery(queryString);
        } catch (RuntimeException e) {
            throw new RuntimeException("An exception occurred while creating a query for String >>" + queryString + "<<", e);
        }
    }

    protected void assertInvalidQuery(String queryString) {
        boolean isPassed = false;
        String message;
        EntityManager em = getEnvironment().getEntityManager();
        try {
            Query query = em.createQuery(queryString);
            try {
                query.getResultList();

                message = "Failed to detect invalid query:'" + queryString + "'";
            } catch (PersistenceException ex) {
                isPassed = true;
                message = ex.getMessage();
            } catch (DatabaseException ex) { // FIXME
                isPassed = true;
                message = ex.getMessage();
            }
        } catch (IllegalArgumentException IAex) {
            isPassed = true;
            message = IAex.getMessage();
        } catch (QueryException ex) { // FIXME
            isPassed = true;
            message = ex.getMessage();
        }
        verify(isPassed, message);
    }

    protected void assertInvalidQueryExecution(String queryString) {
        boolean isPassed = false;
        String message;
        EntityManager em = getEnvironment().getEntityManager();
        try {
            Query q = em.createQuery(queryString);
            q.getResultList();
            message = "Failed to detect invalid query:'" + queryString + "'";
        } catch (Exception ex) {
            isPassed = true;
            message = ex.getMessage();
        }
        verify(isPassed, message);
    }

    protected void assertValidQueryExecution(String queryString) {
        assertValidQueryExecutionWithArgs(queryString, new Object[0]);
    }

    private boolean isUtilDateLike(Object arg) {
        return arg instanceof java.util.Date || arg instanceof java.util.Calendar;
    }

    protected void assertValidQueryExecutionWithArgs(String queryString, Object... args) {
        EntityManager em = getEnvironment().getEntityManager();

        Query q = em.createQuery(queryString);

        // setting arguments
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if (!isUtilDateLike(args[i])) {
                    q.setParameter(i + 1, args[i]);
                } else {
                    throw new IllegalArgumentException(
                            "Currently, for this test-method the use of date-like input parameters is not supported");
                }
            }
        }
        q.getResultList();
    }

    protected enum ParameterType {
        NAMED, POSITIONAL
    }

    protected class InputParameterHolder {
        private boolean isTemporal = false;
        private ParameterType type;
        private final Object identitfier;
        private final Object value;
        private TemporalType temporalType;

        public InputParameterHolder(Object identifier, Object value, TemporalType temporalType) {
            this(identifier, value);
            this.temporalType = temporalType;
            isTemporal = true;
        }

        public InputParameterHolder(Object identifier, Object value) {
            this.identitfier = identifier;
            this.value = value;

            if (identifier instanceof String) {
                type = ParameterType.NAMED;
            } else if (identifier instanceof Integer) {
                type = ParameterType.POSITIONAL;
            } else {
                throw new IllegalArgumentException("InputParameterHolder was created with an illegal identifier: " + identifier
                        + ". Only String and Integer are allowed.");
            }
        }

        public Object getIdentitfier() {
            return identitfier;
        }

        public Object getValue() {
            return value;
        }

        public TemporalType getTemporalType() {
            return temporalType;
        }

        public ParameterType getType() {
            return type;
        }

        public boolean isTemporal() {
            return isTemporal;
        }
    }
}
