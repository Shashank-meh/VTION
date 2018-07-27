package io.appice.db;

public class DbAppDataObject {

    private long rowId = -1;
    private String key;
    private String data = "";
    private long timestamp = 0;

    @Override
    public String toString() {
        String value = "";
        value += "rowid: " + this.rowId + " , key: " + this.key + " , data: " + data + " , timestmp: " + timestamp;
        return value;
    }

    public void setId(long val) {
        this.rowId = val;
    }

    public long getId() {
        return this.rowId;
    }

    public void setKey(String val) {
        this.key = val;
    }

    public String getKey() {
        return this.key;
    }

    public void setData(String val) {
        this.data = val;
    }

    public String getData() {
        return this.data;
    }

    public void setTimeStamp(long val) {
        this.timestamp = val;
    }

    public long getTimeStamp() {
        return this.timestamp;
    }
}
