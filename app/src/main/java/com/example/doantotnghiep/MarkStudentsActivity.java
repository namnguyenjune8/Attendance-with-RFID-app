package com.example.doantotnghiep;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
//import android.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.doantotnghiep.adapters.MarkStudentListAdapter;
import com.example.doantotnghiep.realm.DateRegister;
import com.example.doantotnghiep.realm.Register;
import com.example.doantotnghiep.realm.Student;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;

public class MarkStudentsActivity extends AppCompatActivity
{
    private static final String TAG = "MArkStudentsActivity";
    Toolbar mToolbar;

    String batchID;
    ArrayList<String> macID;
    public static MarkStudentListAdapter markStudentListAdapter;

    Realm realm;
    RealmConfiguration realmConfig;
    RealmResults<Student> studentList;
    RealmList<Student> presentStudentList;
    @Nullable
    Register register;
    DateRegister record;
    int value;

    ListView markStudentListView;


    boolean selectDateCheck = false;
    Date selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_students);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.main_blue));
        }
        initToolbar();
        value = getIntent().getIntExtra("Value",1);

        selectDateCheck = getIntent().getBooleanExtra("Manual Date",false);
        selectedDate = (Date)getIntent().getSerializableExtra("Selected Date");

        //Toast.makeText(this,""+selectDateCheck+" "+selectedDate.getDate()+" "+(selectedDate.getMonth()+1)+" "+(selectedDate.getYear()),Toast.LENGTH_LONG).show();

        Realm.init(this);
        realmConfig = new RealmConfiguration.Builder().build();
        realm = Realm.getInstance(realmConfig);

        markStudentListView = (ListView)findViewById(R.id.student_listview);
        markStudentListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        macID = new ArrayList<>();
        presentStudentList = new RealmList<>();

        batchID = getIntent().getStringExtra("Batch ID");
        macID = getIntent().getStringArrayListExtra("MAC ID's");
        Log.d("Mark",""+macID.size()+" "+batchID);
        studentList = Objects.requireNonNull(realm.where(Register.class).equalTo("BatchID", batchID).findFirst()).getStudents().sort("Roll_number");

        for (Student s : studentList) {
            if(macID.contains(s.getMathe()))
            {
                if(!presentStudentList.contains(s))
                    presentStudentList.add(s);
                macID.remove(s.getMathe());
            }
            else if(macID.contains(s.getMac_ID2()))
            {
                if(!presentStudentList.contains(s))
                    presentStudentList.add(s);
                macID.remove(s.getMac_ID2());
            }
        }
        markStudentListAdapter = new MarkStudentListAdapter(this, studentList, presentStudentList);
        markStudentListView.setAdapter(markStudentListAdapter);

        markStudentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println(i);
                if(presentStudentList.contains(studentList.get(i)))
                    presentStudentList.remove(studentList.get(i));
                else
                    presentStudentList.add(studentList.get(i));
                markStudentListAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initToolbar() {
        androidx.appcompat.widget.Toolbar mToolbar = findViewById(R.id.toolbar);
        // androidx.appcompat.widget.Toolbar (mToolbar) = findViewById(R.id.toolbar);
       // mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Mark Students");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.mark_students_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.marked_menu) {
            markInDatabase();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void markInDatabase()
    {

        record = new DateRegister();
        realm.beginTransaction();
        record.setDateID(realm.where(DateRegister.class).findAll().size() + 1);
        if(selectDateCheck)
            record.setDateToday(selectedDate);
        else
            record.setDateToday(new Date());
        record.setValue(value);
        record.setStudentPresent(presentStudentList);
        realm.copyToRealmOrUpdate(record);
        realm.commitTransaction();

        register = realm.where(Register.class).equalTo("BatchID",batchID).findFirst();
        realm.beginTransaction();
        assert register != null;
        register.getRecord().add(record);
        realm.commitTransaction();
    }



    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}
