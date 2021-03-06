package org.hisp.dhis.validationrule.action;

/*
 * Copyright (c) 2004-2007, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.validationrule.util.Operand;
import org.hisp.dhis.options.displayproperty.DisplayPropertyHandler;
import org.hisp.dhis.validationrule.util.FilterUtil;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Lars Helge Overland
 * @version $Id: GetFilteredDataElementsActionIN.java 5730 2008-09-20 14:32:22Z brajesh $
 */
public class GetFilteredDataElementsActionIN
    extends ActionSupport
{
    private static final int ALL = 0;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    // -------------------------------------------------------------------------
    // Comparator
    // -------------------------------------------------------------------------

    private Comparator<DataElement> dataElementComparator;

    public void setDataElementComparator( Comparator<DataElement> dataElementComparator )
    {
        this.dataElementComparator = dataElementComparator;
    }

    // -------------------------------------------------------------------------
    // DisplayPropertyHandler
    // -------------------------------------------------------------------------

    private DisplayPropertyHandler displayPropertyHandler;

    public void setDisplayPropertyHandler( DisplayPropertyHandler displayPropertyHandler )
    {
        this.displayPropertyHandler = displayPropertyHandler;
    }

    // -------------------------------------------------------------------------
    // Parameters
    // -------------------------------------------------------------------------

    private int dataElementGroupId;

    public void setDataElementGroupId( int dataElementGroupId )
    {
        this.dataElementGroupId = dataElementGroupId;
    }

    private String filter;

    public void setFilter( String filter )
    {
        this.filter = filter;
    }

    private List<DataElement> dataElements;

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }
    
    private List<Operand> operands = new ArrayList<Operand>();

    public List<Operand> getOperands()
    {
        return operands;
    }

    // -------------------------------------------------------------------------
    // Execute
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        // ---------------------------------------------------------------------
        // DataElementGroup filter
        // ---------------------------------------------------------------------

        if ( dataElementGroupId == ALL )
        {
            dataElements = new ArrayList<DataElement>( dataElementService.getDataElementsByType( DataElement.VALUE_TYPE_INT ) );
        }
        else
        {
            Collection<DataElement> groupElements = dataElementService.getDataElementGroup( dataElementGroupId ).getMembers();
            
            dataElements = new ArrayList<DataElement>( FilterUtil.getIntegerDataElements( groupElements ) );
        }

        Collections.sort( dataElements, dataElementComparator );

        //dataElements = displayPropertyHandler.handleDataElements( dataElements );
        
        displayPropertyHandler.handle( dataElements );

        // ---------------------------------------------------------------------
        // String filter
        // ---------------------------------------------------------------------

        Iterator iterator = dataElements.iterator();

        while ( iterator.hasNext() )
        {
            DataElement element = (DataElement) iterator.next();

            String name = element.getName();

            if ( name.length() < filter.length()
                || !( name.substring( 0, filter.length() ).toLowerCase().equals( filter.toLowerCase() ) ) )
            {
                iterator.remove();
            }
        }
        
        for( DataElement dataElement : dataElements )
        {
        	
        	DataElementCategoryCombo categoryCombo = dataElement.getCategoryCombo();
        	Set<DataElementCategoryOptionCombo> optionCombos = categoryCombo.getOptionCombos();
        	
        	if( optionCombos.size() > 1 )
        	{       		
        		        		
        		for( DataElementCategoryOptionCombo optionCombo : optionCombos )
            	{             		           		
            		Operand operand = new Operand( dataElement.getId(), optionCombo.getId(), dataElement.getName() + dataElementCategoryService.getDataElementCategoryOptionCombo( optionCombo ).getName() );
            		operands.add( operand );
            	}       		
        		
        	}
        	else
        	{        		 
        		Operand operand = new Operand( dataElement.getId(), optionCombos.iterator().next().getId(), dataElement.getName() );
        		operands.add( operand );
        	}
        	        	
        } 

        return SUCCESS;
    }
}
