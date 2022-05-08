import {NavigationContainer} from '@react-navigation/native';
import React from 'react';
import {memo} from 'react';
import {OnBoardingStack} from './common';
import { MainScreen } from './Main';
import { ProfileScreen } from './Profile';

export const MainServiceNavigation = memo(() => {
  return (
    <NavigationContainer>
      <OnBoardingStack.Navigator
        initialRouteName="Main"
        screenOptions={{headerShown: false}}>
        <OnBoardingStack.Screen
          name="Main"
          component={MainScreen}
        />
        <OnBoardingStack.Screen
          name="Profile"
          component={ProfileScreen}
        />
      </OnBoardingStack.Navigator>
    </NavigationContainer>
  );
});
