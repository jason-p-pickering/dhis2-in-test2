package org.hisp.dhis.den.action;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.dataelement.CalculatedDataElement;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataEntryForm;
import org.hisp.dhis.dataset.DataEntryFormService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.den.api.LLDataSets;
import org.hisp.dhis.den.api.LLDataValue;
import org.hisp.dhis.den.api.LLDataValueService;
import org.hisp.dhis.den.comments.StandardCommentsManager;
import org.hisp.dhis.den.state.SelectedStateManager;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.minmax.MinMaxDataElement;
import org.hisp.dhis.minmax.MinMaxDataElementService;
import org.hisp.dhis.order.manager.DataElementOrderManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

import com.opensymphony.xwork.Action;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: FormAction.java 4733 2008-03-13 15:26:24Z larshelg $
 */
public class FormAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataElementOrderManager dataElementOrderManager;

    public void setDataElementOrderManager( DataElementOrderManager dataElementOrderManager )
    {
        this.dataElementOrderManager = dataElementOrderManager;
    }

    private LLDataValueService dataValueService;

    public void setDataValueService( LLDataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private DataElementService dataElementService;
    
    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    private DataEntryFormService dataEntryFormService;

    public void setDataEntryFormService( DataEntryFormService dataEntryFormService )
    {
        this.dataEntryFormService = dataEntryFormService;
    }
    
    private StandardCommentsManager standardCommentsManager;

    public void setStandardCommentsManager( StandardCommentsManager standardCommentsManager )
    {
        this.standardCommentsManager = standardCommentsManager;
    }

    private MinMaxDataElementService minMaxDataElementService;

    public void setMinMaxDataElementService( MinMaxDataElementService minMaxDataElementService )
    {
        this.minMaxDataElementService = minMaxDataElementService;
    }

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    } 

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------
    
    private List<String> recordNos;
    
    public List<String> getRecordNos() 
    {
		return recordNos;
	}

    
    private List<OrganisationUnit> orgUnitChildList;

    public List<OrganisationUnit> getOrgUnitChildList() 
    {
		return orgUnitChildList;
	}

    private Map<String, List<LLDataValue>> lldataValueMap;
    
    public Map<String, List<LLDataValue>> getLldataValueMap()
    {
        return lldataValueMap;
    }

    private String isLineListing;
    
    public String getIsLineListing()
    {
        return isLineListing;
    }

    
    private List<DataElement> orderedDataElements = new ArrayList<DataElement>();

    public List<DataElement> getOrderedDataElements()
    {
        return orderedDataElements;
    }

    private Map<Integer, DataValue> dataValueMap;

    public Map<Integer, DataValue> getDataValueMap()
    {
        return dataValueMap;
    }

    private Map<CalculatedDataElement,Integer> calculatedValueMap;
    
    public Map<CalculatedDataElement,Integer> getCalculatedValueMap()
    {
        return calculatedValueMap;
    }
    
    private List<String> standardComments;

    public List<String> getStandardComments()
    {
        return standardComments;
    }

    private Map<String, String> dataElementTypeMap;

    public Map<String, String> getDataElementTypeMap()
    {
        return dataElementTypeMap;
    }

    private Map<Integer, MinMaxDataElement> minMaxMap;

    public Map<Integer, MinMaxDataElement> getMinMaxMap()
    {
        return minMaxMap;
    }

    private Integer integer = new Integer( 0 );

    public Integer getInteger()
    {
        return integer;
    }
    
    private Boolean cdeFormExists;

    /*
     * public Boolean getCustomDataEntryFormExists() { return
     * this.customDataEntryFormExists; }
     */
    
    private DataEntryForm dataEntryForm;

    public DataEntryForm getDataEntryForm()
    {
        return this.dataEntryForm;
    }

    private String customDataEntryFormCode;

    public String getCustomDataEntryFormCode()
    {
        return this.customDataEntryFormCode;
    }
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private Integer selectedDataSetId;

    public void setSelectedDataSetId( Integer selectedDataSetId )
    {
        this.selectedDataSetId = selectedDataSetId;
    }

    public Integer getSelectedDataSetId()
    {
        return selectedDataSetId;
    }

    private Integer selectedPeriodIndex;

    public void setSelectedPeriodIndex( Integer selectedPeriodIndex )
    {
        this.selectedPeriodIndex = selectedPeriodIndex;
    }

    public Integer getSelectedPeriodIndex()
    {
        return selectedPeriodIndex;
    }
    
    private String selDSName;
    
    public String getSelDSName()
    {
        return selDSName;
    }

    private String llbirth;
    
    public String getLlbirth()
    {
        return llbirth;
    }
    
    private String lldeath;
    
	public String getLldeath() 
	{
		return lldeath;
	}

	private String llmdeath;
	
	public String getLlmdeath() 
	{
		return llmdeath;
	}
	
	private String lluuidspe;
	
	public String getLluuidspe() 
	{
		return lluuidspe;
	}

	private String lluuidspep;
	
	public String getLluuidspep() 
	{
		return lluuidspep;
	}
	
	private String lldidsp;
	
	public String getLldidsp() 
	{
		return lldidsp;
	}

	private String llidspl;
	
	public String getLlidspl() 
	{
		return llidspl;
	}
    
	private int maxRecordNo;
    
    public int getMaxRecordNo()
    {
        return maxRecordNo;
    }
    
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        //Intialization
        llbirth = LLDataSets.LL_BIRTHS;
        lldeath = LLDataSets.LL_DEATHS;
        llmdeath = LLDataSets.LL_MATERNAL_DEATHS;
        lluuidspe = LLDataSets.LL_UU_IDSP_EVENTS;
        lluuidspep = LLDataSets.LL_UU_IDSP_EVENTSP;
        lldidsp = LLDataSets.LL_DEATHS_IDSP;
        llidspl = LLDataSets.LL_IDSP_LAB;
        
        OrganisationUnit organisationUnit = selectedStateManager.getSelectedOrganisationUnit();

        DataSet dataSet = selectedStateManager.getSelectedDataSet();

        selDSName = dataSet.getName();

        if(selDSName.equalsIgnoreCase( llbirth ) || selDSName.equalsIgnoreCase( lldeath ) || selDSName.equalsIgnoreCase( llmdeath ) )
        {
            isLineListing = "yes";
        }
        else
        {
            isLineListing = "no";
        }

        /*
          
         if(selDSName.equalsIgnoreCase( llidspl ) || selDSName.equalsIgnoreCase( lldidsp ) || selDSName.equalsIgnoreCase( lluuidspep ) || selDSName.equalsIgnoreCase( llbirth ) || selDSName.equalsIgnoreCase( lldeath ) || selDSName.equalsIgnoreCase( llmdeath ) || selDSName.equalsIgnoreCase( lluuidspe ))
        {
            isLineListing = "yes";
        }
        else
        {
            isLineListing = "no";
        }

*/
        orgUnitChildList = new ArrayList<OrganisationUnit>(organisationUnit.getChildren());
        
        Period period = selectedStateManager.getSelectedPeriod();   

        Collection<DataElement> dataElements = dataSet.getDataElements();        

        if ( dataElements.size() == 0 )
        {
            return SUCCESS;
        }  
        
        Collection<DataElementCategoryOptionCombo> defaultOptionCombo = dataElements.iterator().next().getCategoryCombo().getOptionCombos();        
        
        // ---------------------------------------------------------------------
        // Get the min/max values
        // ---------------------------------------------------------------------

        Collection<MinMaxDataElement> minMaxDataElements = minMaxDataElementService.getMinMaxDataElements(
            organisationUnit, dataElements );
        
        minMaxMap = new HashMap<Integer, MinMaxDataElement>( minMaxDataElements.size() );

        for ( MinMaxDataElement minMaxDataElement : minMaxDataElements )
        {
            minMaxMap.put( minMaxDataElement.getDataElement().getId(), minMaxDataElement );
        }

        // ---------------------------------------------------------------------
        // Order the DataElements
        // ---------------------------------------------------------------------

        //orderedDataElements = dataElementOrderManager.getOrderedDataElements( dataSet );
        
       
        orderedDataElements = dataElementOrderManager.getOrderedDataElements(dataSet);

        // ---------------------------------------------------------------------
        // Get the DataValues and create a map
        // ---------------------------------------------------------------------

        Collection<LLDataValue> dataValues = dataValueService.getDataValues( organisationUnit, period, dataElements, defaultOptionCombo );
        
        dataValueMap = new HashMap<Integer, DataValue>( dataValues.size() );

        /*for ( LLDataValue dataValue : dataValues )
        {
            //dataValueMap.put( dataValue.getDataElement().getId(), dataValue );
        }*/
        
        // ---------------------------------------------------------------------
        // Make the standard comments available
        // ---------------------------------------------------------------------

        standardComments = standardCommentsManager.getStandardComments();

        // ---------------------------------------------------------------------
        // Make the DataElement types available
        // ---------------------------------------------------------------------

        dataElementTypeMap = new HashMap<String, String>();
        dataElementTypeMap.put( DataElement.TYPE_BOOL, i18n.getString( "yes_no" ) );
        dataElementTypeMap.put( DataElement.TYPE_INT, i18n.getString( "number" ) );
        dataElementTypeMap.put( DataElement.TYPE_STRING, i18n.getString( "text" ) );

        // ---------------------------------------------------------------------
        // Get the custom data entry form (if any)
        // ---------------------------------------------------------------------

        // Locate custom data entry form belonging to dataset, if any.
        dataEntryForm = dataEntryFormService.getDataEntryFormByDataSet( dataSet );
        cdeFormExists = (dataEntryForm != null);

        // Add JS and meta data to dynamically persist values of form.
        if ( cdeFormExists )
            customDataEntryFormCode = prepareDataEntryFormCode( dataEntryForm.getHtmlCode(), dataElements, dataValues );

        
        if(selDSName.equalsIgnoreCase( llbirth ) )
        {
            prepareLLBirthFormCode(organisationUnit, period, dataElements, dataValues);
        }
        else if(selDSName.equalsIgnoreCase( lldeath ))
        {
        	prepareLLDeathFormCode(organisationUnit, period, dataElements, dataValues);
        }
        else if(selDSName.equalsIgnoreCase( llmdeath ))
        {
        	prepareLLMaternalDeathFormCode(organisationUnit, period, dataElements, dataValues);
        }
        else if(selDSName.equalsIgnoreCase( lluuidspe ))
        {
        	prepareLLUUIDSPEFormCode(organisationUnit, period, dataElements, dataValues);
        }
        else if(selDSName.equalsIgnoreCase( lluuidspep ))
        {
        	prepareLLUUIDSPEPFormCode(organisationUnit, period, dataElements, dataValues);
        }
        else if(selDSName.equalsIgnoreCase( lldidsp ))
        {
        	prepareLLDIDSPFormCode(organisationUnit, period, dataElements, dataValues);
        }
        else if(selDSName.equalsIgnoreCase( llidspl ))
        {
        	prepareLLIDSPLFormCode(organisationUnit, period, dataElements, dataValues);
        }
        else
        {
            
        }
        
        maxRecordNo = dataValueService.getMaxRecordNo();
        
        return SUCCESS;
    }
    
    
    private void prepareLLIDSPLFormCode(OrganisationUnit organisationUnit, Period period, Collection<DataElement> dataElements,
            Collection<LLDataValue> dataValues)
        {
            lldataValueMap = new HashMap<String, List<LLDataValue>>();
            
            for ( LLDataValue dataValue : dataValues )
            {
                Integer recordNo = dataValue.getRecordNo();
                System.out.println("RecordNo : "+recordNo);
                List<LLDataValue> tempLLDVList;
                if(lldataValueMap == null || lldataValueMap.isEmpty() || lldataValueMap.get(String.valueOf(recordNo)) == null || lldataValueMap.get(String.valueOf(recordNo)).isEmpty())
                {
                	tempLLDVList = new ArrayList<LLDataValue>();
                }
                else
                {
                	tempLLDVList = new ArrayList<LLDataValue>(lldataValueMap.get(String.valueOf(recordNo)));
                }
                
                tempLLDVList.add( dataValue );
                lldataValueMap.put( String.valueOf(recordNo), tempLLDVList );
            }

            Set<String> llDataValueMapKeys = lldataValueMap.keySet();
            Iterator<String> it1 = llDataValueMapKeys.iterator();
            while(it1.hasNext())
            {
                String tempRecordNo = it1.next();
                List<LLDataValue> tempLLDVList1 = new ArrayList<LLDataValue>(lldataValueMap.get(tempRecordNo));
                List<LLDataValue> tempLLDVList2 = new ArrayList<LLDataValue>();
                LLDataValue existingLLDV = new LLDataValue();
                
                for(int i = 0; i<6; i++)
                {
                	LLDataValue tempLLDV1 = new LLDataValue();
                	tempLLDVList2.add(tempLLDV1);
                }

                Iterator<LLDataValue> it2 = tempLLDVList1.iterator();
                while(it2.hasNext())
                {
                    LLDataValue tempLLDV = it2.next();
                    
                    if(tempLLDV.getDataElement().getId() == LLDataSets.LLIDSPL_PATIENT_NAME)
                    {
                        tempLLDVList2.set( 0, tempLLDV );
                        existingLLDV = tempLLDV;
                    }    
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLIDSPL_AGE)
                    {
                        tempLLDVList2.set( 1, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLIDSPL_SEX)
                    {
                        tempLLDVList2.set( 2, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLIDSPL_ADDRESS)
                    {
                        tempLLDVList2.set( 3, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLIDSPL_TEST)
                    {
                        tempLLDVList2.set( 4, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLIDSPL_LAB_DIAGNOSIS)
                    {
                        tempLLDVList2.set( 5, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    
                }

                int llbDes[] = {
                				LLDataSets.LLIDSPL_PATIENT_NAME, LLDataSets.LLIDSPL_AGE, LLDataSets.LLIDSPL_SEX,
                				LLDataSets.LLIDSPL_ADDRESS, LLDataSets.LLIDSPL_TEST, LLDataSets.LLIDSPL_LAB_DIAGNOSIS
                				};
                
                for(int i = 0; i<6; i++)
                {
                	LLDataValue llDv = tempLLDVList2.get(i);
                	if(tempLLDVList2.get(i).getDataElement() == null || tempLLDVList2.get(i).getDataElement().getId() != llbDes[i])
                	{
                		llDv.setPeriod(existingLLDV.getPeriod());
                		llDv.setSource(existingLLDV.getSource());
                		llDv.setDataElement(dataElementService.getDataElement(llbDes[i]));
                		llDv.setRecordNo(existingLLDV.getRecordNo());
                		llDv.setOptionCombo(existingLLDV.getOptionCombo());
                		llDv.setValue(" ");
                		
                		tempLLDVList2.set(i, llDv);
                	}	            		
                }

                lldataValueMap.put( tempRecordNo, tempLLDVList2 );            
            }
         
            recordNos = new ArrayList<String>(lldataValueMap.keySet());
            Collections.sort( recordNos );

        }

    
    private void prepareLLMaternalDeathFormCode(OrganisationUnit organisationUnit, Period period, Collection<DataElement> dataElements,
        Collection<LLDataValue> dataValues)
    {
        lldataValueMap = new HashMap<String, List<LLDataValue>>();
        
        for ( LLDataValue dataValue : dataValues )
        {
            Integer recordNo = dataValue.getRecordNo();
            System.out.println("RecordNo : "+recordNo);
            List<LLDataValue> tempLLDVList;
            if(lldataValueMap == null || lldataValueMap.isEmpty() || lldataValueMap.get(String.valueOf(recordNo)) == null || lldataValueMap.get(String.valueOf(recordNo)).isEmpty())
            {
            	tempLLDVList = new ArrayList<LLDataValue>();
            }
            else
            {
            	tempLLDVList = new ArrayList<LLDataValue>(lldataValueMap.get(String.valueOf(recordNo)));
            }
            
            tempLLDVList.add( dataValue );
            lldataValueMap.put( String.valueOf(recordNo), tempLLDVList );
        }

        Set<String> llDataValueMapKeys = lldataValueMap.keySet();
        Iterator<String> it1 = llDataValueMapKeys.iterator();
        while(it1.hasNext())
        {
            String tempRecordNo = it1.next();
            List<LLDataValue> tempLLDVList1 = new ArrayList<LLDataValue>(lldataValueMap.get(tempRecordNo));
            List<LLDataValue> tempLLDVList2 = new ArrayList<LLDataValue>();
            LLDataValue existingLLDV = new LLDataValue();
            
            for(int i = 0; i<=7; i++)
            {
            	LLDataValue tempLLDV1 = new LLDataValue();
            	tempLLDVList2.add(tempLLDV1);
            }

            Iterator<LLDataValue> it2 = tempLLDVList1.iterator();
            while(it2.hasNext())
            {
                LLDataValue tempLLDV = it2.next();
                
                if(tempLLDV.getDataElement().getId() == LLDataSets.LLMD_MOTHER_NAME)
                {
                    tempLLDVList2.set( 0, tempLLDV );
                    existingLLDV = tempLLDV;
                }    
                else if(tempLLDV.getDataElement().getId() == LLDataSets.LLMD_VILLAGE_NAME)
                {
                    tempLLDVList2.set( 1, tempLLDV );
                    existingLLDV = tempLLDV;
                }
                else if(tempLLDV.getDataElement().getId() == LLDataSets.LLMD_AGE_AT_DEATH)
                {
                    tempLLDVList2.set( 2, tempLLDV );
                    existingLLDV = tempLLDV;
                }
                else if(tempLLDV.getDataElement().getId() == LLDataSets.LLMD_DURATION_OF_PREGNANCY)
                {
                    tempLLDVList2.set( 3, tempLLDV );
                    existingLLDV = tempLLDV;
                }
                else if(tempLLDV.getDataElement().getId() == LLDataSets.LLMD_DELIVERY_AT)
                {
                    tempLLDVList2.set( 4, tempLLDV );
                    existingLLDV = tempLLDV;
                }
                else if(tempLLDV.getDataElement().getId() == LLDataSets.LLMD_NATURE_OF_ASSISTANCE)
                {
                    tempLLDVList2.set( 5, tempLLDV );
                    existingLLDV = tempLLDV;
                }
                else if(tempLLDV.getDataElement().getId() == LLDataSets.LLMD_DEATH_CAUSE)
                {
                    tempLLDVList2.set( 6, tempLLDV );
                    existingLLDV = tempLLDV;
                }
                else if(tempLLDV.getDataElement().getId() == LLDataSets.LLMD_AUDITED)
                {
                    tempLLDVList2.set( 7, tempLLDV );
                    existingLLDV = tempLLDV;
                }
                
            }

            int llbDes[] = {
            				LLDataSets.LLMD_MOTHER_NAME, LLDataSets.LLMD_VILLAGE_NAME, LLDataSets.LLMD_AGE_AT_DEATH,
            				LLDataSets.LLMD_DURATION_OF_PREGNANCY, LLDataSets.LLMD_DELIVERY_AT, LLDataSets.LLMD_NATURE_OF_ASSISTANCE,
            				LLDataSets.LLMD_DEATH_CAUSE, LLDataSets.LLMD_AUDITED 
            				};
            
            for(int i = 0; i<=7; i++)
            {
            	LLDataValue llDv = tempLLDVList2.get(i);
            	if(tempLLDVList2.get(i).getDataElement() == null || tempLLDVList2.get(i).getDataElement().getId() != llbDes[i])
            	{
            		llDv.setPeriod(existingLLDV.getPeriod());
            		llDv.setSource(existingLLDV.getSource());
            		llDv.setDataElement(dataElementService.getDataElement(llbDes[i]));
            		llDv.setRecordNo(existingLLDV.getRecordNo());
            		llDv.setOptionCombo(existingLLDV.getOptionCombo());
            		llDv.setValue(" ");
            		
            		tempLLDVList2.set(i, llDv);
            	}	            		
            }

            lldataValueMap.put( tempRecordNo, tempLLDVList2 );            
        }
        
        recordNos = new ArrayList<String>(lldataValueMap.keySet());
        Collections.sort( recordNos );

    }

    
    private void prepareLLBirthFormCode(OrganisationUnit organisationUnit, Period period, Collection<DataElement> dataElements,
            Collection<LLDataValue> dataValues)
        {
            lldataValueMap = new HashMap<String, List<LLDataValue>>();
            
            for ( LLDataValue dataValue : dataValues )
            {
                Integer recordNo = dataValue.getRecordNo();
                System.out.println("RecordNo : "+recordNo);
                List<LLDataValue> tempLLDVList;
                if(lldataValueMap == null || lldataValueMap.isEmpty() || lldataValueMap.get(String.valueOf(recordNo)) == null || lldataValueMap.get(String.valueOf(recordNo)).isEmpty())
                {
                	tempLLDVList = new ArrayList<LLDataValue>();
                }
                else
                {
                	tempLLDVList = new ArrayList<LLDataValue>(lldataValueMap.get(String.valueOf(recordNo)));
                }
                
                tempLLDVList.add( dataValue );
                lldataValueMap.put( String.valueOf(recordNo), tempLLDVList );
            }

            Set<String> llDataValueMapKeys = lldataValueMap.keySet();
            //List<String> llDataValueMapKeys = new ArrayList<String>(lldataValueMap.keySet());
            
            //Collections.sort(llDataValueMapKeys);
            Iterator<String> it1 = llDataValueMapKeys.iterator();
            while(it1.hasNext())
            {
                String tempRecordNo = it1.next();
                List<LLDataValue> tempLLDVList1 = new ArrayList<LLDataValue>(lldataValueMap.get(tempRecordNo));
                List<LLDataValue> tempLLDVList2 = new ArrayList<LLDataValue>();
                LLDataValue existingLLDV = new LLDataValue();
                
                for(int i = 0; i<=5; i++)
                {
                	LLDataValue tempLLDV1 = new LLDataValue();
                	tempLLDVList2.add(tempLLDV1);
                }

                Iterator<LLDataValue> it2 = tempLLDVList1.iterator();
                while(it2.hasNext())
                {
                    LLDataValue tempLLDV = it2.next();
                    
                    if(tempLLDV.getDataElement().getId() == LLDataSets.LLB_CHILD_NAME)
                    {
                        tempLLDVList2.set( 0, tempLLDV );
                        existingLLDV = tempLLDV;
                    }    
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLB_VILLAGE_NAME)
                    {
                        tempLLDVList2.set( 1, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLB_SEX)
                    {
                        tempLLDVList2.set( 2, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLB_DOB)
                    {
                        tempLLDVList2.set( 3, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLB_WIEGH)
                    {
                        tempLLDVList2.set( 4, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLB_BREASTFED)
                    {
                        tempLLDVList2.set( 5, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                }

                int llbDes[] = {LLDataSets.LLB_CHILD_NAME,LLDataSets.LLB_VILLAGE_NAME,LLDataSets.LLB_SEX,
                				LLDataSets.LLB_DOB,LLDataSets.LLB_WIEGH,LLDataSets.LLB_BREASTFED};
                for(int i = 0; i<=5; i++)
                {
                	LLDataValue llDv = tempLLDVList2.get(i);
                	if(tempLLDVList2.get(i).getDataElement() == null || tempLLDVList2.get(i).getDataElement().getId() != llbDes[i])
                	{
                		llDv.setPeriod(existingLLDV.getPeriod());
                		llDv.setSource(existingLLDV.getSource());
                		llDv.setDataElement(dataElementService.getDataElement(llbDes[i]));
                		llDv.setRecordNo(existingLLDV.getRecordNo());
                		llDv.setOptionCombo(existingLLDV.getOptionCombo());
                		llDv.setValue(" ");
                		
                		tempLLDVList2.set(i, llDv);
                	}	            		
                }

                lldataValueMap.put( tempRecordNo, tempLLDVList2 );            
            }
         
            recordNos = new ArrayList<String>(lldataValueMap.keySet());
            Collections.sort( recordNos );
        }

    
    
    private void prepareLLDeathFormCode(OrganisationUnit organisationUnit, Period period, Collection<DataElement> dataElements,
            Collection<LLDataValue> dataValues)
        {
            lldataValueMap = new HashMap<String, List<LLDataValue>>();
            
            for ( LLDataValue dataValue : dataValues )
            {
                Integer recordNo = dataValue.getRecordNo();
                System.out.println("RecordNo : "+recordNo);
                List<LLDataValue> tempLLDVList;
                if(lldataValueMap == null || lldataValueMap.isEmpty() || lldataValueMap.get(String.valueOf(recordNo)) == null || lldataValueMap.get(String.valueOf(recordNo)).isEmpty())
                {
                	tempLLDVList = new ArrayList<LLDataValue>();
                }
                else
                {
                	tempLLDVList = new ArrayList<LLDataValue>(lldataValueMap.get(String.valueOf(recordNo)));
                }
                
                tempLLDVList.add( dataValue );
                lldataValueMap.put( String.valueOf(recordNo), tempLLDVList );
            }

            Set<String> llDataValueMapKeys = lldataValueMap.keySet();
            Iterator<String> it1 = llDataValueMapKeys.iterator();
            while(it1.hasNext())
            {
                String tempRecordNo = it1.next();
                List<LLDataValue> tempLLDVList1 = new ArrayList<LLDataValue>(lldataValueMap.get(tempRecordNo));
                List<LLDataValue> tempLLDVList2 = new ArrayList<LLDataValue>();
                LLDataValue existingLLDV = new LLDataValue();
                
                for(int i = 0; i<5; i++)
                {
                	LLDataValue tempLLDV1 = new LLDataValue();
                	tempLLDVList2.add(tempLLDV1);
                }

                Iterator<LLDataValue> it2 = tempLLDVList1.iterator();
                while(it2.hasNext())
                {
                    LLDataValue tempLLDV = it2.next();
                    
                    if(tempLLDV.getDataElement().getId() == LLDataSets.LLD_CHILD_NAME)
                    {
                        tempLLDVList2.set( 0, tempLLDV );
                        existingLLDV = tempLLDV;
                    }    
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLD_VILLAGE_NAME)
                    {
                        tempLLDVList2.set( 1, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLD_SEX)
                    {
                        tempLLDVList2.set( 2, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLD_AGE_CATEGORY)
                    {
                        tempLLDVList2.set( 3, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLD_DEATH_CAUSE)
                    {
                        tempLLDVList2.set( 4, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                }

                int llbDes[] = {LLDataSets.LLD_CHILD_NAME,LLDataSets.LLD_VILLAGE_NAME,LLDataSets.LLD_SEX,
                				LLDataSets.LLD_AGE_CATEGORY,LLDataSets.LLD_DEATH_CAUSE};
                
                for(int i = 0; i<5; i++)
                {
                	LLDataValue llDv = tempLLDVList2.get(i);
                	if(tempLLDVList2.get(i).getDataElement() == null || tempLLDVList2.get(i).getDataElement().getId() != llbDes[i])
                	{
                		llDv.setPeriod(existingLLDV.getPeriod());
                		llDv.setSource(existingLLDV.getSource());
                		llDv.setDataElement(dataElementService.getDataElement(llbDes[i]));
                		llDv.setRecordNo(existingLLDV.getRecordNo());
                		llDv.setOptionCombo(existingLLDV.getOptionCombo());
                		llDv.setValue(" ");
                		
                		tempLLDVList2.set(i, llDv);
                	}	            		
                }

                lldataValueMap.put( tempRecordNo, tempLLDVList2 );            
            }
            
            recordNos = new ArrayList<String>(lldataValueMap.keySet());
            Collections.sort( recordNos );

        }

    private void prepareLLUUIDSPEFormCode(OrganisationUnit organisationUnit, Period period, Collection<DataElement> dataElements,
            Collection<LLDataValue> dataValues)
        {
            lldataValueMap = new HashMap<String, List<LLDataValue>>();
            
            for ( LLDataValue dataValue : dataValues )
            {
                Integer recordNo = dataValue.getRecordNo();
                System.out.println("RecordNo : "+recordNo);
                List<LLDataValue> tempLLDVList;
                if(lldataValueMap == null || lldataValueMap.isEmpty() || lldataValueMap.get(String.valueOf(recordNo)) == null || lldataValueMap.get(String.valueOf(recordNo)).isEmpty())
                {
                	tempLLDVList = new ArrayList<LLDataValue>();
                }
                else
                {
                	tempLLDVList = new ArrayList<LLDataValue>(lldataValueMap.get(String.valueOf(recordNo)));
                }
                
                tempLLDVList.add( dataValue );
                lldataValueMap.put( String.valueOf(recordNo), tempLLDVList );
            }

            Set<String> llDataValueMapKeys = lldataValueMap.keySet();
            Iterator<String> it1 = llDataValueMapKeys.iterator();
            while(it1.hasNext())
            {
                String tempRecordNo = it1.next();
                List<LLDataValue> tempLLDVList1 = new ArrayList<LLDataValue>(lldataValueMap.get(tempRecordNo));
                List<LLDataValue> tempLLDVList2 = new ArrayList<LLDataValue>();
                LLDataValue existingLLDV = new LLDataValue();
                
                for(int i = 0; i<4; i++)
                {
                	LLDataValue tempLLDV1 = new LLDataValue();
                	tempLLDVList2.add(tempLLDV1);
                }

                Iterator<LLDataValue> it2 = tempLLDVList1.iterator();
                while(it2.hasNext())
                {
                    LLDataValue tempLLDV = it2.next();
                    
                    if(tempLLDV.getDataElement().getId() == LLDataSets.LLUUIDSPE_SC_NAME)
                    {
                        tempLLDVList2.set( 0, tempLLDV );
                        existingLLDV = tempLLDV;
                    }    
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLUUIDSPE_DATE_OF_EVENT)
                    {
                        tempLLDVList2.set( 1, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLUUIDSPE_DEATAILS)
                    {
                        tempLLDVList2.set( 2, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLUUIDSPE_WAS_INVESTIGATED)
                    {
                        tempLLDVList2.set( 3, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                }

                int llbDes[] = {LLDataSets.LLUUIDSPE_SC_NAME,LLDataSets.LLUUIDSPE_DATE_OF_EVENT,
                				LLDataSets.LLUUIDSPE_DEATAILS,LLDataSets.LLUUIDSPE_WAS_INVESTIGATED};
                
                for(int i = 0; i<4; i++)
                {
                	LLDataValue llDv = tempLLDVList2.get(i);
                	if(tempLLDVList2.get(i).getDataElement() == null || tempLLDVList2.get(i).getDataElement().getId() != llbDes[i])
                	{
                		llDv.setPeriod(existingLLDV.getPeriod());
                		llDv.setSource(existingLLDV.getSource());
                		llDv.setDataElement(dataElementService.getDataElement(llbDes[i]));
                		llDv.setRecordNo(existingLLDV.getRecordNo());
                		llDv.setOptionCombo(existingLLDV.getOptionCombo());
                		llDv.setValue(" ");
                		
                		tempLLDVList2.set(i, llDv);
                	}	            		
                }

                lldataValueMap.put( tempRecordNo, tempLLDVList2 );            
            }
            
            recordNos = new ArrayList<String>(lldataValueMap.keySet());
            Collections.sort( recordNos );

        }

    private void prepareLLUUIDSPEPFormCode(OrganisationUnit organisationUnit, Period period, Collection<DataElement> dataElements,
            Collection<LLDataValue> dataValues)
        {
            lldataValueMap = new HashMap<String, List<LLDataValue>>();
            
            for ( LLDataValue dataValue : dataValues )
            {
                Integer recordNo = dataValue.getRecordNo();
                System.out.println("RecordNo : "+recordNo);
                List<LLDataValue> tempLLDVList;
                if(lldataValueMap == null || lldataValueMap.isEmpty() || lldataValueMap.get(String.valueOf(recordNo)) == null || lldataValueMap.get(String.valueOf(recordNo)).isEmpty())
                {
                	tempLLDVList = new ArrayList<LLDataValue>();
                }
                else
                {
                	tempLLDVList = new ArrayList<LLDataValue>(lldataValueMap.get(String.valueOf(recordNo)));
                }
                
                tempLLDVList.add( dataValue );
                lldataValueMap.put( String.valueOf(recordNo), tempLLDVList );
            }

            Set<String> llDataValueMapKeys = lldataValueMap.keySet();
            Iterator<String> it1 = llDataValueMapKeys.iterator();
            while(it1.hasNext())
            {
                String tempRecordNo = it1.next();
                List<LLDataValue> tempLLDVList1 = new ArrayList<LLDataValue>(lldataValueMap.get(tempRecordNo));
                List<LLDataValue> tempLLDVList2 = new ArrayList<LLDataValue>();
                LLDataValue existingLLDV = new LLDataValue();
                
                for(int i = 0; i<4; i++)
                {
                	LLDataValue tempLLDV1 = new LLDataValue();
                	tempLLDVList2.add(tempLLDV1);
                }

                Iterator<LLDataValue> it2 = tempLLDVList1.iterator();
                while(it2.hasNext())
                {
                    LLDataValue tempLLDV = it2.next();
                    
                    if(tempLLDV.getDataElement().getId() == LLDataSets.LLUUIDSPEP_EVENT_REPORTED)
                    {
                        tempLLDVList2.set( 0, tempLLDV );
                        existingLLDV = tempLLDV;
                    }    
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLUUIDSPEP_DATE_OF_EVENT)
                    {
                        tempLLDVList2.set( 1, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLUUIDSPEP_WAS_INVESTIGATED)
                    {
                        tempLLDVList2.set( 2, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLUUIDSPEP_ACTION_TAKEN)
                    {
                        tempLLDVList2.set( 3, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                }

                int llbDes[] = {LLDataSets.LLUUIDSPEP_EVENT_REPORTED,LLDataSets.LLUUIDSPEP_DATE_OF_EVENT,
                				LLDataSets.LLUUIDSPEP_WAS_INVESTIGATED,LLDataSets.LLUUIDSPEP_ACTION_TAKEN};
                
                for(int i = 0; i<4; i++)
                {
                	LLDataValue llDv = tempLLDVList2.get(i);
                	if(tempLLDVList2.get(i).getDataElement() == null || tempLLDVList2.get(i).getDataElement().getId() != llbDes[i])
                	{
                		llDv.setPeriod(existingLLDV.getPeriod());
                		llDv.setSource(existingLLDV.getSource());
                		llDv.setDataElement(dataElementService.getDataElement(llbDes[i]));
                		llDv.setRecordNo(existingLLDV.getRecordNo());
                		llDv.setOptionCombo(existingLLDV.getOptionCombo());
                		llDv.setValue(" ");
                		
                		tempLLDVList2.set(i, llDv);
                	}	            		
                }

                lldataValueMap.put( tempRecordNo, tempLLDVList2 );            
            }
            
            recordNos = new ArrayList<String>(lldataValueMap.keySet());
            Collections.sort( recordNos );

        }
    
    
    private void prepareLLDIDSPFormCode(OrganisationUnit organisationUnit, Period period, Collection<DataElement> dataElements,
            Collection<LLDataValue> dataValues)
        {
            lldataValueMap = new HashMap<String, List<LLDataValue>>();
            
            for ( LLDataValue dataValue : dataValues )
            {
                Integer recordNo = dataValue.getRecordNo();
                System.out.println("RecordNo : "+recordNo);
                List<LLDataValue> tempLLDVList;
                if(lldataValueMap == null || lldataValueMap.isEmpty() || lldataValueMap.get(String.valueOf(recordNo)) == null || lldataValueMap.get(String.valueOf(recordNo)).isEmpty())
                {
                	tempLLDVList = new ArrayList<LLDataValue>();
                }
                else
                {
                	tempLLDVList = new ArrayList<LLDataValue>(lldataValueMap.get(String.valueOf(recordNo)));
                }
                
                tempLLDVList.add( dataValue );
                lldataValueMap.put( String.valueOf(recordNo), tempLLDVList );
            }

            Set<String> llDataValueMapKeys = lldataValueMap.keySet();
            Iterator<String> it1 = llDataValueMapKeys.iterator();
            while(it1.hasNext())
            {
                String tempRecordNo = it1.next();
                List<LLDataValue> tempLLDVList1 = new ArrayList<LLDataValue>(lldataValueMap.get(tempRecordNo));
                List<LLDataValue> tempLLDVList2 = new ArrayList<LLDataValue>();
                LLDataValue existingLLDV = new LLDataValue();
                
                for(int i = 0; i<5; i++)
                {
                	LLDataValue tempLLDV1 = new LLDataValue();
                	tempLLDVList2.add(tempLLDV1);
                }

                Iterator<LLDataValue> it2 = tempLLDVList1.iterator();
                while(it2.hasNext())
                {
                    LLDataValue tempLLDV = it2.next();
                    
                    if(tempLLDV.getDataElement().getId() == LLDataSets.LLDIDSP_CHILD_NAME)
                    {
                        tempLLDVList2.set( 0, tempLLDV );
                        existingLLDV = tempLLDV;
                    }    
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLDIDSP_VILLAGE_NAME)
                    {
                        tempLLDVList2.set( 1, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLDIDSP_SEX)
                    {
                        tempLLDVList2.set( 2, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLDIDSP_AGE_CATEGORY)
                    {
                        tempLLDVList2.set( 3, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                    else if(tempLLDV.getDataElement().getId() == LLDataSets.LLDIDSP_DEATH_CAUSE)
                    {
                        tempLLDVList2.set( 4, tempLLDV );
                        existingLLDV = tempLLDV;
                    }
                }

                int llbDes[] = {LLDataSets.LLDIDSP_CHILD_NAME,LLDataSets.LLDIDSP_VILLAGE_NAME,LLDataSets.LLDIDSP_SEX,
                				LLDataSets.LLDIDSP_AGE_CATEGORY,LLDataSets.LLDIDSP_DEATH_CAUSE};
                
                for(int i = 0; i<5; i++)
                {
                	LLDataValue llDv = tempLLDVList2.get(i);
                	if(tempLLDVList2.get(i).getDataElement() == null || tempLLDVList2.get(i).getDataElement().getId() != llbDes[i])
                	{
                		llDv.setPeriod(existingLLDV.getPeriod());
                		llDv.setSource(existingLLDV.getSource());
                		llDv.setDataElement(dataElementService.getDataElement(llbDes[i]));
                		llDv.setRecordNo(existingLLDV.getRecordNo());
                		llDv.setOptionCombo(existingLLDV.getOptionCombo());
                		llDv.setValue(" ");
                		
                		tempLLDVList2.set(i, llDv);
                	}	            		
                }

                lldataValueMap.put( tempRecordNo, tempLLDVList2 );            
            }
            
            recordNos = new ArrayList<String>(lldataValueMap.keySet());
            Collections.sort( recordNos );

        }

    
    
    

    /**
     * Prepares the daa entry form code by preparing the data element
     * placeholders with code for displaying, manipulating and persisting data
     * elements.
     * 
     * The function in turn calls separate functions for preparing boolean and
     * non-boolean data elements.
     * 
     */
    private String prepareDataEntryFormCode( String dataEntryFormCode, Collection<DataElement> dataElements,
        Collection<LLDataValue> dataValues )
    {

        String preparedCode = dataEntryFormCode;
//        preparedCode = prepareDataEntryFormInputs( preparedCode, dataElements, dataValues );
//        preparedCode = prepareDataEntryFormCombos( preparedCode, dataElements, dataValues );
        //preparedCode = prepareDataEntryFormInputsAndCombos( preparedCode, dataElements, dataValues );
        
        return preparedCode;

    }
}
