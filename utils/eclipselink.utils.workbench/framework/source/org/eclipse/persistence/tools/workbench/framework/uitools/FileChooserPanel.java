/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.framework.uitools;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;


/**
 * a panel with a labeled entry field and a Browse button that opens
 * a file chooser
 */
public class FileChooserPanel 
	extends AbstractPanel 
{
	/** the value must be a String */
	private PropertyValueModel filePathHolder;

	/** used by the file chooser when selecting a relative file path */
	private FileHolder fileChooserRootFileHolder;
	
	/** used by the file chooser to filter files */
	private FileFilter fileChooserFileFilter;
	
	/** used by the file chooser as a starting point */
	private FileHolder fileChooserDefaultDirectoryHolder;

	private static final long serialVersionUID = 1L;


	// **************** Constructors / Initialization *************************
	
	public FileChooserPanel(ApplicationContext context,
									PropertyValueModel filePathHolder,
									String labelKey) {
		this(context, filePathHolder, labelKey, null, JFileChooser.FILES_ONLY, false);
	}
	
	public FileChooserPanel(ApplicationContext context,
									PropertyValueModel filePathHolder,
									String labelKey,
									boolean topAlignment) {
		this(context, filePathHolder, labelKey, null, JFileChooser.FILES_ONLY, topAlignment);
	}

	public FileChooserPanel(ApplicationContext context,
									PropertyValueModel filePathHolder,
									String labelKey,
									String browseButtonKey) {
		this(context, filePathHolder, labelKey, browseButtonKey, JFileChooser.FILES_ONLY, false);
	}

	public FileChooserPanel(ApplicationContext context,
									PropertyValueModel filePathHolder,
									String labelKey,
									String browseButtonKey,
									boolean topAlignment) {
		this(context, filePathHolder, labelKey, browseButtonKey, JFileChooser.FILES_ONLY, topAlignment);
	}

	/**        
	 * fileSelectionMode options:
	 * 		 JFileChooser.FILES_ONLY
	 * 		 JFileChooser.DIRECTORIES_ONLY
	 * 		 JFileChooser.FILES_AND_DIRECTORIES
	 * 
	 * @see JFileChooser.#setFileSelectionMode
	*/
 	public FileChooserPanel(ApplicationContext context,
									PropertyValueModel filePathHolder,
									String labelKey,
									int fileSelectionMode) {
		this(context, filePathHolder, labelKey, null, fileSelectionMode);
	}
	
 	public FileChooserPanel(ApplicationContext context,
									PropertyValueModel filePathHolder,
									String labelKey,
									int fileSelectionMode,
									boolean topAlignment) {
		this(context, filePathHolder, labelKey, null, fileSelectionMode, topAlignment);
	}

 	public FileChooserPanel(ApplicationContext context,
									PropertyValueModel filePathHolder,
									String labelKey,
									String browseButtonKey,
									int fileSelectionMode) {
		this(context, filePathHolder, labelKey, null, fileSelectionMode, false);
	}

 	public FileChooserPanel(ApplicationContext context,
									PropertyValueModel filePathHolder,
									String labelKey,
									String browseButtonKey,
									int fileSelectionMode,
									boolean topAlignment) {
		super(context);
		this.filePathHolder = filePathHolder;
		this.fileChooserRootFileHolder = new SimpleFileHolder();
		this.fileChooserDefaultDirectoryHolder = new SimpleFileHolder();
		this.initialize(labelKey, browseButtonKey, fileSelectionMode, topAlignment);
	}

 	private void initialize(String labelKey, String browseButtonKey, int fileSelectionMode, boolean topAlignment) {

		GridBagConstraints constraints = new GridBagConstraints();
		boolean labelVisible = labelVisible();

		// File label
		JLabel fileLabel = this.buildLabel(labelKey);
		fileLabel.setVisible(labelVisible);

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = topAlignment ? 2 : 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		this.add(fileLabel, constraints);
		this.addAlignLeft(fileLabel);

		// File text field
		JTextField fileTextField = new JTextField(new DocumentAdapter(this.filePathHolder), null, 1);

		constraints.gridx      = topAlignment ? 0 : 1;
		constraints.gridy      = topAlignment ? 1 : 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(topAlignment ? 1 : 0, topAlignment ? 0 : labelVisible ? 5 : 0, 0, 0);

		this.add(fileTextField, constraints);
		fileLabel.setLabelFor(fileTextField);
		
		// File chooser button
	 	JButton fileChooserButton = this.buildFileChooserButton(browseButtonKey, fileSelectionMode);
		SwingComponentFactory.updateButtonAccessibleName(fileLabel, fileChooserButton);

	 	constraints.gridx      = topAlignment ? 1 : 2;
		constraints.gridy      = topAlignment ? 1 : 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(topAlignment ? 1 : 0, 5, 0, 0);

		this.add(fileChooserButton, constraints);
		this.addAlignRight(fileChooserButton);
	}

	protected JButton buildFileChooserButton(String browseButtonKey, int fileSelectionMode) {
		String buttonText = (browseButtonKey != null) ? browseButtonKey : this.browseButtonKey();
		JButton fileChooserButton = this.buildButton(buttonText);
		fileChooserButton.addActionListener(this.buildFileActionListener(fileSelectionMode));
		return fileChooserButton;
	}

	protected String browseButtonKey() {
		return "FILE_CHOOSER_PANEL.BROWSE_BUTTON_TEXT";
	}

	protected boolean labelVisible() {
		return true;
	}

	private ActionListener buildFileActionListener(final int fileSelectionMode) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileChooserPanel.this.promptForFile(fileSelectionMode);
			}
		};
	}
	
	
	// ********** public API **********
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		Component[] components = this.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(enabled);
		}
	}

	public void requestFocus() {
		this.getComponent(1).requestFocus(); // 1 for the text field
	}
	
	/**
	 * used for file chooser relative path;
	 * this can be a read-only file holder - we won't try to write to it
	 */
	public void setFileChooserRootFileHolder(FileHolder fileChooserRootFileHolder) {
		if (fileChooserRootFileHolder == null) {
			throw new NullPointerException();
		}
		this.fileChooserRootFileHolder = fileChooserRootFileHolder;
	}
	
	public void setFileChooserFileFilter(FileFilter fileChooserFileFilter) {
		this.fileChooserFileFilter = fileChooserFileFilter;
	}
	
	public void setFileChooserDefaultDirectoryHolder(FileHolder fileChooserDefaultDirectoryHolder) {
		if (fileChooserDefaultDirectoryHolder == null) {
			throw new NullPointerException();
		}
		this.fileChooserDefaultDirectoryHolder = fileChooserDefaultDirectoryHolder;
	}


	// ********** internal methods **********
	
	private String getFilePath() {
		return (String) this.filePathHolder.getValue();
	}

	protected File getFileChooserDefaultDirectory() {
		return this.fileChooserDefaultDirectoryHolder.getFile();
	}

	private void setFileChooserDefaultDirectory(File directory) {
		this.fileChooserDefaultDirectoryHolder.setFile(directory);
	}

	private File getFile() {
		String filePath = this.getFilePath();

		if ((filePath == null) || (filePath.length() == 0)) {
			return null;
		}

		return new File(filePath);
	}
	
	private void setFilePath(String filePath) {
		this.filePathHolder.setValue(filePath);
	}

	void promptForFile(int fileSelectionMode) {
		Window window = SwingUtilities.getWindowAncestor(this);
		File file = this.getFile();

		FileChooser fileChooser = new FileChooser(this.getFileChooserDefaultDirectory(), this.fileChooserRootFileHolder.getFile());
		fileChooser.setFileSelectionMode(fileSelectionMode);
		fileChooser.setSelectedFile(file);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileFilter(this.fileChooserFileFilter);
		if (fileChooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
			this.setFilePath(fileChooser.getSelectedFile().getPath());
			this.setFileChooserDefaultDirectory(fileChooser.getCurrentDirectory());
		}
	}


	// ********** client-implemented interface **********

	/**
	 * Used to indirectly reference a File,
	 * typically retrieved from the node currently associated with
	 * a properties page (since the node can change over time).
	 */
	public interface FileHolder {
		/**
		 * Return the current file.
		 */
		File getFile();

		/**
		 * Set the current file.
		 * This operation is optional.
		 */
		void setFile(File file);
	}

	public class SimpleFileHolder implements FileHolder {
		private File file;
		public File getFile() {
			return this.file;
		}
		public void setFile(File file) {
			this.file = file;
		}
	}

}
