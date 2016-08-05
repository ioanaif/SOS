package com.janedoe.sos;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter adapter;
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(adapter);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return (position == 0 ? new HelpFragment() : new EventActivity());
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
    public void acceptEvent(View view){
        int pos = (int) view.getTag();//index of event to be removed
        ListView mlist = (ListView)findViewById(R.id.list);
        EventActivity.MyAdapter mAdapter = (EventActivity.MyAdapter) mlist.getAdapter();
        Event e = (Event) mAdapter.getItem(pos);
        String key = (String) mAdapter.getKey(pos);
        //start new activity with map
        Intent intent = new Intent(MainActivity.this,HelperMainScreen.class);
        intent.putExtra("extraGeo",e.location);
        intent.putExtra("extraDate",e.time);
        intent.putExtra("extraMessage",e.message);
        intent.putExtra("extraFileKey",key);
        startActivity(intent);

        Log.d("Accept","This is working!!!");

    }

}
