/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.semanticwb.datamanager.monitor;

/**
 *
 * @author javiersolis
 */
public class SWBMonitorRecord {
    private long times;
    private long minTime;
    private long maxTime;
    private long averageTime;
    private long firstTime;
    private long lastTime;

    public SWBMonitorRecord() {
        minTime=-1;
        firstTime=-1;
    }
    
    public void addTime(long time)
    {
        times++;
        if(firstTime==-1)firstTime=time;
        if(minTime==-1 || time<minTime)minTime=time;
        if(time>maxTime)maxTime=time;
        averageTime=((averageTime*(times-1))+time)/times;
        lastTime=time;
    }

    public long getTimes() {
        return times;
    }

    public long getAverageTime() {
        return averageTime;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public long getMinTime() {
        return minTime;
    }

    public long getFirstTime() {
        return firstTime;
    }

    public long getLastTime() {
        return lastTime;
    }        
    
    public String toString()
    {
        return "times:"+times+", averageTime:"+averageTime+", minTime:"+minTime+", maxTime:"+maxTime;
    }
    
}
