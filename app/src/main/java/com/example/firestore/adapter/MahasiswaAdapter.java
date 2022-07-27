package com.example.firestore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firestore.R;
import com.example.firestore.model.Mahasiswa;

import java.util.List;

public class MahasiswaAdapter extends RecyclerView.Adapter<MahasiswaAdapter.MyViewHolder> {

    private Context context;
    private List<Mahasiswa> list;
    private Dialog dialog;

    public interface Dialog{
        void onClick(int pos);
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public MahasiswaAdapter(Context context, List<Mahasiswa> list){
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_matkul, parent, false);
        return new MyViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // BINDING DATA DARI FIRESTORE KE VIEWHOLDER list_matkul.xml YG BERASAL DARI MODEL Mahasiswa
        holder.nama_matkul.setText(list.get(position).getNamaMatkul());
        holder.nama_hari.setText(list.get(position).getNamaHari());
        holder.nama_kelas.setText(list.get(position).getNamaKelas());
        holder.jam_kelas.setText(list.get(position).getJamKelas());
    }

    // MENGETAHUI PANJANG DATA LIST
    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        // INISIALISASI LAYOUT KE HOLDER
        TextView nama_matkul, nama_hari, nama_kelas, jam_kelas;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nama_matkul = itemView.findViewById(R.id.list_matkul);
            nama_hari = itemView.findViewById(R.id.list_hari);
            nama_kelas = itemView.findViewById(R.id.list_kelas);
            jam_kelas = itemView.findViewById(R.id.list_jam);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dialog != null){
                        dialog.onClick(getLayoutPosition());
                    }
                }
            });
        }
    }

}
