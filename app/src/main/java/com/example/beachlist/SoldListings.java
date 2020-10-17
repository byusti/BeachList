package com.example.beachlist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SoldListings extends AppCompatActivity {
    Button backButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sold_items);



        //****************************************Back Button and Accept/Reject Buttons*************************************************
        backButton = findViewById(R.id.btn_back_from_sold);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAccountSettingsScreen();
            }
        });
    }

    // takes us back to account settings screen
    public void openAccountSettingsScreen(){
        Intent openScreen = new Intent(this, HomeScreenAfterLogin.class);
        openScreen.putExtra("screen",3);
        startActivity(openScreen);
    }
}