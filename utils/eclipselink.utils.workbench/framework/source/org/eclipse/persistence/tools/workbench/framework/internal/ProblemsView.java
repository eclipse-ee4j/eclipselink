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
package org.eclipse.persistence.tools.workbench.framework.internal;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ListIterator;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;

import org.eclipse.persistence.tools.workbench.framework.app.ApplicationProblem;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationProblemContainer;
import org.eclipse.persistence.tools.workbench.framework.app.NavigatorSelectionModel;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.help.HelpManager;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.uitools.app.ListAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ReadOnlyPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ColumnAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.TableModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.DisplayableTableCellRenderer;


/**
 * This view lays out a list of the problems in a JPanel.
 * When the selected node changes, the newly-selected node is asked for
 * its problems model. The selected nodes must implement
 * the ProblemsNode interface.
 * 
 * @see org.eclipse.persistence.tools.workbench.framework.app.ProblemsNode
 */
final class ProblemsView {

	private ListValueModel applicationProblemsAdapter;
	private ListValueModel branchApplicationProblemsAdapter;

	/** The currently displayed problems. */
	private TableModelAdapter tableModel;
	private ListSelectionModel tableSelectionModel;

	/** we change the selection on double-click */
	private NavigatorSelectionModel navigatorSelectionModel;

	/** report, help */
	private Action problemReportAction;
	JPopupMenu popupMenu;
	private MouseListener popupMenuMouseListener;

	/** F1 */
	private KeyListener keyListener;

	/** The panel holding the current problems table. */
	private JPanel component;


	// ********** constructors/initialization **********

	ProblemsView(ValueModel appProblemContainerHolder, NavigatorSelectionModel navigatorSelectionModel, WorkbenchContext context) {
		super();
		this.navigatorSelectionModel = navigatorSelectionModel;
		this.initialize(appProblemContainerHolder, context);
	}

	private void initialize(ValueModel appProblemContainerHolder, WorkbenchContext context) {
		if (appProblemContainerHolder == null) {
			throw new NullPointerException();
		}

		this.applicationProblemsAdapter = this.buildApplicationProblemsAdapter(appProblemContainerHolder);
		this.branchApplicationProblemsAdapter = this.buildBranchApplicationProblemsAdapter(appProblemContainerHolder);

		ResourceRepository resourceRepository = context.getApplicationContext().getResourceRepository();
		this.tableModel = new TableModelAdapter(this.branchApplicationProblemsAdapter, this.buildColumnAdapter(resourceRepository));
		this.tableSelectionModel = new DefaultListSelectionModel();
		this.tableSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		this.problemReportAction = new ProblemReportAction(appProblemContainerHolder, context);
		this.popupMenu = this.buildPopupMenu(context);
		this.popupMenuMouseListener = this.buildPopupMenuMouseListener();

		this.keyListener = this.buildKeyListener(context.getApplicationContext().getHelpManager());

		this.component = this.buildComponent(resourceRepository);
	}

	private ListValueModel buildApplicationProblemsAdapter(ValueModel appProblemContainerHolder) {
		return new ListAspectAdapter(appProblemContainerHolder, ApplicationProblemContainer.APPLICATION_PROBLEMS_LIST) {
			protected ListIterator getValueFromSubject() {
				return ((ApplicationProblemContainer) this.subject).applicationProblems();
			}
			protected int sizeFromSubject() {
				return ((ApplicationProblemContainer) this.subject).applicationProblemsSize();
			}
		};
	}

	private ListValueModel buildBranchApplicationProblemsAdapter(ValueModel appProblemContainerHolder) {
		return new ListAspectAdapter(appProblemContainerHolder, ApplicationProblemContainer.BRANCH_APPLICATION_PROBLEMS_LIST) {
			protected ListIterator getValueFromSubject() {
				return ((ApplicationProblemContainer) this.subject).branchApplicationProblems();
			}
			protected int sizeFromSubject() {
				return ((ApplicationProblemContainer) this.subject).branchApplicationProblemsSize();
			}
		};
	}

	private ColumnAdapter buildColumnAdapter(ResourceRepository resourceRepository) {
		return new ProblemColumnAdapter(resourceRepository);
	}

	private JPopupMenu buildPopupMenu(WorkbenchContext context) {
		JPopupMenu result = new JPopupMenu();
		result.add(this.problemReportAction);
		result.add(new ProblemsViewHelpAction(context));
		return result;
	}

	/**
	 * when F1 is pressed we will display the help topic
	 * for the source component
	 */
	private KeyListener buildKeyListener(final HelpManager helpManager) {
		return new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (( ! e.isConsumed()) && (e.getKeyCode() == KeyEvent.VK_F1)) {
					helpManager.showTopic("problemsPane");
					e.consume();
				}
			}
		};
	}

	private JPanel buildComponent(ResourceRepository resourceRepository) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEtchedBorder());
		panel.setMinimumSize(new Dimension(0, 0));

		JLabel label = new JLabel(resourceRepository.getString("PROBLEMS_LABEL"));
		label.setDisplayedMnemonic(resourceRepository.getMnemonic("PROBLEMS_LABEL"));
		label.setIcon(resourceRepository.getIcon("problems"));
		label.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(0, 0, 1, 0, panel.getBackground().brighter()),
					BorderFactory.createMatteBorder(0, 0, 1, 0, panel.getBackground().darker())
				),
				BorderFactory.createEmptyBorder(2, 2, 2, 2)
			)
		);
		label.setLabelFor(panel);
		panel.add(label, BorderLayout.PAGE_START);

		JTable problemsTable = this.buildTable();

		JScrollPane scrollPane = new JScrollPane(problemsTable);
		scrollPane.getViewport().setBackground(problemsTable.getBackground());
		scrollPane.setBorder(null);
		panel.add(scrollPane, BorderLayout.CENTER);
		
		panel.addMouseListener(this.popupMenuMouseListener);
		panel.addKeyListener(this.keyListener);
		
		return panel;
	}

	private JTable buildTable() {
		JTable table = new JTable(this.tableModel, null, this.tableSelectionModel);
		table.setDoubleBuffered(true);
		table.setRowHeight(20);

		TableColumn nodeColumn = table.getColumnModel().getColumn(ProblemColumnAdapter.NODE_COLUMN);
		nodeColumn.setCellRenderer(new DisplayableTableCellRenderer());
		nodeColumn.setPreferredWidth(100);

		table.getColumnModel().getColumn(ProblemColumnAdapter.CODE_COLUMN).setPreferredWidth(20);

		table.getColumnModel().getColumn(ProblemColumnAdapter.MESSAGE_COLUMN).setPreferredWidth(500);

		table.addMouseListener(this.buildTableMouseListener());
		table.addMouseListener(this.popupMenuMouseListener);
		table.addKeyListener(this.keyListener);

		return table;
	}

	/**
	 * handle double-clicking a problem in the table
	 */
	private MouseListener buildTableMouseListener() {
		return new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					ProblemsView.this.tableDoubleClicked();
				}
			}
		};
	}

	/**
	 * pop-up menu
	 */
	private MouseListener buildPopupMenuMouseListener() {
		return new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				this.handleMouseEvent(e);
			}
			public void mouseReleased(MouseEvent e) {
				this.handleMouseEvent(e);
			}
			private void handleMouseEvent(MouseEvent e) {
				if (e.isPopupTrigger()) {
					ProblemsView.this.popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		};
	}


	// ********** queries **********

	/**
	 * Return the component to be displayed in the workbench window.
	 */
	Component getComponent() {
		return this.component;
	}

	/**
	 * the problem report action is shared with the workbench window
	 */
	Action getProblemReportAction() {
		return this.problemReportAction;
	}


	// ********** behavior **********

	/**
	 * Toggle the display of "branch" and "exclusive" application problems.
	 */
	void toggle() {
		if (this.tableModel.getModel() == this.applicationProblemsAdapter) {
			this.tableModel.setModel(this.branchApplicationProblemsAdapter);
		} else {
			this.tableModel.setModel(this.applicationProblemsAdapter);
		}
	}

	void tableDoubleClicked() {
		int sel = this.tableSelectionModel.getMinSelectionIndex();
		ApplicationProblem ap = (ApplicationProblem) this.tableModel.getModel().getItem(sel);
		this.navigatorSelectionModel.setSelectedNode(ap.getSource());
	}

	/**
	 * This is called when the window containing the problems view is closed.
	 */
	void close() {
		// do nothing
	}


	// ********** member class **********

	private static class ProblemColumnAdapter implements ColumnAdapter {
		private ResourceRepository resourceRepository;

		static final int COLUMN_COUNT = 3;

		static final int NODE_COLUMN = 0;
		static final int CODE_COLUMN = 1;
		static final int MESSAGE_COLUMN = 2;

		private static final String[] COLUMN_NAMES = new String[] {
			"PROBLEM_NODE",
			"PROBLEM_CODE",
			"PROBLEM_MESSAGE"
		};

		ProblemColumnAdapter(ResourceRepository resourceRepository) {
			super();
			this.resourceRepository = resourceRepository;
		}

		public int getColumnCount() {
			return COLUMN_COUNT;
		}

		public String getColumnName(int index) {
			return this.resourceRepository.getString(COLUMN_NAMES[index]);
		}

		public Class getColumnClass(int index) {
			return Object.class;
		}

		public boolean isColumnEditable(int index) {
			return false;
		}

		public PropertyValueModel[] cellModels(Object subject) {
			ApplicationProblem problem = (ApplicationProblem) subject;
			PropertyValueModel[] result = new PropertyValueModel[COLUMN_COUNT];

			result[NODE_COLUMN]		= new ReadOnlyPropertyValueModel(problem.getSource());
			result[CODE_COLUMN]	= new ReadOnlyPropertyValueModel(problem.getMessageCode());
			result[MESSAGE_COLUMN]	= new ReadOnlyPropertyValueModel(problem.getMessage());

			return result;
		}

	}

}
