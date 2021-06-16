/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.beans;

import java.io.*;
import javax.swing.*;

/**
 * This class can be used to wrapper a window as a stream.
 */
public class TextAreaOutputStream extends OutputStream {
    protected JTextArea text;
    protected boolean shouldAutoScroll;

    public TextAreaOutputStream(JTextArea text) {
        this(text, false);
    }

    public TextAreaOutputStream(JTextArea text, boolean shouldAutoScroll) {
        this.text = text;
        this.shouldAutoScroll = shouldAutoScroll;
    }

    public JTextArea getText() {
        return text;
    }

    /**
     * Append the char to the text area.
     */
    public void scrollToEnd() {
        if (shouldAutoScroll()) {
            if (getText().getParent() instanceof JViewport) {
                int max =
                    ((JScrollPane)(((JViewport)getText().getParent()).getParent())).getVerticalScrollBar().getMaximum();
                ((JScrollPane)(((JViewport)getText().getParent()).getParent())).getVerticalScrollBar().setValue(max);
            }
        }
    }

    /**
     * JDK 1.2.2 has a auto scroll problem when threads are used.
     * This can be used to fix it.
     */
    public void setShouldAutoScroll(boolean shouldAutoScroll) {
        this.shouldAutoScroll = shouldAutoScroll;
    }

    public void setText(JTextArea text) {
        this.text = text;
    }

    /**
     * JDK 1.2.2 has a auto scroll problem when threads are used.
     * This can be used to fix it.
     */
    public boolean shouldAutoScroll() {
        return shouldAutoScroll;
    }

    /**
     * Append the char to the text area.
     */
    public void write(byte[] bytes) {
        getText().append(new String(bytes));
        scrollToEnd();
    }

    /**
     * Append the char to the text area.
     */
    public void write(byte[] bytes, int offset, int length) {
        byte[] buffer = new byte[length];
        System.arraycopy(bytes, offset, buffer, 0, length);
        getText().append(new String(buffer));
        scrollToEnd();
    }

    /**
     * Append the char to the text area.
     */
    public void write(int b) {
        byte[] bytes = new byte[1];
        bytes[0] = (byte)b;
        getText().append(new String(bytes));
        scrollToEnd();
    }
}
