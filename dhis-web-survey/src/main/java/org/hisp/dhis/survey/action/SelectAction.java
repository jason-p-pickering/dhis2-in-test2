package org.hisp.dhis.survey.action;

/*
 * Copyright (c) 2004-2010, University of Oslo
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
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.survey.Survey;
import org.hisp.dhis.survey.SurveyService;
import org.hisp.dhis.survey.comparator.SurveyNameComparator;
import org.hisp.dhis.survey.state.SelectedStateManager;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: SelectAction.java 5930 2008-10-15 03:30:52Z tri $
 */
@SuppressWarnings("serial")
public class SelectAction
    extends ActionSupport
{    
   
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------  

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    private SurveyService surveyService;

    public void setSurveyService( SurveyService surveyService )
    {
        this.surveyService = surveyService;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    private List<Survey> surveys = new ArrayList<Survey>();

    public Collection<Survey> getSurveys()
    {
        return surveys;
    }

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------


    private Integer selectedSurveyId;
   
    public Integer getSelectedSurveyId()
    {
        return selectedSurveyId;
    }

    public void setSelectedSurveyId( Integer selectedSurveyId )
    {
        this.selectedSurveyId = selectedSurveyId;
    }

       
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        // ---------------------------------------------------------------------
        // Validate selected OrganisationUnit
        // ---------------------------------------------------------------------

        organisationUnit = selectedStateManager.getSelectedOrganisationUnit();

        if ( organisationUnit == null )
        {
            selectedSurveyId = null;
 
            selectedStateManager.clearSelectedSurvey();     

            return SUCCESS;
        }

        // ---------------------------------------------------------------------
        // Load and Sort DataSets
        // ---------------------------------------------------------------------

        surveys = selectedStateManager.loadSurveysForSelectedOrgUnit( organisationUnit );       
        
        Collections.sort( surveys, new SurveyNameComparator() );

        // ---------------------------------------------------------------------
        // Validate selected DataSet
        // ---------------------------------------------------------------------

        Survey selectedSurvey;

        if ( selectedSurveyId != null )
        {
            selectedSurvey = surveyService.getSurvey( selectedSurveyId );
        }
        else
        {
            selectedSurvey = selectedStateManager.getSelectedSurvey();
        }

        if ( selectedSurvey != null && surveys.contains( selectedSurvey ) )
        {
            selectedSurveyId = selectedSurvey.getId();
            selectedStateManager.setSelectedSurvey( selectedSurvey );
        }
        else
        {
            selectedSurveyId = null;
            
            selectedStateManager.clearSelectedSurvey();

            return SUCCESS;
        }
        
        
        
        return "defaultform";        
    }
}
