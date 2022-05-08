import {NativeStackScreenProps} from "@react-navigation/native-stack";
import * as React from "react";
import {memo} from "react";
import {View, Text, Button} from "react-native";
import {Color} from "../../public/Colors";
import {OnBoardingStackParamList} from "./common";

type LoginScreenParams = NativeStackScreenProps<
  OnBoardingStackParamList,
  "Login"
>;

export const LoginScreen = memo(({navigation, route}: LoginScreenParams) => {
  const {loginParam, param2} = route.params;
  console.log("로그인??", loginParam);
  console.log(param2);

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
      <Text>전달 파라미터:: {loginParam}</Text>
      <Text>불린 파라미터:: {param2}</Text>
      <Button
        title={"홈 페이지로"}
        onPress={() => {
          navigation.navigate("Home");
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
