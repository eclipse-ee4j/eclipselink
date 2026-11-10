/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
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
//     New Jakarta Persistence 4.0 Features
package org.eclipse.persistence.internal.jpa;

import jakarta.persistence.Parameter;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.queries.StoredFunctionCall;

/**
 * The return value of a stored function, presented as a {@link Parameter} so that it can be handed to
 * {@link StoredProcedureQuery#getOutputParameterValue(Parameter)}.
 * <p>
 * Created by {@link StoredProcedureQuery#registerResultParameter(Class)}, which is how a caller says
 * "this is a function, not a procedure, and it returns this type". There is no other way to say it:
 * {@code createStoredProcedureQuery} always builds a procedure call, and the specification has no
 * separate factory method for functions.
 * <p>
 * A function is invoked through the JDBC escape syntax
 * <pre>{@code {? = call my_function(?, ?)}}</pre>
 * where the leading placeholder receives the return value. That is why this class needs neither a name
 * nor a position from its caller: a return value has no name, and its position is fixed at 1 by the
 * syntax itself. The arguments consequently begin at JDBC position 2.
 * <p>
 * <strong>This is deliberately not a {@code ParameterExpressionImpl}</strong>, even though that is
 * EclipseLink's usual {@code Parameter}. That class is a Criteria expression: it extends
 * {@code ExpressionImpl} and carries an {@code ExpressionBuilder} node used to build query trees. A
 * function return participates in none of that.
 * <p>
 * <strong>Nor is it registered in {@code getInternalParameters()}</strong>, for two reasons. It is not
 * one of the parameters the caller registered, so it has no business in {@code getParameters()} - the
 * reference implementation likewise keeps its function return out of the registered parameter list.
 * And it could not be stored there safely in any case: that map is keyed by field name, where a
 * positional parameter's name is its position, so a return value at position 1 would collide with an
 * argument the caller registered at position 1.
 *
 * @param <T> the type returned by the stored function
 */
public class FunctionReturnImpl<T> implements Parameter<T> {

    /**
     * The JDBC position of a function's return value. Always 1: it is the placeholder to the left of
     * the {@code =} in <code>{? = call f(...)}</code>, so nothing else can occupy that slot.
     */
    private static final int POSITION = 1;

    private final StoredFunctionCall call;
    private final Class<T> resultType;

    @SuppressWarnings("unchecked")
    FunctionReturnImpl(StoredFunctionCall call) {
        this.call = call;

        if (call.getParameters().isEmpty()) {
            throw new IllegalStateException("Passed in call should have parameters");
        }

        if (!(call.getParameters().get(0) instanceof DatabaseField field)) {
            throw new IllegalStateException("1st parameter should be of type DatabaseField");
        }


        this.resultType = (Class<T>) field.getType();
    }

    /**
     * @param call the function call whose return value this represents
     * @param resultType the type the function returns, as registered by the caller
     */
    FunctionReturnImpl(StoredFunctionCall call, Class<T> resultType) {
        this.call = call;
        this.resultType = resultType;
    }

    /**
     * Always {@code null}. A function's return value is not a named argument of the procedure, so
     * there is no name by which the database could identify it.
     */
    @Override
    public String getName() {
        return null;
    }

    /**
     * Always {@code 1}, the position of the return placeholder in
     * <code>{? = call f(...)}</code>.
     */
    @Override
    public Integer getPosition() {
        return POSITION;
    }

    @Override
    public Class<T> getParameterType() {
        return resultType;
    }

    /**
     * The mode of a function return, which is always {@link ParameterMode#OUT} - the database writes
     * the value and the caller reads it back after execution.
     * <p>
     * Not part of {@link Parameter}, which exposes only name, position and type. It is offered here
     * because a function return is meaningfully an output parameter and callers that know they hold
     * one should not have to assume it.
     */
    public ParameterMode getMode() {
        return ParameterMode.OUT;
    }

    /**
     * INTERNAL:
     * Return the field the function's return value is bound to, which {@link StoredFunctionCall}
     * keeps as the first of its parameters.
     */
    public DatabaseField getReturnField() {
        return (DatabaseField) call.getParameters().get(0);
    }

    @Override
    public String toString() {
        return "FunctionReturn(" + call.getProcedureName() + " -> " + resultType.getName() + ")";
    }
}
