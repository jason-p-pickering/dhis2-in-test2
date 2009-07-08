package org.hisp.dhis.reports.chart.action;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.options.displayproperty.DisplayPropertyHandler;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.comparator.PeriodComparator;

import com.opensymphony.xwork.Action;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class GetChartOptionsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ChartService chartService;

    public void setChartService( ChartService chartService )
    {
        this.chartService = chartService;
    }

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
    
    private DisplayPropertyHandler displayPropertyHandler;

    public void setDisplayPropertyHandler( DisplayPropertyHandler displayPropertyHandler )
    {
        this.displayPropertyHandler = displayPropertyHandler;
    }
    
    private Comparator<Indicator> indicatorComparator;

    public void setIndicatorComparator( Comparator<Indicator> indicatorComparator )
    {
        this.indicatorComparator = indicatorComparator;
    }
    
    private Comparator<OrganisationUnit> organisationUnitComparator;

    public void setOrganisationUnitComparator( Comparator<OrganisationUnit> organisationUnitComparator )
    {
        this.organisationUnitComparator = organisationUnitComparator;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }
    
    private String dimension;

    public String getDimension()
    {
        return dimension;
    }

    public void setDimension( String dimension )
    {
        this.dimension = dimension;
    }    
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Chart chart;

    public Chart getChart()
    {
        return chart;
    }    

    private List<IndicatorGroup> indicatorGroups = new ArrayList<IndicatorGroup>();

    public List<IndicatorGroup> getIndicatorGroups()
    {
        return indicatorGroups;
    }
    
    private List<Indicator> availableIndicators;

    public List<Indicator> getAvailableIndicators()
    {
        return availableIndicators;
    }

    private List<Indicator> selectedIndicators;

    public List<Indicator> getSelectedIndicators()
    {
        return selectedIndicators;
    }
    
    private List<PeriodType> periodTypes = new ArrayList<PeriodType>();

    public List<PeriodType> getPeriodTypes()
    {
        return periodTypes;
    }

    private List<Period> availablePeriods;

    public List<Period> getAvailablePeriods()
    {
        return availablePeriods;
    }
    
    private List<Period> selectedPeriods;

    public List<Period> getSelectedPeriods()
    {
        return selectedPeriods;
    }
    
    private List<OrganisationUnitLevel> levels = new ArrayList<OrganisationUnitLevel>();

    public List<OrganisationUnitLevel> getLevels()
    {
        return levels;
    }
    
    private List<OrganisationUnit> availableOrganisationUnits;

    public List<OrganisationUnit> getAvailableOrganisationUnits()
    {
        return availableOrganisationUnits;
    }
    
    private List<OrganisationUnit> selectedOrganisationUnits;

    public List<OrganisationUnit> getSelectedOrganisationUnits()
    {
        return selectedOrganisationUnits;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        indicatorGroups = new ArrayList<IndicatorGroup>( indicatorService.getAllIndicatorGroups() );
        
        availableIndicators = new ArrayList<Indicator>( indicatorService.getAllIndicators() );
        
        periodTypes = new ArrayList<PeriodType>( periodService.getAllPeriodTypes() );
        
        availablePeriods = new ArrayList<Period>( periodService.getPeriodsByPeriodType( new MonthlyPeriodType() ) );
        
        levels = organisationUnitService.getOrganisationUnitLevels();
        
        availableOrganisationUnits = new ArrayList<OrganisationUnit>( organisationUnitService.getAllOrganisationUnits() );
        
        Collections.sort( availableIndicators, indicatorComparator );
        Collections.sort( availablePeriods, new PeriodComparator() );
        Collections.sort( availableOrganisationUnits, organisationUnitComparator );   
        
        displayPropertyHandler.handle( availableIndicators );
        displayPropertyHandler.handle( availableOrganisationUnits );
        
        if ( id != null )
        {
            chart = chartService.getChart( id );
            
            selectedIndicators = chart.getIndicators();
            availableIndicators.removeAll( selectedIndicators );
            
            selectedPeriods = chart.getPeriods();
            availablePeriods.removeAll( selectedPeriods );
            
            selectedOrganisationUnits = chart.getOrganisationUnits();
            availableOrganisationUnits.removeAll( selectedOrganisationUnits );
            
            Collections.sort( selectedIndicators, indicatorComparator );
            Collections.sort( selectedPeriods, new PeriodComparator() );
            Collections.sort( selectedOrganisationUnits, organisationUnitComparator );

            displayPropertyHandler.handle( selectedIndicators );
            displayPropertyHandler.handle( selectedOrganisationUnits );
        }
        
        return SUCCESS;
    }
}
