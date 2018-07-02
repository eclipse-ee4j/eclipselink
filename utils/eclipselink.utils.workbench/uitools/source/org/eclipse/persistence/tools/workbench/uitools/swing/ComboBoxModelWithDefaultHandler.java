/*
 * Copyright (c) 2007, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.tools.workbench.uitools.swing;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;

/**
 * This <code>ComboBoxModel</code> simply verify the value to become the
 * selected and if it's the default value, then it will not be passed to the
 * delegate model.
 * <p>
 * This occurs when the combo box receive a focus lost event, usually the text
 * field fires the focus lost event but not always.
 *
 * @version 11.0.0
 * @since 11.0.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class ComboBoxModelWithDefaultHandler implements ComboBoxModel
{
    /**
     * The holder of the default value.
     */
    private ValueModel defaultValueHolder;

    /**
     * The delegate <code>ComboBoxModel</code>.
     */
    private ComboBoxModel delegate;

    /**
     * Creates a new <code>ComboBoxModelWithDefaultHandler</code>.
     *
     * @param delegate The delegate <code>ComboBoxModel</code>
     * @param defaultValueHolder The holder of the default value
     */
    public ComboBoxModelWithDefaultHandler(ComboBoxModel delegate,
                                           ValueModel defaultValueHolder)
    {
        super();

        this.delegate           = delegate;
        this.defaultValueHolder = defaultValueHolder;
    }

    /*
     * (non-Javadoc)
     */
    public void addListDataListener(ListDataListener listener)
    {
        delegate.addListDataListener(listener);
    }

    private Object defaultValue()
    {
        Object defaultValue = defaultValueHolder.getValue();

        if (defaultValue != null)
        {
            StringBuilder sb = new StringBuilder();
            sb.append('(');
            sb.append(defaultValue);
            sb.append(')');
            defaultValue = sb.toString();
        }
        else
        {
            defaultValue = "";
        }

        return defaultValue;
    }

    /*
     * (non-Javadoc)
     */
    public Object getElementAt(int index)
    {
        return delegate.getElementAt(index);
    }

    /*
     * (non-Javadoc)
     */
    public Object getSelectedItem()
    {
        return delegate.getSelectedItem();
    }

    /*
     * (non-Javadoc)
     */
    public int getSize()
    {
        return delegate.getSize();
    }

    /*
     * (non-Javadoc)
     */
    public void removeListDataListener(ListDataListener listener)
    {
        delegate.removeListDataListener(listener);
    }

    /*
     * (non-Javadoc)
     */
    public void setSelectedItem(Object item)
    {
        if (!defaultValue().equals(item))
        {
            delegate.setSelectedItem(item);
        }
    }
}
