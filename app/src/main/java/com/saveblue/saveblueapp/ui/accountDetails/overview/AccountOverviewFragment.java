package com.saveblue.saveblueapp.ui.accountDetails.overview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.saveblue.saveblueapp.R;

public class AccountOverviewFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_account_overview, container, false);

        initArrowButton(root);

        return root;


    }

    private void initArrowButton(View view) {

        ConstraintLayout accountDetails = view.findViewById(R.id.accountDetails);
        CardView cardView = view.findViewById(R.id.cardAccountDetails);
        ImageView arrowImage = view.findViewById(R.id.accountExpand);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accountDetails.getVisibility() == View.VISIBLE) {
                    accountDetails.setVisibility(View.GONE);
                    arrowImage.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                } else {
                    accountDetails.setVisibility(View.VISIBLE);
                    arrowImage.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                }
            }
        });
    }
    // TODO: card for account info and edit button to open a dialog
}
