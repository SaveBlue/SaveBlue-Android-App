package com.saveblue.saveblueapp.ui.dashboard.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.saveblue.saveblueapp.JwtHandler;
import com.saveblue.saveblueapp.Logout;
import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.models.User;
import com.saveblue.saveblueapp.ui.addExpenseIncome.AddIncomeActivity;

import java.util.Objects;


public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private TextView username;
    private TextView email;
    private ImageView profilePicture;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        username = root.findViewById(R.id.textViewUsername);
        email = root.findViewById(R.id.textViewEmail);
        profilePicture = root.findViewById(R.id.profileImage);

        Button logout = root.findViewById(R.id.LogOutButton);
        logout.setOnClickListener(v -> Logout.logout(requireContext()));


        // edit profile activity setup process
        Intent editProfileIntent = new Intent(getContext(), EditProfileActivity.class);

        Button editProfile = root.findViewById(R.id.editProfile);
        editProfile.setOnClickListener(v -> {
            editProfileIntent.putExtra("Task", "EDIT");
            startActivity(editProfileIntent);
        });

        Button changePassword = root.findViewById(R.id.changePassword);
        changePassword.setOnClickListener(v -> {
            editProfileIntent.putExtra("Task", "PASS");
            startActivity(editProfileIntent);
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initialise viewmodel
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        observerSetup();
    }

    // initialise observer for account list
    public void observerSetup() {
        //fetch jwt from dedicated handler class
        JwtHandler jwtHandler = new JwtHandler(getContext());
        String jwt = jwtHandler.getJwt();
        String id = jwtHandler.getId();

        profileViewModel.getUser(id, jwt).observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {

                username.setText(user.getUsername());
                email.setText(user.getEmail());

                profilePicture.setImageResource(R.drawable.avatar_1);

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        JwtHandler jwtHandler = new JwtHandler(getContext());
        profileViewModel.getUser(jwtHandler.getId(),jwtHandler.getJwt());
    }

}