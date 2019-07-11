package com.studyco.LinearTracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.studyco.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Piyush Prakhar on 19-09-2018.
 */
public class RecyclerAdapterLinearTracker extends RecyclerView.Adapter<RecyclerAdapterLinearTracker.ViewHolder> {
    private List<Integer> entryList;
    private List<String> subjectList;
    private List<Long> startDateList;
    private List<Long> endDateList;
    private List<Long> differenceList;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context context;

    RecyclerAdapterLinearTracker(Context context, List<Integer> entry, List<String> subjectList,
                                 List<Long> startDateList, List<Long> endDateList, List<Long> difference) {
        this.mInflater = LayoutInflater.from(context);
        this.entryList = entry;
        this.subjectList = subjectList;
        this.startDateList = startDateList;
        this.endDateList =endDateList;
        this.differenceList = difference;
        this.context = context;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView subjectName;
        TextView subjectEntry;
        TextView startDate;
        TextView endDate;
        LinearLayout fillLayout;
        RelativeLayout fillLayoutChild;
        ImageView button_delete;
        TextView elapsed;

        ViewHolder(View itemView) {
            super(itemView);
            elapsed = itemView.findViewById(R.id.row_elapsed_percentage);
            subjectName = itemView.findViewById(R.id.subject_row_textView);
            subjectEntry = itemView.findViewById(R.id.subject_row_number);
            startDate = itemView.findViewById(R.id.row_start_date);
            endDate = itemView.findViewById(R.id.row_end_date);
            button_delete =itemView.findViewById(R.id.row_linear_tracking_delete);
            fillLayout =itemView.findViewById(R.id.fill_layout);
            fillLayoutChild =itemView.findViewById(R.id.fill_layout_child);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            button_delete.setOnClickListener(this);

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
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyler_row_linear_tracker, parent, false);
        return new ViewHolder(view);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String header = subjectList.get(position);
        holder.subjectName.setText(header);

        Integer entry = entryList.get(position);
        holder.subjectEntry.setText(entry.toString());

        Long start = startDateList.get(position);
        DateFormat simple = new SimpleDateFormat("dd MMM");
        Date result = new Date(start);
        holder.startDate.setText(simple.format(result));

        Long end = endDateList.get(position);
        Date result_ = new Date(end);
        holder.endDate.setText(simple.format(result_));

        // calculate the percentage  -- > starting calculation

        Calendar calendar = Calendar.getInstance();
        long timeMilli = calendar.getTimeInMillis();

        long totalPassedTime = timeMilli - startDateList.get(position);
        long totalTime = differenceList.get(position);

        double percentLong;
        percentLong = ((double)totalPassedTime/(double)totalTime)*100 ;

        System.out.println(" Percent in long  : "  + percentLong);
        Integer percent = (int)Math.round(percentLong);



        System.out.println(" Percentage is  : "  + percent);

        // set the percentage elapsed

        DecimalFormat df = new DecimalFormat("#.##");

        holder.elapsed.setText(df.format(percentLong)+ "%  elapsed" );


        if(percent > 100 )
            percent = 100;
        if(percent <0)
            percent =1;


        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.weight = percent;
        holder.fillLayout.setLayoutParams(lp);

        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.ic_play_arrow_black_24dp);

        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            rlp.addRule(RelativeLayout.ALIGN_PARENT_END);
        }
        imageView.setLayoutParams(rlp);
        long remDays = ( totalTime - totalPassedTime ) / (24 * 60 * 60 * 1000);
        System.out.println( " Remaining days is : " + remDays);

        // set duration

        int duration;

        if (remDays <= 1 )
            duration =250;
        else if(remDays <= 2)
            duration = 500;
        else if(remDays <= 5)
            duration =750;
        else if (remDays <= 10)
            duration =1000;
        else
            duration =2000;


        // add animation
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(duration);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        imageView.startAnimation(anim);

        // add the ImageView to layout
        holder.fillLayoutChild.addView(imageView);


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