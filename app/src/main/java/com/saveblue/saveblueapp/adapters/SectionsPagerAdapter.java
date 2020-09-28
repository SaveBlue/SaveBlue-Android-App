package com.saveblue.saveblueapp.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.saveblue.saveblueapp.models.Account;
import com.saveblue.saveblueapp.ui.accountDetails.expense.ExpenseFragment;
import com.saveblue.saveblueapp.ui.accountDetails.income.IncomeFragment;
import com.saveblue.saveblueapp.ui.accountDetails.overview.AccountOverviewFragment;
import com.saveblue.saveblueapp.ui.accountDetails.ui.ToDELETE.ExpenseIncomeFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentStateAdapter {
    private String accountId;

    public SectionsPagerAdapter(FragmentActivity fa, String accountId) {
        super(fa);
        this.accountId = accountId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new AccountOverviewFragment();

            case 1:
                return new ExpenseFragment(accountId);

            case 2:
                return new IncomeFragment(accountId);

            default:
                return ExpenseIncomeFragment.newInstance(position + 1);
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}