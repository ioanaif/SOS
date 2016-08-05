package com.janedoe.sos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by demouser on 8/4/16.
 */
public class Chat_Room extends AppCompatActivity{

    private Button send;
    private EditText input_msg;
    private TextView chat_conv;

    private String user_name, event_key;
    private DatabaseReference root;
    private String temp_key;
    private ListView mList;
    private MyAdapter mAdapter;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room);

        send = (Button) findViewById(R.id.send);
        input_msg = (EditText) findViewById(R.id.input_msg);
//        chat_conv = (TextView) findViewById(R.id.chat_conv);

        mList = (ListView) findViewById(R.id.texts); //list of chat messages
        mAdapter = new MyAdapter();
        mList.setAdapter(mAdapter);


        user_name = getSharedPreferences("login",MODE_PRIVATE).getString("user", null);
        event_key = getIntent().getExtras().get("event_key").toString();
        setTitle("Room - ", event_key);

        root = FirebaseDatabase.getInstance().getReference().child("events").child(event_key);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, Object> map = new HashMap<String, Object>();
                temp_key = root.push().getKey();
                root.updateChildren(map);

                DatabaseReference message_root = root.child(temp_key);

                Map<String,Object> map2 = new HashMap<String, Object>();
                map2.put("name",user_name);
                map2.put("msg", input_msg.getText().toString());

                message_root.updateChildren(map2);
                input_msg.setText("");
            }
        });

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private String chat_msg, chat_user_name;
    private void append_chat_conversation(DataSnapshot dataSnapshot)
    {
        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext()){
            chat_msg = (String) ((DataSnapshot)i.next()).getValue();
            chat_user_name = (String) ((DataSnapshot)i.next()).getValue();
//            chat_conv.append(chat_user_name+" : "+ chat_msg +"\n");
//            chat_conv.append(chat_user_name+" : "+ chat_msg +"\n");
            Text e = new Text();
            e.setmAuthor(chat_user_name);
            e.setmText(chat_msg);
            mAdapter.addChat(e);
        }
    }
    private void setTitle(String s, String event_name) {
    }

    private class MyAdapter extends BaseAdapter {

          private ArrayList<Text> texts;
        public MyAdapter(){
            texts = new ArrayList<>();
        }

        public void setTexts(ArrayList<Text> newTexts){
            texts = newTexts;
            notifyDataSetChanged();
        }
        public void addChat(Text e){
            texts.add(e);
            notifyDataSetChanged();

        }

        @Override
        public int getCount() {
            return texts.size();
        }

        @Override
        public Text getItem(int possition) {
            return texts.get(possition);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;
            if(convertView ==null){
                LayoutInflater inflater = LayoutInflater.from(Chat_Room.this);
                view = inflater.inflate(R.layout.text_item,null);

                holder = new ViewHolder();
                holder.author = (TextView) view.findViewById(R.id.author);
                holder.text = (TextView) view.findViewById(R.id.text);
                holder.pic = (ImageView) view.findViewById(R.id.img);
                view.setTag(holder);
            }

            else {
                view = convertView;
                holder = (ViewHolder) view.getTag();

            }
            Text current = texts.get(position);
            holder.author.setText(current.getmAuthor());
            holder.text.setText(current.getmText());

            return view;

        }
    }
}
