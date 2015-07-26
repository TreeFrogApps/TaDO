package com.treefrogapps.TaDo;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class TaDOChooserFragmentRecyclerAdapter extends RecyclerView.Adapter<TaDOChooserFragmentRecyclerAdapter.MyViewHolder>{
    
    private Context context;
    private ArrayList<QueuedItemListData> queuedItemListDataArrayList;
    private DBHelper dbHelper;
    
    public TaDOChooserFragmentRecyclerAdapter(Context context, ArrayList<QueuedItemListData> queuedItemListDataArrayList){
        
        this.context = context;
        this.queuedItemListDataArrayList = queuedItemListDataArrayList;

        dbHelper = new DBHelper(context);
    }

    @Override
    public int getItemCount() {
        return queuedItemListDataArrayList.size();
    }
    
    @Override
    public TaDOChooserFragmentRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        
        View itemView = LayoutInflater.from(parent.getContext().getApplicationContext()).inflate(R.layout.fragment_tado_chooser_recycler_item_view, parent, false);
        
        return new MyViewHolder(itemView);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout taDOChooserRecyclerCardView;
        private TextView taDOChooserRecyclerItemListTextView;
        private TextView taDOChooserRecyclerItemItemTextView;
        private TextView taDOChooserRecyclerItemItemDetailTextView;
        private TextView taDOChooserRecyclerItemHoursTextView;
        private TextView taDOChooserRecyclerItemMinsTextView;
        private TextView taDOChooserRecyclerItemPriorityTextView;
        
        
        public MyViewHolder(View itemView) {
            super(itemView);

            taDOChooserRecyclerCardView = (LinearLayout) itemView.findViewById(R.id.taDOChooserRecyclerCardView);
            taDOChooserRecyclerItemListTextView = (TextView) itemView.findViewById(R.id.taDOChooserRecyclerItemListTextView);
            taDOChooserRecyclerItemItemTextView = (TextView) itemView.findViewById(R.id.taDOChooserRecyclerItemItemTextView);
            taDOChooserRecyclerItemItemDetailTextView = (TextView) itemView.findViewById(R.id.taDOChooserRecyclerItemItemDetailTextView);
            taDOChooserRecyclerItemHoursTextView = (TextView) itemView.findViewById(R.id.taDOChooserRecyclerItemHoursTextView);
            taDOChooserRecyclerItemMinsTextView = (TextView) itemView.findViewById(R.id.taDOChooserRecyclerItemMinsTextView);
            taDOChooserRecyclerItemPriorityTextView = (TextView) itemView.findViewById(R.id.taDOChooserRecyclerItemPriorityTextView);
        }
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        // Get listData for single Item
        String itemId = queuedItemListDataArrayList.get(position).getItemId();
        ItemsListData itemsListData = dbHelper.getSingleItem(itemId);

        holder.taDOChooserRecyclerCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

        holder.taDOChooserRecyclerItemListTextView.setText(dbHelper.getTitle(itemsListData.getTitleId()));
        holder.taDOChooserRecyclerItemItemTextView.setText(itemsListData.getItem());
        holder.taDOChooserRecyclerItemItemDetailTextView.setText(itemsListData.getItemDetail());

        String duration = itemsListData.getDuration();
        String[] timeArray = duration.split(":");
        // done to drop first zero
        holder.taDOChooserRecyclerItemHoursTextView.setText(String.valueOf(Integer.parseInt(timeArray[0])));
        holder.taDOChooserRecyclerItemMinsTextView.setText(timeArray[1]);

        String itemPriorityState = itemsListData.getItemPriority();

        switch (itemPriorityState) {
            case "L":
                holder.taDOChooserRecyclerItemPriorityTextView.setBackgroundResource(R.drawable.priority_circle_low_40dp);
                break;
            case "M":
                holder.taDOChooserRecyclerItemPriorityTextView.setBackgroundResource(R.drawable.priority_circle_med_40dp);
                break;
            case "H":
                holder.taDOChooserRecyclerItemPriorityTextView.setBackgroundResource(R.drawable.priority_circle_high_40dp);
                break;
        }
    }



}
    
    
