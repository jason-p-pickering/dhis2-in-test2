package org.hisp.dhis.reports.api.impl;

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

import static org.hisp.dhis.system.util.DateUtils.getMediumDate;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.aggregation.AggregatedIndicatorValue;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.api.PivotTable;
import org.hisp.dhis.reports.api.PivotTableService;
/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DefaultPivotTableService
    implements PivotTableService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    /*private DataMartStore dataMartStore;
    
    public void setDataMartStore( DataMartStore dataMartStore )
    {
        this.dataMartStore = dataMartStore;
    }*/

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }    
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private AggregationService aggregationService;
    
    public void setAggregationService(AggregationService aggregationService) 
    {
		this.aggregationService = aggregationService;
	}

    private Indicator indicator;
    private Period period;
    private OrganisationUnit organisationUnit;
    // -------------------------------------------------------------------------
    // PivotTableService implementation
    // -------------------------------------------------------------------------

	public PivotTable getPivotTable( int indicatorGroupId, String periodTypeName, String startDate, String endDate, int level )
    {
        PeriodType periodType = PeriodType.getPeriodTypeByName( periodTypeName );
        
        List<Period> periods = new ArrayList<Period>( 
            periodService.getPeriodsBetweenDates( periodType, getMediumDate( startDate ), getMediumDate( endDate ) ) );
        
        List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>( 
            organisationUnitService.getOrganisationUnitsAtLevel( level ) );
         
        List<Indicator> indicators = null;
        List<AggregatedIndicatorValue> indicatorValues = new ArrayList<AggregatedIndicatorValue>();
        AggregatedIndicatorValue indicatorValue;

        if ( indicatorGroupId == -1 ) // -1 -> All
        {
            indicators = new ArrayList<Indicator>( indicatorService.getAllIndicators() );
        }
        else
        {
            indicators = new ArrayList<Indicator>( indicatorService.getIndicatorGroup( indicatorGroupId ).getMembers() );

            //new code added on 13th april for indian module
            double aggregatedIndicatorValue, aggregatedNumeratorValue, aggregatedDenominatorValue  = 0;
            for(int ou=0;ou<organisationUnits.size();ou++){
                for(int p=0;p<periods.size();p++){
                    for(int i=0;i<indicators.size();i++){
                        try{
                        indicator = indicators.get(i);
                        period = periods.get(p);
                        organisationUnit = organisationUnits.get(ou);
                        int factor = (int) (indicator.getIndicatorType().getFactor());
                        
                        aggregatedIndicatorValue = aggregationService.getAggregatedIndicatorValue(indicator, period.getStartDate(), period.getEndDate(),organisationUnit);
                        aggregatedNumeratorValue = aggregationService.getAggregatedNumeratorValue(indicator, period.getStartDate(), period.getEndDate(),organisationUnit);
                        aggregatedDenominatorValue = aggregationService.getAggregatedDenominatorValue(indicator, period.getStartDate(), period.getEndDate(),organisationUnit);
                        aggregatedIndicatorValue = Math.round(aggregatedIndicatorValue * Math.pow(10,2))/Math.pow(10,2);
                        if(aggregatedIndicatorValue<0){
                            aggregatedIndicatorValue=0;
                        }
                        indicatorValue = new AggregatedIndicatorValue( indicator.getId(),period.getId(), period.getPeriodType().getId() , organisationUnit.getId(),level,factor, aggregatedIndicatorValue, aggregatedNumeratorValue, aggregatedDenominatorValue);
                        indicatorValues.add(indicatorValue);

                        }catch(Exception e){
                            System.out.println(e.getMessage());
                        }
                    }
                }
            }           
        }
        
        PivotTable pivotTable = new PivotTable();
        
        pivotTable.setIndicators( indicators );
        pivotTable.setPeriods( periods );
        pivotTable.setOrganisationUnits( organisationUnits );
        pivotTable.setIndicatorValues( indicatorValues );
        
        return pivotTable;
    }
}
