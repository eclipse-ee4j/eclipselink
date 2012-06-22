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
package org.eclipse.persistence.tools.workbench.uitools;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.utility.SynchronizedBoolean;
import org.eclipse.persistence.tools.workbench.utility.iterators.ArrayIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.FilteringIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;


/**
 * This is a simple development tool for browsing the current set
 * of Threads and ThreadGroups.
 * 
 * NB: This browser is primarily for development-time use only. It would
 * need to be refactored for use by end-users (strings would need to
 * be "externalized", behavior made more configurable, etc.).
 */
public class ThreadBrowser {
	private DefaultTreeModel treeModel;
	private JTree tree;	// we want to set the "expansion" state
	private SynchronizedBoolean synchronizedAutoRefresh;
	private Thread autoRefreshThread;
	private JFrame browser;

	private static final ThreadGroup[] EMPTY_GROUPS = new ThreadGroup[0];
	private static final Comparator THREAD_GROUP_COMPARATOR  =
		new Comparator() {
			public int compare(Object o1, Object o2) {
				return Collator.getInstance().compare(((ThreadGroup) o1).getName(), ((ThreadGroup) o2).getName());
			}
		};

	private static final Thread[] EMPTY_THREADS = new Thread[0];
	private static final Comparator THREAD_COMPARATOR  =
		new Comparator() {
			public int compare(Object o1, Object o2) {
				return Collator.getInstance().compare(((Thread) o1).getName(), ((Thread) o2).getName());
			}
		};



	public static void main(String[] args) throws Exception {
		new ThreadBrowser().exec(args);
	}

	public ThreadBrowser() {
		super();
		this.initialize();
	}


	// ********** initialization **********

	private void initialize() {
		this.treeModel = this.buildTreeModel();
		this.tree = this.buildTree();
		this.expandAll();
		this.synchronizedAutoRefresh = new SynchronizedBoolean(false);
		this.autoRefreshThread = this.buildAutoRefreshThread();
		this.autoRefreshThread.start();
		this.browser = this.buildBrowser();
	}

	private Thread buildAutoRefreshThread() {
		return new Thread(this.buildAutoRefreshRunnable(), "Thread Browser Auto-Refresh");
	}

	private Runnable buildAutoRefreshRunnable() {
		return new AutoRefreshRunnable(this.synchronizedAutoRefresh, this.buildRefreshRunnable());
	}

	private Runnable buildRefreshRunnable() {
		return new Runnable() {
			public void run() {
				ThreadBrowser.this.refresh();
			}
		};
	}

	private DefaultTreeModel buildTreeModel() {
		return new DefaultTreeModel(this.buildRootNode(), true);
	}

	private TreeNode buildRootNode() {
		return this.buildTreeNode(this.systemRootThreadGroup());
	}

	/**
	 * Return the system "root" thread group.
	 * All other thread groups and threads are under this thread group.
	 */
	private ThreadGroup systemRootThreadGroup() {
		ThreadGroup tg = Thread.currentThread().getThreadGroup();
		ThreadGroup parent = tg.getParent();
		while (parent != null) {
			tg = parent;
			parent = tg.getParent();
		}
		return tg;
	}

	/**
	 * Build and return a tree node for the specified thread group,
	 * with all its children populated.
	 */
	private DefaultMutableTreeNode buildTreeNode(ThreadGroup tg) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(tg, true);
		ThreadGroup[] subGroups = this.subGroupsOf(tg);
		for (int i = 0; i < subGroups.length; i++) {
			node.add(this.buildTreeNode(subGroups[i]));		// recurse
		}

		Thread[] threads = this.threadsOf(tg);
		for (int i = 0; i < threads.length; i++) {
			node.add(this.buildTreeNode(threads[i]));
		}

		return node;
	}

	/**
	 * Return the specified thread group's sub thread groups.
	 * @see ThreadGroup#enumerate(ThreadGroup[], boolean)
	 */
	private ThreadGroup[] subGroupsOf(ThreadGroup tg) {
		int estimatedSize = tg.activeGroupCount();
		if (estimatedSize == 0) {
			return EMPTY_GROUPS;
		}

		ThreadGroup[] subGroups;
		int subGroupsCopied;
		do {
			estimatedSize += estimatedSize;
			subGroups = new ThreadGroup[estimatedSize];
			subGroupsCopied = tg.enumerate(subGroups, false);
		} while (estimatedSize <= subGroupsCopied);
		ThreadGroup[] result = new ThreadGroup[subGroupsCopied];
		System.arraycopy(subGroups, 0, result, 0, subGroupsCopied);
		Arrays.sort(result, THREAD_GROUP_COMPARATOR);
		return result;
	}

	/**
	 * Return the specified thread group's threads.
	 * @see ThreadGroup#enumerate(Thread[], boolean)
	 */
	private Thread[] threadsOf(ThreadGroup tg) {
		int estimatedSize = tg.activeCount();
		if (estimatedSize == 0) {
			return EMPTY_THREADS;
		}

		Thread[] threads;
		int threadsCopied;
		do {
			estimatedSize += estimatedSize;
			threads = new Thread[estimatedSize];
			threadsCopied = tg.enumerate(threads, false);
		} while (estimatedSize <= threadsCopied);
		Thread[] result = new Thread[threadsCopied];
		System.arraycopy(threads, 0, result, 0, threadsCopied);
		Arrays.sort(result, THREAD_COMPARATOR);
		return result;
	}

	/**
	 * Build and return a tree node for the specified thread.
	 */
	private DefaultMutableTreeNode buildTreeNode(Thread thread) {
		return new DefaultMutableTreeNode(thread, false);
	}

	private JTree buildTree() {
		JTree result = new LocalTree(this.treeModel);
		result.setRootVisible(true);
		result.setShowsRootHandles(true);
		result.setRowHeight(20);
		result.setDoubleBuffered(true);
		return result;
	}

	private JFrame buildBrowser() {
		JFrame window = new JFrame("Thread Browser");
		window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		window.getContentPane().add(this.buildMainPanel(), "Center");
		window.setLocation(300, 300);
		window.setSize(400, 400);
		window.addWindowListener(this.buildWindowListener());
		return window;
	}

	private Component buildMainPanel() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(this.buildTreePane(), BorderLayout.CENTER);
		mainPanel.add(this.buildControlPanel(), BorderLayout.SOUTH);
		return mainPanel;
	}

	private Component buildTreePane() {
		return new JScrollPane(this.tree);
	}

	private Component buildControlPanel() {
		GridLayout grid = new GridLayout(1,0);
		grid.setHgap(5);
		JPanel controlPanel = new JPanel(grid);
		controlPanel.add(this.buildDumpSelectedGroupsButton());
		controlPanel.add(this.buildRefreshButton());
		controlPanel.add(this.buildAutoRefreshCheckBox());
		controlPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
		return controlPanel;
	}

	private WindowListener buildWindowListener() {
		return new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
				ThreadBrowser.this.interruptAutoRefreshThread();
			}
		};
	}


	// ********** buttons **********

	private JButton buildDumpSelectedGroupsButton() {
		return new JButton(this.buildDumpSelectedGroupsAction());
	}

	private Action buildDumpSelectedGroupsAction() {
		Action action = new AbstractAction("dump") {
			public void actionPerformed(ActionEvent event) {
				ThreadBrowser.this.dumpSelectedGroups();
			}
		};
		action.setEnabled(true);
		return action;
	}

	void dumpSelectedGroups() {
		for (Iterator stream = this.selectedThreadGroups(); stream.hasNext(); ) {
			((ThreadGroup) stream.next()).list();
		}
	}

	private JButton buildRefreshButton() {
		return new JButton(this.buildRefreshAction());
	}

	private Action buildRefreshAction() {
		Action action = new AbstractAction("refresh") {
			public void actionPerformed(ActionEvent event) {
				ThreadBrowser.this.refresh();
			}
		};
		action.setEnabled(true);
		return action;
	}

	void refresh() {
		this.hook();
		this.treeModel.setRoot(this.buildRootNode());
		this.expandAll();
	}

	/**
	 * this is just a hook where you can put something that
	 * you want to execute every few seconds or so  ~bjv
	 */
	private void hook() {
//		Window window = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();
//		System.out.println((window == null) ? "null" : window.getName());
	}

	private JCheckBox buildAutoRefreshCheckBox() {
		JCheckBox checkBox = new JCheckBox();
		checkBox.setText("auto refresh");
		checkBox.setHorizontalAlignment(SwingConstants.CENTER);
		PropertyValueModel autoRefreshHolder = new SimplePropertyValueModel(Boolean.valueOf(this.synchronizedAutoRefresh.getValue()));
		autoRefreshHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildAutoRefreshListener());
		checkBox.setModel(new CheckBoxModelAdapter(autoRefreshHolder));
		return checkBox;
	}

	private PropertyChangeListener buildAutoRefreshListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				ThreadBrowser.this.setAutoRefresh(((Boolean) evt.getNewValue()).booleanValue());
			}
		};
	}


	// ********** queries **********

	private Iterator threadGroupPaths() {
		Collection threadGroupPaths = new ArrayList();
		this.addThreadGroupPathsTo((DefaultMutableTreeNode) this.treeModel.getRoot(), threadGroupPaths);
		return threadGroupPaths.iterator();
	}

//	private Iterator selectedThreads() {
//		return new FilteringIterator(this.selectedValues()) {
//			protected boolean accept(Object next) {
//				return next instanceof Thread;
//			}
//		};
//	}
//
	private Iterator selectedThreadGroups() {
		return new FilteringIterator(this.selectedValues()) {
			protected boolean accept(Object next) {
				return next instanceof ThreadGroup;
			}
		};
	}

	private Iterator selectedValues() {
		return new TransformationIterator(this.selectedPaths()) {
			protected Object transform(Object next) {
				return ((DefaultMutableTreeNode) ((TreePath) next).getLastPathComponent()).getUserObject();
			}
		};
	}

	private Iterator selectedPaths() {
		TreePath[] paths = this.tree.getSelectionPaths();
		return (paths == null) ? NullIterator.instance() : new ArrayIterator(paths);
	}


	// ********** behavior **********

	private void exec(String[] args) throws Exception {
		this.browser.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.open();
	}

	public void open() {
		this.browser.setVisible(true);
	}

	private void expandAll() {
		for (Iterator stream = this.threadGroupPaths(); stream.hasNext(); ) {
			this.tree.expandPath((TreePath) stream.next());
		}
	}

	private void addThreadGroupPathsTo(DefaultMutableTreeNode node, Collection threadGroupPaths) {
		if (node.getUserObject() instanceof ThreadGroup) {
			threadGroupPaths.add(new TreePath(node.getPath()));
			for (Enumeration stream = node.children(); stream.hasMoreElements(); ) {
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) stream.nextElement();
				this.addThreadGroupPathsTo(child, threadGroupPaths);		// recurse
			}
		}
	}

	void setAutoRefresh(boolean autoRefresh) {
		this.synchronizedAutoRefresh.setValue(autoRefresh);
	}

	void interruptAutoRefreshThread() {
		this.autoRefreshThread.interrupt();
	}


	// ********** nested classes **********

	/**
	 * pretty up the nodes' text
	 */
	private static class LocalTree extends JTree {
		LocalTree(TreeModel treeModel) {
			super(treeModel);
		}

		public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			Object userObject = node.getUserObject();
			if (userObject instanceof Thread) {
				return this.convertToText((Thread) userObject);
			}
			return this.convertToText((ThreadGroup) userObject);
		}

		private String convertToText(Thread thread) {
			StringBuffer sb = new StringBuffer();
			if (thread == Thread.currentThread()) {
				sb.append('*');
			}
			sb.append(thread.getName());
			sb.append(" [priority=");
			sb.append(thread.getPriority());
			sb.append("]");
			if (thread.isDaemon()) {
				sb.append(" - daemon");
			}
			return sb.toString();
		}

		private String convertToText(ThreadGroup threadGroup) {
			StringBuffer sb = new StringBuffer();
			sb.append(threadGroup.getName());
			sb.append(" [max priority=");
			sb.append(threadGroup.getMaxPriority());
			sb.append("]");
			return sb.toString();
		}

	}

	/**
	 * This runnable will execute until interrupted. If the auto-refresh flag
	 * is set to true, this runnable will execute a "refresh runnable" every
	 * 5 seconds by dispatching it to the AWT Event Queue.
	 */
	private static class AutoRefreshRunnable implements Runnable {
		private SynchronizedBoolean synchronizedAutoRefresh;
		private Runnable refreshRunnable;

		AutoRefreshRunnable(SynchronizedBoolean synchronizedAutoRefresh, Runnable refreshRunnable) {
			super();
			this.synchronizedAutoRefresh = synchronizedAutoRefresh;
			this.refreshRunnable = refreshRunnable;
		}

		public void run() {
			while (true) {
				try {
					Thread.sleep(5000);
					this.synchronizedAutoRefresh.waitUntilTrue();
				} catch (InterruptedException ex) {
					// we were interrupted while sleeping or waiting, must be quittin' time
					return;
				}
				EventQueue.invokeLater(this.refreshRunnable);
			}
		}

	}

}
