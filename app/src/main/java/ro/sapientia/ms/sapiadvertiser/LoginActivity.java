package ro.sapientia.ms.sapiadvertiser;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mPhoneNr;
    private EditText mCode;
    private Button mGetCode;
    private Button mLogin;
    private String mVerificationCode;
    private Spinner mCountryCode;

    private FirebaseAuth mAuth;
    private PhonePrefix prefix = new PhonePrefix();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPhoneNr = findViewById(R.id.editText_phonenr);
        mLogin = findViewById(R.id.button_login);
        mCode = findViewById(R.id.editText_code);
        mGetCode = findViewById(R.id.button_getCode);
        mCountryCode = findViewById(R.id.spinner_counterycode);

        mLogin.setVisibility(View.INVISIBLE);
        mCode.setVisibility(View.INVISIBLE);

        mLogin.setOnClickListener(this);
        mGetCode.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        //Intent intent = new Intent(LoginActivity.this,LoginActivity.class);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,prefix.getList());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCountryCode.setAdapter(spinnerAdapter);
        mCountryCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //String countryPrefix = prefix.prefixFor(adapterView.getItemAtPosition(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_getCode:
                String phoneNr = mPhoneNr.getText().toString();

                String countryPrefix = prefix.prefixFor(mCountryCode.getSelectedItem().toString());
                String finalPhoneNr = countryPrefix + phoneNr;

                if(finalPhoneNr.isEmpty() || finalPhoneNr.length()<10)
                {
                    mPhoneNr.setError("Valid number is required");
                    mPhoneNr.requestFocus();
                    return;
                }
                SendVerificationCode(finalPhoneNr);

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

    private void signIntWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Intent intent = new Intent(LoginActivity.this,NewsActivity.class);
                            //ez allati sor akkor kell ha nem akarsz vissza menni a loginre, hanem a visszaval egybol az appot bezarja
                            // intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
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
}