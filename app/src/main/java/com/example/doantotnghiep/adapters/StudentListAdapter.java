package com.example.doantotnghiep.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.doantotnghiep.ClassDetailsActivity;
import com.example.doantotnghiep.R;
import com.example.doantotnghiep.realm.Student;

import io.realm.RealmList;
import io.realm.RealmResults;


public class StudentListAdapter extends BaseAdapter
{
    Context context;
    RealmResults<Student> studentList;

    TextView rollView,nameView,phoneView,matheView,mac2View;

    public StudentListAdapter(Context context, RealmResults<Student> studentList) {
       this.context = context;
        this.studentList = studentList;
    }


    @Override
    public int getCount() {
        if(studentList!=null)
            return studentList.size();
        else
            return 0;
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return studentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null)
        {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.student_list_item, parent, false);
        }
        rollView = (TextView)convertView.findViewById(R.id.student_item_roll);
        nameView = (TextView)convertView.findViewById(R.id.student_item_name);
        phoneView = (TextView)convertView.findViewById(R.id.student_item_phone);
        matheView = (TextView)convertView.findViewById(R.id.student_item_mathe);
        mac2View = (TextView)convertView.findViewById(R.id.student_item_mac2);

        rollView.setText(studentList.get(position).getRoll_number());
        nameView.setText(studentList.get(position).getStudent_name());
        phoneView.setText("MSSV: "+studentList.get(position).getPhone_no());
        matheView.setText("ID thẻ :"+studentList.get(position).getMathe());
        if(studentList.get(position).getMac_ID2() == null)
            mac2View.setText("ID thiết bị :N/A");
        else
            mac2View.setText("ID thiết bị :"+studentList.get(position).getMac_ID2());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ClassDetailsActivity)context).showStudentAttendanceFragment(studentList.get(position).getRoll_number());
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Delete Student from Class?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                RealmList<Student> finalStudents = new RealmList<Student>();
                                for(int i=0 ; i<studentList.size() ; i++) {
                                    if(i!=position)
                                        finalStudents.add(studentList.get(i));
                                }
                                ((ClassDetailsActivity)context).updateStudentList(finalStudents);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id)
                            {


                            }
                        }).create();
                AlertDialog alert = builder.create();
                alert.show();


                return false;
            }
        });

        return convertView;

    }

}
