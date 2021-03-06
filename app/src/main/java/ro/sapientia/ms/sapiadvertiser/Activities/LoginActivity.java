package ro.sapientia.ms.sapiadvertiser.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import ro.sapientia.ms.sapiadvertiser.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mPhoneNr;
    private EditText mCode;
    private Button mGetCode;
    private Button mLogin;
    private String mVerificationCode;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPhoneNr = findViewById(R.id.editText_phonenr);
        mLogin = findViewById(R.id.button_login);
        mCode = findViewById(R.id.editText_code);
        mGetCode = findViewById(R.id.button_getCode);

        mLogin.setVisibility(View.INVISIBLE);
        mCode.setVisibility(View.INVISIBLE);

        mLogin.setOnClickListener(this);
        mGetCode.setOnClickListener(this);

        if(mAuth.getCurrentUser() != null)
        {
            mDatabase.child("users").child(mAuth.getCurrentUser().getPhoneNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        if(!dataSnapshot.child("FirstName").getValue().toString().isEmpty() &&
                                !dataSnapshot.child("LastName").getValue().toString().isEmpty() &&
                                !dataSnapshot.child("Email").getValue().toString().isEmpty() &&
                                !dataSnapshot.child("Address").getValue().toString().isEmpty() &&
                                !dataSnapshot.child("ProfileImage").getValue().toString().isEmpty())
                        {
                            Intent intent = new Intent(LoginActivity.this, NewsActivity.class);
                            intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                        else
                        {
                            Intent intent = new Intent(LoginActivity.this, NewsActivity.class);
                            intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }
                    else
                    {
                        newUser(mAuth.getCurrentUser().getPhoneNumber());
                        Intent intent = new Intent(LoginActivity.this, NewsActivity.class);
                        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_getCode:
                String phoneNr = mPhoneNr.getText().toString();

                if(phoneNr.isEmpty() || phoneNr.length()!=12)
                {
                    mPhoneNr.setError("Valid number is required");
                    mPhoneNr.requestFocus();
                    return;
                }
                SendVerificationCode(phoneNr);

                break;
            case R.id.button_login:
                String code = mCode.getText().toString();
                if(code.isEmpty() || code.length()<6)
                {
                    mCode.setError("Valid code is required");
                    mCode.requestFocus();
                    return;
                }
                VerifyCode(code);

                break;
        }
    }

    private void VerifyCode(String code)
    {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationCode,code);
        signIntWithCredential(credential);
    }

    private void signIntWithCredential(final PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            mDatabase.child("users").child(mPhoneNr.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Intent intent = new Intent(LoginActivity.this, NewsActivity.class);
                                        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        newUser(mPhoneNr.getText().toString());
                                        Intent intent = new Intent(LoginActivity.this, NewsActivity.class);
                                        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void SendVerificationCode(String phoneNr)
    {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNr,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks         // OnVerificationStateChangedCallbacks
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mLogin.setVisibility(View.VISIBLE);
            mCode.setVisibility(View.VISIBLE);
            mPhoneNr.setVisibility(View.INVISIBLE);
            mGetCode.setVisibility(View.INVISIBLE);
            mVerificationCode = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if(code != null)
            {
                mCode.setText(code);
                VerifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
            mPhoneNr.setText("");
        }
    };

    private void newUser(String userId)
    {
        mDatabase.child("users").child(userId).child("FirstName").setValue("");
        mDatabase.child("users").child(userId).child("LastName").setValue("");
        mDatabase.child("users").child(userId).child("Address").setValue("");
        mDatabase.child("users").child(userId).child("Email").setValue("");
        mDatabase.child("users").child(userId).child("ProfileImage").setValue("");
    }
}