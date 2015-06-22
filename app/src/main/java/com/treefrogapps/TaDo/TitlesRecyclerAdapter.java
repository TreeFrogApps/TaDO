package com.treefrogapps.TaDo;


import android.content.Context;
import android.graphics.Color;
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

        private int circleState = 1;

        private TextView recyclerTitleIDTextView;

        private TextView recyclerTitleCircleTextView;
        private TextView recyclerTitleTextView;
        private TextView recyclerTitleTadoTextView;
        private TextView recyclerTitleItemsTextView;

        public MyViewHolder(View itemView) {
            super(itemView);

            recyclerTitleIDTextView = (TextView) itemView.findViewById(R.id.recyclerTitleIDTextView);

            recyclerTitleCircleTextView = (TextView) itemView.findViewById(R.id.recyclerTitleCircleTextView);

            recyclerTitleTextView = (TextView) itemView.findViewById(R.id.recyclerTitleTextView);
            recyclerTitleTadoTextView = (TextView) itemView.findViewById(R.id.recyclerTitleTadoTextView);
            recyclerTitleItemsTextView = (TextView) itemView.findViewById(R.id.recyclerTitleItemsTextView);
        }

        @Override
        public void onClick(View v) {


        }
    }


    @Override
    public void onBindViewHolder(final TitlesRecyclerAdapter.MyViewHolder holder, final int position) {

        holder.recyclerTitleIDTextView.setText(titlesListDataArrayList.get(position).getTitle_id());

        holder.recyclerTitleCircleTextView.setBackgroundResource(setTitleCircleColorResource
                (titlesListDataArrayList.get(position).getTitle().toUpperCase().substring(0, 1)));

        holder.recyclerTitleCircleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.recyclerTitleCircleTextView.startAnimation(Animations.scale1to0(context));

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (holder.circleState == 1) {
                            holder.recyclerTitleCircleTextView.setBackgroundResource(R.mipmap.ic_circle_reverse);
                            holder.recyclerTitleCircleTextView.setTextColor(Color.TRANSPARENT);
                            holder.circleState = 0;
                        } else {

                            holder.recyclerTitleCircleTextView.setBackgroundResource(setTitleCircleColorResource
                                    (titlesListDataArrayList.get(position).getTitle().toUpperCase().substring(0, 1)));
                            holder.recyclerTitleCircleTextView.setTextColor(context.getResources().getColor(R.color.white));
                            holder.circleState = 1;
                        }
                        holder.recyclerTitleCircleTextView.startAnimation(Animations.scale0to1(context));
                    }
                }, 100);
            }
        });

        holder.recyclerTitleCircleTextView.setText(titlesListDataArrayList.get(position).getTitle().toUpperCase().substring(0, 1));
        holder.recyclerTitleTextView.setText(titlesListDataArrayList.get(position).getTitle());

        DBHelper dbHelper = new DBHelper(context);
        // query database for counts of items - complete total and not done total
        ArrayList<ItemsListData> itemsListDataDone = dbHelper.getItemsForTitleDone(titlesListDataArrayList.get(position).getTitle());
        ArrayList<ItemsListData> itemsListDataNotDone = dbHelper.getItemsForTitleNotDone(titlesListDataArrayList.get(position).getTitle());
        itemsListDataDone.addAll(itemsListDataNotDone);

        holder.recyclerTitleItemsTextView.setText(String.valueOf(itemsListDataDone.size()));
        holder.recyclerTitleTadoTextView.setText(String.valueOf(itemsListDataNotDone.size()));

    }

    public int setTitleCircleColorResource(String listChar){

        switch(listChar){
            case "A": return R.drawable.circle_a_40dp;
            case "B": return R.drawable.circle_b_40dp;
            case "C": return R.drawable.circle_c_40dp;
            case "D": return R.drawable.circle_d_40dp;
            case "E": return R.drawable.circle_e_40dp;
            case "F": return R.drawable.circle_f_40dp;
            case "G": return R.drawable.circle_g_40dp;
            case "H": return R.drawable.circle_h_40dp;
            case "I": return R.drawable.circle_i_40dp;
            case "J": return R.drawable.circle_j_40dp;
            case "K": return R.drawable.circle_k_40dp;
            case "L": return R.drawable.circle_l_40dp;
            case "M": return R.drawable.circle_m_40dp;
            case "N": return R.drawable.circle_n_40dp;
            case "O": return R.drawable.circle_o_40dp;
            case "P": return R.drawable.circle_p_40dp;
            case "Q": return R.drawable.circle_q_40dp;
            case "R": return R.drawable.circle_r_40dp;
            case "S": return R.drawable.circle_s_40dp;
            case "T": return R.drawable.circle_t_40dp;
            case "U": return R.drawable.circle_u_40dp;
            case "V": return R.drawable.circle_v_40dp;
            case "W": return R.drawable.circle_w_40dp;
            case "X": return R.drawable.circle_x_40dp;
            case "Y": return R.drawable.circle_y_40dp;
            case "Z": return R.drawable.circle_z_40dp;
            default : return R.drawable.circle_other_40dp;
        }
    }

}
