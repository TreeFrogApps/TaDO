package com.treefrogapps.TaDo;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemRecyclerAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<ItemsListData> listItemsDataArrayList = new ArrayList<>();

    public ItemRecyclerAdapter (Context context, ArrayList<ItemsListData> listItemsDataArrayList){

        this.context = context;
        this.listItemsDataArrayList = listItemsDataArrayList;
    }




    @Override
    public ItemRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item_view, viewGroup, false);

        return new MyViewHolder(itemView);
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{

        TextView recyclerViewItemID; // use for accessing unique item Id - with this duplicates aren't a problem

        TextView recyclerViewItemTextView;
        TextView recyclerViewHoursTextView;
        TextView recyclerViewMinsTextView;

        public MyViewHolder(View itemView) {
            super(itemView);

            recyclerViewItemID = (TextView) itemView.findViewById(R.id.recyclerItemID);

            recyclerViewItemTextView = (TextView) itemView.findViewById(R.id.recyclerItemTextView);
            recyclerViewHoursTextView = (TextView) itemView.findViewById(R.id.recyclerHoursTextView);
            recyclerViewMinsTextView = (TextView) itemView.findViewById(R.id.recyclerMinsTextView);

            itemView.setOnLongClickListener(this);

        }

        @Override
        public boolean onLongClick(View v) {

            // TODO
            return false;
        }
    }


    @Override
    public void onBindViewHolder(ItemRecyclerAdapter.MyViewHolder myViewHolder, int i) {

        myViewHolder.recyclerViewItemID.setText(listItemsDataArrayList.get(i).getItemId());

        myViewHolder.recyclerViewItemTextView.setText(listItemsDataArrayList.get(i).getItem());

        String duration = listItemsDataArrayList.get(i).getDuration();
        String[] timeArray = duration.split(":");

        myViewHolder.recyclerViewHoursTextView.setText(timeArray[0]);
        myViewHolder.recyclerViewMinsTextView.setText(timeArray[1]);

    }

    @Override
    public int getItemCount() {
        return listItemsDataArrayList.size();
    }
}
