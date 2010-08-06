/*******************************************************************************
 * Copyright (c) 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Chris Delahunt 
 *       - Bug 318187 - NegativeArraySizeException on JCEEncryptor.decryptPassword()   
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.simultaneous;

import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.internal.security.JCEEncryptor;

/**
 * @author Chris Delahunt
 * @date June 26, 2003
 *  Test fix for 318187:
 *  java.lang.NegativeArraySizeException is thrown when threads concurrently access the 
 *  JCEEncryptor.decrypt(password) 
 *  
 */
public class ConcurrentDecryptionTest extends AutoVerifyTestCase {
    //used to signal the threads to stop, accessed directly from the threads
    public boolean run;
    public Exception error;

    //number of concurrent threads to use
    public int threadCount = 100;
    //password to encrypt
    public String password = "test1";
    //wait time for the error to occur
    public int sleepTime = 8000;

    //accessed directly from the threads
    private JCEEncryptor encryptor;
    private String encryptedPassword;
    
    
    protected void setup() throws Throwable {
        encryptor = new JCEEncryptor();
        encryptedPassword = encryptor.encryptPassword(password);
    }

    @Override
    protected void test() throws Throwable {
        run = true;
        error = null;
        try {
            Thread[] thread = new Thread[threadCount];
            for(int i=0; i<threadCount; i++) {
                thread[i]=new Thread(new Runner1(this));
            }
            for(int i=0;i<threadCount; i++){
                thread[i].start();
            }
            try {
                Thread.currentThread().sleep(sleepTime);
            } catch (InterruptedException e) {
                //exception could be ignored
                e.printStackTrace();
            }
        } finally {
            //set the flag so started threads will end.
            run = false;
        }
    }
    
    protected void verify() throws Throwable {
        if (error!=null){
            throw new TestErrorException("error encountered: "+error, error);
        }
    }
    
    //runnable that repeatedly calls decryptPassword until the run flag is set to false
    class Runner1 implements Runnable {
        protected ConcurrentDecryptionTest concurrentDecryptionTest;
        public Runner1(ConcurrentDecryptionTest concurrentDecryptionTest) {
            this.concurrentDecryptionTest = concurrentDecryptionTest;
        }
        public void run() {
            try {
                while (concurrentDecryptionTest.run && (concurrentDecryptionTest.error == null) ){
                    concurrentDecryptionTest.encryptor.decryptPassword(concurrentDecryptionTest.encryptedPassword);
                }
            } catch (Exception e){
                concurrentDecryptionTest.run = false;
                concurrentDecryptionTest.error = e;
            }
        }
    }

}
