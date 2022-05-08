import { Action } from "redux";

function createAction<T, P extends (...args: any) => any>(type: T, payloadCreator: P): (...args: Parameters<P>) => Action<T> & { payload: ReturnType<P> };
function createAction<T>(type: T): () => Action<T>;
function createAction(type: any, payloadCreator?: any) {
  return (...args: any[]) => ({
    type,
    ...(payloadCreator && { payload: payloadCreator(...args) }),
  });
}

export default createAction;

type ReducerMap<A extends Action<string>, S> = {
  [AT in A["type"]]?: (state: S, action: MatchedAction<A, AT>) => S;
} & { [key: string]: (state: S, action: Action) => S };

type MatchedAction<A, T> = A extends Action<T> ? A : never;

export const handleActions = <S, A extends Action<string>>(reducerMap: ReducerMap<A, S>, defaultState: S) => (state = defaultState, action?: A): S =>
  action && reducerMap[action.type] ? reducerMap[action.type](state, action) : state;
