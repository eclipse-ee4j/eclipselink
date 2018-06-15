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
package org.eclipse.persistence.tools.workbench.test.uitools.app.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import javax.swing.ComboBoxModel;
import javax.swing.ListModel;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.uitools.app.SynchronizedList;
import org.eclipse.persistence.tools.workbench.uitools.SimpleDisplayable;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;

public class ComboBoxModelAdapterTests extends TestCase {

    public static Test suite() {
        return new TestSuite(ComboBoxModelAdapterTests.class);
    }

    public ComboBoxModelAdapterTests(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        // nothing yet...
    }

    protected void tearDown() throws Exception {
        // nothing yet...
        super.tearDown();
    }

    public void testHasListeners() throws Exception {
        SimpleListValueModel listHolder = this.buildListHolder();
        assertFalse(listHolder.hasAnyListChangeListeners(ValueModel.VALUE));
        SimplePropertyValueModel selectionHolder = new SimplePropertyValueModel(((ListIterator) listHolder.getValue()).next());
        assertFalse(selectionHolder.hasAnyPropertyChangeListeners(ValueModel.VALUE));

        ComboBoxModel comboBoxModel = new ComboBoxModelAdapter(listHolder, selectionHolder);
        assertFalse(listHolder.hasAnyListChangeListeners(ValueModel.VALUE));
        assertFalse(selectionHolder.hasAnyPropertyChangeListeners(ValueModel.VALUE));
        this.verifyHasNoListeners(comboBoxModel);

        SynchronizedList synchList = new SynchronizedList(comboBoxModel);
        PropertyChangeListener selectionListener = this.buildSelectionListener();
        selectionHolder.addPropertyChangeListener(ValueModel.VALUE, selectionListener);
        assertTrue(listHolder.hasAnyListChangeListeners(ValueModel.VALUE));
        assertTrue(selectionHolder.hasAnyPropertyChangeListeners(ValueModel.VALUE));
        this.verifyHasListeners(comboBoxModel);

        comboBoxModel.removeListDataListener(synchList);
        selectionHolder.removePropertyChangeListener(ValueModel.VALUE, selectionListener);
        assertFalse(listHolder.hasAnyListChangeListeners(ValueModel.VALUE));
        assertFalse(selectionHolder.hasAnyPropertyChangeListeners(ValueModel.VALUE));
        this.verifyHasNoListeners(comboBoxModel);
    }

    private PropertyChangeListener buildSelectionListener() {
        return new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                // do nothing...
            }
        };
    }

    private void verifyHasNoListeners(ListModel listModel) throws Exception {
        boolean hasNoListeners = ((Boolean) ClassTools.invokeMethod(listModel, "hasNoListDataListeners")).booleanValue();
        assertTrue(hasNoListeners);
    }

    private void verifyHasListeners(ListModel listModel) throws Exception {
        boolean hasListeners = ((Boolean) ClassTools.invokeMethod(listModel, "hasListDataListeners")).booleanValue();
        assertTrue(hasListeners);
    }

    private SimpleListValueModel buildListHolder() {
        return new SimpleListValueModel(this.buildList());
    }

    private List buildList() {
        List list = new ArrayList();
        this.populateCollection(list);
        return list;
    }

    private void populateCollection(Collection c) {
        c.add(new SimpleDisplayable("foo"));
        c.add(new SimpleDisplayable("bar"));
        c.add(new SimpleDisplayable("baz"));
        c.add(new SimpleDisplayable("joo"));
        c.add(new SimpleDisplayable("jar"));
        c.add(new SimpleDisplayable("jaz"));
    }

}
