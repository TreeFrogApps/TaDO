package com.treefrogapps.TaDo;


public class ItemsListData {

    private int titleId;
    // get name from title spinner when querying database -  insertIntoItemsDatabase(ItemsListData itemsListData)
    private String title;
    private String item;

    public void setTitleId (int title_id){
        this.titleId = title_id;
    }

    public int getTitleId() {
        return this.titleId;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return this.title;
    }

    public void setItem (String item){
        this.item = item;
    }

    public String getItem() {
        return this.item;
    }
}
