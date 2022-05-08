import {NativeStackScreenProps} from "@react-navigation/native-stack";
import * as React from "react";
import {memo} from "react";
import {View, Text, Button} from "react-native";
import {Color} from "../../public/Colors";
import {sendToast} from "../../utils/common";
import {useCommon} from "../../utils/hooks/useCommon";
import {OnBoardingStackParamList} from "./common";

type LoginScreenParams = NativeStackScreenProps<
  OnBoardingStackParamList,
  "Login"
>;

export const LoginScreen = memo(({navigation}: LoginScreenParams) => {
  const {loginTrial, checkLoginState, changeNavigation} = useCommon();

  return (
    <View style={{flex: 1, alignItems: "center", justifyContent: "center"}}>
      <Text
        style={{
          color: Color.black,
          fontSize: 100,
        }}
      >
        Login Screen
      </Text>
      <Button
        title={"로그인"}
        onPress={() => {
          if (checkLoginState()) {
            sendToast(`이미 로그인이 되어있는데요?`);
          } else {
            loginTrial(`멤버키-입니다`);
          }
        }}
      />
      <Button
        title={"메인 페이지로 이동"}
        onPress={() => {
          if (checkLoginState()) {
            changeNavigation("MainService");
          } else {
            sendToast(`로그인이 안되어있는데요?`);
          }
        }}
      />
      <Button
        title={"뒤로"}
        onPress={() => {
          navigation.navigate("Home");
        }}
      />
    </View>
  );
});
