import { Alert, Platform, ToastAndroid } from "react-native";

export const objectMatchProps = (obj, name) => {
  return Object.entries(obj).find((item) => item[0] === name);
};

export const sendToast = (text: string) => {
  if (Platform.OS === 'ios') {
    Alert.alert(text);
  } else {
    ToastAndroid.show(text, ToastAndroid.SHORT);
  }
};
