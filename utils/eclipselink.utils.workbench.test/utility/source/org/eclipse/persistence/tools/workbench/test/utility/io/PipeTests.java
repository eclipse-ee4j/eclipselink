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
package org.eclipse.persistence.tools.workbench.test.utility.io;

import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.io.Pipe;


public class PipeTests extends TestCase {
	volatile Pipe pipe;
	volatile byte[] buffer;
	volatile int position;
	volatile String string;
	volatile boolean exCaught;

	public static Test suite() {
		return new TestSuite(PipeTests.class);
	}

	public PipeTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.pipe = new Pipe(10);
		this.buffer = new byte[1000];
		this.position = 0;
		this.string = "The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog.";
		this.exCaught = false;
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	/**
	 * test with a normal writer and normal reader
	 */
	public void testPipe() throws Exception {
		this.verifyPipe(this.buildWriteRunnable(), this.buildReadRunnable());
	}

	/**
	 * test with a 1-byte writer and a normal reader
	 */
	public void testPipeWrite1() throws Exception {
		this.verifyPipe(this.buildWriteRunnable1(), this.buildReadRunnable());
	}

	/**
	 * test with a normal writer and a 1-byte reader
	 */
	public void testPipeRead1() throws Exception {
		this.verifyPipe(this.buildWriteRunnable(), this.buildReadRunnable1());
	}

	/**
	 * test with a 1-byte writer and a 1-byte reader
	 */
	public void testPipeWrite1Read1() throws Exception {
		this.verifyPipe(this.buildWriteRunnable1(), this.buildReadRunnable1());
	}

	private void verifyPipe(Runnable writer, Runnable reader)  throws Exception {
		Thread readThread = new Thread(reader);
		readThread.start();
		writer.run();
		readThread.join();
		assertFalse(this.exCaught);
		assertEquals(this.string, new String(this.buffer, 0, this.position));
	}

	/**
	 * test with a normal writer and a normal reader
	 */
	public void testPipeTruncate() throws Exception {
		this.verifyPipeTruncate(this.buildWriteRunnable(), this.buildReadRunnable());
	}

	/**
	 * test with a 1-byte writer and a normal reader
	 */
	public void testPipeTruncateWrite1() throws Exception {
		this.verifyPipeTruncate(this.buildWriteRunnable1(), this.buildReadRunnable());
	}

	/**
	 * test with a normal writer and 1-byte reader
	 */
	public void testPipeTruncateRead1() throws Exception {
		this.verifyPipeTruncate(this.buildWriteRunnable(), this.buildReadRunnable1());
	}

	/**
	 * test with a 1-byte writer and 1-byte reader
	 */
	public void testPipeTruncateWrite1Read1() throws Exception {
		this.verifyPipeTruncate(this.buildWriteRunnable1(), this.buildReadRunnable1());
	}

	private void verifyPipeTruncate(Runnable writer, Runnable reader) throws Exception {
		// use a buffer that will truncate the string
		this.buffer = new byte[20];
		Thread readThread = new Thread(reader);
		readThread.start();
		writer.run();
		readThread.join();
		assertFalse(this.exCaught);
		assertEquals(this.string.substring(0, 20), new String(this.buffer, 0, this.position));
	}

	private Runnable buildWriteRunnable() {
		return new Runnable() {
			public void run() {
				OutputStream out = PipeTests.this.pipe.getOutputStream();
				try {
					out.write(PipeTests.this.string.getBytes());
					out.close();
				} catch (Exception ex) {
					// the pipe will be closed when the read buffer is full
					if ( ! ex.getMessage().equals("Pipe closed")) {
						PipeTests.this.exCaught = true;
						ex.printStackTrace();
					}
				}
			}
		};
	}

	/**
	 * build a writer that writes 1 byte at a time
	 */
	private Runnable buildWriteRunnable1() {
		return new Runnable() {
			public void run() {
				OutputStream out = PipeTests.this.pipe.getOutputStream();
				try {
					byte[] bytes = PipeTests.this.string.getBytes();
					for (int i = 0; i < bytes.length; i++) {
						out.write(bytes[i]);
					}
					out.close();
				} catch (Exception ex) {
					// the pipe will be closed when the read buffer is full
					if ( ! ex.getMessage().equals("Pipe closed")) {
						PipeTests.this.exCaught = true;
						ex.printStackTrace();
					}
				}
			}
		};
	}

	private Runnable buildReadRunnable() {
		return new Runnable() {
			public void run() {
				try {
					InputStream in = PipeTests.this.pipe.getInputStream();
					int len = PipeTests.this.buffer.length;
					int bytesRead = 0;
					do {
						if (len <= 0) {
							in.close();
							return;
						}
						bytesRead = in.read(PipeTests.this.buffer, PipeTests.this.position, len);
						if (bytesRead != -1) {
							PipeTests.this.position += bytesRead;
							len -= bytesRead;
						}
					} while (bytesRead != -1);
					in.close();
				} catch (Exception ex) {
					PipeTests.this.exCaught = true;
					ex.printStackTrace();
				}
			}
		};
	}

	/**
	 * build a reader that reads 1 byte at a time
	 */
	private Runnable buildReadRunnable1() {
		return new Runnable() {
			public void run() {
				try {
					InputStream in = PipeTests.this.pipe.getInputStream();
					int len = PipeTests.this.buffer.length;
					int b = -1;
					do {
						if (len <= 0) {
							in.close();
							return;
						}
						b = in.read();
						if (b != -1) {
							PipeTests.this.buffer[PipeTests.this.position] = (byte) b;
							PipeTests.this.position++;
							len--;
						}
					} while (b != -1);
					in.close();
				} catch (Exception ex) {
					PipeTests.this.exCaught = true;
					ex.printStackTrace();
				}
			}
		};
	}

	public void testAvailable() throws Exception {
		// use a bigger pipe so the entire string can be buffered at once
		this.pipe = new Pipe(5000);
		this.buildWriteRunnable().run();
		assertEquals(this.string.length(), this.pipe.getInputStream().available());
		byte[] bytes = new byte[20];
		this.pipe.getInputStream().read(bytes);
		assertEquals(this.string.substring(0, 20), new String(bytes));
		assertEquals(this.string.length() - 20, this.pipe.getInputStream().available());
	}

	public void testFullPipe() throws Exception {
		this.verifyFullPipe(this.buildWriteRunnable(), this.buildReadRunnable());
	}

	public void testFullPipe1() throws Exception {
		this.verifyFullPipe(this.buildWriteRunnable1(), this.buildReadRunnable1());
	}

	private void verifyFullPipe(Runnable writer, Runnable reader) throws Exception {
		this.pipe = new Pipe(5);
		this.string = "12345";
		int len = this.string.length();
		this.buffer = new byte[len];
		// this write should not block
		writer.run();
		reader.run();
		assertEquals(this.string, new String(this.buffer, 0, len));
	}

	public void testPipeLaps() throws Exception {
		this.pipe = new Pipe(10);
		OutputStream out = this.pipe.getOutputStream();
		InputStream in = this.pipe.getInputStream();
		this.string = "0123456789";
		// fill the pipe - this write should not block
		out.write(this.string.getBytes());
		assertEquals(10, in.available());
		// read half the bytes
		int bytesRead = in.read(this.buffer, 0, 5);
		assertEquals(5, in.available());
		assertEquals(5, bytesRead);
		assertEquals("01234", new String(this.buffer, 0, 5));

		// fill the pipe again
		this.string = "abcde";
		out.write(this.string.getBytes());
		assertEquals(10, in.available());
		// read all the bytes, which are wrapped in the pipe
		bytesRead = in.read(this.buffer, 0, 10);
		assertEquals(0, in.available());
		assertEquals(10, bytesRead);
		assertEquals("56789abcde", new String(this.buffer, 0, 10));
	}

	public void testPipeLaps1() throws Exception {
		this.pipe = new Pipe(10);
		OutputStream out = this.pipe.getOutputStream();
		InputStream in = this.pipe.getInputStream();
		this.string = "0123456789";
		// fill the pipe - this write should not block
		byte[] bytes = this.string.getBytes();
		for (int i = 0; i < bytes.length; i++) {
			out.write(bytes[i]);
		}
		assertEquals(10, in.available());
		// read half the bytes
		for (int i = 0; i < 5; i++) {
			this.buffer[i] = (byte) in.read();
		}
		assertEquals(5, in.available());
		assertEquals("01234", new String(this.buffer, 0, 5));

		// fill the pipe again
		this.string = "abcde";
		bytes = this.string.getBytes();
		for (int i = 0; i < bytes.length; i++) {
			out.write(bytes[i]);
		}
		assertEquals(10, in.available());
		// read all the bytes, which are wrapped in the pipe
		for (int i = 0; i < 10; i++) {
			this.buffer[i] = (byte) in.read();
		}
		assertEquals(0, in.available());
		assertEquals("56789abcde", new String(this.buffer, 0, 10));
	}

	public void testTimeout() throws Exception {
		this.pipe = new Pipe(100, 100L);
		boolean timeout = false;
		try {
			this.pipe.getInputStream().read(new byte[50]);
		} catch (InterruptedIOException ex) {
			// the pipe should already be closed
			timeout = true;
		}
		assertTrue(timeout);
	}

	public void testRecloseWriteStream() throws Exception {
		this.testPipe();
		boolean closeFailed = false;
		try {
			this.pipe.getOutputStream().close();
		} catch (IllegalStateException ex) {
			// the pipe should already be closed
			if (ex.getMessage().equals("OutputStream already closed")) {
				closeFailed = true;
			} else {
				throw ex;
			}
		}
		assertTrue(closeFailed);
	}

	public void testRecloseReadStream() throws Exception {
		this.testPipe();
		boolean closeFailed = false;
		try {
			this.pipe.getInputStream().close();
		} catch (IllegalStateException ex) {
			// the pipe should already be closed
			if (ex.getMessage().equals("InputStream already closed")) {
				closeFailed = true;
			} else {
				throw ex;
			}
		}
		assertTrue(closeFailed);
	}

	public void testMultipleThreads() throws Exception {
		Thread rt1 = new Thread(this.buildReadRunnable(2));
		rt1.start();

		Thread rt2 = new Thread(this.buildReadRunnable(2));
		rt2.start();

		String string1 = "abcdefghijklmnopqrstuvwxyz";
		Thread wt1 = new Thread(this.buildWriteRunnable(string1.getBytes(), 3));
		wt1.start();

		String string2 = "01234567890123456789";
		Thread wt2 = new Thread(this.buildWriteRunnable(string2.getBytes(), 3));
		wt2.start();

		wt2.join();
		wt1.join();
		this.pipe.getOutputStream().close();
		rt2.join();
		rt1.join();

		// we just want to make sure the above code does not suspend
		// indefinitely or trigger a deadlock; uncomment the appropriate line
		// in #buildReadRunnable(int) to see the results on the console - it
		// will probably look something like this:
		//     abc012def345ghi678jkl901mno234pqr567stu89vwxyz
		assertFalse(this.exCaught);
	}

	private Runnable buildReadRunnable(final int chunkSize) {
		return new Runnable() {
			public void run() {
				try {
					InputStream in = PipeTests.this.pipe.getInputStream();
					byte[] bytes = new byte[chunkSize];
					boolean moreBytes = true;
					while (moreBytes) {
						int totalBytesRead = 0;
						while (totalBytesRead < chunkSize) {
							int bytesRead = in.read(bytes, totalBytesRead, bytes.length - totalBytesRead);
							if (bytesRead == -1) {
								moreBytes = false;
								break;
							}
							totalBytesRead += bytesRead;
						}
						// uncomment the following line to see what happens
						// System.out.print(new String(bytes, 0, totalBytesRead));
						Thread.sleep(100);
					}
				} catch (Exception ex) {
					PipeTests.this.exCaught = true;
					ex.printStackTrace();
				}
			}
		};
	}

	private Runnable buildWriteRunnable(final byte[] bytes, final int chunkSize) {
		return new Runnable() {
			public void run() {
				try {
					OutputStream out = PipeTests.this.pipe.getOutputStream();
					int totalBytesWritten = 0;
					int remainingBytes = bytes.length;
					while (remainingBytes > 0) {
						int bytesWritten = Math.min(chunkSize, remainingBytes);
						out.write(bytes, totalBytesWritten, bytesWritten);
						totalBytesWritten += bytesWritten;
						remainingBytes -= bytesWritten;
						Thread.sleep(100);
					}
				} catch (Exception ex) {
					PipeTests.this.exCaught = true;
					ex.printStackTrace();
				}
			}
		};
	}

}
