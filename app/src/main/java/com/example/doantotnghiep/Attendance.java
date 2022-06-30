package com.example.doantotnghiep;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;



import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.doantotnghiep.realm.Register;
import com.melnykov.fab.FloatingActionButton;
import com.example.doantotnghiep.adapters.ListViewAdapter;
import com.example.doantotnghiep.others.ObjectItem;

import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;


public class Attendance extends AppCompatActivity
{
    ListView listView;
    FloatingActionButton fab;
    Intent intent;

    Realm realm;
    RealmResults<Register> allBatch;
    RealmConfiguration realmConfig;
    int numBatch;
//    ActionBar mToolbar;
     Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.main_blue));
        }
        initToolbar();

        listView =  findViewById(android.R.id.list);
        listView.addHeaderView(new View(this), null, false);
        fab =  findViewById(R.id.fab);
        fab.attachToListView(listView);

        Realm.init(this);
        realmConfig = new RealmConfiguration.Builder().build();
        realm = Realm.getInstance(realmConfig);
        allBatch = realm.where(Register.class).findAll();
        numBatch = allBatch.size();
        ObjectItem[] ObjectItemData = new ObjectItem[numBatch];
        int i;
        for(i=0 ; i<numBatch ; i++)
            ObjectItemData[i] = new ObjectItem(allBatch.get(i).getBatchID(),allBatch.get(i).getSubject(),allBatch.get(i).getSubjectCode(),allBatch.get(i).getBatch(),allBatch.get(i).getSemester(),allBatch.get(i).getStream(),allBatch.get(i).getSection(),allBatch.get(i).getGroup());

        ListViewAdapter listAdapter = new ListViewAdapter(this,R.layout.list_item,ObjectItemData);
        listView.setAdapter(listAdapter);

        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                intent = new Intent(com.example.doantotnghiep.Attendance.this, Add_class.class);
                com.example.doantotnghiep.Attendance.this.startActivity(intent);
            }
        });
    }

    private void initToolbar() {
        //androidx.appcompat.widget.Toolbar mToolbar = findViewById(R.id.toolbar);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Điểm danh");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void selected_class(String BatchID)
    {
        intent = new Intent(com.example.doantotnghiep.Attendance.this, ClassDetailsActivity.class);
        intent.putExtra("BatchID", BatchID);
        com.example.doantotnghiep.Attendance.this.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}
