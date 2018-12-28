package ro.sapientia.ms.sapiadvertiser.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import ro.sapientia.ms.sapiadvertiser.Adapters.ViewPagerAdapter;
import ro.sapientia.ms.sapiadvertiser.R;

public class MyAdvDetailActivity extends AppCompatActivity {
    private ViewPagerAdapter mAdapter;
    private ViewPager mViewPager;
    private ArrayList<String> mImageURLs = new ArrayList<>();
    private Button mShareButton;
    private Button mDeleteButton;
    private Button mEditButton;
    private Button mSaveButton;
    private TextView mTitle;
    private TextView mLongDesc;
    private TextView mPhone;
    private TextView mEmail;
    private TextView mLocation;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private String mNewsId;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String mUserId = mAuth.getCurrentUser().getPhoneNumber();
    private Context mContext = this;
    private String mOldTitle;
    private String mOldLongDesc;


    public void setViews(String pTitle,String pLongdesc,String pPhone,String pEmail,String pLocation){
        this.mTitle.setText(pTitle);
        this.mLongDesc.setText(pLongdesc);
        this.mPhone.setText(pPhone);
        this.mEmail.setText(pEmail);
        this.mLocation.setText(pLocation);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_adv_detail);
        mViewPager = findViewById(R.id.view_pager2);
        mShareButton = findViewById(R.id.my_adv_det_share_butt);
        mDeleteButton = findViewById(R.id.my_adv_det_delete_butt);
        mSaveButton = findViewById(R.id.my_adv_det_save_butt);
        mEditButton = findViewById(R.id.my_adv_det_edit_butt);
        mTitle = findViewById(R.id.my_adv_det_title);
        mLongDesc = findViewById(R.id.my_adv_det_longdesc);
        mPhone = findViewById(R.id.my_adv_det_phone);
        mEmail = findViewById(R.id.my_adv_det_email);
        mLocation = findViewById(R.id.my_adv_det_location);

        mSaveButton.setVisibility(View.INVISIBLE);

        getIncomingExtras();
        final Intent sharingIntent=new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Here is the share content body";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteDialog();
            }
        });

        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLongDesc.setEnabled(true);
                mTitle.setEnabled(true);
                mDeleteButton.setEnabled(false);
                mShareButton.setEnabled(false);
                mSaveButton.setVisibility(View.VISIBLE);
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveDialog();
                mDatabase.child("sapiAdvertisments").child(mNewsId).child("LongDesc").setValue(mLongDesc.getText().toString());
                mDatabase.child("sapiAdvertisments").child(mNewsId).child("Title").setValue(mTitle.getText().toString());
            }
        });

        mDatabase.child("users").child(mUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                final String email = dataSnapshot.child("Email").getValue().toString();
                final String loc = dataSnapshot.child("Address").getValue().toString();
                mDatabase.child("sapiAdvertisments").child(mNewsId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot data : dataSnapshot.child("Image").getChildren())
                        {
                            mImageURLs.add(data.getValue().toString());
                        }
                        String longDesc = dataSnapshot.child("LongDesc").getValue().toString();
                        String title = dataSnapshot.child("Title").getValue().toString();
                        mOldLongDesc = longDesc;
                        mOldTitle = title;
                        setViews(title, longDesc, mUserId, email, loc);
                        mAdapter = new ViewPagerAdapter(mContext, mImageURLs);
                        mViewPager.setAdapter(mAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getIncomingExtras()
    {
        if(getIntent().hasExtra("news_id"))
        {
            mNewsId = getIntent().getStringExtra("news_id");
        }
    }

    public void SaveDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Sapi Advertiser");
        builder.setMessage("Are you sure you want save?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do do my action here
                mDatabase.child("sapiAdvertisments").child(mNewsId).child("LongDesc").setValue(mLongDesc.getText().toString());
                mDatabase.child("sapiAdvertisments").child(mNewsId).child("Title").setValue(mTitle.getText().toString());
                mLongDesc.setEnabled(false);
                mTitle.setEnabled(false);
                mDeleteButton.setEnabled(true);
                mShareButton.setEnabled(true);
                mSaveButton.setVisibility(View.INVISIBLE);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mLongDesc.setText(mOldLongDesc);
                mTitle.setText(mOldTitle);
                mLongDesc.setEnabled(false);
                mTitle.setEnabled(false);
                mDeleteButton.setEnabled(true);
                mShareButton.setEnabled(true);
                mSaveButton.setVisibility(View.INVISIBLE);
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }


    private void DeleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Sapi Advertiser");
        builder.setMessage("Are you sure you want to delete this advertisment?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do do my action here
                mDatabase.child("sapiAdvertisments").child(mNewsId).child("isDeleted").setValue(1);
                finish();
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
