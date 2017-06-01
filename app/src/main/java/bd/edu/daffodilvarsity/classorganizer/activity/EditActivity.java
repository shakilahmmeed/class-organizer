package bd.edu.daffodilvarsity.classorganizer.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.polaric.colorful.Colorful;
import org.polaric.colorful.ColorfulActivity;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classorganizer.data.DayData;
import bd.edu.daffodilvarsity.classorganizer.utils.PrefManager;
import bd.edu.daffodilvarsity.classorganizer.R;

public class EditActivity extends ColorfulActivity {

    private PrefManager prefManager;
    private TextView courseCodeText;
    private EditText courseTitle;
    private EditText editInitial;
    private EditText editRoom;
    private Spinner weekDaySpinner;
    private Spinner startTimeSpinner;
    private Spinner endTimeSpinner;
    private int position = -1;
    private ArrayList<DayData> dayDatas;
    private DayData dayData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Colorful.applyTheme(this);
        setContentView(R.layout.activity_edit);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_modify);
        setSupportActionBar(toolbar);
        findViewById(R.id.modify_appbar_layout).bringToFront();
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        prefManager = new PrefManager(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            dayData = extras.getParcelable("DAYDATA");
        }
        dayDatas = prefManager.getSavedDayData();
        for (int i = 0; i < dayDatas.size(); i++) {
            if (dayData.equals(dayDatas.get(i))) {
                position = i;
            }
        }

        //Setting course current daydatas
        setupCurrentDay();
        // Making navigation bar colored
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(getResources().getColor(Colorful.getThemeDelegate().getPrimaryColor().getColorRes()));
        }
    }

    private void setupCurrentDay() {
        courseCodeText = (TextView) findViewById(R.id.course_code_title);
        courseCodeText.setText(dayData.getCourseCode());

        courseTitle = (EditText) findViewById(R.id.edit_course_title);
        courseTitle.setText(dayData.getCourseTitle());

        editInitial = (EditText) findViewById(R.id.edit_initial);
        editInitial.setText(dayData.getTeachersInitial());

        editRoom = (EditText) findViewById(R.id.edit_room);
        editRoom.setText(dayData.getRoomNo());

        //Term spinner
        weekDaySpinner = (Spinner) findViewById(R.id.edit_week_day);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> termAdapter = ArrayAdapter.createFromResource(this, R.array.weekdays, R.layout.spinner_row);
        //Getting the position of current day in spinner
        int spinnerPos = termAdapter.getPosition(dayData.getDay().substring(0, 1).toUpperCase() + dayData.getDay().substring(1).toLowerCase());
        // Specify the layout to use when the list of choices appears
        termAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        weekDaySpinner.setAdapter(termAdapter);
        weekDaySpinner.setSelection(spinnerPos);

        String[] startEndTime = timeSplitter(dayData.getTime());
        String startTime = startEndTime[0];
        startTime = startTime.substring(0, startTime.length() - 2) + startTime.substring(startTime.length() - 2, startTime.length() - 1);
        String endTime = startEndTime[1];
        endTime = endTime.substring(1, endTime.length());
        //Start time spinner
        startTimeSpinner = (Spinner) findViewById(R.id.modify_time_start);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> startTimeAdapter = ArrayAdapter.createFromResource(this, R.array.start_time, R.layout.spinner_row);
        //Getting the position of start time in spinner
        int startTimePos = startTimeAdapter.getPosition(startTime);
        startTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startTimeSpinner.setAdapter(startTimeAdapter);
        startTimeSpinner.setSelection(startTimePos);

        //End time spinner
        endTimeSpinner = (Spinner) findViewById(R.id.modify_time_end);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> endTimeAdapter = ArrayAdapter.createFromResource(this, R.array.end_time, R.layout.spinner_row);
        //Getting the position of start time in spinner
        int endTimePos = endTimeAdapter.getPosition(endTime);
        endTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        endTimeSpinner.setAdapter(endTimeAdapter);
        endTimeSpinner.setSelection(endTimePos);
    }

    private String timeJoiner(String startTime, String endTime) {
        return startTime + " - " + endTime;
    }

    private String[] timeSplitter(String time) {
        return time.split("-");
    }

    private DayData getEditedDay() {
        String newCourseTitle = courseTitle.getText().toString();
        String courseCode = courseCodeText.getText().toString();
        String initial = editInitial.getText().toString();
        String room = editRoom.getText().toString();
        String time = timeJoiner(startTimeSpinner.getSelectedItem().toString(), endTimeSpinner.getSelectedItem().toString());
        String day = weekDaySpinner.getSelectedItem().toString();
        double timeWeight = timeWeight(startTimeSpinner.getSelectedItem().toString());
        return new DayData(courseCode, initial, prefManager.getSection(), prefManager.getLevel(), prefManager.getTerm(), room, time, day, timeWeight, newCourseTitle);
    }

    private double timeWeight(String startTime) {
        switch (startTime) {
            case "08.30 AM":
                return 1.0;
            case "10.00 AM":
                return 2.0;
            case "11.30 AM":
                return 3.0;
            case "01.00 PM":
                return 4.0;
            case "02.30 PM":
                return 5.0;
            case "04.00 PM":
                return 6.0;
            case "06.00 PM":
                return 7.0;
            case "07.30 PM":
                return 8.0;
            case "09.00 PM":
                return 9.0;
            case "09.00 AM":
                return 1.5;
            case "11.00 AM":
                return 2.5;
            case "03.00 PM":
                return 4.5;
            default:
                Log.e("EditActivity", "INVALID START TIME");
                return 0;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // create an action bar button, done
        getMenuInflater().inflate(R.menu.activity_modify_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.done_button) {
            //Saving the changed data
            DayData editedDay = getEditedDay();
            if (position > -1) {
                prefManager.saveModifiedData(editedDay, PrefManager.EDIT_DATA_TAG, false);
                dayDatas.set(position, editedDay);
            }
            prefManager.saveDayData(dayDatas);
            prefManager.saveReCreate(true);
            showSnackBar(this, "Saved");
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    //Method to display snackbar properly
    public void showSnackBar(Activity activity, String message) {
        View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
    }
}