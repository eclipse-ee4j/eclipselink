/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.utility.io;

import java.io.Writer;

/**
 * A token stream that collects its output in a string buffer, which can
 * then be used to construct a string. The normal write() methods will
 * "escape" any embedded delimiters or escape characters.
 * Use the writeDelimiter() methods to write a delimiter
 * without it being "escaped". The escape character itself will
 * always be "escaped" (i.e. there is no way to write out the escape
 * character without it being doubled). Also, the escape character
 * may not also be listed as a delimiter.
 */
public class TokenWriter
	extends Writer
{
	/** The delimiter characters, contained in a single String */
	private String delimiters;

	/** The escape character, used to mark embedded delimiters */
	private char escapeCharacter;

	/** The buffer that holds the String as it is built */
	private StringBuffer buffer;

	/** Cache the value of the highest delimiter character */
	private char maxDelimiter;

	/** Temporary buffer used to hold writes of delimiters */
	private char[] delimiterBuffer;

	/** Size of delimiterBuffer, must be >= 1 */
	private final int delimiterBufferSize = 1024;

	/** The default delimiters are
		<space>
		<tab>
		<newline>
		<carriage return>
		<form feed>
	 */
	public static final String DEFAULT_DELIMITERS = " \t\n\r\f";

	/** The default escape character is '\' <backslash> */
	public static final char DEFAULT_ESCAPE_CHARACTER = '\\';


	/**
	 * Create a new token writer, using the specified delimiters,
	 * escape character, and string buffer.
	 *
	 * @param delimiters  a String containing the delimiters
	 * @param escapeCharacter  a char representing the escape character
	 * @param buffer  the StringBuffer to write on
	 */
	private TokenWriter(String delimiters, char escapeCharacter, StringBuffer buffer) {
		super();
		this.delimiters = delimiters;
		this.escapeCharacter = escapeCharacter;
		this.buffer = buffer;
		this.lock = buffer; // synchronize on the buffer instead of the writer
		this.initialize();
	}
	
	/**
	 * Create a new token writer, using the default delimiters,
	 * default escape character, and default initial string buffer size.
	 */
	public TokenWriter() {
		this(DEFAULT_DELIMITERS, DEFAULT_ESCAPE_CHARACTER, new StringBuffer());
	}
	
	/**
	 * Create a new token writer, using the specified initial string buffer
	 * size and the default delimiters and default escape character.
	 *
	 * @param initialSize  an int specifying the initial size of the buffer
	 */
	public TokenWriter(int initialSize) {
		this(DEFAULT_DELIMITERS, DEFAULT_ESCAPE_CHARACTER, new StringBuffer(initialSize));
	}
	
	/**
	 * Create a new token writer, using the specified delimiters
	 * and the default escape character and default initial string buffer size.
	 *
	 * @param delimiters  a String containing the delimiters
	 */
	public TokenWriter(String delimiters) {
		this(delimiters, DEFAULT_ESCAPE_CHARACTER, new StringBuffer());
	}
	
	/**
	 * Create a new token writer, using the specified delimiters and
	 * escape character and the default initial string buffer size.
	 *
	 * @param delimiters  a String containing the delimiters
	 * @param escapeCharacter  a char representing the escape character
	 */
	public TokenWriter(String delimiters, char escapeCharacter) {
		this(delimiters, escapeCharacter, new StringBuffer());
	}
	
	/**
	 * Create a new token writer, using the specified delimiters,
	 * escape character, and initial string buffer size.
	 *
	 * @param delimiters  a String containing the delimiters
	 * @param escapeCharacter  a char representing the escape character
	 * @param initialSize  an int specifying the initial size of the buffer
	 */
	public TokenWriter(String delimiters, char escapeCharacter, int initialSize) {
		this(delimiters, escapeCharacter, new StringBuffer(initialSize));
	}
	
	/**
	 * Calculate the max delimiter value and check for an
	 * invalid escape character.
	 */
	private void initialize() {
		this.calculateMaxDelimiter();
		if (this.charIsDelimiter(this.escapeCharacter)) {
			throw new IllegalArgumentException("The \"escape\" character may not belong to the list of delimiters.");
		}
	}
	
	/**
	 * Calculate the maximum value of a delimiter character.
	 * This will be used to short-circuit the search for a delimiter.
	 * @see #charIsDelimiter(int)
	 */
	private void calculateMaxDelimiter() {
		if (this.delimiters == null) {
			throw new NullPointerException();
		}
		this.maxDelimiter = 0;
		for (int i = 0; i < this.delimiters.length(); i++) {
			char c = this.delimiters.charAt(i);
			if (this.maxDelimiter < c)
				this.maxDelimiter = c;
		}
	}
	
	/**
	 * Return whether the specified character is a delimiter.
	 *
	 * @param  c  character
	 */
	private boolean charIsDelimiter(int c) {
		return (c <= this.maxDelimiter) && (this.delimiters.indexOf(c) >= 0);
	}
	
	/**
	 * Return whether the specified character is an escape character.
	 *
	 * @param  c  character
	 */
	private boolean charIsTheEscapeCharacter(int c) {
		return c == this.escapeCharacter;
	}
	
	/**
	 * Return whether the specified character
	 * requires an "escape".
	 *
	 * @param  c  character
	 */
	private boolean charRequiresEscape(int c) {
		return this.charIsTheEscapeCharacter(c) || this.charIsDelimiter(c);
	}
	
	/**
	 * Write a single character.
	 * "Escape" it if necessary.
	 *
	 * @param  c  character
	 */
	public void write(int c) {
		synchronized (this.lock) {
			if (this.charRequiresEscape(c))
				this.buffer.append(this.escapeCharacter);
			this.buffer.append((char) c);
		}
	}
	
	/**
	 * Write a single delimiter character without "escaping" it.
	 *
	 * @param  c  character
	 */
	public void writeDelimiter(int c) {
		synchronized (this.lock) {
			if (!this.charIsDelimiter(c))
				throw new IllegalArgumentException("Not a delimiter: " + c);
			this.buffer.append((char) c);
		}
	}
	
	/**
	 * Check the specified indices for validity within the specified array.
	 * Throw an IndexOutOfBoundsException if there are any problems.
	 * If the offset is equal to the length of the character array
	 * (which could be considered an illegal index) but
	 * the length is zero, nothing will happen....
	 *
	 * @param  cbuffer  Array of characters
	 * @param  offset   Offset from which to start writing characters
	 * @param  length   Number of characters to write
	 */
	private void checkIndices(char[] cbuffer, int offset, int length) {
		if ((offset < 0) || (offset > cbuffer.length) || (length < 0) || ((offset + length) > cbuffer.length) || ((offset + length) < 0))
			throw new IndexOutOfBoundsException();
	}
	
	/**
	 * Write a portion of an array of characters.
	 * "Escape" any embedded delimiter or escape characters.
	 *
	 * @param  cbuffer  Array of characters
	 * @param  offset   Offset from which to start writing characters
	 * @param  length   Number of characters to write
	 */
	public void write(char[] cbuffer, int offset, int length) {
		synchronized (this.lock) {
			this.checkIndices(cbuffer, offset, length);
			if (length == 0) {
				return;
			}
			int tooFar = offset + length;
			for (int i = offset; i < tooFar; i++) {
				this.write(cbuffer[i]);
			}
		}
	}
	
	/**
	 * Write a portion of an array of delimiter characters.
	 * Do <em>not</em> "escape" any of them.
	 *
	 * @param  cbuffer  Array of characters
	 * @param  offset   Offset from which to start writing characters
	 * @param  length   Number of characters to write
	 */
	public void writeDelimiter(char[] cbuffer, int offset, int length) {
		synchronized (this.lock) {
			this.checkIndices(cbuffer, offset, length);
			if (length == 0) {
				return;
			}
			int tooFar = offset + length;
			for (int i = offset; i < tooFar; i++) {
				this.writeDelimiter(cbuffer[i]);
			}
		}
	}
	
	/**
	 * Write a string of delimiter characters.
	 * Do <em>not</em> "escape" any them.
	 *
	 * @param  string  String to be written
	 */
	public void writeDelimiter(String string) {
		this.writeDelimiter(string, 0, string.length());
	}
	
	/**
	 * Write a portion of a string of delimiter characters.
	 * Do <em>not</em> "escape" any them.
	 *
	 * @param  string  String to be written
	 * @param  offset  Offset from which to start writing characters
	 * @param  length  Number of characters to write
	 */
	public void writeDelimiter(String string, int offset, int length) {
		synchronized (this.lock) {
			char[] cbuffer;
			if (length <= this.delimiterBufferSize) {
				if (this.delimiterBuffer == null) {
					this.delimiterBuffer = new char[this.delimiterBufferSize];
				}
				cbuffer = this.delimiterBuffer;
			} else {
				// don't permanently allocate large buffers
				cbuffer = new char[length];
			}
			string.getChars(offset, (offset + length), cbuffer, 0);
			this.writeDelimiter(cbuffer, 0, length);
		}
	}
	
	/**
	 * Return the buffer's current value as a string.
	 *
	 * @return String representation of the current buffer value
	 */
	public String toString() {
		return this.buffer.toString();
	}
	
	/**
	 * Return the string buffer itself.
	 *
	 * @return StringBuffer holding the current buffer value
	 */
	public StringBuffer getBuffer() {
		return this.buffer;
	}
	
	/**
	 * Flush the stream.
	 */
	public void flush() {
		// do nothing...
	}
	
	/**
	 * Close the stream.
	 * This method does not release the buffer, since its
	 * contents might still be required.
	 */
	public void close() {
		// do nothing...
	}

}
