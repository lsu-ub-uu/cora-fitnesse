package se.uu.ub.cora.corafitnesse.testdata.management;

import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.Test;
import static org.testng.Assert.*;


public class WikiLineTests {
    private static final Integer LINE_NO = 0;


    @Test
    // Test that wiki line is parsed correctly during creation
    public void TestParseWikiLine(){
        // Arrange
        WikiLine wikiLine = new WikiLine(LINE_NO,getWikiLineStringBeforeInsertion());

        // Act
        wikiLine.parse();

        // Assert
        assertEquals(wikiLine.getRecordType(),"fitnessStudentThesis");
        assertEquals(wikiLine.getJson().get("name"), "fitnesseExample");
    }

    @Test
    // Test that the wiki line is reassembled with same content (disregarding whitespace)
    public void TestJoinColumns(){
        // Arrange
        WikiLine wikiLine = new WikiLine(LINE_NO, getWikiLineStringBeforeInsertion());

        // Act
        wikiLine.parse();
        wikiLine.joinColumns();

        // Assert
        assertEquals(wikiLine.getContent().replace(" ","").length(),
                getWikiLineStringBeforeInsertion().replace(" ","").length());
    }

    @Test
    public void TestInsertValidationType(){
        // Arrange
        WikiLine wikiLine = new WikiLine(LINE_NO, getWikiLineStringBeforeInsertion());

        // Act
        wikiLine.parse();
        wikiLine.insertValidationType(JsonTestDataHandler.getJsonFragmentForValidationType());

        // Assert
        JSONAssert.assertEquals(getExpectedJsonAfterInsertion(), wikiLine.getJson().toString(), false);

    }


    private String getWikiLineStringBeforeInsertion(){
        return """
        | $adminAuthToken | fitnessStudentThesis | {"name": "fitnesseExample", "children": [{ "name": "recordInfo", "children": [{ "name": "dataDivider", "children": [{ "name": "linkedRecordType", "value": "system" }, { "name": "linkedRecordId", "value": "testSystem" }]}]}, { "name": "fitnesseTextVar", "value": "This is a very long thesis" }]} | | CREATED |""";
    }

    private String getExpectedJsonAfterInsertion(){

        return """
                {
                  "children": [
                    {
                      "children": [
                        {
                          "children": [
                            {
                              "name": "linkedRecordType",
                              "value": "system"
                            },
                            {
                              "name": "linkedRecordId",
                              "value": "testSystem"
                            }
                          ],
                          "name": "dataDivider"
                        },
                        {
                          "children": [
                            {
                              "name": "linkedRecordType",
                              "value": "validationType"
                            },
                            {
                              "name": "linkedRecordId",
                              "value": "fitnessStudentThesis"
                            }
                          ],
                          "name": "validationType"
                        }
                      ],
                      "name": "recordInfo"
                    },
                    {
                      "name": "fitnesseTextVar",
                      "value": "This is a very long thesis"
                    }
                  ],
                  "name": "fitnesseExample"
                }
                """;

    }

}
