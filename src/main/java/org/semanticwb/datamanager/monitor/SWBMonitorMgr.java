/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.semanticwb.datamanager.monitor;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.semanticwb.datamanager.DataObject;

/**
 *
 * @author javiersolis
 */
public class SWBMonitorMgr {
    
    public static boolean active=false;
    private static HashMap<String,SWBMonitorRecord> stats=new HashMap();    
    private static HashMap<Long,LinkedList> threads=new HashMap();    
    
    
    private SWBMonitorMgr()
    {
        
    }
    
    public static void startMonitor(String path)
    {
        long tid=Thread.currentThread().getId();
        LinkedList<DataObject> list=threads.get(tid);
        if(list==null)
        {
            list=new LinkedList();
            threads.put(tid, list);
        }
        list.addLast(new DataObject().addParam("path", path).addParam("time", System.currentTimeMillis()));
    }
    
    public static void endMonitor()
    {
        long time=System.currentTimeMillis();
        long tid=Thread.currentThread().getId();
        LinkedList<DataObject> list=threads.get(tid);
        if(list!=null)
        {            
            String path="";
            for(DataObject obj: list)
            {
                path+=" "+obj.getString("path");
            }
            DataObject obj=list.pollLast();
            addTime(path,time-obj.getLong("time"));
        }
    }  
    
    public static void cancelMonitor()
    {
        long time=System.currentTimeMillis();
        long tid=Thread.currentThread().getId();
        LinkedList<DataObject> list=threads.get(tid);
        if(list!=null)
        {            
            DataObject obj=list.pollLast();
        }        
    }    
        
    private static void addTime(String path, long time)
    {
        SWBMonitorRecord record=stats.get(path);
        if(record==null)
        {
            synchronized(stats)
            {
                record=stats.get(path);
                if(record==null)
                {
                    record=new SWBMonitorRecord();
                    stats.put(path, record);
                }
            }
        }
        record.addTime(time);
    }
    
    public static Map<String,SWBMonitorRecord> getStats()
    {
        return stats;
    }
    
}
