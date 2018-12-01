package ro.sapientia.ms.sapiadvertiser;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.style.UpdateLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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
    private ImageButton mLogOff_Button;
    private ImageButton mMyAdvs_Button;
    private ImageButton mEdit_Button;
    private ImageButton mSave_Button;
    private EditText mLName;
    private EditText mFName;
    private EditText mEmail;
    private EditText mPhoneNr;
    private EditText mAddress;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String mUserNr = mAuth.getCurrentUser().getPhoneNumber();

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
        mSave_Button = mInflatedView.findViewById(R.id.imageButton_Save);
        mEdit_Button = mInflatedView.findViewById(R.id.imageButton_Edit);
        mSave_Button.setClickable(false);
        mFName = mInflatedView.findViewById(R.id.editText_FName);
        mLName = mInflatedView.findViewById(R.id.editText_LName);
        mEmail = mInflatedView.findViewById(R.id.editText_Email);
        mAddress = mInflatedView.findViewById(R.id.editText_Address);
        mPhoneNr = mInflatedView.findViewById(R.id.editText_PhoneNr);
        /*mFName.setEnabled(false);
        mLName.setEnabled(false);
        mPhoneNr.setEnabled(false);
        mAddress.setEnabled(false);
        mEmail.setEnabled(false);*/

        mDatabase.child(mUserNr).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                mLName.setText(dataSnapshot.child("LastName").getValue().toString());
                mFName.setText(dataSnapshot.child("FirstName").getValue().toString());
                mAddress.setText(dataSnapshot.child("Address").getValue().toString());
                mEmail.setText(dataSnapshot.child("Email").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabase.addValueEventListener(new ValueEventListener() {
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
        });

        mMyAdvs_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: new intent
            }
        });
        mLogOff_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
            }
        });
        mEdit_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEdit_Button.setClickable(false);
                mSave_Button.setClickable(true);
                /*mFName.setEnabled(true);
                mLName.setEnabled(true);
                mPhoneNr.setEnabled(true);
                mAddress.setEnabled(true);
                mEmail.setEnabled(true);*/

            }
        });
        mSave_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean saveSuccess = false;
                //TODO::Database update
                if(saveSuccess)
                {
                    mEdit_Button.setClickable(true);
                    mSave_Button.setClickable(false);
                    /*mFName.setEnabled(false);
                    mLName.setEnabled(false);
                    mPhoneNr.setEnabled(false);
                    mAddress.setEnabled(false);
                    mEmail.setEnabled(false);*/
                }
            }
        });

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_person, container, false);
    }

}
