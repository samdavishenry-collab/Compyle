package com.ai.soiltester.ui.results;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ai.soiltester.R;
import com.ai.soiltester.models.Crop;

import java.util.ArrayList;
import java.util.List;

public class CropRecommendationsAdapter extends RecyclerView.Adapter<CropRecommendationsAdapter.CropViewHolder> {

    private List<Crop> crops = new ArrayList<>();

    @NonNull
    @Override
    public CropViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_crop_recommendation, parent, false);
        return new CropViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CropViewHolder holder, int position) {
        Crop crop = crops.get(position);
        holder.bind(crop);
    }

    @Override
    public int getItemCount() {
        return crops.size();
    }

    public void updateCrops(List<Crop> newCrops) {
        crops.clear();
        crops.addAll(newCrops);
        notifyDataSetChanged();
    }

    static class CropViewHolder extends RecyclerView.ViewHolder {
        TextView textCropEmoji;
        TextView textCropName;
        TextView textCropDescription;
        TextView textGrowingPeriod;
        TextView textWaterNeeds;
        TextView textSuitability;

        public CropViewHolder(@NonNull View itemView) {
            super(itemView);
            textCropEmoji = itemView.findViewById(R.id.text_crop_emoji);
            textCropName = itemView.findViewById(R.id.text_crop_name);
            textCropDescription = itemView.findViewById(R.id.text_crop_description);
            textGrowingPeriod = itemView.findViewById(R.id.text_growing_period);
            textWaterNeeds = itemView.findViewById(R.id.text_water_needs);
            textSuitability = itemView.findViewById(R.id.text_suitability);
        }

        public void bind(Crop crop) {
            textCropEmoji.setText(crop.getEmoji());
            textCropName.setText(crop.getName());
            textCropDescription.setText(crop.getDescription());
            textGrowingPeriod.setText("Growing period: " + crop.getGrowingPeriodText());
            textWaterNeeds.setText("Water needs: " + crop.getWaterNeed());

            // Set suitability text and color
            String suitability = crop.getSuitabilityText();
            textSuitability.setText(suitability + " match");

            // Set color based on suitability
            int color;
            switch (suitability.toLowerCase()) {
                case "excellent":
                    color = itemView.getContext().getResources().getColor(R.color.success, null);
                    break;
                case "good":
                    color = itemView.getContext().getResources().getColor(R.color.green_primary, null);
                    break;
                case "fair":
                    color = itemView.getContext().getResources().getColor(R.color.warning, null);
                    break;
                default:
                    color = itemView.getContext().getResources().getColor(R.color.error, null);
                    break;
            }
            textSuitability.setTextColor(color);
        }
    }
}