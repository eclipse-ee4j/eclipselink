/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.parser.AbstractExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSchemaName;
import org.eclipse.persistence.jpa.jpql.parser.AbstractTraverseChildrenVisitor;
import org.eclipse.persistence.jpa.jpql.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.ConstructorExpression;
import org.eclipse.persistence.jpa.jpql.parser.EntityTypeLiteral;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.JPQLStatementBNF;
import org.eclipse.persistence.jpa.jpql.parser.ResultVariable;
import org.eclipse.persistence.jpa.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.StateFieldPathExpression;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.java.JavaQuery;

/**
 * The abstract implementation providing refactoring support for JPQL queries. This version does not
 * changes the {@link org.eclipse.persistence.jpa.jpql.model.query. } but
 * rather gather the changes in {@link RefactoringDelta} and it is the responsibility of the invoker
 * to the actual change.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @see DefaultBasicRefactoringTool
 * @see EclipseLinkBasicRefactoringTool
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public abstract class BasicRefactoringTool extends AbstractRefactoringTool {

	/**
	 * Keeps track of the changes made to the JPQL query.
	 */
	private DefaultRefactoringDelta delta;

	/**
	 * The parsed tree representation of the JPQL query.
	 */
	private JPQLExpression jpqlExpression;

	/**
	 * The {@link JPQLGrammar} that was used to parse the JPQL query or JPQL fragments.
	 */
	private JPQLGrammar jpqlGrammar;

	/**
	 * The context used to query information about the JPQL query.
	 */
	private JPQLQueryContext queryContext;

	/**
	 * Creates a new <code>BasicRefactoringTool</code>.
	 *
	 * @param jpqlQuery The JPQL query to manipulate
	 * @param jpqlGrammar The {@link JPQLGrammar} that was used to parse the JPQL query
	 * @param managedTypeProvider The external form of a provider that gives access to the JPA metadata
	 */
	protected BasicRefactoringTool(CharSequence jpqlQuery,
	                                JPQLGrammar jpqlGrammar,
	                                IManagedTypeProvider managedTypeProvider) {

		this(jpqlQuery, jpqlGrammar, managedTypeProvider, JPQLStatementBNF.ID);
	}

	/**
	 * Creates a new <code>BasicRefactoringTool</code>.
	 *
	 * @param jpqlFragment The JPQL query to manipulate or a single JPQL fragment, which is parsed
	 * using the JPQL query BNF identifier by the given ID
	 * @param jpqlGrammar The {@link JPQLGrammar} that was used to parse the JPQL fragment
	 * @param managedTypeProvider The external form of a provider that gives access to the JPA metadata
	 * @param jpqlQueryBNFId The unique identifier of the {@link org.eclipse.persistence.jpa.jpql.
	 * parser.JPQLQueryBNF JPQLQueryBNF} that determines how to parse the JPQL fragment
	 */
	protected BasicRefactoringTool(CharSequence jpqlFragment,
	                               JPQLGrammar jpqlGrammar,
	                               IManagedTypeProvider managedTypeProvider,
	                               String jpqlQueryBNFId) {

		super(jpqlFragment, managedTypeProvider, jpqlQueryBNFId);
		this.jpqlGrammar = jpqlGrammar;
		this.delta       = new DefaultRefactoringDelta(jpqlFragment);
	}

	/**
	 * Creates the visitor that will traverse the {@link } representation of the JPQL
	 * query and will rename the fully qualified class name.
	 *
	 * @param oldClassName The current name of the class to rename
	 * @param newClassName The new name of the class
	 * @return A new {@link ClassNameRenamer}
	 */
	protected ClassNameRenamer buildClassNameRenamer(String oldClassName, String newClassName) {
		return new ClassNameRenamer(oldClassName, newClassName);
	}

	/**
	 * Creates the visitor that will traverse the {@link } representation of the JPQL
	 * query and will rename the entity name.
	 *
	 * @param oldEntityName The current name of the entity to rename
	 * @param newEntityName The new name of the entity
	 * @return A new {@link EntityNameRenamer}
	 */
	protected EntityNameRenamer buildEntityNameRenamer(String oldEntityName, String newEntityName) {
		return new EntityNameRenamer(oldEntityName, newEntityName);
	}

	/**
	 * Creates the visitor that will traverse the {@link } representation of the JPQL
	 * query and will rename the enum constant.
	 *
	 * @param oldClassName The new name of the enum constant
	 * @param newClassName The current name of the enum constant to rename
	 * @return A new {@link EnumConstantRenamer}
	 */
	protected EnumConstantRenamer buildEnumConstantRenamer(String oldClassName, String newClassName) {
		return new EnumConstantRenamer(getManagedTypeProvider(), oldClassName, newClassName);
	}

	/**
	 * Creates the visitor that will traverse the {@link } representation of the JPQL
	 * query and will rename a type's attribute name.
	 *
	 * @param typeName The fully qualified name of the type that got one of its attributes renamed
	 * @param oldFieldName The current name of the attribute to rename
	 * @param newFieldName The new name of the attribute
	 * @return A new {@link FieldNameRenamer}
	 */
	protected FieldNameRenamer buildFieldNameRenamer(String typeName,
	                                                 String oldFieldName,
	                                                 String newFieldName) {

		return new FieldNameRenamer(getQueryContext(), typeName, oldFieldName, newFieldName);
	}

	/**
	 * Creates a new {@link JPQLQueryContext} that can retrieve information from the declaration
	 * portion of the JPQL query.
	 *
	 * @return A new concrete instance of {@link JPQLQueryContext}
	 */
	protected abstract JPQLQueryContext buildJPQLQueryContext();

	/**
	 * Creates the visitor that will traverse the {@link } representation of the JPQL
	 * query and will rename a result variable.
	 *
	 * @param oldVariableName The current result variable name
	 * @param newVariableName The new name of the result variable
	 * @return A new {@link ResultVariableNameRenamer}
	 */
	protected ResultVariableNameRenamer buildResultVariableNameRenamer(String oldVariableName,
	                                                                   String newVariableName) {

		return new ResultVariableNameRenamer(oldVariableName, newVariableName);
	}

	/**
	 * Creates the visitor that will traverse the {@link } representation of the JPQL
	 * query and will rename an identification variable.
	 *
	 * @param oldVariableName The current identification variable name
	 * @param newVariableName The new name of the identification variable
	 * @return A new {@link VariableNameRenamer}
	 */
	protected VariableNameRenamer buildVariableNameRenamer(String oldVariableName,
	                                                       String newVariableName) {

		return new VariableNameRenamer(oldVariableName, newVariableName);
	}

	/**
	 * Returns the delta of the changes made to the JPQL query.
	 *
	 * @return An object containing the refactoring events
	 */
	public RefactoringDelta getDelta() {
		return delta;
	}

	/**
	 * Returns the parsed tree representation of the JPQL query.
	 *
	 * @return The root of the parsed tree
	 */
	public JPQLExpression getExpression() {
		if (jpqlExpression == null) {
			jpqlExpression = new JPQLExpression(getJPQLFragment(), jpqlGrammar, isTolerant());
		}
		return jpqlExpression;
	}

	/**
	 * Returns the {@link JPQLGrammar} that is associated with this builder.
	 *
	 * @return The {@link JPQLGrammar} that was used to parse the JPQL query or JPQL fragments
	 */
	public JPQLGrammar getGrammar() {
		return jpqlGrammar;
	}

	/**
	 * Returns the {@link JPQLQueryContext} that is used by this visitor.
	 *
	 * @return The {@link JPQLQueryContext} holding onto the JPQL query and the cached information
	 */
	public JPQLQueryContext getQueryContext() {
		if (queryContext == null) {
			queryContext = buildJPQLQueryContext();
			queryContext.setJPQLExpression(getExpression());
			queryContext.setQuery(new JavaQuery(getManagedTypeProvider(), getJPQLFragment()));
		}
		return queryContext;
	}

	/**
	 * Determines whether some refactoring operations found changes to be made in the JPQL query.
	 *
	 * @return <code>true</code> if there is at least one {@link TextEdit}; <code>false</code> otherwise
	 */
	public boolean hasChanges() {
		return delta.hasTextEdits();
	}

	/**
	 * Renames a fully qualified class name.
	 *
	 * @param oldClassName The current fully qualified class name of the class to rename
	 * @param newClassName The new fully qualified class name
	 */
	public void renameClassName(String oldClassName, String newClassName) {
		ClassNameRenamer renamer = buildClassNameRenamer(oldClassName, newClassName);
		getExpression().accept(renamer);
		delta.addTextEdits(renamer.textEdits);
	}

	/**
	 * Renames a given entity name.
	 *
	 * @param oldEntityName The current name of the entity to rename
	 * @param newEntityName The new name of the entity
	 */
	public void renameEntityName(String oldEntityName, String newEntityName) {
		AbstractRenamer renamer = buildEntityNameRenamer(oldEntityName, newEntityName);
		getExpression().accept(renamer);
		delta.addTextEdits(renamer.textEdits);
	}

	/**
	 * Renames an enum constant, which has to be fully qualified.
	 *
	 * @param oldEnumConstant The current fully qualified name of the enum constant to rename
	 * @param newEnumConstant The new fully qualified name of the enum constant
	 */
	public void renameEnumConstant(String oldEnumConstant, String newEnumConstant) {
		AbstractRenamer renamer = buildEnumConstantRenamer(oldEnumConstant, newEnumConstant);
		getExpression().accept(renamer);
		delta.addTextEdits(renamer.textEdits);
	}

	/**
	 * Renames a field from the given type.
	 *
	 * @param type The Java class from which the change originate
	 * @param oldFieldName The current name of the attribute to rename
	 * @param newFieldName The new name of the attribute
	 */
	public void renameField(Class<?> type, String oldFieldName, String newFieldName) {
		renameField(type.getName(), oldFieldName, newFieldName);
	}

	/**
	 * Renames a field from the given type.
	 *
	 * @param type The {@link IType} from which the change originate
	 * @param oldFieldName The current name of the attribute to rename
	 * @param newFieldName The new name of the attribute
	 */
	public void renameField(IType type, String oldFieldName, String newFieldName) {
		renameField(type.getName(), oldFieldName, newFieldName);
	}

	/**
	 * Renames a field from the given type.
	 *
	 * @param typeName The fully qualified name of the type that got one of its attributes renamed
	 * @param oldFieldName The current name of the attribute to rename
	 * @param newFieldName The new name of the attribute
	 */
	public void renameField(String typeName, String oldFieldName, String newFieldName) {
		AbstractRenamer renamer = buildFieldNameRenamer(typeName, oldFieldName, newFieldName);
		getExpression().accept(renamer);
		delta.addTextEdits(renamer.textEdits);
	}

	/**
	 * Renames a result variable name.
	 *
	 * @param oldVariableName The current identification variable name
	 * @param newVariableName The new name of the identification variable
	 */
	public void renameResultVariable(String oldVariableName, String newVariableName) {
		AbstractRenamer renamer = buildResultVariableNameRenamer(oldVariableName, newVariableName);
		getExpression().accept(renamer);
		delta.addTextEdits(renamer.textEdits);
	}

	/**
	 * Renames a variable name.
	 *
	 * @param oldVariableName The current identification variable name
	 * @param newVariableName The new name of the identification variable
	 */
	public void renameVariable(String oldVariableName, String newVariableName) {
		AbstractRenamer renamer = buildVariableNameRenamer(oldVariableName, newVariableName);
		getExpression().accept(renamer);
		delta.addTextEdits(renamer.textEdits);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toActualText() {
		return getDelta().applyChanges();
	}

	/**
	 * The abstract class that all refactoring classes should extend, it automatically provides
	 * the {@link MultiTextEdit} that will holds the {@link TextEdit} objects that are related to
	 * the same refactoring event.
	 */
	protected abstract class AbstractRenamer extends AbstractTraverseChildrenVisitor {

		/**
		 * The list of {@link TextEdit} objects that were created for each refactoring operation.
		 */
		protected List<TextEdit> textEdits;

		/**
		 * Creates a new <code>AbstractRenamer</code>.
		 */
		protected AbstractRenamer() {
			super();
			textEdits = new ArrayList<TextEdit>();
		}

		/**
		 * Adds a new {@link TextEdit} with the given information.
		 *
		 * @param expression The {@link Expression} which should be refactored, it will be used to
		 * retrieve the offset of the change
		 * @param extraOffset Additional offset that will be added to the given {@link Expression}'s
		 * offset, which is the length of the string representation of what is before it
		 * @param oldValue The old value to change to the new one
		 * @param newValue The new value
		 */
		protected void addTextEdit(Expression expression, int extraOffset, String oldValue, String newValue) {
			TextEdit textEdit = buildTextEdit(
				reposition(expression.getOffset() + extraOffset),
				oldValue,
				newValue
			);
			textEdits.add(textEdit);
		}

		/**
		 * Adds a new {@link TextEdit} with the given information.
		 *
		 * @param expression The {@link Expression} which should be refactored, it will be used to
		 * retrieve the offset of the change
		 * @param oldValue The old value to change to the new one
		 * @param newValue The new value
		 */
		protected void addTextEdit(Expression expression, String oldValue, String newValue) {
			addTextEdit(expression, 0, oldValue, newValue);
		}

		/**
		 * Creates a new {@link TextEdit} for the given refactoring information.
		 *
		 * @param offset The position where the change should be made within the actual JPQL fragment
		 * @param oldValue The old value to change to the new one
		 * @param newValue The new value
		 * @return A new {@link TextEdit}
		 */
		protected TextEdit buildTextEdit(int offset, String oldValue, String newValue) {
			return new DefaultTextEdit(offset, oldValue, newValue);
		}

		/**
		 * Repositions the given position that is based on the generated JPQL query to be the position
		 * from the JPQL fragment that was parsed.
		 *
		 * @param offset The position within the string generated by {@link Expression#toActualText()}
		 * @return The position within the JPQL fragment that was passed to the refactoring tool
		 */
		protected int reposition(int offset) {
			return ExpressionTools.repositionCursor(
				getExpression().toActualText(),
				offset,
				getJPQLFragment()
			);
		}
	}

	/**
	 * This visitor renames a fully qualified class name.
	 */
	protected class ClassNameRenamer extends AbstractRenamer {

		/**
		 * The current name of the class to rename.
		 */
		protected final String newClassName;

		/**
		 * The new name of the class.
		 */
		protected final String oldClassName;

		/**
		 * Creates a new <code>ClassNameRenamer</code>.
		 *
		 * @param oldClassName The current name of the class to rename
		 * @param newClassName The new name of the class
		 */
		public ClassNameRenamer(String oldClassName, String newClassName) {
			super();
			this.oldClassName = oldClassName;
			this.newClassName = newClassName;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ConstructorExpression expression) {
			visit(expression, expression.getClassName(), 4); // 4 = "NEW "
		}

		/**
		 * Visits the given {@link } and if its value is the same as the old class name or
		 * if the value represents an inner class of that old class name, then the given {@link
		 * StateObjectUpdater} will be notified to replace the value.
		 *
		 * @param expression The {@link } that is being visited
		 * @param extraOffset Additional offset that will be added to the given {@link Expression}'s
		 * offset, which is the length of the string representation of what is before it
		 * @param value The value to check if it's the old class name
		 * @param updater The {@link StateObjectUpdater} is notified when to replace the value
		 */
		protected void visit(Expression expression, String value, int extraOffset) {

			if (oldClassName.equals(value)) {
				addTextEdit(expression, extraOffset, oldClassName, newClassName);
			}
			else {
				int index = value.lastIndexOf(AbstractExpression.DOT);

				// Traverse the value by retrieving a fragment up to the last dot (based on the index)
				for (; index > -1; index = value.lastIndexOf(AbstractExpression.DOT, index - 1)) {

					// Retrieve the fragment from the beginning to the current dot
					String fragment = value.substring(0, index);

					if (oldClassName.equals(fragment)) {
						addTextEdit(expression, extraOffset, oldClassName, newClassName);
						break;
					}
					// No need to continue the search
					else if (fragment.length() < oldClassName.length()) {
						break;
					}
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(StateFieldPathExpression expression) {
			// A fully qualified enum constant is parsed as a state field path expression
			if (!expression.startsWithDot()) {
				visit(expression, expression.toActualText(), 0);
			}
		}
	}

	/**
	 * This visitor renames an entity name. There are three possible {@link StateObjects}
	 * that can represent an entity name:
	 * <ul>
	 * <li>
	 * {@link AbstractSchemaNameStateObject}:
	 * <i>Employee</i> in<br><br>
	 * <code><b>SELECT</b> e<br>
	 * <b>FROM</b> Employee e</code><br><br>
	 * </li>
	 * <li>
	 * {@link EntityTypeLiteralStateObject}:
	 * <i>Exempt</i> in<br><br>
	 * <pre><code><b> SELECT CASE TYPE</b>(e) <b>WHEN</b> Exempt <b>THEN</b> 'Exempt'
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>ELSE</b> 'NONE'
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>END</b>
	 * <b>FROM</b> Employee e</code></pre>
	 * </li>
	 * <li>
	 * {@link IdentificationVariableStateObject}:
	 * <i>Exempt</i> in<br>
	 * <pre><code> <b>SELECT</b> e
	 * <b>FROM</b> Employee e
	 * <b>WHERE TYPE</b>(e) <> Exempt</code></pre>
	 * </li>
	 * </ul>
	 */
	protected class EntityNameRenamer extends AbstractRenamer {

		/**
		 * The current name of the entity to rename.
		 */
		protected final String newEntityName;

		/**
		 * The new name of the entity.
		 */
		protected final String oldEntityName;

		/**
		 * Creates a new <code>EntityNameRenamer</code>.
		 *
		 * @param oldEntityName The current name of the entity to rename
		 * @param newEntityName The new name of the entity
		 */
		public EntityNameRenamer(String oldEntityName, String newEntityName) {
			super();
			this.oldEntityName = oldEntityName;
			this.newEntityName = newEntityName;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AbstractSchemaName expression) {
			if (oldEntityName.equals(expression.getText())) {
				addTextEdit(expression, oldEntityName, newEntityName);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(EntityTypeLiteral expression) {
			if (oldEntityName.equals(expression.getEntityTypeName())) {
				addTextEdit(expression, oldEntityName, newEntityName);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariable expression) {
			if (oldEntityName.equals(expression.getText())) {
				addTextEdit(expression, oldEntityName, newEntityName);
			}
		}
	}

	/**
	 * This visitor renames an enum constant. An enum constant is represented by a path expression.
	 */
	protected class EnumConstantRenamer extends AbstractRenamer {

		/**
		 * The external form of a provider that gives access to the JPA metadata.
		 */
		protected final IManagedTypeProvider managedTypeProvider;

		/**
		 * The current name of the enum constant to rename.
		 */
		protected final String newEnumConstant;

		/**
		 * The new name of the enum constant.
		 */
		protected final String oldEnumConstant;

		/**
		 * Creates a new <code>ClassNameRenamer</code>.
		 *
		 * @param managedTypeProvider The provider of managed types
		 * @param oldEnumConstant The new name of the enum constant
		 * @param newEnumConstant The current name of the enum constant to rename
		 */
		public EnumConstantRenamer(IManagedTypeProvider managedTypeProvider,
		                           String oldEnumConstant,
		                           String newEnumConstant) {

			super();
			this.oldEnumConstant     = oldEnumConstant;
			this.newEnumConstant     = newEnumConstant;
			this.managedTypeProvider = managedTypeProvider;
		}

		protected void renameEnumConstant(AbstractPathExpression expression) {

			String path = expression.toString();

			if (path.equals(oldEnumConstant)) {

				// Check to see if the path is actually a fully qualified enum constant
				IType type = managedTypeProvider.getTypeRepository().getEnumType(path);

				// If it is not null, then it's a fully qualified enum constant
				if (type != null) {
					addTextEdit(expression, oldEnumConstant, newEnumConstant);
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionValuedPathExpression expression) {
			renameEnumConstant(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(StateFieldPathExpression expression) {
			renameEnumConstant(expression);
		}
	}

	/**
	 * This visitor renames any segment of a path expression.
	 */
	protected class FieldNameRenamer extends AbstractRenamer {

		/**
		 * The new name of the attribute.
		 */
		protected final String newFieldName;

		/**
		 * The current name of the attribute to rename.
		 */
		protected final String oldFieldName;

		/**
		 * The context used to query information about the JPQL query.
		 */
		private final JPQLQueryContext queryContext;

		/**
		 * The fully qualified name of the type that got one of its attributes renamed.
		 */
		protected final String typeName;

		/**
		 * Creates a new <code>FieldNameRenamer</code>.
		 *
		 * @param queryContext The context used to query information about the JPQL query
		 * @param typeName The fully qualified name of the type that got one of its attributes renamed
		 * @param oldFieldName The current name of the attribute to rename
		 * @param newFieldName The new name of the attribute
		 */
		public FieldNameRenamer(JPQLQueryContext queryContext,
		                        String typeName,
		                        String oldFieldName,
		                        String newFieldName) {

			super();
			this.typeName     = typeName;
			this.oldFieldName = oldFieldName;
			this.newFieldName = newFieldName;
			this.queryContext = queryContext;
		}

		/**
		 * Performs the rename on the path expression.
		 *
		 * @param expression The {@link AbstractPathExpression} being visited, which may have to have
		 * its path renamed
		 */
		protected void rename(AbstractPathExpression expression) {

			if (!expression.hasIdentificationVariable()) {
				return;
			}

			Resolver resolver = queryContext.getResolver(expression.getIdentificationVariable());

			// Can't continue to resolve the path expression
			if (resolver == null) {
				return;
			}

			// Now traverse the path expression after the identification variable
			for (int index = expression.hasVirtualIdentificationVariable() ? 0 : 1, count = expression.pathSize(); index < count; index++) {

				// Retrieve the mapping for the path at the current position
				String path = expression.getPath(index);
				Resolver childResolver = resolver.getChild(path);

				if (childResolver == null) {
					childResolver = new StateFieldResolver(resolver, path);
					resolver.addChild(path, childResolver);
					resolver = childResolver;
				}

				IMapping mapping = resolver.getMapping();

				// Invalid path expression
				if (mapping == null) {
					break;
				}

				// The name matches
				if (mapping.getName().equals(oldFieldName)) {

					// Make sure the field name is from the right type
					String parentTypeName = mapping.getParent().getType().getName();

					if (parentTypeName.equals(typeName)) {
						int extraOffset = expression.toParsedText(0, index).length() + 1 /* '.' */;
						addTextEdit(expression, extraOffset, oldFieldName, newFieldName);
						break;
					}
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionValuedPathExpression expression) {
			rename(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(StateFieldPathExpression expression) {
			rename(expression);
		}
	}

	/**
	 * This visitor renames all the result variables found in the JPQL query.
	 */
	protected class ResultVariableNameRenamer extends AbstractRenamer {

		/**
		 * The new name of the result variable.
		 */
		protected final String newVariableName;

		/**
		 * The current result variable name.
		 */
		protected final String oldVariableName;

		/**
		 * Makes sure an identification variable is renamed only when it's used by an order by item.
		 */
		protected boolean renameIdentificationVariable;

		/**
		 * Creates a new <code>ResultVariableNameRenamer</code>.
		 *
		 * @param oldVariableName The current result variable name
		 * @param newVariableName The new name of the result variable
		 */
		public ResultVariableNameRenamer(String oldVariableName, String newVariableName) {
			super();
			this.oldVariableName = oldVariableName;
			this.newVariableName = newVariableName;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariable expression) {

			if (renameIdentificationVariable &&
			    oldVariableName.equalsIgnoreCase(expression.getText())) {

				addTextEdit(expression, oldVariableName, newVariableName);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ResultVariable expression) {

			if (expression.hasResultVariable()) {
				renameIdentificationVariable = true;
				try {
					expression.getResultVariable().accept(this);
				}
				finally {
					renameIdentificationVariable = false;
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SelectStatement expression) {

			// Result variables defined in the SELECT clause
			expression.getSelectClause().accept(this);

			// Result variables used in the ORDER BY clause
			if (expression.hasOrderByClause()) {
				renameIdentificationVariable = true;
				try {
					expression.getOrderByClause().accept(this);
				}
				finally {
					renameIdentificationVariable = false;
				}
			}
		}
	}

	/**
	 * This visitor renames all the identification variables found in the JPQL query.
	 */
	protected class VariableNameRenamer extends AbstractRenamer {

		/**
		 * The new name of the identification variable.
		 */
		protected final String newVariableName;

		/**
		 * The current identification variable name.
		 */
		protected final String oldVariableName;

		/**
		 * Creates a new <code>VariableNameRenamer</code>.
		 *
		 * @param oldVariableName The current identification variable name
		 * @param newVariableName The new name of the identification variable
		 */
		public VariableNameRenamer(String oldVariableName, String newVariableName) {
			super();
			this.oldVariableName = oldVariableName;
			this.newVariableName = newVariableName;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariable expression) {
			if (oldVariableName.equalsIgnoreCase(expression.getText())) {
				addTextEdit(expression, oldVariableName, newVariableName);
			}
		}
	}
}