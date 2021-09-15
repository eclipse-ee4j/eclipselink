/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
//      tware - updates for JPA 2.0 specification
package org.eclipse.persistence.internal.jpa.parsing;

import java.util.List;

/**
 * INTERNAL
 * <p><b>Purpose</b>: This interface specifies methods to create parse trees
 * and parse tree nodes.
 * <p><b>Responsibilities</b>:
 * <ul>
 * <li> Used by the EJBQLParser to create an internal representation of an
 * EJBQL query.</li>
 * <li> Abstract from concrete parse tree and parse tree node implementation
 * classes.</li>
 * <li> The parse is created in a bottom-up fashion. All methods takes any
 * child nodes for the parse tree node to be created as arguments. It is the
 * responsibility of the new&lt;XXX&gt; method to set the parent-child relationship
 * between the returned node any any of the child nodes passed as arguments.</li>
 * </ul>
 */
public interface NodeFactory {

    /** Trim specification constants. */
    enum TrimSpecification { LEADING, TRAILING, BOTH }

    // ------------------------------------------
    // Trees
    // ------------------------------------------

    /** */
    Object newSelectStatement(int line, int column,
                              Object select, Object from,
                              Object where, Object groupBy,
                              Object having, Object orderBy);

    /** */
    Object newUpdateStatement(int line, int column,
                              Object update, Object set, Object where);

    /** */
    Object newDeleteStatement(int line, int column,
                              Object delete, Object where);

    // ------------------------------------------
    // Top level nodes
    // ------------------------------------------

    /** */
    Object newSelectClause(int line, int column,
                           boolean distinct, List selectExprs);

    Object newSelectClause(int line, int column, boolean distinct, List selectExprs, List identifiers);

    /** */
    Object newFromClause(int line, int column, List varDecls);

    /** */
    Object newWhereClause(int line, int column, Object condition);

    /** */
    Object newGroupByClause(int line, int column, List items);

    /** */
    Object newHavingClause(int line, int column, Object arg);

    /** */
    Object newOrderByClause(int line, int column, List items);

    /** */
    Object newUpdateClause(int line, int column,
                           String schema, String variable);

    /** */
    Object newDeleteClause(int line, int column,
                           String schema, String variable);

    // ------------------------------------------
    // Variable declaration nodes
    // ------------------------------------------

    /** */
    Object newRangeVariableDecl(int line, int column,
                                String schema, String variable);

    /** */
    Object newJoinVariableDecl(int line, int column, boolean outer,
                               Object path, String variable, Object downcast);

    /** */
    Object newFetchJoin(int line, int column, boolean outer, Object path);

    /** */
    Object newCollectionMemberVariableDecl(int line, int column,
                                           Object path, String variable);

    /** */
    Object newVariableDecl(int line, int column,
                           Object path, String variable);

    // ------------------------------------------
    // Identifier and path expression nodes
    // ------------------------------------------

    /** */
    Object newDot(int line, int column, Object left, Object right);

    /** */
    Object newVariableAccessOrTypeConstant(int line, int column, String identifier);

    /** */
    Object newAttribute(int line, int column, String identifier);

    /** */
    Object newQualifiedAttribute(int line, int column,
                                 String variable, String attribute);

    // ------------------------------------------
    // Aggregate nodes
    // ------------------------------------------

    /** */
    Object newAvg(int line, int column, boolean ditinct, Object arg);

    /** */
    Object newMax(int line, int column, boolean ditinct, Object arg);

    /** */
    Object newMin(int line, int column, boolean ditinct, Object arg);

    /** */
    Object newSum(int line, int column, boolean ditinct, Object arg);

    /** */
    Object newCount(int line, int column, boolean ditinct, Object arg);

    // ------------------------------------------
    // Binary expression nodes
    // ------------------------------------------

    /** */
    Object newOr(int line, int column, Object left, Object right);

    /** */
    Object newAnd(int line, int column, Object left, Object right);

    /** */
    Object newEquals(int line, int column, Object left, Object right);

    /** */
    Object newNotEquals(int line, int column, Object left, Object right);

    /** */
    Object newGreaterThan(int line, int column, Object left, Object right);

    /** */
    Object newGreaterThanEqual(int line, int column,
                               Object left, Object right);

    /** */
    Object newLessThan(int line, int column, Object left, Object right);

    /** */
    Object newLessThanEqual(int line, int column,
                            Object left, Object right);

    /** */
    Object newPlus(int line, int column, Object left, Object right);

    /** */
    Object newMinus(int line, int column, Object left, Object right);

    /** */
    Object newMultiply(int line, int column, Object left, Object right);

    /** */
    Object newDivide(int line, int column, Object left, Object right);

    // ------------------------------------------
    // Unary expression nodes
    // ------------------------------------------

    /** */
    Object newUnaryPlus(int line, int column, Object arg);

    /** */
    Object newUnaryMinus(int line, int column, Object arg);

    /** */
    Object newNot(int line, int column, Object arg);

    // ------------------------------------------
    // Conditional expression nodes
    // ------------------------------------------

    /** */
    Object newBetween(int line, int column, boolean not, Object arg,
                      Object lower, Object upper);

    /** */
    Object newLike(int line, int column, boolean not, Object string,
                   Object pattern, Object escape) ;

    /** */
    Object newEscape(int line, int column, Object arg);

    /** */
    Object newIn(int line, int column,
                 boolean not, Object expr, List items);

    /** */
    Object newIsNull(int line, int column, boolean not, Object expr);

    /** */
    Object newIsEmpty(int line, int column, boolean not, Object expr) ;

    /** */
    Object newMemberOf(int line, int column, boolean not,
                       Object expr, Object collection);

    // ------------------------------------------
    // Parameter nodes
    // ------------------------------------------

    /** */
    Object newPositionalParameter(int line, int colimn, String position);

    /** */
    Object newNamedParameter(int line, int colimn, String name);

    // ------------------------------------------
    // Literal nodes
    // ------------------------------------------

    /** */
    Object newBooleanLiteral(int line, int column, Object value);

    /** */
    Object newIntegerLiteral(int line, int column, Object value);

    /** */
    Object newLongLiteral(int line, int column, Object value);

    /** */
    Object newFloatLiteral(int line, int column, Object value);

    /** */
    Object newDoubleLiteral(int line, int column, Object value);

    /** */
    Object newStringLiteral(int line, int column, Object value);

    /** */
    Object newNullLiteral(int line, int column);

    // ------------------------------------------
    // Objects for functions returning strings
    // ------------------------------------------

    /** */
    Object newConcat(int line, int column, List objects);

    /** */
    Object newSubstring(int line, int column,
                        Object string, Object start, Object length);

    /** */
    Object newTrim(int line, int column, TrimSpecification trimSpec,
                   Object trimChar, Object string);

    /** */
    Object newLower(int line, int column, Object arg);

    /** */
    Object newUpper(int line, int column, Object arg);

    // ------------------------------------------
    // Objects for functions returning numerics
    // ------------------------------------------

    /** */
    Object newLocate(int line, int column,
                     Object pattern, Object arg, Object startPos);

    /** */
    Object newLength(int line, int column, Object arg);

    /** */
    Object newAbs(int line, int column, Object arg);

    /** */
    Object newSqrt(int line, int column, Object arg);

    /** */
    Object newMod(int line, int column, Object left, Object right);

    /** */
    Object newSize(int line, int column, Object arg);

    // ------------------------------------------
    // Objects for functions returning datetime
    // ------------------------------------------

    /** */
    Object newCurrentDate(int line, int column);

    /** */
    Object newCurrentTime(int line, int column);

    /** */
    Object newCurrentTimestamp(int line, int column);

    // ------------------------------------------
    // Custom function
    // ------------------------------------------

    /** */
    Object newFunc(int line, int column, String name, List parameters);

    // ------------------------------------------
    // Subquery nodes
    // ------------------------------------------

    /** */
    Object newSubquery(int line, int column, Object select, Object from, Object where,
                       Object groupBy, Object having);

    /** */
    Object newExists(int line, int column, boolean not, Object subquery);

    /** */
    Object newIn(int line, int column, boolean not, Object expr, Object subquery);

    /** */
    Object newAll(int line, int column, Object subquery);

    /** */
    Object newAny(int line, int column, Object subquery);

    /** */
    Object newSome(int line, int column, Object subquery);

    // ------------------------------------------
    // Miscellaneous nodes
    // ------------------------------------------

    /** */
    Object newAscOrdering(int line, int column, Object arg);

    /** */
    Object newDescOrdering(int line, int column, Object arg);

    /** */
    Object newConstructor(int line, int colimn,
                          String className, List args);

    /** */
    Object newSetClause(int line, int colimn, List assignments);

    /** */
    Object newSetAssignmentClause(int line, int column,
                                  Object target, Object value);

    Object newKey(int line, int column, Object left);

    Object newMapEntry(int line, int column, Object arg);

    Object newType(int line, int column, Object left);

    Object newCaseClause(int line, int column, Object base, List whenClauses, Object elseClause);

    Object newCoalesceClause(int line, int column, List clauses);

    Object newNullIfClause(int line, int column, Object left, Object right);

    Object newWhenClause(int line, int column, Object conditionClause, Object theClause);

    Object newIndex(int line, int column, Object object);

    Object newDateLiteral(int line, int column, Object object);

    Object newTimeLiteral(int line, int column, Object object);

    Object newTimeStampLiteral(int line, int column, Object object);
}

