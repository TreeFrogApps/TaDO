package com.treefrogapps.TaDo;


import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class TitlesRecyclerAdapter extends RecyclerView.Adapter<TitlesRecyclerAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<TitlesListData> titlesListDataArrayList;

    private int circleState = 1;
    private DBHelper dbHelper = new DBHelper(context);

    public TitlesRecyclerAdapter(Context context, ArrayList<TitlesListData> titlesListDataArrayList) {

        this.context = context;
        this.titlesListDataArrayList = titlesListDataArrayList;
    }


    @Override
    public int getItemCount() {
        return this.titlesListDataArrayList.size();
    }

    @Override
    public TitlesRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext().getApplicationContext()).inflate(R.layout.recycler_title_view, parent, false);
        return new MyViewHolder(itemView);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView recyclerTitleIDTextView;

        private TextView recyclerTitleCircleTextView;
        private TextView recyclerTitleTextView;
        private TextView recyclerTitleTadoTextView;
        private TextView recyclerTitleItemsTextView;

        public MyViewHolder(View itemView) {
            super(itemView);

            recyclerTitleIDTextView = (TextView) itemView.findViewById(R.id.recyclerTitleIDTextView);

            recyclerTitleCircleTextView = (TextView) itemView.findViewById(R.id.recyclerTitleCircleTextView);
            recyclerTitleCircleTextView.setOnClickListener(this);

            recyclerTitleTextView = (TextView) itemView.findViewById(R.id.recyclerTitleTextView);
            recyclerTitleTadoTextView = (TextView) itemView.findViewById(R.id.recyclerTitleTadoTextView);
            recyclerTitleItemsTextView = (TextView) itemView.findViewById(R.id.recyclerTitleItemsTextView);
        }

        @Override
        public void onClick(View v) {

            if (v.getId() == recyclerTitleCircleTextView.getId()) {

                recyclerTitleCircleTextView.startAnimation(Animations.scale1to0(itemView.getContext()));

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (circleState == 1) {
                            recyclerTitleCircleTextView.setBackgroundResource(R.mipmap.ic_circle_reverse);
                            circleState = 0;
                        } else {
                            // TODO - set correct color for letter
                            recyclerTitleCircleTextView.setBackgroundColor(itemView.getResources().getColor(R.color.circle_other));
                            circleState = 1;
                        }
                        recyclerTitleCircleTextView.startAnimation(Animations.scale0to1(itemView.getContext()));
                    }
                }, 200);
            }
        }
    }


    @Override
    public void onBindViewHolder(TitlesRecyclerAdapter.MyViewHolder holder, int position) {

        holder.recyclerTitleIDTextView.setText(titlesListDataArrayList.get(position).getTitle_id());

        holder.recyclerTitleCircleTextView.setText(titlesListDataArrayList.get(position).getTitle().toUpperCase().substring(0, 1));
        holder.recyclerTitleTextView.setText(titlesListDataArrayList.get(position).getTitle());

        // query database for counts of items - complete total and not done total
        ArrayList<ItemsListData> itemsListDataDone = dbHelper.getItemsForTitleDone(titlesListDataArrayList.get(position).getTitle());
        ArrayList<ItemsListData> itemsListDataNotDone = dbHelper.getItemsForTitleNotDone(titlesListDataArrayList.get(position).getTitle());
        itemsListDataDone.addAll(itemsListDataNotDone);

        holder.recyclerTitleItemsTextView.setText(String.valueOf(itemsListDataDone.size()));
        holder.recyclerTitleTadoTextView.setText(String.valueOf(itemsListDataNotDone.size()));

    }

}
