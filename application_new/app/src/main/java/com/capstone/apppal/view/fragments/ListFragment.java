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
  public final static int CREATE_OPTION_MODE = 0;
  public final static int ENTER_OPTION_MODE = 1;
  public final static int ROOM_LIST_OPTION_MODE = 2;
  //  private RoomsInfo roomsInfo;
  private int currentOption;
  private RecyclerView mListView;
  private RecyclerViewAdapter mRecyclerViewAdapter;
  private RecyclerView.LayoutManager mLayoutManager;
  private HashMap<String, Object>[] mDataSet;


  private FirebaseDatabase database;
  private FirebaseAuth mAuth = null;
  private String email;

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
                        roomsInfo.setPasssword(Encrypted(inputText, email));
                        RoomHandler roomhandler = new RoomHandler();
                        roomhandler.singleRoomCreate(roomsInfo);
                        ((OnBoardingActivity) getActivity()).enterDrawingRoom();
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
            ((OnBoardingActivity) getActivity()).goToListFragment(ListFragment.ROOM_LIST_OPTION_MODE);
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
                RoomsInfo roomInfo = getRoomInfo(inputText);
                if (roomInfo != null) {
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
                      if (checkPassword("방 코드", inputText)) {
                        ChoiceDialog choiceDialog = new ChoiceDialog(getContext(), new ChoiceDialog.DataTransfer() {
                          @Override
                          public ChoiceDialog.Data getData() {
                            ChoiceDialog.Data data = new ChoiceDialog.Data();
                            data.setTextButton1("해당 방 입장");
                            data.setTextButton2("클론 생성");
                            return data;
                          }

                          @Override
                          public void onButtonClick1() {
                            ConfirmDialog confirmDialog = new ConfirmDialog(getContext(), new ConfirmDialog.DataTransfer() {
                              @Override
                              public ConfirmDialog.Data getData() {
                                ConfirmDialog.Data data = new ConfirmDialog.Data();
                                data.setTextMain("[ 그림방 제목:: " + "????" + " ]\n해당 그림방으로 입장하시겠습니까?");
                                data.setTextMainButton("네");
                                data.setTextSubButton("아니오");
                                return data;
                              }

                              @Override
                              public void onMainButtonClick() {
                                enterRoom("방 코드");
                              }

                              @Override
                              public void onSubButtonClick() {
                                createClone("방 코드");
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
        /** @김종규
         * 나의 방 목록 리스트 페이지 초기화
         * 방 목록 데이터 넘겨주기
         * 더미:: 10개
         */
        int dummyLen = 10;
        mDataSet = new HashMap[dummyLen];
        for (int i = 0; i < dummyLen; i++) {
          mDataSet[i] = new HashMap<>();
          mDataSet[i].put("viewType", RecyclerViewAdapter.SECOND_TYPE);
          mDataSet[i].put("title", "그림방 제목:: " + i);
          mDataSet[i].put("desc", null);
          int finalI = i;
          mDataSet[i].put("func", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              ChoiceDialog choiceDialog = new ChoiceDialog(getContext(), new ChoiceDialog.DataTransfer() {
                @Override
                public ChoiceDialog.Data getData() {
                  ChoiceDialog.Data data = new ChoiceDialog.Data();
                  data.setTextMain("해당 방 제목\n[" + "@김종규" + "]\n\n방 코드\n[" + "@김종규" + "]");
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
                      data.setTextMain("[ 그림방 제목:: " + finalI + " ]\n해당 그림방으로 입장하시겠습니까?");
                      data.setTextMainButton("네");
                      data.setTextSubButton("아니오");
                      return data;
                    }

                    @Override
                    public void onMainButtonClick() {
                      enterRoom("방 코드");
                    }

                    @Override
                    public void onSubButtonClick() {
                    }

                  });
                  launchDialog(confirmDialog);
                }

                @Override
                public void onButtonClick2() {
                  modifyPassword("방 코드");
                }

                @Override
                public void onButtonClick3() {

                }
              });
              launchDialog(choiceDialog);
            }
          });
        }
        break;
      default:
        break;
    }
  }

  /**
   * @김종규 해당 방으로 입장
   */
  private void enterRoom(String roomCode) {

  }

  /**
   * @김종규 방 정보 받아오기
   */
  private RoomsInfo getRoomInfo(String roomCode) {
    return null;
  }

  /**
   * @김종규 비밀번호 확인
   */
  private boolean checkPassword(String roomCode, String plainPassword) {
    return true;
  }

  /**
   * @김종규 비밀번호 수정
   */
  private void modifyPassword(String roomCode) {

  }

  /**
   * @김종규 클론 생성
   */
  private void createClone(String roomCode) {

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

  public String Encrypted(String password, String email) {
    String result = "";
    String input = password + email;
    try {
			/* MessageDigest 클래스의 getInstance() 메소드의 매개변수에 "SHA-256" 알고리즘 이름을 지정함으로써
				해당 알고리즘에서 해시값을 계산하는 MessageDigest를 구할 수 있다 */
      MessageDigest mdSHA256 = MessageDigest.getInstance("SHA-256");

      // 데이터(패스워드+ email)를 한다. 즉 '암호화'와 유사한 개념
      mdSHA256.update(input.getBytes("UTF-8"));

      // 바이트 배열로 해쉬를 반환한다.
      byte[] sha256Hash = mdSHA256.digest();

      // StringBuffer 객체는 계속해서 append를 해도 객체는 오직 하나만 생성된다. => 메모리 낭비 개선
      StringBuffer hexSHA256hash = new StringBuffer();

      // 256비트로 생성 => 32Byte => 1Byte(8bit) => 16진수 2자리로 변환 => 16진수 한 자리는 4bit
      for (byte b : sha256Hash) {
        String hexString = String.format("%02x", b);
        hexSHA256hash.append(hexString);
      }
      result = hexSHA256hash.toString();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return result;
  }
}
