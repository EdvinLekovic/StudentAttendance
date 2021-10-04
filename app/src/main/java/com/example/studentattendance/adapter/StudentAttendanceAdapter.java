//package com.example.studentattendance.adapter;
//
//import android.content.Context;
//import android.os.Build;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.RequiresApi;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.studentattendance.R;
//import com.example.studentattendance.enumeration.Type;
//import com.example.studentattendance.model.Professor;
//import com.example.studentattendance.model.Student;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//public class StudentAttendanceAdapter extends RecyclerView.Adapter<StudentAttendanceAdapter.StudentViewHolder> {
//
//    //private List<Student> studentList;
//    private HashMap<String, List<Student>> studentsPerSubjects;
//    private Professor professor;
//    private ViewGroup parent;
//    private Context context;
//    private FirebaseDatabase firebaseDatabase;
//    private int count;
//    private String subject;
//    private String subjectSearch;
//
//    public StudentAttendanceAdapter(Professor professor) {
//        this.professor = professor;
//        this.firebaseDatabase = FirebaseDatabase.getInstance();
//        //this.studentList = new ArrayList<>();
//        this.studentsPerSubjects = new HashMap<>();
//        this.count = 0;
//        this.subject = professor.getSubjectsTeaching().get(0);
//        addingStudents();
//    }
//
//    private void addingStudents() {
//        firebaseDatabase.getReference("users").addValueEventListener(new ValueEventListener() {
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot item : snapshot.getChildren()) {
//                    System.out.println(item.getKey());
//                    String type = item.child("type").getValue(String.class);
//                    if (type.equals(Type.STUDENT.toString())) {
//                        Student student = item.getValue(Student.class);
//                        addingStudentsToMap(student);
//                        count++;
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//    private void addingStudentsToMap(Student student) {
//        for (int i = 0; i < student.getSubjectsListening().size(); i++) {
//            if (studentsPerSubjects.isEmpty() ||
//                    !studentsPerSubjects.containsKey(student.getSubjectsListening().get(i))) {
//                List<Student> students = new ArrayList<>();
//                students.add(student);
//                studentsPerSubjects.put(student.getSubjectsListening().get(i), students);
//            } else {
//                studentsPerSubjects.get(student.getSubjectsListening().get(i)).add(student);
//            }
//        }
//    }
//
//    public List<Student> getStudentsBySubject(String subject){
//        if(studentsPerSubjects.containsKey(subject)) {
//            return studentsPerSubjects.get(subject);
//        }
//        return new ArrayList<>();
//    }
//
//    @NonNull
//    @Override
//    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        this.parent = parent;
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_row, null);
//        return new StudentViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
//        holder.bindData(position,professor.getSubjectsTeaching().get(0));
//    }
//
//    public void setSubjectSearch(String subject){
//        this.subjectSearch = subject;
//    }
//
//    @Override
//    public int getItemCount() {
//        return count;
//    }
//
//    class StudentViewHolder extends RecyclerView.ViewHolder {
//
//        private TextView index, name, surname, attendance;
//
//        public StudentViewHolder(@NonNull View itemView) {
//            super(itemView);
//            this.index = itemView.findViewById(R.id.indexView);
//            this.name = itemView.findViewById(R.id.nameView);
//            this.surname = itemView.findViewById(R.id.surnameView);
//            this.attendance = itemView.findViewById(R.id.attendanceView);
//        }
//
//        public void bindData(int position,String subject) {
//            List<Student> students = getStudentsBySubject(subjectSearch != null ? subjectSearch : subject);
//            if(!students.isEmpty()) {
//                Student student = students.get(position);
//                index.setText(student.getIndex());
//                name.setText(student.getName());
//                surname.setText(student.getLastName());
//                attendance.setText(student.isLogged() ? "Present" : "Absent");
//            }
//            else {
//                index.setText("");
//                name.setText("");
//                surname.setText("");
//                attendance.setText("");
//            }
//        }
//
//
//    }
//
//
//}
