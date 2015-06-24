package com.treefrogapps.TaDo;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class RestoreRecyclerPosition {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    public RestoreRecyclerPosition(RecyclerView recyclerView, LinearLayoutManager linearLayoutManager){

        this.recyclerView = recyclerView;
        this.linearLayoutManager = linearLayoutManager;
    }

    public RestoreRecyclerPosition.RecyclerPositionValues getCurrentPosition(){

        RecyclerPositionValues recyclerPositionValues = new RecyclerPositionValues();

        linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerPositionValues.setFirstItem(linearLayoutManager.findFirstVisibleItemPosition());
        View firstItemView = linearLayoutManager.findViewByPosition(recyclerPositionValues.getFirstItem());
        recyclerPositionValues.setTopOffset(firstItemView.getTop());

        return recyclerPositionValues;
    }

    public void setCurrentPosition(RecyclerPositionValues recyclerPositionValues){

        linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        linearLayoutManager.scrollToPositionWithOffset
                (recyclerPositionValues.getFirstItem(), (int) recyclerPositionValues.getTopOffset());
    }

    public class RecyclerPositionValues {

        private int firstItem;
        private float topOffset;

        public int getFirstItem() {
            return firstItem;
        }

        public void setFirstItem(int firstItem) {
            this.firstItem = firstItem;
        }

        public float getTopOffset() {
            return topOffset;
        }

        public void setTopOffset(float topOffset) {
            this.topOffset = topOffset;
        }
    }
}
