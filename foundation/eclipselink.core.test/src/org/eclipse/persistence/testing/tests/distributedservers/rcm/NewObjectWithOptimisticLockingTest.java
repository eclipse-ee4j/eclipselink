package org.eclipse.persistence.testing.tests.distributedservers.rcm;

import java.util.Iterator;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.optimisticlocking.ListHolder;
import org.eclipse.persistence.testing.models.optimisticlocking.ListItem;
import org.eclipse.persistence.testing.tests.distributedservers.DistributedServer;
import org.eclipse.persistence.testing.tests.distributedservers.DistributedServersModel;

// test for bug 357103
public class NewObjectWithOptimisticLockingTest extends ConfigurableCacheSyncDistributedTest {

    protected ListHolder holder = null;
    
    public NewObjectWithOptimisticLockingTest(){
        super();
        cacheSyncConfigValues.put(ListHolder.class, new Integer(ClassDescriptor.SEND_OBJECT_CHANGES));
        cacheSyncConfigValues.put(ListItem.class, new Integer(ClassDescriptor.SEND_OBJECT_CHANGES));
    }
    
    public void setup(){
        super.setup();
        
        UnitOfWork uow = getSession().acquireUnitOfWork();
        ListHolder holder = new ListHolder();
        holder = (ListHolder)uow.registerObject(holder);
        ListItem item = new ListItem();
        item.setDescription("test");
        item = (ListItem)uow.registerObject(item);
        holder.getItems().add(item);
        item.setHolder(holder);
        uow.commit();
    }
    
    public void test(){
        DistributedServer server = (DistributedServer)DistributedServersModel.getDistributedServers().get(0);
        UnitOfWork uow = server.getDistributedSession().acquireUnitOfWork();
        holder = (ListHolder)uow.readObject(ListHolder.class);
        ListItem item = new ListItem();
        item.setDescription("test2");
        item = (ListItem)uow.registerObject(item);
        holder.getItems().add(item);
        uow.commit();
    }
    
    public void verify(){
        // ensure the changes are propgated
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e){};
        holder = (ListHolder)getSession().readObject(ListHolder.class);
        if (holder.getItems().size() != 2){
            throw new TestErrorException("Incorrect number of items");
        }
        
        boolean found = false;
        Iterator i = holder.getItems().iterator();
        while (i.hasNext()){
            ListItem item = (ListItem)i.next();
            if (item.getDescription() != null && item.getDescription().equals("test2")){
                found = true;
            }
        }
        if (!found){
            throw new TestErrorException("A new object was not complete propogated.");
        }

    }
    
    public void reset(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        holder = (ListHolder)uow.readObject(ListHolder.class);
        Iterator i = holder.getItems().iterator();
        while (i.hasNext()){
            uow.deleteObject(i.next());
        }
        uow.deleteObject(holder);
        uow.commit();
        super.reset();
    }
}
