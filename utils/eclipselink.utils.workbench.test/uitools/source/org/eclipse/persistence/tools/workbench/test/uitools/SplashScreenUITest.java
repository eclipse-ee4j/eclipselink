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
package org.eclipse.persistence.tools.workbench.test.uitools;

import java.awt.Frame;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.eclipse.persistence.tools.workbench.uitools.SplashScreen;

/**
 * Display a splash screen until the user confirms the prompt.
 */
public class SplashScreenUITest {

    public static void main(String[] args) throws Exception {
        new SplashScreenUITest().exec(args);
    }

    public SplashScreenUITest() {
        super();
    }

    public void exec(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        Icon image = new ImageIcon(this.getClass().getResource("/test.splash.screen.gif"));
        SplashScreen ss = new SplashScreen(new Frame(), image);
        ss.start();
        JOptionPane.showMessageDialog(ss, "Press OK to close the splash screen.");
        ss.stop();
        System.exit(0);
    }

}
