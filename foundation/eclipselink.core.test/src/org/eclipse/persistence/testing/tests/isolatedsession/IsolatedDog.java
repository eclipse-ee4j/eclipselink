/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.tests.isolatedsession;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class IsolatedDog {

    protected String id;
    protected String name;
    protected ValueHolderInterface bone;

    public IsolatedDog() {
        super();
        this.bone = new ValueHolder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IsolatedBone getBone() {
        return (IsolatedBone)bone.getValue();
    }

    public void setBone(IsolatedBone bone) {
        this.bone.setValue(bone);
    }

    public static void afterLoad(ClassDescriptor descriptor) {
        OneToOneMapping mapping = (OneToOneMapping)descriptor.getMappingForAttributeName("bone");
        Expression selectionCriteria = mapping.buildSelectionCriteria();
        ExpressionBuilder builder = new ExpressionBuilder();
        mapping.setSelectionCriteria(selectionCriteria.and(builder.get("deleted").equal("N")));
    }

    public static TableDefinition buildISOLATEDDOGTable() {
        TableDefinition tabledefinition = new TableDefinition();

        // SECTION: TABLE
        tabledefinition.setName("ISOLATED_DOG");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        tabledefinition.addField(field);

        // SECTION: FIELD
        FieldDefinition field2 = new FieldDefinition();
        field2.setName("NAME");
        field2.setTypeName("VARCHAR");
        field2.setSize(100);
        field2.setShouldAllowNull(false);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        tabledefinition.addField(field2);

        return tabledefinition;
    }

    public static IsolatedDog buildIsolatedDogExample1() {
        IsolatedDog dog = new IsolatedDog();
        dog.setId("100");
        dog.setName("Fido");

        IsolatedBone bone = new IsolatedBone();
        bone.setColor("White");
        bone.setId("200");
        bone.setDeleted("N");

        bone.setOwner(dog);
        dog.setBone(bone);

        return dog;
    }

    public String toString() {
        return getClass().getSimpleName() + " [" + System.identityHashCode(this) + "]";
    }

}
