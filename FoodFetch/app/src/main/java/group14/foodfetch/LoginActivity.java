package group14.foodfetch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    /*Widgets*/
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;
    /*FireBase utilities*/
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener aListener;
    private Handler handler = new Handler();
    private TaskTimer taskTimer;
    private ProgressDialog pDialog;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*Initialise a firebase instance*/
        firebaseAuth = FirebaseAuth.getInstance();

        /*Link to the corresponding inputs*/
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        textViewRegister = (TextView) findViewById(R.id.textViewRegister);

        pDialog = new ProgressDialog(this);

        /*set up onclick listener*/
        buttonLogin.setOnClickListener(this);
        textViewRegister.setOnClickListener(this);

        /*Sigin status listener, check if the user has already logged in*/
        aListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
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
//                                /*if a donor, go to donor activity*/
//                                startActivity(new Intent(getApplicationContext(),
//                                        PostActivity.class));
//                            }
//                            else{
//                                /*if a fb, go to fb activity*/
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
            }
        };
    }

    @Override
    protected void onStart(){
        super.onStart();
        firebaseAuth.addAuthStateListener(aListener);


    }
    
    /*Login function*/
    private void login(){
//        /*User credentials */
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            finish();
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            FirebaseUser usr = firebaseAuth.getCurrentUser();
                            DatabaseReference databaseReference =
                                    database.getReference("/" + usr.getUid());
                            databaseReference.child("doner").addValueEventListener(new ValueEventListener(){
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String type = dataSnapshot.getValue().toString();
                                    if(type.toLowerCase().equals("true")) {
                                        startActivity(new Intent(getApplicationContext(),
                                                PostActivity.class));
                                    }
                                    else{
                                        startActivity(new Intent(getApplicationContext(),
                                                MyPostsActivity.class));
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Failed, Try again.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });}

    /*Check if the email field is empty and if the format is correct*/
    public boolean emailChecker(String email){
        return !(TextUtils.isEmpty(email) ||
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }



//    public class FetchedUser
//    {
//        public boolean doner;
//
//        public FetchedUser(){}
//
//        public FetchedUser(boolean doner) {
//            this.doner = doner;
//        }
//
//        public void setDoner(boolean doner) {
//
//        }
//
//        @Override
//        public String toString() {
//            return "user is donor: " + doner;
//        }
//    }

    /*Check if the password field is empty and if it is at 6 characters*/
    public boolean pwChecker(String password){
        return !(TextUtils.isEmpty(password) || password.length() < 6);
    }

    @Override
    public void onClick(View v) {
        if(v == buttonLogin){
            /*User credentials */
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
                Toast.makeText(this, "Please fill your password and make sure there are " +
                                "6 characters at least",
                        Toast.LENGTH_LONG).show();
                return;//halt
            }
            //Now, open a new task and new delayer, holding on the session up to 60s.
            //After 60s, halt the process and return an err msg
            LoginTask loginTask = new LoginTask(LoginActivity.this,pDialog);
            taskTimer = new TaskTimer(loginTask);
            handler.postDelayed(taskTimer, 1*1000);
            loginTask.execute();
        }
        if(v == textViewRegister){
            /*Close this page*/
            finish();
            /*Goto register page*/
            startActivity(new Intent(this, RegisterActivity.class));
        }
    }

    public class LoginTask extends AsyncTask<Void,Void,Boolean>{
        private ProgressDialog pDialog;
        private AppCompatActivity activity;

        public LoginTask(AppCompatActivity activity, ProgressDialog pDialog) {
            this.activity = activity;
            this.pDialog = pDialog;
        }

        @Override
        protected void onPreExecute() {
            pDialog.setMessage("Processing, please wait for a moment");
            pDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try{
                login();
                return true;
            }catch (Exception e){return false;}
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);
            if(pDialog.isShowing()){
                pDialog.dismiss();
            }
            if (success) {
//                Toast.makeText(activity, "You are logged in", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "Error, please try it again", Toast.LENGTH_LONG).show();
            }
        }
    }
}
