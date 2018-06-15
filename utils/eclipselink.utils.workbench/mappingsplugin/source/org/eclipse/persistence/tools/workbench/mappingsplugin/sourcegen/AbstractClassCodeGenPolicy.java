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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsplugin.sourcegen;

import java.util.Calendar;

import org.eclipse.persistence.tools.workbench.framework.Application;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.resources.StringRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregateMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassCodeGenPolicy;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

import org.eclipse.persistence.internal.codegen.ClassDefinition;

abstract class AbstractClassCodeGenPolicy implements MWClassCodeGenPolicy
{
    private ApplicationContext context;


    protected AbstractClassCodeGenPolicy(ApplicationContext context) {
        super();
        this.context = context;
    }

    public ClassDefinition classDefinition() {
        return getMWClass().classDefinition(this);
    }

    protected abstract MWClass getMWClass();

    public String classComment(MWClass mwClass)
    {
        String CR = StringTools.CR;
        String[] arguments = new String[] {application().getFullProductNameAndVersionNumber(), Calendar.getInstance().getTime().toString()};
        String comment = " ###  "
                         + stringRepository().getString("CLASS_COMMENT_FOR_CODE_GEN", arguments)
                         + "  ###";

        if (mwClass.zeroArgumentConstructor() == null)
            comment += CR + CR + stringRepository().getString("CLASS_COMMENT_FOR_CODE_GEN_NO_ZERO_ARG_CONSTRUCTOR", StringTools.CR);

        return comment;
    }
    public String emptyMethodBodyComment() {
        return    stringRepository().getString("EMPTY_METHOD_BODY_COMMENT");
    }

    public String collectionImplementationClassNotDeterminedComment(MWClassAttribute attribute, MWClass concreteValueType) {
        return stringRepository().getString("CODE_GEN_COMMENT_FOR_COLLECTION_IMPLEMENTATION_CLASS_NOT_DETERMINED",
                                                                     new Object[] {attribute.getName(), concreteValueType.shortName()});
    }

    public String oneToOneMappingThatControlsWritingOfPrimaryKeyComment(MWOneToOneMapping mapping) {
        return stringRepository().getString("CODE_GEN_COMMENT_FOR_ONE_TO_ONE_MAPPING_THAT_CONTROLS_WRITING_OF_PRIMARY_KEY",
                                                                            new Object[] {mapping.getName(), StringTools.CR});
    }


    public String aggregateMappingDoesNotAllowNullImplementationClassNotDeterminedComment() {
        return stringRepository().getString("CODE_GEN_COMMENT_FOR_AGGREGATE_MAPPING_THAT_DOES_NOT_ALLOW_NULL_IMPLEMENTATION_CLASS_NOT_DETERMINED");
    }

    public String aggregateMappingDoesNotAllowNullComment(MWAggregateMapping mapping) {
        return stringRepository().getString("CODE_GEN_COMMENT_FOR_AGGREGATE_MAPPING_THAT_DOES_NOT_ALLOW_NULL",
                                                                        new Object[] {mapping.getName()});
    }

    protected StringRepository stringRepository() {
        return this.context.getResourceRepository();
    }

    protected Application application() {
        return this.context.getApplication();
    }
}
