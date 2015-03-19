package com.checkmeout.ninetofivedevelopment.tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

//http://stackoverflow.com/questions/27708503/how-to-handle-swipe-to-remove-on-recyclerview-correctly

public class TaskActivity extends ActionBarActivity {
    final Context context = this;
    private Realm realm;
    TaskObjectAdapter mAdapter;
    List<TaskObject> tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);


        realm = Realm.getInstance(this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToRecyclerView(recList);

        fab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    displayDialog();
                    return true;
                }
                return false;
            }
        });

        mAdapter = new TaskObjectAdapter();
        mAdapter.notifyDataSetChanged();
        createList(2);
        mAdapter.setData(getAllTasks());

        recList.setAdapter(mAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tasks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private List<TaskObject> getAllTasks(){
        // Build the query looking at all users:
        RealmQuery<TaskObject> query = realm.where(TaskObject.class);
        RealmResults<TaskObject> result = query.findAll();
        result.sort("completed", RealmResults.SORT_ORDER_DESCENDING);

        tasks = new ArrayList<TaskObject>();

        for(TaskObject task : result)
            tasks.add(task);

        return tasks;
    }

    private void createList(int size) {

        for (int i=1; i <= size; i++) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    TaskObject task = realm.createObject(TaskObject.class); // Create a new object
                    task.setName("Bye ®");
                    task.setColor("#6889ff");
                    task.setCompleted(false);
                }
            });
        }

    }

    public void displayDialog () {
        AlertDialog.Builder builder;
        final AlertDialog dialog;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.create_new_dialog, (ViewGroup) findViewById(R.id.root_layout));

        final EditText taskToCreate = (EditText) layout.findViewById(R.id.editText);
        Button confirmButton = (Button) layout.findViewById(R.id.confirmButton);

        final RadioGroup radioGroup = (RadioGroup) layout.findViewById(R.id.radioGroup);


        builder = new AlertDialog.Builder(context);
        builder.setView(layout);
        dialog = builder.create();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save selected shit

                if (!taskToCreate.getText().toString().equals("") && radioGroup.getCheckedRadioButtonId() != -1) {
                    int radioButtonID = radioGroup.getCheckedRadioButtonId();
                    View radioButton = radioGroup.findViewById(radioButtonID);
                    int index = radioGroup.indexOfChild(radioButton);   //index of selected radio button

                    String color = radioButton.getTag().toString();  //dump color as hex string "#FFFFFF" to task color
                    String task = taskToCreate.getText().toString();  //text to dump to db

                    System.out.println("Task: " + task + " - color: " + color);
                    //call redraw for tableview? before dismiss?

                    realm.beginTransaction();
                    TaskObject taskObj = realm.createObject(TaskObject.class); // Create a new object
                    taskObj.setName(task);
                    taskObj.setColor(color);
                    taskObj.setCompleted(false);
                    mAdapter.add(taskObj);
                    realm.commitTransaction();


                    dialog.dismiss();

                    Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
                }
                else {
                    //do nothing??
                }
            }
        });

        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        //hide fab on show??
        dialog.show();
    }
}