/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React from 'react';

import {SafeAreaView, useColorScheme} from 'react-native';

import {Color} from './src/public/Colors';
import {OnBoardingNavigation} from './src/screen/OnBoarding/index';

const App = () => {
  const isDarkMode = useColorScheme() === 'dark';

  const backgroundStyle = {
    backgroundColor: isDarkMode ? Color.black : Color.white,
  };

  return (
    <OnBoardingNavigation />
  );
};

export default App;
