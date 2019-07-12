package com.example.spotifyauthentication.Models.Tracks;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrackDetails {

    @SerializedName("items")
    @Expose
    private List<Item> items = null;

    @SerializedName("total")
    @Expose
    private Integer total;

    @SerializedName("limit")
    @Expose
    private Integer limit;

    @SerializedName("offset")
    @Expose
    private Integer offset;

    @SerializedName("href")
    @Expose
    private String href;

    @SerializedName("previous")
    @Expose
    private String previous;

    @SerializedName("next")
    @Expose
    private String next;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }
}