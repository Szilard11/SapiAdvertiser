package ro.sapientia.ms.sapiadvertiser;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.style.UpdateLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class PersonFragment extends Fragment {

    private CircleImageView mProfileImage;
    private Button mLogOff_Button;
    private Button mMyAdvs_Button;
    private Button mEdit_Button;
    private Button mSave_Button;
    private EditText mLName;
    private EditText mFName;
    private EditText mEmail;
    private EditText mPhoneNr;
    private EditText mAddress;

    private String m_oldFName;
    private String m_oldLName;
    private String m_oldPhoneNr;
    private String m_oldEmail;

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String mUserNr = mAuth.getCurrentUser().getPhoneNumber();

    private View mInflatedView;

    public PersonFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.mInflatedView = inflater.inflate(R.layout.fragment_person, container, false);

        mProfileImage = mInflatedView.findViewById(R.id.profile_image);
        mMyAdvs_Button = mInflatedView.findViewById(R.id.advsButt);
        mLogOff_Button = mInflatedView.findViewById(R.id.logoffButt);
        mSave_Button = mInflatedView.findViewById(R.id.button_Save);
        mEdit_Button = mInflatedView.findViewById(R.id.button_Edit);
        mSave_Button.setClickable(false);
        mFName = mInflatedView.findViewById(R.id.editText_FName);
        mLName = mInflatedView.findViewById(R.id.editText_LName);
        mEmail = mInflatedView.findViewById(R.id.editText_Email);
        mAddress = mInflatedView.findViewById(R.id.editText_Address);
        mPhoneNr = mInflatedView.findViewById(R.id.editText_PhoneNr);

        mDatabase.child("users").child(mUserNr).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                mLName.setText(dataSnapshot.child("LastName").getValue().toString());
                mFName.setText(dataSnapshot.child("FirstName").getValue().toString());
                mAddress.setText(dataSnapshot.child("Address").getValue().toString());
                mEmail.setText(dataSnapshot.child("Email").getValue().toString());
                mPhoneNr.setText(mUserNr);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mLName.setText(dataSnapshot.child("LastName").getValue().toString());
                mFName.setText(dataSnapshot.child("FirstName").getValue().toString());
                mAddress.setText(dataSnapshot.child("Address").getValue().toString());
                mEmail.setText(dataSnapshot.child("Email").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Failed to read data
            }
        });*/

        mMyAdvs_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),AdvDetailActivity.class);
                startActivity(intent);
            }
        });
        mLogOff_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
            }
        });
        mEdit_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_oldFName = mFName.getText().toString();
                m_oldLName = mLName.getText().toString();
                m_oldPhoneNr = mPhoneNr.getText().toString();
                m_oldEmail = mEmail.getText().toString();
                mEdit_Button.setClickable(false);
                mSave_Button.setClickable(true);
                mFName.setEnabled(true);
                mLName.setEnabled(true);
                mPhoneNr.setEnabled(true);
                mAddress.setEnabled(true);
                mEmail.setEnabled(true);
                mLogOff_Button.setClickable(false);
                mMyAdvs_Button.setClickable(false);
            }
        });
        mSave_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO::Database update? Validate entered texts? Edit kozben nincs mashva menes vagy regi marad?
                /*Boolean fName = false;
                Boolean lName = false;
                Boolean email = false;
                Boolean phoneNr = false;*/
                String newFName = mFName.getText().toString();
                String newLName = mLName.getText().toString();
                String newEmail = mEmail.getText().toString();
                String newPhoneNr = mPhoneNr.getText().toString();

                String regexStr = "^[+][0-9]{10,13}$";
                if(newPhoneNr.matches(regexStr))
                {
                    if (m_oldFName != newFName) {
                        mDatabase.child("users").child(mUserNr).child("FirstName").setValue(newFName);
                        //fName = true;
                    }
                    if (m_oldLName != newLName) {
                        mDatabase.child("users").child(mUserNr).child("LastName").setValue(newLName);
                        //lName = true;
                    }
                    if (m_oldEmail != newEmail) {
                        mDatabase.child("users").child(mUserNr).child("Email").setValue(newEmail);
                        //email = true;
                    }
                    if (m_oldPhoneNr != newPhoneNr) {
                        mDatabase.child(mUserNr).setValue(newPhoneNr);
                        mDatabase.child("sapiAdvertisments/userId").setValue(newPhoneNr);
                        //phoneNr = true;
                    }
                    mEdit_Button.setClickable(true);
                    mSave_Button.setClickable(false);
                    mFName.setEnabled(false);
                    mLName.setEnabled(false);
                    mPhoneNr.setEnabled(false);
                    mAddress.setEnabled(false);
                    mEmail.setEnabled(false);
                    mLogOff_Button.setClickable(true);
                    mMyAdvs_Button.setClickable(true);
                }
                else
                {
                    Toast.makeText(getActivity().getApplicationContext(), "Invalid phone number, can't save profile", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Inflate the layout for this fragment
        return this.mInflatedView;
    }
}