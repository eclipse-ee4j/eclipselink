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
package org.eclipse.persistence.tools.workbench.mappingsmodel.meta;

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.utility.NameTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

import org.eclipse.persistence.internal.codegen.NonreflectiveMethodDefinition;

public abstract class MWMethodCodeGenPolicy {

    private volatile MWClassCodeGenPolicy classCodeGenPolicy;
    private volatile MWMethod method;

    private final static String ARRAY_SUFFIX = "Array";
    private final static String A_ARTICLE = "a";
    private final static String AN_ARTICLE = "an";


    MWMethodCodeGenPolicy(MWMethod method, MWClassCodeGenPolicy classCodeGenPolicy)
    {
        this.method = method;
        this.classCodeGenPolicy = classCodeGenPolicy;
    }

    MWClassCodeGenPolicy getClassCodeGenPolicy() {
        return this.classCodeGenPolicy;

    }

    MWMethod getMethod()
    {
        return this.method;
    }

    void insertArguments(NonreflectiveMethodDefinition methodDef)
    {
        for (Iterator stream = getMethod().methodParameters(); stream.hasNext(); )
            addArgument(methodDef, (MWMethodParameter) stream.next());
    }

    void insertMethodBody(NonreflectiveMethodDefinition methodDef)
    {
        methodDef.addLine(this.classCodeGenPolicy.emptyMethodBodyComment());

        if (! this.method.isConstructor() && ! this.method.getReturnType().isVoid())
            methodDef.addLine("return " + this.method.getReturnTypeDeclaration().defaultReturnValueString() + ";");
    }

    protected void addArgument(NonreflectiveMethodDefinition methodDef, MWMethodParameter argument)
    {
        methodDef.addArgument(argument.declaration(), uniqueParameterName(argument, methodDef));
    }

    protected String uniqueParameterName(MWMethodParameter methodParameter, NonreflectiveMethodDefinition methodDef) {
        String parmName = methodParameter.getType().shortName();
        StringBuffer sb = new StringBuffer(parmName.length() + 10);

        if (StringTools.charIsVowel(parmName.charAt(0))) {
            sb.append(AN_ARTICLE);
        } else {
            sb.append(A_ARTICLE);
        }

        StringTools.capitalizeOn(parmName, sb);

        if (methodParameter.getDimensionality() > 0) {
            sb.append(ARRAY_SUFFIX);
        }

        return NameTools.uniqueNameFor(sb.toString(), methodDef.argumentNames());
    }

}
