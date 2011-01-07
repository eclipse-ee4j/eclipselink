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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;

import org.eclipse.persistence.tools.workbench.utility.io.Pipe;


/**
 * A console builds a simple window that will display all the text written
 * to two streams: "out" and "err". Once a console is constructed the
 * standard system streams can be redirected to the console's streams:
 * 
 * 	Console console = new Console();
 * 	System.setOut(new PrintStream(console.getOutStream(), true));	// true = auto-flush
 * 	System.setErr(new PrintStream(console.getErrStream(), true));	// true = auto-flush
 * 
 * The console window can be opened explicitly:
 * 
 * 	console.open();
 * 
 * or it will open automatically whenever text is written to either of
 * the two streams.
 * 
 * NB: This console is primarily for development-time use only. It would
 * need to be refactored for use by end-users (strings would need to
 * be "externalized", behavior made more configurable, etc.).
 */
public class Console {
	/** Any text written to the streams is redirected to this document. */
	private StyledDocument document;

	/**
	 * The document text is mono-spaced and color-coded
	 * (out -> black; err -> red).
	 */
	private AttributeSet outAttributeSet;
	private AttributeSet errAttributeSet;

	/** The standard output and error streams can be redirected to these. */
	private OutputStream outStream;
	private OutputStream errStream;

	/**
	 * We start a thread for each of the streams. The threads wait for
	 * text to be written to the streams and then append the text to
	 * the document. The threads are interrupted when the console is
	 * closed.
	 */
	private Thread outSynchronizerThread;
	private Thread errSynchronizerThread;

	/** Hold the text pane so we can keep the "tail" of the text visible. */
	private JTextPane textPane;

	/** Hold the window so we can open and close it. */
	private JFrame console;


	// ********** static methods **********

	/**
	 * Build a console that replaces the current System
	 * output and error streams.
	 */
	public static Console buildSystemConsole() {
		Console console = new Console();
		System.setOut(new PrintStream(console.getOutStream(), true));	// true = auto-flush
		System.setErr(new PrintStream(console.getErrStream(), true));	// true = auto-flush
		return console;
	}


	// ********** constructor **********

	/**
	 * Default constructor.
	 */
	public Console() {
		super();
		this.initialize();
	}


	// ********** initialization **********

	protected void initialize() {
		this.document = this.buildDocument();

		// set up the "out" stream
		this.outAttributeSet = this.buildOutAttributeSet();
		Pipe outPipe = new Pipe();
		this.outStream = outPipe.getOutputStream();
		this.outSynchronizerThread = this.buildSynchronizerThread(outPipe.getInputStream(), this.outAttributeSet, "out");
		this.outSynchronizerThread.start();

		// set up the "err" stream
		this.errAttributeSet = this.buildErrAttributeSet();
		Pipe errPipe = new Pipe();
		this.errStream = errPipe.getOutputStream();
		this.errSynchronizerThread = this.buildSynchronizerThread(errPipe.getInputStream(), this.errAttributeSet, "err");
		this.errSynchronizerThread.start();

		// set up the UI, but don't open it until the client requests
		// or something is written to the console
		this.textPane = this.buildTextPane();
		this.console = this.buildConsole();
	}

	private StyledDocument buildDocument() {
		StyledDocument result = new DefaultStyledDocument();

		// set up tab stops for the entire document
		MutableAttributeSet mas = new SimpleAttributeSet();
		StyleConstants.setTabSet(mas, new TabSet(new TabStop[] {new TabStop(30)}));
		result.setParagraphAttributes(0, 0, mas, false);

		result.addDocumentListener(this.buildDocumentListener());
		return result;
	}

	private DocumentListener buildDocumentListener() {
		return new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				Console.this.documentChanged();
			}
			public void insertUpdate(DocumentEvent e) {
				Console.this.documentChanged();
			}
			public void removeUpdate(DocumentEvent e) {
				Console.this.documentChanged();
			}
			public String toString() {
				return "document listener";
			}
		};
	}

	private AttributeSet buildOutAttributeSet() {
		return this.buildAttributeSet(Color.BLACK);
	}

	private AttributeSet buildErrAttributeSet() {
		return this.buildAttributeSet(Color.RED);
	}

	private AttributeSet buildAttributeSet(Color color) {
		MutableAttributeSet attributes = new SimpleAttributeSet();
		StyleConstants.setForeground(attributes, color);
		StyleConstants.setFontFamily(attributes, "Monospaced");
		StyleConstants.setFontSize(attributes, 12);
		return attributes;
	}

	private Thread buildSynchronizerThread(InputStream inputStream, AttributeSet attributeSet, String name) {
		return new Thread(new Synchronizer(inputStream, this.document, attributeSet), "Console Synchronizer: " + name);
	}

	private JTextPane buildTextPane() {
		JTextPane result = new NonWrappingTextPane(this.document);
		result.setEditable(false);
		return result;
	}

	private JFrame buildConsole() {
		JFrame window = new JFrame(this.title());
		window.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		window.getContentPane().add(this.buildMainPanel(), BorderLayout.CENTER);
		window.setLocation(300, 300);
		window.setSize(600, 400);
		window.addWindowListener(this.buildWindowListener());
		return window;
	}

	protected String title() {
		return "Console";
	}

	protected JPanel buildMainPanel() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(this.buildScrollableTextPane(), BorderLayout.CENTER);
		mainPanel.add(this.buildControlPanel(), BorderLayout.PAGE_END);
		return mainPanel;
	}

	protected Component buildScrollableTextPane() {
		return new JScrollPane(this.textPane);
	}

	protected Component buildControlPanel() {
		JPanel controlPanel = new JPanel(new BorderLayout());
		GridLayout grid = new GridLayout(1, 0);
		grid.setHgap(5);
		JPanel controlPanel2 = new JPanel(grid);
		controlPanel2.add(this.buildCopyButton());
		controlPanel2.add(this.buildClearButton());
		controlPanel2.setBorder(BorderFactory.createEmptyBorder(4, 0, 2, 0));
		controlPanel.add(controlPanel2, BorderLayout.LINE_END);
		return controlPanel;
	}

	private Component buildCopyButton() {
		return new JButton(this.buildCopyAction());
	}

	private Action buildCopyAction() {
		Action action = new AbstractAction("Copy") {
			public void actionPerformed(ActionEvent event) {
				Console.this.copy();
			}
		};
		action.setEnabled(true);
		return action;
	}

	private Component buildClearButton() {
		return new JButton(this.buildClearAction());
	}

	private Action buildClearAction() {
		Action action = new AbstractAction("Clear") {
			public void actionPerformed(ActionEvent event) {
				Console.this.clear();
			}
		};
		action.setEnabled(true);
		return action;
	}

	private WindowListener buildWindowListener() {
		return new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
				Console.this.shutDown();
			}
			public String toString() {
				return "window listener";
			}
		};
	}


	// ********** queries **********

	/**
	 * Return the "out" stream, typically corresponding to System.out.
	 */
	public OutputStream getOutStream() {
		return this.outStream;
	}

	/**
	 * Return the "err" stream, typically corresponding to System.err.
	 */
	public OutputStream getErrStream() {
		return this.errStream;
	}


	// ********** behavior **********

	/**
	 * Open the console window. The console can be "hidden" and re-opened
	 * repeatedly, but once it is "closed" it can no longer be re-opened.
	 * @see #hide()
	 * @see #close()
	 */
	public void open() {
		if ((this.outSynchronizerThread == null) || (this.errSynchronizerThread == null)) {
			throw new IllegalStateException("Console cannot be re-opened once it has been closed.");
		}
		this.console.setVisible(true);
	}

	/**
	 * Hide the console window. The window will re-appear either when a
	 * client calls #open() or when something is written to either of the
	 * console's two output streams.
	 */
	public void hide() {
		this.console.setVisible(false);
	}

	/**
	 * Close the console window. This also shuts down the two threads
	 * monitoring the "out" and "err" streams. Once this method is called
	 * the console can no longer be re-opened.
	 */
	public void close() {
		this.console.dispose();
	}

	/**
	 * The document changed in some fashion, make sure the console is
	 * open and the last line is visible.
	 */
	void documentChanged() {
		this.textPane.setCaretPosition(this.document.getLength());
		if ( ! this.console.isVisible()) {
			this.open();
		}
	}

	/**
	 * Copy the entire contents of the console to the system "clipboard".
	 */
	void copy() {
		this.textPane.selectAll();
		this.textPane.copy();
		this.textPane.setCaretPosition(this.document.getLength());
	}

	/**
	 * Clear out the console.
	 */
	public void clear() {
		try {
			this.document.remove(0, this.document.getLength());
		} catch (BadLocationException ex) {
			// should never happen...
		}
	}

	/**
	 * This is called when the console is closed and disposed.
	 */
	void shutDown() {
		this.outSynchronizerThread.interrupt();
		this.outSynchronizerThread = null;

		this.errSynchronizerThread.interrupt();
		this.errSynchronizerThread = null;
	}


	// ********** nested classes **********

	/**
	 * This task will synchronize a document with an input stream
	 * by reading from the stream and updating the document via the AWT
	 * Event Queue.
	 */
	private static class Synchronizer implements Runnable {
		private InputStream inputStream;
		private Document document;
		private AttributeSet attributeSet;

		Synchronizer(InputStream inputStream, Document document, AttributeSet attributeSet) {
			super();
			this.inputStream = inputStream;
			this.document = document;
			this.attributeSet = attributeSet;
		}

		/**
		 * Loop while there are more data to be read from the input stream.
		 * @see Runnable#run()
		 */
		public void run() {
			byte[] buffer = new byte[2048];		// use the default "pipe" size
			int length = this.read(buffer);
			while (length != -1) {
				this.appendDocument(new String(buffer, 0, length));
				length = this.read(buffer);
			}
		}

		/**
		 * Wrap any unexpected exception in a RuntimeException.
		 */
		private int read(byte[] buffer) {
			try {
				return this.inputStream.read(buffer);
			} catch (InterruptedIOException ex) {
				return -1;		// the thread was interrupted - time to quit
			} catch (IOException ex) {
				// the pipe is broken
				this.appendDocument("The Thread writing to the pipe is dead.");
				return 0;
//				throw new RuntimeException(ex);
			}
		}

		/**
		 * Place a task on the AWT Event Queue that will append
		 * the document with the specified string.
		 */
		private void appendDocument(String string) {
			EventQueue.invokeLater(new Appendix(this.document, string, this.attributeSet));
		}

	}


	/**
	 * This is the task dispatched to the AWT Event Queue that
	 * will update the document model held by the console's text area.
	 */
	private static class Appendix implements Runnable {
		private Document document;
		private String string;
		private AttributeSet attributeSet;

		Appendix(Document document, String string, AttributeSet attributeSet) {
			super();
			this.document = document;
			this.string = string;
			this.attributeSet = attributeSet;
		}

		/**
		 * Append the string to the end of the document,
		 * with an optional attribute set.
		 * @see Runnable#run()
		 */
		public void run() {
			try {
				this.document.insertString(this.document.getLength(), this.string, this.attributeSet);
			} catch (BadLocationException ex) {
				// should never happen...
			}
		}

	}


	/**
	 * Disable line-wrap in JTextPane.
	 * Work-around for JDK bug/rfe 4131119.
	 */
	private static class NonWrappingTextPane extends JTextPane {

		NonWrappingTextPane(StyledDocument document) {
			super(document);
		}

		public boolean getScrollableTracksViewportWidth() {
			return this.getSize().width < this.getParent().getSize().width;
		}

		public void setSize(Dimension d) {
			if (d.width < this.getParent().getSize().width) {
				d.width = this.getParent().getSize().width;
			}
			super.setSize(d);
		}

	}

}
