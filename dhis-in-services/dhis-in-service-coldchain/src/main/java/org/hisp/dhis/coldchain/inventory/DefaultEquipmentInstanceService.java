package org.hisp.dhis.coldchain.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultEquipmentInstanceService implements EquipmentInstanceService
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private EquipmentInstanceStore equipmentInstanceStore;

    public void setEquipmentInstanceStore( EquipmentInstanceStore equipmentInstanceStore )
    {
        this.equipmentInstanceStore = equipmentInstanceStore;
    }

    private EquipmentDetailsService equipmentDetailsService;
    
    public void setEquipmentDetailsService( EquipmentDetailsService equipmentDetailsService )
    {
        this.equipmentDetailsService = equipmentDetailsService;
    }
    
    private EquipmentStatusService equipmentStatusService;
    
    public void setEquipmentStatusService( EquipmentStatusService equipmentStatusService )
    {
        this.equipmentStatusService = equipmentStatusService;
    }
    
    // -------------------------------------------------------------------------
    // EquipmentInstance
    // -------------------------------------------------------------------------
    
    @Override
    public int addEquipmentInstance( EquipmentInstance equipmentInstance )
    {
        //return equipmentInstanceStore.addEquipmentInstance( equipmentInstance );
        return equipmentInstanceStore.save( equipmentInstance );
    }
    @Override
    public void deleteEquipmentInstance( EquipmentInstance equipmentInstance )
    {
        //equipmentInstanceStore.deleteEquipmentInstance( equipmentInstance );
        equipmentInstanceStore.delete( equipmentInstance );
    }
    
    public void deleteCompleteEquipmentInstance( EquipmentInstance equipmentInstance )
    {
        List<EquipmentDetails> equipmentDetailsList = new ArrayList<EquipmentDetails>( equipmentDetailsService.getEquipmentDetails( equipmentInstance ) );
        for( EquipmentDetails equipmentDetails : equipmentDetailsList )
        {
            equipmentDetailsService.deleteEquipmentDetails( equipmentDetails );
        }
        
        List<EquipmentStatus> equipmentStatusHistory = new ArrayList<EquipmentStatus>( equipmentStatusService.getEquipmentStatusHistory( equipmentInstance ) );
        for( EquipmentStatus equipmentStatus : equipmentStatusHistory )
        {
            equipmentStatusService.deleteEquipmentStatus( equipmentStatus );
        }
        
        deleteEquipmentInstance( equipmentInstance );
    }
    
    @Override
    public Collection<EquipmentInstance> getAllEquipmentInstance()
    {
        //return equipmentInstanceStore.getAllEquipmentInstance();
        return equipmentInstanceStore.getAll();
    }
    @Override
    public void updateEquipmentInstance( EquipmentInstance equipmentInstance )
    {
        //equipmentInstanceStore.updateEquipmentInstance( equipmentInstance );
        equipmentInstanceStore.update( equipmentInstance );
    }
    
    public int createEquipment( EquipmentInstance equipmentInstance, List<EquipmentDetails> equipmentDetails )
    {
        int equipmentInstanceId = addEquipmentInstance( equipmentInstance );
        
        for( EquipmentDetails equipment : equipmentDetails )
        {
            equipmentDetailsService.addEquipmentDetails( equipment );
        }
        
        return equipmentInstanceId;
    }

    public EquipmentInstance getEquipmentInstance( int id )
    {
        return equipmentInstanceStore.get( id );
    }
    
    public Collection<EquipmentInstance> getEquipmentInstances( OrganisationUnit orgUnit )
    {
        return equipmentInstanceStore.getEquipmentInstances( orgUnit );
    }

    public Collection<EquipmentInstance> getEquipmentInstances( OrganisationUnit orgUnit, InventoryType inventoryType )
    {
        return equipmentInstanceStore.getEquipmentInstances( orgUnit, inventoryType );
    }

    public Collection<EquipmentInstance> getEquipmentInstances( OrganisationUnit orgUnit, InventoryType inventoryType, int min, int max )
    {
        return equipmentInstanceStore.getEquipmentInstances( orgUnit, inventoryType, min, max );
    }

    @Override
    public int getCountEquipmentInstance( OrganisationUnit orgUnit, InventoryType inventoryType )
    {
        return equipmentInstanceStore.getCountEquipmentInstance( orgUnit, inventoryType );
    }

    public int getCountEquipmentInstance( OrganisationUnit orgUnit, InventoryType inventoryType, InventoryTypeAttribute inventoryTypeAttribute, String searchText )
    {
        return equipmentInstanceStore.getCountEquipmentInstance(  orgUnit,  inventoryType, inventoryTypeAttribute ,  searchText );
    }

    public Collection<EquipmentInstance> getEquipmentInstances( OrganisationUnit orgUnit, InventoryType inventoryType, InventoryTypeAttribute inventoryTypeAttribute, String searchText, int min, int max )
    {
        return equipmentInstanceStore.getEquipmentInstances( orgUnit, inventoryType, inventoryTypeAttribute, searchText, min, max );
    }
}
