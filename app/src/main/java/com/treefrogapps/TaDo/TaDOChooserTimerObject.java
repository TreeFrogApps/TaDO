package com.treefrogapps.TaDo;


public class TaDOChooserTimerObject {

    private CurrentItemListData currentItemListData;
    private long startTimeInMillis;
    private long timeOnExitInMillis;
    private long systemTimeOnExit;
    private boolean isActive;
    private boolean isPaused;
    public boolean isTimerObject;

    public TaDOChooserTimerObject(){

    }

    public TaDOChooserTimerObject(CurrentItemListData currentItemListData, long startTimeInMillis,
                                  long timeOnExitInMillis, long systemTimeOnExit, boolean isActive, boolean isPaused){

        this.currentItemListData = currentItemListData;
        this.startTimeInMillis = startTimeInMillis;
        this.timeOnExitInMillis = timeOnExitInMillis;
        this.systemTimeOnExit = systemTimeOnExit;
        this.isActive = isActive;
        this.isPaused = isPaused;

        isTimerObject = true;
    }


    public CurrentItemListData getCurrentItemListData() {
        return currentItemListData;
    }

    public void setCurrentItemListData(CurrentItemListData currentItemListData) {
        this.currentItemListData = currentItemListData;
    }

    public long getStartTimeInMillis() {
        return startTimeInMillis;
    }

    public void setStartTimeInMillis(long startTimeInMillis) {
        this.startTimeInMillis = startTimeInMillis;
    }

    public long getTimeOnExitInMillis() {
        return timeOnExitInMillis;
    }

    public void setTimeOnExitInMillis(long timeOnExitInMillis) {
        this.timeOnExitInMillis = timeOnExitInMillis;
    }

    public long getSystemTimeonExit() {
        return systemTimeOnExit;
    }

    public void setSystemTimeOnExit(long systemTimeOnExit) {
        this.systemTimeOnExit = systemTimeOnExit;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setIsPaused(boolean isPaused) {
        this.isPaused = isPaused;
    }


}
