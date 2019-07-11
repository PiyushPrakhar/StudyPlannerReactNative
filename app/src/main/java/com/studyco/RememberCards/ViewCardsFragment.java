package com.studyco.RememberCards;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.studyco.Assignments.AssignmentExpandedActivity;
import com.studyco.Databases.AssignmentDatabase;
import com.studyco.Databases.RememberCardDatabase;
import com.studyco.R;
import com.studyco.UserMonitor.InformServer;

import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class ViewCardsFragment extends Fragment implements RecyclerAdapterRemCard.ItemClickListener {


    RecyclerAdapterRemCard adapter;

    ArrayList<String> cardNamesList = new ArrayList<>();
    ArrayList<Integer> cardEntryList = new ArrayList<>();
    ArrayList<String> cardPriorityList = new ArrayList<>();
    ArrayList<String> cardSubjectList = new ArrayList<>();
    ArrayList<String> cardAlertsList = new ArrayList<>();

    InformServer informServer;
    String ActivityTag = "Remember Cards";

    Context c ;

    @SuppressLint("ValidFragment")
    public ViewCardsFragment(Context c){
        this.c = c;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_view_card, container, false);

        // fonts

        Typeface lato = Typeface.createFromAsset(getActivity().getAssets(), "lato_regular.ttf");

        Typeface lora = Typeface.createFromAsset(getActivity().getAssets(), "lora_regular.ttf");


        //  ' Inform server ' initialization

        informServer = new InformServer(c);

        //Database fetch

        RememberCardDatabase rememberCardDatabase = new RememberCardDatabase(c);
        Cursor cursor = rememberCardDatabase.allData();
        if(cursor.moveToFirst()){
            cursor.moveToFirst();

            for(int i =0;i<cursor.getCount();i++)
            {
                cardEntryList.add(i+1);
                cardNamesList.add(cursor.getString(0));
                cardSubjectList.add(cursor.getString(1));
                cardPriorityList.add(cursor.getString(2));
                cardAlertsList.add(cursor.getString(4));


                cursor.moveToNext();

            }
        }
        else
        {
            RelativeLayout relativeLayout = (RelativeLayout)v.findViewById(R.id.activity_remember_cards);

        }




        // Display the contents of the recycler view adapter on AssignmentActivity ( Recycler view )

        RecyclerView recyclerView = v.findViewById(R.id.recycler_remember_Cards);
        recyclerView.setLayoutManager(new LinearLayoutManager(c));
        adapter = new RecyclerAdapterRemCard(lato,lora, c, cardNamesList,cardEntryList,cardPriorityList,cardSubjectList,cardAlertsList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        return v ;
    }

    @Override
    public void onItemClick(View view, int position) {
       RememberCardDatabase rememberCardDatabase = new RememberCardDatabase(c);
        Cursor cursor = rememberCardDatabase.allData();
        if( cursor.moveToPosition(position))
        {
            cursor.moveToPosition(position);
            Intent intent = new Intent(getActivity() , RememberCardExpanded.class);

            String s1 = null ;
            String s2 = null ;
            String s3 = null ;
            String s4 = null ;
            String s5 = null ;

            s1 = cursor.getString(0);
            s2 = cursor.getString(1);
            s3 = cursor.getString(2);
            s4 = cursor.getString(3);
            s5 = cursor.getString(4);

            String strings[] = {s1 , s2 , s3 , s4 ,s5};

            intent.putExtra("strings",strings);

            // User monitor
            informServer.informServerMethod(ActivityTag,"Remember Card Viewed" + " -> "+"[ " +s1 + " ]");

            startActivity(intent);
        }

    }

    @Override
    public void onLongItemClick(View v, int position) {

    }
}