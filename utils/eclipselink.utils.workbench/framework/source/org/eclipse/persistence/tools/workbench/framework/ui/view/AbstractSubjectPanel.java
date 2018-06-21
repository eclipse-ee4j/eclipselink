/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.framework.ui.view;

import java.awt.GridBagLayout;
import java.awt.LayoutManager;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;


public abstract class AbstractSubjectPanel extends AbstractPanel {

    /**
     * Holds the subject for the panel.
     */
    private ValueModel subjectHolder;

    /**
     * Default layout is GridBagLayout
     */
    public AbstractSubjectPanel(ValueModel subjectHolder, ApplicationContext context) {
        this(new GridBagLayout(), subjectHolder, context);
    }

    public AbstractSubjectPanel(LayoutManager layoutManager, ValueModel subjectHolder, ApplicationContext context) {
        super(layoutManager, context);
        this.initialize(subjectHolder);
        this.initializeLayout();
    }

    /**
     * Default layout is GridBagLayout
     */
    public AbstractSubjectPanel(ValueModel subjectHolder, WorkbenchContextHolder contextHolder) {
        this(new GridBagLayout(), subjectHolder, contextHolder);
    }



    public AbstractSubjectPanel(LayoutManager layoutManager, ValueModel subjectHolder, WorkbenchContextHolder contextHolder) {
        super(layoutManager, contextHolder);
        this.initialize(subjectHolder);
        this.initializeLayout();
    }


    protected void initialize(ValueModel sh) {
        this.subjectHolder = sh;
    }

    /**
     * Subclasses should implement this abstract method and build
     * the appropriate components and add them to this properties page
     * by calling the various add(Component) methods inherited from JPanel.
     */
    protected abstract void initializeLayout();


    protected ValueModel getSubjectHolder() {
        return this.subjectHolder;
    }

    public Object subject() {
        return this.subjectHolder.getValue();
    }

}
