import {NativeStackScreenProps} from "@react-navigation/native-stack";
import * as React from "react";
import {memo} from "react";
import {View, Text, Button} from "react-native";
import {Color} from "../../public/Colors";
import {OnBoardingStackParamList} from "./common";

type HomeScreenParams = NativeStackScreenProps<
  OnBoardingStackParamList,
  "Home"
>;

export const HomeScreen = memo(({navigation,}: HomeScreenParams) => {
  console.log('홈??',);
  return (
    <View style={{flex: 1, alignItems: "center", justifyContent: "center"}}>
      <Text
        style={{
          color: Color.black,
          fontSize: 100,
        }}
      >
        Home Screen
      </Text>
      <Button
        title={"로그인 페이지로"}
        onPress={() => {
          navigation.navigate("Login", {
            loginParam: "버튼버튼",
            param2: true,
          });
        }}
      />
      <Button
        title={"뒤로"}
        onPress={() => {
          navigation.navigate("Login", {
            loginParam: "뒤로가기를 누른",
            param2: true,
          });
        }}
      />
    </View>
  );
});
