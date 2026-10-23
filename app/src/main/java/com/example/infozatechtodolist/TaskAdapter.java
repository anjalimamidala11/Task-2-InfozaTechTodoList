package com.example.infozatechtodolist;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final ArrayList<TodoTask> tasks;
    private final OnTaskChangeListener listener;

    public interface OnTaskChangeListener {
        void onTasksChanged();
    }

    public TaskAdapter(ArrayList<TodoTask> tasks, OnTaskChangeListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TodoTask task = tasks.get(position);

        holder.tvTaskTitle.setText(task.getTitle());

        holder.checkboxTask.setOnCheckedChangeListener(null);
        holder.checkboxTask.setChecked(task.isCompleted());

        if (task.isCompleted()) {
            holder.tvTaskTitle.setPaintFlags(
                    holder.tvTaskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.tvTaskTitle.setPaintFlags(
                    holder.tvTaskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        holder.checkboxTask.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            notifyItemChanged(holder.getBindingAdapterPosition());
            listener.onTasksChanged();
        });

        holder.tvTaskTitle.setOnClickListener(v -> {
            android.widget.EditText input = new android.widget.EditText(v.getContext());
            input.setText(task.getTitle());
            input.setSingleLine(true);
            input.setSelection(input.getText().length());

            new AlertDialog.Builder(v.getContext())
                    .setTitle("Edit Task")
                    .setView(input)
                    .setPositiveButton("Save", (dialog, which) -> {
                        String newTitle = input.getText().toString().trim();

                        if (!newTitle.isEmpty()) {
                            task.setTitle(newTitle);
                            notifyItemChanged(holder.getBindingAdapterPosition());
                            listener.onTasksChanged();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        holder.btnDeleteTask.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        int currentPosition = holder.getBindingAdapterPosition();

                        if (currentPosition != RecyclerView.NO_POSITION) {
                            tasks.remove(currentPosition);
                            notifyItemRemoved(currentPosition);
                            listener.onTasksChanged();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkboxTask;
        TextView tvTaskTitle;
        Button btnDeleteTask;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            checkboxTask = itemView.findViewById(R.id.checkboxTask);
            tvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
            btnDeleteTask = itemView.findViewById(R.id.btnDeleteTask);
        }
    }
}

