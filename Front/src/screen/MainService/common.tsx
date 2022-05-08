import {createStackNavigator} from "@react-navigation/stack";

export type MainServiceStackParamList = {
  Main: undefined;
  Profile: undefined;
};

export const OnBoardingStack = createStackNavigator<MainServiceStackParamList>();
