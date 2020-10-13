package com.example.beachlist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class PendingFriendsListTab extends AppCompatActivity {
    RecyclerView recyclerView;
    List<FriendsData> list = new ArrayList<>();
    PendingFriendsRecyclerAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_friend);

        //******************************************Display Pending Friends List*******************************
        recyclerView = findViewById(R.id.pending_friends_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // This is temporary, added these to test the list would display
        String firstNames[] = getResources().getStringArray(R.array.first_names);
        int profilePics[] = {R.drawable.bulbasaur, R.drawable.charmander, R.drawable.froakie, R.drawable.golem, R.drawable.jigglypuff,
                R.drawable.pikachu, R.drawable.squirtle, R.drawable.sudowoodo, R.drawable.totodile, R.drawable.treeko};
        String lastNames[] = getResources().getStringArray(R.array.last_names);

        // clears list each time to make sure no duplicates are added
        list.clear();

        // adds the pending friends to be the list that will be displayed
        for(int i = 0; i < firstNames.length; i++){
            FriendsData pendingFriend = new FriendsData(profilePics[i],firstNames[i],lastNames[i]);
            list.add(pendingFriend);
        }

        // Links recycler view adapter
        adapter = new PendingFriendsRecyclerAdapter(this,list);
        recyclerView.setAdapter(adapter);

        //****************************************Back Button and Accept/Reject Buttons*************************************************
        Button backButton = findViewById(R.id.pending_btn_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAccountSettingsScreen();
            }
        });

        //***********************************************Tab Setup********************************************
        tabFunctionality();
        /*
        final TabLayout tabs = findViewById(R.id.friends_list_both_tabs);
        tabs.setScrollPosition(1,0f,true);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                openFriends();
            }
        });
        */
        // Cancel Button
        //Button accountPictureButton = findViewById(R.id.btn_account_picture_button);
        //accountPictureButton.setOnClickListener(new View.OnClickListener() {
        //   @Override
        //    public void onClick(View view) {
        //        ();
        //    }
        //});

    }

    // takes us back to account settings screen
    public void openAccountSettingsScreen(){
        Intent openScreen = new Intent(this, HomeScreenAfterLogin.class);
        openScreen.putExtra("screen",3);
        startActivity(openScreen);
    }

    // Sets what to do when each tab is selected / reselected from the pending friends list tab
    public void tabFunctionality(){
        final TabLayout tabs = findViewById(R.id.friends_list_both_tabs);
        tabs.setScrollPosition(1,0f,true);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                openPendingFriends();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                openFriends();
            }
        });
    }

    // opens friends list tab
    public void openFriends(){
        Intent openScreen = new Intent(this, FriendsListTab.class);
        startActivity(openScreen);
    }

    // opens pending friends list tab
    public void openPendingFriends(){
        Intent openScreen = new Intent(this, PendingFriendsListTab.class);
        startActivity(openScreen);
    }

}