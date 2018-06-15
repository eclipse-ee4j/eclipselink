/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.tools.workbench.mappingsmodel.query;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

/**
 * @see StoredProcedurePropertiesPage
 * @version 1.1
 * @since 1.1
 * @author Les Davis
 */
public final class MWProcedureNamedOutputArgument extends MWAbstractProcedureArgument
{
    /**
     * Default constructor - for TopLink use only
     */
    @SuppressWarnings("unused")
      private MWProcedureNamedOutputArgument() {
          super();
      }

    MWProcedureNamedOutputArgument(MWProcedure procedure, String name) {
        super(procedure, name);
    }

    @Override
    public boolean isNamed(){
        return true;
    }

    @Override
    public boolean isNamedIn() {
        return false;
    }

    @Override
    public boolean isNamedOut() {
        return true;
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
        return false;
    }

    public static XMLDescriptor buildDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(MWProcedureNamedOutputArgument.class);
        descriptor.getInheritancePolicy().setParentClass(MWAbstractProcedureArgument.class);

        return descriptor;
    }

    protected void addRuntimeEclipseLinkArgument(StoredProcedureCall call) {
        if (StringTools.stringIsEmpty(getFieldSubTypeName()) && StringTools.stringIsEmpty(getFieldName())) {
            if (!StringTools.stringIsEmpty(getFieldJavaClassName())) {
                call.addNamedOutputArgument(getArgumentName(), getArgumentName(), ClassTools.classForName(getFieldJavaClassName()));
            } else {
                call.addNamedOutputArgument(getArgumentName(), getArgumentName(), getFieldSqlTypeCode());
            }
        } else if (StringTools.stringIsEmpty(getFieldSubTypeName())) {
            call.addNamedOutputArgument(getArgumentName(), getFieldName(), getFieldSqlTypeCode());
        }  else {
            if (!StringTools.stringIsEmpty(getFieldJavaClassName())) {
                call.addNamedOutputArgument(getArgumentName(), getFieldName(), getFieldSqlTypeCode(), getFieldSubTypeName(), ClassTools.classForName(getFieldJavaClassName()));
            } else {
                call.addNamedOutputArgument(getArgumentName(), getFieldName(), getFieldSqlTypeCode(), getFieldSubTypeName());
            }
        }
    }

}
