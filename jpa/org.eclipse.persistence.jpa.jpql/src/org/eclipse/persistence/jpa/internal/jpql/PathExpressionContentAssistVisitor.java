/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.internal.jpql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractPathExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractTraverseParentVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.AvgFunction;
import org.eclipse.persistence.jpa.internal.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.CollectionMemberDeclaration;
import org.eclipse.persistence.jpa.internal.jpql.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.Expression;
import org.eclipse.persistence.jpa.internal.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.Join;
import org.eclipse.persistence.jpa.internal.jpql.parser.QueryPosition;
import org.eclipse.persistence.jpa.internal.jpql.parser.StateFieldPathExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.SumFunction;
import org.eclipse.persistence.jpa.internal.jpql.util.AndFilter;
import org.eclipse.persistence.jpa.internal.jpql.util.Filter;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.TypeHelper;
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.spi.IQuery;
import org.eclipse.persistence.jpa.jpql.spi.IType;

/**
 * This visitor is responsible to retrieve the possible choices from a path expression, which is
 * either a state field path expression or a collection-valued path expression.
 * <p>
 * Note: This visitor does access the application's classes.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
final class PathExpressionContentAssistVisitor extends AbstractTraverseParentVisitor {

	/**
	 * This builder is responsible to retrieve the possible choices for a given state field path.
	 */
	private ChoiceBuilder choiceBuilder;

	/**
	 * The root of the {@link TypeResolver} hierarchy.
	 */
	private final DefaultTypeResolver parent;

	/**
	 * The external form representing the JPA query.
	 */
	private final IQuery query;

	/**
	 * Contains the position of the cursor within the parsed {@link Expression}.
	 */
	private final QueryPosition queryPosition;

	/**
	 * Creates a new <code>PathExpressionContentAssistVisitor</code>.
	 *
	 * @param query The external form representing the JPA query
	 * @param items The object used to store the various choices, those choices are stored per category
	 * @param queryPosition Contains the position of the cursor within the parsed {@link Expression}
	 */
	PathExpressionContentAssistVisitor(IQuery query, QueryPosition queryPosition) {
		super();

		this.query         = query;
		this.queryPosition = queryPosition;
		this.parent        = new DefaultTypeResolver(query);
		this.choiceBuilder = new DefaultChoiceBuilder();
	}

	private PathChoiceBuilder buildChoiceBuilder(AbstractPathExpression expression,
	                                             TypeResolver resolver,
	                                             Filter<IMapping> filter) {
		return buildChoiceBuilder(
			expression,
			resolver,
			filter,
			ExpressionTools.EMPTY_STRING
		);
	}

	private PathChoiceBuilder buildChoiceBuilder(AbstractPathExpression expression,
	                                             TypeResolver resolver,
	                                             Filter<IMapping> filter,
	                                             String pattern) {
		return new PathChoiceBuilder(
			resolver,
			buildFilter(expression, filter),
			pattern
		);
	}

	private PathExpressionHelper buildCollectionPathHelper() {
		return new PathExpressionHelper() {
			public boolean accept(IMapping value) {
				return MappingTypeHelper.isCollectionMapping(value);
			}

			public TypeResolver buildTypeResolver(TypeResolver parent, String path) {
				return new CollectionValuedFieldTypeResolver(parent, path);
			}
		};
	}

	private TypeResolver buildDeclarationVisitor(Expression expression) {

		// Locate the declaration expression
		DeclarationExpressionLocator locator = new DeclarationExpressionLocator(false);
		expression.accept(locator);

		// Create the resolver/visitor that will be able to resolve the variables
		DeclarationTypeResolver declarationResolver = null;

		for (Expression declarationExpression : locator.reversedDeclarationExpresions()) {
			if (declarationResolver == null) {
				declarationResolver = new DeclarationTypeResolver(parent);
			}
			else {
				declarationResolver = new DeclarationTypeResolver(declarationResolver);
			}
			declarationExpression.accept(declarationResolver);
		}

		return declarationResolver;
	}

	private Filter<IMapping> buildFilter(AbstractPathExpression expression, Filter<IMapping> filter) {

		// Wrap the filter with another Filter that will make sure only the
		// mappings with the right type will be accepted, for instance, AVG(e.|
		// can only accept state fields with a numeric type
		ExpressionTypeVisitor visitor = new ExpressionTypeVisitor();
		expression.getParent().accept(visitor);

		return new AndFilter<IMapping>(
			new MappingTypeFilter(visitor.type()),
			filter
		);
	}

	/**
	 * Retrieves the list of the possible choices that match the criteria, which are the cursor
	 * position and how much of the query is written.
	 *
	 * @param items The object used to store the various choices, those choices are stored per
	 * category
	 */
	public void buildItems(DefaultContentAssistItems items) {
		items.addMappings(choiceBuilder.choices());
	}

	private PathExpressionHelper buildPropertyPathHelper() {
		return new PathExpressionHelper() {
			public boolean accept(IMapping value) {
				return MappingTypeHelper.isPropertyMapping(value);
			}

			public TypeResolver buildTypeResolver(TypeResolver parent, String path) {
				return new StateFieldTypeResolver(parent, path);
			}
		};
	}

	private IManagedTypeProvider provider() {
		return query.getProvider();
	}

	private IType typeFor(Class<?> type) {
		return provider().getTypeRepository().getType(type);
	}

	private void visit(AbstractPathExpression expression, PathExpressionHelper helper) {

		int position = queryPosition.getPosition(expression);
		boolean choiceBuilderCreated = false;
		TypeResolver resolver = null;
		int cursor = 0;

		Expression identificationVariable = expression.getIdentificationVariable();
		String identificationVariableText = identificationVariable.toParsedText();

		// Nothing to do since the cursor is actually inside the identification
		// variable, which is handled by another visitor
		if (position < identificationVariableText.length()) {
			return;
		}

		// First create the resolver that will be able to resolve the
		// identification variables by visiting the declaration expression
		resolver = buildDeclarationVisitor(expression);

		// The declaration is not present or malformed
		if (resolver == null) {
			return;
		}

		// Retrieve the resolver from the path declaration
		PathDeclarationVisitor visitor = new PathDeclarationVisitor(resolver);
		identificationVariable.accept(visitor);
		resolver = visitor.resolver();

		// Move the cursor after the identification variable
		cursor = identificationVariableText.length() + 1;

		// The cursor is only after the identification variable
		if (position == cursor - 1) {
			return;
		}

		for (int index = 1, count = expression.pathSize(); index < count; index++) {
			String path = expression.getPath(index);

			// We're at the position, create the ChoiceBuilder
			if (cursor + path.length() >= position) {
				if (cursor == position) {
					path = ExpressionTools.EMPTY_STRING;
				}
				else if (position - cursor > -1) {
					path = path.substring(0, position - cursor);
				}

				// Special case where the path expression only has the
				// identification variable set
				if (resolver == null) {
					break;
				}

				choiceBuilder = buildChoiceBuilder(expression, resolver, helper, path);
				choiceBuilderCreated = true;
				break;
			}
			// The path is entirely before the position of the cursor
			else {
				// The first path is always an identification variable
				if (resolver == null) {
					resolver = new IdentificationVariableResolver(
						buildDeclarationVisitor(expression),
						path
					);
				}
				// Any other path is a property or collection-valued path
				else if ((index + 1 < count) || expression.endsWithDot()) {
					resolver = new SingleValuedObjectFieldTypeResolver(resolver, path);
				}
				else {
					resolver = helper.buildTypeResolver(resolver, path);
				}

				// Move the cursor after the path and dot
				cursor += path.length() + 1;
			}
		}

		if (!choiceBuilderCreated && (resolver != null)) {
			choiceBuilder = buildChoiceBuilder(expression, resolver, helper);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionValuedPathExpression expression) {
		if (expression.toParsedText().indexOf('.') > -1) {
			visit(expression, buildCollectionPathHelper());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StateFieldPathExpression expression) {

		if (expression.toParsedText().indexOf('.') > -1) {

			// Retrieve the filter based on the location of the state field path, for instance, in a
			// JOIN or IN expression, the filter has to filter out the property and accept the fields
			// of collection type
			FilterVisitor visitor = new FilterVisitor();
			expression.accept(visitor);

			// Now visit the state field path expression
			visit(expression, visitor.helper);
		}
	}

	/**
	 * This builder is used to retrieve the possible choices available from a class based on the
	 * position of the cursor, which will be used to traverse the path.
	 */
	private interface ChoiceBuilder {

		/**
		 * Retrieves the possible choices that can be used to complete a path expression based on the
		 * location of the cursor.
		 *
		 * @return The possible choices based on the location of the cursor
		 */
		Collection<IMapping> choices();
	}

	/**
	 * A {@link ChoiceBuilder} that returns no choices.
	 */
	private static class DefaultChoiceBuilder implements ChoiceBuilder {

		/**
		 * {@inheritDoc}
		 */
		public Collection<IMapping> choices() {
			return Collections.emptyList();
		}
	}

	/**
	 * This visitor retrieves the permitted type from the path expression's parent. For instance, SUM
	 * or AVG only accepts state fields that have a numeric type.
	 */
	private class ExpressionTypeVisitor extends AbstractExpressionVisitor {

		/**
		 * The type that is retrieved based on the expression, it determines what is acceptable.
		 */
		private IType type;

		/**
		 * Creates a new <code>ExpressionTypeVisitor</code>.
		 */
		ExpressionTypeVisitor() {
			super();
			type = typeFor(Object.class);
		}

		/**
		 * Returns the type that is retrieved based on the expression, it determines what is acceptable.
		 *
		 * @return The type of the path expression's parent
		 */
		IType type() {
			return type;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AvgFunction expression) {
			type = typeFor(Number.class);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionExpression expression) {
			expression.getParent().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SumFunction expression) {
			type = typeFor(Number.class);
		}
	}

	/**
	 * This visitor is responsible to create the right {@link PropertyFilter} based on the type of
	 * expression.
	 */
	private class FilterVisitor extends AbstractTraverseParentVisitor {

		/**
		 * The helper used to complete the resolution of the path expression.
		 */
		PathExpressionHelper helper;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionMemberDeclaration expression) {
			helper = buildCollectionPathHelper();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(Join expression) {
			helper = buildCollectionPathHelper();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(JPQLExpression expression) {
			helper = buildPropertyPathHelper();
		}
	}

	/**
	 * This {@link Filter} is responsible to filter out the mappings that can't have their type
	 * assignable to the one passed in.
	 */
	private class MappingTypeFilter implements Filter<IMapping> {

		/**
		 * The type used to determine if the mapping's type is a valid type.
		 */
		private final IType type;

		/**
		 * Creates a new <code>MappingTypeFilter</code>.
		 *
		 * @param type The type used to determine if the mapping's type is a valid type
		 */
		MappingTypeFilter(IType type) {
			super();
			this.type = type;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean accept(IMapping value) {
			IType mappingType = value.getType();
			mappingType = getTypeHelper().convertPrimitive(mappingType);
			return mappingType.isAssignableTo(type);
		}

		private TypeHelper getTypeHelper() {
			return query.getProvider().getTypeRepository().getTypeHelper();
		}
	}

	/**
	 * This {@link ChoiceBuilder} returns the possible properties (non-collection type or collection
	 * type) from a managed type.
	 */
	private class PathChoiceBuilder implements ChoiceBuilder {

		/**
		 * The filter used to filter out either the collection type properties or the non-collection
		 * type properties.
		 */
		private final Filter<IMapping> filter;

		/**
		 * The suffix is used to determine if the property name needs to be filtered out or not.
		 */
		private final String suffix;

		/**
		 * This resolver is used to retrieve the managed type, which is the parent path of this one.
		 */
		private final TypeResolver typeResolver;

		/**
		 * Creates a new <code>PathChoiceBuilder</code>.
		 *
		 * @param typeResolver This resolver is used to retrieve the managed type, which is the parent
		 * path of this one
		 * @param filter The filter used to filter out either the collection type properties or the
		 * non-collection type properties
		 * @param suffix The suffix is used to determine if the property name needs to be filtered out
		 * or not
		 */
		PathChoiceBuilder(TypeResolver typeResolver, Filter<IMapping> filter, String suffix) {
			super();
			this.filter       = filter;
			this.suffix       = suffix;
			this.typeResolver = typeResolver;
		}

		private void addFilteredMappings(IManagedType managedType, List<IMapping> mappings) {

			Filter<IMapping> filter = buildAggregateFilter(suffix);

			for (IMapping mapping : managedType.mappings()) {
				if (filter.accept(mapping)) {
					mappings.add(mapping);
				}
			}
		}

		private AndFilter<IMapping> buildAggregateFilter(String suffix) {
			return new AndFilter<IMapping>(
				filter,
				buildMappingNameFilter(suffix)
			);
		}

		private Filter<IMapping> buildMappingNameFilter(final String suffix) {
			return new Filter<IMapping>() {
				public boolean accept(IMapping mapping) {
					return mapping.getName().startsWith(suffix);
				}
			};
		}

		/**
		 * {@inheritDoc}
		 */
		public Collection<IMapping> choices() {

			IManagedType managedType = typeResolver.getManagedType();

			if (managedType == null) {
				return Collections.emptyList();
			}

			ArrayList<IMapping> mappings = new ArrayList<IMapping>();
			addFilteredMappings(managedType, mappings);
			return mappings;
		}
	}

	/**
	 * This helper completes the behavior for retrieving the possible choices for a given path
	 * expression.
	 */
	private static interface PathExpressionHelper extends Filter<IMapping> {

		/**
		 * Creates the {@link TypeResolver} that can resolve the type of the given path.
		 *
		 * @param parent The parent visitor is used to retrieve the type from where the property
		 * should be retrieved
		 * @param path A single path to resolve
		 * @return The {@link TypeResolver} responsible to return the type for the given path
		 */
		TypeResolver buildTypeResolver(TypeResolver parent, String path);
	}
}