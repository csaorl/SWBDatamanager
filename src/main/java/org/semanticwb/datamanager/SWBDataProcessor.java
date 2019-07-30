/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.semanticwb.datamanager;

import java.util.Iterator;
import org.semanticwb.datamanager.script.ScriptObject;

/**
 *
 * @author javier.solis
 */
public class SWBDataProcessor implements Comparable<SWBDataProcessor>
{

    /**
     *
     */
    public static final String METHOD_REQUEST="request";

    /**
     *
     */
    public static final String METHOD_RESPONSE="response";
    
    
    private String name=null;
    private ScriptObject script=null;
    
    /**
     *
     * @param name
     * @param script
     */
    protected SWBDataProcessor(String name, ScriptObject script)
    {
        this.name=name;
        this.script=script;
    }

    /**
     * Regresa Nombre del DataSource
     * @return String
     */
    public String getName() {
        return name;
    }
    
    /**
     * Regresa ScriptObject con el script con la definición del datasource definida el el archivo js
     * @return ScriptObject
     */
    public ScriptObject getDataProcessorScript()
    {
        return script;
    }      

    @Override
    public int compareTo(SWBDataProcessor o) 
    {
        return script.getInt("order")>o.getDataProcessorScript().getInt("order")?1:-1;
    }
}
