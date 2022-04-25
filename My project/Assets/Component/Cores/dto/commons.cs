using System.Collections.Generic;

[System.Serializable]
public class DataResponse<T>
{
    public int status;
    public string message;
    public T data;

}

[System.Serializable]
public class UserData
{
    public List<UserDto> userList;
}