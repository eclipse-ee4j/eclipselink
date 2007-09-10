/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.oxm.schema.model;

public class AnyAttribute {
    public static final String LAX = "lax";
    private String processContents;

    public AnyAttribute() {
    }

    public void setProcessContents(String processContents) {
        this.processContents = processContents;
    }

    public String getProcessContents() {
        return processContents;
    }
}