/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.tools.sessionconsole;

import java.util.*;
import javax.swing.*;
import java.net.URL;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.tools.profiler.*;
import org.eclipse.persistence.tools.beans.*;

public class ProfileBrowser extends JFrame {
    private JPanel ivjMainPanel = null;
    private ProfileBrowserPanel ivjProfileBrowserPanel = null;

    public ProfileBrowser() {
        super();
        initialize();
    }

    public ProfileBrowser(String title) {
        super(title);
    }

    /**
     * PUBLIC:
     * Open a browser on the profiler profiles.
     */
    public static void browseProfiler(SessionProfiler profiler) {
        browseProfiles(((PerformanceProfiler)profiler).getProfiles());
    }

    /**
     * PUBLIC:
     * Open a browser on the profiles.
     */
    public static void browseProfiles(List<Profile> profiles) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            ProfileBrowser aProfileBrowserFrame;
            aProfileBrowserFrame = new ProfileBrowser();
            aProfileBrowserFrame.setVisible(true);
            aProfileBrowserFrame.setProfiles(profiles);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of javax.swing.JPanel");
            exception.printStackTrace(System.out);
        }
    }

    /**
     * Center a component in the middle of the screen.
     */
    public static void centerComponent(java.awt.Component component) {
        java.awt.Dimension screenSize = 
            java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        java.awt.Dimension size = component.getSize();

        screenSize.height = screenSize.height / 2;
        screenSize.width = screenSize.width / 2;

        size.height = size.height / 2;
        size.width = size.width / 2;

        component.setLocation(screenSize.width - size.width, 
                              screenSize.height - size.height);
    }

    /**
     * Return the JFrameContentPane property value.
     * @return javax.swing.JPanel
     */
    private
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    javax.swing.JPanel getMainPanel() {
        if (ivjMainPanel == null) {
            try {
                ivjMainPanel = new javax.swing.JPanel();
                ivjMainPanel.setName("MainPanel");
                ivjMainPanel.setLayout(new java.awt.GridBagLayout());
                ivjMainPanel.setBackground(java.awt.SystemColor.control);

                java.awt.GridBagConstraints constraintsProfileBrowserPanel = 
                    new java.awt.GridBagConstraints();
                constraintsProfileBrowserPanel.gridx = 1;
                constraintsProfileBrowserPanel.gridy = 1;
                constraintsProfileBrowserPanel.fill = 
                        java.awt.GridBagConstraints.BOTH;
                constraintsProfileBrowserPanel.weightx = 1.0;
                constraintsProfileBrowserPanel.weighty = 1.0;
                constraintsProfileBrowserPanel.insets = 
                        new java.awt.Insets(2, 2, 2, 2);
                getMainPanel().add(getProfileBrowserPanel(), 
                                   constraintsProfileBrowserPanel);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjMainPanel;
    }

    /**
     * Return the ProfileBrowserPanel1 property value.
     * @return ProfileBrowserPanel
     */
    private
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    ProfileBrowserPanel getProfileBrowserPanel() {
        if (ivjProfileBrowserPanel == null) {
            try {
                ivjProfileBrowserPanel = 
                        new org.eclipse.persistence.tools.sessionconsole.ProfileBrowserPanel();
                ivjProfileBrowserPanel.setName("ProfileBrowserPanel");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjProfileBrowserPanel;
    }

    /**
     * Called whenever the part throws an exception.
     * @param exception java.lang.Throwable
     */
    private void handleException(Throwable exception) {
        MessageDialog.displayException(exception, this);
    }

    /**
     * Initialize the class.
     */
    private
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    void initialize() {
        try {
            // user code begin {1}
            // user code end
            setName("ProfileBrowserFrame");
            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            setSize(659, 465);
            setTitle("TopLink Profile Browser");
            URL iconURL = getClass().getResource("/tl_icon16.gif");
            if (iconURL != null) {
                setIconImage(new ImageIcon(iconURL).getImage());
            }
            setContentPane(getMainPanel());
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        // user code begin {2}
        centerComponent(this);
        getProfileBrowserPanel().setup();
        // user code end
    }

    public void setProfiles(List<Profile> profiles) {
        getProfileBrowserPanel().setProfiles(profiles);
    }
}
