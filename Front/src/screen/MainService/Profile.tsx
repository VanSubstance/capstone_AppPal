import {NativeStackScreenProps} from "@react-navigation/native-stack";
import * as React from "react";
import {memo} from "react";
import {View, Text, Button} from "react-native";
import {Color} from "../../public/Colors";
import {useCommon} from "../../utils/hooks/useCommon";
import {MainServiceStackParamList} from "./common";

type ProfileScreenParams = NativeStackScreenProps<
  MainServiceStackParamList,
  "Profile"
>;

export const ProfileScreen = memo(
  ({navigation, route}: ProfileScreenParams) => {
    console.log("프로필 스크린??");
    const {memberKey} = useCommon();

    return (
      <View style={{flex: 1, alignItems: "center", justifyContent: "center"}}>
        <Text
          style={{
            color: Color.black,
            fontSize: 100,
          }}
        >
          프로필 스크린 :: {memberKey}
        </Text>
        <Button
          title={"메인 플레이 스크린으로"}
          onPress={() => {
            navigation.navigate("Main");
          }}
        />
        <Button title={"뒤로"} onPress={() => {}} />
      </View>
    );
  },
);
