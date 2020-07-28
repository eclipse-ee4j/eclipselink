/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.tools.model.query;

import java.io.IOException;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.parser.ConstructorExpression;
import org.eclipse.persistence.jpa.jpql.parser.ConstructorItemBNF;
import org.eclipse.persistence.jpa.jpql.tools.spi.IType;
import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * In the <code><b>SELECT</b></code> clause a constructor may be used in the <code><b>SELECT</b></code>
 * list to return one or more Java instances. The specified class is not required to be an entity or
 * to be mapped to the database. The constructor name must be fully qualified.
 *
 * <div><b>BNF:</b> <code>constructor_expression ::= NEW constructor_name(constructor_item {, constructor_item}*)</code><p></div>
 *
 * @see ConstructorExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused"}) // unused used for the import statement: see bug 330740
public class ConstructorExpressionStateObject extends AbstractListHolderStateObject<StateObject> {

    /**
     * The fully qualified class name of the Java object to instantiate.
     */
    private String className;

    /**
     * The actual type that was resolved based on the class name.
     */
    private IType type;

    /**
     * Notifies the class name property has changed.
     */
    public static final String CLASS_NAME_PROPERTY = "className";

    /**
     * Notify the list of {@link StateObject StateObjects} representing the constructor items.
     */
    public static final String CONSTRUCTOR_ITEMS_LIST = "constructorItems";

    /**
     * Creates a new <code>ConstructorExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public ConstructorExpressionStateObject(StateObject parent) {
        super(parent);
    }

    /**
     * Creates a new <code>ConstructorExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param type The fully qualified name of the Java class to instantiate
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public ConstructorExpressionStateObject(StateObject parent, Class<?> type) {
        this(parent, type.getName());
    }

    /**
     * Creates a new <code>ConstructorExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param type The Java class to instantiate
     * @param constructorItems The list of arguments
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public ConstructorExpressionStateObject(StateObject parent,
                                            Class<?> type,
                                            List<? extends StateObject> constructorItems) {
        super(parent, constructorItems);
        this.className = type.getName();
    }

    /**
     * Creates a new <code>ConstructorExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param type The Java class to instantiate
     * @param jpqlFragment The portion of the JPQL query that represents the constructor's arguments
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public ConstructorExpressionStateObject(StateObject parent,
                                            Class<?> type,
                                            String jpqlFragment) {
        this(parent, type);
        parse(jpqlFragment);
    }

    /**
     * Creates a new <code>ConstructorExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param className The fully qualified name of the Java class to instantiate
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public ConstructorExpressionStateObject(StateObject parent, String className) {
        super(parent);
        this.className = className;
    }

    /**
     * Creates a new <code>ConstructorExpressionStateObject</code>.
     *
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param className The fully qualified name of the Java class to instantiate
     * @param constructorItems The list of arguments
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public ConstructorExpressionStateObject(StateObject parent,
                                            String className,
                                            List<? extends StateObject> constructorItems) {
        super(parent, constructorItems);
        this.className = className;
    }

    /**
     * Creates a new <code>ConstructorExpressionStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @param className The fully qualified name of the Java class to instantiate
     * @param jpqlFragment The portion of the JPQL query that represents the constructor's arguments
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public ConstructorExpressionStateObject(StateObject parent,
                                            String className,
                                            String jpqlFragment) {
        super(parent);
        this.className = className;
        parse(jpqlFragment);
    }

    /**
     * {@inheritDoc}
     */
    public void accept(StateObjectVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Returns the fully qualified class name that will be used to retrieve the constructor.
     *
     * @return The fully qualified class name or an empty string if it is not defined
     */
    public String getClassName() {
        return className;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConstructorExpression getExpression() {
        return (ConstructorExpression) super.getExpression();
    }

    /**
     * Returns the actual {@link IType} that was resolved or <code>null</code> if it could not be resolved.
     *
     * @return The actual {@link IType}
     */
    public IType getType() {
        if (type == null) {
            type = resolveType();
        }
        return type;
    }

    @Override
    public boolean isEquivalent(StateObject stateObject) {

        if (super.isEquivalent(stateObject)) {
            ConstructorExpressionStateObject constructor = (ConstructorExpressionStateObject) stateObject;
            return ExpressionTools.valuesAreEqual(className, constructor.className) &&
                   areChildrenEquivalent(constructor);
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String listName() {
        return CONSTRUCTOR_ITEMS_LIST;
    }

    /**
     * Parses the given JPQL fragment, which represents the constructor's arguments.
     *
     * @param jpqlFragment The JPQL fragment, which represents either a single or multiple arguments
     */
    public void parse(String jpqlFragment) {
        List<StateObject> stateObjects = buildStateObjects(jpqlFragment, ConstructorItemBNF.ID);
        addItems(stateObjects);
    }

    /**
     * Resolves the actual {@link IType} based on the class name.
     *
     * @return The {@link IType} with the fully qualified class name
     */
    protected IType resolveType() {

        if (ExpressionTools.stringIsEmpty(className)) {
            return null;
        }

        return getType(className);
    }

    /**
     * Sets the fully qualified class name that will be used to retrieve the constructor.
     *
     * @param className The fully qualified class name
     */
    public void setClassName(CharSequence className) {
        type = null;
        setClassNameInternally(className);
    }

    /**
     * Sets the fully qualified class name that will be used to retrieve the constructor.
     *
     * @param type The type used to retrieve its fully qualified class name
     */
    public void setClassName(Class<?> type) {
        setClassName(type.getName());
    }

    /**
     * Sets the fully qualified class name that will be used to retrieve the constructor.
     *
     * @param className The fully qualified class name
     */
    protected void setClassNameInternally(CharSequence className) {
        String oldClassName = this.className;
        this.className = (className != null) ? className.toString() : null;
        firePropertyChanged(CLASS_NAME_PROPERTY, oldClassName, className);
    }

    /**
     * Keeps a reference of the {@link ConstructorExpression parsed object} object, which should only
     * be done when this object is instantiated during the conversion of a parsed JPQL query into
     * {@link StateObject StateObjects}.
     *
     * @param expression The {@link ConstructorExpression parsed object} representing a <code><b>NEW</b></code>
     * expression
     */
    public void setExpression(ConstructorExpression expression) {
        super.setExpression(expression);
    }

    /**
     * Sets the actual {@link IType} and updates the class name.
     *
     * @param type The new {@link IType}
     */
    public void setType(IType type) {
        this.type = type;
        setClassNameInternally((type != null) ? type.getName() : null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void toTextInternal(Appendable writer) throws IOException {

        // NEW
        writer.append(NEW);

        // Class name
        if (className != null) {
            writer.append(SPACE);
            writer.append(className);
        }

        writer.append(LEFT_PARENTHESIS);

        // Constructor items
        toStringItems(writer, true);

        writer.append(RIGHT_PARENTHESIS);
    }
}
