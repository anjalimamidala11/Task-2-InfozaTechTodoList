
package com.example.infozatechtodolist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText etTask;
    private ArrayList<TodoTask> tasks;
    private TaskAdapter adapter;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etTask = findViewById(R.id.etTask);
        Button btnAddTask = findViewById(R.id.btnAddTask);
        RecyclerView recyclerTasks = findViewById(R.id.recyclerTasks);

        preferences = getSharedPreferences("TodoStorage", MODE_PRIVATE);
        tasks = new ArrayList<>();

        loadTasks();

        adapter = new TaskAdapter(tasks, this::saveTasks);
        recyclerTasks.setLayoutManager(new LinearLayoutManager(this));
        recyclerTasks.setAdapter(adapter);

        btnAddTask.setOnClickListener(v -> {
            String title = etTask.getText().toString().trim();

            if (TextUtils.isEmpty(title)) {
                etTask.setError("Enter a task");
                return;
            }

            tasks.add(new TodoTask(title));
            adapter.notifyItemInserted(tasks.size() - 1);
            saveTasks();

            etTask.setText("");
            recyclerTasks.scrollToPosition(tasks.size() - 1);

            Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show();
        });
    }

    private void saveTasks() {
        JSONArray array = new JSONArray();

        try {
            for (TodoTask task : tasks) {
                JSONObject object = new JSONObject();
                object.put("title", task.getTitle());
                object.put("completed", task.isCompleted());
                array.put(object);
            }

            preferences.edit()
                    .putString("tasks", array.toString())
                    .apply();

        } catch (Exception e) {
            Toast.makeText(this, "Could not save tasks", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadTasks() {
        String savedData = preferences.getString("tasks", "[]");

        try {
            JSONArray array = new JSONArray(savedData);

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);

                TodoTask task = new TodoTask(object.getString("title"));
                task.setCompleted(object.getBoolean("completed"));

                tasks.add(task);
            }
        } catch (Exception e) {
            tasks.clear();
        }
    }
}
