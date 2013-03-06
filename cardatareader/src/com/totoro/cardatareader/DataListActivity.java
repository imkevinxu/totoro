package com.totoro.cardatareader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class DataListActivity extends FragmentActivity
        implements DataListFragment.Callbacks {

    private boolean mTwoPane;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_list);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (findViewById(R.id.data_detail_container) != null) {
            mTwoPane = true;
            ((DataListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.data_list))
                    .setActivateOnItemClick(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(DataDetailFragment.ARG_ITEM_ID, id);
            DataDetailFragment fragment = new DataDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.data_detail_container, fragment)
                    .commit();

        } else {
            Intent detailIntent = new Intent(this, DataDetailActivity.class);
            detailIntent.putExtra(DataDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
}
