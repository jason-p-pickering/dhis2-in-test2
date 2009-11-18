package org.hisp.dhis.reports.dataset.action;

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

import static org.hisp.dhis.datamart.DataMartInternalProcess.PROCESS_TYPE;
import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.util.InternalProcessUtil.PROCESS_KEY_REPORT;
import static org.hisp.dhis.util.InternalProcessUtil.setCurrentRunningProcess;

import java.util.ArrayList;
import java.util.List;

import org.amplecode.cave.process.ProcessCoordinator;
import org.amplecode.cave.process.ProcessExecutor;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.datamart.DataMartInternalProcess;
import org.hisp.dhis.reports.dataset.state.SelectedStateManager;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * This class exports the relevant data for the dataset report to datamart.
 * 
 * @author Lars Helge Overland
 * @version $Id$
 */
public class GenerateDataSetReportDataSourceAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProcessCoordinator processCoordinator;

    public void setProcessCoordinator( ProcessCoordinator processCoordinator )
    {
        this.processCoordinator = processCoordinator;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        String owner = currentUserService.getCurrentUsername();

        ProcessExecutor executor = processCoordinator.newProcess( PROCESS_TYPE, owner );

        DataMartInternalProcess process = (DataMartInternalProcess) executor.getProcess();
        
        List<Integer> periods = new ArrayList<Integer>();
        periods.add( selectedStateManager.getSelectedPeriod().getId() );
        
        List<Integer> organisationUnits = new ArrayList<Integer>();
        organisationUnits.add( selectedStateManager.getSelectedOrganisationUnit().getId() );
        
        process.setDataElementIds( getIdentifiers( DataElement.class, selectedStateManager.getSelectedDataSet().getDataElements() ) );
        process.setIndicatorIds( new ArrayList<Integer>() );
        process.setPeriodIds( periods  );
        process.setOrganisationUnitIds( organisationUnits );

        processCoordinator.requestProcessExecution( executor );

        setCurrentRunningProcess( PROCESS_KEY_REPORT, executor.getId() );
        
        return SUCCESS;
    }
}
