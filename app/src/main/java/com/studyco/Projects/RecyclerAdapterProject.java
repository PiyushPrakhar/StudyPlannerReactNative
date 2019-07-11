package com.studyco.Projects;

import android.annotation.SuppressLint;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.studyco.R;

import java.util.List;

/**
 * Created by Piyush Prakhar on 19-09-2018.
 */
public class RecyclerAdapterProject extends RecyclerView.Adapter<RecyclerAdapterProject.ViewHolder> {
    private List<String> projectList;
    private List<Integer> entryList;
    private List<String> expiryList;
    private List<String> statusList;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    RecyclerAdapterProject(Context context, List<String> data, List<Integer> entry, List<String> expiry, List<String> statusList) {
        this.mInflater = LayoutInflater.from(context);
        this.projectList = data;
        this.entryList = entry;
        this.expiryList = expiry;
        this.statusList = statusList;
    }

    // stores and recycles views as they are scrolled off screen

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView projectName;
        TextView projectEntry;
        TextView projectExpiry;
        TextView projectCompletedText;
        ImageView projectCompletedImage;
        RelativeLayout relativeLayout;


        ViewHolder(View itemView) {
            super(itemView);
            projectName = itemView.findViewById(R.id.project_row_textView);
            projectEntry = itemView.findViewById(R.id.project_row_number);
            projectExpiry = itemView.findViewById(R.id.project_row_expiry);
            relativeLayout = itemView.findViewById(R.id.recycler_row_completed_layout);
            projectCompletedImage = itemView.findViewById(R.id.recycler_project_completed_image);
            projectCompletedText = itemView.findViewById(R.id.project_row_completed_text);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
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
        View view = mInflater.inflate(R.layout.recycler_row_project, parent, false);
        return new ViewHolder(view);
    }
    // binds the data to the TextView in each row
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        String header = projectList.get(position);
        holder.projectName.setText(header);

        Integer entry = entryList.get(position);
        holder.projectEntry.setText(entry.toString());

        String expiry = expiryList.get(position);
        holder.projectExpiry.setText(expiry);

        String status = statusList.get(position);
        if(status.equals("Completed")) {
            holder.projectCompletedImage.setImageResource(R.drawable.outline_check_circle_24);
            holder.projectCompletedText.setText("Completed");
        }
        else
        {
            holder.projectCompletedImage.setImageResource(R.drawable.incomplete);
            holder.projectCompletedText.setText("Incomplete");
        }



    }
    // total number of rows
    @Override
    public int getItemCount() {
        return projectList.size();
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return projectList.get(id);
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