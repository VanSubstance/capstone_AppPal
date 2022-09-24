package com.capstone.apppal.view.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.apppal.OnBoardingActivity;
import com.capstone.apppal.R;
import com.capstone.apppal.utils.GlobalState;
import com.capstone.apppal.view.dialog.ConfirmDialog;
import com.capstone.apppal.view.dialog.InputDialog;
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
            ConfirmDialog confirmDialog = new ConfirmDialog(getContext(), new ConfirmDialog.DataTransfer() {
              @Override
              public ConfirmDialog.Data getData() {
                ConfirmDialog.Data data = new ConfirmDialog.Data();
                data.setTextMain("새로운 그림방으로 입장하시겠습니까?");
                data.setTextMainButton("네");
                data.setTextSubButton("아니오");
                return data;
              }

              @Override
              public void onMainButtonClick() {
                ((OnBoardingActivity) getActivity()).enterDrawingRoom();
              }

              @Override
              public void onSubButtonClick() {

              }

            });
            confirmDialog.setCanceledOnTouchOutside(true);
            confirmDialog.setCancelable(true);
            confirmDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            confirmDialog.show();
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
            InputDialog roomCodeDialog = new InputDialog(getContext(), new InputDialog.DataTransfer() {
              @Override
              public InputDialog.Data getData() {
                InputDialog.Data data = new InputDialog.Data();
                data.setTextMain("입장하고자 하시는 \n그림방 코드를 입력해주세요!");
                data.setTextEdit("초기설정값");
                data.setMaxLength(6);
                data.setTextMainButton("확인");
                data.setTextSubButton("취소");
                return data;
              }

              @Override
              public void onMainButtonClick(String inputText) {
                Log.e("TAG", "onMainButtonClick: 확인:: 방 코드:: " + inputText);
                InputDialog passwordDialog = new InputDialog(getContext(), new InputDialog.DataTransfer() {
                  @Override
                  public InputDialog.Data getData() {
                    InputDialog.Data data = new InputDialog.Data();
                    data.setIsEncrypted(true);
                    data.setTextMain("그림방의 비밀번호를 입력해주세요!");
                    data.setMaxLength(16);
                    data.setTextMainButton("확인");
                    data.setTextSubButton("취소");
                    return data;
                  }

                  @Override
                  public void onMainButtonClick(String inputText) {
                    Log.e("TAG", "onMainButtonClick: 확인:: 비밀번호:: " + inputText);
                  }

                  @Override
                  public void onSubButtonClick(String inputText) {
                    Log.e("TAG", "onMainButtonClick: 취소");
                  }

                });
                passwordDialog.setCanceledOnTouchOutside(true);
                passwordDialog.setCancelable(true);
                passwordDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                passwordDialog.show();
              }

              @Override
              public void onSubButtonClick(String inputText) {
                Log.e("TAG", "onMainButtonClick: 취소");
              }

            });
            roomCodeDialog.setCanceledOnTouchOutside(true);
            roomCodeDialog.setCancelable(true);
            roomCodeDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            roomCodeDialog.show();
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
