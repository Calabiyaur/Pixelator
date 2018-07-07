package main.java.view.tool;

import junit.framework.Assert;

class ToolsTest {

    //@Test
    void fromString() {
        Tools[] values = Tools.values();
        for (int i = 0; i < values.length; i++) {
            Tools tool = values[i];
            String name = tool.toString();
            Assert.assertEquals(tool, Tools.fromString(name));
        }
    }

}
