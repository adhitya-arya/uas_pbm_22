package com.example.firestore.ui.daftarmatkul;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firestore.R;
import com.example.firestore.adapter.MahasiswaAdapter;
import com.example.firestore.databinding.FragmentDaftarMatkulBinding;
import com.example.firestore.model.Mahasiswa;
import com.example.firestore.ui.InputActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DaftarMatkulFragment extends Fragment {

    private DaftarMatkulViewModel mViewModel;
   // private FragmentDaftarMatkulBinding binding;
    private RecyclerView recyclerView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Mahasiswa> list = new ArrayList<>();
    private MahasiswaAdapter mahasiswaAdapter;
    private ProgressDialog progressDialog;

    public static DaftarMatkulFragment newInstance() {
        return new DaftarMatkulFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DaftarMatkulViewModel daftarMatkulViewModel = new ViewModelProvider(this).get(DaftarMatkulViewModel.class);

        // PROSES BINDING LAYOUT KE FRAGMENT
        @NonNull FragmentDaftarMatkulBinding binding = FragmentDaftarMatkulBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final TextView textView = binding.textHome;
        recyclerView = binding.recyclerView;

        // ANIMASI LOADING
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading");
        // CREATE ADAPTER & ISI ADAPTER DARI LIST MAHASISWA
        mahasiswaAdapter = new MahasiswaAdapter(getContext(), list);

        // POP-UP EDIT & DELETE
        mahasiswaAdapter.setDialog(new MahasiswaAdapter.Dialog() {
            @Override
            public void onClick(int pos) {
                final CharSequence[] dialogItem = {"Edit","Hapus"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setItems(dialogItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                // JIKA EDIT DATA DIARAHKAN KE InputActivity
                                // AMBIL VALUE DARI FIRESTORE DISIMPAN KE INTENT
                                Intent intent = new Intent(getContext(), InputActivity.class);
                                intent.putExtra("id", list.get(pos).getId());
                                intent.putExtra("nama_matkul", list.get(pos).getNamaMatkul());
                                intent.putExtra("nama_hari", list.get(pos).getNamaHari());
                                startActivity(intent);
                                break;
                            case 1:
                                // DELETE
                                deleteData(list.get(pos).getId());
                                break;
                        }
                    }
                });
                dialog.show();
            }
        });

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
        //binding = null;
    }

    // GET DATA (READ)
    private void getData(){
        progressDialog.show();
        db.collection("daftar_matkul").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        list.clear();
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                // AMBIL COLLECTION DARI FIREBASE DAN DIMASUKKAN KE MODEL MHS
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

    // DELETE
    private void deleteData(String id){
        db.collection("daftar_matkul").document(id).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()){
                            Toast.makeText(getContext(), "Data gagal dihapus", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                        getData();
                    }
                });
    }
}