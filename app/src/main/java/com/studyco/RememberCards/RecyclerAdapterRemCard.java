package com.studyco.RememberCards;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.studyco.R;

import java.util.List;

/**
 * Created by Piyush Prakhar on 19-09-2018.
 */
public class RecyclerAdapterRemCard extends RecyclerView.Adapter<RecyclerAdapterRemCard.ViewHolder> {
    private List<String> cardNamesList;
    private List<String> cardPriorityList;
    private List<String> cardSubjectList;
    private List<Integer> cardEntryList;
    private List<String> cardAlertList;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Typeface lato;
    private Typeface lora;

    public RecyclerAdapterRemCard(Typeface lato , Typeface lora  ,Context context, List<String> cardNamesList, List<Integer> cardEntryList, List<String> cardPriorityList,
                           List<String> cardSubjectList, List<String> cardAlertList) {
        this.mInflater = LayoutInflater.from(context);
        this.cardNamesList = cardNamesList;
        this.cardEntryList = cardEntryList;
        this.cardPriorityList = cardPriorityList;
        this.cardSubjectList = cardSubjectList;
        this.cardAlertList= cardAlertList;
        this.lato = lato;
        this.lora =lora;

    }

    // stores and recycles views as they are scrolled off screen

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView cardNameText;
        TextView cardAlertText;
        TextView cardSubjectText;
        TextView cardSeriesText;
        RelativeLayout relativeLayout;
        ImageView cardNotifsImage;

        /*
        ImageButton button_proceed;
        ImageButton button_delete;
        */

        ViewHolder(View itemView) {
            super(itemView);
            cardNameText = itemView.findViewById(R.id.card_row_name_textView);
            cardAlertText = itemView.findViewById(R.id.card_row_alert_textView);
            cardSubjectText = itemView.findViewById(R.id.card_row_subject);
            cardSeriesText = itemView.findViewById(R.id.card_row_number);
            cardNotifsImage=itemView.findViewById(R.id.card_row_notifs);
            relativeLayout = itemView.findViewById(R.id.recycler_row_alert_recycler_layout);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            cardSubjectText.setTypeface(lato);
            cardNameText.setTypeface(lato);
            cardAlertText.setTypeface(lato);
            cardSeriesText.setTypeface(lato);


            /*
            button_proceed = itemView.findViewById(R.id.button_proceed);
            button_delete = itemView.findViewById(R.id.button_delete);
            button_delete.setOnClickListener(this);
            button_proceed.setOnClickListener(this);*/

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            if (mClickListener != null) {
                mClickListener.onLongItemClick(view, getAdapterPosition());
            }
            return true;
        }
    }
    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_row_remember_cards, parent, false);
        return new ViewHolder(view);
    }
    // binds the data to the TextView in each row
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position){

        String header = cardNamesList.get(position);
        holder.cardNameText.setText(header);

        String expiry = cardSubjectList.get(position);
        holder.cardSubjectText.setText(expiry);

        Integer series = cardEntryList.get(position);
        holder.cardSeriesText.setText(series.toString());

        String status = cardAlertList.get(position);
        if(status.equals("on")) {
            holder.cardNotifsImage.setImageResource(R.drawable.notifications_active);
            holder.cardAlertText.setText("Alert On");
        }
        else {
            holder.cardNotifsImage.setImageResource(R.drawable.notifs_off);
            holder.cardAlertText.setText("Alert Off");

        }
    }
    // total number of rows
    @Override
    public int getItemCount() {
        return cardSubjectList.size();
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return cardSubjectList.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
        void onLongItemClick(View v, int position);
    }
}