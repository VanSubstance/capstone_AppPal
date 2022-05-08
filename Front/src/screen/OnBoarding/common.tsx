import {createStackNavigator} from "@react-navigation/stack";

export type OnBoardingStackParamList = {
  Home: undefined;
  Login: {loginParam: string; param2: boolean};
};

export const OnBoardingStack = createStackNavigator<OnBoardingStackParamList>();
