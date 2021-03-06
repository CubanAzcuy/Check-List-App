package com.checkmeout.ninetofivedevelopment.tasks;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.pavlospt.CircleView;

import java.util.Collections;
import java.util.List;

/**
 * Created by robertgross on 3/19/15.
 */
public class TaskObjectAdapter extends  RecyclerView.Adapter<TaskObjectAdapter.TaskCardViewHolder>  {

    private List<TaskObject> tasks = null;
    private TaskActivity.OnCellTouchListener onCellTouchListener;

    public TaskObjectAdapter(TaskActivity.OnCellTouchListener onCellTouchListener) {
        this.tasks = tasks;
        this.onCellTouchListener = onCellTouchListener;
    }
    public void setData(List<TaskObject> details) {
        this.tasks = details;
    }

    public void add(TaskObject taskObject) {
        tasks.add(taskObject);
        notifyItemInserted(tasks.size());
    }

    public void remove(int position) {
        tasks.remove(position);
        notifyItemRemoved(position);
    }

    public void swapPositions(int from, int to) {
        Collections.swap(tasks, from, to);
    }

    @Override
    public void onBindViewHolder(TaskCardViewHolder contactViewHolder, int i) {
        TaskObject ci = tasks.get(i);
        contactViewHolder.vName.setText(ci.getName());
        contactViewHolder.vColor.setFillColor(Color.parseColor(ci.getColor()));
        contactViewHolder.vColor.setStrokeColor(Color.parseColor(ci.getColor()));

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


    public class TaskCardViewHolder extends RecyclerView.ViewHolder {

        protected TextView vName;
        protected CircleView vColor;

        public TaskCardViewHolder(View v) {
            super(v);
            vName =  (TextView) v.findViewById(R.id.title);
            vColor = (CircleView) v.findViewById(R.id.color);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCellTouchListener.onCardViewTap(view, getPosition());
                }
            });
        }
    }

}
