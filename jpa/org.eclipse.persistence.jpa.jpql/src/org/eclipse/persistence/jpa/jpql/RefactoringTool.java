/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle. All rights reserved.
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

import org.eclipse.persistence.jpa.jpql.model.IJPQLQueryBuilder;
import org.eclipse.persistence.jpa.jpql.model.IJPQLQueryFormatter;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractPathExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractSchemaNameStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractTraverseChildrenVisitor;
import org.eclipse.persistence.jpa.jpql.model.query.CollectionValuedPathExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ConstructorExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.EntityTypeLiteralStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.IdentificationVariableStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.JPQLQueryStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.OrderByItemStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ResultVariableStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SelectStatementStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.StateFieldPathExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.StateObject;
import org.eclipse.persistence.jpa.jpql.parser.AbstractExpression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.JPQLStatementBNF;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.spi.IType;

/**
 * The abstract implementation providing refactoring support for JPQL queries.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @see DefaultRefactoringTool
 * @see EclipseLinkRefactoringTool
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public abstract class RefactoringTool {

	/**
	 * The JPQL query to manipulate or a single JPQL fragment, which is parsed using the JPQL query
	 * specified BNF.
	 */
	private CharSequence jpqlFragment;

	/**
	 * The unique identifier of the {@link org.eclipse.persistence.jpa.jpql.parser.JPQLQueryBNF
	 * JPQLQueryBNF} that determines how to parse the JPQL fragment.
	 */
	private String jpqlQueryBNFId;

	/**
	 * The builder that creates the {@link StateObject} representation of the JPQL query.
	 */
	private IJPQLQueryBuilder jpqlQueryBuilder;

	/**
	 * The formatter that converts a {@link StateObject} representation of the JPQL query into a
	 * string representation.
	 */
	private IJPQLQueryFormatter jpqlQueryFormatter;

	/**
	 * The external form of a provider that gives access to the JPA metadata.
	 */
	private IManagedTypeProvider managedTypeProvider;

	/**
	 * The editable representation of the JPQL query.
	 */
	private JPQLQueryStateObject stateObject;

	/**
	 * Determines whether the parsing system should be tolerant.
	 */
	private boolean tolerant;

	/**
	 * Creates a new <code>RefactoringTool</code>.
	 *
	 * @param managedTypeProvider The external form of a provider that gives access to the JPA metadata
	 * @param jpqlQueryBuilder The builder that creates the {@link StateObject} representation of the
	 * JPQL query
	 * @param jpqlQuery The JPQL query to manipulate
	 */
	protected RefactoringTool(IManagedTypeProvider managedTypeProvider,
	                          IJPQLQueryBuilder jpqlQueryBuilder,
	                          CharSequence jpqlQuery) {

		this(managedTypeProvider, jpqlQueryBuilder, jpqlQuery, JPQLStatementBNF.ID);
	}

	/**
	 * Creates a new <code>RefactoringTool</code>.
	 *
	 * @param managedTypeProvider The external form of a provider that gives access to the JPA metadata
	 * @param jpqlQueryBuilder The builder that creates the {@link StateObject} representation of the
	 * JPQL query
	 * @param jpqlFragment The JPQL query to manipulate or a single JPQL fragment, which is parsed
	 * using the JPQL query BNF identifier by the given ID
	 * @param jpqlQueryBNFId The unique identifier of the {@link org.eclipse.persistence.jpa.jpql.
	 * parser.JPQLQueryBNF JPQLQueryBNF} that determines how to parse the JPQL fragment
	 */
	protected RefactoringTool(IManagedTypeProvider managedTypeProvider,
	                          IJPQLQueryBuilder jpqlQueryBuilder,
	                          CharSequence jpqlFragment,
	                          String jpqlQueryBNFId) {

		super();
		this.tolerant            = true;
		this.jpqlFragment        = jpqlFragment;
		this.jpqlQueryBNFId      = jpqlQueryBNFId;
		this.jpqlQueryBuilder    = jpqlQueryBuilder;
		this.managedTypeProvider = managedTypeProvider;
	}

	/**
	 * Creates the visitor that will traverse the {@link StateObject} representation of the JPQL
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
	 * Creates the visitor that will traverse the {@link StateObject} representation of the JPQL
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
	 * Creates the visitor that will traverse the {@link StateObject} representation of the JPQL
	 * query and will rename the enum constant.
	 *
	 * @param oldEnumConstant The new name of the enum constant
	 * @param newEnumConstant The current name of the enum constant to rename
	 * @return A new {@link EnumConstantRenamer}
	 */
	protected EnumConstantRenamer buildEnumConstantRenamer(String oldClassName, String newClassName) {
		return new EnumConstantRenamer(managedTypeProvider, oldClassName, newClassName);
	}

	/**
	 * Creates the visitor that will traverse the {@link StateObject} representation of the JPQL
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

		return new FieldNameRenamer(managedTypeProvider, typeName, oldFieldName, newFieldName);
	}

	/**
	 * Creates a new formatter that will convert the {@link StateObject} representation of the JPQL
	 * query, once the refactoring occurred into a string.
	 *
	 * @return A new concrete instance of {@link IJPQLQueryFormatter}
	 */
	protected abstract IJPQLQueryFormatter buildFormatter();

	/**
	 * Creates a new {@link JPQLQueryContext} that can retrieve information from the declaration
	 * portion of the JPQL query.
	 * <p>
	 * <b>NOTE:</b> This is temporary because the {@link StateObject} API does not have that
	 * knowledge yet.
	 *
	 * @return A new concrete instance of {@link JPQLQueryContext}
	 */
	protected abstract JPQLQueryContext buildJPQLQueryContext();

	/**
	 * Creates the visitor that will traverse the {@link StateObject} representation of the JPQL
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
	 * Creates the {@link StateObject} representation of the JPQL fragment to manipulate.
	 *
	 * @return The parsed and editable JPQL query
	 * @see #getStateObject()
	 */
	protected JPQLQueryStateObject buildStateObject() {
		return jpqlQueryBuilder.buildStateObject(
			managedTypeProvider,
			jpqlFragment,
			jpqlQueryBNFId,
			tolerant
		);
	}

	/**
	 * Creates the visitor that will traverse the {@link StateObject} representation of the JPQL
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
	 * Returns the {@link IJPQLQueryFormatter} that creates an accurate representation of the {@link
	 * StateObject}, i.e. that output the JPQL query with the case used for the JPQL identifier.
	 *
	 * @return An instance of {@link IJPQLQueryFormatter}
	 */
	public IJPQLQueryFormatter getFormatter() {
		if (jpqlQueryFormatter == null) {
			jpqlQueryFormatter = buildFormatter();
		}
		return jpqlQueryFormatter;
	}

	/**
	 * Returns the {@link JPQLGrammar} that is associated with this builder.
	 *
	 * @return The {@link JPQLGrammar} that was used to parse the JPQL query or JPQL fragments
	 */
	public JPQLGrammar getGrammar() {
		return jpqlQueryBuilder.getGrammar();
	}

	/**
	 * Returns the original JPQL query or the JPQL fragment that was passed to this tool so it can
	 * be manipulated.
	 *
	 * @return The string representation of the JPQL query or fragment
	 */
	public CharSequence getJPQLFragment() {
		return jpqlFragment;
	}

	/**
	 * Returns the unique identifier of the JPQL query BNF that determined how the JPQL query or
	 * fragment needs to be parsed.
	 *
	 * @return The ID of the {@link org.eclipse.persistence.jpa.jpql.parser.JPQLQueryBNF JPQLQueryBNF}
	 * used to parse the query
	 */
	public String getJPQLQueryBNFId() {
		return jpqlQueryBNFId;
	}

	/**
	 * Returns the builder that creates the {@link StateObject} representation of the JPQL query.
	 *
	 * @return The builder of the {@link StateObject} to be manipulated
	 */
	public IJPQLQueryBuilder getJPQLQueryBuilder() {
		return jpqlQueryBuilder;
	}

	/**
	 * Returns the provider of managed types.
	 *
	 * @return The provider that gives access to the managed types
	 */
	public IManagedTypeProvider getManagedTypeProvider() {
		return managedTypeProvider;
	}

	/**
	 * Returns the {@link StateObject} representation of the JPQL query or JPQL fragment that was parsed.
	 *
	 * @return The editable state model
	 */
	public JPQLQueryStateObject getStateObject() {
		if (stateObject == null) {
			stateObject = buildStateObject();
		}
		return stateObject;
	}

	/**
	 * Determines whether the parsing system should be tolerant, meaning if it should try to parse
	 * invalid or incomplete queries.
	 *
	 * @return By default, the parsing system uses tolerance
	 */
	public boolean isTolerant() {
		return tolerant;
	}

	/**
	 * Renames a fully qualified class name.
	 *
	 * @param oldClassName The current fully qualified class name of the class to rename
	 * @param newClassName The new fully qualified class name
	 */
	public void renameClassName(String oldClassName, String newClassName) {
		ClassNameRenamer renamer = buildClassNameRenamer(oldClassName, newClassName);
		getStateObject().accept(renamer);
	}

	/**
	 * Renames a given entity name.
	 *
	 * @param oldEntityName The current name of the entity to rename
	 * @param newEntityName The new name of the entity
	 */
	public void renameEntityName(String oldEntityName, String newEntityName) {
		EntityNameRenamer renamer = buildEntityNameRenamer(oldEntityName, newEntityName);
		getStateObject().accept(renamer);
	}

	/**
	 * Renames an enum constant, which has to be fully qualified.
	 *
	 * @param oldEnumConstant The current fully qualified name of the enum constant to rename
	 * @param newEnumConstant The new fully qualified name of the enum constant
	 */
	public void renameEnumConstant(String oldEnumConstant, String newEnumConstant) {
		EnumConstantRenamer renamer = buildEnumConstantRenamer(oldEnumConstant, newEnumConstant);
		getStateObject().accept(renamer);
	}

	/**
	 * Renames a field from the given type.
	 *
	 * @param typeName The fully qualified name of the type that got one of its attributes renamed
	 * @param oldFieldName The current name of the attribute to rename
	 * @param newFieldName The new name of the attribute
	 */
	public void renameField(Class<?> type, String oldFieldName, String newFieldName) {
		renameField(type.getName(), oldFieldName, newFieldName);
	}

	/**
	 * Renames a field from the given type.
	 *
	 * @param typeName The fully qualified name of the type that got one of its attributes renamed
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
		FieldNameRenamer renamer = buildFieldNameRenamer(typeName, oldFieldName, newFieldName);
		getStateObject().accept(renamer);
	}

	/**
	 * Renames a result variable name.
	 *
	 * @param oldVariableName The current identification variable name
	 * @param newVariableName The new name of the identification variable
	 */
	public void renameResultVariable(String oldVariableName, String newVariableName) {
		ResultVariableNameRenamer renamer = buildResultVariableNameRenamer(oldVariableName, newVariableName);
		getStateObject().accept(renamer);
	}

	/**
	 * Renames a variable name.
	 *
	 * @param oldVariableName The current identification variable name
	 * @param newVariableName The new name of the identification variable
	 */
	public void renameVariable(String oldVariableName, String newVariableName) {
		VariableNameRenamer renamer = buildVariableNameRenamer(oldVariableName, newVariableName);
		getStateObject().accept(renamer);
	}

	/**
	 * Sets the {@link IJPQLQueryFormatter} that creates an accurate representation of the {@link
	 * StateObject}, i.e. that output the JPQL query with the case used for the JPQL identifier.
	 *
	 * @param jpqlQueryFormatter This formatter converts a {@link StateObject} representation of the
	 * JPQL query into a string representation
	 */
	public void setFormatter(IJPQLQueryFormatter jpqlQueryFormatter) {
		this.jpqlQueryFormatter = jpqlQueryFormatter;
	}

	/**
	 * Sets whether the parsing system should be tolerant, meaning if it should try to parse invalid
	 * or incomplete queries.
	 *
	 * @param tolerant <code>true</code> if the JPQL query or fragment should be parsed with tolerance;
	 * <code>false</code> otherwise
	 */
	public void setTolerant(boolean tolerant) {
		this.tolerant = tolerant;
	}

	/**
	 * Returns a string representation of the {@link org.eclipse.persistence.jpa.jpql.model.query.StateObject
	 * StateObject}, which represents exactly how the JPQL query was parsed but include the
	 * refactoring changes.
	 *
	 * @return The string representation of the JPQL query
	 */
	public String toActualText() {
		return getFormatter().toString(getStateObject());
	}

	/**
	 * This visitor renames a fully qualified class name.
	 */
	protected static class ClassNameRenamer extends AbstractTraverseChildrenVisitor {

		/**
		 * The {@link StateObjectUpdater} that updates the class name when notified.
		 */
		protected StateObjectUpdater<ConstructorExpressionStateObject> constructorUpdater;

		/**
		 * The current name of the class to rename.
		 */
		protected final String newClassName;

		/**
		 * The new name of the class.
		 */
		protected final String oldClassName;

		/**
		 * The {@link StateObjectUpdater} that updates the state field path expression when notified.
		 */
		protected StateObjectUpdater<StateFieldPathExpressionStateObject> pathExpressionUpdater;

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

		protected StateObjectUpdater<ConstructorExpressionStateObject> buildConstructorUpdater() {
			return new StateObjectUpdater<ConstructorExpressionStateObject>() {
				public void update(ConstructorExpressionStateObject stateObject, CharSequence newValue) {
					stateObject.setClassName(newValue);
				}
			};
		}

		protected StateObjectUpdater<StateFieldPathExpressionStateObject> buildPathExpressionStateObjectUpdater() {
			return new StateObjectUpdater<StateFieldPathExpressionStateObject>() {
				public void update(StateFieldPathExpressionStateObject stateObject, CharSequence newValue) {
					stateObject.setPath(newValue);
				}
			};
		}

		protected StateObjectUpdater<ConstructorExpressionStateObject> constructorUpdater() {
			if (constructorUpdater == null ){
				constructorUpdater = buildConstructorUpdater();
			}
			return constructorUpdater;
		}

		protected StateObjectUpdater<StateFieldPathExpressionStateObject> pathExpressionUpdater() {
			if (pathExpressionUpdater == null ){
				pathExpressionUpdater = buildPathExpressionStateObjectUpdater();
			}
			return pathExpressionUpdater;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ConstructorExpressionStateObject stateObject) {
			visit(stateObject, stateObject.getClassName(), constructorUpdater());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(StateFieldPathExpressionStateObject stateObject) {
			visit(stateObject, stateObject.getPath(), pathExpressionUpdater());
		}

		/**
		 * Visits the given {@link StateObject} and if its value is the same as the old class name or
		 * if the value represents an inner class of that old class name, then the given {@link
		 * StateObjectUpdater} will be notified to replace the value.
		 *
		 * @param stateObject The {@link StateObject} that is being visited
		 * @param value The value to check if it's the old class name
		 * @param updater The {@link StateObjectUpdater} is notified when to replace the value
		 */
		protected <T extends StateObject> void visit(T stateObject,
		                                             String value,
		                                             StateObjectUpdater<T> updater) {

			if (oldClassName.equals(value)) {
				updater.update(stateObject, newClassName);
			}
			else {
				int index = value.lastIndexOf(AbstractExpression.DOT);

				// Traverse the value by retrieving a fragment up to the last dot (based on the index)
				for (; index > -1; index = value.lastIndexOf(AbstractExpression.DOT, index - 1)) {
					String fragment = value.substring(0, index);

					if (oldClassName.equals(fragment)) {
						StringBuilder newValue = new StringBuilder(newClassName);
						newValue.append(AbstractExpression.DOT);

						// The path does not end with '.'
						if (index + 1 < value.length()) {
							newValue.append(value.substring(index + 1));
						}

						updater.update(stateObject, newValue);
						break;
					}
					// No need to continue the search
					else if (fragment.length() < oldClassName.length()) {
						break;
					}
				}
			}
		}
	}

	/**
	 * This visitor renames an entity name. There are three possible {@link StateObject StateObjects}
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
	 * <code><b>SELECT CASE TYPE</b>(e) <b>WHEN</b> Exempt <b>THEN</b> 'Exempt'<p>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	 * <b>ELSE</b> 'NONE'
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	 * <b>END</b><p>
	 * <b>FROM</b> Employee e</code><br><br>
	 * </li>
	 * <li>
	 * {@link IdentificationVariableStateObject}:
	 * <i>Exempt</i> in<br><br>
	 * <code><b>SELECT</b> e<br>
	 * <b>FROM</b> Employee e</code><br>
	 * <b>WHERE TYPE</b>(e) <> Exempt
	 * </li>
	 * </ul>
	 */
	protected static class EntityNameRenamer extends AbstractTraverseChildrenVisitor {

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
		public void visit(AbstractSchemaNameStateObject stateObject) {
			if (oldEntityName.equals(stateObject.getText())) {
				stateObject.setText(newEntityName);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(EntityTypeLiteralStateObject stateObject) {
			if (oldEntityName.equals(stateObject.getText())) {
				stateObject.setText(newEntityName);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariableStateObject stateObject) {
			if (oldEntityName.equals(stateObject.getText())) {
				stateObject.setText(newEntityName);
			}
		}
	}

	/**
	 * This visitor renames an enum constant. An enum constant is represented by a path expression.
	 */
	protected static class EnumConstantRenamer extends AbstractTraverseChildrenVisitor {

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

		protected void renameEnumConstant(AbstractPathExpressionStateObject stateObject) {

			String path = stateObject.toString();

			if (path.equals(oldEnumConstant)) {

				IType type = managedTypeProvider.getTypeRepository().getEnumType(path);

				if (type != null) {
					stateObject.setPath(newEnumConstant);
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionValuedPathExpressionStateObject stateObject) {
			renameEnumConstant(stateObject);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(StateFieldPathExpressionStateObject stateObject) {
			renameEnumConstant(stateObject);
		}
	}

	/**
	 * This visitor renames any segment of a path expression.
	 */
	protected static class FieldNameRenamer extends AbstractTraverseChildrenVisitor {

		/**
		 * The external form of a provider that gives access to the JPA metadata.
		 */
		protected final IManagedTypeProvider managedTypeProvider;

		/**
		 * The new name of the attribute.
		 */
		protected final String newFieldName;

		/**
		 * The current name of the attribute to rename.
		 */
		protected final String oldFieldName;

		/**
		 * The fully qualified name of the type that got one of its attributes renamed.
		 */
		protected final String typeName;

		/**
		 * Creates a new <code>FieldNameRenamer</code>.
		 *
		 * @param typeName The fully qualified name of the type that got one of its attributes renamed
		 * @param oldFieldName The current name of the attribute to rename
		 * @param newFieldName The new name of the attribute
		 */
		public FieldNameRenamer(IManagedTypeProvider managedTypeProvider,
		                        String typeName,
		                        String oldFieldName,
		                        String newFieldName) {

			super();
			this.typeName            = typeName;
			this.oldFieldName        = oldFieldName;
			this.newFieldName        = newFieldName;
			this.managedTypeProvider = managedTypeProvider;
		}

		/**
		 * Performs the rename on the path expression.
		 *
		 * @param stateObject The {@link AbstractPathExpressionStateObject} being visited, which may
		 * have to have its path renamed
		 */
		protected void rename(AbstractPathExpressionStateObject stateObject) {

			// Now traverse the path expression after the identification variable
			for (int index = 1, count = stateObject.itemsSize(); index < count; index++) {

				// Retrieve the mapping for the path at the current position
				IMapping mapping = stateObject.getMapping(index);

				if (mapping == null) {
					break;
				}

				// The name matches
				if (mapping.getName().equals(oldFieldName)) {

					// Make sure the field name is from the right type
					String parentTypeName = mapping.getParent().getType().getName();

					if (parentTypeName.equals(typeName)) {
						stateObject.setPath(index, newFieldName);
						break;
					}
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionValuedPathExpressionStateObject stateObject) {
			rename(stateObject);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(StateFieldPathExpressionStateObject stateObject) {
			rename(stateObject);
		}
	}

	/**
	 * This visitor renames all the result variables found in the JPQL query.
	 */
	protected static class ResultVariableNameRenamer extends AbstractTraverseChildrenVisitor {

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
		public void visit(IdentificationVariableStateObject stateObject) {

			if (renameIdentificationVariable &&
			    oldVariableName.equalsIgnoreCase(stateObject.getText())) {

				stateObject.setText(newVariableName);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(JPQLQueryStateObject stateObject) {
			if (stateObject.hasQueryStatement()) {
				stateObject.getQueryStatement().accept(this);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(OrderByItemStateObject stateObject) {
			if (stateObject.hasStateObject()) {
				stateObject.getStateObject().accept(this);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ResultVariableStateObject stateObject) {
			if (oldVariableName.equalsIgnoreCase(stateObject.getResultVariable())) {
				stateObject.setResultVariable(newVariableName);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SelectStatementStateObject stateObject) {

			// Result variables defined in the SELECT clause
			stateObject.getSelectClause().accept(this);

			// Result variables used in the ORDER BY clause
			if (stateObject.hasOrderByClause()) {
				renameIdentificationVariable = true;
				try {
					stateObject.getOrderByClause().accept(this);
				}
				finally {
					renameIdentificationVariable = false;
				}
			}
		}
	}

	/**
	 * This interface is used to transparently push the new value into the {@link StateObject}.
	 */
	protected static interface StateObjectUpdater<T extends StateObject> {

		/**
		 * Updates the given {@link StateObject} by updating its state with the given new value.
		 *
		 * @param stateObject The {@link StateObject} to update
		 * @param newValue The new value to push into the object
		 */
		void update(T stateObject, CharSequence newValue);
	}

	/**
	 * This visitor renames all the identification variables found in the JPQL query.
	 */
	protected static class VariableNameRenamer extends AbstractTraverseChildrenVisitor {

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
		public void visit(CollectionValuedPathExpressionStateObject stateObject) {

			// Make sure the collection-valued path expression's identification variable was created
			stateObject.getIdentificationVariable();

			super.visit(stateObject);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariableStateObject stateObject) {
			if (oldVariableName.equalsIgnoreCase(stateObject.getText())) {
				stateObject.setText(newVariableName);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(StateFieldPathExpressionStateObject stateObject) {

			// Make sure the collection-valued path expression's identification variable was created
			stateObject.getIdentificationVariable();

			super.visit(stateObject);
		}
	}
}