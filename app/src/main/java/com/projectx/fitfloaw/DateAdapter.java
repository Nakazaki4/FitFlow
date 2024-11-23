package com.projectx.fitfloaw;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.projectx.fitfloaw.localdatabase.DatabaseRepository;
import com.projectx.fitfloaw.localdatabase.WalkingSession;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {
    private static final String TAG = "DateAdapter";
    private int selectedPosition = RecyclerView.NO_POSITION;
    private MainActivity mainActivity;
    private List<DateModel> dateList;
    private final Context context;

    public DateAdapter(Context context, MainActivity mainActivity, List<DateModel> dateList) {
        this.dateList = dateList;
        this.context = context;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_item, parent, false);
        return new DateViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        DateModel dateModel = dateList.get(position);
        holder.bind(dateModel, position == selectedPosition);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int clickedPosition = holder.getAdapterPosition();
                if (clickedPosition != RecyclerView.NO_POSITION) {
                    toggleSelection(clickedPosition);

                    long clickedDateMillis = dateModel.getLocalDate()
                            .atStartOfDay(ZoneOffset.UTC)
                            .toInstant()
                            .toEpochMilli();

                    WalkingSession walkingSession = DatabaseRepository.getInstance(context).getWalkingSessionByDate(clickedDateMillis);

                    LocalDate today = LocalDate.now(ZoneOffset.UTC);
                    if (dateModel.getLocalDate().isEqual(today)) {
                        // Resume LiveData observers if today's date is clicked and if not already resumed
                        if (!DatabaseRepository.getInstance(context).getMinutesWalked().hasObservers() &&
                                !DatabaseRepository.getInstance(context).getStepsWalked().hasObservers() &&
                                !DatabaseRepository.getInstance(context).getCaloriesBurned().hasObservers()) {
                            resumeLiveDataObservers();
                        }
                    } else {
                        // Stop LiveData observers if it's not today's date
                        stopLiveDataObservers();
                        if(walkingSession != null) {
                            mainActivity.staticUiElementsBinding(walkingSession.getSteps(), walkingSession.getMinutes(), walkingSession.getCalories());
                        }else {
                            Toast.makeText(context, "No data available for this date", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    private void toggleSelection(int position) {
        if (position == selectedPosition) {
            // Deselect if clicking the same item
            selectedPosition = RecyclerView.NO_POSITION;
        } else {
            // Select new item
            selectedPosition = position;
        }
        notifyDataSetChanged();
    }

    private void stopLiveDataObservers() {
        mainActivity.stopLiveDataObservers();
    }

    private void resumeLiveDataObservers() {
        mainActivity.startLiveDataObservers();
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateTextView;
        private final TextView dayTextView;
        private final Context context;

        public DateViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            dateTextView = itemView.findViewById(R.id.dateText);
            dayTextView = itemView.findViewById(R.id.dayText);
        }

        public void bind(DateModel dateModel, boolean isSelected) {
            dateTextView.setText(dateModel.getDate());
            dayTextView.setText(dateModel.getDay());

            if (isSelected) {
                onDaySelected();
            } else if (dateModel.isSelected()) {
                onDayPreSelected();
            } else {
                onDayUnselected();
            }
        }

        private void onDaySelected() {
            ((CardView) itemView).setCardBackgroundColor(setColor(R.color.light_blue));
            dateTextView.setTextColor(Color.WHITE);
            dayTextView.setTextColor(Color.WHITE);
            itemView.setSelected(true);
        }

        private void onDayPreSelected() {
            ((CardView) itemView).setCardBackgroundColor(setColor(R.color.orange));
            dateTextView.setTextColor(Color.WHITE);
            dayTextView.setTextColor(Color.WHITE);
        }

        private void onDayUnselected() {
            ((CardView) itemView).setCardBackgroundColor(setColor(android.R.color.transparent));
            dateTextView.setTextColor(setColor(R.color.dark_blue));
            dayTextView.setTextColor(setColor(R.color.dark_blue));
            itemView.setSelected(false);
        }

        private int setColor(int color) {
            return context.getResources().getColor(color);
        }
    }
}
