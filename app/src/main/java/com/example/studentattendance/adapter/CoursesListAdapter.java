package com.example.studentattendance.adapter;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentattendance.R;
import com.example.studentattendance.model.Course;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CoursesListAdapter extends RecyclerView.Adapter<CoursesListAdapter.CoursesViewHolder> {


    private List<Course> courseList;
    private ViewGroup parent;
    private CourseClickListener courseClickListener;
    public CoursesListAdapter() {
        courseList = new ArrayList<>();
    }

    @NonNull
    @Override
    public CoursesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_row, null, false);
        return new CoursesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CoursesViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public void updateData(List<Course> courses) {
        this.courseList = courses;
        this.notifyDataSetChanged();
    }

    class CoursesViewHolder extends RecyclerView.ViewHolder {

        private TextView subjectName, dateStart, dateEnd,classRoom;
        private View view;

        public CoursesViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            subjectName = itemView.findViewById(R.id.course_name);
            dateStart = itemView.findViewById(R.id.startDate);
            dateEnd = itemView.findViewById(R.id.endDate);
            classRoom = itemView.findViewById(R.id.classRoom);
        }


        public void bindData(int position) {
            Course course = courseList.get(position);
            subjectName.setText(course.getSubject());
            Date start = course.getDateStart();
            @SuppressLint("DefaultLocale")
            String dateTime = String.format("%d.%d.%d",start.getDate(),start.getMonth()+1,start.getYear()+1900);
            dateStart.setText(dateTime);
            Date end = course.getDateEnd();
            @SuppressLint("DefaultLocale")
            String startDateString = String.format("%02d:%02d", start.getHours(), start.getMinutes());
            @SuppressLint("DefaultLocale")
            String endDateString = String.format("%02d:%02d", end.getHours(), end.getMinutes());
            String time = startDateString +" "+endDateString;
            dateEnd.setText(time);
            classRoom.setText(course.getClassRoom());
            view.setOnClickListener(v -> courseClickListener.onCourseClicked(getAdapterPosition()));
        }
    }

    public Course getCourse(int position){
        return courseList.get(position);
    }

    public interface CourseClickListener {
        void onCourseClicked(int position);
    }

    public void setCourseClickListener(CourseClickListener courseClickListener){
        this.courseClickListener = courseClickListener;
    }



}
