package se.uu.ub.cora.corafitnesse.testdata.management;

import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.testng.Assert.assertEquals;

public class WikiPageTests {

    private static final Path wikiPagePath = Paths.get("tooling/src/test/resources/subdir_includes/FixtureTableLines.wiki");
    @Test
    // Test that wiki page contains correct number of parsed wiki lines
    public void TestProcessWikiPage(){
        // Arrange
        WikiPage wikiPage = new WikiPage(wikiPagePath);

        // Act
        wikiPage.processJsonLines();

        // Assert
        assertEquals(wikiPage.getWikiLines().size(), 3);
    }

}
