using System.Collections;
using UnityEngine;
using UnityEngine.Networking;

public class APIManager : MonoBehaviour
{
    string jsonResult;
    bool isOnLoading = true;
    private string apiUrl = "http://apppalstaticserver-env.eba-rixzt96v.us-east-2.elasticbeanstalk.com/";

    void Start()
    {
        StartCoroutine(LoadData());
    }


    IEnumerator LoadData()
    {
        string GetDataUrl = apiUrl + "user";
        using (UnityWebRequest www = UnityWebRequest.Get(GetDataUrl))
        {
            //www.chunkedTransfer = false;
            yield return www.Send();
            if (www.isNetworkError || www.isHttpError)
            {
                Debug.Log(www.error);
            }
            else
            {
                if (www.isDone)
                {
                    isOnLoading = false;
                    jsonResult =
                        System.Text.Encoding.UTF8.GetString(www.downloadHandler.data);
                    var response = JsonUtility.FromJson<DataResponse<UserData>>(jsonResult);
                    Debug.Log("raw json: " + jsonResult);
                    Debug.Log("status: " + response.status);
                    Debug.Log("message: " + response.message);
                    Debug.Log("data: ");
                    foreach (UserDto userInfo in response.data.userList)
                    {
                        Debug.Log("email: " + userInfo.email);
                    }
                }
            }
        }
    }

}