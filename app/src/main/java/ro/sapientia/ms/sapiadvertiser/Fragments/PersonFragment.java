package ro.sapientia.ms.sapiadvertiser.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.IOException;
import java.util.UUID;
import de.hdodenhof.circleimageview.CircleImageView;
import ro.sapientia.ms.sapiadvertiser.Activities.LoginActivity;
import ro.sapientia.ms.sapiadvertiser.R;

import static android.app.Activity.RESULT_OK;

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
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("ProfileImages");

    private String m_oldFName;
    private String m_oldLName;
    private String m_oldEmail;
    private String m_oldAddress;

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
                Glide.with(getContext())
                        .asBitmap()
                        .load(dataSnapshot.child("ProfileImage").getValue().toString())
                        .into(mProfileImage);
                mPhoneNr.setText(mUserNr);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mMyAdvs_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyAdvsFragment fragment = new MyAdvsFragment();
                setFragment(fragment);
            }
        });
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        mLogOff_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogOffDialog();
            }
        });

        mEdit_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_oldFName = mFName.getText().toString();
                m_oldLName = mLName.getText().toString();
                m_oldEmail = mEmail.getText().toString();
                m_oldAddress = mAddress.getText().toString();
                mEdit_Button.setClickable(false);
                mSave_Button.setClickable(true);
                mProfileImage.setEnabled(true);
                mFName.setEnabled(true);
                mLName.setEnabled(true);
                mAddress.setEnabled(true);
                mEmail.setEnabled(true);
                mLogOff_Button.setClickable(false);
                mMyAdvs_Button.setClickable(false);
            }
        });
        mSave_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newFName = mFName.getText().toString();
                String newLName = mLName.getText().toString();
                String newEmail = mEmail.getText().toString();
                String newAddress = mAddress.getText().toString();
                if (m_oldFName != newFName) {
                    mDatabase.child("users").child(mUserNr).child("FirstName").setValue(newFName);
                }
                if (m_oldLName != newLName) {
                    mDatabase.child("users").child(mUserNr).child("LastName").setValue(newLName);
                }
                if (m_oldEmail != newEmail) {
                    mDatabase.child("users").child(mUserNr).child("Email").setValue(newEmail);
                }
                if(m_oldAddress != newAddress)
                {
                    mDatabase.child("users").child(mUserNr).child("Address").setValue(newAddress);
                }

                uploadImage();

                mEdit_Button.setClickable(true);
                mSave_Button.setClickable(false);
                mProfileImage.setEnabled(false);
                mFName.setEnabled(false);
                mLName.setEnabled(false);
                mAddress.setEnabled(false);
                mEmail.setEnabled(false);
                mLogOff_Button.setClickable(true);
                mMyAdvs_Button.setClickable(true);
            }
        });

        // Inflate the layout for this fragment
        return this.mInflatedView;
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,fragment);
        fragmentTransaction.commit();
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            final StorageReference storageRef = mStorageRef.child(UUID.randomUUID().toString());
            storageRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    mDatabase.child("users").child(mUserNr).child("ProfileImage").setValue(uri.toString());
                                    filePath = null;
                                }
                            });
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                mProfileImage.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void LogOffDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Sapi Advertiser");
        builder.setMessage("Are you sure you want to log off?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                mAuth.signOut();
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}