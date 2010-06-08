/*
 * The contents of this file are subject to the terms 
 * of the Common Development and Distribution License 
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at 
 * https://glassfish.dev.java.net/public/CDDLv1.0.html or
 * glassfish/bootstrap/legal/CDDLv1.0.txt.
 * See the License for the specific language governing 
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL 
 * Header Notice in each file and include the License file 
 * at glassfish/bootstrap/legal/CDDLv1.0.txt.  
 * If applicable, add the following below the CDDL Header, 
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information: 
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 */
package javax.ejb;

/**
 * The EJBException exception is thrown by an enterprise Bean instance to 
 * its container to report that the invoked business method or callback method
 * could not be completed because of an unexpected error (e.g. the instance 
 * failed to open a database connection).
 */
public class EJBException extends java.lang.RuntimeException {
    /**
     * @serial
     */
    private Exception causeException = null;

    /**
     * Constructs an EJBException with no detail message.
     */  
    public EJBException() {
    }

    /**
     * Constructs an EJBException with the specified
     * detailed message.
     */  
    public EJBException(String message) {
        super(message);
    }

    /**
     * Constructs an EJBException that embeds the originally thrown exception.
     */  
    public EJBException(Exception  ex) {
        super();
	causeException = ex;
    }

    /**
     * Constructs an EJBException that embeds the originally thrown exception
     * with the specified detail message. 
     */  
    public EJBException(String message, Exception  ex) {
        super(message);
	causeException = ex;
    }


    /**
     * Obtain the exception that caused the EJBException being thrown.
     */
    public Exception getCausedByException() {
	return causeException;
    }

    /**
     * Returns the detail message, including the message from the nested
     * exception if there is one.
     */
    public String getMessage() {
	String msg = super.getMessage();
        if (causeException == null)
            return msg;
        else if ( msg == null ) {
            return "nested exception is: " + causeException.toString();
	}
	else {
            return msg + "; nested exception is: " + causeException.toString();
	}
    }

    /**
     * Prints the composite message and the embedded stack trace to
     * the specified stream <code>ps</code>.
     * @param ps the print stream
     */
    public void printStackTrace(java.io.PrintStream ps)
    {
        if (causeException == null) {
            super.printStackTrace(ps);
        } else {
            synchronized(ps) {
		ps.println(this);
		// Print the cause exception first, so that the output
		// appears in stack order (i.e. innermost exception first)
                causeException.printStackTrace(ps);
                super.printStackTrace(ps);
            }
        }
    }

    /** 
     * Prints the composite message to <code>System.err</code>.
     */ 
    public void printStackTrace()
    {
        printStackTrace(System.err);
    }

    /**
     * Prints the composite message and the embedded stack trace to
     * the specified print writer <code>pw</code>.
     * @param pw the print writer
     */
    public void printStackTrace(java.io.PrintWriter pw)
    {
        if (causeException == null) {
            super.printStackTrace(pw);
        } else {
            synchronized(pw) {
		pw.println(this);
		// Print the cause exception first, so that the output
		// appears in stack order (i.e. innermost exception first)
                causeException.printStackTrace(pw);
		super.printStackTrace(pw);
            }
        }
    }
}
