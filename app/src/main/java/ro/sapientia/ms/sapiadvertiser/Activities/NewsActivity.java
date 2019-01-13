package ro.sapientia.ms.sapiadvertiser.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ro.sapientia.ms.sapiadvertiser.Fragments.AddFragment;
import ro.sapientia.ms.sapiadvertiser.Fragments.HomeFragment;
import ro.sapientia.ms.sapiadvertiser.Fragments.PersonFragment;
import ro.sapientia.ms.sapiadvertiser.R;

public class NewsActivity extends AppCompatActivity {

    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;

    private HomeFragment homeFragment;
    private AddFragment addFragment;
    private PersonFragment personFragment;
    private Boolean mNewUser = true;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        mMainFrame = (FrameLayout) findViewById(R.id.main_frame);
        mMainNav = (BottomNavigationView) findViewById(R.id.main_nav);

        homeFragment = new HomeFragment();
        addFragment = new AddFragment();
        personFragment = new PersonFragment();

        mMainNav.setSelectedItemId(R.id.nav_home);
        setFragment(homeFragment);
        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_home :
                        mMainNav.setItemBackgroundResource(R.color.colorPrimaryDark);
                        setFragment(homeFragment);
                        return true;
                    case R.id.nav_add:
                        if(mNewUser)
                        {
                            NewDialog();
                        }
                        else {
                            mMainNav.setItemBackgroundResource(R.color.colorPrimaryDark);
                            setFragment(addFragment);
                        }
                            return true;
                    case R.id.nav_person:
                        mMainNav.setItemBackgroundResource(R.color.colorPrimaryDark);
                        setFragment(personFragment);
                        return true;
                        default:
                            return false;
                }
            }
        });
        mDatabase.child("users").child(mAuth.getCurrentUser().getPhoneNumber()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child("FirstName").getValue().toString().isEmpty() &&
                        !dataSnapshot.child("LastName").getValue().toString().isEmpty() &&
                        !dataSnapshot.child("Email").getValue().toString().isEmpty() &&
                        !dataSnapshot.child("Address").getValue().toString().isEmpty() &&
                        !dataSnapshot.child("ProfileImage").getValue().toString().isEmpty()) {
                    mNewUser = false;
                }
                else
                {
                    mNewUser = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,fragment);
        fragmentTransaction.commit();
    }

    private void NewDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Sapi Advertiser");
        builder.setMessage("You must edit yor profile before adding new advertiment!");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}