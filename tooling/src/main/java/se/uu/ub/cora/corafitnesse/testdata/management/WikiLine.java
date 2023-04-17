package se.uu.ub.cora.corafitnesse.testdata.management;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

abstract class WikiLine {

    protected static final String FIXTURE_LINE_SEPARATOR = "|";
    protected static final String REGULAR_LINE_SEPARATOR = "{!-";

    protected int lineNo;
    protected String content;
    protected JSONObject json;
    protected String recordType;
    protected String[] columns;
    protected WikiPage currentPage;

    public static WikiLine tryParse(int lineNo, String content, WikiPage wikiPage) {
        try {
            if (content.trim().startsWith(FIXTURE_LINE_SEPARATOR)) {
                FixtureTableWikiLine fixtureTableWikiLine = new FixtureTableWikiLine(lineNo, content);
                if (fixtureTableWikiLine.tryParse()){
                    return  fixtureTableWikiLine;
                }
            }
            else if(content.contains(REGULAR_LINE_SEPARATOR)) {
                RegularWikiLine regularWikiLine = new RegularWikiLine(lineNo, content, wikiPage);
                if (regularWikiLine.tryParse()){
                    return regularWikiLine;
                }
            }
        } catch (Exception exception) {
            return null;
        }

        return null;
    }

    public abstract void joinColumns();

    public void insertValidationType(String jsonFragmentTemplate) {
        String jsonFragment = jsonFragmentTemplate.replace("$ValidationType", recordType);
        JSONObject validationType = new JSONObject(jsonFragment);

        JSONObject recordInfo = getRecordInfo();
        recordInfo.getJSONArray("children").put(validationType);
    }

    protected JSONObject getRecordInfo() {
        JSONArray children = json.getJSONArray("children");
        for (Object child : children) {
            JSONObject jsonChild = (JSONObject) child;
            if (jsonChild.get("name").equals("recordInfo")) {
                return jsonChild;
            }
        }
        return null;
    }

    protected boolean containsJsonData(){
        for (String column : columns) {
            if (isJson(column)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isNotCommentedOut(){
       return !this.content.startsWith("#");
    }

    public int getLineNo() {
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
