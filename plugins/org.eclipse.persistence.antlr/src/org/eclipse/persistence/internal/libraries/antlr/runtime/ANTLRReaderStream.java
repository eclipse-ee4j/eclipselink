package org.eclipse.persistence.internal.libraries.antlr.runtime;

import java.io.*;

/** Vacuum all input from a Reader and then treat it like a StringStream.
 *  Manage the buffer manually to avoid unnecessary data copying.
 *
 *  If you need encoding, use ANTLRInputStream.
 */
public class ANTLRReaderStream extends ANTLRStringStream {
	public static final int READ_BUFFER_SIZE = 1024;
	public static final int INITIAL_BUFFER_SIZE = 1024;

	public ANTLRReaderStream() {
	}

	public ANTLRReaderStream(Reader r) throws IOException {
		this(r, INITIAL_BUFFER_SIZE, READ_BUFFER_SIZE);
	}

	public ANTLRReaderStream(Reader r, int size) throws IOException {
		this(r, size, READ_BUFFER_SIZE);
	}

	public ANTLRReaderStream(Reader r, int size, int readChunkSize) throws IOException {
		load(r, size, readChunkSize);
	}

	public void load(Reader r, int size, int readChunkSize)
		throws IOException
	{
		if ( r==null ) {
			return;
		}
		if ( size<=0 ) {
			size = INITIAL_BUFFER_SIZE;
		}
		if ( readChunkSize<=0 ) {
			size = READ_BUFFER_SIZE;
		}
		// System.out.println("load "+size+" in chunks of "+readChunkSize);
		try {
			// alloc initial buffer size.
			data = new char[size];
			// read all the data in chunks of readChunkSize
			int numRead=0;
			int p = 0;
			do {
				if ( p+readChunkSize > data.length ) { // overflow?
					// System.out.println("### overflow p="+p+", data.length="+data.length);
					char[] newdata = new char[data.length*2]; // resize
					System.arraycopy(data, 0, newdata, 0, data.length);
					data = newdata;
				}
				numRead = r.read(data, p, readChunkSize);
				// System.out.println("read "+numRead+" chars; p was "+p+" is now "+(p+numRead));
				p += numRead;
			} while (numRead!=-1); // while not EOF
			// set the actual size of the data available;
			// EOF subtracted one above in p+=numRead; add one back
			super.n = p+1;
			//System.out.println("n="+n);
		}
		finally {
			r.close();
		}
	}
}
