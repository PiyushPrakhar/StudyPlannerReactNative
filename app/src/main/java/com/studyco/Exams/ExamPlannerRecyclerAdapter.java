package com.studyco.Exams;


import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.studyco.Databases.ExamScheduleDatabase;
import com.studyco.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Piyush Prakhar on 19-09-2018.
 */
public class ExamPlannerRecyclerAdapter extends RecyclerView.Adapter<ExamPlannerRecyclerAdapter.ViewHolder>
        implements ItemTouchHelperAdapter {
    private ArrayList<String> subjectDateList;
    private ArrayList<String> subjectNameListOne;
    private ArrayList<String> subjectNameListTwo;
    private ArrayList<Integer> subjectDiffListOne;
    private ArrayList<Integer> subjectDiffListTwo;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context context;

    @Override
    public void onItemMove(int fromPosition, int toPosition) {

        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(subjectNameListOne, i, i + 1);
                Collections.swap(subjectNameListTwo, i, i + 1);
                Collections.swap(subjectDiffListOne,i,i+1);
                Collections.swap(subjectDiffListTwo,i,i+1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(subjectNameListOne, i, i - 1);
                Collections.swap(subjectNameListTwo, i, i - 1);
                Collections.swap(subjectDiffListOne, i, i - 1);
                Collections.swap(subjectDiffListTwo, i, i - 1);
            }
        }

        for(String s : subjectNameListOne)
            System.out.println(s);

        for(String s :subjectDateList)
            System.out.println(s);

        notifyItemMoved(fromPosition, toPosition);

        //  save new data

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    saveScheduleState(subjectNameListOne,subjectNameListTwo,subjectDiffListOne,subjectDiffListTwo,subjectDateList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();



        return ;

    }

    @Override
    public void onItemDismiss(int position) {
        subjectNameListOne.remove(position);
        notifyItemRemoved(position);

    }

    @Override
    public void onItemDropped() {
        notifyDataSetChanged();
    }


    ExamPlannerRecyclerAdapter(Context context,
                               ArrayList<String> subjectNameListOne,
                               ArrayList<String> subjectNameListTwo,
                               ArrayList<Integer> subjectDiffListOne,
                               ArrayList<Integer> subjectDiffListTwo,
                               ArrayList<String> subjectDateList  ) {
        this.mInflater = LayoutInflater.from(context);
        this.subjectNameListOne = subjectNameListOne;
        this.subjectNameListTwo = subjectNameListTwo;
        this.subjectDiffListOne=subjectDiffListOne;
        this.subjectDiffListTwo=subjectDiffListTwo;
        this.subjectDateList = subjectDateList;
        this.context = context;

    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView subjectDate;
        TextView subjectDescriptionOne;
        TextView subjectDescriptionTwo;
        TextView subjectDiffOne;
        TextView subjectDiffTwo;
        ImageView imageRatingOne;
        ImageView imageRatingTwo;

        ViewHolder(View itemView) {
                        super(itemView);
            subjectDate = itemView.findViewById(R.id.calendar_date_text);
            subjectDescriptionOne = itemView.findViewById(R.id.calendar_details_text_one);
            subjectDescriptionTwo = itemView.findViewById(R.id.calendar_details_text_two);
            subjectDiffOne = itemView.findViewById(R.id.ratingOne);
            subjectDiffTwo =itemView.findViewById(R.id.ratingTwo);
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
        View view = mInflater.inflate(R.layout.recycler_row_exam_calendar, parent, false);
        return new ViewHolder(view);
    }
    // binds the data to the TextView in each row

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position){

        String subjectNameOne = subjectNameListOne.get(position);
        if(subjectNameOne.length()>=3)
            holder.subjectDescriptionOne.setText(subjectNameOne.substring(0,3));
        else
            holder.subjectDescriptionOne.setText(subjectNameOne);



        try{
            String subjectNameTwo = subjectNameListTwo.get(position);
            if(subjectNameTwo.length()>=3)
                holder.subjectDescriptionTwo.setText(subjectNameTwo.substring(0,3));
            else
                holder.subjectDescriptionTwo.setText(subjectNameTwo);

        }
        catch(Exception e)
        {
            System.out.println(" One day schedule ");
        }

        Integer subDiffOne =subjectDiffListOne.get(position);
        holder.subjectDiffOne.setText(subDiffOne.toString());

        Integer subDiffTwo =subjectDiffListTwo.get(position);
        holder.subjectDiffTwo.setText(subDiffTwo.toString());

        String subjectDescription = subjectDateList.get(position);
        holder.subjectDate.setText(subjectDescription);

    }
    // total number of rows
    @Override
    public int getItemCount() {
        return subjectNameListOne.size();
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return subjectDateList.get(id);
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

    public void saveScheduleState(ArrayList<String> listOne , ArrayList<String> listTwo ,
                                  ArrayList<Integer> listDiffOne ,ArrayList<Integer> listDiffTwo, ArrayList<String> listDate)
    {
        ExamScheduleDatabase db = new ExamScheduleDatabase(context);
        Cursor c = db.allData();

        if(c.moveToFirst())
            db.deleteAll();

        if(listOne !=null)
        {
            for(int i = 0; i < listOne.size(); i++)
                db.insertData(listOne.get(i),listTwo.get(i),listDiffOne.get(i),listDiffTwo.get(i),listDate.get(i));
            Toast.makeText(context,"Saved",Toast.LENGTH_SHORT).show();

        }
    }

}
