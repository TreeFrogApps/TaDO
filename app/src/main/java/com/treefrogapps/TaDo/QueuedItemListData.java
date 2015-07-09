package com.treefrogapps.TaDo;


public class QueuedItemListData {

    private String queuedItemId;
    private String itemId;

    public QueuedItemListData(){

    }

    public void setQueuedItemId(String queuedItemId){
        this.queuedItemId = queuedItemId;
    }

    public String getQueuedItemId(){
        return this.queuedItemId;
    }

    public void setItemId(String itemId){
        this.itemId = itemId;
    }

    public String getItemId(){
        return this.itemId;
    }
}
