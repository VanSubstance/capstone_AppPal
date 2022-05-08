import { NavigationGroupType } from "../../screen";
import createAction, {handleActions} from "./util/actionTs";

type GlobalState = {
  currentNavGroup: NavigationGroupType;
};

const initialState: GlobalState = {
  currentNavGroup: 'OnBoarding',
};

type GlobalAction = ReturnType<typeof navigationChange>;

const prefix = "global/";

const NAV_CHANGE = `${prefix}NAV_CHANGE`;

export const navigationChange = createAction(NAV_CHANGE, (targetGroup: NavigationGroupType) => ({
  targetGroup,
}));

export default handleActions<GlobalState, GlobalAction>(
  {
    [NAV_CHANGE]: (state: GlobalState, {payload: {targetGroup}}: any) => ({
      ...state,
      currentNavGroup: targetGroup,
    }),
  },
  initialState,
);
