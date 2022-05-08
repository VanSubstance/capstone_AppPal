import {NavigationContainer} from '@react-navigation/native';
import React from 'react';
import {memo} from 'react';
import {OnBoardingStack} from './common';
import {HomeScreen} from './Home';
import { LoginScreen } from './Login';

export const OnBoardingNavigation = memo(() => {
  return (
    <NavigationContainer>
      <OnBoardingStack.Navigator
        initialRouteName="Home"
        screenOptions={{headerShown: false}}>
        <OnBoardingStack.Screen
          name="Home"
          component={HomeScreen}
        />
        <OnBoardingStack.Screen
          name="Login"
          component={LoginScreen}
        />
      </OnBoardingStack.Navigator>
    </NavigationContainer>
  );
});
