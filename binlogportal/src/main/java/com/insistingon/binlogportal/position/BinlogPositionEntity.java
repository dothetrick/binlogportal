package com.insistingon.binlogportal.position;

public class BinlogPositionEntity {
    String binlogName;
    Long position;
    Long serverId;

    public String getBinlogName() {
        return binlogName;
    }

    public void setBinlogName(String binlogName) {
        this.binlogName = binlogName;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    @Override
    public String toString() {
        return "BinlogPositionEntity{" +
                "binlogName='" + binlogName + '\'' +
                ", position=" + position +
                ", serverId=" + serverId +
                '}';
    }
}
