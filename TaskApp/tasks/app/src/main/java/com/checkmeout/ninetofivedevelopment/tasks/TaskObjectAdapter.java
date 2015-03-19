package com.checkmeout.ninetofivedevelopment.tasks;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by robertgross on 3/19/15.
 */
public class TaskObjectAdapter extends  RecyclerView.Adapter<TaskObjectAdapter.TaskCardViewHolder>  {

    private List<TaskObject> tasks = null;

    public void setData(List<TaskObject> details) {
        this.tasks = details;
    }

    public void add(TaskObject taskObject) {
        tasks.add(taskObject);
        notifyItemInserted(tasks.size());
    }

    @Override
    public void onBindViewHolder(TaskCardViewHolder contactViewHolder, int i) {
        TaskObject ci = tasks.get(i);
        contactViewHolder.vName.setText(ci.getName());
        contactViewHolder.vColor.setText(ci.getColor());
    }

    @Override
    public TaskCardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_layout, viewGroup, false);

        return new TaskCardViewHolder(itemView);
    }

    @Override
    public int getItemCount() {

        if (tasks == null) {
            return 0;
        }
        return tasks.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    public static class TaskCardViewHolder extends RecyclerView.ViewHolder {

        protected TextView vName;
        protected TextView vColor;

        public TaskCardViewHolder(View v) {
            super(v);
            vName =  (TextView) v.findViewById(R.id.title);
            vColor = (TextView)  v.findViewById(R.id.txtColor);
        }
    }
}
