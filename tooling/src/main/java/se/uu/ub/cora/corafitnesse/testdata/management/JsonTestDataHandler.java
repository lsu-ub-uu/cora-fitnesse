package se.uu.ub.cora.corafitnesse.testdata.management;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


public class JsonTestDataHandler {

    private final String rootPath;
    private final List<WikiPage> wikiPages = new ArrayList<>();

    public JsonTestDataHandler(String rootPath) {
        this.rootPath = rootPath;
    }

    public void collectWikiPagesWithJson() {
        try {
            Stream<Path> stream = Files.walk(Paths.get(rootPath));
            stream.filter(Files::isRegularFile).forEach(this::addPage);
            stream.close();
        } catch (IOException exception) {
            System.err.println("Error occurred when collecting wiki pages with json: " + exception.getMessage());
        }

    }

    private void addPage(Path filePath) {
        WikiPage wikiPage = new WikiPage(filePath);
        if (wikiPage.isWikiPage() && wikiPage.containsJson()) {
            wikiPages.add(wikiPage);
        }
    }

    public void insertValidationType() {
        // loop through all the wiki pages here and call respective methods on each page
        for (WikiPage wikiPage : this.wikiPages) {
            wikiPage.processJsonLines();
            wikiPage.insertValidationType(getJsonFragmentForValidationType());
            wikiPage.writeLines();
        }
    }

    public static String getJsonFragmentForValidationType() {
        return """ 
                {"children": [{"name": "linkedRecordType","value": "validationType"},{"name": "linkedRecordId","value": "$ValidationType"}],"name": "validationType"}""";

    }

    public List<WikiPage> getWikiPages() {
        return this.wikiPages;
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("This program requires two arguments in the following order");
            System.out.println("1) what kind of test data transformation to do");
            System.out.println("2) relative path to the root of the wiki pages to be updated");
        } else {
            if (args[0].equals("InsertValidationType")){
                JsonTestDataHandler jdm = new JsonTestDataHandler(args[1]);
                jdm.collectWikiPagesWithJson();
                jdm.insertValidationType();
            }
        }
    }


}
