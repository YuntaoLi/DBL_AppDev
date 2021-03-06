package group14.foodfetch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    
    /*Widgets*/

    private User newUser;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonRegisterDonor;
    private Button buttonRegisterFB;
    private TextView textViewLogin;
    private ProgressDialog pDialog;
    /*FireBase utilities*/
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener aListener;
    private DatabaseReference databaseReference;


    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        /*Initialise a firebase instance*/
        firebaseAuth = FirebaseAuth.getInstance();

        /*Link to the corresponding inputs*/
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonRegisterDonor = (Button) findViewById(R.id.buttonRegisterDonor);
        buttonRegisterFB = (Button) findViewById(R.id.buttonRegisterFB);
        textViewLogin = (TextView) findViewById(R.id.textViewLogin);

        pDialog = new ProgressDialog(this);
        
        /*set up onclick listener*/
        buttonRegisterDonor.setOnClickListener(this);
        buttonRegisterFB.setOnClickListener(this);
        textViewLogin.setOnClickListener(this);
        /*Get data structure*/
        databaseReference = FirebaseDatabase.getInstance().getReference();

        /*Sigin status listener, check if the user has already logged in*/
        aListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                if(firebaseAuth.getCurrentUser() != null){
//                    finish();
//                    FirebaseDatabase database = FirebaseDatabase.getInstance();
//                    FirebaseUser usr = firebaseAuth.getCurrentUser();
//                    DatabaseReference databaseReference =
//                            database.getReference("/" + usr.getUid());
//                    databaseReference.child("doner").addValueEventListener(new ValueEventListener(){
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            String type = dataSnapshot.getValue().toString();
//                            if(type.toLowerCase().equals("true")) {
//                                startActivity(new Intent(getApplicationContext(),
//                                        PostActivity.class));
//                            }
//                            else{
//                                startActivity(new Intent(getApplicationContext(),
//                                        MyPostsActivity.class));
//                            }
//                        }
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//                }
//            }
        };
    }

    /*Register as a donor*/
    private void registerDonor(){

        newUser = new Donor();
        /*User credentials */
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        
        /*If the email is valid*/
        if(!emailChecker(email)){
            Toast.makeText(this, "Please input a valid email",
                    Toast.LENGTH_SHORT).show();
            return;//halt
        }
        /*If the pw is valid*/
        if(!pwChecker(password)){
            Toast.makeText(this, "Please fill your password " +
                            "and make sure there are 6 characters at least",
                    Toast.LENGTH_LONG).show();
            return;//halt
        }
        
        /*Processing by showing the progress*/
        pDialog.setMessage("Processing, please wait for a moment");
        pDialog.show();
        /*create the user instance*/
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                pDialog.dismiss();
                if(task.isSuccessful()){
                    finish();

                    FirebaseUser usr = firebaseAuth.getCurrentUser();
                    databaseReference.child(usr.getUid()).setValue(newUser);
                    // goto Corresponding page
                    startActivity(new Intent(getApplicationContext(), PostActivity.class));


                }
                else{
                    Toast.makeText(RegisterActivity.this, "Failed, Try again.", Toast.LENGTH_LONG)
                            .show();
                }



            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        firebaseAuth.addAuthStateListener(aListener);
    }

    /*Register as a food bank*/
    private void registerFB(){
        newUser = new FoodBank();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        /*If the email is valid*/
        if(!emailChecker(email)){
            Toast.makeText(this, "Please input a valid email",
                    Toast.LENGTH_LONG).show();
            return;//halt
        }
        /*If the pw is valid*/
        if(!pwChecker(password)){
            Toast.makeText(this, "Please fill your password " +
                            "and make sure there are 6 characters at least",
                    Toast.LENGTH_LONG).show();
            return;//halt
        }

        /*Processing by showing the progress*/
        pDialog.setMessage("Processing, please wait for a moment");
        pDialog.show();

        /*create the user instance*/
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pDialog.dismiss();
                        if(task.isSuccessful()){
                            finish();
                            FirebaseUser usr = firebaseAuth.getCurrentUser();
                            databaseReference.child(usr.getUid()).setValue(newUser);
                            //goto Corresponding page
                            startActivity(new Intent(getApplicationContext(),
                                    AnnouncementActivity.class));//replace to postoverview

                        }
                        else{
                            Toast.makeText(RegisterActivity.this, "Failed, Try again.",
                                    Toast.LENGTH_LONG).show();

                        }

                    }
                });
    }

    /*Check if the email field is empty and if the format is correct*/
    public boolean emailChecker(String email){
        return !(TextUtils.isEmpty(email) ||
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    /*Check if the password field is empty and if it is at 6 characters*/
    public boolean pwChecker(String password){
        return !(TextUtils.isEmpty(password) || password.length() < 6);
    }

    @Override
    public void onClick(View v) {
        if (v == buttonRegisterDonor) {
            registerDonor();
        }
        if (v == buttonRegisterFB) {
            registerFB();
        }
        if (v == textViewLogin) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
