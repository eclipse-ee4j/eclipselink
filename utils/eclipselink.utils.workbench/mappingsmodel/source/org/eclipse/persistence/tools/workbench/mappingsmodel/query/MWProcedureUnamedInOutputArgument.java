/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.tools.workbench.mappingsmodel.query;


import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

/**
 * @version 1.1
 * @since 1.1
 * @author Les Davis
 */
public final class MWProcedureUnamedInOutputArgument extends MWAbstractProcedureInOutputArgument
{
    /**
     * Default constructor - for TopLink use only
     */
    @SuppressWarnings("unused")
      private MWProcedureUnamedInOutputArgument() {
          super();
      }

    MWProcedureUnamedInOutputArgument(MWProcedure procedure) {
        super(procedure, null);
    }

    @Override
    public boolean isNamed() {
        return false;
    }

    @Override
    public boolean isNamedIn() {
        return false;
    }

    @Override
    public boolean isNamedOut() {
        return false;
    }

    @Override
    public boolean isNamedInOut() {
        return false;
    }

    @Override
    public boolean isUnnamedIn() {
        return false;
    }

    @Override
    public boolean isUnnamedOut() {
        return false;
    }

    @Override
    public boolean isUnnamedInOut() {
        return true;
    }

    public static XMLDescriptor buildDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(MWProcedureUnamedInOutputArgument.class);
        descriptor.getInheritancePolicy().setParentClass(MWAbstractProcedureInOutputArgument.class);

        return descriptor;
    }

    protected void addRuntimeEclipseLinkArgument(StoredProcedureCall call) {
        if (getPassType().equals(MWAbstractProcedureArgument.VALUE_TYPE)) {
            if (!StringTools.stringIsEmpty(getFieldJavaClassName())) {
                call.addUnamedInOutputArgumentValue(getArgumentValue(), getOutFieldName(), ClassTools.classForName(getFieldJavaClassName()));
            } else {
                call.addUnamedInOutputArgumentValue(getArgumentValue(), getOutFieldName(), null);
            }
        } else {
            if (StringTools.stringIsEmpty(getFieldSubTypeName()) && StringTools.stringIsEmpty(getOutFieldName())) {
                if (!StringTools.stringIsEmpty(getFieldJavaClassName())) {
                    call.addUnamedInOutputArgument(getFieldName(), ClassTools.classForName(getFieldJavaClassName()));
                } else {
                    call.addUnamedInOutputArgument(getFieldName());
                }
            } else if (StringTools.stringIsEmpty(getFieldSubTypeName())) {
                call.addUnamedInOutputArgument(getFieldName(), getOutFieldName(), getFieldSqlTypeCode());
            } else {
                if (!StringTools.stringIsEmpty(getFieldJavaClassName()) && !StringTools.stringIsEmpty(getNestedTypeFieldName())) {
                    call.addUnamedInOutputArgument(getFieldName(), getOutFieldName(), getFieldSqlTypeCode(), getFieldSubTypeName(), ClassTools.classForName(getFieldJavaClassName()), new DatabaseField(getNestedTypeFieldName()));
                } else if (!StringTools.stringIsEmpty(getFieldJavaClassName())) {
                    call.addUnamedInOutputArgument(getFieldName(), getOutFieldName(), getFieldSqlTypeCode(), getFieldSubTypeName(), ClassTools.classForName(getFieldJavaClassName()));
                } else {
                    call.addUnamedInOutputArgument(getFieldName(), getOutFieldName(), getFieldSqlTypeCode(), getFieldSubTypeName());
                }
            }
        }
    }

}
