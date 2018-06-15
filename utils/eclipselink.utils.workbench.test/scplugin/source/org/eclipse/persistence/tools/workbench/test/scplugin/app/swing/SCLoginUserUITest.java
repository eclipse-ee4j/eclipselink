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
package org.eclipse.persistence.tools.workbench.test.scplugin.app.swing;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.LoginAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;

/**
 * UI test for verifying the SC Login User Name with a text field widget.
 * The subject is a Login, the property to config is USER_NAME_PROPERTY.
 */
public class SCLoginUserUITest extends SCDatabaseLoginUITest {

    private PropertyValueModel stringHolder;
    private Document documentModel;

    public static void main(String[] args) throws Exception {
        new SCLoginUserUITest().exec(args);
    }

    private SCLoginUserUITest() {
        super();
    }

    protected String windowTitle() {
        return "Enter the User Name:";
    }

    private void exec( String[] args) throws Exception {

        setUp();

        stringHolder = this.buildStringHolder( subjectHolder());

        documentModel = this.buildDocumentAdapter( stringHolder);

        this.openWindow();
    }

    private PropertyValueModel buildStringHolder( ValueModel subjectHolder) {

        return new PropertyAspectAdapter( subjectHolder, LoginAdapter.USER_NAME_PROPERTY) {
            protected Object getValueFromSubject() {
                return (( LoginAdapter)subject).getUserName();
            }
            protected void setValueOnSubject(Object value) {
                (( LoginAdapter)subject).setUserName(( String)value);
            }
        };
    }

    private Document buildDocumentAdapter( PropertyValueModel stringHolder) {

        return new DocumentAdapter( stringHolder);
    }

    protected Component buildPropertyTestingPanel() {

        JPanel taskListPanel = new JPanel( new GridLayout(1, 0));
        taskListPanel.add( this.buildTextField());
        taskListPanel.add( this.buildTextField());
        return taskListPanel;
    }

    private JTextField buildTextField() {

        JTextField textField = new JTextField( documentModel, null, 0);
        return textField;
    }

    protected void resetProperty() {

        subject().setUserName( "");
    }
}
