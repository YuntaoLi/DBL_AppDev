package group14.foodfetch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    /*Widgets*/
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;
    private ProgressDialog pDialog;
    /*FireBase utilities*/
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener aListener;

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
                if(firebaseAuth.getCurrentUser() != null){
                    finish();
                    startActivity(new Intent(getApplicationContext(), PostActivity.class));
                }
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

        /*Processing by showing the progress*/
        pDialog.setMessage("Processing, please wait for a moment");
        pDialog.show();

        /*Login via auth*/
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pDialog.dismiss();
                        if(task.isSuccessful()){
                            finish();
                            startActivity(new Intent(getApplicationContext(),
                                    PostActivity.class));
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Failed, Try again.",
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
        if(v == buttonLogin){
            login();
        }
        if(v == textViewRegister){
            /*Close this page*/
            finish();
            /*Goto register page*/
            startActivity(new Intent(this, RegisterActivity.class));
        }
    }
}
