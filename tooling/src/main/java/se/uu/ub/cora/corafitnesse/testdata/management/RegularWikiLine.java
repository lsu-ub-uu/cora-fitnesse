package se.uu.ub.cora.corafitnesse.testdata.management;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

class RegularWikiLine extends WikiLine {
    private static final String REGULAR_LINE_SEPARATOR = "\\{!-";

    public RegularWikiLine(int lineNo, String content, WikiPage page) {
        this.lineNo = lineNo;
        this.content = content;
        this.currentPage = page;
    }

    boolean tryParse(){
        splitIntoColumns();
        parse();
        return isValid();
    }

    private boolean isValid(){
        return containsJsonData() &&
                getRecordInfo() != null &&
                isNotCommentedOut() &&
                this.recordType != null;
    }

    private void parse() {
        json = new JSONObject(columns[1]);
        extractRecordType();
    }

    @Override
    public void joinColumns() {
        columns[1] = json.toString();
        content = String.join("{!-", columns);
        content = content + "-!}";
    }


    void splitIntoColumns(){
        String contentTemp = StringUtils.removeEnd(content, "-!}");
        columns = contentTemp.split(REGULAR_LINE_SEPARATOR);
    }

    private boolean hasExternalRecordType(){
        String defineRecordTypeLine = this.currentPage.findDefineRecordTypeLine();
        return defineRecordTypeLine != null;
    }

    boolean hasEmbeddedRecordType(){
        String recordTypeSection = """
                "name":"type""";

        return content.contains(recordTypeSection);
    }

    private void extractRecordType(){
       if (hasEmbeddedRecordType()) {
            JSONArray recordInfoMembers = getRecordInfo().getJSONArray("children");
            for (Object recordInfoMember : recordInfoMembers) {
                JSONObject recordInfoMemberJson = (JSONObject) recordInfoMember;
                // type in record info defines the record type
                if (recordInfoMemberJson.getString("name").equals("type")) {
                    recordType = recordInfoMemberJson.getJSONArray("children").
                            getJSONObject(1).getString("value");
                }
            }
        }
       else if (hasExternalRecordType()){
            String recordTypeDef = currentPage.findDefineRecordTypeLine().replace("}", "");
            this.recordType = recordTypeDef.replace("!define recordType {","");
        }
    }
}
