/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.semanticwb.datamanager;

import java.io.IOException;
import java.util.Iterator;

/**
 *
 * @author javiersolis
 */
public class SWBProcess {
    private SWBScriptEngine eng=null;
    private DataObject dataObject=null;
    private DataObject states=null;
    private DataObject transitions=null;
    private DataObject transitionStates=null;

    public SWBProcess(SWBScriptEngine eng, DataObject dataObject) {
        this.dataObject=dataObject;
        this.eng=eng;
    }
    
    public DataObject getDataObject() {
        return dataObject;
    }

    /**
     * Returns all States of the process
     * @return DataObject maped by id
     * @throws IOException 
     */
    public DataObject getStates() throws IOException
    {
        if(states==null)
        {
            synchronized(this)
            {
                if(states==null)
                {
                    DataObject query=new DataObject();
                    query.addSubObject("data").addParam("process", dataObject.getId());
                    states=eng.getDataSource("SWBF_State").mapById(query);
                }
            }
        }
        return states;
    }
    
    /**
     * Returns one state of the process by id
     * @param id
     * @return DataObject of the state
     * @throws IOException 
     */
    public DataObject getState(String id) throws IOException
    {
        return getStates().getDataObject(id);
    }    
    
    public DataObject findState(String prop, String value) throws IOException
    {
        DataObject ret=null;
        Iterator it=getStates().values().iterator();
        while (it.hasNext()) {
            DataObject obj = (DataObject)it.next();
            if(obj.getString("prop").equals(prop) && obj.getString("value").equals(value))
            {
                ret=obj;
            }
        }
        return ret;
    }
    
    /**
     * Returns all transition of the process
     * @return DataObject maped by id
     * @throws IOException 
     */
    public DataObject getTransitions() throws IOException
    {
        if(transitions==null)
        {
            synchronized(this)
            {
                if(transitions==null)
                {
                    DataObject query=new DataObject();
                    query.addSubObject("data").addParam("process", dataObject.getId());
                    transitions=eng.getDataSource("SWBF_Transition").mapById(query);
                }
            }
        }
        return transitions;
    }  
    
    /**
     * returns a Transition of the process by the id
     * @param id
     * @return DataObject of the Transition
     * @throws IOException 
     */
    public DataObject getTransition(String id) throws IOException
    {
        return getTransitions().getDataObject(id);
    }    
    
    /**
     * Retirns all transitionStates of the process
     * @return DataObject maped by id
     * @throws IOException 
     */
    public DataObject getTransitionStates() throws IOException
    {
        if(transitionStates==null)
        {
            synchronized(this)
            {
                if(transitionStates==null)
                {
                    DataObject query=new DataObject();
                    query.addSubObject("data").addParam("process", dataObject.getId());
                    transitionStates=eng.getDataSource("SWBF_TransitionStates").mapById(query);
                }
            }
        }
        return transitionStates;
    }     
    
    /**
     * Retirns all transitionStates of the process
     * @param transitionId
     * @return DataList of TransitionStates
     * @throws IOException 
     */
    public DataList<DataObject> getTransitionStates(String transitionId) throws IOException
    {
        DataList list=new DataList();
        if(transitionId!=null)
        {
            DataObject ts=getTransitionStates();
            Iterator it=ts.values().iterator();
            while (it.hasNext()) 
            {
                DataObject obj = (DataObject)it.next();
                if(transitionId.equals(obj.getString("transition")))
                {
                    list.add(obj);
                }
            }
        }
        return list;
    }  

    /**
     * Returns an especific TransitionState by id
     * @param transitionStateId
     * @return DataObject of the TransitionState
     * @throws IOException 
     */
    public DataObject getTransitionState(String transitionStateId) throws IOException
    {
        return getTransitionStates().getDataObject(transitionStateId);
    }    
    
    /**
     * Return a list of the active States
     * @param rec
     * @return DataObject of States mapped by id or Empty Dataobject if rec is null
     * @throws IOException 
     */
    public DataObject getActiveStates(DataObject rec) throws IOException
    {
        DataObject ret=new DataObject();
        if(rec!=null)
        {
            Iterator it=getStates().values().iterator();
            while (it.hasNext()) {
                DataObject state = (DataObject)it.next();
                String prop=state.getString("prop");
                String value=state.getString("value");
                if(prop!=null)prop=prop.substring(prop.indexOf(".")+1);   
                String v=rec.getString(prop);

                if(v!=null && v.equals(value))
                {
                    ret.addParam(state.getId(),state);
                }
            }
        }
        return ret;
    }
    
    
    /**
     * Returns the list of transitions that can be executed by resource id
     * @param eng
     * @param rec_id
     * @return DataList<DataObject> Transition
     * @throws IOException 
     */
    public DataList<DataObject> getNextTransitions(SWBScriptEngine eng, String rec_id) throws IOException
    {
        return getNextTransitions(eng, eng.getDataSource(dataObject.getString("ds")).fetchObjById(rec_id));
    }
    
    /**
     * Returns the list of transitions that can be executed by resource object
     * @param eng
     * @param rec
     * @return DataList<DataObject> Transition
     * @throws IOException 
     */
    public DataList getNextTransitions(SWBScriptEngine eng, DataObject rec) throws IOException
    {
        DataList ret=new DataList();
        if(rec==null)
        {
            DataObject trans=getUserInitTransition(eng);
            if(trans!=null)
            {
                ret.add(trans);
            }
        }else
        {
            DataObject activeStates=getActiveStates(rec);
            //System.out.println("rec:"+rec.getId());
            //System.out.println("activeStates:"+activeStates);
            if(activeStates.isEmpty())
            {
                DataObject trans=getUserInitTransition(eng);
                if(trans!=null)
                {
                    ret.add(trans);
                }                
            }else
            {
                Iterator it2=getTransitions().values().iterator();              //TODO: Transiciones script de todos los procesos
                while (it2.hasNext()) {
                    DataObject trans = (DataObject)it2.next();

                    DataList roles=trans.getDataList("roles_view");
                    if(roles==null || eng.hasUserAnyRole(roles))
                    {
                        DataList<String> sources=trans.getDataList("sourceStates");
                        if(sources!=null)
                        {
                            if(activeStates.keySet().containsAll(sources))
                            {
                                ret.add(trans);
                            }
                        }
                    }
                }                
            }
        }
        return ret;
    }    
    
    /** Returns the Init Transition
     * Returns InitTransition
     * @return
     * @throws IOException 
     */
    public DataObject getInitTransition() throws IOException
    {
        return getTransition(dataObject.getString("initTransition"));
    }
    
    
    /**
     * Return the init Transition if the user have access to it
     * @param eng
     * @return
     * @throws IOException 
     */
    public DataObject getUserInitTransition(SWBScriptEngine eng) throws IOException
    {
        DataObject trans=getInitTransition();
        if(trans!=null)
        {
            if(eng.hasUserAnyRole(trans.getDataList("roles_view")))
            {
                return trans;
            }
        }
        return null;
    }
    
}
