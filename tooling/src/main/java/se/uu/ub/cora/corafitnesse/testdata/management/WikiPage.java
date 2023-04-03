package se.uu.ub.cora.corafitnesse.testdata.management;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class WikiPage {

    private final Path path;
    private final List<WikiLine> wikiLines = new ArrayList<>();
    private List<String> wikiLineStrings;

    public WikiPage(Path path) {
        this.path = path;
        readWikiFile(path);
    }

    public void processJsonLines() {
        int lineNo = 0;
        for (String wikiLineString : wikiLineStrings) {
            // Exclude lines which already have a validationType
            if(!wikiLineString.contains("validationType")) {
                WikiLine wikiLine = WikiLine.tryParse(lineNo, wikiLineString, this);
                if (wikiLine != null) {
                    wikiLines.add(wikiLine);
                }
            }
            lineNo++;
        }
    }

    private void readWikiFile(Path path){
        try {
            wikiLineStrings = Files.readAllLines(path);
        } catch (IOException exception) {
            System.err.println("Error occured when reading wiki file: " + exception.getMessage());
        }
    }

    public void insertValidationType(String jsonFragmentTemplate) {
        for (WikiLine wikiLine : wikiLines) {
            wikiLine.insertValidationType(jsonFragmentTemplate);
            wikiLine.joinColumns();
        }

    }

    public String findDefineRecordTypeLine(){
        for (String wikiLineString : wikiLineStrings) {
            if( !wikiLineString.startsWith("#") && wikiLineString.contains("!define recordType")){
                return wikiLineString;
            }
        }
        return null;
    }

    public void writeLines() {
        for (WikiLine wikiLine : wikiLines) {
            wikiLineStrings.set(wikiLine.getLineNo(), wikiLine.getContent());
        }
        try {
            Files.write(path, wikiLineStrings, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            System.err.println("Error occured when trying to write lines back to wiki file " + exception.getMessage());
        }

    }

    public boolean isWikiPage() {
        return path.toString().endsWith(".wiki") || path.toString().endsWith("content.txt");
    }

    public boolean containsJson() {
        int lineNo = 0;
        for (String wikiLineString : wikiLineStrings) {
            WikiLine wikiLine = WikiLine.tryParse(lineNo, wikiLineString, this);
            if (wikiLine != null) {
                return true;
            }
        }
        return false;
    }

    public List<WikiLine> getJsonWikiLines() {
        return wikiLines;
    }

    public Path getPath(){
        return path;
    }

}
