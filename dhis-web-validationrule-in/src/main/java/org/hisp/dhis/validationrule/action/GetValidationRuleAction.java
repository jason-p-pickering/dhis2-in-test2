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

import com.opensymphony.xwork2.ActionContext;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleService;

import com.opensymphony.xwork2.ActionSupport;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;


/**
 * @author Margrethe Store
 * @version $Id: GetValidationRuleAction.java 5730 2008-09-20 14:32:22Z brajesh $
 */
public class GetValidationRuleAction
    extends ActionSupport
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------    
    
    private ValidationRuleService validationRuleService;

    public void setValidationRuleService( ValidationRuleService validationRuleService )
    {
        this.validationRuleService = validationRuleService;
    }

    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------
    
    private int id;

    public void setId( int id )
    {
        this.id = id;
    }   
    
    private ValidationRule validationRule;

    public ValidationRule getValidationRule()
    {
        return validationRule;
    }
    
    private String leftSideTextualExpression;

    public String getLeftSideTextualExpression()
    {
        return leftSideTextualExpression;
    }

    private String rightSideTextualExpression;

    public String getRightSideTextualExpression()
    {
        return rightSideTextualExpression;
    }
    
    private String validationOperator;
    
    public String getValidationOperator()
    {
    	return validationOperator;
    }
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------    
    
    public String execute() throws Exception
    {

        ActionContext ctx = ActionContext.getContext();
        HttpServletRequest req = (HttpServletRequest) ctx.get( ServletActionContext.HTTP_REQUEST );

        if( req.getMethod().equalsIgnoreCase("get") ) return "logout";
        
        validationRule = validationRuleService.getValidationRule( id );
        
        leftSideTextualExpression = expressionService.getExpressionDescription( validationRule.getLeftSide().getExpression() );
        rightSideTextualExpression = expressionService.getExpressionDescription( validationRule.getRightSide().getExpression() );
        
        validationOperator = validationRule.getOperator();       
        
        return SUCCESS;
    }
}
