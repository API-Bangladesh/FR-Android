package com.shamim.frremoteattendence.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.shamim.frremoteattendence.R;
import com.shamim.frremoteattendence.model_class.door_loc_model_class;

import java.util.ArrayList;
import java.util.List;

public class door_loc_adapter extends RecyclerView.Adapter<door_loc_adapter.myviewholder> {
    private final String TAG = "door_loc_adapter";
    Context context;
    List<door_loc_model_class> data = new ArrayList<>();


    public door_loc_adapter(List<door_loc_model_class> data, Context context) {
        this.data = data;
        this.context = context;
    }


    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<door_loc_model_class> newData) {
        data.clear();
        data=newData;
        data.addAll(newData);
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.staff_data_row, parent, false);
        return new myviewholder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        door_loc_model_class model_class_data = data.get(position);

        holder.inTime.setText(model_class_data.getInTime());
        holder.outTime.setText(model_class_data.getOutTime());
        holder.date.setText("Date: "+model_class_data.getDate());
        holder.totalwork.setText(model_class_data.getTotal_worked_hour_in_minutes());
        holder.cumulative.setText(model_class_data.getCumulative_work_hour());

        Log.d(TAG, "worked_hour_in_minutes: "+model_class_data.getInTime());
        if ("0".equals(model_class_data.getTotal_worked_hour_in_minutes())) {
            Toast.makeText(context, ""+model_class_data.getInTime(), Toast.LENGTH_SHORT).show();

            holder.itemView.setBackgroundResource(R.drawable.custom_item_background_color); // Use your custom drawable resource

            //holder.inTime.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));
            //holder.itemView.setBackgroundResource(R.drawable.button_backround); // Replace with your custom drawable

            // Set the background color to red for rows with "0.00" total work hours
            //holder.itemView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light));
        } else {
            // Set the default background color for other rows
            //holder.itemView.setBackgroundColor(context.getResources().getColor(android.R.color.white));
            holder.inTime.setTextColor(context.getResources().getColor(android.R.color.black));


        }

//        if (model_class_data.getE_id() == null) {
//            // Display a message or an empty view for null data
//
//            Toast.makeText(context, "Data Is Null", Toast.LENGTH_SHORT).show();
//        } else {
//            LocalDate currentDate = LocalDate.now();
//
//            LocalDate dataDate = LocalDate.parse(model_class_data.getDate());
//            if (dataDate.isEqual(currentDate)) {
//                if (model_class_data.getE_id() != null) {
//                    holder.e_ID.setText(model_class_data.getE_id());
//                    holder.name.setText(model_class_data.getName());
//                    holder.inTime.setText(model_class_data.getInTime());
//                    holder.outTime.setText(model_class_data.getOutTime());
//                    holder.date.setText(model_class_data.getDate());
//                } else {
//                    Toast.makeText(context, "Data Is Null", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class myviewholder extends RecyclerView.ViewHolder {
        TextView   date, inTime, outTime,totalwork,cumulative;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            inTime = (TextView) itemView.findViewById(R.id.intime_spacific);
            outTime = (TextView) itemView.findViewById(R.id.outTime_spacific);
            date = (TextView) itemView.findViewById(R.id.date_spacific);
            totalwork=(TextView) itemView.findViewById(R.id.worktime_spacific);
            cumulative=(TextView) itemView.findViewById(R.id.cumulative_spacific);



        }
    }
}
