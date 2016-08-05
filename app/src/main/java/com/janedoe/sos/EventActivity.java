package com.janedoe.sos;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;

import android.renderscript.ScriptGroup;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

public class EventActivity extends Fragment {

    private ListView mList;
    private MyAdapter mAdapter;
    private int RADIUS = 1000;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_event, container, false);
        view.setBackgroundColor(Color.LTGRAY);

        final Activity activity = getActivity();

        mList = (ListView) view.findViewById(R.id.list);
        mAdapter = new MyAdapter();
        mList.setAdapter(mAdapter);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference allevents = dbRef.child("events");


        ChildEventListener eventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Event e = dataSnapshot.getValue(Event.class);
                String key = dataSnapshot.getKey();
                LocationManager lm = (LocationManager) activity.getSystemService(activity.LOCATION_SERVICE);

                if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(activity.getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                Location currloc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if(currloc == null) {
                    //Toast.makeText((EventActivity.this).activity                                                                                                                                                                                                                                                                                                                                                                                                                                        , "Cannot connect to GPS", Toast.LENGTH_SHORT).show();
                    return;
                }

                String loc = e.location;

                double lat = Double.parseDouble(loc.split(",")[0]);
                double lon = Double.parseDouble(loc.split(",")[1]);
                Location eventloc = new Location("");
                eventloc.setLongitude(lon);
                eventloc.setLatitude(lat);

                float distance = eventloc.distanceTo(currloc);
                if (distance <= RADIUS) {
                    mAdapter.addEvent(key, e);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                Toast.makeText(EventActivity.this, "New info available", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                Toast.makeText(EventActivity.this, "Event removed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        allevents.addChildEventListener(eventListener);

        return view;


    }
    public class ViewHolder{

        public TextView time;
        public TextView loc;


    }

    public class MyAdapter extends BaseAdapter {

        private ArrayList<Event> events;
        private ArrayList<String> keys;

        public MyAdapter () {
            events = new ArrayList<>();
            keys = new ArrayList<>();
        }

        public void addEvent(String key,Event e) {
            events.add(e);
            keys.add(key);
            notifyDataSetChanged();
        }

        public void removeEvent(int pos) {
            events.remove(pos);
            keys.remove(pos);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return events.size();
        }

        @Override
        public Object getItem(int position) {
            return events.get(position);
        }
        public Object getKey(int position){
            return keys.get(position);
        }
        @Override
        public long getItemId(int position) {
            return 0;
        }

        public int getIndex(Event e){
            return events.indexOf(e);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View v;
            final int pos = position;
            ViewHolder holder;
            Button ac;
            if (convertView==null) {
                LayoutInflater li = LayoutInflater.from(getActivity());
                v = li.inflate(R.layout.event_object,null);


                holder = new ViewHolder();
                holder.loc = (TextView) v.findViewById(R.id.desc); //save pointers
                holder.time = (TextView) v.findViewById(R.id.timetext);


                v.setTag(holder);

            }
            else{
                v = convertView;
                holder = (ViewHolder)v.getTag(); //get holder object which has the pointers to the children

            }

            ac = (Button) v.findViewById(R.id.show);

            Event curr = events.get(position);
            ac.setTag(curr);

            holder.time.setText(curr.time);
            holder.loc.setText(curr.message);


            return v;

        }



    }

    public void declineEvent(View view){
        //get event

        int pos = (int) view.getTag(); //index of event to be removed

        //remove event from list
        mAdapter.removeEvent(pos);



    }

}
