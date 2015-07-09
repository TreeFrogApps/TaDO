package com.treefrogapps.TaDo;

public class CurrentItemListData {

    private String currentItemId;
    private String itemId;

    public CurrentItemListData(){

    }

    public void setCurrentItemId(String currentItemId){
        this.currentItemId = currentItemId;
    }

    public String getCurrentItemId(){
        return this.currentItemId;
    }

    public void setItemId(String itemId){
        this.itemId = itemId;
    }

    public String getItemId(){
        return this.itemId;
    }
}
