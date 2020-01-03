package com.example.learnjapanese.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.learnjapanese.Adapters.UserData;
import com.example.learnjapanese.MainActivity;
import com.example.learnjapanese.R;
import com.example.learnjapanese.RegisterActivity;
import com.example.learnjapanese.ThemeSettings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentUserPage extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_page, container, false);
    }
    ImageView imageViewLogout;
    TextView textViewEmail;
    TextView textViewName;
    TextView textViewBorn;
    TextView textViewSex;
    CircleImageView imageViewProfile;

    private StorageReference mStorageRef;
    private  DatabaseReference mDatabaseRef;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ThemeSettings themeSettings = new ThemeSettings(getActivity());
        super.onViewCreated(view, savedInstanceState);
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        imageViewLogout = view.findViewById(R.id.imageViewLogout);
        textViewEmail = view.findViewById(R.id.textViewEmail);
        textViewName = view.findViewById(R.id.textViewName);
        textViewBorn = view.findViewById(R.id.textViewBorn);
        textViewSex = view.findViewById(R.id.textViewSex);
        imageViewProfile=view.findViewById(R.id.profile_image);

        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        textViewEmail.setText(firebaseAuth.getCurrentUser().getEmail());
        imageViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                getActivity().finish();
                getActivity().overridePendingTransition(0, 0);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(0, 0);
            }
        });

        final DatabaseReference databaseReferenceUser;
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference("UserData");
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReferenceUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String id = user.getUid();
                UserData userData =dataSnapshot.child(id).getValue(UserData.class);
                textViewName.setText( userData.name+" "+userData.lastName);
                textViewBorn.setText(userData.day+"."+userData.month+"."+userData.year);
                textViewSex.setText(userData.sex);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //uploadFile();
                openFileChooser();
            }
        });


    }
    private static final int PICK_IMAGE_REQUEST=1;
    private Uri mImageUri;
    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
        && data !=null && data.getData() != null){
            mImageUri = data.getData();
            Log.d(TAG, "onActivityResult: "+mImageUri);
            Picasso.get().load(mImageUri).into(imageViewProfile);
        }
    }
    private static final String TAG="FragmentUserPage";
}
