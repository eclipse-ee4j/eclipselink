/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     tware - updates for JPA 2.0 specification
package org.eclipse.persistence.internal.jpa.parsing;

import java.util.List;

import org.eclipse.persistence.internal.jpa.parsing.TemporalLiteralNode.TemporalType;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Implements a node factory used by the EJBQLParser
 * class.
 * <p><b>Responsibilities</b>:
 * <ul>
 * <li> Create EJBQLParseTree instances for EJBQL SELECT-, UPDATE- and DELETE
 * statements (see methods newSelectStatement, newUpdateStatement and
 * newDeleteStatement).</li>
 * <li> Any new&lt;XXX&gt; method returns an instance of the appropriate Node
 * subclass.</li>
 * <li> The relationship to the child nodes passed as arguments are
 * automatically initialized.</li>
 * <li>Note, this implementation has a state managing the parse tree context
 * and a list of parameter names for the current parse tree. This state needs
 * to be initialized before the same node factory implementation instance may
 * be used to create a second parse tree (see methods initContext and
 * initParameters).</li>
 * <li> The implementation automatically adds the list of parameters to the
 * created parse tree.</li>
 * <li> The implementation automatically sets the parse tree context for any
 * created major node.</li>
 * </ul>
 */
public class NodeFactoryImpl implements NodeFactory {

    /** The parse tree context. */
    private ParseTreeContext context;

    /** */
    private String currentIdentificationVariable;

    /** No-arg Constructor */
    public NodeFactoryImpl(String queryInfo) {
        this.context = new ParseTreeContext(this, queryInfo);
    }

    // ------------------------------------------
    // Trees
    // ------------------------------------------

    /** */
    @Override
    public Object newSelectStatement(int line, int column,
                                     Object select, Object from,
                                     Object where, Object groupBy,
                                     Object having, Object orderBy) {
        QueryNode queryNode = (QueryNode)select;
        JPQLParseTree tree = new JPQLParseTree();
        queryNode.setParseTree(tree);
        tree.setContext(context);
        tree.setQueryNode(queryNode);
        tree.setFromNode((FromNode)from);
        tree.setWhereNode((WhereNode)where);
        tree.setGroupByNode((GroupByNode)groupBy);
        tree.setHavingNode((HavingNode)having);
        tree.setOrderByNode((OrderByNode)orderBy);
        return tree;
    }

    /** */
    @Override
    public Object newUpdateStatement(int line, int column,
                                     Object update, Object set, Object where) {
        QueryNode queryNode = (QueryNode)update;
        JPQLParseTree tree = new JPQLParseTree();
        queryNode.setParseTree(tree);
        tree.setContext(context);
        tree.setQueryNode(queryNode);
        tree.setSetNode((SetNode)set);
        tree.setWhereNode((WhereNode)where);
        return tree;
    }

    /** */
    @Override
    public Object newDeleteStatement(int line, int column,
                                     Object delete, Object where) {
        QueryNode queryNode = (QueryNode)delete;
        JPQLParseTree tree = new JPQLParseTree();
        queryNode.setParseTree(tree);
        tree.setContext(context);
        tree.setQueryNode(queryNode);
        tree.setWhereNode((WhereNode)where);
        return tree;
    }

    // ------------------------------------------
    // Major nodes
    // ------------------------------------------

    @Override
    public Object newSelectClause(int line, int column,
                                    boolean distinct, List selectExprs) {
        return newSelectClause(line, column, distinct, selectExprs, null);
    }

    @Override
    public Object newSelectClause(int line, int column, boolean distinct, List selectExprs, List identifiers) {
        SelectNode node = new SelectNode();
        node.setContext(context);
        node.setSelectExpressions(selectExprs);
        node.setIdentifiers(identifiers);
        if (identifiers != null){
            for (int i=0;i<identifiers.size();i++){
                if (identifiers.get(i) != null){
                    context.registerJoinVariable(calculateCanonicalName((String)identifiers.get(i)), (Node)selectExprs.get(i), line, column);
                }
            }
        }
        node.setDistinct(distinct);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newFromClause(int line, int column, List decls) {
        FromNode node = new FromNode();
        node.setContext(context);
        node.setDeclarations(decls);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newWhereClause(int line, int column, Object condition) {
        WhereNode node = new WhereNode();
        node.setContext(context);
        node.setLeft((Node)condition);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newGroupByClause(int line, int column, List items) {
        GroupByNode node = new GroupByNode();
        node.setContext(context);
        node.setGroupByItems(items);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newHavingClause(int line, int column, Object arg) {
        HavingNode node = new HavingNode();
        node.setContext(context);
        node.setHaving((Node)arg);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newOrderByClause(int line, int column, List items) {
        OrderByNode node = new OrderByNode();
        node.setContext(context);
        node.setOrderByItems(items);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newUpdateClause(int line, int column,
                                  String schema, String variable) {
        UpdateNode node = new UpdateNode();
        node.setContext(context);
        node.setAbstractSchemaName(schema);
        node.setAbstractSchemaIdentifier(variable);
        setPosition(node, line, column);
        registerSchema(calculateCanonicalName(variable), schema, line, column);
        return node;
    }

    /** */
    @Override
    public Object newDeleteClause(int line, int column,
                                  String schema, String variable) {
        DeleteNode node = new DeleteNode();
        node.setContext(context);
        node.setAbstractSchemaName(schema);
        node.setAbstractSchemaIdentifier(variable);
        setPosition(node, line, column);
        registerSchema(calculateCanonicalName(variable), schema, line, column);
        return node;
    }

    // ------------------------------------------
    // Variable declaration nodes
    // ------------------------------------------

    /** */
    @Override
    public Object newRangeVariableDecl(int line, int column,
                                       String schema, String variable) {
        RangeDeclNode node = new RangeDeclNode();
        node.setAbstractSchemaName(schema);
        node.setVariableName(variable);
        setPosition(node, line, column);
        registerSchema(node.getCanonicalVariableName(), schema, line, column);
        currentIdentificationVariable = variable;
        return node;
    }

    /** */
    @Override
    public Object newJoinVariableDecl(int line, int column, boolean outerJoin,
                                      Object path, String variable, Object downcast) {
        DotNode dotNode = (DotNode)path;
        AttributeNode rightNode = (AttributeNode)dotNode.getRight();
        rightNode.setOuterJoin(outerJoin);
        if (downcast != null){
            AttributeNode rightMostNode = (AttributeNode)dotNode.getRightMostNode();
            rightMostNode.setCastClassName((String)downcast);
        }
        JoinDeclNode node = new JoinDeclNode();
        node.setPath(dotNode);
        node.setVariableName(variable);
        node.setOuterJoin(outerJoin);
        setPosition(node, line, column);
        context.registerJoinVariable(node.getCanonicalVariableName(), dotNode, line, column);
        currentIdentificationVariable = variable;
        return node;
    }

    /** */
    @Override
    public Object newFetchJoin(int line, int column,
                               boolean outerJoin, Object path) {
        DotNode dotNode = (DotNode)path;
        AttributeNode rightNode = (AttributeNode)dotNode.getRight();
        rightNode.setOuterJoin(outerJoin);
        // register the dot expression to be added as joined attribute
        FetchJoinNode node = new FetchJoinNode();
        node.setPath(dotNode);
        node.setOuterJoin(outerJoin);
        setPosition(node, line, column);
        context.registerFetchJoin(currentIdentificationVariable, dotNode);
        return node;
    }

    /** */
    @Override
    public Object newCollectionMemberVariableDecl(int line, int column,
                                                  Object path, String variable) {
        DotNode dotNode = (DotNode)path;
        AttributeNode rightNode = (AttributeNode)dotNode.getRight();
        // The IN-clause expression must be a collection valued path expression
        rightNode.setRequiresCollectionAttribute(true);
        CollectionMemberDeclNode node = new CollectionMemberDeclNode();
        node.setPath(dotNode);
        node.setVariableName(variable);
        setPosition(node, line, column);
        context.registerJoinVariable(node.getCanonicalVariableName(), dotNode, line, column);
        currentIdentificationVariable = variable;
        return node;
    }

    /** */
    @Override
    public Object newVariableDecl(int line, int column,
                                  Object path, String variable) {
        DotNode dotNode = (DotNode)path;
        JoinDeclNode node = new JoinDeclNode();
        node.setPath(dotNode);
        node.setVariableName(variable);
        setPosition(node, line, column);
        context.registerJoinVariable(node.getCanonicalVariableName(), dotNode, line, column);
        currentIdentificationVariable = variable;
        return node;
    }

    // ------------------------------------------
    // Identifier and path expression nodes
    // ------------------------------------------

    /** */
    @Override
    public Object newDot(int line, int column, Object left, Object right) {
        DotNode node = new DotNode();
        node.setLeft((Node)left);
        node.setRight((Node)right);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newVariableAccessOrTypeConstant(int line, int column, String identifier) {
        VariableNode node = new VariableNode(identifier);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newAttribute(int line, int column, String identifier) {
        AttributeNode node = new AttributeNode(identifier);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newQualifiedAttribute(int line, int column,
                                        String variable, String attribute) {
        Object varNode = newVariableAccessOrTypeConstant(line, column, variable);
        Object attrNode = newAttribute(line, column, attribute);
        return newDot(line, column, varNode, attrNode);
    }

    // ------------------------------------------
    // Aggregate nodes
    // ------------------------------------------

    /** */
    @Override
    public Object newAvg(int line, int column, boolean distinct, Object arg) {
        AvgNode node = new AvgNode();
        node.setLeft((Node)arg);
        node.setDistinct(distinct);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newMax(int line, int column, boolean distinct, Object arg) {
        MaxNode node = new MaxNode();
        node.setLeft((Node)arg);
        node.setDistinct(distinct);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newMin(int line, int column, boolean distinct, Object arg) {
        MinNode node = new MinNode();
        node.setLeft((Node)arg);
        node.setDistinct(distinct);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newSum(int line, int column, boolean distinct, Object arg) {
        SumNode node = new SumNode();
        node.setLeft((Node)arg);
        node.setDistinct(distinct);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newCount(int line, int column, boolean distinct, Object arg) {
        CountNode node = new CountNode();
        node.setLeft((Node)arg);
        node.setDistinct(distinct);
        setPosition(node, line, column);
        return node;
    }

    // ------------------------------------------
    // Binary expression nodes
    // ------------------------------------------

    /** */
    @Override
    public Object newOr(int line, int column, Object left, Object right) {
        OrNode node = new OrNode();
        node.setLeft((Node)left);
        node.setRight((Node)right);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newAnd(int line, int column, Object left, Object right) {
        AndNode node = new AndNode();
        node.setLeft((Node)left);
        node.setRight((Node)right);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newEquals(int line, int column, Object left, Object right) {
        EqualsNode node = new EqualsNode();
        node.setLeft((Node)left);
        node.setRight((Node)right);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newNotEquals(int line, int column, Object left, Object right) {
        NotEqualsNode node = new NotEqualsNode();
        node.setLeft((Node)left);
        node.setRight((Node)right);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newGreaterThan(int line, int column,
                                 Object left, Object right) {
        GreaterThanNode node = new GreaterThanNode();
        node.setLeft((Node)left);
        node.setRight((Node)right);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newGreaterThanEqual(int line, int column,
                                      Object left, Object right) {
        GreaterThanEqualToNode node = new GreaterThanEqualToNode();
        node.setLeft((Node)left);
        node.setRight((Node)right);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newLessThan(int line, int column, Object left, Object right) {
        LessThanNode node = new LessThanNode();
        node.setLeft((Node)left);
        node.setRight((Node)right);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newLessThanEqual(int line, int column,
                                   Object left, Object right) {
        LessThanEqualToNode node = new LessThanEqualToNode();
        node.setLeft((Node)left);
        node.setRight((Node)right);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newPlus(int line, int column, Object left, Object right) {
        PlusNode node = new PlusNode();
        node.setLeft((Node)left);
        node.setRight((Node)right);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newMinus(int line, int column, Object left, Object right) {
        MinusNode node = new MinusNode();
        node.setLeft((Node)left);
        node.setRight((Node)right);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newMultiply(int line, int column, Object left, Object right) {
        MultiplyNode node = new MultiplyNode();
        node.setLeft((Node)left);
        node.setRight((Node)right);
        setPosition(node, line, column);
        return node;
    }

    // ------------------------------------------
    // Conditional expression nodes
    // ------------------------------------------

    /** */
    @Override
    public Object newBetween(int line, int column, boolean not, Object arg,
                             Object lower, Object upper) {
        BetweenNode node = new BetweenNode();
        node.setLeft((Node)arg);
        node.setRightForBetween((Node)lower);
        node.setRightForAnd((Node)upper);
        setPosition(node, line, column);
        return not? newNot(line, column, node) : node;
    }

    /** */
    @Override
    public Object newDivide(int line, int column, Object left, Object right) {
        DivideNode node = new DivideNode();
        node.setLeft((Node)left);
        node.setRight((Node)right);
        setPosition(node, line, column);
        return node;
    }

    // ------------------------------------------
    // Unary expression nodes
    // ------------------------------------------

    /** */
    @Override
    public Object newUnaryPlus(int line, int column, Object arg) {
        return arg;
    }

    /** */
    @Override
    public Object newUnaryMinus(int line, int column, Object arg) {
        UnaryMinus node = new UnaryMinus();
        node.setLeft((Node)arg);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newNot(int line, int column, Object arg) {
        NotNode node = new NotNode();
        node.setLeft((Node)arg);
        setPosition(node, line, column);
        return node;
    }

    // ------------------------------------------
    // Conditional expression nodes
    // ------------------------------------------

    /** */
    @Override
    public Object newLike(int line, int column, boolean not, Object string,
                          Object pattern, Object escape)  {
        LikeNode node = new LikeNode();
        node.setLeft((Node)string);
        node.setRight((Node)pattern);
        node.setEscapeNode((EscapeNode)escape);
        setPosition(node, line, column);
        return not ? newNot(line, column, node) : node;
    }

    /** */
    @Override
    public Object newEscape(int line, int column, Object arg) {
        EscapeNode node = new EscapeNode();
        node.setLeft((Node)arg);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newIn(int line, int column,
                        boolean not, Object expr, List items) {
        InNode node = new InNode();
        if (not) node.indicateNot();
        node.setLeft((Node)expr);
        node.setTheObjects(items);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newIsNull(int line, int column, boolean not, Object expr) {
        NullComparisonNode node = new NullComparisonNode();
        node.setLeft((Node)expr);
        setPosition(node, line, column);
        return not ? newNot(line, column, node) : node;
    }

    /** */
    @Override
    public Object newIsEmpty(int line, int column, boolean not, Object expr)  {
        EmptyCollectionComparisonNode node =
            new EmptyCollectionComparisonNode();
        node.setLeft((Node)expr);
        if (not) node.indicateNot();
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newMemberOf(int line, int column,
                              boolean not, Object expr, Object collection)  {
        MemberOfNode node = new MemberOfNode();
        node.setLeft((Node)expr);
        node.setRight((Node)collection);
        if (not) node.indicateNot();
        setPosition(node, line, column);
        return node;
    }

    // ------------------------------------------
    // Parameter nodes
    // ------------------------------------------

    /** */
    @Override
    public Object newPositionalParameter(int line, int column, String position) {
        ParameterNode node = new ParameterNode(position);
        context.addParameter(position);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newNamedParameter(int line, int column, String name) {
        ParameterNode node = new ParameterNode(name);
        context.addParameter(name);
        setPosition(node, line, column);
        return node;
    }

    // ------------------------------------------
    // Literal nodes
    // ------------------------------------------

    /** */
    @Override
    public Object newBooleanLiteral(int line, int column, Object value) {
        BooleanLiteralNode node = new BooleanLiteralNode();
        node.setLiteral(value);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newIntegerLiteral(int line, int column, Object value) {
        IntegerLiteralNode node = new IntegerLiteralNode();
        node.setLiteral(value);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newLongLiteral(int line, int column, Object value) {
        LongLiteralNode node = new LongLiteralNode();
        node.setLiteral(value);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newFloatLiteral(int line, int column, Object value) {
        FloatLiteralNode node = new FloatLiteralNode();
        node.setLiteral(value);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newDoubleLiteral(int line, int column, Object value) {
        DoubleLiteralNode node = new DoubleLiteralNode();
        node.setLiteral(value);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newStringLiteral(int line, int column, Object value) {
        StringLiteralNode node = new StringLiteralNode();
        node.setLiteral(value);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newNullLiteral(int line, int column) {
        LiteralNode node = new LiteralNode();
        node.setLiteral(null);
        setPosition(node, line, column);
        return node;
    }

    // ------------------------------------------
    // Objects for functions returning strings
    // ------------------------------------------

    /** */
    @Override
    public Object newConcat(int line, int column, List objects) {
        ConcatNode node = new ConcatNode();
        node.setObjects(objects);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newSubstring(int line, int column,
                               Object string, Object start, Object length) {
        SubstringNode node = new SubstringNode();
        node.setLeft((Node)string);
        node.setStartPosition((Node)start);
        node.setStringLength((Node)length);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newTrim(int line, int column, TrimSpecification trimSpec,
                          Object trimChar, Object string) {
        TrimNode node = new TrimNode();
        node.setLeft((Node)string);
        node.setTrimChar((Node)trimChar);
        switch (trimSpec) {
        case LEADING:
            node.setLeading(true);
            break;
        case TRAILING:
            node.setTrailing(true);
            break;
        case BOTH:
            node.setBoth(true);
            break;
        }
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newLower(int line, int column, Object arg) {
        LowerNode node = new LowerNode();
        node.setLeft((Node)arg);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newUpper(int line, int column, Object arg) {
        UpperNode node = new UpperNode();
        node.setLeft((Node)arg);
        setPosition(node, line, column);
        return node;
    }

    // ------------------------------------------
    // Objects for functions returning numerics
    // ------------------------------------------

    /** */
    @Override
    public Object newLocate(int line, int column,
                            Object pattern, Object arg, Object startPos) {
        LocateNode node = new LocateNode();
        node.setFind((Node)pattern);
        node.setFindIn((Node)arg);
        node.setStartPosition((Node)startPos);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newLength(int line, int column, Object arg) {
        LengthNode node = new LengthNode();
        node.setLeft((Node)arg);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newAbs(int line, int column, Object arg) {
        AbsNode node = new AbsNode();
        node.setLeft((Node)arg);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newSqrt(int line, int column, Object arg) {
        SqrtNode node = new SqrtNode();
        node.setLeft((Node)arg);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newMod(int line, int column, Object left, Object right) {
        ModNode node = new ModNode();
        node.setLeft((Node)left);
        node.setDenominator((Node)right);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newSize(int line, int column, Object arg) {
        SizeNode node = new SizeNode();
        node.setLeft((Node)arg);
        setPosition(node, line, column);
        return node;
    }

    // ------------------------------------------
    // Objects for functions returning datetime
    // ------------------------------------------

    /** */
    @Override
    public Object newCurrentDate(int line, int column) {
        DateFunctionNode node = new DateFunctionNode();
        node.useCurrentDate();
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newCurrentTime(int line, int column) {
        DateFunctionNode node = new DateFunctionNode();
        node.useCurrentTime();
        setPosition(node, line, column);

        return node;
    }

    /** */
    @Override
    public Object newCurrentTimestamp(int line, int column) {
        DateFunctionNode node = new DateFunctionNode();
        node.useCurrentTimestamp();
        setPosition(node, line, column);
        return node;
    }

    // ------------------------------------------
    // Custom function
    // ------------------------------------------

    /** */
    @Override
    public Object newFunc(int line, int column, String name, List parameters) {
        FuncNode node = new FuncNode();
        if(name.startsWith("'") && name.endsWith("'")) {
            name = name.substring(1, name.length()-1);
        }
        node.setName(name);
        node.setParameters(parameters);
        setPosition(node, line, column);
        return node;
    }

    // ------------------------------------------
    // Subquery nodes
    // ------------------------------------------

    /** */
    @Override
    public Object newSubquery(int line, int column,
                              Object select, Object from, Object where,
                              Object groupBy, Object having) {
        QueryNode queryNode = (QueryNode)select;
        JPQLParseTree tree = new JPQLParseTree();
        queryNode.setParseTree(tree);
        tree.setQueryNode(queryNode);
        tree.setFromNode((FromNode)from);
        tree.setWhereNode((WhereNode)where);
        tree.setGroupByNode((GroupByNode)groupBy);
        tree.setHavingNode((HavingNode)having);
        tree.setContext(context);
        SubqueryNode node = new SubqueryNode();
        node.setParseTree(tree);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newExists(int line, int column, boolean not, Object subquery) {
        ExistsNode node = new ExistsNode();
        if (not) node.indicateNot();
        node.setLeft((Node)subquery);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newIn(int line, int column,
                        boolean not, Object expr, Object subquery) {
        InNode node = new InNode();
        if (not) node.indicateNot();
        node.setLeft((Node)expr);
        node.addNodeToTheObjects((Node)subquery);
        setPosition(node, line, column);
        node.setIsListParameterOrSubquery(true);
        return node;
    }

    /** */
    @Override
    public Object newAll(int line, int column, Object subquery) {
        AllNode node = new AllNode();
        node.setLeft((Node)subquery);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newAny(int line, int column, Object subquery) {
        AnyNode node = new AnyNode();
        node.setLeft((Node)subquery);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newSome(int line, int column, Object subquery) {
        SomeNode node = new SomeNode();
        node.setLeft((Node)subquery);
        setPosition(node, line, column);
        return node;
    }

    // ------------------------------------------
    // Miscellaneous nodes
    // ------------------------------------------

    /** */
    @Override
    public Object newAscOrdering(int line, int column, Object arg) {
        OrderByItemNode node = new OrderByItemNode();
        SortDirectionNode sortDirection = new SortDirectionNode();
        sortDirection.useAscending();
        node.setDirection(sortDirection);
        node.setOrderByItem(arg);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newDescOrdering(int line, int column, Object arg) {
        OrderByItemNode node = new OrderByItemNode();
        SortDirectionNode sortDirection = new SortDirectionNode();
        sortDirection.useDescending();
        node.setDirection(sortDirection);
        node.setOrderByItem(arg);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newConstructor(int line, int column,
                                 String className, List args) {
        ConstructorNode node = new ConstructorNode(className);
        node.setConstructorItems(args);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newSetClause(int line, int column, List assignments) {
        SetNode node = new SetNode();
        node.setAssignmentNodes(assignments);
        setPosition(node, line, column);
        return node;
    }

    /** */
    @Override
    public Object newSetAssignmentClause(int line, int column,
                                         Object target, Object value) {
        EqualsAssignmentNode node = new EqualsAssignmentNode();
        node.setLeft((Node)target);
        node.setRight((Node)value);
        return node;
    }

    // ------------------------------------------
    // Helper methods
    // ------------------------------------------

    /** */
    private void setPosition(Node node, int line, int column) {
        node.setLine(line);
        node.setColumn(column);
    }

    /** */
    private void registerSchema(String variable, String schema, int line, int column) {
        if (variable != null) {
            context.registerSchema(variable, schema, line, column);
        }
        else {
            // UPDATE and DELETE may not define a variable =>
            // use schema name as variable
            context.registerSchema(calculateCanonicalName(schema), schema, line, column);
        }
    }

    /** */
    private String calculateCanonicalName(String name) {
        return (name == null) ? null :
            IdentificationVariableDeclNode.calculateCanonicalName(name);
    }

    @Override
    public Object newKey(int line, int column, Object left){
        MapKeyNode node = new MapKeyNode();
        node.setLeft((Node)left);
        setPosition(node, line, column);
        return node;
    }

    @Override
    public Object newMapEntry(int line, int column, Object arg){
        MapEntryNode node = new MapEntryNode();
        node.setLeft((Node)arg);
        setPosition(node, line, column);
        return node;
    }

    @Override
    public Object newType(int line, int column, Object left){
        ClassForInheritanceNode node = new ClassForInheritanceNode();
        node.setLeft((Node)left);
        setPosition(node, line, column);
        return node;
    }

    @Override
    public Object newCaseClause(int line, int column, Object base, List whenClauses, Object elseClause){
        CaseNode node = new CaseNode();
        node.setWhenClauses(whenClauses);
        if (base != null){
            node.setLeft((Node)base);
        }
        node.setRight((Node)elseClause);
        setPosition(node, line, column);
        return node;
    }

    @Override
    public Object newCoalesceClause(int line, int column, List clauses){
        CoalesceNode node = new CoalesceNode();
        node.setClauses(clauses);
        setPosition(node, line, column);
        return node;
    }

    @Override
    public Object newNullIfClause(int line, int column, Object left, Object right){
        NullIfNode node = new NullIfNode();
        node.setLeft((Node)left);
        node.setRight((Node)right);
        setPosition(node, line, column);
        return node;
    }

    @Override
    public Object newWhenClause(int line, int column, Object conditionClause, Object thenClause){
        WhenThenNode node = new WhenThenNode();
        node.setLeft((Node)conditionClause);
        node.setRight((Node)thenClause);
        setPosition(node, line, column);
        return node;
    }

    @Override
    public Object newIndex(int line, int column, Object object){
        IndexNode node = new IndexNode();
        node.setLeft((Node)object);
        setPosition(node, line, column);
        return node;
    }

    @Override
    public Object newDateLiteral(int line, int column, Object value){
        TemporalLiteralNode node = new TemporalLiteralNode(TemporalType.DATE);
        node.setLiteral(value);
        setPosition(node, line, column);
        return node;
    }

    @Override
    public Object newTimeLiteral(int line, int column, Object value){
        TemporalLiteralNode node = new TemporalLiteralNode(TemporalType.TIME);
        node.setLiteral(value);
        setPosition(node, line, column);
        return node;
    }

    @Override
    public Object newTimeStampLiteral(int line, int column, Object value){
        TemporalLiteralNode node = new TemporalLiteralNode(TemporalType.TIMESTAMP);
        node.setLiteral(value);
        setPosition(node, line, column);
        return node;
    }

}

