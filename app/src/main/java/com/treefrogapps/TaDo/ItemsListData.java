package com.treefrogapps.TaDo;


public class ItemsListData {

    private String itemId;
    private String titleId;
    private String title;
    private String item;
    private String itemDetail;
    private String duration;
    private String dateTime;
    private String itemDone;
    private String itemPriority;

    public ItemsListData(){

    }


    public void setItemId(String item_id) {
        this.itemId = item_id;
    }

    public String getItemId() {
        return String.valueOf(this.itemId);
    }

    public void setTitleId(String title_id) {
        this.titleId = title_id;
    }

    public String getTitleId() {
        return this.titleId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getItem() {
        return this.item;
    }

    public void setItemDetail(String itemDetail){
        this.itemDetail = itemDetail;
    }

    public String getItemDetail(){
        return this.itemDetail;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDuration() {
        return this.duration;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDateTime() {
        return this.dateTime;
    }

    public void setItemDone(String itemDone) {
        this.itemDone = itemDone;
    }

    public String getItemDone() {
        return this.itemDone;
    }

    public void setItemPriority(String itemPriority){
        this.itemPriority = itemPriority;
    }

    public String getItemPriority(){
        return this.itemPriority;
    }
}
