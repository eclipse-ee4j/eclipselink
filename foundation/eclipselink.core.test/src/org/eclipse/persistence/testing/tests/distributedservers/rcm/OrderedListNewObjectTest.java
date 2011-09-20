package org.eclipse.persistence.testing.tests.distributedservers.rcm;

import java.util.Iterator;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.optimisticlocking.ListHolder;
import org.eclipse.persistence.testing.models.optimisticlocking.ListItem;
import org.eclipse.persistence.testing.tests.distributedservers.DistributedServer;
import org.eclipse.persistence.testing.tests.distributedservers.DistributedServersModel;

// Bug 358261
public class OrderedListNewObjectTest extends ConfigurableCacheSyncDistributedTest {

    public OrderedListNewObjectTest(){
        super();
        cacheSyncConfigValues.put(ListHolder.class, new Integer(ClassDescriptor.SEND_NEW_OBJECTS_WITH_CHANGES));
        cacheSyncConfigValues.put(ListItem.class, new Integer(ClassDescriptor.SEND_NEW_OBJECTS_WITH_CHANGES));
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
        
        // put our ListHolder in the cache
        ListHolder holder = (ListHolder)server.getDistributedSession().readObject(ListHolder.class);
        holder.getItems().size();
        
        // remove the objects, this should updabe both caches
        UnitOfWork uow = getSession().acquireUnitOfWork();
        holder = (ListHolder)uow.readObject(ListHolder.class);
        ListItem item = holder.getItems().get(0);
        uow.registerObject(item);
        uow.deleteObject(item);
        uow.deleteObject(holder);
        uow.commit();
        
        // add a new object.  these changes should be sent
        uow = getSession().acquireUnitOfWork();
        holder = new ListHolder();
        holder = (ListHolder)uow.registerObject(holder);
        item = new ListItem();
        item.setDescription("test");
        item = (ListItem)uow.registerObject(item);
        holder.getItems().add(item);
        item.setHolder(holder);
        uow.commit();
    }
    
    public void verify(){
        // ensure the changes are propgated
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e){};
        DistributedServer server = (DistributedServer)DistributedServersModel.getDistributedServers().get(0);

        ListHolder holder = (ListHolder)server.getDistributedSession().readObject(ListHolder.class);
        if (holder.getItems().size() != 1){
            throw new TestErrorException("Incorrect number of items");
        }
    }
    
    public void reset(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        ListHolder holder = (ListHolder)uow.readObject(ListHolder.class);
        Iterator i = holder.getItems().iterator();
        while (i.hasNext()){
            uow.deleteObject(i.next());
        }
        uow.deleteObject(holder);
        uow.commit();
        super.reset();
    }
    
}
