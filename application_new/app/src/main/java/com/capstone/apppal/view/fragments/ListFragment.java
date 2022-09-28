package com.capstone.apppal.view.fragments;

import android.app.Dialog;
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
import com.capstone.apppal.RoomHandler;
import com.capstone.apppal.VO.RoomsInfo;
import com.capstone.apppal.network.SimpleCallback;
import com.capstone.apppal.utils.CommonFunctions;
import com.capstone.apppal.utils.GlobalState;
import com.capstone.apppal.view.dialog.ChoiceDialog;
import com.capstone.apppal.view.dialog.ConfirmDialog;
import com.capstone.apppal.view.dialog.InputDialog;
import com.capstone.apppal.view.item.RecyclerViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class ListFragment extends Fragment {
  private final static String TAG = "ListFragment";
  public final static int CREATE_OPTION_MODE = 0;
  public final static int ENTER_OPTION_MODE = 1;
  public final static int ROOM_LIST_OPTION_MODE = 2;
  //  private RoomsInfo roomsInfo;
  private int currentOption;
  private RecyclerView mListView;
  private RecyclerViewAdapter mRecyclerViewAdapter;
  private RecyclerView.LayoutManager mLayoutManager;
  private HashMap<String, Object>[] mDataSet;

  private FirebaseAuth mAuth = null;
  private String email;

  private FirebaseDatabase database;
  private RoomHandler roomHandler;

  public ListFragment() {
    super();
    roomHandler = new RoomHandler();
  }

  public ListFragment(int optionMode) {
    super();
    roomHandler = new RoomHandler();
    currentOption = optionMode;
  }

  public ListFragment(int optionMode, HashMap<String, Object>[] dataSet) {
    super();
    roomHandler = new RoomHandler();
    mDataSet = dataSet;
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
        RoomsInfo roomsInfo = new RoomsInfo();
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
                database = FirebaseDatabase.getInstance();
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                email = user.getEmail();
                String uid = user.getUid();
                roomsInfo.setUid(uid);
                InputDialog titledDialog = new InputDialog(getContext(), new InputDialog.DataTransfer() {
                  @Override
                  public InputDialog.Data getData() {
                    InputDialog.Data data = new InputDialog.Data();
                    data.setIsEncrypted(false);
                    data.setTextMain("그림방의 이름을 설정해주세요!\n최대 12자(알파벳 소문자 기준)");
                    data.setMaxLength(12);
                    data.setTextMainButton("확인");
                    data.setTextSubButton("취소");
                    return data;
                  }

                  @Override
                  public void onMainButtonClick(String inputText) {
                    roomsInfo.setTitle(inputText);
                    InputDialog passwordDialog = new InputDialog(getContext(), new InputDialog.DataTransfer() {
                      @Override
                      public InputDialog.Data getData() {
                        InputDialog.Data data = new InputDialog.Data();
                        data.setIsEncrypted(true);
                        data.setTextMain("그림방의 비밀번호을 설정해주세요!\n최대 16자(알파벳 소문자 기준)");
                        data.setMaxLength(16);
                        data.setTextMainButton("확인");
                        data.setTextSubButton("취소");
                        return data;
                      }

                      @Override
                      public void onMainButtonClick(String inputText) {
                        roomsInfo.setPasssword(inputText);
                        roomHandler.singleRoomCreate(roomsInfo, data -> {
                          enterRoom(data);
                        });
                      }

                      @Override
                      public void onSubButtonClick(String inputText) {
                      }

                    });
                    launchDialog(passwordDialog);
                  }

                  @Override
                  public void onSubButtonClick(String inputText) {
                  }

                });
                launchDialog(titledDialog);
              }

              @Override
              public void onSubButtonClick() {
              }

            });
            launchDialog(confirmDialog);
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
            roomHandler.getMyRoomList(data -> {
              Log.e(TAG, "onClick: data:: " + data);
              mDataSet = new HashMap[data.size()];
              int i = 0;
              for (RoomsInfo roomInfo : data) {
                mDataSet[i] = new HashMap<>();
                mDataSet[i].put("viewType", RecyclerViewAdapter.SECOND_TYPE);
                mDataSet[i].put("title", roomInfo.getTitle());
                mDataSet[i].put("desc", roomInfo.getRoomCode());
                mDataSet[i].put("func", new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                    ChoiceDialog choiceDialog = new ChoiceDialog(getContext(), new ChoiceDialog.DataTransfer() {
                      @Override
                      public ChoiceDialog.Data getData() {
                        ChoiceDialog.Data data = new ChoiceDialog.Data();
                        data.setTextMain("방 제목\n[" + roomInfo.getTitle() + "]\n\n방 코드\n[" + roomInfo.getRoomCode() + "]");
                        data.setTextButton1("입장");
                        data.setTextButton2("비밀번호 변경");
                        return data;
                      }

                      @Override
                      public void onButtonClick1() {
                        ConfirmDialog confirmDialog = new ConfirmDialog(getContext(), new ConfirmDialog.DataTransfer() {
                          @Override
                          public ConfirmDialog.Data getData() {
                            ConfirmDialog.Data data = new ConfirmDialog.Data();
                            data.setTextMain("[ " + roomInfo.getTitle() + " ]\n해당 그림방으로 입장하시겠습니까?");
                            data.setTextMainButton("네");
                            data.setTextSubButton("아니오");
                            return data;
                          }

                          @Override
                          public void onMainButtonClick() {
                            enterRoom(roomInfo);
                          }

                          @Override
                          public void onSubButtonClick() {
                          }

                        });
                        launchDialog(confirmDialog);
                      }

                      @Override
                      public void onButtonClick2() {
                        modifyPassword(roomInfo);
                      }

                      @Override
                      public void onButtonClick3() {

                      }
                    });
                    launchDialog(choiceDialog);
                  }
                });
                i++;
              }
              ((OnBoardingActivity) getActivity()).goToListFragment(ListFragment.ROOM_LIST_OPTION_MODE, mDataSet);
            });
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
                data.setMaxLength(6);
                data.setTextMainButton("확인");
                data.setTextSubButton("취소");
                return data;
              }

              @Override
              public void onMainButtonClick(String inputText) {
                roomHandler.getRoomInfo(inputText, roomInfo -> {
                  if (roomInfo.getTitle() != null) {
                    InputDialog passwordDialog = new InputDialog(getContext(), new InputDialog.DataTransfer() {
                      @Override
                      public InputDialog.Data getData() {
                        InputDialog.Data data = new InputDialog.Data();
                        data.setIsEncrypted(true);
                        data.setTextMain("[" + roomInfo.getTitle() + "]\n" + "그림방의 비밀번호를 입력해주세요!");
                        data.setMaxLength(16);
                        data.setTextMainButton("확인");
                        data.setTextSubButton("취소");
                        return data;
                      }

                      @Override
                      public void onMainButtonClick(String inputText) {
                        if (checkPassword(roomInfo, inputText)) {
                          ChoiceDialog choiceDialog = new ChoiceDialog(getContext(), new ChoiceDialog.DataTransfer() {
                            @Override
                            public ChoiceDialog.Data getData() {
                              ChoiceDialog.Data data = new ChoiceDialog.Data();
                              data.setTextButton1("해당 방 입장");
                              data.setTextButton2("복사 후 입장");
                              return data;
                            }

                            @Override
                            public void onButtonClick1() {
                              ConfirmDialog confirmDialog = new ConfirmDialog(getContext(), new ConfirmDialog.DataTransfer() {
                                @Override
                                public ConfirmDialog.Data getData() {
                                  ConfirmDialog.Data data = new ConfirmDialog.Data();
                                  data.setTextMain("[ 그림방 제목:: " + roomInfo.getTitle() + " ]\n해당 그림방으로 입장하시겠습니까?");
                                  data.setTextMainButton("네");
                                  data.setTextSubButton("아니오");
                                  return data;
                                }

                                @Override
                                public void onMainButtonClick() {
                                  enterRoom(roomInfo);
                                }

                                @Override
                                public void onSubButtonClick() {
                                  roomHandler.singleRoomCreate(roomInfo, data -> {
                                    enterRoom(data);
                                  });
                                }

                              });
                              launchDialog(confirmDialog);
                            }

                            @Override
                            public void onButtonClick2() {
                            }

                            @Override
                            public void onButtonClick3() {
                            }
                          });
                          launchDialog(choiceDialog);
                        } else {
                          launchNoticeDialog("비밀번호가 틀렸습니다. \n다시 확인해주세요.");
                        }

                      }

                      @Override
                      public void onSubButtonClick(String inputText) {
                      }

                    });
                    launchDialog(passwordDialog);
                  } else {
                    launchNoticeDialog("해당 코드의 그림방이 존재하지 않습니다. \n다시 확인해주세요.");
                  }
                });
              }

              @Override
              public void onSubButtonClick(String inputText) {
              }

            });
            launchDialog(roomCodeDialog);
          }
        });
        break;
      case ROOM_LIST_OPTION_MODE:
        break;
      default:
        break;
    }
  }

  private void enterRoom(RoomsInfo roomInfo) {
    ((OnBoardingActivity) getActivity()).enterDrawingRoom(roomInfo);
  }

  private boolean checkPassword(RoomsInfo roomInfo, String plainPassword) {
    return roomInfo.getPasssword().equals(CommonFunctions.Encrypted(plainPassword, roomInfo.getRoomCode()));
  }

  private void modifyPassword(RoomsInfo roomInfo) {

  }

  private void launchDialog(Dialog dialog) {
    dialog.setCanceledOnTouchOutside(true);
    dialog.setCancelable(true);
    dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    dialog.show();
  }

  private void launchNoticeDialog(String noticeText) {
    ConfirmDialog confirmDialog = new ConfirmDialog(getContext(), new ConfirmDialog.DataTransfer() {
      @Override
      public ConfirmDialog.Data getData() {
        ConfirmDialog.Data data = new ConfirmDialog.Data();
        data.setTextMain(noticeText);
        return data;
      }

      @Override
      public void onMainButtonClick() {
      }

      @Override
      public void onSubButtonClick() {
      }

    });
    launchDialog(confirmDialog);
  }
}
