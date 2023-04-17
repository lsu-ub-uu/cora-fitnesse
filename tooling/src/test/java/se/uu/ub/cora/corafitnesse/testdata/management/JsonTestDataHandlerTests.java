package se.uu.ub.cora.corafitnesse.testdata.management;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.testng.Assert.*;

public class JsonTestDataHandlerTests {

    private static final String ROOT_PATH_TEST_RESOURCES = "tooling/src/test/resources";
    private static final String ROOT_PATH_INCLUDES = "tooling/src/test/resources/subdir_includes/WithJson";
    private static final String ROOT_PATH_TEMP = "tooling/src/test/resources/subdir_includes/temp";

    @BeforeClass
    public void copyFilesToTempWorkingFolder() throws IOException {
        File source = new File(ROOT_PATH_INCLUDES);
        File destination = new File(ROOT_PATH_TEMP);
        FileUtils.copyDirectory(source, destination);
    }

    @Test
    // Test that we can collect wiki pages containing json based on a root path and exclude other files
    public void collectJsonWikiPages() {
        // Arrange
        JsonTestDataHandler jsonDataManager = new JsonTestDataHandler(ROOT_PATH_TEST_RESOURCES);

        // Act
        jsonDataManager.collectWikiPagesWithJson();

        // Assert (note that it also considers files copied into subdir_includes/temp)
        assertEquals(jsonDataManager.getWikiPages().size(), 4);
    }

    @Test
    // Test end-to-end that we can insert validation type into wiki files with json
    public void transformWikiPages() throws IOException {
        // Arrange
        JsonTestDataHandler jsonDataManager = new JsonTestDataHandler(ROOT_PATH_TEMP);

        // Act
        jsonDataManager.collectWikiPagesWithJson();
        jsonDataManager.insertValidationType();

        // Assert
        assertEquals(jsonDataManager.getWikiPages().size(), 2);
        pagesTransformedCorrectly(jsonDataManager.getWikiPages());
        assertEquals(countValidationTypeMatches(jsonDataManager), 8);
    }

    // check that all transformed wiki lines with json on all pages were correctly inserted with validation type
    // also check in the actual written files that validation type is part of the content expected number of times
    private void pagesTransformedCorrectly(List<WikiPage> wikiPages){
        for(WikiPage wikiPage : wikiPages){
            for(WikiLine transformedWikiLine : wikiPage.getJsonWikiLines()){
                  assertTrue(transformedWikiLine.content.contains("validationType"));
            }
        }
    }

    private int countValidationTypeMatches(JsonTestDataHandler jsonTestDataHandler) throws IOException {
        int totalCount = 0;
        for(WikiPage wikiPage : jsonTestDataHandler.getWikiPages()){
            totalCount += StringUtils.countMatches(Files.readString(wikiPage.getPath()), "validationType");
        }
        return totalCount;
    }


}
