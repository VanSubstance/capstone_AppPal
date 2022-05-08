import {useCallback} from "react";
import {useDispatch, useSelector} from "react-redux";
import {RootState} from "../../core";
import {navigationChange} from "../../core/redux/global";
import {loginAction} from "../../core/redux/member";
import {NavigationGroupType, NavigationTypesAllowLogin} from "../../screen";
import {sendToast} from "../common";

export const useCommon = () => {
  const dispatch = useDispatch();
  const {memberKey} = useSelector(({member}: RootState) => member);
  const {currentNavGroup} = useSelector(({global}: RootState) => global);

  const checkLoginState = useCallback(() => {
    if (memberKey) {
      return true;
    }
    return false;
  }, [memberKey]);

  const loginTrial = useCallback(
    memberKey => {
      if (checkLoginState()) {
        console.error("이미 로그인되어있음:: ", memberKey);
      } else {
        dispatch(loginAction(memberKey));
      }
    },
    [memberKey],
  );

  const changeNavigation = useCallback(
    (targetNavigation: NavigationGroupType) => {
      if (NavigationTypesAllowLogin.includes(targetNavigation)) {
        if (!checkLoginState()) {
          sendToast(`회원만 가능한 화면인데요?`);
          return false;
        }
      }
      dispatch(navigationChange(targetNavigation));
    },
    [currentNavGroup, memberKey],
  );

  return {
    loginTrial,
    checkLoginState,
    memberKey,
    changeNavigation,
  };
};
