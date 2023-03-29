package se.uu.ub.cora.corafitnesse.testdata.management;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class JsonTestDataHandlerTests {

    private static final String rootPath = "tooling/src/test/resources";

    @Test
    // Test that we can collect wiki pages containing json based on a root path and exclude other files
    public void collectJsonWikiPages() {
        // Arrange
        JsonTestDataHandler jsonDataManager = new JsonTestDataHandler(rootPath);

        // Act
        jsonDataManager.collectWikiPagesWithJson();

        // Assert
        assertEquals(jsonDataManager.getWikiPages().size(), 1);
    }


}
