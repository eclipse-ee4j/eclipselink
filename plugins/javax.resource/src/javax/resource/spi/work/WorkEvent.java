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

package javax.resource.spi.work;

import java.lang.Object;
import java.lang.Runnable;
import java.lang.Exception;
import java.lang.Throwable;
import java.util.EventObject;

/**
 * This class models the various events that occur during the processing of
 * a <code>Work</code> instance.
 *
 * @version 1.0
 * @author  Ram Jeyaraman
 */
public class WorkEvent extends EventObject {

    /**
     * Indicates <code>Work</code> instance has been accepted.
     */
    public static final int WORK_ACCEPTED = 1;

    /**
     * Indicates <code>Work</code> instance has been rejected.
     */
    public static final int WORK_REJECTED = 2;

    /**
     * Indicates <code>Work</code> instance has started execution.
     */
    public static final int WORK_STARTED = 3;

    /**
     * Indicates <code>Work</code> instance has completed execution.
     */
    public static final int WORK_COMPLETED = 4;

    /**
     * The event type.
     */
    private int type;

    /**
     * The <code>Work</code> object on which the event occured.
     */
    private Work work;

    /**
     * The exception that occured during <code>Work</code> processing.
     */
    private WorkException exc;

    /**
     * The start delay duration (in milliseconds).
     */
    private long startDuration = WorkManager.UNKNOWN;

    /**
     * Constructor.
     *
     * @param source The object on which the event initially 
     * occurred.
     *
     * @param type The event type.
     *
     * @param work The <code>Work</code> object on which 
     * the event occured.
     *
     * @param exc The exception that occured during 
     * <code>Work</code> processing.

    */
    public WorkEvent(Object source, int type, Work work, WorkException exc) {
	super(source);
	this.type = type;
	this.work =  work;
	this.exc = exc;
    }

    /**
     * Constructor.
     *
     * @param source The object on which the event initially 
     * occurred.
     *
     * @param type The event type.
     *
     * @param work The <code>Work</code> object on which 
     * the event occured.
     *
     * @param exc The exception that occured during 
     * <code>Work</code> processing.
     *
     * @param startDuration The start delay duration 
     * (in milliseconds).
     */
    public WorkEvent(Object source, int type, Work work, WorkException exc,
            long startDuration) {
	this(source, type, work, exc);
	this.startDuration = startDuration;
    }

    /**
     * Return the type of this event.
     *
     * @return the event type.
     */
    public int getType() { return this.type; }

    /**
     * Return the <code>Work</code> instance which is the cause of the event.
     *
     * @return the <code>Work</code> instance.
     */
    public Work getWork() { return this.work; }

    /**
     * Return the start interval duration.
     *
     * @return the time elapsed (in milliseconds) since the <code>Work</code>
     * was accepted, until the <code>Work</code> execution started. Note, 
     * this does not offer real-time guarantees. It is valid to return -1, if
     * the actual start interval duration is unknown.
     */
    public long getStartDuration() { return this.startDuration; }

    /**
     * Return the <code>WorkException</code>. The actual 
     * <code>WorkException</code> subtype returned depends on the type of the
     * event.
     *
     * @return a <code>WorkRejectedException</code> or a 
     * <code>WorkCompletedException</code>, if any.
     */
    public WorkException getException() { return this.exc; }
}
