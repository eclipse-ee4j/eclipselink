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
package org.eclipse.persistence.tools.workbench.utility.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;

/**
 * Improved implementation of the JDK's piped streams. This pipe allows
 * multiple threads to write and read the pipe and does not poll the pipe
 * and does not kill the pipe if a thread writing to it dies.
 */
public class Pipe {
	private final int bufferSize;
	private final byte[] buffer;
	private final long timeout;

	private final InputStream in = new InputStreamAdapter();
	private final OutputStream out = new OutputStreamAdapter();

	private boolean readLap = false;
	private int readIndex = 0;

	private boolean writeLap = false;
	private int writeIndex = 0;

	private boolean inputStreamClosed = false;
	private boolean outputStreamClosed = false;


	// ********** constructors **********

	/**
	 * Construct a pipe with the default buffer size (2048) and time-out
	 * value (zero, which means no time-out).
	 */
	public Pipe() {
		this(2048);
	}

	/**
	 * Construct a pipe with the specified buffer size and the default
	 * time-out value (zero, which means no time-out).
	 */
	public Pipe(int bufferSize) {
		this(bufferSize, 0L);
	}

	/**
	 * Construct a pipe with the specified buffer size and
	 * time-out value. A time-out value of zero will cause reads to
	 * block indefinitely.
	 */
	public Pipe(int bufferSize, long timeout) {
		super();
		this.bufferSize = bufferSize;
		this.buffer = new byte[bufferSize];
		this.timeout = timeout;
	}


	// ********** accessors **********

	/**
	 * Return the input stream half of the pipe.
	 * This stream will return the data written to
	 * the output stream half of the pipe.
	 */
	public InputStream getInputStream() {
		return this.in;
	}

	/**
	 * Return the output stream half of the pipe.
	 * Data written to this stream will be returned
	 * by the input stream half of the pipe.
	 */
	public OutputStream getOutputStream() {
		return this.out;
	}


	// ********** reading **********

	/**
	 * Return the number of bytes currently available for reading
	 * from the input stream.
	 */
	synchronized int available() {
		if (this.inputStreamClosed) {
			return 0;
		}
		return this.bytesInPipe();
	}

	/**
	 * Return the number of bytes currently available for reading
	 * from the input stream.
	 */
	private int bytesInPipe() {
		int diff = this.writeIndex - this.readIndex;
		if (diff == 0) {
			// the write is either at the same position or a complete lap ahead
			return (this.writeLap == this.readLap) ? 0 : this.bufferSize;
		}
		return (diff > 0) ? diff : diff + this.bufferSize;
	}

	/**
	 * Read a byte from the pipe. This method will block
	 * until either a byte is available or a time-out occurs.
	 * If a time-out occurs, throw an InterruptedIOException.
	 */
	synchronized int read() throws IOException {
		if (this.inputStreamClosed) {
			throw new IOException("Pipe closed");
		}
		// wait for a byte to become available
		long stop = System.currentTimeMillis() + this.timeout;
		long remaining = this.timeout;
		while (this.bytesInPipe() == 0) {
			if (this.outputStreamClosed) {
				return -1;		// if the pipe is empty and the output stream is closed, we are done
			}
			try {
				this.wait(remaining);
			} catch (InterruptedException ex) {
				throw new InterruptedIOException();
			}
			if (this.timeout > 0L) {
				remaining = stop - System.currentTimeMillis();
				if (remaining <= 0L) {
					throw new InterruptedIOException();		// a time-out occurred
				}
			}
			if (this.inputStreamClosed) {
				throw new IOException("Pipe closed");		// the pipe might've been closed while we were waiting
			}
		}
		// a byte is available and the input stream is still open
		byte b = this.buffer[this.readIndex];
		this.readIndex++;
		if (this.readIndex == this.bufferSize) {
			this.readLap = ! this.readLap;
			this.readIndex = 0;
		}

		// notify any waiting writers that there is free space in the pipe
		this.notifyAll();

		return b & 0xFF;
	}

	/**
	 * Read the specified number of bytes from the pipe and
	 * into the specified byte array at the specified offset.
	 * Return the actual number of bytes read.
	 * This method will block until either a byte is available
	 * or a time-out occurs. If a time-out occurs, throw an
	 * InterruptedIOException.
	 */
	synchronized int read(byte[] b, int off, int len) throws IOException {
		if (this.inputStreamClosed) {
			throw new IOException("Pipe closed");
		}
		// wait for a byte to become available
		long stop = System.currentTimeMillis() + this.timeout;
		long remaining = this.timeout;
		int bytesInPipe = this.bytesInPipe();
		while (bytesInPipe == 0) {
			if (this.outputStreamClosed) {
				return -1;		// if the pipe is empty and the output stream is closed, we are done
			}
			try {
				this.wait(remaining);
			} catch (InterruptedException ex) {
				throw new InterruptedIOException();
			}
			if (this.timeout > 0L) {
				remaining = stop - System.currentTimeMillis();
				if (remaining <= 0L) {
					throw new InterruptedIOException();		// a time-out occurred
				}
			}
			if (this.inputStreamClosed) {
				throw new IOException("Pipe closed");		// the pipe might've been closed while we were waiting
			}
			bytesInPipe = this.bytesInPipe();
		}
		// some bytes are available and the input stream is still open
		int bytesRead = (len > bytesInPipe) ? bytesInPipe : len;
		int copyLength1 = this.bufferSize - this.readIndex;
		if (copyLength1 > bytesRead) {
			copyLength1 = bytesRead;
		}
		System.arraycopy(this.buffer, this.readIndex, b, off, copyLength1);
		this.readIndex += copyLength1;
		if (this.readIndex == this.bufferSize) {
			this.readLap = ! this.readLap;
			this.readIndex = 0;
		}

		int copyLength2 = bytesRead - copyLength1;
		if (copyLength2 > 0) {
			System.arraycopy(this.buffer, 0, b, off + copyLength1, copyLength2);
			this.readIndex += copyLength2;
		}

		// notify any waiting writers that there is free space in the pipe
		this.notifyAll();

		return bytesRead;
	}

	/**
	 * Close the input stream half of the pipe. This will effectively
	 * close the output stream half of the pipe also, preventing any
	 * further data from being written to the pipe.
	 */
	synchronized void closeInputStream() {
		if (this.inputStreamClosed) {
			throw new IllegalStateException("InputStream already closed");
		}
		this.inputStreamClosed = true;
		this.outputStreamClosed = true;
		this.notifyAll();
	}


	// ********** writing **********

	/**
	 * Return the number of bytes currently available in the buffer
	 * for writing.
	 */
	private int freeSpace() {
		int diff = this.readIndex - this.writeIndex;
		if (diff == 0) {
			// the write is either at the same position or a complete lap ahead
			return (this.writeLap == this.readLap) ? this.bufferSize : 0;
		}
		return (diff > 0) ? diff : diff + this.bufferSize;
	}

	/**
	 * Write the specified byte to the pipe. This method will block
	 * indefinitely if the pipe is full.
	 */
	synchronized void write(int b) throws IOException {
		if (this.outputStreamClosed) {
			throw new IOException("Pipe closed");
		}
		// wait for some free space
		while (this.freeSpace() == 0) {
			try {
				this.wait();
			} catch (InterruptedException ex) {
				throw new InterruptedIOException();
			}
			if (this.outputStreamClosed) {
				throw new IOException("Pipe closed");		// the pipe might've been closed while we were waiting
			}
		}
		// free space is available and the output stream is still open
		this.buffer[this.writeIndex] = (byte) b;
		this.writeIndex++;
		if (this.writeIndex == this.bufferSize) {
			this.writeLap = ! this.writeLap;
			this.writeIndex = 0;
		}

		this.notifyAll();
	}

	/**
	 * Write the specified bytes to the pipe. This method will block
	 * indefinitely if the pipe is full.
	 */
	synchronized void write(byte[] b, int off, int len) throws IOException {
		if (this.outputStreamClosed) {
			throw new IOException("Pipe closed");
		}
		while (len > 0) {
			// wait for some free space
			int freeSpace = this.freeSpace();
			while (freeSpace == 0) {
				try {
					this.wait();
				} catch (InterruptedException ex) {
					throw new InterruptedIOException();
				}
				if (this.outputStreamClosed) {
					throw new IOException("Pipe closed");		// the pipe might've been closed while we were waiting
				}
				freeSpace = this.freeSpace();
			}
			// free space is available and the output stream is still open;
			// calculate how many bytes we can put in the buffer this pass
			int tempLength = (len > freeSpace) ? freeSpace : len;

			int copyLength1 = this.bufferSize - this.writeIndex;
			if (copyLength1 > tempLength) {
				copyLength1 = tempLength;
			}
			System.arraycopy(b, off, this.buffer, this.writeIndex, copyLength1);
			this.writeIndex += copyLength1;
			if (this.writeIndex == this.bufferSize) {
				this.writeLap = ! this.writeLap;
				this.writeIndex = 0;
			}

			int copyLength2 = tempLength - copyLength1;
			if (copyLength2 > 0) {
				System.arraycopy(b, off + copyLength1, this.buffer, 0, copyLength2);
				this.writeIndex += copyLength2;
			}
			// move to the next chunk of bytes
			off += tempLength;
			len -= tempLength;

			// notify any waiting readers that there is data to be read from the pipe
			this.notifyAll();
		}
	}

	/**
	 * Close the output stream half of the pipe. Although no more data
	 * can be written to the pipe after this method is called, the input
	 * stream half of the pipe can remain open and will return the data
	 * still remaining in the buffer.
	 */
	synchronized void closeOutputStream() {
		if (this.outputStreamClosed) {
			throw new IllegalStateException("OutputStream already closed");
		}
		this.outputStreamClosed = true;
		this.notifyAll();
	}


	// ********** inner classes **********

	/**
	 * Adapt the pipe to the InputStream specification.
	 */
	private class InputStreamAdapter extends InputStream {

		InputStreamAdapter() {
			super();
		}

		/**
		 * @see java.io.InputStream#available()
		 */
		public int available() throws IOException {
			return Pipe.this.available();
		}

		/**
		 * @see java.io.InputStream#read()
		 */
		public int read() throws IOException {
			return Pipe.this.read();
		}

		/**
		 * @see java.io.InputStream#read(byte[], int, int)
		 */
		public int read(byte[] b, int off, int len) throws IOException {
			return Pipe.this.read(b, off, len);
		}

		/**
		 * @see java.io.InputStream#close()
		 */
		public void close() throws IOException {
			Pipe.this.closeInputStream();
		}

	}


	/**
	 * Adapt the pipe to the OutputStream specification.
	 */
	private class OutputStreamAdapter extends OutputStream {

		OutputStreamAdapter() {
			super();
		}

		/**
		 * @see java.io.OutputStream#write(int)
		 */
		public void write(int b) throws IOException {
			Pipe.this.write(b);
		}

		/**
		 * @see java.io.OutputStream#write(byte[], int, int)
		 */
		public void write(byte[] b, int off, int len) throws IOException {
			Pipe.this.write(b, off, len);
		}

		/**
		 * @see java.io.OutputStream#close()
		 */
		public void close() throws IOException {
			Pipe.this.closeOutputStream();
		}

	}

}
