package com.optic.projectofinal.UI.activities.fragments.tabsFragments.profileDetails;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.optic.projectofinal.R;
import com.optic.projectofinal.adapters.OpinionsAdapter;
import com.optic.projectofinal.databinding.FragmentTabOpinionsBinding;
import com.optic.projectofinal.models.Opinion;
import com.optic.projectofinal.providers.UserDatabaseProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OpinionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OpinionsFragment extends Fragment {
    private static final String TAG = "own";
    private String idUser;
    private FragmentTabOpinionsBinding binding;
    public OpinionsFragment(String idUser) {
        this.idUser = idUser;
    }

    public OpinionsFragment() {
        // Required empty public constructor
    }


    public static OpinionsFragment newInstance(String idUser) {
        OpinionsFragment fragment = new OpinionsFragment(idUser);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentTabOpinionsBinding.inflate(inflater,container,false);
        binding.spinner.setAdapter(new ArrayAdapter<String>(getContext(),R.layout.support_simple_spinner_dropdown_item,new String[]{"Todos ","Cerrajero","Fontanero","Jardinero"}));
        loadOpinions();
        
        return binding.getRoot();
    }

    private void loadOpinions() {
        new UserDatabaseProvider().getOpinions(idUser).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                ArrayList<Opinion> listOpinions=new ArrayList<>();
                for(DocumentSnapshot i:list){
                    Opinion it=i.toObject(Opinion.class);
                    listOpinions.add(it);
                }
                binding.listOpinions.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.listOpinions.setAdapter(new OpinionsAdapter(getContext(),listOpinions));
            }
        }).addOnFailureListener(v-> Log.e(TAG, "loadOpinions: "+v.getMessage() ));
    }
}