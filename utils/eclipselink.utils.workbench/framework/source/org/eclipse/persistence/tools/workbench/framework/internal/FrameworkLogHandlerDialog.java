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
package org.eclipse.persistence.tools.workbench.framework.internal;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import org.eclipse.persistence.tools.workbench.framework.Application;
import org.eclipse.persistence.tools.workbench.framework.NodeManager;
import org.eclipse.persistence.tools.workbench.framework.action.ActionRepository;
import org.eclipse.persistence.tools.workbench.framework.app.NavigatorSelectionModel;
import org.eclipse.persistence.tools.workbench.framework.context.AbstractApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.AbstractWorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.help.HelpManager;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.TextAreaDialog;
import org.eclipse.persistence.tools.workbench.utility.iterators.SingleElementIterator;


/**
 * Hacked up dialog for notifying the user of a SEVERE log entry,
 * typically an unhandled exception.
 * 
 * The main "hacking" occurs when we go searching for a workbench
 * window to use as a "owner" window. Even then, an "owner" is not
 * required.
 */
final class FrameworkLogHandlerDialog
	extends AbstractDialog
{
	/** the log record that triggered this dialog */
	private LogRecord logRecord;

	/** this will localize the log record's message */
	private Formatter logFormatter;


	// ********** static methods **********

	/**
	 * Try to find a workbench window to use as a parent.
	 * This might return null....
	 */
	private static WorkbenchWindow findWindow() {
		WorkbenchWindow anyWindow = null;
		WorkbenchWindow focusWindow = null;
		Frame[] frames = Frame.getFrames();
		for (int i = 0; i < frames.length; i++) {
			Frame frame = frames[i];
			if (frame instanceof WorkbenchWindow) {
				WorkbenchWindow ww = (WorkbenchWindow) frame;
				if (ww.isFocusOwner()) {
					focusWindow = ww;
					break;
				}
				if (anyWindow == null) {
					anyWindow = ww;
				}
			}
		}
		return (focusWindow == null) ? anyWindow : focusWindow;
	}

	/**
	 * Hack together a workbench context to pass to the dialog.
	 */
	private static WorkbenchContext buildContext(FrameworkApplication application) {
		return new LocalWorkbenchContext(application, findWindow());
	}


	// ********** constructor **********

	/**
	 * Construct a dialog for the specified log record.
	 */
	public FrameworkLogHandlerDialog(FrameworkApplication application, LogRecord logRecord) {
		super(buildContext(application), application.getResourceRepository().getString("UNEXPECTED_ERROR"));
		this.logRecord = logRecord;
		this.logFormatter = new SimpleFormatter();
	}


	// ********** AbstractDialog implementation **********

	/**
	 * The main panel merely consists of a label or two.
	 * @see org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog#buildMainPanel()
	 */
	protected Component buildMainPanel() {
		JPanel mainPanel = new JPanel(new GridLayout(0, 1));

		JLabel messageLabel = new JLabel();
		messageLabel.setText(this.logFormatter.formatMessage(this.logRecord));
		messageLabel.setIcon(this.resourceRepository().getIcon("error"));
		messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		mainPanel.add(messageLabel);

		if (this.logRecord.getThrown() != null) {
			JTextArea textArea = new JTextArea(this.logRecord.getThrown().toString());
			textArea.setFont(messageLabel.getFont());
			textArea.setTabSize(2);
			mainPanel.add(new JScrollPane(textArea));
		}
		mainPanel.setPreferredSize(new Dimension(350, Math.min(mainPanel.getPreferredSize().height + 50, 150)));

		return mainPanel;
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog#helpTopicId()
	 */
	protected String helpTopicId() {
		return "dialog.unexpectedError";
	}

	/**
	 * No need for a Cancel button.
	 * @see org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog#cancelButtonIsVisible()
	 */
	protected boolean cancelButtonIsVisible() {
		return false;
	}

	/**
	 * Allow the user to see the stack trace, if appropriate.
	 * @see org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog#buildCustomActions()
	 */
	protected Iterator buildCustomActions() {
		if (this.logRecord.getThrown() == null) {
			return super.buildCustomActions();
		}
		Action stackTraceAction = this.buildStackTraceAction();
		return new SingleElementIterator(stackTraceAction);
	}

	private Action buildStackTraceAction() {
		return new AbstractAction(this.buildStackTraceText()) {
			public void actionPerformed(ActionEvent e) {
				FrameworkLogHandlerDialog.this.displayStackTrace();
			}
		};
	}

	protected String buildStackTraceText() {
		return this.resourceRepository().getString("STACK_TRACE");
	}

	/**
	 * display the stack trace with a text area dialog
	 */
	protected void displayStackTrace() {
		TextAreaDialog dialog =
			new TextAreaDialog(
				this.logRecord.getThrown(),
				this.helpTopicId(),
				this.getWorkbenchContext(),
				this
			);
		dialog.setTitle(this.buildStackTraceText());
		dialog.show();
	}


	/**
	 * Hack-o-rama: Since this dialog is instantiated outside the "normal"
	 * application context, we try to hack together a partial context.
	 */
	private static class LocalWorkbenchContext extends AbstractWorkbenchContext {
		FrameworkApplication application;
		private WorkbenchWindow window;
		private ApplicationContext applicationContext;
		
		LocalWorkbenchContext(FrameworkApplication application, WorkbenchWindow window) {
			super();
			this.application = application;
			this.window = window;
			this.applicationContext = buildApplicationContext();
		}

		private ApplicationContext buildApplicationContext() {
			return new AbstractApplicationContext() {
				public Application getApplication() {
					return LocalWorkbenchContext.this.application;
				}
				public HelpManager getHelpManager() {
					return LocalWorkbenchContext.this.application.getHelpManager();
				}
				public NodeManager getNodeManager() {
					return LocalWorkbenchContext.this.application.getNodeManager();
				}
				public Preferences getPreferences() {
					return LocalWorkbenchContext.this.application.getRootPreferences();
				}
				public ResourceRepository getResourceRepository() {
					return LocalWorkbenchContext.this.application.getResourceRepository();
				}
			};			
		}
		
		// ApplicationContext
		public Application getApplication() {
			return this.application;
		}
		public HelpManager getHelpManager() {
			return this.application.getHelpManager();
		}
		public NodeManager getNodeManager() {
			return this.application.getNodeManager();
		}
		public Preferences getPreferences() {
			return this.application.getRootPreferences();
		}
		public ResourceRepository getResourceRepository() {
			return this.application.getResourceRepository();
		}
		public ApplicationContext getApplicationContext() {
			return this.applicationContext;
		}
		
		// WorkbenchContext
		public ActionRepository getActionRepository() {
			throw new UnsupportedOperationException();
		}
		public Window getCurrentWindow() {
			// the window can be null...
			return this.window;
		}
		public NavigatorSelectionModel getNavigatorSelectionModel() {
			throw new UnsupportedOperationException();
		}
        public Component getPropertiesPage() {
            throw new UnsupportedOperationException();
        }
	}

}
