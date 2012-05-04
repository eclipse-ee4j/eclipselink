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
package org.eclipse.persistence.tools.workbench.uitools;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

/**
 * Asynchronously display a copyright and image.
 * 
 * Usage:
 * 	SplashScreen ss = new SplashScreen("- 2001...", icon);
 * 	ss.start();
 * 	...initialize stuff...
 * 	ss.stop();
 */
public final class SplashScreen
	extends JWindow
	implements Runnable
{
	private Thread thread;
	private int timeout;

	// ********** constructor/initialization **********

	public SplashScreen(Frame owner, String copyright, Icon image, int timeout) {
		super(owner);
		this.timeout = timeout;
		this.initialize(copyright, image);
	}

	/**
	 * no timeout
	 */
	public SplashScreen(Frame owner, String copyright, Icon image) {
		this(owner, copyright, image, 0);
	}
	
	/**
	 * no copyright
	 */
	public SplashScreen(Frame owner, Icon image) {
		this(owner, "", image);
	}

	private void initialize(String copyright, Icon image) {
		// copyright - probably always displayed left-to-right, to match the image
		JLabel copyrightLabel = new JLabel(copyright, SwingConstants.LEFT);
		copyrightLabel.setFont(new Font("dialog", Font.PLAIN, 12));
		int copyrightWidth = copyrightLabel.getPreferredSize().width;
		int copyrightHeight = copyrightLabel.getPreferredSize().height;

		// image
		JLabel imageLabel = new JLabel(image);
		int imageWidth = image.getIconWidth();
		int imageHeight = image.getIconHeight();
		imageLabel.setBounds(0, 0, imageWidth, imageHeight);

		// place the copyright near the bottom left hand corner
		copyrightLabel.setBounds(10, imageHeight - 26, copyrightWidth, copyrightHeight);

		// add the components in the right order
		this.getContentPane().add(copyrightLabel);
		this.getContentPane().add(imageLabel);

		// center the image on the screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int positionX = (screenSize.width - imageWidth) / 2;
		int positionY = (screenSize.height - imageHeight) / 2;

		this.setBounds(positionX, positionY, imageWidth, imageHeight);
	}


	// ********** Runnable implementation **********

	/**
	 * Display the splash screen and wait for a timeout.
	 */
	public void run() {
        setVisible(true);
       
        synchronized(this) {
            try {
                wait(timeout);
            }
            catch(InterruptedException interruptedexception) { }
        }

        setVisible(false);
        dispose();
    }


	// ********** public API **********

	/**
	 * Start the thread that will display the splash screen.
	 */
	public synchronized void start() {
		if (this.thread != null) {
			throw new IllegalStateException("splash screen is already started");
		}
		this.thread = new Thread(this, "Splash Screen");
		this.thread.start();
	}

	/**
	 * Hide the splash screen by interrupting the thread.
	 */
	public synchronized void stop() {
		if (this.thread == null) {
			throw new IllegalStateException("splash screen is not started");
		}
		if (this.thread.isAlive()) {
			this.thread.interrupt();
		}
	}

}
