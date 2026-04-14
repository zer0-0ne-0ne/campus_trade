package com.example.campus_trade.entity;

public class Collect {
    private Integer id;
    private Integer uid;
    private Integer pid;
    private String createTime;

    public Collect() {}
    public Collect(Integer uid, Integer pid) {
        this.uid = uid;
        this.pid = pid;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getUid() { return uid; }
    public void setUid(Integer uid) { this.uid = uid; }
    public Integer getPid() { return pid; }
    public void setPid(Integer pid) { this.pid = pid; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
}
