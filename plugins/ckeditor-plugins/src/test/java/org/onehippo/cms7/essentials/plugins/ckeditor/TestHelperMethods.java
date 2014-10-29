package org.onehippo.cms7.essentials.plugins.ckeditor;


import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TestHelperMethods {

    @Test
    public void testExtractFromplugins(){
        String expected = "timestamp,abbr,leaflet,widget,lineutils,plugin3";
        String json = "{extraPlugins: '" +expected+ "'}";
        String plugins = CKEditorResource.extractPluginsFrom(json);
        assertEquals(expected,plugins);
    }

    @Test
    public void testCombinePlugins(){
        String existingPluginsList = "timestamp,abbr,leaflet,widget,lineutils,clipboard,dialog,dialogui";
        String newPluginsList = "timestamp,abbr,leaflet,widget,lineutils,plugin3";
        String expectedPluginList  = "timestamp,lineutils,plugin3,abbr,dialog,clipboard,dialogui,widget,leaflet";
        assertEquals(expectedPluginList,CKEditorResource.combine(existingPluginsList,newPluginsList));
    }

    @Test
    public void testCombineEmptyPlugins(){
        String existingPluginsList = null;
        String newPluginsList = "timestamp,abbr,leaflet,widget,lineutils,plugin3";
        String expectedPluginList  = "timestamp,abbr,leaflet,widget,lineutils,plugin3";
        assertEquals(expectedPluginList,CKEditorResource.combine(existingPluginsList,newPluginsList));
    }
}
