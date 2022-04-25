using UnityEngine;
[System.Serializable]
public class UserDto
{
    public string memberKey;
    public string email;
    public string status;
    public string password;
    public string regDate;
    public string lastLoginDate;

    public string ToString()
    {
        return "UserInfo:: \n"
            + "memberKey:: \n" + memberKey
            + "email:: \n" + email
            + "status:: \n" + status
            + "password:: \n" + password
            + "regDate:: \n" + regDate
            + "lastLoginDate:: \n" + lastLoginDate;
    }
}
