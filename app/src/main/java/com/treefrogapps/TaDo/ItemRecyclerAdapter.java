package com.treefrogapps.TaDo;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemRecyclerAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<ItemsListData> listItemsDataArrayList;

    public ItemRecyclerAdapter(Context context, ArrayList<ItemsListData> listItemsDataArrayList) {

        this.context = context;
        this.listItemsDataArrayList = listItemsDataArrayList;
    }

    @Override
    public int getItemViewType(int position) {

        if (listItemsDataArrayList.get(position).getItemDone().equals("N")) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return listItemsDataArrayList.size();
    }


    @Override
    public ItemRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        switch (viewType) {
            case 0:
                View itemView0 = LayoutInflater.from(viewGroup.getContext().getApplicationContext()).inflate(R.layout.recycler_item_view, viewGroup, false);
                return new MyViewHolder(itemView0);
            case 1:
                View itemView1 = LayoutInflater.from(viewGroup.getContext().getApplicationContext()).inflate(R.layout.recycler_item_view_completed, viewGroup, false);
                return new MyViewHolder(itemView1);
        }

        View itemView = LayoutInflater.from(viewGroup.getContext().getApplicationContext()).inflate(R.layout.recycler_item_view, viewGroup, false);
        return new MyViewHolder(itemView);
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView recyclerViewItemID; // visibility - gone - use for accessing unique item Id - with this duplicates aren't a problem
        private TextView recyclerViewItemDone; // visibility - gone - used just check for tagging 'Done' or 'Not Done'
        private LinearLayout recyclerViewLinearLayout;

        private TextView recyclerViewItemTextView;
        private TextView recyclerItemDetailTextView;
        private TextView recyclerViewPriorityTextView;
        private TextView recyclerViewHoursTextView;
        private TextView recyclerViewMinsTextView;

        public MyViewHolder(View itemView) {
            super(itemView);

            recyclerViewItemID = (TextView) itemView.findViewById(R.id.recyclerItemID);
            recyclerViewItemDone = (TextView) itemView.findViewById(R.id.recyclerItemDone);
            recyclerViewLinearLayout = (LinearLayout) itemView.findViewById(R.id.recyclerViewLinearLayout);

            recyclerViewItemTextView = (TextView) itemView.findViewById(R.id.recyclerItemTextView);
            recyclerItemDetailTextView = (TextView) itemView.findViewById(R.id.recyclerItemDetailTextView);
            recyclerViewPriorityTextView = (TextView) itemView.findViewById(R.id.recyclerViewPriorityTextView);
            recyclerViewHoursTextView = (TextView) itemView.findViewById(R.id.recyclerHoursTextView);
            recyclerViewMinsTextView = (TextView) itemView.findViewById(R.id.recyclerMinsTextView);
        }
    }

    @Override
    public void onBindViewHolder(ItemRecyclerAdapter.MyViewHolder myViewHolder, final int i) {

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateItemsAddEditItemDialog createItemsAddEditItemDialog = new CreateItemsAddEditItemDialog();
                createItemsAddEditItemDialog.mOnAddItemCallBack = (CreateItemsActivity)context ;
                Bundle bundle = new Bundle();
                bundle.putString(Constants.ITEMS_ID, listItemsDataArrayList.get(i).getItemId());
                createItemsAddEditItemDialog.setArguments(bundle);
                createItemsAddEditItemDialog.show(((CreateItemsActivity)context).getSupportFragmentManager(), "Dialog03");
            }
        });

        myViewHolder.recyclerViewItemID.setText(listItemsDataArrayList.get(i).getItemId());
        myViewHolder.recyclerViewItemDone.setText(listItemsDataArrayList.get(i).getItemDone());
        myViewHolder.recyclerViewPriorityTextView.setText("!");

        // check the itemDoneStatus - color accordingly
        if (listItemsDataArrayList.get(i).getItemDone().equals("N")) {

            String itemPriorityState = listItemsDataArrayList.get(i).getItemPriority();

            switch (itemPriorityState) {
                case "L":
                    myViewHolder.recyclerViewPriorityTextView.setBackgroundResource(R.drawable.priority_circle_low_40dp);
                    break;
                case "M":
                    myViewHolder.recyclerViewPriorityTextView.setBackgroundResource(R.drawable.priority_circle_med_40dp);
                    break;
                case "H":
                    myViewHolder.recyclerViewPriorityTextView.setBackgroundResource(R.drawable.priority_circle_high_40dp);
                    break;
            }
        } else {
            myViewHolder.recyclerViewPriorityTextView.setBackgroundResource(R.mipmap.ic_circle_complete_grey);
            myViewHolder.recyclerViewPriorityTextView.setText("");
        }

        myViewHolder.recyclerViewItemTextView.setText(listItemsDataArrayList.get(i).getItem());
        myViewHolder.recyclerItemDetailTextView.setText(listItemsDataArrayList.get(i).getItemDetail());

        String duration = listItemsDataArrayList.get(i).getDuration();
        String[] timeArray = duration.split(":");
        // done to drop first zero
        myViewHolder.recyclerViewHoursTextView.setText(String.valueOf(Integer.parseInt(timeArray[0])));
        myViewHolder.recyclerViewMinsTextView.setText(timeArray[1]);

    }
}