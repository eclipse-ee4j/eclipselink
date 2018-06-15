/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.tools.resolver;

import static org.eclipse.persistence.jpa.jpql.LiteralType.PATH_EXPRESSION_LAST_PATH;
import static org.eclipse.persistence.jpa.jpql.LiteralType.RESULT_VARIABLE;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.parser.AbsExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractEclipseLinkExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.AdditionExpression;
import org.eclipse.persistence.jpa.jpql.parser.AvgFunction;
import org.eclipse.persistence.jpa.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.jpql.parser.ConcatExpression;
import org.eclipse.persistence.jpa.jpql.parser.CountFunction;
import org.eclipse.persistence.jpa.jpql.parser.DivisionExpression;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.IndexExpression;
import org.eclipse.persistence.jpa.jpql.parser.LengthExpression;
import org.eclipse.persistence.jpa.jpql.parser.LocateExpression;
import org.eclipse.persistence.jpa.jpql.parser.LowerExpression;
import org.eclipse.persistence.jpa.jpql.parser.MaxFunction;
import org.eclipse.persistence.jpa.jpql.parser.MinFunction;
import org.eclipse.persistence.jpa.jpql.parser.ResultVariable;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.SizeExpression;
import org.eclipse.persistence.jpa.jpql.parser.SqrtExpression;
import org.eclipse.persistence.jpa.jpql.parser.StateFieldPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.SubstringExpression;
import org.eclipse.persistence.jpa.jpql.parser.SubtractionExpression;
import org.eclipse.persistence.jpa.jpql.parser.SumFunction;
import org.eclipse.persistence.jpa.jpql.parser.TrimExpression;
import org.eclipse.persistence.jpa.jpql.parser.UpperExpression;
import org.eclipse.persistence.jpa.jpql.tools.JPQLQueryContext;
import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedTypeVisitor;
import org.eclipse.persistence.jpa.jpql.tools.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.tools.spi.IMappingType;
import org.eclipse.persistence.jpa.jpql.tools.spi.IType;
import org.eclipse.persistence.jpa.jpql.tools.spi.ITypeDeclaration;
import org.eclipse.persistence.jpa.jpql.tools.utility.iterable.SnapshotCloneIterable;

/**
 * This {@link Resolver} wraps a subquery that is used as the "root" object in a query's declaration.
 * <p>
 * Example:
 *
 * <pre><code>   SELECT e.firstName
 * FROM Employee e, (SELECT count(e2), e2.firstName FROM Employee e2) e3
 * WHERE e.firstName = e3.firstName</code></pre>
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
public class FromSubqueryResolver extends Resolver {

    /**
     * The virtual {@link IManagedType} representing the subquery.
     */
    private IManagedType managedType;

    /**
     * The {@link JPQLQueryContext} is used to query information about the application metadata and
     * cached information.
     */
    private JPQLQueryContext queryContext;

    /**
     * The subquery being defined as a "root" object.
     */
    private SimpleSelectStatement subquery;

    /**
     * Creates a new <code>FromSubqueryResolver</code>.
     *
     * @param parent The parent of this resolver, which is never <code>null</code>
     * @param queryContext The context used to query information about the application metadata and
     * cached information
     * @param subquery
     * @exception NullPointerException If the parent is <code>null</code>
     */
    public FromSubqueryResolver(Resolver parent,
                                JPQLQueryContext queryContext,
                                SimpleSelectStatement subquery) {

        super(parent);
        this.subquery     = subquery;
        this.queryContext = queryContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IType buildType() {
        return getManagedType().getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ITypeDeclaration buildTypeDeclaration() {
        return getType().getTypeDeclaration();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IManagedType getManagedType() {
        if (managedType == null) {
            managedType = new VirtualManagedType();
        }
        return managedType;
    }

    private enum MappingType {
        PROPERTY,
        RELATIONSHIP,
        UNKNOWN
    }

    /**
     * This {@link IManagedType} represents a virtual managed type where its content will be derived
     * from the subquery.
     */
    protected class VirtualManagedType implements IManagedType {

        /**
         * The virtual {@link IMapping mappings} representing what is selected.
         */
        private Map<String, IMapping> mappings;

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(IManagedTypeVisitor visitor) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compareTo(IManagedType managedType) {
            return getType().getName().compareTo(managedType.getType().getName());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public IMapping getMappingNamed(String name) {
            initializeMappings();
            return mappings.get(name);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public IManagedTypeProvider getProvider() {
            return FromSubqueryResolver.this.getProvider();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public IType getType() {
            return getProvider().getTypeRepository().getType(IType.UNRESOLVABLE_TYPE);
        }

        private void initializeMappings() {

            if (mappings == null) {
                mappings = new HashMap<String, IMapping>();

                // Create virtual mappings that wraps the select items
                VirtualMappingBuilder builder = new VirtualMappingBuilder();
                builder.parent   = this;
                builder.mappings = mappings;

                subquery.accept(builder);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Iterable<IMapping> mappings() {
            initializeMappings();
            return new SnapshotCloneIterable<IMapping>(mappings.values());
        }
    }

    /**
     * This virtual {@link IMapping} wraps one of the select items.
     */
    protected static class VirtualMapping implements IMapping {

        private MappingType mappingType;
        private String name;
        private IManagedType parent;
        private Resolver resolver;

        protected VirtualMapping(IManagedType parent,
                                 String name,
                                 Resolver resolver,
                                 MappingType mappingType) {

            super();
            this.name        = name;
            this.parent      = parent;
            this.resolver    = resolver;
            this.mappingType = mappingType;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compareTo(IMapping mapping) {
            return getName().compareTo(mapping.getName());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getMappingType() {
            IMapping mapping = resolver.getMapping();
            return (mapping != null) ? mapping.getMappingType() : IMappingType.TRANSIENT;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getName() {
            return name;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public IManagedType getParent() {
            return parent;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public IType getType() {
            return resolver.getType();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ITypeDeclaration getTypeDeclaration() {
            return resolver.getTypeDeclaration();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
            // TODO: Can we do this???
            return getType().hasAnnotation(annotationType);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isCollection() {
            IMapping mapping = resolver.getMapping();
            return (mapping != null) ? mapping.isCollection() : false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isProperty() {
            IMapping mapping = resolver.getMapping();
            return (mapping != null) ? mapping.isProperty() : (mappingType == MappingType.PROPERTY);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isRelationship() {
            IMapping mapping = resolver.getMapping();
            return (mapping != null) ? mapping.isRelationship() : (mappingType == MappingType.RELATIONSHIP);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isTransient() {
            IMapping mapping = resolver.getMapping();
            return (mapping != null) ? mapping.isTransient() : false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return name;
        }
    }

    /**
     * This visitor will traverse the <code><b>SELECT</b></code> clause and create virtual mappings
     * for the state field path expressions and any expression aliased with a result variable.
     */
    protected class VirtualMappingBuilder extends AbstractEclipseLinkExpressionVisitor {

        /**
         * The virtual {@link IMapping} that wraps a select item mapped with the result variable if
         * one was defined otherwise the name will be generated based on the type of select item.
         */
        private Map<String, IMapping> mappings;

        /**
         *
         */
        private MappingType mappingType;

        /**
         * The virtual {@link IManagedType}.
         */
        protected IManagedType parent;

        /**
         * Creates a new <code>VirtualMappingBuilder</code>.
         */
        public VirtualMappingBuilder() {
            super();
            mappingType = MappingType.UNKNOWN;
        }

        /**
         * Creates
         *
         * @param name
         * @param resolver
         * @return
         */
        protected IMapping buildMapping(String name, Resolver resolver) {
            return new VirtualMapping(parent, name, resolver, mappingType);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(AbsExpression expression) {
            mappingType = MappingType.PROPERTY;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(AdditionExpression expression) {
            mappingType = MappingType.PROPERTY;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(AvgFunction expression) {
            mappingType = MappingType.PROPERTY;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CollectionExpression expression) {
            expression.acceptChildren(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(ConcatExpression expression) {
            mappingType = MappingType.PROPERTY;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CountFunction expression) {
            mappingType = MappingType.PROPERTY;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(DivisionExpression expression) {
            mappingType = MappingType.PROPERTY;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(IndexExpression expression) {
            mappingType = MappingType.PROPERTY;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(LengthExpression expression) {
            mappingType = MappingType.PROPERTY;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(LocateExpression expression) {
            mappingType = MappingType.PROPERTY;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(LowerExpression expression) {
            mappingType = MappingType.PROPERTY;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(MaxFunction expression) {
            mappingType = MappingType.PROPERTY;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(MinFunction expression) {
            mappingType = MappingType.PROPERTY;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(ResultVariable expression) {

            String name = queryContext.literal(expression, RESULT_VARIABLE);

            if (ExpressionTools.stringIsNotEmpty(name)) {

                // Visit the select expression in order to get a mapping type that could
                // help determine the mapping type
                Expression selectExpression = expression.getSelectExpression();
                selectExpression.accept(this);

                // Retrieve the select expression's Resolver and wrap it with a virtual mapping
                Resolver resolver = queryContext.getResolver(selectExpression);
                mappings.put(name, buildMapping(name, resolver));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SimpleSelectClause expression) {
            expression.getSelectExpression().accept(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SimpleSelectStatement expression) {
            expression.getSelectClause().accept(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SizeExpression expression) {
            mappingType = MappingType.PROPERTY;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SqrtExpression expression) {
            mappingType = MappingType.PROPERTY;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(StateFieldPathExpression expression) {

            if (!expression.startsWithDot()) {
                String name = queryContext.literal(expression, PATH_EXPRESSION_LAST_PATH);

                if (ExpressionTools.stringIsNotEmpty(name)) {

                    mappingType = MappingType.UNKNOWN;

                    Resolver resolver = queryContext.getResolver(expression);
                    mappings.put(name, buildMapping(name, resolver));
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SubstringExpression expression) {
            mappingType = MappingType.PROPERTY;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SubtractionExpression expression) {
            mappingType = MappingType.PROPERTY;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SumFunction expression) {
            mappingType = MappingType.PROPERTY;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(TrimExpression expression) {
            mappingType = MappingType.PROPERTY;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(UpperExpression expression) {
            mappingType = MappingType.PROPERTY;
        }
    }
}
