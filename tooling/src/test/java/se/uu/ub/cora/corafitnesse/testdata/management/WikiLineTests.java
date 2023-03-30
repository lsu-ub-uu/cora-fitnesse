package se.uu.ub.cora.corafitnesse.testdata.management;

import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.testng.Assert.*;


public class WikiLineTests {
    private static final int LINE_NO = 0;
    private final WikiPage currentPageDefault = new WikiPage(
            Paths.get("tooling/src/test/resources/subdir_includes/Dummy.wiki"));


    @Test
    // Test that fixture table based wiki line with json is parsed correctly during creation
    public void TestParseFixtureTableWikiLine(){
        // Arrange and Act (since the parse itself is part of object creation flow)
        WikiLine wikiLine = WikiLine.tryParse(LINE_NO, getFixtureLineStringBeforeInsertion(), currentPageDefault);

        // Assert
        assertEquals(wikiLine.getRecordType(),"fitnessStudentThesis");
        assertEquals(wikiLine.getJson().getString("name"), "fitnesseExample");
    }

    @Test
    // Test that regular wiki line with json is parsed correctly during creation
    // This type of wiki line contains the record type as part of record info
    public void TestParseRegularWikiLine(){
        // Arrange and Act
        WikiLine wikiLine = WikiLine.tryParse(LINE_NO, getRegularWikiLineBeforeInsertion(), currentPageDefault);

        // Assert
        assertEquals(wikiLine.getRecordType(),"testWorkout");
        assertEquals(wikiLine.getJson().getString("name"), "workout");
    }

    @Test
    // Same as above but with the difference that the record type is defined separately in the beginning of the page
    public void TestParseRegularWikiLineWithExternalRecordType(){
        // Arrange
        Path wikiPagePath = Paths.get("tooling/src/test/resources/subdir_includes/RegularLinesWithExternalRecordType.wiki");
        WikiPage wikiPage = new WikiPage(wikiPagePath);
        WikiLine wikiLine = WikiLine.tryParse(LINE_NO, getRegularWikiLineWithoutInternalRecordType(), wikiPage);

        // Assert
        assertEquals(wikiLine.getRecordType(),"testWorkoutExternal");
        assertEquals(wikiLine.getJson().getString("name"), "workout");
    }

    @Test
    // Test that the wiki line is reassembled with same content (disregarding whitespace)
    public void TestJoinColumns(){
        // Arrange
        WikiLine wikiLine = WikiLine.tryParse(LINE_NO, getFixtureLineStringBeforeInsertion(), currentPageDefault);

        // Act
        wikiLine.joinColumns();

        // Assert
        assertEquals(wikiLine.getContent().replace(" ","").length(),
                getFixtureLineStringBeforeInsertion().replace(" ","").length());
    }

    @Test
    public void TestInsertValidationType(){
        // Arrange
        WikiLine wikiLine = WikiLine.tryParse(LINE_NO, getFixtureLineStringBeforeInsertion(), currentPageDefault);

        // Act
        wikiLine.insertValidationType(JsonTestDataHandler.getJsonFragmentForValidationType());

        // Assert
        JSONAssert.assertEquals(getExpectedJsonAfterInsertion(), wikiLine.getJson().toString(), false);

    }

    private String getRegularWikiLineWithoutInternalRecordType(){

        return """
                !define originalData {!-{"children":[{"children":[{"name":"id","value":"cirkelfys1"},{"children":[{"name":"linkedRecordType","value":"system"},{"name":"linkedRecordId","value":"systemOne"}],"name":"dataDivider"},{"children":[{"name":"linkedRecordType","value":"user"},{"name":"linkedRecordId","value":"131313"}],"name":"createdBy"},{"repeatId":"0","children":[{"children":[{"name":"linkedRecordType","value":"user"},{"name":"linkedRecordId","value":"131313"}],"name":"updatedBy"}],"name":"updated"}],"name":"recordInfo"},{"name":"workoutName1","value":"cirkelfys"},{"name":"numOfParticipants1","value":"45"},{"name":"numOfParticipants2","value":"42"},{"name":"selectableAttribute1","value":"valueOne","attributes":{"exampleAttributeChoice":"one"}},{"name":"selectableAttribute2","value":"valueTwo","attributes":{"exampleAttributeChoice":"two"}},{"name":"selectableAttribute3","value":"valueThree","attributes":{"exampleAttributeChoice":"three"}},{"name":"instructorId1","value":"3561"},{"name":"instructorId2","value":"3562"},{"name":"instructorId3","value":"3563"},{"children":[{"name":"numOfExercises1_1","value":"15"},{"name":"laps1_1","value":"5"},{"name":"laps1_2","value":"10"},{"name":"duration1_1","value":"4h"},{"name":"duration1_2","value":"5h"},{"name":"duration1_3","value":"6h"}],"name":"exercises1"},{"children":[{"name":"nickName1_1","value":"Annela"},{"name":"firstName1_1","value":"Anna"},{"name":"firstName1_2","value":"Maria"},{"name":"lastName1_1","value":"Ledare"},{"name":"lastName1_2","value":"Chef"},{"name":"lastName1_3","value":"Boss"}],"name":"instructorName1"},{"children":[{"name":"nickName2_1","value":"Annela2"},{"name":"firstName2_1","value":"Anna2"},{"name":"firstName2_2","value":"Maria2"},{"name":"lastName2_1","value":"Ledare2"},{"name":"lastName2_2","value":"Chef2"},{"name":"lastName2_3","value":"Boss2"}],"name":"instructorName2"},{"children":[{"name":"rating1_1","value":"4"},{"name":"ratingDate1_1","value":"2021-01-12"},{"name":"ratingDate1_2","value":"2021-01-13"},{"name":"voteCount1_1","value":"434123"},{"name":"voteCount1_2","value":"4341223"},{"name":"voteCount1_3","value":"4341233"}],"name":"popularity1"},{"children":[{"name":"rating2_1","value":"42"},{"name":"ratingDate2_1","value":"2021-02-12"},{"name":"ratingDate2_2","value":"2021-02-13"},{"name":"voteCount2_1","value":"4341232"},{"name":"voteCount2_2","value":"43412232"},{"name":"voteCount2_3","value":"43412332"}],"name":"popularity2"},{"children":[{"name":"rating3_1","value":"43"},{"name":"ratingDate3_1","value":"2021-03-12"},{"name":"ratingDate3_2","value":"2021-03-13"},{"name":"voteCount3_1","value":"34341232"},{"name":"voteCount3_2","value":"343412232"},{"name":"voteCount3_3","value":"343412332"}],"name":"popularity3"}],"name":"workout"}-!}""";
    }

    private String getRegularWikiLineBeforeInsertion(){
        return """
                !define originalData {!-{"children":[{"children":[{"name":"id","value":"cirkelfys1"},{"children":[{"name":"linkedRecordType","value":"system"},{"name":"linkedRecordId","value":"systemOne"}],"name":"dataDivider"},{"children":[{"name":"linkedRecordType","value":"recordType"},{"name":"linkedRecordId","value":"testWorkout"}],"name":"type"},{"children":[{"name":"linkedRecordType","value":"user"},{"name":"linkedRecordId","value":"131313"}],"name":"createdBy"},{"repeatId":"0","children":[{"children":[{"name":"linkedRecordType","value":"user"},{"name":"linkedRecordId","value":"131313"}],"name":"updatedBy"}],"name":"updated"}],"name":"recordInfo"},{"name":"workoutName1","value":"cirkelfys"},{"name":"numOfParticipants1","value":"45"},{"name":"numOfParticipants2","value":"42"},{"name":"selectableAttribute1","value":"valueOne","attributes":{"exampleAttributeChoice":"one"}},{"name":"selectableAttribute2","value":"valueTwo","attributes":{"exampleAttributeChoice":"two"}},{"name":"selectableAttribute3","value":"valueThree","attributes":{"exampleAttributeChoice":"three"}},{"name":"instructorId1","value":"3561"},{"name":"instructorId2","value":"3562"},{"name":"instructorId3","value":"3563"},{"children":[{"name":"numOfExercises1_1","value":"15"},{"name":"laps1_1","value":"5"},{"name":"laps1_2","value":"10"},{"name":"duration1_1","value":"4h"},{"name":"duration1_2","value":"5h"},{"name":"duration1_3","value":"6h"}],"name":"exercises1"},{"children":[{"name":"nickName1_1","value":"Annela"},{"name":"firstName1_1","value":"Anna"},{"name":"firstName1_2","value":"Maria"},{"name":"lastName1_1","value":"Ledare"},{"name":"lastName1_2","value":"Chef"},{"name":"lastName1_3","value":"Boss"}],"name":"instructorName1"},{"children":[{"name":"nickName2_1","value":"Annela2"},{"name":"firstName2_1","value":"Anna2"},{"name":"firstName2_2","value":"Maria2"},{"name":"lastName2_1","value":"Ledare2"},{"name":"lastName2_2","value":"Chef2"},{"name":"lastName2_3","value":"Boss2"}],"name":"instructorName2"},{"children":[{"name":"rating1_1","value":"4"},{"name":"ratingDate1_1","value":"2021-01-12"},{"name":"ratingDate1_2","value":"2021-01-13"},{"name":"voteCount1_1","value":"434123"},{"name":"voteCount1_2","value":"4341223"},{"name":"voteCount1_3","value":"4341233"}],"name":"popularity1"},{"children":[{"name":"rating2_1","value":"42"},{"name":"ratingDate2_1","value":"2021-02-12"},{"name":"ratingDate2_2","value":"2021-02-13"},{"name":"voteCount2_1","value":"4341232"},{"name":"voteCount2_2","value":"43412232"},{"name":"voteCount2_3","value":"43412332"}],"name":"popularity2"},{"children":[{"name":"rating3_1","value":"43"},{"name":"ratingDate3_1","value":"2021-03-12"},{"name":"ratingDate3_2","value":"2021-03-13"},{"name":"voteCount3_1","value":"34341232"},{"name":"voteCount3_2","value":"343412232"},{"name":"voteCount3_3","value":"343412332"}],"name":"popularity3"}],"name":"workout"}-!}""";
    }

    private String getFixtureLineStringBeforeInsertion(){
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
