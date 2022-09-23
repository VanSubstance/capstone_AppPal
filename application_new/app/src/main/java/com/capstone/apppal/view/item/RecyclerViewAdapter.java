package com.capstone.apppal.view.item;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.apppal.OnBoardingActivity;
import com.capstone.apppal.R;
import com.capstone.apppal.view.fragments.ListFragment;

import java.util.HashMap;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private HashMap<String, Object>[] localDataSet;
  public final static int BASE_TYPE = 0;
  public final static int SECOND_TYPE = 1;

  private OnBoardingActivity onBoardingActivity;

  /**
   * Initialize the dataset of the Adapter.
   *
   * @param dataSet String[] containing the data to populate views to be used
   *                by RecyclerView.
   */
  public RecyclerViewAdapter(HashMap<String, Object>[] dataSet, OnBoardingActivity onBoardingActivity) {
    localDataSet = dataSet;
    this.onBoardingActivity = onBoardingActivity;
  }

  // Create new views (invoked by the layout manager)
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    // Create a new view, which defines the UI of the list item
    if (viewType == BASE_TYPE) {
      View view = LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.holder_base, viewGroup, false);
      BaseViewHolder holder = new BaseViewHolder(view);
      return holder;
    } else {
      View view = LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.holder_base, viewGroup, false);
      BaseViewHolder holder = new BaseViewHolder(view);
      return holder;
    }
  }

  // Replace the contents of a view (invoked by the layout manager)
  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
    // Get element from your dataset at this position and replace the
    // contents of the view with that element
    if (viewHolder instanceof BaseViewHolder) {
      ((BaseViewHolder) viewHolder).setData(localDataSet[position]);
    } else {
      ((BaseViewHolder) viewHolder).setData(localDataSet[position]);
    }
  }

  // Return the size of your dataset (invoked by the layout manager)
  @Override
  public int getItemCount() {
    return localDataSet.length;
  }

  @Override
  public int getItemViewType(int position) {
    return (int) localDataSet[position].get("viewType");
  }

  public HashMap<String, Object> getItemData(int position) {
    return localDataSet[position];
  }

  static class BaseViewHolder extends RecyclerView.ViewHolder {
    private TextView mTitleText;
    private TextView mDescText;
    private HashMap<String, Object> data;

    public BaseViewHolder(@NonNull View itemView) {
      super(itemView);
      init();
    }

    private void init() {
      mTitleText = itemView.findViewById(R.id.text_title);
      mDescText = itemView.findViewById(R.id.text_desc);
    }

    private void setTitleText(String text) {
      mTitleText.setText(text);
    }

    private void setDescText(String text) {
      mDescText.setText(text);
    }

    public void setData(HashMap<String, Object> data) {
      this.data = data;
      setTitleText((String) data.get("title"));
      setDescText((String) data.get("desc"));
      itemView.setOnClickListener((View.OnClickListener) data.get("func"));
    }
  }
}