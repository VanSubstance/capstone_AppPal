package com.capstone.apppal.view.item;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.capstone.apppal.R;

import java.util.HashMap;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

  private HashMap<String, Object>[] localDataSet;
  public final static int BASE_TYPE = 0;
  public final static int SECOND_TYPE = 1;


  /**
   * Provide a reference to the type of views that you are using
   * (custom ViewHolder).
   */
  public static class ViewHolder extends RecyclerView.ViewHolder {
    private final ItemBase itemView;

    public ViewHolder(View view) {
      super(view);
      // Define click listener for the ViewHolder's View

      itemView = new ItemBase(view.getContext());
    }

    public ItemBase getItemView() {
      return itemView;
    }
  }

  /**
   * Initialize the dataset of the Adapter.
   *
   * @param dataSet String[] containing the data to populate views to be used
   *                by RecyclerView.
   */
  public RecyclerViewAdapter(HashMap<String, Object>[] dataSet) {
    localDataSet = dataSet;
  }

  // Create new views (invoked by the layout manager)
  @Override
  public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    // Create a new view, which defines the UI of the list item
    View view = null;
    view = LayoutInflater.from(viewGroup.getContext())
      .inflate(R.layout.item_base, viewGroup, false);

    return new ViewHolder(view);
  }

  // Replace the contents of a view (invoked by the layout manager)
  @Override
  public void onBindViewHolder(ViewHolder viewHolder, final int position) {

    // Get element from your dataset at this position and replace the
    // contents of the view with that element
    viewHolder.getItemView().setData(localDataSet[position]);
  }

  // Return the size of your dataset (invoked by the layout manager)
  @Override
  public int getItemCount() {
    return localDataSet.length;
  }
}