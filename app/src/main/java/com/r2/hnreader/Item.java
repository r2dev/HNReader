package com.r2.hnreader;

/**
 * item class provide for fastjson library to parse json file
 */
public class Item {
    private long id;
    private String type;
    private String by;
    private long time;
    private long parent;
    private String url;
    private int score;
    private String title;
    private int descendants;
    private String text;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String user) {
        this.by = user;
    }

    public long getParent() {
        return parent;
    }

    public void setParent(long parent) {
        this.parent = parent;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDescendants() {
        return descendants;
    }

    public void setDescendants(int descendants) {
        this.descendants = descendants;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", by='" + by + '\'' +
                ", time=" + time +
                ", parent=" + parent +
                ", url='" + url + '\'' +
                ", score=" + score +
                ", title='" + title + '\'' +
                ", descendants=" + descendants +
                ", text='" + text + '\'' +
                '}';
    }
}
