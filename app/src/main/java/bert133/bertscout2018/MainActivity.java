package bert133.bertscout2018;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_team) {
            toolbar.setTitle("Add Team!");
            return true;
        }
        if (id == R.id.action_sync_data) {
            toolbar.setTitle("Sync Data!");
            return true;
        }
        if (id == R.id.action_clear_data) {
            toolbar.setTitle("Clear Data!");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            /*TextView textView = (TextView) rootView.findViewById(R.id.section_label);*/
            /*textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));*/
            final GridView gridview = (GridView) rootView.findViewById(R.id.gridView);

            List<String> teamList = new ArrayList<String>(Arrays.asList(teams));

            ArrayAdapter<String> gridViewArrayAdapter = new ArrayAdapter<String>
                    (getContext(), android.R.layout.simple_list_item_1, teamList);

            gridview.setAdapter(gridViewArrayAdapter);

/*
            // todo ### this code doesn't work, this context fails here, never runs when debugging ###
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Toast.makeText(null, "" + position, Toast.LENGTH_SHORT).show();
                }
            });
*/

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 1;
        }
    }

    public static String[] teams = new String[]{
            "133",
            "58",
            "172",
            "5687",
            "1111",
            "2222",
            "3333",
            "4444",
            "5555",
            "6666",
            "7777",
            "8888",
            "9999",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
            "0000",
    };


}
