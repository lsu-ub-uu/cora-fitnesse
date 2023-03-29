package se.uu.ub.cora.corafitnesse.testdata.management;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WikiLine {

    private final Integer lineNo;
    private String content;
    private JSONObject json;
    private String recordType;
    private String[] columns;

    // use this to know at what position in the line to insert the json again after modifications are done
    private Integer jsonColumnPosition;

    public WikiLine(Integer lineNo, String content) {
        this.lineNo = lineNo;
        this.content = content;
    }

    public Boolean tryParse() {
        try {
            String contentTemp = StringUtils.removeStart(content, "|");
            contentTemp = StringUtils.removeEnd(contentTemp, "|");
            columns = contentTemp.split("\\|");

            for (String column : columns) {
                if (isJson(column)) {
                    return true;
                }
            }
        } catch (Exception exception) {
            return false;
        }

        return false;
    }

    public void parse() {
        String contentTemp = StringUtils.removeStart(content, "|");
        contentTemp = StringUtils.removeEnd(contentTemp, "|");
        columns = contentTemp.split("\\|");

        for (int i = 0; i < columns.length; i++) {
            if (isJson(columns[i])) {
                // usually json data is the 3rd or 4th column on the line
                this.json = new JSONObject(columns[i]);
                this.jsonColumnPosition = i;
                break;
            }

        }

        // record type is in 2nd column by convention
        this.recordType = columns[1].trim();
    }

    public void insertValidationType(String jsonFragmentTemplate) {
        String jsonFragment = jsonFragmentTemplate.replace("$VT", recordType);
        JSONObject validationType = new JSONObject(jsonFragment);

        JSONArray children = this.json.getJSONArray("children");

        // Put validation type into record info
        for (Object child : children) {
            JSONObject jsonChild = (JSONObject) child;
            if (jsonChild.get("name").equals("recordInfo")) {
                jsonChild.getJSONArray("children").put(validationType);
            }
        }

    }

    public void joinColumns() {
        columns[jsonColumnPosition] = " " + json.toString() + " ";
        content = String.join("|", columns);
        content = "|" + content + "|";
    }

    public Integer getLineNo() {
        return lineNo;
    }

    public String getContent() {
        return content;
    }

    public String getRecordType() {
        return recordType;
    }

    public JSONObject getJson() {
        return json;
    }

    public static boolean isJson(String jsonString) {
        try {
            new JSONObject(jsonString);
        } catch (JSONException exception) {
            return false;
        }
        return true;
    }
}
