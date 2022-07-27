package com.example.firestore.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.firestore.R;
import com.example.firestore.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // BINDING VIEW KE FRAGMENT PROFILE
        final TextView viewnim = binding.textNim;
        final TextView viewnama = binding.textNama;
        final TextView viewemail = binding.textEmail;
        final TextView viewgithub = binding.textGithub;
        final ImageView viewgambar = binding.gambarView;

        viewnim.setText("20410100069");
        viewnama.setText("Adhitya Aryaputra Ashari");
        viewemail.setText("20410100069@dinamika.ac.id");
        viewgithub.setText("github.com/adhitya-arya");
        viewgambar.setImageResource(R.drawable.fotoku);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}