package se.uu.ub.cora.corafitnesse.testdata.management;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

public class FixtureTableWikiLine extends WikiLine{

    // use this to know at what position in the line to insert the json again after modifications are done
    private int jsonColumnPosition;

    public FixtureTableWikiLine(int lineNo, String content) {
        this.lineNo = lineNo;
        this.content = content;

    }

    boolean tryParse(){
        splitIntoColumns();
        parse();
        return isValid();
    }

    private boolean isValid() {
        return containsJsonData() &&
                getRecordInfo() != null &&
                isNotCommentedOut();
    }

    private void parse() {
        for (int i = 0; i < columns.length; i++) {
            if (isJson(columns[i])) {
                // usually json data is the 3rd or 4th column on the line
                this.json = new JSONObject(columns[i]);
                this.jsonColumnPosition = i;
                break;
            }

        }
        // record type is in 2nd column by convention in fixture lines
        this.recordType = columns[1].trim();
    }

    @Override
    public void joinColumns() {
        columns[jsonColumnPosition] = " " + json.toString() + " ";
        content = String.join(FIXTURE_LINE_SEPARATOR, columns);
        content = FIXTURE_LINE_SEPARATOR + content + FIXTURE_LINE_SEPARATOR;

    }

    protected void splitIntoColumns(){
        String contentTemp = StringUtils.removeStart(content, FIXTURE_LINE_SEPARATOR);
        contentTemp = StringUtils.removeEnd(contentTemp, FIXTURE_LINE_SEPARATOR);
        columns = contentTemp.split("\\|");
    }

}
