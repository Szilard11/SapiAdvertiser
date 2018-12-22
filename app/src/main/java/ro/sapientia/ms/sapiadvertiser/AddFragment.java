package ro.sapientia.ms.sapiadvertiser;


import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddFragment extends Fragment {
    private EditText mTitle;
    private EditText mShortDesc;
    private EditText mLongDesc;
    private EditText mPhoneNum;
    private EditText mLocation;
    private ImageButton mAddImage;
    private View mInflatedView;
    private Button mUpload;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private static final int RESULT_LOAD_IMAGE = 1;


    private List<String> fileNameList;
    private List<String> fileDoneList;


    public AddFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        this.mInflatedView = inflater.inflate(R.layout.fragment_add, container, false);
        mTitle = mInflatedView.findViewById(R.id.editText_Title);
        mShortDesc = mInflatedView.findViewById(R.id.editText_shortDesc);
        mLongDesc = mInflatedView.findViewById(R.id.editText_longDesc);
        mPhoneNum = mInflatedView.findViewById(R.id.editText_phone);
        mLocation = mInflatedView.findViewById(R.id.editText_Location);
        mAddImage = mInflatedView.findViewById(R.id.imageButton_add);
        mUpload = mInflatedView.findViewById(R.id.button_Upload);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        mAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), RESULT_LOAD_IMAGE);
            }
        });

        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TOdo lecsekkolni a kitoltott mezoket
                uploadAdv();
            }
        });


        return this.mInflatedView;
    }

    private void uploadAdv() {
        //TODO megoldani a feltoltest
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK){

            if(data.getClipData() != null){

                int totalItemsSelected = data.getClipData().getItemCount();

                for(int i = 0; i < totalItemsSelected; i++){

                    Uri fileUri = data.getClipData().getItemAt(i).getUri();

                    String fileName = getFileName(fileUri);

                    fileNameList.add(fileName);
                    fileDoneList.add("uploading");


                    String timestamp = String.valueOf(System.currentTimeMillis());
                    String fName =  timestamp + String.valueOf(i);

                    StorageReference fileToUpload = mStorageRef.child("Images").child(fName);

                    final int finalI = i;
                    fileToUpload.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            fileDoneList.remove(finalI);
                            fileDoneList.add(finalI, "done");
                        }
                    });

                }

                Toast.makeText(getActivity(), "Selected Multiple Files", Toast.LENGTH_SHORT).show();

            } else if (data.getData() != null){


                        Toast.makeText(getActivity(), "Selected Single File", Toast.LENGTH_SHORT).show();

            }

        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }



}
