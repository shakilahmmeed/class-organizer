package bd.edu.daffodilvarsity.classorganizer;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by musfiqus on 4/1/2017.
 */

public class RoutineLoader {

    private static final String SAVE_ROUTINE = "daydata.dat";
    private static final String SAVE_SECTION = "section.dat";
    private static final String SAVE_TERM = "term.dat";
    private int level;
    private int term;
    private String section;
    private Context context;
    private File file;

    public RoutineLoader(int level, int term, String section, Context context) {
        this.level = level;
        this.term = term;
        this.section = section;
        this.context = context;
        file = new File(context.getFilesDir(), SAVE_ROUTINE);
    }

    public RoutineLoader(Context context) {
        this.context = context;
    }

    private int setSemester() {
        if (this.level == 0) {
            return 1 + this.term;
        } else if (this.level == 1) {
            return 4 + this.term;
        } else if (this.level == 2) {
            return 7 + this.term;
        } else {
            return 10 + this.term;
        }
    }

    private ArrayList<String> courseCodeGenerator(int semester) {
        if (semester == 1) {
            String[] semesterData = context.getResources().getStringArray(R.array.semester_one);
            return new ArrayList<>(Arrays.asList(semesterData));
        } else if (semester == 2) {
            String[] semesterData = context.getResources().getStringArray(R.array.semester_two);
            return new ArrayList<>(Arrays.asList(semesterData));
        } else if (semester == 3) {
            String[] semesterData = context.getResources().getStringArray(R.array.semester_three);
            return new ArrayList<>(Arrays.asList(semesterData));
        } else if (semester == 4) {
            String[] semesterData = context.getResources().getStringArray(R.array.semester_four);
            return new ArrayList<>(Arrays.asList(semesterData));
        } else if (semester == 5) {
            String[] semesterData = context.getResources().getStringArray(R.array.semester_five);
            return new ArrayList<>(Arrays.asList(semesterData));
        } else if (semester == 6) {
            String[] semesterData = context.getResources().getStringArray(R.array.semester_six);
            return new ArrayList<>(Arrays.asList(semesterData));
        } else if (semester == 7) {
            String[] semesterData = context.getResources().getStringArray(R.array.semester_seven);
            return new ArrayList<>(Arrays.asList(semesterData));
        } else if (semester == 8) {
            String[] semesterData = context.getResources().getStringArray(R.array.semester_eight);
            return new ArrayList<>(Arrays.asList(semesterData));
        } else if (semester == 9) {
            String[] semesterData = context.getResources().getStringArray(R.array.semester_nine);
            return new ArrayList<>(Arrays.asList(semesterData));
        } else if (semester == 10) {
            String[] semesterData = context.getResources().getStringArray(R.array.semester_ten);
            return new ArrayList<>(Arrays.asList(semesterData));
        } else if (semester == 11) {
            String[] semesterData = context.getResources().getStringArray(R.array.semester_eleven);
            return new ArrayList<>(Arrays.asList(semesterData));
        } else if (semester == 12) {
            String[] semesterData = context.getResources().getStringArray(R.array.semester_twelve);
            return new ArrayList<>(Arrays.asList(semesterData));
        } else {
            Log.e("RoutineLoader", "Error in courseCodeGenerator");
            return null;
        }
    }

    public boolean loadRoutine() {
        //Initializing DB Helper
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        PrefManager prefManager = new PrefManager(context);

        //Generating course codes from generated semester
        ArrayList<String> courseCodes = courseCodeGenerator(setSemester());
        Log.e("COURSECODE", "SIZE " + courseCodes.size());

        ArrayList<DayData> mDayData = db.getDayData(courseCodes, section);
        Log.e("LOADDAYDATA", "SIZE " + mDayData.size());
        prefManager.saveDayData(mDayData);
        //returning if loading was successful or not
        return mDayData.size() <= 0;
    }

//    public void writeRoutine(ArrayList<DayData> dayDatas) {
//        Log.e("WRITE", "SIZE "+dayDatas.size());
//
//        try {
//            FileOutputStream fos = new FileOutputStream(file, false);
//            ObjectOutputStream oos = new ObjectOutputStream(fos);
//            oos.writeObject(dayDatas);
//            Log.e("WRITE", "CALLED");
//            oos.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public ArrayList<DayData> readRoutine() {
//        ArrayList<DayData> loadedData = new ArrayList<>();
//        try {
//            FileInputStream fis = context.openFileInput(SAVE_ROUTINE);
//            ObjectInputStream ois = new ObjectInputStream(fis);
//            loadedData = (ArrayList<DayData>) ois.readObject();
//            Log.e("READROUTINE", "SIZE: "+loadedData.size());
//            ois.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return loadedData;
//    }
}