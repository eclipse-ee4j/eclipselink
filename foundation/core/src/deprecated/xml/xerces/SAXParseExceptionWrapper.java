/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package deprecated.xml.xerces;


/**
 * This class wraps the <code>org.xml.sax.SAXParseException</code> class,
 * providing indirect access to its protocol via an interface.
 * This allows easy access to the protocol to those classes not loaded
 * by the custom class loader that may load the SAXParseException class.
 *
 * @see org.xml.sax.SAXParseException
 * @see deprecated.xml.SAXParseException
 *
 * @author Big Country
 * @since TOPLink/Java 3.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.oxm}
 */
public class SAXParseExceptionWrapper implements deprecated.xml.SAXParseException {
    private org.xml.sax.SAXParseException wrappedException;

    /**
     *Construct a new instance for the specified wrapped exception.
     */
    public SAXParseExceptionWrapper(org.xml.sax.SAXParseException wrappedException) {
        super();
        this.wrappedException = wrappedException;
    }

    // methods for org.xml.sax.SAXParseException:

    /**
     * @see org.xml.sax.SAXParseException#getColumnNumber()
     */
    public int getColumnNumber() {
        return wrappedException.getColumnNumber();
    }

    /**
     * @see org.xml.sax.SAXParseException#getLineNumber()
     */
    public int getLineNumber() {
        return wrappedException.getLineNumber();
    }

    /**
     * @see org.xml.sax.SAXParseException#getPublicId()
     */
    public String getPublicId() {
        return wrappedException.getPublicId();
    }

    /**
     * @see org.xml.sax.SAXParseException#getSystemId()
     */
    public String getSystemId() {
        return wrappedException.getSystemId();
    }

    // methods for org.xml.sax.SAXException:

    /**
     * @see org.xml.sax.SAXException#getException()
     */
    public Exception getException() {
        return wrappedException.getException();
    }

    // methods for java.lang.Throwable:

    /**
     * @see java.lang.Throwable#fillInStackTrace()
     */
    public Throwable fillInStackTrace() {
        return wrappedException.fillInStackTrace();
    }

    /**
     * @see java.lang.Throwable#getLocalizedMessage()
     */
    public String getLocalizedMessage() {
        return wrappedException.getLocalizedMessage();
    }

    /**
     * @see java.lang.Throwable#getMessage()
     */
    public String getMessage() {
        return wrappedException.getMessage();
    }

    /**
     * @see java.lang.Throwable#printStackTrace()
     */
    public void printStackTrace() {
        wrappedException.printStackTrace();
    }

    /**
     * @see java.lang.Throwable#printStackTrace(java.io.PrintStream)
     */
    public void printStackTrace(java.io.PrintStream s) {
        wrappedException.printStackTrace(s);
    }

    /**
     * @see java.lang.Throwable#printStackTrace(java.io.PrintWriter)
     */
    public void printStackTrace(java.io.PrintWriter s) {
        wrappedException.printStackTrace(s);
    }
}