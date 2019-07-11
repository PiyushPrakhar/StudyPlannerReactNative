package com.studyco.Subjects;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.studyco.R;

import java.util.List;

/**
 * Created by Piyush Prakhar on 19-09-2018.
 */
public class RecyclerAdapterSubject extends RecyclerView.Adapter<RecyclerAdapterSubject.ViewHolder> {
    private List<String> subjectList;
    private List<Integer> entryList;
    private List<String> creditsList;
    private List<Integer> difficultyList;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    RecyclerAdapterSubject(Context context, List<String> data, List<Integer> entry, List<String> credits,List<Integer> difficulty) {
        this.mInflater = LayoutInflater.from(context);
        this.subjectList = data;
        this.entryList = entry;
        this.creditsList = credits;
        this.difficultyList = difficulty;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView subjectName;
        TextView subjectEntry;
        LinearLayout linearLayout25;
        LinearLayout linearLayout50;
        LinearLayout linearLayout75;
        LinearLayout linearLayout100;
        TextView credits;
        TextView difficulty;


        ViewHolder(View itemView) {
            super(itemView);
            subjectName = itemView.findViewById(R.id.subject_row_textView);
            subjectEntry = itemView.findViewById(R.id.subject_row_number);
            linearLayout25 = itemView.findViewById(R.id.subject_row_progress_25);
            linearLayout50 = itemView.findViewById(R.id.subject_row_progress_50);
            linearLayout75 = itemView.findViewById(R.id.subject_row_progress_75);
            linearLayout100 = itemView.findViewById(R.id.subject_row_progress_100);
            credits = itemView.findViewById(R.id.subject_row_credits);
            difficulty = itemView.findViewById(R.id.subject_row_difficulty);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

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
        View view = mInflater.inflate(R.layout.recycler_row_subject, parent, false);
        return new ViewHolder(view);
    }
    // binds the data to the TextView in each row
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String header = subjectList.get(position);
        holder.subjectName.setText(header);

        Integer entry = entryList.get(position);
        holder.subjectEntry.setText(entry.toString());

        String creds = creditsList.get(position);
        holder.credits.setText("Credits : " + creds);

        Integer difficulty = difficultyList.get(position);
        if (difficulty == 1) {
            holder.linearLayout25.setBackgroundColor(Color.parseColor("#ff99cc00"));
            holder.difficulty.setText("Difficulty : Easy");
        }
        if (difficulty == 2) {
            holder.linearLayout25.setBackgroundColor(Color.parseColor("#ff99cc00"));
            holder.linearLayout50.setBackgroundColor(Color.parseColor("#ff99cc00"));
            holder.difficulty.setText("Difficulty : Moderate");

        }
        if (difficulty == 3) {
            holder.linearLayout25.setBackgroundColor(Color.parseColor("#ff99cc00"));
            holder.linearLayout50.setBackgroundColor(Color.parseColor("#ff99cc00"));
            holder.linearLayout75.setBackgroundColor(Color.parseColor("#ff99cc00"));
            holder.difficulty.setText("Difficulty : Challenging");
        }
        if (difficulty == 4) {
            holder.linearLayout25.setBackgroundColor(Color.parseColor("#ff99cc00"));
            holder.linearLayout50.setBackgroundColor(Color.parseColor("#ff99cc00"));
            holder.linearLayout75.setBackgroundColor(Color.parseColor("#ff99cc00"));
            holder.linearLayout100.setBackgroundColor(Color.parseColor("#ff99cc00"));
            holder.difficulty.setText("Difficulty : Hard");
        }

    }
    // total number of rows
    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return subjectList.get(id);
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