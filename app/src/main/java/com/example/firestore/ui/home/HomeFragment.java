package com.example.firestore.ui.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firestore.adapter.MahasiswaAdapter;
import com.example.firestore.databinding.FragmentHomeBinding;
import com.example.firestore.model.Mahasiswa;
import com.example.firestore.ui.InputActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.divider.MaterialDividerItemDecoration;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Mahasiswa> list = new ArrayList<>();
    private MahasiswaAdapter mahasiswaAdapter;
    private ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // PROSES BINDING LAYOUT KE FRAGMENT
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        recyclerView = binding.recyclerView;

        // ANIMASI LOADING
       progressDialog = new ProgressDialog(getContext());
       progressDialog.setTitle("Loading");
       progressDialog.setMessage("Mengambil data...");
       mahasiswaAdapter = new MahasiswaAdapter(getContext(), list);

        // KONFIGURASI ADAPTER KE LAYOUT RECYLEVIEW DI ISI DENGAN DATA MAHASISWA
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(mahasiswaAdapter);

        return root;
    }

    // KETIKA APLIKASI DIBUKA, LANGSUNG AMBIL DATA DARI FIREBASE
    @Override
    public void onStart() {
        super.onStart();
        getData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // AMBIL DATA HARI MATKUL = SESUAI HARI INI
    private void getData(){
        progressDialog.show();
        db.collection("daftar_matkul").whereEqualTo("hari_matkul", getCurrentDate()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        list.clear();
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                // AMBIL COLLECTION DARI FIREBASE
                                Mahasiswa mhs = new Mahasiswa(
                                        document.getString("nama_matkul"),
                                        document.getString("hari_matkul"),
                                        document.getString("kelas_matkul"),
                                        document.getString("jam_matkul")
                                );
                                mhs.setId(document.getId()); // SET ID COLLECTION
                                list.add(mhs); // LIST DI ISI DATA DARI MODEL MAHASISWA
                            }
                            mahasiswaAdapter.notifyDataSetChanged();
                        }else{
                            Toast.makeText(getContext(), "Data gagal diambil", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    // METHOD CONVERT DATE KE HARI SEKARANG
    private static String getCurrentDate(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", new Locale("id","ID"));
        String tgl = sdf.format(c);
        return tgl;
    }

}