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
    }

    public void processJsonLines() {
        try {
            wikiLineStrings = Files.readAllLines(path);
            Integer lineNo = 0;
            for (String wikiLineString : wikiLineStrings) {
                WikiLine wikiLine = new WikiLine(lineNo, wikiLineString);
                if (wikiLine.tryParse()) {
                    wikiLine.parse();
                    wikiLines.add(wikiLine);
                }
                lineNo++;
            }

        } catch (IOException exception) {
            System.err.println("Error occured when processing json lines: " + exception.getMessage());
        }
    }

    public void insertValidationType(String jsonFragmentTemplate) {
        for (WikiLine wikiLine : wikiLines) {
            wikiLine.insertValidationType(jsonFragmentTemplate);
            wikiLine.joinColumns();
        }

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

    public Boolean isWikiPage() {
        return path.toString().endsWith(".wiki") || path.toString().endsWith("content.txt");
    }

    public Boolean containsJson() {
        try {
            wikiLineStrings = Files.readAllLines(path);
            Integer lineNo = 0;
            for (String wikiLineString : wikiLineStrings) {
                WikiLine wikiLine = new WikiLine(lineNo, wikiLineString);
                if (wikiLine.tryParse()) {
                    return true;
                }
            }
        } catch (IOException exception) {
            System.err.println("Error occured when processing json lines: " + exception.getMessage());
        }
        return false;
    }

    public List<WikiLine> getWikiLines() {
        return wikiLines;
    }

}
