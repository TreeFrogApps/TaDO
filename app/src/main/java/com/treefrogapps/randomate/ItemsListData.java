package com.treefrogapps.randomate;


public class ItemsListData {

    private int titleId;
    private String item;

    public void setTitleId (int title_id){
        this.titleId = title_id;
    }

    public int getTitleId() {
        return this.titleId;
    }

    public void setItem (String item){
        this.item = item;
    }

    public String getItem() {
        return this.item;
    }
}
