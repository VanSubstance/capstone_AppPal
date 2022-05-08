import {createStackNavigator} from "@react-navigation/stack";

export type OnBoardingStackParamList = {
  Home: undefined;
  Login: undefined;
};

export const OnBoardingStack = createStackNavigator<OnBoardingStackParamList>();
