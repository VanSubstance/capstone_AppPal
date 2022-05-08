/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React, { memo } from "react";
import {Provider as StoreProvider, useSelector} from "react-redux";

import {SafeAreaView, useColorScheme} from "react-native";

import {Color} from "./src/public/Colors";
import {OnBoardingNavigation} from "./src/screen/OnBoarding/index";
import {store} from "./src/core";
import {MainServiceNavigation} from "./src/screen/MainService";

import {RootState} from "./src/core";

const App = () => {
  const isDarkMode = useColorScheme() === "dark";

  const backgroundStyle = {
    backgroundColor: isDarkMode ? Color.black : Color.white,
  };

  return (
    <StoreProvider store={store}>
      <NavigationControllContent />
    </StoreProvider>
  );
};

const NavigationControllContent = memo(() => {
  const {currentNavGroup} = useSelector(({global}: RootState) => global);
  return currentNavGroup === "OnBoarding" ? (
    <OnBoardingNavigation />
  ) : currentNavGroup === "MainService" ? (
    <MainServiceNavigation />
  ) : null;
});

export default App;
