package com.treefrogapps.TaDo;


public class ItemsListData {

    private int titleId;
    // get name from title spinner when querying database -  insertIntoItemsDatabase(ItemsListData itemsListData)
    private String title;
    private String item;
    private String duration;
    private String dateTime;

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

    public void setDuration(String duration){
        this.duration = duration;
    }

    public String getDuration(){
        return this.duration;
    }

    public void setDateTime(String dateTime){
        this.dateTime = dateTime;
    }

    public String getDateTime(){
        return this.dateTime;
    }
}
