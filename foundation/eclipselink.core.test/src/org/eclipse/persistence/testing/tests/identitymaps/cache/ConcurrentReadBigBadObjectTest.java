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
package org.eclipse.persistence.testing.tests.identitymaps.cache;

import java.util.Vector;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.tests.clientserver.Server;
import org.eclipse.persistence.testing.models.bigbad.*;


/**
 * This test is set up to have one thread build a BigBadObject instance, while multiple other threads
 * attempt to access the same instance from the cache.  The test fails if any of the threads access 
 * an incomplete instance of the BigBadObject object - where it has been only partially built.  Only
 * the "number02" attribute is checked, as all mapping weights are changed so this mapping is 
 * built last.  Built to test bug 4772232
 */
public class ConcurrentReadBigBadObjectTest extends TestCase {
    public ConcurrentReadBigBadObjectTest() {
    }
    
    BigBadObject referenceObject;
    protected int numOftries = 300;
    protected int numOfThreads= 10;
    protected boolean failed;
    protected Server server;
    protected int OrigDirectMapWeight;
    
    
    public void setup() throws Exception{
        
        DatabaseLogin login;
        login = (DatabaseLogin) getSession().getLogin().clone();
        server = new Server(login);
        server.serverSession.setLogLevel(getSession().getLogLevel());
        server.serverSession.setLog(getSession().getLog());
                

        server.copyDescriptors(getSession());
        ClassDescriptor d = (server.serverSession).getClassDescriptor(BigBadObject.class);
        DatabaseMapping m;
        
        Vector v = d.getMappings();
        int mappings = v.size();
        int i =0;
        while (i<mappings){
            m = (DatabaseMapping)v.get(i);
            m.setWeight(new Integer(Integer.MAX_VALUE-1));
            i++;
        }
        
        m = d.getMappingForAttributeName("number02");
        m.setWeight(new Integer(Integer.MAX_VALUE));
        
        server.login();
        server.serverSession.setLogLevel(getSession().getLogLevel());
        server.serverSession.setLog(getSession().getLog());
    }
    
    public void reset() throws Exception{
        server.logout();
    }
    
    public void test() {
        failed=false;

        referenceObject = (BigBadObject)this.getSession().readObject(BigBadObject.class);
        Reader[] threadList = new Reader[numOfThreads];
        
        for (int i=0;i<numOfThreads;){
            threadList[i]=new Reader(referenceObject, server.serverSession.acquireClientSession(), ++i);
        }
        
        for (int i=0;i<numOfThreads;i++){
             threadList[i].start();
        }
        
        try{
            for (int i=0;i<numOfThreads;i++){
                threadList[i].join();
            }
        }catch (InterruptedException ex){
        }
        int count=0;
        while(!failed && count<numOfThreads){
            if(threadList[count].exception!=null){
                throw threadList[count].exception;
            }
            failed = threadList[count].hadError();
            count++;
        }
        if (failed){
            throw new TestErrorException("Test failed, getFromIdentityMap returned an object before it was finished being built");
        }
        
    }
    
    
    /*
     * Threads to read/access the cache numOftries times.  Only thread 1 will initialize the cache and 
     * read the BigBadObject object instance.
     */
    private class Reader extends Thread {
        protected BigBadObject referenceObject, readObject; 
        protected Session session;
        public int thread;
        public int counter;
        public RuntimeException exception;
        
        protected boolean experienceError = false;
        
        public Reader(BigBadObject object, Session session, int thread){
            this.referenceObject = object;
            this.session = session;
            this.thread= thread;
            counter=0;
        }
        
        public void run(){
            try{
                counter=0;
                while(!experienceError && counter<numOftries){
                    if( thread==1){
                        session.getIdentityMapAccessor().initializeIdentityMap(BigBadObject.class);
                        readObject = (BigBadObject)session.readObject(referenceObject);
                    }else{
                        readObject = (BigBadObject)session.getIdentityMapAccessor().getFromIdentityMap(referenceObject);
                    }
                    if ((readObject!=null)&& 
                        ( (readObject.number02==null) || (!readObject.number02.equals(referenceObject.number02)) ) ){
                        this.experienceError = true;
                    }
                    counter++;
                }
            }catch (RuntimeException ex){
                this.experienceError = true;
                this.exception=ex;
            }
        }
        
        public boolean hadError(){
            return experienceError;
        }
    }
}
