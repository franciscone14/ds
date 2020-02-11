package pt.ipb.sd;

import java.io.Serializable;

public class PeerData implements Serializable {
    String uid;
    int timestamp;

    public PeerData(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void incrementTimestamp(){
        this.timestamp++;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "PeerData{" +
                "uid='" + uid + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
