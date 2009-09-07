package org.hisp.dhis.config;

public interface ConfigurationService
{

    String ID = ConfigurationService.class.getName();
    
    // -------------------------------------------------------------------------
    // Config
    // -------------------------------------------------------------------------

    int addConfiguration( Configuration_IN con );
    
    void updateConfiguration( Configuration_IN con );
    
    void deleteConfiguration( Configuration_IN con );
    
    Configuration_IN getConfiguration( int id );
    
    Configuration_IN getConfigurationByKey( String ckey );

}
