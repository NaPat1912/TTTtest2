package com.tisconet.ttttest;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.net.HttpURLConnection;
import java.net.URL;

public class SendActivity extends AppCompatActivity {

    private EditText InputNum;
    private Button btnEnter, btnlogout;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private String TAG = SendActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //Add to Activity
//        FirebaseMessaging.getInstance().unsubscribeFromTopic("pushNotifications");

        InputNum = (EditText) findViewById(R.id.edtInput);
        btnEnter = (Button) findViewById(R.id.btn_enter);
        btnlogout = (Button) findViewById(R.id.btn_signup);

        FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount = InputNum.getText().toString();

                if (TextUtils.isEmpty(amount)) {
                    Toast.makeText(getApplicationContext(), "Enter your amount", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    getToken();
                    Toast.makeText(SendActivity.this, "Subscribed to Topic: Push Notifications", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });


        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                startActivity(new Intent(SendActivity.this, LoginActivity.class));
            }
        });
    }

    private void getToken() {

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUser.getToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            Log.d(TAG, idToken);
                            // Send token to your backend via HTTPS
                            makeHttpConnection(idToken);
                            // ...
                        } else {
                            // Handle error -> task.getException();
                            Log.d(TAG, task.getException().toString());
                        }
                    }
                });
//        String username = auth.getCurrentUser().getEmail().replace("@tisco.co.th","");
//
//        mDatabase.child("users").child(username).child("token").addListenerForSingleValueEvent(
//                new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        // I commented the code here to check whether its coming from this part but it continued to occur even after commenting.
//                        Log.d(TAG,"getToken:" + dataSnapshot.toString());
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
//                    }
//                });

    }

    private void makeHttpConnection(String token) {
        new MyDownloadTask().execute(token);
    }

    class MyDownloadTask extends AsyncTask<String, Void, Void> {


        protected void onPreExecute() {
            //display progress dialog.

        }

        protected Void doInBackground(String... params) {
            String token = params[0];
            Log.d(TAG, token);
            URL url;
            HttpURLConnection urlConnection = null;
            try {
//            ttttest-a3bd3
                url = new URL("https://us-central1-ttttest-a3bd3.cloudfunctions.net/pushNotification");

                urlConnection = (HttpURLConnection) url
                        .openConnection();
                urlConnection.setRequestProperty("Authorization", token);
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, String.valueOf(responseCode));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            // dismiss progress dialog and update ui
            Log.d(TAG, "onPostExecute");
        }
    }
}
