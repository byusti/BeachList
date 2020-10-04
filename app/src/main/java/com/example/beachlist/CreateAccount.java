package com.example.beachlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class CreateAccount extends AppCompatActivity {
    private EditText fNameEt, lNameEt, idNumberEt, emailEt, passwordEt, gradDateEt, phoneNumEt;
    private Bitmap profileImage;
    private ImageView profilePicture;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    public static final int IMAGE_REQUEST = 33;
    public static final int PROCESSED_OK = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        //instance of authentication
        mAuth = FirebaseAuth.getInstance();

        //instance of the database
        database = FirebaseDatabase.getInstance();

        profilePicture = findViewById(R.id.ivProfileImage);

        // Set Profile Picture Button
        Button profilePicButton = findViewById(R.id.btn_Profile_image);
        profilePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Access the phones camera roll to let user pick their profile picture
                openCameraRoll();
            }
        });

        // Get input fields
        getUserInputs();
        progressDialog = new ProgressDialog(this);

        // Create Account Button
        Button createAccountButton = findViewById(R.id.btnCreateAccount);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Register the User
                Register();
            }
        });

        // Cancel Button
        Button cancelButton = findViewById(R.id.btnCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLoginScreen();
            }
        });
    }

    // Opens Camera Roll
    public void openCameraRoll(){
        Intent openCameraRoll = new Intent(Intent.ACTION_PICK);

        // Location of where to find the pictures
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();

        // URI of the path to pictures
        Uri data = Uri.parse(pictureDirectoryPath);

        // Set data and type (* means all image types)
        openCameraRoll.setDataAndType(data,"image/*");

        // start the activity (accessing camera roll)
        startActivityForResult(openCameraRoll, IMAGE_REQUEST);
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == PROCESSED_OK){
            // Accessed camera roll successfully
            if(requestCode == IMAGE_REQUEST){
                // Camera roll sent back a picture

                // Address of image in phone
                Uri imageUri = data.getData();

                // Stream to read image data
                InputStream input;

                try {
                    input = getContentResolver().openInputStream(imageUri);

                    // Get Bitmap from InputStream
                    profileImage = BitmapFactory.decodeStream(input);

                    // Displays image in the application
                    profilePicture.setImageBitmap(profileImage);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Unable to load image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void Register(){
        // Check if any field left blank
        if(anyEmptyFields()){
            displayEmptyFieldError();
            return;
        }

        // Check if user selected an image
//        if (profileImage == null) {
//            displayEmptyImageError();
//        }

        final String fname = fNameEt.getText().toString();
        final String lname = lNameEt.getText().toString();
        final String idNumber = idNumberEt.getText().toString();
        final String email = emailEt.getText().toString();
        final String gradDate = gradDateEt.getText().toString();
        final String phoneNum = phoneNumEt.getText().toString();

        String password = passwordEt.getText().toString();

        // Provided email cant be shorter than 19 characters since "@student.csulb.edu" is fixed at 18 characters
        if (email.length() <= 18) {
            displayMalformedEmailError();
            return;
        }
        // Checks if the email provided is a student csulb email
        else if (!isValidSchoolEmail(email)) {
            displayMalformedEmailError();
            return;
        }
        //Login progress dialog
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(CreateAccount.this, "Successfully registered", Toast.LENGTH_LONG).show();

                    //Set firebase user to current user instance
                    final FirebaseUser user = mAuth.getCurrentUser();
                    //Get special user ID
                    String userId = user.getUid();
                    //Initialize database within User reference with a child using user ID
                    databaseReference = database.getReference("users").child(userId);
                    //Create User reference with data
                    UserData currentUser = new UserData(userId, fname, lname, idNumber, email, gradDate, phoneNum, profileImage);
                    //Write data to database
                    databaseReference.setValue(currentUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                sendValidationEmail(user);
                                FirebaseAuth.getInstance().signOut();
                                openLoginScreen();
                            }
                            else {
                                Toast.makeText(CreateAccount.this, "Data storage failed!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(CreateAccount.this, "Sign up failed!",Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    // Opens Login Screen
    public void openLoginScreen(){
        Intent openScreen = new Intent(this, MainActivity.class);
        startActivity(openScreen);
        finish();
    }

    // Displays when any field is left blank
    public void displayEmptyFieldError(){
        Toast.makeText(CreateAccount.this, "Fill in all information",Toast.LENGTH_SHORT).show();
    }

    // Displays when an incorrect email is provided
    public void displayMalformedEmailError(){
        Toast.makeText(CreateAccount.this, "Must provide an email ending in @student.csulb.edu",Toast.LENGTH_SHORT).show();
    }

    public void displayEmptyImageError() {
        Toast.makeText(CreateAccount.this, "Please pick a picture for your account!", Toast.LENGTH_LONG).show();
    }

    // Get the information entered by the user
    public void getUserInputs(){
        fNameEt = findViewById(R.id.etFirstName);
        lNameEt = findViewById(R.id.etLastName);
        idNumberEt = findViewById(R.id.etIDNumber);
        emailEt = findViewById(R.id.etEmail);
        passwordEt = findViewById(R.id.etPassword);
        gradDateEt = findViewById(R.id.etGradDate);
        phoneNumEt = findViewById(R.id.etPhone);
    }

    // Checks if any text field is left empty
    public boolean anyEmptyFields() {
        return fNameEt.getText().toString().isEmpty()
                || lNameEt.getText().toString().isEmpty()
                || fNameEt.getText().toString().isEmpty()
                || idNumberEt.getText().toString().isEmpty()
                || emailEt.getText().toString().isEmpty()
                || passwordEt.getText().toString().isEmpty()
                || gradDateEt.getText().toString().isEmpty()
                || phoneNumEt.getText().toString().isEmpty();
    }

    public boolean isValidSchoolEmail(String email) {
        return email.substring(email.length() - 18).equals("@student.csulb.edu");
    }

    public void sendValidationEmail(final FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(CreateAccount.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CreateAccount.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}