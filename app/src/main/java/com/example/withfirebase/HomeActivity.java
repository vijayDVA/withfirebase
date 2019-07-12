package com.example.withfirebase;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.withfirebase.model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity
{
    private FloatingActionButton  fabBtn;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private RecyclerView recyclerView;
    private EditText titleup;
    private EditText noteup;
    private Button btndeleteup;
    private Button btnupdateup;

    private String title;
    private String note;
    private String post;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Task App");

        mAuth=FirebaseAuth.getInstance();


        FirebaseUser mUser=mAuth.getCurrentUser();
        String uId=mUser.getUid();

        mDatabase= FirebaseDatabase.getInstance().getReference().child("TaskNote").child(uId);
        mDatabase.keepSynced(true);

        recyclerView=findViewById(R.id.recycler);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        fabBtn=findViewById(R.id.fab_btn);
        fabBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder myDialog=new AlertDialog.Builder(HomeActivity.this);
                LayoutInflater inflater=LayoutInflater.from(HomeActivity.this);
                View myview =inflater.inflate(R.layout.custominputfield,null);
                myDialog.setView(myview);

                final AlertDialog dialog=myDialog.create();

                final EditText title=myview.findViewById(R.id.edt_title);
                final EditText note=myview.findViewById(R.id.edt_note);
                Button btnsave=myview.findViewById(R.id.btn_save);

                btnsave.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        String mTitle = title.getText().toString().trim();
                        String mNote = note.getText().toString().trim();
                        if (TextUtils.isEmpty(mTitle))
                        {
                            title.setError("Required filed..");
                            return;
                        }
                        if (TextUtils.isEmpty(mNote))
                        {
                            note.setError("Required field..");
                            return;
                        }

                        String id=mDatabase.push().getKey();
                        String datee= DateFormat.getDateInstance().format(new Date());
                        Data data=new Data(mTitle,mNote,id,datee);
                        mDatabase.child(id).setValue(data);
                        Toast.makeText(getApplicationContext(),"Data Insert",Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        FirebaseRecyclerAdapter<Data,MyViewHolder>adapter =new FirebaseRecyclerAdapter<Data, MyViewHolder>
                (
                        Data.class,
                        R.layout.item_data,
                        MyViewHolder.class,
                        mDatabase
                )
        {
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, final Data model, final int position)
            {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDate(model.getDate());
                viewHolder.setNote(model.getNote());
                viewHolder.myview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        post=getRef(position).getKey();
                        title=model.getTitle();
                        note=model.getNote();

                        updateData();
                    }
                });

            }
        };
        recyclerView.setAdapter(adapter);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {

        View myview;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myview=itemView;
        }
        public void setTitle(String title){
            TextView mTitle=myview.findViewById(R.id.title);
            mTitle.setText(title);
        }
        public void setNote(String note){
            TextView mNote=myview.findViewById(R.id.note);
            mNote.setText(note);
        }

        public void setDate(String date){
            TextView mDate=myview.findViewById(R.id.date);
            mDate.setText(date);
        }

    }

    public void updateData()
    {
        AlertDialog.Builder mydialog=new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater=LayoutInflater.from(HomeActivity.this);
         View myview=inflater.inflate(R.layout.updateinputfield,null);
         mydialog.setView(myview);

         final AlertDialog dialog=mydialog.create();

         titleup=myview.findViewById(R.id.edt_title_upd);
         noteup=myview.findViewById(R.id.edt_note_upd);

         titleup.setText(title);
         titleup.setSelection(title.length());

         noteup.setText(note);
         noteup.setSelection(note.length());

         btndeleteup=myview.findViewById(R.id.btn_delete_upd);
         btnupdateup=myview.findViewById(R.id.btn_update_upd);
         btnupdateup.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 title=titleup.getText().toString().trim();
                 note=noteup.getText().toString().trim();

                 String mDate=DateFormat.getDateInstance().format(new Date());
                 Data data=new Data(title,note,mDate,post);
                 dialog.dismiss();

                 mDatabase.child(post).setValue(data);
                 dialog.dismiss();

             }
         });

         btndeleteup.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 mDatabase.child(post).removeValue();
                 dialog.dismiss();
             }


         });
         dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
