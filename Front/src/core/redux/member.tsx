import createAction, {handleActions} from "./util/actionTs";

type MemberState = {
  memberKey: string;
};

const initialState: MemberState = {
  memberKey: null,
};

type MemberAction = ReturnType<typeof loginAction>;

const prefix = "member/";

const MEMBER_LOGIN = `${prefix}MEMBER_LOGIN`;

export const loginAction = createAction(MEMBER_LOGIN, (memberKey: string) => ({
  memberKey,
}));

export default handleActions<MemberState, MemberAction>(
  {
    [MEMBER_LOGIN]: (state: MemberState, {payload: {memberKey}}: any) => ({
      ...state,
      memberKey: memberKey,
    }),
  },
  initialState,
);
