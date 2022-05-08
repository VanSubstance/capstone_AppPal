import {combineReducers, createStore, Store} from "redux";
import member from "./redux/member";
import global from "./redux/global";

export const rootReducer = combineReducers({
  member,
  global,
});

export type RootState = ReturnType<typeof rootReducer>;

export const store: Store = createStore(rootReducer);
