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
package org.eclipse.persistence.tools.workbench.uitools.app.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.EventObject;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import javax.swing.text.Position;
import javax.swing.text.Segment;

import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * This javax.swing.text.Document can be used to keep a DocumentListener
 * (e.g. a JTextField) in synch with a PropertyValueModel that holds a string.
 * 
 * NB: This model should only be used for "small" documents;
 * i.e. documents used by text fields, not text panes.
 * @see synchronizeDelegate(String)
 */
public class DocumentAdapter implements Document, Serializable {

	/** The delegate document whose behavior we "enhance". */
	protected Document delegate;

	/** A listener that allows us to forward any changes made to the delegate document. */
	protected CombinedListener delegateListener;

	/** A value model on the underlying model string. */
	protected PropertyValueModel stringHolder;

	/** A listener that allows us to synchronize with changes made to the underlying model string. */
	protected PropertyChangeListener stringListener;

    /** The event listener list for the document. */
    protected EventListenerList listenerList = new EventListenerList();


	// ********** constructors **********

	/**
	 * Default constructor - initialize stuff.
	 */
	private DocumentAdapter() {
		super();
		this.initialize();
	}

	/**
	 * Constructor - the string holder is required.
	 * Wrap the specified document.
	 */
	public DocumentAdapter(PropertyValueModel stringHolder, Document delegate) {
		this();
		if (stringHolder == null || delegate == null) {
			throw new NullPointerException();
		}
		this.stringHolder = stringHolder;
		// postpone listening to the underlying model string
		// until we have listeners ourselves...
		this.delegate = delegate;
	}

	/**
	 * Constructor - the string holder is required.
	 * Wrap a plain document.
	 */
	public DocumentAdapter(PropertyValueModel stringHolder) {
		this(stringHolder, new PlainDocument());
	}


	// ********** initialization **********

	protected void initialize() {
		this.stringListener = this.buildStringListener();
		this.delegateListener = this.buildDelegateListener();
	}

	protected PropertyChangeListener buildStringListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				DocumentAdapter.this.stringChanged(e);
			}
			public String toString() {
				return "string listener";
			}
		};
	}

	protected CombinedListener buildDelegateListener() {
		return new InternalListener();
	}


	// ********** Document implementation **********

	/**
	 * @see javax.swing.text.Document#getLength()
	 */
	public int getLength() {
		return this.delegate.getLength();
	}

	/**
	 * Extend to start listening to the underlying models if necessary.
	 * @see javax.swing.text.Document#addDocumentListener(javax.swing.event.DocumentListener)
	 */
	public void addDocumentListener(DocumentListener listener) {
		if (this.listenerList.getListenerCount(DocumentListener.class) == 0) {
			this.delegate.addDocumentListener(this.delegateListener);
			this.engageStringHolder();
		}
		this.listenerList.add(DocumentListener.class, listener);
	}

	/**
	 * Extend to stop listening to the underlying models if appropriate.
	 * @see javax.swing.text.Document#removeDocumentListener(javax.swing.event.DocumentListener)
	 */
	public void removeDocumentListener(DocumentListener listener) {
		this.listenerList.remove(DocumentListener.class, listener);
		if (this.listenerList.getListenerCount(DocumentListener.class) == 0) {
			this.disengageStringHolder();
			this.delegate.removeDocumentListener(this.delegateListener);
		}
	}

	/**
	 * Extend to start listening to the delegate document if necessary.
	 * @see javax.swing.text.Document#addUndoableEditListener(javax.swing.event.UndoableEditListener)
	 */
	public void addUndoableEditListener(UndoableEditListener listener) {
		if (this.listenerList.getListenerCount(UndoableEditListener.class) == 0) {
			this.delegate.addUndoableEditListener(this.delegateListener);
		}
		this.listenerList.add(UndoableEditListener.class, listener);
	}

	/**
	 * Extend to stop listening to the delegate document if appropriate.
	 * @see javax.swing.text.Document#removeUndoableEditListener(javax.swing.event.UndoableEditListener)
	 */
	public void removeUndoableEditListener(UndoableEditListener listener) {
		this.listenerList.remove(UndoableEditListener.class, listener);
		if (this.listenerList.getListenerCount(UndoableEditListener.class) == 0) {
			this.delegate.removeUndoableEditListener(this.delegateListener);
		}
	}

	/**
	 * @see javax.swing.text.Document#getProperty(java.lang.Object)
	 */
	public Object getProperty(Object key) {
		return this.delegate.getProperty(key);
	}

	/**
	 * @see javax.swing.text.Document#putProperty(java.lang.Object, java.lang.Object)
	 */
	public void putProperty(Object key, Object value) {
		this.delegate.putProperty(key, value);
	}

	/**
	 * Extend to update the underlying model string directly.
	 * The resulting event will be ignored: @see synchronizeDelegate(String).
	 * @see javax.swing.text.Document#remove(int, int)
	 */
	public void remove(int offset, int len) throws BadLocationException {
		this.delegate.remove(offset, len);
		this.stringHolder.setValue(this.delegate.getText(0, this.delegate.getLength()));
	}

	/**
	 * Extend to update the underlying model string directly.
	 * The resulting event will be ignored: @see synchronizeDelegate(String).
	 * @see javax.swing.text.Document#insertString(int, java.lang.String, javax.swing.text.AttributeSet)
	 */
	public void insertString(int offset, String insertedString, AttributeSet a) throws BadLocationException {
		this.delegate.insertString(offset, insertedString, a);
		this.stringHolder.setValue(this.delegate.getText(0, this.delegate.getLength()));
	}

	/**
	 * @see javax.swing.text.Document#getText(int, int)
	 */
	public String getText(int offset, int length) throws BadLocationException {
		return this.delegate.getText(offset, length);
	}

	/**
	 * @see javax.swing.text.Document#getText(int, int, javax.swing.text.Segment)
	 */
	public void getText(int offset, int length, Segment txt) throws BadLocationException {
		this.delegate.getText(offset, length, txt);
	}

	/**
	 * @see javax.swing.text.Document#getStartPosition()
	 */
	public Position getStartPosition() {
		return this.delegate.getStartPosition();
	}

	/**
	 * @see javax.swing.text.Document#getEndPosition()
	 */
	public Position getEndPosition() {
		return this.delegate.getEndPosition();
	}

	/**
	 * @see javax.swing.text.Document#createPosition(int)
	 */
	public Position createPosition(int offs) throws BadLocationException {
		return this.delegate.createPosition(offs);
	}

	/**
	 * @see javax.swing.text.Document#getRootElements()
	 */
	public Element[] getRootElements() {
		return this.delegate.getRootElements();
	}

	/**
	 * @see javax.swing.text.Document#getDefaultRootElement()
	 */
	public Element getDefaultRootElement() {
		return this.delegate.getDefaultRootElement();
	}

	/**
	 * @see javax.swing.text.Document#render(java.lang.Runnable)
	 */
	public void render(Runnable r) {
		this.delegate.render(r);
	}


	// ********** queries **********

	public DocumentListener[] getDocumentListeners() {
		return (DocumentListener[]) this.listenerList.getListeners(DocumentListener.class);
	}

	public UndoableEditListener[] getUndoableEditListeners() {
		return (UndoableEditListener[]) this.listenerList.getListeners(UndoableEditListener.class);
	}


	// ********** behavior **********

	/**
	 * A third party has modified the underlying model string.
	 * Synchronize the delegate document accordingly.
	 */
	protected void stringChanged(PropertyChangeEvent e) {
		this.synchronizeDelegate((String) e.getNewValue());
	}

	/**
	 * Replace the document's entire text string with the new string.
	 */
	protected void synchronizeDelegate(String s) {
		try {
			int len = this.delegate.getLength();
			// check to see whether the delegate has already been synchronized
			// (via #insertString() or #remove())
			if ( ! this.delegate.getText(0, len).equals(s)) {
				this.delegate.remove(0, len);
				this.delegate.insertString(0, s, null);
			}
		} catch (BadLocationException ex) {
			throw new IllegalStateException(ex.getMessage());	// this should not happen...
		}
	}

	protected void engageStringHolder() {
		this.stringHolder.addPropertyChangeListener(ValueModel.VALUE, this.stringListener);
		this.synchronizeDelegate((String) this.stringHolder.getValue());
	}

	protected void disengageStringHolder() {
		this.stringHolder.removePropertyChangeListener(ValueModel.VALUE, this.stringListener);
	}

	protected void delegateChangedUpdate(DocumentEvent e) {
		// no need to lazy-initialize the event;
		// we wouldn't get here if we did not have listeners...
		DocumentEvent ee = new InternalDocumentEvent(this, e);
		DocumentListener[] listeners = this.getDocumentListeners();
		for (int i = listeners.length; i-- > 0; ) {
			listeners[i].changedUpdate(ee);
		}
	}

	protected void delegateInsertUpdate(DocumentEvent e) {
		// no need to lazy-initialize the event;
		// we wouldn't get here if we did not have listeners...
		DocumentEvent ee = new InternalDocumentEvent(this, e);
		DocumentListener[] listeners = this.getDocumentListeners();
		for (int i = listeners.length; i-- > 0; ) {
			listeners[i].insertUpdate(ee);
		}
	}

	protected void delegateRemoveUpdate(DocumentEvent e) {
		// no need to lazy-initialize the event;
		// we wouldn't get here if we did not have listeners...
		DocumentEvent ee = new InternalDocumentEvent(this, e);
		DocumentListener[] listeners = this.getDocumentListeners();
		for (int i = listeners.length; i-- > 0; ) {
			listeners[i].removeUpdate(ee);
		}
	}

	protected void delegateUndoableEditHappened(UndoableEditEvent e) {
		// no need to lazy-initialize the event;
		// we wouldn't get here if we did not have listeners...
		UndoableEditEvent ee = new UndoableEditEvent(this, e.getEdit());
		UndoableEditListener[] listeners = this.getUndoableEditListeners();
		for (int i = listeners.length; i-- > 0; ) {
			listeners[i].undoableEditHappened(ee);
		}
	}

	// ********** standard methods **********

	public String toString() {
		return StringTools.buildToStringFor(this, this.stringHolder);
	}


// ********** inner class **********

	protected interface CombinedListener extends DocumentListener, UndoableEditListener {
		// just consolidate the two interfaces
	}

	protected class InternalListener implements CombinedListener {
		/**
		 * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
		 */
		public void changedUpdate(DocumentEvent e) {
			DocumentAdapter.this.delegateChangedUpdate(e);
		}
		/**
		 * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
		 */
		public void insertUpdate(DocumentEvent e) {
			DocumentAdapter.this.delegateInsertUpdate(e);
		}
		/**
		 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
		 */
		public void removeUpdate(DocumentEvent e) {
			DocumentAdapter.this.delegateRemoveUpdate(e);
		}
		/**
		 * @see javax.swing.event.UndoableEditListener#undoableEditHappened(javax.swing.event.UndoableEditEvent)
		 */
		public void undoableEditHappened(UndoableEditEvent e) {
			DocumentAdapter.this.delegateUndoableEditHappened(e);
		}
	}
	
	protected static class InternalDocumentEvent
		extends EventObject
		implements DocumentEvent
	{
		protected DocumentEvent delegate;
	
		protected InternalDocumentEvent(Document document, DocumentEvent delegate) {
			super(document);
			this.delegate = delegate;
		}
		/**
		 * @see javax.swing.event.DocumentEvent#getChange(javax.swing.text.Element)
		 */
		public ElementChange getChange(Element elem) {
			return this.delegate.getChange(elem);
		}
		/**
		 * @see javax.swing.event.DocumentEvent#getDocument()
		 */
		public Document getDocument() {
			return (Document) this.source;
		}
		/**
		 * @see javax.swing.event.DocumentEvent#getLength()
		 */
		public int getLength() {
			return this.delegate.getLength();
		}
		/**
		 * @see javax.swing.event.DocumentEvent#getOffset()
		 */
		public int getOffset() {
			return this.delegate.getOffset();
		}
		/**
		 * @see javax.swing.event.DocumentEvent#getType()
		 */
		public EventType getType() {
			return this.delegate.getType();
		}
	}

}
