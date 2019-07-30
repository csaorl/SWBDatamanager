/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.semanticwb.datamanager;

import org.semanticwb.datamanager.script.ScriptObject;


/**
 *
 * @author javier.solis
 */
public class SWBDataService implements Comparable<SWBDataService>
{

    /**
     *
     */
    public static final String METHOD_SERVICE="service";
        
    private String name=null;
    private ScriptObject script=null;
    
    /**
     *
     * @param name
     * @param script
     */
    protected SWBDataService(String name, ScriptObject script)
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
    public ScriptObject getDataServiceScript()
    {
        return script;
    }      

    @Override
    public int compareTo(SWBDataService o) {
        return script.getInt("order")>o.getDataServiceScript().getInt("order")?1:-1;
    }
   
}
