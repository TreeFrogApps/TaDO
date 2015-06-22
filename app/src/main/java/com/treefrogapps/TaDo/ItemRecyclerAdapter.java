package com.treefrogapps.TaDo;


import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemRecyclerAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<ItemsListData> listItemsDataArrayList;

    public ItemRecyclerAdapter(Context context, ArrayList<ItemsListData> listItemsDataArrayList) {

        this.context = context;
        this.listItemsDataArrayList = listItemsDataArrayList;
    }


    @Override
    public ItemRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        Log.e("Recycler adapter size", String.valueOf(getItemCount()));

        View itemView = LayoutInflater.from(viewGroup.getContext().getApplicationContext()).inflate(R.layout.recycler_item_view, viewGroup, false);

        return new MyViewHolder(itemView);
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {

        private TextView recyclerViewItemID; // visibility - gone - use for accessing unique item Id - with this duplicates aren't a problem
        private TextView recyclerViewItemDone; // visibility - gone - used just check for tagging 'Done' or 'Not Done'
        private LinearLayout recyclerViewLinearLayout;
        private LinearLayout recyclerViewItemDoneView;
        private ImageView recyclerViewImageView;

        private TextView recyclerViewItemTextView;
        private TextView recyclerViewHoursTextView;
        private TextView recyclerViewMinsTextView;

        public MyViewHolder(View itemView) {
            super(itemView);

            recyclerViewItemID = (TextView) itemView.findViewById(R.id.recyclerItemID);
            recyclerViewItemDone = (TextView) itemView.findViewById(R.id.recyclerItemDone);
            recyclerViewLinearLayout = (LinearLayout) itemView.findViewById(R.id.recyclerViewLinearLayout);
            recyclerViewItemDoneView = (LinearLayout) itemView.findViewById(R.id.recyclerViewItemDoneView);
            recyclerViewImageView = (ImageView) itemView.findViewById(R.id.recyclerViewImageView);

            recyclerViewItemTextView = (TextView) itemView.findViewById(R.id.recyclerItemTextView);
            recyclerViewHoursTextView = (TextView) itemView.findViewById(R.id.recyclerHoursTextView);
            recyclerViewMinsTextView = (TextView) itemView.findViewById(R.id.recyclerMinsTextView);

            itemView.setOnClickListener(this);

        }

        @Override
        public boolean onLongClick(View v) {

            // TODO
            return false;
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(itemView.getContext(), "ITEM CLICKED", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBindViewHolder(ItemRecyclerAdapter.MyViewHolder myViewHolder, int i) {

        myViewHolder.recyclerViewItemID.setText(listItemsDataArrayList.get(i).getItemId());
        myViewHolder.recyclerViewItemDone.setText(listItemsDataArrayList.get(i).getItemDone());
        myViewHolder.recyclerViewItemDone.setText(listItemsDataArrayList.get(i).getItemDone());

        myViewHolder.recyclerViewItemTextView.setText(listItemsDataArrayList.get(i).getItem());

        String duration = listItemsDataArrayList.get(i).getDuration();
        String[] timeArray = duration.split(":");

        myViewHolder.recyclerViewHoursTextView.setText(timeArray[0]);
        myViewHolder.recyclerViewMinsTextView.setText(timeArray[1]);

        //set background color based on item done - 'Y'
        if (myViewHolder.recyclerViewItemDone.getText().toString().equals(Constants.ITEM_DONE)) {
            myViewHolder.recyclerViewImageView.setBackgroundResource(R.mipmap.ic_item_done);
            myViewHolder.recyclerViewLinearLayout.setBackgroundColor(context.getResources().getColor(R.color.grey_very_light));

        } else {
            myViewHolder.recyclerViewImageView.setBackgroundResource(R.mipmap.ic_item_tado);
            myViewHolder.recyclerViewLinearLayout.setBackgroundColor(Color.TRANSPARENT);
        }

    }

    @Override
    public int getItemCount() {
        return listItemsDataArrayList.size();
    }
}
