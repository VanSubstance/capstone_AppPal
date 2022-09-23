package com.capstone.apppal.view.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.apppal.OnBoardingActivity;
import com.capstone.apppal.R;
import com.capstone.apppal.utils.GlobalState;
import com.capstone.apppal.view.item.RecyclerViewAdapter;

import java.util.HashMap;

public class ListFragment extends Fragment {
  public final static int CREATE_OPTION_MODE = 0;
  public final static int ENTER_OPTION_MODE = 1;
  public final static int ROOM_OPTION_MODE = 2;

  private int currentOption;
  private RecyclerView mListView;
  private RecyclerViewAdapter mRecyclerViewAdapter;
  private RecyclerView.LayoutManager mLayoutManager;
  private HashMap<String, Object>[] mDataSet;

  public ListFragment() {
    super();
  }

  public ListFragment(int optionMode) {
    super();
    currentOption = optionMode;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Initialize dataset, this data would usually come from a local content provider or
    // remote server.
    initDataset();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_list, container, false);
    init(rootView);

    return rootView;
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    // Save currently selected layout manager.
    super.onSaveInstanceState(savedInstanceState);
  }

  public ListFragment newInstance() {
    return new ListFragment();
  }

  private void init(View rootView) {
    initDataset();

    mRecyclerViewAdapter = new RecyclerViewAdapter(mDataSet, ((OnBoardingActivity) getActivity()));
    mListView = rootView.findViewById(R.id.list_item);
    mListView.setAdapter(mRecyclerViewAdapter);

    mLayoutManager = new LinearLayoutManager(getActivity());
    mListView.setLayoutManager(mLayoutManager);
    mListView.scrollToPosition(0);
  }

  private void initDataset() {
    switch (currentOption) {
      case CREATE_OPTION_MODE:
        mDataSet = new HashMap[2];
        mDataSet[0] = new HashMap<>();
        mDataSet[0].put("viewType", RecyclerViewAdapter.BASE_TYPE);
        mDataSet[0].put("title", "그림방 생성 (신규)");
        mDataSet[0].put("desc", "신규 그림방을 만듭니다!");
        mDataSet[0].put("func", new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            ((OnBoardingActivity) getActivity()).enterDrawingRoom();
          }
        });
        mDataSet[1] = new HashMap<>();
        mDataSet[1].put("viewType", RecyclerViewAdapter.BASE_TYPE);
        mDataSet[1].put("title", "그림방 불러오기 (기존)");
        mDataSet[1].put("desc", "기존 그림방을 불러옵니다!");
        mDataSet[1].put("func", new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            ((OnBoardingActivity) getActivity()).goToListFragment(ListFragment.ENTER_OPTION_MODE);
          }
        });
        break;
      case ENTER_OPTION_MODE:
        mDataSet = new HashMap[2];
        mDataSet[0] = new HashMap<>();
        mDataSet[0].put("viewType", RecyclerViewAdapter.SECOND_TYPE);
        mDataSet[0].put("title", "내 그림방 리스트 보기");
        mDataSet[0].put("desc", "내 소유의 그림방 리스트를 확인합니다!");
        mDataSet[0].put("func", new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Log.e("TAG", "onClick: 내 그림방 리스트 보기 클릭!!");
          }
        });
        mDataSet[1] = new HashMap<>();
        mDataSet[1].put("viewType", RecyclerViewAdapter.SECOND_TYPE);
        mDataSet[1].put("title", "직접 코드 입력");
        mDataSet[1].put("desc", "다른 사람이 그린 그림방을 불러옵니다!");
        mDataSet[1].put("func", new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Log.e("TAG", "onClick: 방 코드 불러오기 클릭!!");
          }
        });
        break;
      case ROOM_OPTION_MODE:
        break;
      default:
        mDataSet = new HashMap[GlobalState.MAXIMUM_COUNT_FOR_LIST];
        for (int i = 0; i < GlobalState.MAXIMUM_COUNT_FOR_LIST; i++) {
          mDataSet[i] = new HashMap<>();
          mDataSet[i].put("title", "This is element #" + i);
          mDataSet[i].put("desc", "보조 텍스트란:: " + i);
        }
        break;
    }
  }
}
