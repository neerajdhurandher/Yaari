package com.example.apptrail4;

        import android.os.Bundle;
        import android.text.Editable;
        import android.text.TextWatcher;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.EditText;

        import androidx.annotation.NonNull;
        import androidx.fragment.app.Fragment;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import com.example.apptrail4.AdapterUserRV;
        import com.example.apptrail4.ModelUser;
        import com.example.apptrail4.R;
        import com.google.android.material.floatingactionbutton.FloatingActionButton;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import java.util.ArrayList;
        import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class search_frg extends Fragment {
    RecyclerView recyclerView;
    AdapterUserRV adapterUserRV;
    List<ModelUser> userList;
    FloatingActionButton fl_search_btn;
    EditText search_user;
    FirebaseUser currentuser;

    public search_frg() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view =  inflater.inflate(R.layout.fragment_search_frg, container, false);

        recyclerView = view.findViewById(R.id.user_recycleerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        fl_search_btn = view.findViewById(R.id.floatingsearchbtn);

        currentuser = FirebaseAuth.getInstance().getCurrentUser();


        userList  = new ArrayList<>();
        getAllUsers();



        search_user = view.findViewById(R.id.search_user_box_id);

        final String searchuser = search_user.getText().toString();

        search_user.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                getAllUsers();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchuser = search_user.getText().toString();
                SearchUsers(searchuser);

            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchuser = search_user.getText().toString();
                SearchUsers(searchuser);

            }
        });


        fl_search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String searchuser = search_user.getText().toString();

                SearchUsers(searchuser);

            }
        });


        return view;
    }




    private void SearchUsers(final String searchuser) {

        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){

                    ModelUser modelUser = ds.getValue(ModelUser.class);

                    if(!modelUser.getUid().equals(currentuser.getUid())) {

                        if (!searchuser.isEmpty()){
                            if (modelUser.getDisplayname().toLowerCase().contains(searchuser.toLowerCase()) ||
                                    modelUser.getEmail().toLowerCase().contains(searchuser.toLowerCase())){

                                userList.add(modelUser);

                            }
                        }
                        else {
                            getAllUsers();
                        }


                    }
                    adapterUserRV = new AdapterUserRV(getActivity(),userList);
                    adapterUserRV.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterUserRV);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getAllUsers() {

        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){

                    ModelUser modelUser = ds.getValue(ModelUser.class);

                    if(!modelUser.getUid().equals(currentuser.getUid())){

                        userList.add(modelUser);
                    }


                    adapterUserRV = new AdapterUserRV(getActivity(),userList);
                    recyclerView.setAdapter(adapterUserRV);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
