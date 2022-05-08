import {NativeStackScreenProps} from "@react-navigation/native-stack";
import * as React from "react";
import {memo} from "react";
import {View, Text, Button} from "react-native";
import {Color} from "../../public/Colors";
import { MainServiceStackParamList } from "./common";

type MainScreenParams = NativeStackScreenProps<
  MainServiceStackParamList,
  "Main"
>;

export const MainScreen = memo(({navigation}: MainScreenParams) => {
  console.log("메인스크린??");

  return (
    <View style={{flex: 1, alignItems: "center", justifyContent: "center"}}>
      <Text
        style={{
          color: Color.black,
          fontSize: 100,
        }}
      >
        메인 플레이 스크린
      </Text>
      <Button
        title={"프로필 페이지로"}
        onPress={() => {
          navigation.navigate("Profile");
        }}
      />
      <Button
        title={"뒤로"}
        onPress={() => {
        }}
      />
    </View>
  );
});
