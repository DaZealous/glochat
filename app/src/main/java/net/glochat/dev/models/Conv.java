package net.glochat.dev.models;

public class Conv {

    public boolean seen;
    public long timestamp;
    public String key;
    public String type;

    public Conv(){

    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Conv(boolean seen, long timestamp, String key) {
        this.seen = seen;
        this.timestamp = timestamp;
        this.key = key;
    }
}
